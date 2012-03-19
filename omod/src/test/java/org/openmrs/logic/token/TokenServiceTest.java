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
package org.openmrs.logic.token;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.logic.Rule;
import org.openmrs.logic.rule.AgeRule;
import org.openmrs.logic.rule.provider.ClassRuleProvider;
import org.openmrs.logic.rule.provider.RuleProvider;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

public class TokenServiceTest extends BaseModuleContextSensitiveTest {
	
	private TokenService service;
	
	@Before
	public void before() {
		service = Context.getService(TokenService.class);
	}
	
	/**
	 * @see {@link TokenService#registerToken(String,RuleProvider,String)}
	 */
	@Test
	@Verifies(value = "should register a rule", method = "registerToken(String,RuleProvider,String)")
	public void registerToken_shouldRegisterARule() throws Exception {
		service.registerToken("A token", new ClassRuleProvider(), AgeRule.class.getName());
		Assert.assertNotNull(service.getTokenRegistrationByToken("A token"));
	}
	
	/**
	 * @see {@link TokenService#getRule(String,String)}
	 */
	@Test
	@Verifies(value = "should get the rule matching the parameter values", method = "getRule(String,String)")
	public void getRule_shouldGetTheRuleMatchingTheParameterValues() throws Exception {
		Rule rule = service.getRule("org.openmrs.logic.rule.provider.ClassRuleProvider", "org.openmrs.logic.rule.AgeRule");
		Assert.assertNotNull(rule);
		Assert.assertEquals(AgeRule.class, rule.getClass());
	}
}
