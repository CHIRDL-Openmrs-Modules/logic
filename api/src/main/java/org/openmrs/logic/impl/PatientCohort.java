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

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;


/**
 * A Cohort that you can query to get full Patient objects.
 * If you request a patient, all patients in the cohort will be fetched from the database at once.
 * If you do not request a patient, this class is as efficient as a plain Cohort.
 */
public class PatientCohort extends Cohort {

	public static final long serialVersionUID = 1L;
	
	private static final Logger log = LoggerFactory.getLogger(PatientCohort.class);
	
	private transient Map<Integer, Patient> patients;
	
	/**
     * No-arg constructor
     */
    public PatientCohort() {
	    super();
    }

    /**
     * "Copy" constructor (not exactly)
     * @param cohort {@link Cohort} to copy
     */
    public PatientCohort(Cohort cohort) {
	    super(cohort.getMemberIds());
    }

	/**
	 * Gets the full {@link Patient} object for the given patient id, possibly doing an expensive query if
	 * this is the first time this or {@link #getPatients()} has been called.
	 * 
	 * @param patientId
	 * @return
	 */
	public Patient getPatient(Integer patientId) {
		return getPatients().get(patientId);
	}
	
	/**
	 * Gets full {@link Patient} objects for everyone in this cohort, possibly doing an expensive query
	 * if this is the first time this or {@link #getPatient(Integer)} has been called.
	 * 
	 * @return
	 */
	public synchronized Map<Integer, Patient> getPatients() {
		if (patients == null) {
			log.debug("Fetching patients from DAO");
			patients = new HashMap<Integer, Patient>();
			
			// CHICA-1151 The PatientSet service no longer exists
			// This method is causing FullNameRuleTest to fail as well as LogicServiceTest
			// We could create a new method in the PatientService that gets a list of patients by patientIds
			// However, since this only appears to be used by tests, query the PatientService one at a time, so that we don't
			// have to keep adding code to openmrs that we'll have to add every time we upgrade
			PatientService patientService = Context.getPatientService();
			for(Integer patientId : getMemberIds())
			{
				Patient patient = patientService.getPatient(patientId);
				if(patient != null)
				{
					patients.put(patient.getPatientId(), patient);
				}	
			}
			
			// ******* Original code
			//for (Patient patient : Context.getPatientSetService().getPatients(getMemberIds()))
				//patients.put(patient.getPatientId(), patient);
		}
		return patients;
	}

}
