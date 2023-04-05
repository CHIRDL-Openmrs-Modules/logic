package org.openmrs.logic.rule;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openmrs.api.context.Context;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.rule.provider.ClassRuleProvider;
import org.openmrs.logic.token.TokenService;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;

public class FullNameRuleTest extends BaseModuleContextSensitiveTest {
	
	/**
	 * @see {@link FullNameRule#eval(LogicContext,Integer,Map<QString;QObject;>)}
	 * 
	 */
	@Test
	public void eval_shouldReturnPatientFullName() throws Exception {
		Context.getService(TokenService.class).registerToken("fullname", new ClassRuleProvider(), FullNameRule.class.getName());
		Result result = Context.getLogicService().eval(7, Context.getLogicService().parse("fullname"), null);
		Assertions.assertEquals("Collet Test Chebaskwony", result.toString());
	}
}