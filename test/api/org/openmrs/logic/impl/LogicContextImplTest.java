package org.openmrs.logic.impl;


import java.util.Calendar;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.rule.AbstractRule;
import org.openmrs.logic.rule.provider.ClassRuleProvider;
import org.openmrs.logic.token.TokenService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

public class LogicContextImplTest extends BaseModuleContextSensitiveTest {
	
	@Before
	public void runBeforeEachTest() throws Exception {
		executeDataSet("org/openmrs/logic/include/LogicStandardDatasets.xml");
		executeDataSet("org/openmrs/logic/include/LogicTests-patients.xml");
	}
	
	/**
	 * This test is a bit hacky, and doesn't quite test what it advertises, but it should definitely break
	 * if the behavior changes.
	 * @see {@link LogicContextImpl#eval(Integer,LogicCriteria,Map<String,Object>)}
	 */
	@Test
	@Verifies(value = "should evaluate a rule that requires a new index date in a new logic context", method = "eval(Integer,LogicCriteria,Map<QString;QObject;>)")
	public void eval_shouldEvaluateARuleThatRequiresANewIndexDateInANewLogicContext() throws Exception {
		Rule rule = new AgeAtFirstEncounter();
		Result result = rule.eval(new LogicContextImpl(7), 7, null);
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
	
	/**
	 * @verifies behave right when a rule and a subrule with a different index date evaluate the same criteria
	 * @see LogicContextImpl#eval(Integer, LogicCriteria, Map)
	 */
	@Test
	public void eval_shouldBehaveRightWhenARuleAndASubruleWithADifferentIndexDateEvaluateTheSameCriteria() throws Exception {
		Context.getService(TokenService.class).registerToken("another", new ClassRuleProvider(), RuleThatCallsAge.class.getName());
		Rule rule = new ParentRuleThatCallsAge();
		Result result = rule.eval(new LogicContextImpl(7), 7, null);
		Assert.assertEquals(Double.valueOf(7), result.toNumber());
	}
	
	private class ParentRuleThatCallsAge extends AbstractRule {
		@Override
		public Result eval(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {
			Calendar before = Calendar.getInstance();
			before.add(Calendar.YEAR, -7);
		    Result age = context.eval(patientId, "age");
		    LogicCriteria another = new LogicCriteriaImpl("another");
		    another.asOf(before.getTime());
		    Result ageBefore = context.eval(patientId, another, parameters);
		    return new Result(age.toNumber() - ageBefore.toNumber());
		}
	}
	
}