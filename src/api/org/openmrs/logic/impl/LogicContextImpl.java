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

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicCache;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.LogicExpression;
import org.openmrs.logic.LogicExpressionBinary;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.Rule;
import org.openmrs.logic.datasource.LogicDataSource;
import org.openmrs.logic.op.Operand;
import org.openmrs.logic.op.OperandDate;
import org.openmrs.logic.op.Operator;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.rule.ReferenceRule;

/**
 * The context within which logic rule and data source evaluations are made. The logic context is
 * responsible for maintaining context-sensitive information &mdash; e.g., the index date and global
 * parameters &mdash; as well as handling caching of results. <strong>Index date</strong> is the
 * date used as "today" for any calculations or queries. This allows the same rule to be evaluated
 * retrospectively. For example, a rule calculating the "maximum CD4 count in the past six months"
 * can be calculated as if it were 4-July-2005.
 */
public class LogicContextImpl implements LogicContext {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	/**
	 * Hold the index date for this context, representing the value for "today" and thereby allowing
	 * the same rules to be run today as well as retrospectively
	 */
	private Date indexDate;
	
	/**
	 * Globally available parameters within this logic context. Global parameters are available to
	 * all evaluations performed within this context
	 */
	private Map<String, Object> globalParameters;
	
	/**
	 * If this context was constructed from another logic context, this references the original
	 * context; otherwise, this is null
	 */
	@SuppressWarnings("unused")
	private LogicContext parentContext = null;
	
	/**
	 * Patients being processed within this logic context
	 */
	private Cohort patients;
		
	/**
	 * Cache used by this log context
	 * 
	 * @see org.openmrs.logic.LogicCache
	 */
	private LogicCache cache;
	
	/**
	 * Constructs a logic context applied to a single patient
	 * 
	 * @param patient
	 */
	public LogicContextImpl(Patient patient) {
		this.patients = new Cohort();
		this.globalParameters = new HashMap<String, Object>();
		patients.addMember(patient.getPatientId());
		setIndexDate(new Date());
	}
	
	/**
	 * Constructs a logic context applied to a cohort of patients
	 * 
	 * @param patients
	 */
	public LogicContextImpl(Cohort patients) {
		this.patients = patients;
		this.globalParameters = new HashMap<String, Object>();
		setIndexDate(new Date());
	}
	
	/**
	 * @see org.openmrs.logic.LogicContext#eval(org.openmrs.Patient, java.lang.String)
	 */
	public Result eval(Patient patient, String token) throws LogicException {
		return eval(patient, new LogicCriteriaImpl(token), null);
	}
	
	/**
	 * @see org.openmrs.logic.LogicContext#eval(org.openmrs.Patient, java.lang.String,
	 *      java.util.Map)
	 */
	public Result eval(Patient patient, String token, Map<String, Object> parameters) throws LogicException {
		return eval(patient, new LogicCriteriaImpl(token), parameters);
	}
	
	/**
	 * @see org.openmrs.logic.LogicContext#eval(org.openmrs.Patient,
	 *      org.openmrs.logic.LogicCriteria, java.util.Map)
	 */
	public Result eval(Patient patient, LogicCriteria criteria, Map<String, Object> parameters) throws LogicException {
		Result result = getCache().get(patient, criteria, parameters);
		
		if (result == null) {
			// if criteria has an index date, that will override the context's index date for this evaluation
			Date originalIndexDate = getIndexDate();
			Date criteriaIndexDate = getIndexDate(criteria);
			if (criteriaIndexDate != null)
				setIndexDate(criteriaIndexDate);

			Integer targetPatientId = patient.getPatientId();
			log.debug("Context database read (pid = " + targetPatientId + ")");
			Rule rule = Context.getLogicService().getRule(criteria.getRootToken());
			Map<Integer, Result> resultMap = new Hashtable<Integer, Result>();

			for (Patient currPatient : Context.getPatientSetService().getPatients(patients.getMemberIds())) {
				Result r = Result.emptyResult();
				if (rule instanceof ReferenceRule) {
					r = ((ReferenceRule) rule).eval(this, currPatient, criteria);
				} else {
					r = rule.eval(this, currPatient, parameters);
					r = applyCriteria(r, criteria);
				}
				
				resultMap.put(currPatient.getPatientId(), r);
			}
			result = resultMap.get(targetPatientId);
			getCache().put(criteria, parameters, rule.getTTL(), resultMap);
			
			// if we overrode the index date, restore it
			if (criteriaIndexDate != null)
				setIndexDate(originalIndexDate);
		}
		
		return result;
	}

	/**
	 * Criteria are applied to results of rules <em>after</em> the rule has been evaluated, since
	 * rules are not expected to interpret all possible criteria
	 * 
	 * @param result
	 * @param criteria
	 * @return
	 */
	private Result applyCriteria(Result result, LogicCriteria criteria) {
		// TODO: apply criteria to result
		return result;
	}
	
	/**
	 * @see org.openmrs.logic.LogicContext#getLogicDataSource(java.lang.String)
	 */
	public LogicDataSource getLogicDataSource(String name) {
		return Context.getLogicService().getLogicDataSource(name);
	}
	
	/**
	 * @see org.openmrs.logic.LogicContext#read(org.openmrs.Patient,
	 *      org.openmrs.logic.datasource.LogicDataSource, java.lang.String)
	 */
	public Result read(Patient patient, LogicDataSource dataSource, String key) throws LogicException {
		return read(patient, dataSource, new LogicCriteriaImpl(key));
	}
	
	/**
	 * @see org.openmrs.logic.LogicContext#read(org.openmrs.Patient, java.lang.String)
	 */
	public Result read(Patient patient, String key) throws LogicException {
		
		LogicService logicService = Context.getLogicService();
		LogicDataSource dataSource = logicService.getLogicDataSource("obs");
		return read(patient, dataSource, key);
	}
	
	/**
	 * @see org.openmrs.logic.LogicContext#read(org.openmrs.Patient,
	 *      org.openmrs.logic.LogicCriteria)
	 */
	public Result read(Patient patient, LogicCriteria criteria) throws LogicException {
		LogicService logicService = Context.getLogicService();
		LogicDataSource dataSource = logicService.getLogicDataSource("obs");
		return read(patient, dataSource, criteria);
	}
	
	/**
	 * @see org.openmrs.logic.LogicContext#read(org.openmrs.Patient,
	 *      org.openmrs.logic.datasource.LogicDataSource, org.openmrs.logic.LogicCriteria)
	 */
	public Result read(Patient patient, LogicDataSource dataSource, LogicCriteria criteria) throws LogicException {
		Result result = getCache().get(patient, dataSource, criteria);
		log
		        .debug("Reading from data source: " + criteria.getRootToken() + " (" + (result == null ? "NOT" : "")
		                + " cached)");
		if (result == null) {
			Map<Integer, Result> resultMap = dataSource.read(this, patients, criteria);
			getCache().put(dataSource, criteria, resultMap);
			result = resultMap.get(patient.getPatientId());
		}
		if (result == null)
			result = Result.emptyResult();
		return result;
	}
	
	/**
	 * @see org.openmrs.logic.LogicContext#setIndexDate(java.util.Date)
	 */
	public void setIndexDate(Date indexDate) {
		this.indexDate = indexDate;
	}
	
	/**
	 * @see org.openmrs.logic.LogicContext#getIndexDate()
	 */
	public Date getIndexDate() {
		return indexDate;
	}
	
	/**
	 * @see org.openmrs.logic.LogicContext#today()
	 */
	public Date today() {
		return getIndexDate();
	}
	
	/**
	 * @see org.openmrs.logic.LogicContext#setGlobalParameter(java.lang.String, java.lang.Object)
	 */
	public Object setGlobalParameter(String id, Object value) {
		return globalParameters.put(id, value);
	}
	
	/**
	 * @see org.openmrs.logic.LogicContext#getGlobalParameter(java.lang.String)
	 */
	public Object getGlobalParameter(String id) {
		return globalParameters.get(id);
	}
	
	/**
	 * @see org.openmrs.logic.LogicContext#getGlobalParameters()
	 */
	public Collection<String> getGlobalParameters() {
		return globalParameters.keySet();
	}
	
	/**
	 * @return the cache for this logic context
	 */
	private LogicCache getCache() {
		if (cache == null)
			cache = new LogicCache();
		return cache;
	}

	/**
	 * Get the indexDate specified in this criteria. (Hack: for now this means any AsOf date we can find.)
	 * 
	 * @param criteria
	 * @return
	 */
	private Date getIndexDate(LogicCriteria criteria) {
		return getIndexDate(criteria.getExpression());
	}

	/**
     * Recursively look for an indexDate in expression (i.e. the right operand of any ASOF operator we can find)
     * 
     * @param expression
     * @return any indexDate specified in this expression
     */
    private Date getIndexDate(LogicExpression expression) {
	    if (Operator.ASOF.equals(expression.getOperator())) {
	    	return (OperandDate) expression.getRightOperand();
	    } else {
	    	Operand operand = expression.getRightOperand();
	    	if (operand instanceof LogicExpression) {
	    		Date date = getIndexDate((LogicExpression) operand);
	    		if (date != null)
	    			return date;
	    	}
	    	if (expression instanceof LogicExpressionBinary) {
	    		operand = ((LogicExpressionBinary) expression).getLeftOperand();
	    		if (operand instanceof LogicExpression) {
		    		Date date = getIndexDate((LogicExpression) operand);
		    		if (date != null)
		    			return date;
		    	}
	    	}
	    }
	    return null;
    }
    
}
