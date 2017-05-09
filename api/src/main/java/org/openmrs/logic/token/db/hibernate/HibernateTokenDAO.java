package org.openmrs.logic.token.db.hibernate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openmrs.logic.rule.provider.RuleProvider;
import org.openmrs.logic.token.TokenRegistration;
import org.openmrs.logic.token.db.TokenDAO;
import org.springframework.transaction.annotation.Transactional;

/**
 * Hibernate-based implementation of {@link TokenDAO}
 */
public class HibernateTokenDAO implements TokenDAO {

	private SessionFactory sessionFactory;
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	/**
     * @see org.openmrs.logic.token.db.TokenDAO#deleteTokenRegistration(org.openmrs.logic.token.TokenRegistration)
     */
    @Transactional
    public void deleteTokenRegistration(TokenRegistration tokenRegistration) {
	    sessionFactory.getCurrentSession().delete(tokenRegistration);
    }
    
	/**
     * @see org.openmrs.logic.token.db.TokenDAO#getAllTokens()
     */
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true)
    public List<String> getAllTokens() {
	    Query query = sessionFactory.getCurrentSession().createQuery("select token from TokenRegistration");
	    return query.list();
    }

	/**
     * @see org.openmrs.logic.token.db.TokenDAO#getTokens(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true)
    public List<String> getTokens(String query) {
		Query q = sessionFactory.getCurrentSession().createQuery("select token from TokenRegistration where lower(token) like :query");
		if (query == null)
			q.setString("query", "%");
		else
			q.setString("query", "%" + query.toLowerCase() + "%");
		return q.list();
    }

	/**
     * @see org.openmrs.logic.token.db.TokenDAO#getCountOfTokenRegistrations(java.lang.String)
     */
    @Transactional(readOnly=true)
    public int getCountOfTokenRegistrations(String query) {
	    Criteria crit = makeCriteria(query);
	    crit.setProjection(Projections.rowCount());
		return (Integer) crit.uniqueResult();
    }

	/**
     * @see org.openmrs.logic.token.db.TokenDAO#getTokenRegistration(java.lang.Integer)
     */
    @Transactional(readOnly=true)
    public TokenRegistration getTokenRegistration(Integer id) {
    	return (TokenRegistration) sessionFactory.getCurrentSession().get(TokenRegistration.class, id);
    }

    /**
     * @see org.openmrs.logic.token.db.TokenDAO#getTokenRegistrationByUuid(java.lang.String)
     */
    @Transactional(readOnly=true)
    public TokenRegistration getTokenRegistrationByUuid(String uuid) {
    	Criteria crit = makeCriteria();
    	crit.add(Restrictions.eq("uuid", uuid));
    	return (TokenRegistration) crit.uniqueResult();
    }

	/**
     * @see org.openmrs.logic.token.db.TokenDAO#getTokenRegistrations(java.lang.String, java.lang.Integer, java.lang.Integer)
     */
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true)
    public List<TokenRegistration> getTokenRegistrations(String query, Integer start, Integer length) {
    	Criteria criteria = makeCriteria(query);
	    if (start != null)
			criteria.setFirstResult(start);
		if (length != null && length > 0)
			criteria.setMaxResults(length);
		return criteria.list();
    }

	/**
     * @see org.openmrs.logic.token.db.TokenDAO#saveTokenRegistration(org.openmrs.logic.token.TokenRegistration)
     */
    @Transactional
    public TokenRegistration saveTokenRegistration(TokenRegistration tokenRegistration) {
	    sessionFactory.getCurrentSession().saveOrUpdate(tokenRegistration);
	    return tokenRegistration;
    }

	/**
	 * Shorthand for creating a criteria query for TokenRegistration 
     */
    private Criteria makeCriteria() {
    	return sessionFactory.getCurrentSession().createCriteria(TokenRegistration.class);
    }

	/**
	 * Shorthand for creating a criteria query for TokenRegistration where token is like query
     */
    private Criteria makeCriteria(String query) {
    	Criteria crit = makeCriteria();
    	if (StringUtils.isNotBlank(query))
			crit.add(Restrictions.ilike("token", query, MatchMode.ANYWHERE));
		return crit;
    }

	/**
     * @see org.openmrs.logic.token.db.TokenDAO#getTags(java.lang.String)
     */
    @Transactional(readOnly=true)
    public List<String> getTags(String partialTag) {
    	// TODO this is inefficient
		List<String> allTags = new ArrayList<String>();
		Query query = sessionFactory.getCurrentSession().createQuery(
		    "select tokenRegistration from TokenRegistration tokenRegistration where exists elements(tokenRegistration.tags) ");
		Iterator<?> logicTokens = query.iterate();
		while (logicTokens.hasNext()) {
			TokenRegistration logicToken = (TokenRegistration) logicTokens.next();
			for (String tag : logicToken.getTags()) {
				if (tag.matches("^.*" + partialTag + ".*$")) {
					allTags.add(tag);
				}
			}
		}
		return allTags;
    }

	/**
     * @see org.openmrs.logic.token.db.TokenDAO#getTokensByTag(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true)
    public List<String> getTokensByTag(String tag) {
    	String hql = "select token from TokenRegistration where :tag in elements(tags)";
    	Query query = sessionFactory.getCurrentSession().createQuery(hql);
    	query.setString("tag", tag);
    	return query.list();
    }

    /**
     * @see org.openmrs.logic.token.db.TokenDAO#getTokenRegistrationsByProvider(org.openmrs.logic.rule.provider.RuleProvider)
     */
    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true)
    public List<TokenRegistration> getTokenRegistrations(String token, RuleProvider provider, String providerToken, String configuration) {
    	Criteria crit = makeCriteria();
    	if (token != null)
    		crit.add(Restrictions.ilike("token", token, MatchMode.EXACT));
    	if (provider != null)
    		crit.add(Restrictions.eq("providerClassName", provider.getClass().getName()));
    	if (providerToken != null)
    		crit.add(Restrictions.eq("providerToken", providerToken));
    	if (configuration != null)
    		crit.add(Restrictions.eq("configuration", configuration));
    	crit.addOrder(Order.asc("token"));
    	return crit.list();
    }
    
    /**
     * @see org.openmrs.logic.token.db.TokenDAO#deleteConfigurationsNotIn(org.openmrs.logic.rule.provider.RuleProvider, java.util.List)
     */
    public void deleteConfigurationsNotIn(RuleProvider provider, List<String> validConfigurations) {
        Query query = sessionFactory.getCurrentSession().createQuery("delete from TokenRegistration where providerClassName = :providerClassName and configuration not in (:validConfigs)");
        query.setString("providerClassName", provider.getClass().getName());
        query.setParameter("validConfigs", validConfigurations);
    }
}
