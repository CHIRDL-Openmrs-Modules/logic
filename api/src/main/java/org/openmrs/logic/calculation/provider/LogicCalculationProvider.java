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
package org.openmrs.logic.calculation.provider;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.Calculation;
import org.openmrs.calculation.CalculationProvider;
import org.openmrs.calculation.InvalidCalculationException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.calculation.LogicCalculation;
import org.openmrs.logic.token.TokenService;

/**
 * {@link CalculationProvider} that exposes Logic Rules as Calculations
 */
public class LogicCalculationProvider implements CalculationProvider {
	
	/**
	 * @see org.openmrs.calculation.provider.CalculationProvider#getCalculation(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public Calculation getCalculation(String ruleProviderClassName, String ruleConfig) throws InvalidCalculationException {
		Rule rule = Context.getService(TokenService.class).getRule(ruleProviderClassName, ruleConfig);
		if (rule == null)
			return null;
		LogicCalculation logicCalculation = new LogicCalculation(rule);
		
		//in case this rule has an associated registered token, set it on the wrapper
		if (StringUtils.isNotBlank(ruleConfig))
			logicCalculation.setConfiguration(ruleConfig);
		return logicCalculation;
	}
}
