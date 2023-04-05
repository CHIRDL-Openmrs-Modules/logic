package org.openmrs.logic.rule.definition.impl;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openmrs.annotation.Authorized;
import org.openmrs.logic.rule.definition.RuleDefinitionService;
import org.openmrs.test.SkipBaseSetup;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;

public class RuleDefinitionServiceImplTest extends BaseModuleContextSensitiveTest {
	
	@Test
	@SkipBaseSetup
	public void checkAuthorizationAnnotations() throws Exception {
		Method[] allMethods = RuleDefinitionService.class.getDeclaredMethods();
		for (Method method : allMethods) {
		    if (Modifier.isPublic(method.getModifiers())) {
		        Authorized authorized = method.getAnnotation(Authorized.class);
		        Assertions.assertNotNull(authorized, "Authorized annotation not found on method " + method.getName());
		    }
		}
	}
}
