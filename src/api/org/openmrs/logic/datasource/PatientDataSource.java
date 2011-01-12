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
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.db.LogicPatientDAO;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.rule.provider.RuleProvider;
import org.openmrs.logic.rule.provider.SimpleReferenceRuleProvider;
import org.openmrs.logic.util.LogicUtil;

/**
 * Provides access to patient data. Valid keys are:
 * <ul>
 * <li><strong>identifier</strong> &mdash; text result of patient identifiers</li>
 * </ul>
 */
public class PatientDataSource extends SimpleReferenceRuleProvider implements LogicDataSource, RuleProvider {
	
	private static final Collection<String> keys = new ArrayList<String>();
	
	private LogicPatientDAO logicPatientDAO;
	
	/**
	 * @return the logicPatientDAO
	 */
	public LogicPatientDAO getLogicPatientDAO() {
		return logicPatientDAO;
	}
	
	/**
	 * @param logicPatientDAO the logicPatientDAO to set
	 */
	public void setLogicPatientDAO(LogicPatientDAO logicPatientDAO) {
		this.logicPatientDAO = logicPatientDAO;
	}
	
	static {
		String[] keyList = new String[] { "identifier" };
		for (String k : keyList)
			keys.add(k);
	}
	
	/**
	 * @see org.openmrs.logic.datasource.LogicDataSource#read(LogicContext, Cohort, LogicCriteria)
	 * @should read identifier
	 */
	public Map<Integer, Result> read(LogicContext context, Cohort who, LogicCriteria criteria) {
		
		Map<Integer, Result> resultMap = new HashMap<Integer, Result>();
		// calculate
		List<Patient> patientList = getLogicPatientDAO().getPatients(who.getMemberIds(), criteria);
		
		String token = criteria.getRootToken();
		
		// put in the result map
		for (Patient patient : patientList) {
			if (token.equalsIgnoreCase("identifier")) {
				PatientIdentifier identifier = patient.getPatientIdentifier();
				resultMap.put(patient.getPatientId(), new Result(identifier.getIdentifier()));
			}
		}
		
		LogicUtil.applyAggregators(resultMap, criteria, who);
		return resultMap;
	}
	
	/**
	 * @see org.openmrs.logic.datasource.LogicDataSource#getDefaultTTL()
	 */
	public int getDefaultTTL() {
		return 60 * 60 * 4; // 4 hours
	}
	
	/**
	 * @see org.openmrs.logic.datasource.LogicDataSource#getKeys()
	 */
	@Override
	public Collection<String> getKeys() {
		return keys;
	}
	
	/**
	 * @see org.openmrs.logic.datasource.LogicDataSource#hasKey(java.lang.String)
	 */
	public boolean hasKey(String key) {
		return getKeys().contains(key);
	}
	
	/**
	 * @see org.openmrs.logic.datasource.DataSourceRuleProvider#getDataSourceName()
	 */
	@Override
	public String getReferenceRulePrefix() {
		return "patient";
	}
	
}
