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
package org.openmrs.logic.impl;

import java.io.ByteArrayInputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.Rule;
import org.openmrs.logic.datasource.LogicDataSource;
import org.openmrs.logic.queryparser.LogicQueryBaseParser;
import org.openmrs.logic.queryparser.LogicQueryLexer;
import org.openmrs.logic.queryparser.LogicQueryTreeParser;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.logic.token.TokenRegistration;
import org.openmrs.logic.token.TokenService;
import org.springframework.beans.factory.annotation.Autowired;

import antlr.BaseAST;

/**
 * Default implementation of the LogicService. This class should not be used directly. This class,
 * if chosen, is injected into the Context and served up as the LogicService of choice. Use: <code>
 *   LogicService logicService = Context.getLogicService();
 * </code>
 * 
 * @see org.openmrs.api.context.Context
 * @see org.openmrs.logic.LogicService
 */
public class LogicServiceImpl implements LogicService {
	
	protected final Log log = LogFactory.getLog(getClass());

	@Autowired
	private List<LogicDataSource> allLogicDataSources;
	
	private transient Map<String, LogicDataSource> dataSources;
	
	/**
	 * Default constructor
	 */
	public LogicServiceImpl() {
	}
			
	/**
	 * Clean up after this class. Set the static var to null so that the classloader can reclaim the
	 * space.
	 * 
	 * @see org.openmrs.api.impl.BaseOpenmrsService#onShutdown()
	 */
	public void onShutdown() {
		dataSources = null;
	}
	
	/**
	 * @see org.openmrs.logic.LogicService#getTokens()
	 */
	public Set<String> getTokens() {
		Set<String> tokens = new HashSet<String>();
		tokens.addAll(getAllTokens());
		return tokens;
	}
	
	/**
	 * @see org.openmrs.logic.LogicService#getAllTokens()
	 */
	public List<String> getAllTokens() {
		return Context.getService(TokenService.class).getAllTokens();
	}
	
	/**
	 * @see org.openmrs.logic.LogicService#findToken(java.lang.String)
	 */
	public Set<String> findToken(String token) {
		Set<String> tokens = new HashSet<String>();
		tokens.addAll(getTokens(token));
		return tokens;
	}
	
	/**
	 * @see org.openmrs.logic.LogicService#getTokens(java.lang.String)
	 */
	public List<String> getTokens(String partialToken) {
		return Context.getService(TokenService.class).getTokens(partialToken);
	}
	
	/**
	 * @see org.openmrs.logic.LogicService#addRule(java.lang.String, org.openmrs.logic.Rule)
	 */
	public void addRule(String token, Rule rule) throws LogicException {
		throw new UnsupportedOperationException("Use TokenService.registerToken");
	}
	
	/**
	 * @see org.openmrs.logic.LogicService#getRule(java.lang.String)
	 * @should return ReferenceRule when the token are already registered
	 * @should return new ReferenceRule when the special string token are passed
	 * @should return Rule when concept derived name are passed
	 * @should return Rule when registered concept derived name are passed
	 */
	public Rule getRule(String token) throws LogicException {
		return Context.getService(TokenService.class).getRule(token);
	}
	
	/**
	 * @see org.openmrs.logic.LogicService#updateRule(java.lang.String, org.openmrs.logic.Rule)
	 */
	public void updateRule(String token, Rule rule) throws LogicException {
		throw new UnsupportedOperationException("Use TokenService.registerToken");
	}
	
	/**
	 * @see org.openmrs.logic.LogicService#removeRule(java.lang.String)
	 */
	public void removeRule(String token) throws LogicException {
		Context.getService(TokenService.class).removeToken(token);
	}
	
	/**
	 * @see org.openmrs.logic.LogicService#eval(org.openmrs.Patient, java.lang.String)
	 */
	public Result eval(Patient who, String token) throws LogicException {
		return eval(who, new LogicCriteriaImpl(token));
	}
	
	/**
	 * @see org.openmrs.logic.LogicService#eval(Patient, String, Map)
	 */
	public Result eval(Patient who, String token, Map<String, Object> parameters) throws LogicException {
		return eval(who, new LogicCriteriaImpl(token, parameters));
	}
	
	/**
	 * @see org.openmrs.logic.LogicService#eval(Patient, LogicCriteriaImpl)
	 */
	public Result eval(Patient who, LogicCriteria criteria) throws LogicException {
		return eval(who, criteria, criteria.getLogicParameters());
	}
	
	/**
	 * @see org.openmrs.logic.LogicService#eval(Patient, LogicCriteria, Map)
	 */
	public Result eval(Patient who, LogicCriteria criteria, Map<String, Object> parameters) throws LogicException {
		LogicContext context = new LogicContextImpl(who);
		Result result = context.eval(who, criteria, parameters);
		context = null;
		return result;
	}
	
	/**
	 * @see org.openmrs.logic.LogicService#eval(org.openmrs.Cohort, java.lang.String)
	 */
	public Map<Integer, Result> eval(Cohort who, String token) throws LogicException {
		return eval(who, new LogicCriteriaImpl(token));
	}
	
	/**
	 * @see org.openmrs.logic.LogicService#eval(org.openmrs.Cohort, java.lang.String, java.util.Map)
	 */
	public Map<Integer, Result> eval(Cohort who, String token, Map<String, Object> parameters) throws LogicException {
		return eval(who, new LogicCriteriaImpl(token, parameters));
	}
	
	/**
	 * @see org.openmrs.logic.LogicService#eval(org.openmrs.Cohort, org.openmrs.logic.LogicCriteria)
	 */
	public Map<Integer, Result> eval(Cohort who, LogicCriteria criteria) throws LogicException {
		return eval(who, criteria, criteria.getLogicParameters());
	}
	
	/**
	 * @see org.openmrs.logic.LogicService#eval(org.openmrs.Cohort, org.openmrs.logic.LogicCriteria,
	 *      java.util.Map)
	 */
	public Map<Integer, Result> eval(Cohort who, LogicCriteria criteria, Map<String, Object> parameters)
	                                                                                                    throws LogicException {
		LogicContext context = new LogicContextImpl(who);
		Map<Integer, Result> resultMap = new Hashtable<Integer, Result>();
		for (Integer pid : who.getMemberIds())
			resultMap.put(pid, context.eval(new Patient(pid), criteria, parameters));
		context = null;
		return resultMap;
	}
	
	/**
	 * @see org.openmrs.logic.LogicService#eval(org.openmrs.Cohort, java.util.List)
	 */
	public Map<LogicCriteria, Map<Integer, Result>> eval(Cohort patients, List<LogicCriteria> criterias)
	                                                                                                    throws LogicException {
		Map<LogicCriteria, Map<Integer, Result>> result = new HashMap<LogicCriteria, Map<Integer, Result>>();
		
		for (LogicCriteria criteria : criterias) {
			result.put(criteria, eval(patients, criteria));
		}
		
		return result;
	}
	
	/**
	 * @see org.openmrs.logic.LogicService#addRule(String, String[], Rule)
	 */
	public void addRule(String token, String[] tags, Rule rule) throws LogicException {
		throw new UnsupportedOperationException("Use TokenService.registerToken and manually add tags");
	}
	
	/**
	 * @see org.openmrs.logic.LogicService#addTokenTag(java.lang.String, java.lang.String)
	 */
	public void addTokenTag(String token, String tag) {
		TokenRegistration tr = Context.getService(TokenService.class).getTokenRegistrationByToken(token);
		tr.addTag(tag);
		Context.getService(TokenService.class).saveTokenRegistration(tr);
	}
	
	/**
	 * @see org.openmrs.logic.LogicService#findTags(java.lang.String)
	 */
	public Set<String> findTags(String partialTag) {
		Set<String> tags = new HashSet<String>();
		tags.addAll(getTags(partialTag));
		return tags;
	}
	
	/**
	 * @see org.openmrs.logic.LogicService#getTags(java.lang.String)
	 */
	public List<String> getTags(String partialTag) {
		return Context.getService(TokenService.class).getTags(partialTag);
	}
	
	/**
	 * @see org.openmrs.logic.LogicService#getTagsByToken(java.lang.String)
	 */
	public Collection<String> getTagsByToken(String token) {
		return Context.getService(TokenService.class).getTokenRegistrationByToken(token).getTags();
	}
	
	/**
	 * @see org.openmrs.logic.LogicService#getTokenTags(java.lang.String)
	 */
	public Set<String> getTokenTags(String token) {
		return Context.getService(TokenService.class).getTokenRegistrationByToken(token).getTags();
	}
	
	/**
	 * @see org.openmrs.logic.LogicService#getTokensByTag(java.lang.String)
	 */
	public Set<String> getTokensByTag(String tag) {
		Set<String> tokens = new HashSet<String>();
		tokens.addAll(getTokensWithTag(tag));
		return tokens;
	}
	
	/**
	 * @see org.openmrs.logic.LogicService#getTokensWithTag(java.lang.String)
	 */
	public List<String> getTokensWithTag(String tag) {
		return Context.getService(TokenService.class).getTokensByTag(tag);
	}
	
	/**
	 * @see org.openmrs.logic.LogicService#removeTokenTag(java.lang.String, java.lang.String)
	 */
	public void removeTokenTag(String token, String tag) {
		TokenRegistration tr = Context.getService(TokenService.class).getTokenRegistrationByToken(token);
		tr.removeTag(tag);
		Context.getService(TokenService.class).saveTokenRegistration(tr);
	}
	
	/**
	 * @see org.openmrs.logic.LogicService#getDefaultDatatype(String)
	 */
	public Datatype getDefaultDatatype(String token) {
		return Context.getService(TokenService.class).getRule(token).getDefaultDatatype();
	}
	
	public Set<RuleParameterInfo> getParameterList(String token) {
		return Context.getService(TokenService.class).getRule(token).getParameterList();
	}
	
	/**
	 * @deprecated data sources are now auto-registered via Spring
	 * @see org.openmrs.logic.LogicService#registerLogicDataSource(java.lang.String,
	 *      org.openmrs.logic.datasource.LogicDataSource)
	 */
	public void registerLogicDataSource(String name, LogicDataSource dataSource) throws LogicException {
		// do nothing
	}
	
	/**
	 * @see org.openmrs.logic.LogicService#getLogicDataSource(String)
	 */
	public LogicDataSource getLogicDataSource(String name) {
		return getLogicDataSources().get(name);
	}
	
	/**
	 * @see org.openmrs.logic.LogicService#getLogicDataSources()
	 */
	public Map<String, LogicDataSource> getLogicDataSources() {
		if (dataSources == null) {
			dataSources = new Hashtable<String, LogicDataSource>();
			for (LogicDataSource ds : allLogicDataSources) {
				String name = null;
                try {
	                name = (String) ds.getClass().getField("NAME").get(ds);
                } catch (Exception ex) { }
                if (name == null)
                	throw new LogicException("All data sources must declare a unique public static NAME property");
				dataSources.put(name, ds);
			}
		}
		return dataSources;
	}
	
	/**
	 * @deprecated data sources are now auto-registered via Spring
	 * @see org.openmrs.logic.LogicService#setLogicDataSources(Map)
	 */
	public void setLogicDataSources(Map<String, LogicDataSource> dataSources) throws LogicException {
		// do nothing
	}
	
	/**
	 * @deprecated data sources are now auto-registered via Spring
	 * @see org.openmrs.logic.LogicService#removeLogicDataSource(java.lang.String)
	 */
	public void removeLogicDataSource(String name) {
		// do nothing
	}
	
	/**
	 * @see org.openmrs.logic.LogicService#parseString(java.lang.String)
	 */
	public LogicCriteria parseString(String inStr) {
		return parse(inStr);
	}
	
	/**
	 * @should correctly parse expression with only aggregator and token
	 * @see org.openmrs.logic.LogicService#parse(java.lang.String)
	 */
	public LogicCriteria parse(String criteria) {
		
		try {
			if (!criteria.endsWith(";")) {
				criteria += ";";
			}
			byte currentBytes[] = criteria.getBytes();
			
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(currentBytes);
			
			// Create a scanner that reads from the input stream passed to us
			LogicQueryLexer lexer = new LogicQueryLexer(byteArrayInputStream);
			
			// Create a parser that reads from the scanner
			LogicQueryBaseParser parser = new LogicQueryBaseParser(lexer);
			
			// start parsing at the compilationUnit rule
			parser.query_parse();
			
			BaseAST t = (BaseAST) parser.getAST();
			
			//System.out.println(t.toStringTree());     // prints Abstract Syntax Tree
			
			LogicQueryTreeParser treeParser = new LogicQueryTreeParser();
			
			LogicCriteriaImpl lc = treeParser.query_AST(t);
			// System.out.println(lc.toString());
			return lc;
		}
		catch (Exception e) {
			throw new LogicException("Could not parse expression", e);
		}
	}
}
