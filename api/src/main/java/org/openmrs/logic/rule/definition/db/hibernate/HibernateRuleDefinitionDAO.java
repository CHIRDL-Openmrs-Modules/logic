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
package org.openmrs.logic.rule.definition.db.hibernate;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.openmrs.api.db.DAOException;
import org.openmrs.logic.rule.definition.RuleDefinition;
import org.openmrs.logic.rule.definition.db.RuleDefinitionDAO;
import org.springframework.transaction.annotation.Transactional;


/**
 * Hibernate-based implementation of {@link RuleDefinitionDAO}
 */
public class HibernateRuleDefinitionDAO implements RuleDefinitionDAO {

	private SessionFactory sessionFactory;
	
    /**
     * @param sessionFactory the sessionFactory to set
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
    	this.sessionFactory = sessionFactory;
    }

	/**
	 * @see RuleDefinitionDAO#getRuleDefinition(Integer)
	 */
	@Transactional(readOnly=true)
	public RuleDefinition getRuleDefinition(Integer id) throws DAOException {
		return (RuleDefinition) sessionFactory.getCurrentSession().get(RuleDefinition.class, id);
	}

	/**
	 * @see RuleDefinitionDAO#getRuleDefinition(String)
	 */
	@Transactional(readOnly=true)
	public RuleDefinition getRuleDefinition(String name) throws DAOException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(RuleDefinition.class);
		criteria.add(Expression.eq("name", name));
		return (RuleDefinition) criteria.uniqueResult();
	}

	/**
	 * @see RuleDefinitionDAO#getAllRuleDefinitions(boolean)
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true)
	public List<RuleDefinition> getAllRuleDefinitions(boolean includeRetired) throws DAOException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(RuleDefinition.class);
		if (!includeRetired) {
			criteria.add(Expression.eq("retired", false));
		}
		criteria.addOrder(Order.asc("name"));
		return criteria.list();
	}
	
	/**
	 * @see RuleDefinitionDAO#saveRuleDefinition(RuleDefinition)
	 */
	@Transactional
	public RuleDefinition saveRuleDefinition(RuleDefinition ruleDefinition) throws DAOException {
		sessionFactory.getCurrentSession().saveOrUpdate(ruleDefinition);
		return ruleDefinition;
	}

	/**
	 * @see RuleDefinitionDAO#deleteRuleDefinition(RuleDefinition)
	 */
	@Transactional
	public void deleteRuleDefinition(RuleDefinition ruleDefinition) throws DAOException {
		sessionFactory.getCurrentSession().delete(ruleDefinition);	
	}
	
}
