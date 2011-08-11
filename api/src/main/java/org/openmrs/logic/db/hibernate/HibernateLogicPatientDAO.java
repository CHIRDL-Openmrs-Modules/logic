/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.logic.db.hibernate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Patient;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.LogicExpression;
import org.openmrs.logic.LogicTransform;
import org.openmrs.logic.db.LogicPatientDAO;
import org.openmrs.logic.op.Operator;
import org.openmrs.logic.util.LogicExpressionToCriterion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * 
 */
@Repository
public class HibernateLogicPatientDAO extends LogicExpressionToCriterion implements LogicPatientDAO {
	
	static {
		map.put("IDENTIFIER", "identifier.identifier");
	}
	
	protected final Log log = LogFactory.getLog(getClass());
	
	/**
	 * Hibernate session factory
	 */
	@Autowired
	private SessionFactory sessionFactory;
	
	/**
	 * Set session factory
	 * 
	 * @param sessionFactory
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	// Helper function, converts logic service's criteria into Hibernate's
	// criteria
	@SuppressWarnings("unchecked")
	private List<Patient> logicToHibernate(LogicExpression expression, Collection<Integer> patientIds) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Patient.class);
		criteria.createAlias("identifiers", "identifier");
		
		Date indexDate = Calendar.getInstance().getTime();
		Operator transformOperator = null;
		LogicTransform transform = expression.getTransform();
		Integer numResults = null;
		
		if (transform != null) {
			transformOperator = transform.getTransformOperator();
			numResults = transform.getNumResults();
		}
		
		if (numResults == null) {
			numResults = 1;
		}
		// set the transform and evaluate the right criteria
		// if there is any
		if (transformOperator == Operator.DISTINCT) {
			criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		}
		Criterion c = this.getCriterion(expression, indexDate, criteria);
		if (c != null) {
			criteria.add(c);
		}
		List<Patient> results = new ArrayList<Patient>();
		
		criteria.add(Restrictions.in("patientId", patientIds));
		results.addAll(criteria.list());
		
		//return a single result per patient for these operators
		//I don't see an easy way to do this in hibernate so I am
		//doing some post processing
		if (transformOperator == Operator.FIRST || transformOperator == Operator.LAST) {
			HashMap<Integer, ArrayList<Patient>> nResultMap = new HashMap<Integer, ArrayList<Patient>>();
			
			for (Patient currResult : results) {
				Integer currPatientId = currResult.getPatientId();
				ArrayList<Patient> prevResults = nResultMap.get(currPatientId);
				if (prevResults == null) {
					prevResults = new ArrayList<Patient>();
					nResultMap.put(currPatientId, prevResults);
				}
				
				if (prevResults.size() < numResults) {
					prevResults.add(currResult);
				}
			}
			
			if (nResultMap.values().size() > 0) {
				results.clear();
				
				for (ArrayList<Patient> currPatientPatient : nResultMap.values()) {
					results.addAll(currPatientPatient);
				}
			}
		}
		return results;
	}
	
	/**
	 * @throws LogicException
	 * @see org.openmrs.api.db.PatientDAO#getPeople(String, Boolean)
	 */
	public List<Patient> getPatients(Collection<Integer> patientIds, LogicCriteria logicCriteria) throws LogicException {
		return logicToHibernate(logicCriteria.getExpression(), patientIds);
	}
	
}
