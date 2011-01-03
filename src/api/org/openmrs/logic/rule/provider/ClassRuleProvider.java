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
package org.openmrs.logic.rule.provider;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.StatefulRule;
import org.springframework.stereotype.Component;


/**
 * A generic rule provider that instantiates a Rule from its class name and returns it, possibly
 * applying state.
 */
@Component
public class ClassRuleProvider extends AbstractRuleProvider implements RuleProvider {
	
	/**
	 * Instantiates a rule based on the passed-in parameter
	 * @param ruleConfiguration the rule class name, possibly followed by a space and then the rule state
	 * @return an instantiated rule
	 */
	@Override
	public Rule getRule(String ruleConfiguration) {
		if (ruleConfiguration == null)
			throw new IllegalArgumentException("class name must be provided");

		String ruleClassName;
		String ruleState;
		int firstSpace = ruleConfiguration.indexOf(' ');
		if (firstSpace > 0) {
			ruleClassName = ruleConfiguration.substring(0, firstSpace);
			ruleState = ruleConfiguration.substring(firstSpace + 1);
		} else {
			ruleClassName = ruleConfiguration;
			ruleState = null;
		}

		try {
			Class<?> c = Context.loadClass(ruleClassName);
			Rule rule = (Rule) c.newInstance();
			if (StringUtils.isNotBlank(ruleState)) {
				((StatefulRule) rule).restoreFromString(ruleState);
			}
			return rule;
		} catch (Exception ex) {
			throw new LogicException(ex);
		}
	}
	
}
