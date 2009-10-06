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
package org.openmrs.logic.datasource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.db.LogicObsDAO;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.util.LogicUtil;
import org.openmrs.logic.datasource.LogicDataSource;

/**
 * Provides access to clinical observations. The keys for this data source are the primary names of
 * all tests within the concept dictionary. Results have a result date equal to the observation
 * datetime and a value based on the observed value.
 */
public class ObsDataSource implements LogicDataSource {
	
	private static final Collection<String> keys = new ArrayList<String>();
	
	private LogicObsDAO logicObsDAO;
	
	public void setLogicObsDAO(LogicObsDAO logicObsDAO) {
		this.logicObsDAO = logicObsDAO;
	}
	
	public LogicObsDAO getLogicObsDAO() {
		return logicObsDAO;
	}
	
	/**
	 * @throws LogicException
     * 
     * @should get all obs
	 * @should get first obs
	 * @should get last obs
	 * @should get obs gt value
	 * @should get obs ge value
	 * @should get obs lt value
	 * @should get obs le value
	 * @should get obs eq value
	 * @should get last obs if it is lt value
	 * @should get last obs of those lt value
 	 * @should get obs before date
	 * @should get obs after date
	 * @should get obs gt value after date
	 * @should get last obs if it is before date
	 * @should get last obs of those before date
	 * @should get first n obs
	 * @should get last n obs
	 * @should get not of a clause
	 * @should get and of two clauses
	 * @should get or of two clauses
	 * @should get average of obs
	 * @should get average of null when no obs
	 * @should get count of obs when obs
	 * @should get count of zero when no obs
	 * @see org.openmrs.logic.datasource.LogicDataSource#read(org.openmrs.logic.LogicContext,
	 *      org.openmrs.Cohort, org.openmrs.logic.LogicCriteria)
	 */
	public Map<Integer, Result> read(LogicContext context, Cohort patients, LogicCriteria criteria) throws LogicException {
		
		Map<Integer, Result> finalResult = new HashMap<Integer, Result>();
		// TODO: make the obs service method more efficient (so we don't have to re-organize
		// into groupings by patient...or it can be done most expeditiously
		List<Obs> obs = getLogicObsDAO().getObservations(patients, criteria);
		
		// group the received observations by patient and convert them to
		// Results
		for (Obs ob : obs) {
			int personId = ob.getPerson().getPersonId();
			Result result = finalResult.get(personId);
			if (result == null) {
				result = new Result();
				finalResult.put(personId, result);
			}
			
			result.add(new Result(ob));
		}
		
		LogicUtil.applyAggregators(finalResult, criteria, patients);
		
		return finalResult;
	}
	
	/**
	 * @see org.openmrs.logic.datasource.LogicDataSource#getDefaultTTL()
	 */
	public int getDefaultTTL() {
		return 60 * 30; // 30 minutes
	}
	
	/**
	 * @see org.openmrs.logic.datasource.LogicDataSource#getKeys()
	 */
	public Collection<String> getKeys() {
		return keys;
	}
	
	/**
	 * @see org.openmrs.logic.datasource.LogicDataSource#hasKey(java.lang.String)
	 */
	public boolean hasKey(String key) {
		Concept concept = Context.getConceptService().getConcept(key);
		if (concept == null)
			return false;
		else
			return true;
	}
	
	public void addKey(String key) {
		getKeys().add(key);
	}
	
}
