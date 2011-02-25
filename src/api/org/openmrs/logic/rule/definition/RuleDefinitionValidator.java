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

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;


/**
 * Validator for {@link RuleDefinition}
 */
public class RuleDefinitionValidator implements Validator {
	
	@SuppressWarnings("unchecked")
	public boolean supports(Class c) {
		return c.equals(RuleDefinition.class);
	}
	
	public void validate(Object obj, Errors errors) {
		RuleDefinition rule = (RuleDefinition) obj;

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "error.null");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "language", "error.null");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "ruleContent", "error.null");

		if (StringUtils.isNotBlank(rule.getLanguage()) && StringUtils.isNotBlank(rule.getRuleContent())) {
			RuleDefinitionService service = Context.getService(RuleDefinitionService.class);
			LanguageHandler handler = service.getLanguageHandler(rule.getLanguage());
			Date originalDateChanged = rule.getDateChanged();
			try {
				rule.setDateChanged(new Date());
				Rule r = handler.compile(rule);
				if (r == null)
					throw new LogicException("Unknown error compiling rule");
			} catch (Exception ex) {
				while (ex.getCause() != null && ex.getCause() != ex && ex.getCause() instanceof Exception)
					ex = (Exception) ex.getCause();
				errors.rejectValue("ruleContent", ex.getMessage());
			} finally {
				rule.setDateChanged(originalDateChanged);
			}
		}
	}
	
}
