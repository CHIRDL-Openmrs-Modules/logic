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
package org.openmrs.logic.calculation;

import junit.framework.Assert;

import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.api.patient.PatientCalculationService;
import org.openmrs.calculation.result.Result;
import org.openmrs.logic.Rule;
import org.openmrs.logic.calculation.provider.LogicCalculationProvider;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Contains behavior tests for exposing logic rules as calculations
 */
public class CalculationBehaviorTest extends BaseModuleContextSensitiveTest {
	
	/**
	 * @see {@link LogicCalculation#LogicCalculation(Rule)}
	 */
	@Test
	public void shouldEvaluateTheAgeRuleAsACalculation() throws Exception {
		int patientId = 2;
		int expected = Context.getPatientService().getPatient(patientId).getAge();
		LogicCalculation calculation = (LogicCalculation) new LogicCalculationProvider().getCalculation(
		    "org.openmrs.logic.rule.provider.ClassRuleProvider", "org.openmrs.logic.rule.AgeRule");
		Result result = Context.getService(PatientCalculationService.class).evaluate(patientId, calculation);
		Assert.assertEquals(expected, result.asType(Double.class).intValue());
	}
}
