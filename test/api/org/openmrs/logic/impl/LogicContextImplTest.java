package org.openmrs.logic.impl;


import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.datasource.EncounterDataSource;
import org.openmrs.logic.datasource.PersonDataSource;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.rule.AbstractRule;
import org.openmrs.logic.rule.AgeRule;
import org.openmrs.logic.rule.provider.ClassRuleProvider;
import org.openmrs.logic.token.TokenService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

public class LogicContextImplTest extends BaseModuleContextSensitiveTest {
	
	/**
	 * This test is a bit hacky, and doesn't quite test what it advertises, but it should definitely break
	 * if the behavior changes.
	 * @see {@link LogicContextImpl#eval(Integer,LogicCriteria,Map<String,Object>)}
	 */
	@Test
	@Verifies(value = "should evaluate a rule that requires a new index date in a new logic context", method = "eval(Integer,LogicCriteria,Map<QString;QObject;>)")
	public void eval_shouldEvaluateARuleThatRequiresANewIndexDateInANewLogicContext() throws Exception {
		new PersonDataSource().afterStartup();
		new EncounterDataSource().afterStartup();
		Context.getService(TokenService.class).registerToken("age", new ClassRuleProvider(), AgeRule.class.getName());
		Rule rule = new AgeAtFirstEncounter();
		Result result = rule.eval(new LogicContextImpl(new Patient(7)), 7, null);
		Assert.assertEquals(Double.valueOf(31), result.toNumber());
	}
	
	private class AgeAtFirstEncounter extends AbstractRule {
        @Override
        public Result eval(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {
        	try {
        		LogicCriteria ageCriteria = new LogicCriteriaImpl("age");
        		LogicCriteria firstEncounter = new LogicCriteriaImpl("encounter").first();
        		Result result = context.read(patientId, context.getLogicDataSource("encounter"), firstEncounter);
        		Assert.assertTrue(result.exists());
        		ageCriteria.asOf(result.getResultDate());
        		return context.eval(patientId, ageCriteria, parameters);
        	} catch (Exception ex) {
        		throw new LogicException(ex);
        	}
        }
	}
}