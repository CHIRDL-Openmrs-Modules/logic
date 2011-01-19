package org.openmrs.logic.rule;


import org.junit.Assert;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.rule.provider.ClassRuleProvider;
import org.openmrs.logic.token.TokenService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

public class FullNameRuleTest extends BaseModuleContextSensitiveTest {
	
	/**
	 * @see {@link FullNameRule#eval(LogicContext,Integer,Map<QString;QObject;>)}
	 * 
	 */
	@Test
	@Verifies(value = "should return patient full name", method = "eval(LogicContext,Integer,Map<QString;QObject;>)")
	public void eval_shouldReturnPatientFullName() throws Exception {
		Context.getService(TokenService.class).registerToken("fullname", new ClassRuleProvider(), FullNameRule.class.getName());
		Result result = Context.getLogicService().eval(7, Context.getLogicService().parse("fullname"), null);
		Assert.assertEquals("Collet Test Chebaskwony", result.toString());
	}
}