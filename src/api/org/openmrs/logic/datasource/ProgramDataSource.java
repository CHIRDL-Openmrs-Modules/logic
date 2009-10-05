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
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.util.Util;

/**
 * Provides access to patient program data
 * Valid keys are:
 * <ul>
 *   <li>
 *     <strong>program enrollment</strong> &mdash;  
 *     coded result of the program's {@link Program#getConcept() concept}.<br/>
 *     result date is the {@link PatientProgram#getDateEnrolled() date enrolled}
 *   </li>
 *   <li>
 *     <strong>program completion</strong> &mdash;  
 *     coded result of the program's {@link Program#getConcept() concept}.<br/>
 *     result date is the {@link PatientProgram#getDateCompleted() date completed}
 *   </li>
 *   <li>
 *     <strong>current state</strong> &mdash;  
 *     coded result of the patient program's current state's {@link ProgramWorkflowState#getConcept() concept}.<br/>
 *     result date is the {@link PatientProgram#getDateEnrolled() date enrolled}
 *   </li>
 * </ul>
 */
public class ProgramDataSource implements LogicDataSource {
	
	private Log log = LogFactory.getLog(ProgramDataSource.class);
	
	private static final Collection<String> keys = new ArrayList<String>();
	
	private static String PROGRAM_ENROLLMENT_KEY = "PROGRAM ENROLLMENT";
	
	private static String PROGRAM_COMPLETED_KEY = "PROGRAM COMPLETION";
	
	private static String CURRENT_STATE_KEY = "CURRENT STATE";
	
	static {
		String[] keyList = new String[] { PROGRAM_ENROLLMENT_KEY, PROGRAM_COMPLETED_KEY, CURRENT_STATE_KEY };
		
		for (String k : keyList)
			keys.add(k);
	}
	
	/**
	 * @see {@link org.openmrs.logic.datasource.LogicDataSource#read(LogicContext, Cohort, LogicCriteria)}
	 */
    public Map<Integer, Result> read(LogicContext context, Cohort patients, LogicCriteria criteria) {
		
		if (log.isInfoEnabled())
			log.info("read patient programs for " + patients.size() + " patients, criteria " + criteria);
		
		Map<Integer, Result> resultSet = new HashMap<Integer, Result>();
		
		Collection<PatientProgram> patientPrograms = getPatientPrograms(patients, criteria);
		
		if (log.isDebugEnabled())
			log.debug("found " + patientPrograms.size() + " patient programs");
		
		// loop over all the patient programs and create Result objects for it
		for (PatientProgram patientProgram : patientPrograms) {
			//log.info("PatientProgram: " + patientProgram.getDateEnrolled());
			String token = criteria.getRootToken();
			Integer personId = patientProgram.getPatient().getPersonId();
			
			Result result = null;
			
			if (PROGRAM_ENROLLMENT_KEY.equalsIgnoreCase(token)) {
				result = new Result(patientProgram.getProgram().getConcept());
				result.setResultDate(patientProgram.getDateEnrolled());
			} else if (PROGRAM_COMPLETED_KEY.equalsIgnoreCase(token)) {
				result = new Result(patientProgram.getProgram().getConcept());
				result.setResultDate(patientProgram.getDateCompleted());
			} else if (CURRENT_STATE_KEY.equalsIgnoreCase(token)) {
				result = new Result(patientProgram.getCurrentState(null).getState().getConcept());
				result.setResultDate(patientProgram.getDateEnrolled());
			}
			
			if (result != null) {
				log.info("Add result to result set: " + result);
				if (!resultSet.containsKey(personId)) {
					resultSet.put(personId, result);
				} else {
					resultSet.get(personId).add(result);
				}
			}
		}
		
		if (log.isDebugEnabled())
			log.debug("applying aggregators");

		Util.applyAggregators(resultSet, criteria, patients);
		return resultSet;
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
		return getKeys().contains(key);
	}
	
	/**
	 * Convenience method to get the patient programs for the given patients
	 * for this logic query 
	 * 
	 * @param patients the current cohort of patients to restrict to 
	 * @param criteria (not currently used)
	 * @return all patient programs for all patients in the given cohort 
	 */
	private Collection<PatientProgram> getPatientPrograms(Cohort patients, LogicCriteria criteria) {
		ProgramWorkflowService service = Context.getProgramWorkflowService();
		return service.getPatientPrograms(patients, null); 
	}
}
