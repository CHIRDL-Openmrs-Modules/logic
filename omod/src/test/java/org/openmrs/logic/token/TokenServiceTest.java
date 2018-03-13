package org.openmrs.logic.token;


import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.context.Context;
import org.openmrs.logic.rule.AgeRule;
import org.openmrs.logic.rule.provider.ClassRuleProvider;
import org.openmrs.logic.rule.provider.RuleProvider;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.openmrs.test.Verifies;

public class TokenServiceTest extends BaseModuleContextSensitiveTest {
	
	private TokenService service;
	
	@Before
	public void before() {
		service = Context.getService(TokenService.class);
	}
	
	/**
	 * @see {@link TokenService#registerToken(String,RuleProvider,String)}
	 * 
	 */
	@Test
	@Verifies(value = "should register a rule", method = "registerToken(String,RuleProvider,String)")
	public void registerToken_shouldRegisterARule() throws Exception {
		service.registerToken("A token", new ClassRuleProvider(), AgeRule.class.getName());
		Assert.assertNotNull(service.getTokenRegistrationByToken("A token"));
	}
	
	@Test
	@SkipBaseSetup
	public void checkAuthorizationAnnotations() throws Exception {
		Method[] allMethods = TokenService.class.getDeclaredMethods();
		for (Method method : allMethods) {
		    if (Modifier.isPublic(method.getModifiers())) {
		        Authorized authorized = method.getAnnotation(Authorized.class);
		        Assert.assertNotNull("Authorized annotation not found on method " + method.getName(), authorized);
		    }
		}
	}
}