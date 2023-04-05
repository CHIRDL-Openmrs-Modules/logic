package org.openmrs.logic.token;


import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.context.Context;
import org.openmrs.logic.rule.AgeRule;
import org.openmrs.logic.rule.provider.ClassRuleProvider;
import org.openmrs.logic.rule.provider.RuleProvider;
import org.openmrs.test.SkipBaseSetup;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;

public class TokenServiceTest extends BaseModuleContextSensitiveTest {
	
	private TokenService service;
	
	@BeforeEach
	public void before() {
		this.service = Context.getService(TokenService.class);
	}
	
	/**
	 * @see {@link TokenService#registerToken(String,RuleProvider,String)}
	 * 
	 */
	@Test
	public void registerToken_shouldRegisterARule() throws Exception {
		this.service.registerToken("A token", new ClassRuleProvider(), AgeRule.class.getName());
		Assertions.assertNotNull(this.service.getTokenRegistrationByToken("A token"));
	}
	
	@Test
	@SkipBaseSetup
	public void checkAuthorizationAnnotations() throws Exception {
		Method[] allMethods = TokenService.class.getDeclaredMethods();
		for (Method method : allMethods) {
		    if (Modifier.isPublic(method.getModifiers())) {
		        Authorized authorized = method.getAnnotation(Authorized.class);
		        Assertions.assertNotNull(authorized, "Authorized annotation not found on method " + method.getName());
		    }
		}
	}
}