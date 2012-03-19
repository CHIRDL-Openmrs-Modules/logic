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

import org.openmrs.api.context.Context;
import org.openmrs.calculation.BaseCalculation;
import org.openmrs.calculation.ConfigurableCalculation;
import org.openmrs.calculation.InvalidCalculationException;
import org.openmrs.calculation.definition.SimpleParameterDefinition;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.logic.Rule;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.logic.token.TokenRegistration;
import org.openmrs.logic.token.TokenService;

/**
 * Wrapper Patient Calculation class for a logic Rule
 */
public class LogicCalculation extends BaseCalculation implements PatientCalculation, ConfigurableCalculation {
	
	private Rule rule;
	
	private TokenRegistration tokenRegistration;
	
	/**
	 * Convenience constructor
	 * 
	 * @param rule
	 */
	public LogicCalculation(Rule rule) {
		this.rule = rule;
		if (rule.getParameterList() != null) {
			for (RuleParameterInfo parameter : rule.getParameterList()) {
				addParameterDefinition(new SimpleParameterDefinition(null, parameter.getParameterClass().getName(), null,
				        parameter.isRquired()));
			}
		}
	}
	
	/**
	 * @return the rule
	 */
	public Rule getRule() {
		return rule;
	}
	
	/**
	 * @param rule the rule to set
	 */
	public void setRule(Rule rule) {
		this.rule = rule;
	}
	
	/**
	 * @return the tokenRegistration
	 */
	public TokenRegistration getTokenRegistration() {
		return tokenRegistration;
	}
	
	/**
	 * @param tokenRegistration the tokenRegistration to set
	 */
	public void setTokenRegistration(TokenRegistration tokenRegistration) {
		this.tokenRegistration = tokenRegistration;
	}
	
	/**
	 * Looks up the registered token for the associated rule and sets it on this instance
	 * 
	 * @see org.openmrs.calculation.ConfigurableCalculation#setConfiguration(java.lang.String)
	 */
	@Override
	public void setConfiguration(String configuration) throws InvalidCalculationException {
		setTokenRegistration(Context.getService(TokenService.class).getTokenRegistrationByToken(configuration));
	}
}
