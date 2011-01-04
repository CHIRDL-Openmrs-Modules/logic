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
package org.openmrs.logic.rule.definition;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


/**
 * Validator for {@link RuleDefinition}
 */
public class RuleDefinitionValidator implements Validator {
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean supports(Class c) {
		return c.equals(RuleDefinition.class);
	}
	
	@Override
	public void validate(Object obj, Errors errors) {
		RuleDefinition rule = (RuleDefinition) obj;
		if (StringUtils.isEmpty(rule.getName()))
			errors.rejectValue("name", "error.null");
		if (StringUtils.isEmpty(rule.getLanguage()))
			errors.rejectValue("language", "error.null");
		if (StringUtils.isEmpty(rule.getRuleContent()))
			errors.rejectValue("ruleContent", "error.null");
	}
	
}
