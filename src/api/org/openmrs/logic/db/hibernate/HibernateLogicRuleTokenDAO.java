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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.DAOException;
import org.openmrs.logic.LogicRule;
import org.openmrs.logic.LogicRuleToken;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.db.LogicRuleTokenDAO;

/**
 * Implementation of methods defined in the {@link RuleTokenDAO}. The function is not meant to be
 * used directly. Use methods available in the LogicService instead.
 * 
 * @see {@link LogicService}
 * @see {@link Context}
 */
public class HibernateLogicRuleTokenDAO implements LogicRuleTokenDAO {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	/**
	 * Hibernate session factory
	 */
	private SessionFactory sessionFactory;
	
	/**
	 * Set session factory
	 * 
	 * @param sessionFactory
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	/**
	 * @see LogicRuleTokenDAO#getLogicRule(Integer)
	 */
	public LogicRule getLogicRule(Integer id) throws DAOException {
		return (LogicRule) sessionFactory.getCurrentSession().get(LogicRule.class, id);
	}

	/**
	 * @see LogicRuleTokenDAO#getLogicRule(String)
	 */
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
	 * @see LogicRuleTokenDAO#getAllLogicRules(boolean)
	 */
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
	 * @see LogicRuleTokenDAO#saveLogicRule(LogicRule)
	 */
	public LogicRule saveLogicRule(LogicRule logicRule) throws DAOException {
		sessionFactory.getCurrentSession().saveOrUpdate(logicRule);
		return logicRule;
	}

	/**
	 * @see LogicRuleTokenDAO#purgeLogicRule(LogicRule)
	 */
	public void purgeLogicRule(LogicRule logicRule) throws DAOException {
		sessionFactory.getCurrentSession().delete(logicRule);	
	}

	/**
	 * @see LogicRuleTokenDAO#deleteLogicRuleToken(LogicRuleToken)
	 */
	public void deleteLogicRuleToken(LogicRuleToken logicToken) throws DAOException {
		sessionFactory.getCurrentSession().delete(logicToken);
	}
	
	/**
	 * @see LogicRuleTokenDAO#saveLogicRuleToken(LogicRuleToken)
	 */
	public LogicRuleToken saveLogicRuleToken(LogicRuleToken logicToken) throws DAOException {
		sessionFactory.getCurrentSession().saveOrUpdate(logicToken);
		return logicToken;
	}
	
	/**
	 * @see LogicRuleTokenDAO#getLogicRuleToken(String)
	 */
	public LogicRuleToken getLogicRuleToken(String token) {
		return (LogicRuleToken) sessionFactory.getCurrentSession().createQuery(
		    "from LogicRuleToken logicRuleToken where logicRuleToken.token = :token").setString("token", token)
		        .uniqueResult();
	}
	
	/**
	 * @see LogicRuleTokenDAO#getAllTags()
	 */
	public List<String> getAllTags() {
		List<String> allTags = new ArrayList<String>();
		Query query = sessionFactory.getCurrentSession().createQuery(
		    "select logicRuleToken from LogicRuleToken logicRuleToken where exists elements(logicRuleToken.ruleTokenTags) ");
		Iterator<?> logicTokens = query.iterate();
		while (logicTokens.hasNext()) {
			LogicRuleToken logicToken = (LogicRuleToken) logicTokens.next();
			allTags.addAll(logicToken.getRuleTokenTags());
		}
		return allTags;
	}
	
	/**
	 * @see LogicRuleTokenDAO#getAllTokens()
	 */
	@SuppressWarnings("unchecked")
	public List<String> getAllTokens() {
		String strQuery = "select logicRuleToken.token from LogicRuleToken logicRuleToken";
		Query query = sessionFactory.getCurrentSession().createQuery(strQuery);
		return query.list();
	}
	
	/**
	 * @see LogicRuleTokenDAO#getTags(String)
	 */
	public List<String> getTags(String partialTag) {
		List<String> allTags = new ArrayList<String>();
		Query query = sessionFactory.getCurrentSession().createQuery(
		    "select logicRuleToken from LogicRuleToken logicRuleToken where exists elements(logicRuleToken.ruleTokenTags) ");
		Iterator<?> logicTokens = query.iterate();
		while (logicTokens.hasNext()) {
			LogicRuleToken logicToken = (LogicRuleToken) logicTokens.next();
			for (String tag : logicToken.getRuleTokenTags()) {
				if (tag.matches("^.*" + partialTag + ".*$")) {
					allTags.add(tag);
				}
			}
		}
		return allTags;
	}
	
	/**
	 * @see LogicRuleTokenDAO#getTagsByTokens(Set)
	 */
	public List<String> getTagsByTokens(Set<String> tokens) {
		List<String> allTags = new ArrayList<String>();
		String strQuery = "select logicRuleToken from LogicRuleToken logicRuleToken where logicRuleToken.token in :tokens";
		Query query = sessionFactory.getCurrentSession().createQuery(strQuery);
		query.setParameterList("tokens", tokens, Hibernate.STRING);
		Iterator<?> logicTokens = query.iterate();
		while (logicTokens.hasNext()) {
			LogicRuleToken logicToken = (LogicRuleToken) logicTokens.next();
			allTags.addAll(logicToken.getRuleTokenTags());
		}
		return allTags;
	}
	
	/**
	 * @see LogicRuleTokenDAO#getTokens(String)
	 */
	@SuppressWarnings("unchecked")
	public List<String> getTokens(String partialToken) {
		String strQuery = "select logicRuleToken.token from LogicRuleToken logicRuleToken where logicRuleToken.token like :partialToken";
		Query query = sessionFactory.getCurrentSession().createQuery(strQuery);
		query.setString("partialToken", "%" + partialToken + "%");
		return query.list();
	}
	
	/**
	 * @see LogicRuleTokenDAO#getTokensByTag(String)
	 */
	public List<String> getTokensByTag(String tag) {
		Set<String> tags = new HashSet<String>();
		tags.add(tag);
		
		return getTokensByTags(tags);
	}
	
	/**
	 * @see LogicRuleTokenDAO#getTokensByTags(Set)
	 */
	@SuppressWarnings("unchecked")
	public List<String> getTokensByTags(Set<String> tags) {
		String strQuery = "select logicRuleToken.token from LogicRuleToken logicRuleToken where :tags in elements(logicRuleToken.ruleTokenTags)";
		Query query = sessionFactory.getCurrentSession().createQuery(strQuery);
		query.setParameterList("tags", tags, Hibernate.STRING);
		return query.list();
	}
}
