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

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.openmrs.util.OpenmrsUtil;

/**
 * The context within which logic rule and data source evaluations are made. The logic context is
 * responsible for maintaining context-sensitive information &mdash; e.g., the index date and global
 * parameters &mdash; as well as handling caching of results. <strong>Index date</strong> is the
 * date used as "today" for any calculations or queries. This allows the same rule to be evaluated
 * retrospectively. For example, a rule calculating the "maximum CD4 count in the past six months"
 * can be calculated as if it were 4-July-2005.
 */
public class LogicContextImpl implements LogicContext {
	
    private static final Logger log = LoggerFactory.getLogger(LogicContextImpl.class);
	
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
	private PatientCohort patients;
		
	/**
	 * Cache used by this logic context
	 * 
	 * @see org.openmrs.logic.LogicCache
	 */
	private LogicCache cache;
	
	/**
	 * Creates a {@link LogicContext} that inherits from parentContext (meaning that it shares a
	 * cohort of patients, and its global parameters, and changes to those in the new context will
	 * be reflected in the parent context).
	 * If newIndexDate is non-null, then it will override the index date from the parent context.
	 * This LogicContext has its own cache, although eventually it should share a cache with its
	 * parent (once caches support index dates in some way)
	 * @param parentContext
	 * @param newIndexDate
	 */
	private LogicContextImpl(LogicContextImpl parentContext, Date newIndexDate) {
		this.parentContext = parentContext;
		this.globalParameters = parentContext.globalParameters;
		this.patients = parentContext.patients;
		// once we fix the cache to also contain either the context or the index date in its cache keys, we should share our parent's cache
		// this.cache = parentContext.cache;
		this.indexDate = newIndexDate != null ? newIndexDate : parentContext.indexDate;
	}
	
	/**
	 * Constructs a logic context applied to a single patient
	 * 
	 * @param patientId
	 */
	public LogicContextImpl(Integer patientId) {
		this.patients = new PatientCohort();
		this.globalParameters = new HashMap<String, Object>();
		patients.addMember(patientId);
		setIndexDate(newIndexDate());
	}
	
	/**
	 * Constructs a logic context applied to a cohort of patients
	 * 
	 * @param patients
	 */
	public LogicContextImpl(Cohort patients) {
		if (patients instanceof PatientCohort)
			this.patients = (PatientCohort) patients;
		else
			this.patients = new PatientCohort(patients);
		this.globalParameters = new HashMap<String, Object>();
		setIndexDate(newIndexDate());
	}
	
	/**
	 * @return the default indexDate, i.e. the first second of today. (The real today, not today().)
	 */
	private Date newIndexDate() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		return cal.getTime();
	}
	
	/**
	 * @see org.openmrs.logic.LogicContext#getPatient(java.lang.Integer)
	 */
	public Patient getPatient(Integer patientId) {
		return patients.getPatient(patientId);
	}
	
	/**
	 * @see org.openmrs.logic.LogicContext#eval(java.lang.Integer, java.lang.String)
	 */
	public Result eval(Integer patientId, String token) throws LogicException {
		return eval(patientId, new LogicCriteriaImpl(token), null);
	}
	
	/**
	 * @see org.openmrs.logic.LogicContext#eval(java.lang.Integer, java.lang.String,
	 *      java.util.Map)
	 */
	public Result eval(Integer patientId, String token, Map<String, Object> parameters) throws LogicException {
		return eval(patientId, new LogicCriteriaImpl(token), parameters);
	}
	
	/**
	 * @see org.openmrs.logic.LogicContext#eval(java.lang.Integer,
	 *      org.openmrs.logic.LogicCriteria, java.util.Map)
	 * @should evaluate a rule that requires a new index date in a new logic context
	 * @should behave right when a rule and a subrule with a different index date evaluate the same criteria
	 */
	public Result eval(Integer patientId, LogicCriteria criteria, Map<String, Object> parameters) throws LogicException {
		Result result = getCache().get(patientId, criteria, parameters);
		
		if (result == null) {
			// if criteria specifies an index date, and it differs from the current index date, we need to
			// evaluate this in a newly-created logic context
			Date criteriaIndexDate = getIndexDate(criteria);
			if (criteriaIndexDate != null && !OpenmrsUtil.nullSafeEquals(criteriaIndexDate, getIndexDate())) {

				return new LogicContextImpl(this, criteriaIndexDate).eval(patientId, criteria, parameters);

			} else {

				Rule rule = Context.getLogicService().getRule(criteria.getRootToken());
				Map<Integer, Result> resultMap = new Hashtable<Integer, Result>();
	
				for (Integer currPatientId : patients.getMemberIds()) {
					Result r = Result.emptyResult();
					if (rule instanceof ReferenceRule) {
						r = ((ReferenceRule) rule).eval(this, currPatientId, criteria);
					} else {
						r = rule.eval(this, currPatientId, parameters);
						applyCriteria(r, criteria);
					}
					
					resultMap.put(currPatientId, r);
				}
				result = resultMap.get(patientId);
				getCache().put(criteria, parameters, rule.getTTL(), resultMap);
				
			}
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
	 * @see org.openmrs.logic.LogicContext#read(java.lang.Integer,
	 *      org.openmrs.logic.datasource.LogicDataSource, java.lang.String)
	 */
	public Result read(Integer patientId, LogicDataSource dataSource, String key) throws LogicException {
		return read(patientId, dataSource, new LogicCriteriaImpl(key));
	}
	
	/**
	 * @see org.openmrs.logic.LogicContext#read(java.lang.Integer, java.lang.String)
	 */
	public Result read(Integer patientId, String key) throws LogicException {
		
		LogicService logicService = Context.getLogicService();
		LogicDataSource dataSource = logicService.getLogicDataSource("obs");
		return read(patientId, dataSource, key);
	}
	
	/**
	 * @see org.openmrs.logic.LogicContext#read(java.lang.Integer,
	 *      org.openmrs.logic.LogicCriteria)
	 */
	public Result read(Integer patientId, LogicCriteria criteria) throws LogicException {
		LogicService logicService = Context.getLogicService();
		LogicDataSource dataSource = logicService.getLogicDataSource("obs");
		return read(patientId, dataSource, criteria);
	}
	
	/**
	 * @see org.openmrs.logic.LogicContext#read(java.lang.Integer,
	 *      org.openmrs.logic.datasource.LogicDataSource, org.openmrs.logic.LogicCriteria)
	 */
	public Result read(Integer patientId, LogicDataSource dataSource, LogicCriteria criteria) throws LogicException {
		Result result = getCache().get(patientId, dataSource, criteria);
		String resultVal = null;
		if (log.isDebugEnabled())
		    resultVal = result == null ? "NOT" : "";
			log.debug("Reading from data source: {} ({} cached)", criteria.getRootToken(), resultVal);
		if (result == null) {
			Map<Integer, Result> resultMap = dataSource.read(this, patients, criteria);
			getCache().put(dataSource, criteria, resultMap);
			result = resultMap.get(patientId);
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
