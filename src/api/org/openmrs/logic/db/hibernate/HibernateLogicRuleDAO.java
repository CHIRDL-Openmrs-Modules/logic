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

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.openmrs.api.db.DAOException;
import org.openmrs.logic.LogicRule;
import org.openmrs.logic.db.LogicRuleDAO;


/**
 * Hibernate-based implementation of {@link LogicRuleDAO}
 */
public class HibernateLogicRuleDAO implements LogicRuleDAO {

	private SessionFactory sessionFactory;
	
    /**
     * @param sessionFactory the sessionFactory to set
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
    	this.sessionFactory = sessionFactory;
    }

	/**
	 * @see LogicRuleDAO#getLogicRule(Integer)
	 */
	@Override
	public LogicRule getLogicRule(Integer id) throws DAOException {
		return (LogicRule) sessionFactory.getCurrentSession().get(LogicRule.class, id);
	}

	/**
	 * @see LogicRuleDAO#getLogicRule(String)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public LogicRule getLogicRule(String name) throws DAOException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(LogicRule.class);
		criteria.add(Expression.eq("name", name));
		List<LogicRule> found = criteria.list();
		if (found == null || found.isEmpty()) {
			return null;
		}
		return found.get(0);
	}

	/**
	 * @see LogicRuleDAO#getAllLogicRules(boolean)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<LogicRule> getAllLogicRules(boolean includeRetired) throws DAOException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(LogicRule.class);
		if (!includeRetired) {
			criteria.add(Expression.like("retired", false));
		}
		criteria.addOrder(Order.asc("name"));
		return criteria.list();
	}
	
	/**
	 * @see LogicRuleDAO#saveLogicRule(LogicRule)
	 */
	@Override
	public LogicRule saveLogicRule(LogicRule logicRule) throws DAOException {
		sessionFactory.getCurrentSession().saveOrUpdate(logicRule);
		return logicRule;
	}

	/**
	 * @see LogicRuleDAO#purgeLogicRule(LogicRule)
	 */
	@Override
	public void purgeLogicRule(LogicRule logicRule) throws DAOException {
		sessionFactory.getCurrentSession().delete(logicRule);	
	}
	
}
