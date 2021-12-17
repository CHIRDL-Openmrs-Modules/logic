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
package org.openmrs.logic;

import java.util.Calendar;

import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openmrs.logic.impl.LogicCriteriaImpl;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;

/**
 * 
 */
public class LogicCriteriaTest extends BaseModuleContextSensitiveTest {
	
	/**
	 * Runs the basic stuff
	 * 
	 * @throws Exception
	 */
	@Before
	public void runBeforeEachTest() throws Exception {
		executeDataSet("org/openmrs/logic/include/LogicStandardDatasets.xml");
	}
	
	/**
	 * TODO break this test into a lot of smaller tests
	 */
	@Test
	public void shouldLogicCriteria() {
		LogicCriteria criteria = new LogicCriteriaImpl("CD4 COUNT").within(Duration.months(6)).exists();
		
		criteria = new LogicCriteriaImpl("CURRENT ANTIRETROVIRAL DRUGS USED FOR TREATMENT");
		
		Assertions.assertEquals("CURRENT ANTIRETROVIRAL DRUGS USED FOR TREATMENT", criteria.getRootToken());
		
		criteria = new LogicCriteriaImpl("CD4 COUNT");
		Assertions.assertEquals("CD4 COUNT", criteria.getRootToken());
		
		criteria = new LogicCriteriaImpl("CD4 COUNT").lt(170);
		Assertions.assertEquals("CD4 COUNT", criteria.getRootToken());
		
		criteria = new LogicCriteriaImpl("CD4 COUNT").gt(185);
		Assertions.assertEquals("CD4 COUNT", criteria.getRootToken());
		
		criteria = new LogicCriteriaImpl("CD4 COUNT").equalTo(190);
		Assertions.assertEquals("CD4 COUNT", criteria.getRootToken());
		
		Calendar cal = Calendar.getInstance();
		cal.set(2006, 3, 11);
		
		criteria = new LogicCriteriaImpl("CD4 COUNT").before(cal.getTime());
		Assertions.assertEquals("CD4 COUNT", criteria.getRootToken());
		
		criteria = new LogicCriteriaImpl("CD4 COUNT").lt(190).before(cal.getTime());
		Assertions.assertEquals("CD4 COUNT", criteria.getRootToken());
		
		criteria = new LogicCriteriaImpl("CD4 COUNT").after(cal.getTime());
		Assertions.assertEquals("CD4 COUNT", criteria.getRootToken());
		
		criteria = new LogicCriteriaImpl("CD4 COUNT").last();
		Assertions.assertEquals("CD4 COUNT", criteria.getRootToken());
		
		criteria = new LogicCriteriaImpl("CD4 COUNT").lt(200);
		Assertions.assertEquals("CD4 COUNT", criteria.getRootToken());
		
		criteria = new LogicCriteriaImpl("PROBLEM ADDED").contains("HUMAN IMMUNODEFICIENCY VIRUS");
		Assertions.assertEquals("PROBLEM ADDED", criteria.getRootToken());
		
		criteria = new LogicCriteriaImpl("CD4 COUNT").within(Duration.months(1));
		Assertions.assertEquals("CD4 COUNT", criteria.getRootToken());
		
		criteria = new LogicCriteriaImpl("AGE").gt(10);
		Assertions.assertEquals("AGE", criteria.getRootToken());
		
		criteria = new LogicCriteriaImpl("BIRTHDATE").after(cal.getTime());
		Assertions.assertEquals("BIRTHDATE", criteria.getRootToken());
		
		criteria = new LogicCriteriaImpl("BIRTHDATE").before(cal.getTime());
		Assertions.assertEquals("BIRTHDATE", criteria.getRootToken());
		
		criteria = new LogicCriteriaImpl("BIRTHDATE").within(Duration.years(5));
		Assertions.assertEquals("BIRTHDATE", criteria.getRootToken());
		
		criteria = new LogicCriteriaImpl("PROBLEM ADDED").contains("HUMAN IMMUNODEFICIENCY VIRUS").first();
		Assertions.assertEquals("PROBLEM ADDED", criteria.getRootToken());
		
		criteria = new LogicCriteriaImpl("PROBLEM ADDED").contains("HIV INFECTED").first();
		Assertions.assertEquals("PROBLEM ADDED", criteria.getRootToken());
		
		criteria = new LogicCriteriaImpl("PROBLEM ADDED").contains("ASYMPTOMATIC HIV INFECTION").first();
		Assertions.assertEquals("PROBLEM ADDED", criteria.getRootToken());
		
		criteria = new LogicCriteriaImpl("HIV VIRAL LOAD").first();
		Assertions.assertEquals("HIV VIRAL LOAD", criteria.getRootToken());
		
		criteria = new LogicCriteriaImpl("HIV VIRAL LOAD, QUALITATIVE").first();
		Assertions.assertEquals("HIV VIRAL LOAD, QUALITATIVE", criteria.getRootToken());
		
		criteria = new LogicCriteriaImpl("CD4 COUNT").lt(200).first();
		Assertions.assertEquals("CD4 COUNT", criteria.getRootToken());
		
		criteria = new LogicCriteriaImpl("${OBS::CD4 COUNT}").first();
		Assertions.assertEquals("${OBS::CD4 COUNT}", criteria.getRootToken());
		
		criteria = new LogicCriteriaImpl("${PERSON::BIRTHDATE}");
		Assertions.assertEquals("${PERSON::BIRTHDATE}", criteria.getRootToken());
		
		criteria = new LogicCriteriaImpl("CD4 COUNT").last().lt(350);
		
		Assertions.assertEquals("CD4 COUNT", criteria.getRootToken());
		
		criteria = new LogicCriteriaImpl("CD4 COUNT").last().lt(350).and(
		    new LogicCriteriaImpl("%%orders.ACTIVE MEDS").notExists());
		Assertions.assertEquals("CD4 COUNT", criteria.getRootToken());
		criteria = new LogicCriteriaImpl("CD4 COUNT").last().before(cal.getTime());
		Assertions.assertEquals("CD4 COUNT", criteria.getRootToken());
		criteria = new LogicCriteriaImpl("CD4 COUNT").not().lt(150);
		Assertions.assertEquals("CD4 COUNT", criteria.getRootToken());
		criteria = new LogicCriteriaImpl("CD4 COUNT").not().not().lt(150);
		
		Assertions.assertEquals("CD4 COUNT", criteria.getRootToken());
	}
}
