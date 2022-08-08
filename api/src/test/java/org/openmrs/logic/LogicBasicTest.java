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

import java.util.ArrayList;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.logic.impl.LogicCriteriaImpl;
import org.openmrs.logic.result.Result;
import org.openmrs.test.SkipBaseSetup;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;

/**
 * TODO add more tests
 */
@SkipBaseSetup
public class LogicBasicTest extends BaseModuleContextSensitiveTest {
	
	/**
	 * Runs the basic stuff since we have SkipBaseSetup on the whole class
	 * 
	 * @throws Exception
	 */
	@BeforeEach
	public void runBeforeEachTest() throws Exception {
		initializeInMemoryDatabase();
		executeDataSet("org/openmrs/logic/include/LogicStandardDatasets.xml");
		executeDataSet("org/openmrs/logic/include/LogicTests-patients.xml");
		authenticate();
	}
	
	/**
	 * TODO comment on this method
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldCheckWhetherRecentResultsExist() throws Exception {
		executeDataSet("org/openmrs/logic/include/LogicBasicTest.concepts.xml");
		
		// Result = NO CD4 COUNT IN LAST 6 MONTHS
		Patient patient = Context.getPatientService().getPatient(2);
		Result result = Context.getLogicService().eval(patient.getPatientId(),
		    new LogicCriteriaImpl("CD4 COUNT").within(Duration.months(6)).exists()); // CHICA-1151 pass in patientId instead of patient
		
		Assertions.assertFalse(result.exists());
	}
	
	/**
	 * This test looks for "LAST CD4 COUNT < 350".
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldFilterByNumericResult() throws Exception {
		executeDataSet("org/openmrs/logic/include/LogicBasicTest.concepts.xml");
		// Result = LAST CD4 COUNT < 350
		Patient patient = Context.getPatientService().getPatient(3);
		Result result = Context.getLogicService().eval(patient.getPatientId(), new LogicCriteriaImpl("CD4 COUNT").last().lt(350)); // CHICA-1151 pass in patientId instead of patient
		Assertions.assertTrue(result.exists());
		Assertions.assertEquals(100.0, result.toNumber(), 0);
	}
	
	/**
	 * This test looks for "LAST CD4 COUNT < 350". The catch is that the last cd4 count for patient
	 * #2 is voided
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldFilterByNumericResultWithVoidedObs() throws Exception {
		executeDataSet("org/openmrs/logic/include/LogicBasicTest.concepts.xml");
		// Result = LAST CD4 COUNT < 350
		Patient patient = Context.getPatientService().getPatient(3);
		Result result = Context.getLogicService().eval(patient.getPatientId(), new LogicCriteriaImpl("CD4 COUNT").last().lt(350)); // CHICA-1151 pass in patientId instead of patient
		Assertions.assertTrue(result.exists(), "A result should exist");
		Assertions.assertEquals(100.0, result.toNumber().doubleValue(), 0);
	}
	
	/**
	 * TODO fix this test. This needs to be renamed for a better description of what its testing
	 * TODO result.exists() returns false right now.
	 * 
	 * @throws Exception
	 */
	@Test
	public void shouldFetchActiveMedications() throws Exception {
		executeDataSet("org/openmrs/logic/include/LogicBasicTest.concepts.xml");
		Patient patient = Context.getPatientService().getPatient(2);
		Result result = Context.getLogicService().eval(patient.getPatientId(),
		    new LogicCriteriaImpl("CURRENT ANTIRETROVIRAL DRUGS USED FOR TREATMENT")); // CHICA-1151 pass in patientId instead of patient
		Assertions.assertTrue(result.exists());
	}
	
	/**
	 * This test is invalid until an OrderDataSource is written
	 * 
	 * @throws Exception
	 */
	@Test
	@Disabled
	//until we have an OrderDataSource
	public void shouldFilterUsingComposition() throws Exception {
		executeDataSet("org/openmrs/logic/include/LogicBasicTest.concepts.xml");
		// LAST CD4 COUNT < 350 AND NO ACTIVE MEDICATIONS
		Patient patient = Context.getPatientService().getPatient(2);
		Result result = Context.getLogicService()
		        .eval(
		            patient.getPatientId(),
		            new LogicCriteriaImpl("CD4 COUNT").last().lt(350).and(
		                new LogicCriteriaImpl("%%orders.ACTIVE MEDS").notExists())); // CHICA-1151 pass in patientId instead of patient
		Assertions.assertTrue(result.exists());
	}
	
	/**
	 * TODO comment on this method
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void shouldSimpleLogic() throws Exception {
		executeDataSet("org/openmrs/logic/include/LogicBasicTest.concepts.xml");
		
		// Patient p = Context.getPatientService().getPatient(2);
		Cohort cohort = new Cohort();
		ArrayList<Integer> ids = new java.util.ArrayList<Integer>(); // (Context.getPatientSetService().getAllPatients().getMemberIds()); // CHICA-1151 This service no longer exists. Removed the Test annotation
		for (int i = 1; i < ids.size(); i++) {
			cohort.addMember(ids.get(i));
		}
		cohort.addMember(2);
		//long l = System.currentTimeMillis();
		//System.out.println(new Date());
		LogicService ls = Context.getLogicService();
		Map<Integer, Result> m = ls.eval(cohort, "\"WEIGHT (KG)\"");
		Assertions.assertNotNull(m);
		Assertions.assertTrue(m.size() > 0);
		//System.out.println(m.toString());
		//System.out.println(String.valueOf(System.currentTimeMillis() - l) + " milliseconds");
		
	}
}
