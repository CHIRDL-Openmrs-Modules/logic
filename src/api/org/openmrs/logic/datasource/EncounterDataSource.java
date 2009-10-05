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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.Cohort;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.User;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.db.LogicEncounterDAO;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.util.Util;
import org.openmrs.logic.datasource.LogicDataSource;

/**
 * Provides access to encounter metadata.
 * 
 * Results have a result date equal to the encounter datetime and a value based
 * on the key passed in.
 * 
 * Valid keys are:
 * <ul>
 *   <li>
 *     <strong>encounterDatetime</strong> &mdash;  
 *     date result of the {@link Encounter#getEncounterDatetime()}
 *   </li>
 *   <li>
 *     <strong>encounterType</strong> &mdash; 
 *     text result of the encounter's {@link EncounterType#getName() name}
 *   </li>
 *   <li>
 *     <strong>encounterLocation</strong> &mdash; 
 *     text result of the encounter's location's {@link Location#getName() name}
 *   </li>
 *   <li>
 *     <strong>encounterProvider</strong> &mdash; 
 *     text result of the encounter's provider's {@link User#getName() name}
 *   </li>
 * 
 * </ul>
 */
public class EncounterDataSource implements LogicDataSource {
	
	private static final Collection<String> keys = new ArrayList<String>();
	
	private LogicEncounterDAO logicEncounterDAO;
	
	static {
        String[] keyList = new String[] { 
        	"encounterDatetime",
        	"encounterType",
        	"encounterLocation",
        	"encounterProvider"
        };
		
		for (String k : keyList)
			keys.add(k);
	}
	
	public void setLogicEncounterDAO(LogicEncounterDAO logicEncounterDAO) {
		this.logicEncounterDAO = logicEncounterDAO;
	}
	
	public LogicEncounterDAO getLogicEncounterDAO() {
		return logicEncounterDAO;
	}
	
	/**
	 * @see org.openmrs.logic.datasource.LogicDataSource#read(org.openmrs.logic.LogicContext,
	 *      org.openmrs.Cohort, org.openmrs.logic.LogicCriteria)
	 */
	public Map<Integer, Result> read(LogicContext context, Cohort patients, LogicCriteria criteria) {
		
		Map<Integer, Result> finalResult = new HashMap<Integer, Result>();
		List<Encounter> encounters = getLogicEncounterDAO().getEncounters(patients, criteria);
		
		String rootToken = criteria.getRootToken();

		// group the received Encounters by patient and convert them to
		// Results
		for (Encounter encounter : encounters) {
			int personId = encounter.getPatient().getPatientId();
			Result result = finalResult.get(personId);
			if (result == null) {
				result = new Result();
				finalResult.put(personId, result);
			}Date encounterDatetime = encounter.getEncounterDatetime();
			
			if ("encounterDatetime".equalsIgnoreCase(rootToken)) {
				// add the encounter date as the result
				// (most commonly used)
				result.add(new Result(encounterDatetime, encounterDatetime, encounter));
			}
			else if ("encounterType".equalsIgnoreCase(rootToken)) {
				EncounterType type = encounter.getEncounterType();
				String encounterTypeName = "";
				if (type != null)
					encounterTypeName = type.getName();
				
				// add the type as the result
				result.add(new Result(encounterDatetime, encounterTypeName, type));
			}
			else if ("encounterLocation".equalsIgnoreCase(rootToken)) {
				Location location = encounter.getLocation();
				String locationName = "";
				if (location != null)
					locationName = location.getName();
				
				// add the location as the result
				result.add(new Result(encounterDatetime, locationName, location));
			}
			else if ("encounterProvider".equalsIgnoreCase(rootToken)) {
				String providerName = "";
				User provider = encounter.getProvider();
				
				// check for null objects
				if (provider == null) {
					// TODO should this return a string like this, or just null?
					providerName = "(no provider)";
				}
				else if (provider.getPersonName() == null) {
					providerName = "(no provider name)";
				}
				else {
					providerName = provider.getPersonName().getFullName();
				}
				
				// add the provider as the result
				result.add(new Result(encounterDatetime, providerName, provider));
			}
			
			result.add(new Result(encounter.getEncounterDatetime(), Datatype.DATETIME, false, null, encounter
			        .getEncounterDatetime(), null, null, encounter));
		}
		Util.applyAggregators(finalResult, criteria, patients);
		
		return finalResult;
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
	
}
