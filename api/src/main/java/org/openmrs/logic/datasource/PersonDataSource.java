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

import org.apache.commons.lang.StringUtils;
import org.openmrs.Cohort;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.db.LogicPersonDAO;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.rule.provider.RuleProvider;
import org.openmrs.logic.rule.provider.SimpleDataSourceRuleProvider;
import org.openmrs.logic.util.LogicUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Provides access to person demographic data. Valid keys are:
 * <ul>
 * <li><strong>gender</strong> &mdash; text result of "M" or "F"</li>
 * <li><strong>birthdate</strong> &mdash; date result with patient's birthdate</li>
 * <li><strong>birthdate estimated</strong> &mdash; boolean result (true if patient's birthdate is
 * estimated)</li>
 * <li><strong>death</strong> &mdash; a coded result containing the cause of death with the result
 * date equal to the death date. If the patient is not dead, then the result is null.</li>
 * </ul>
 */
@Repository
public class PersonDataSource extends SimpleDataSourceRuleProvider implements LogicDataSource, RuleProvider {
	
	public static final String NAME = "person";
	
	private static final Collection<String> keys = new ArrayList<String>();
	
	@Autowired
	private LogicPersonDAO logicPersonDAO;
	
	/**
	 * @return the logicPersonDAO
	 */
	public LogicPersonDAO getLogicPersonDAO() {
		return logicPersonDAO;
	}
	
	/**
	 * @param logicPersonDAO the logicPersonDAO to set
	 */
	public void setLogicPersonDAO(LogicPersonDAO logicPersonDAO) {
		this.logicPersonDAO = logicPersonDAO;
	}
	
	static {
		String[] keyList = new String[] { "gender", "birthdate", "birthdate estimated", "dead", "death date",
		        "cause of death", "given name", "middle name", "family name", "family name2" };
		for (String k : keyList)
			keys.add(k);
	}
	
	/**
	 * @see org.openmrs.logic.datasource.LogicDataSource#read(LogicContext, Cohort, LogicCriteria)
	 * @should get gender
	 * @should get gender equals value
	 * @should get birthdate
	 * @should get birthdate_estimated
	 * @should get dead
	 * @should get death_date
	 * @should get cause_of_death
	 * @should get first name
	 * @should get middle name
	 * @should get family name
	 * @should get family name2
	 */
	public Map<Integer, Result> read(LogicContext context, Cohort who, LogicCriteria criteria) {
		
		Map<Integer, Result> resultMap = new HashMap<Integer, Result>();
		// calculate
		List<Person> personList = getLogicPersonDAO().getPersons(who.getMemberIds(), criteria);
		
		String token = criteria.getRootToken();
		
		// put in the result map
		for (Person person : personList) {
			if (token.equalsIgnoreCase("gender"))
				resultMap.put(person.getPersonId(), new Result(person.getGender()));
			else if (token.equalsIgnoreCase("birthdate"))
				resultMap.put(person.getPersonId(), new Result(person.getBirthdate()));
			else if (token.equalsIgnoreCase("birthdate estimated"))
				resultMap.put(person.getPersonId(), new Result(person.getBirthdateEstimated()));
			else if (token.equalsIgnoreCase("death date"))
				resultMap.put(person.getPersonId(), new Result(person.getDeathDate()));
			else if (token.equalsIgnoreCase("cause of death")) {
				Result deathResult;
				if (person.isDead())
					deathResult = new Result(person.getDeathDate(), person.getCauseOfDeath(), person);
				else
					deathResult = Result.emptyResult();
				resultMap.put(person.getPersonId(), deathResult);
			} else if (token.equalsIgnoreCase("dead")) {
				resultMap.put(person.getPersonId(), new Result(person.isDead()));
			} else if (StringUtils.containsIgnoreCase(token, "name")) {
				PersonName personName = person.getPersonName();
				
				String valueText = StringUtils.EMPTY;
				if (StringUtils.equalsIgnoreCase(token, "given name"))
					valueText = personName.getGivenName();
				else if (StringUtils.equalsIgnoreCase(token, "middle name"))
					valueText = personName.getMiddleName();
				else if (StringUtils.equalsIgnoreCase(token, "family name"))
					valueText = personName.getFamilyName();
				else if (StringUtils.equalsIgnoreCase(token, "family name2"))
					valueText = personName.getFamilyName2();
				
				resultMap.put(person.getPersonId(), new Result(new Date(), valueText, personName));
			}
			// TODO more keys to be added
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

}
