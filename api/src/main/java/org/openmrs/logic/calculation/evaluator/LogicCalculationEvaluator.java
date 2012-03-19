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
package org.openmrs.logic.calculation.evaluator;

import java.util.Map;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.api.patient.PatientCalculationContext;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.calculation.patient.PatientCalculationEvaluator;
import org.openmrs.calculation.result.CohortResult;
import org.openmrs.calculation.result.EmptyResult;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.calculation.LogicCalculation;
import org.openmrs.logic.impl.LogicContextImpl;
import org.openmrs.logic.impl.LogicCriteriaImpl;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.token.TokenRegistration;
import org.openmrs.logic.util.LogicUtil;

/**
 * Evaluator for {@link LogicCalculation}
 */
@Handler(supports = LogicCalculation.class, order = 50)
public class LogicCalculationEvaluator implements PatientCalculationEvaluator {
	
	/**
	 * @see org.openmrs.calculation.patient.PatientCalculationEvaluator#evaluate(org.openmrs.Cohort,
	 *      org.openmrs.calculation.patient.PatientCalculation, java.util.Map,
	 *      org.openmrs.calculation.api.patient.PatientCalculationContext)
	 */
	@Override
	public CohortResult evaluate(Cohort cohort, PatientCalculation calculation, Map<String, Object> parameterValues,
	                             PatientCalculationContext calculationContext) {
		LogicCalculation logicCalculation = (LogicCalculation) calculation;
		CohortResult results = new CohortResult();
		if (cohort != null) {
			TokenRegistration token = logicCalculation.getTokenRegistration();
			for (Integer patientId : cohort.getMemberIds()) {
				Result logicResult = null;
				if (token != null) {
					logicResult = Context.getLogicService().eval(patientId,
					    new LogicCriteriaImpl(logicCalculation.getTokenRegistration().getToken()), parameterValues);
				} else {
					LogicContext logicContext = new LogicContextImpl(patientId);
					if (calculationContext.getNow() != null)
						logicContext.setIndexDate(calculationContext.getNow());
					logicResult = logicCalculation.getRule().eval(logicContext, patientId, parameterValues);
				}
				
				if (logicResult == null || logicResult.isNull()) {
					results.put(patientId, new EmptyResult());
				} else {
					results.put(patientId,
					    LogicUtil.convertToCalculationResult(logicResult, calculation, calculationContext));
				}
			}
		}
		
		return results;
	}
}
