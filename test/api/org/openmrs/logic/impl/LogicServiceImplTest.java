package org.openmrs.logic.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.Rule;
import org.openmrs.logic.datasource.ObsDataSource;
import org.openmrs.logic.result.Result;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.openmrs.test.Verifies;

public class LogicServiceImplTest extends BaseModuleContextSensitiveTest {
	
	@Before
	public void prepareData() throws Exception {
		initializeInMemoryDatabase();
		executeDataSet("org/openmrs/logic/include/LogicTests-patients.xml");
		executeDataSet("org/openmrs/logic/include/LogicBasicTest.concepts.xml");
		executeDataSet("org/openmrs/logic/include/RuleDefinitionTest.xml");
		executeDataSet("org/openmrs/logic/include/GlobalPropertiesTest.xml");
		executeDataSet("org/openmrs/logic/include/LogicStandardDatasets.xml");
		authenticate();	
	}
	
	/**
	 * @see {@link LogicServiceImpl#parse(String)}
	 */
	@Test
	@SkipBaseSetup
	@Verifies(value = "should correctly parse expression with only aggregator and token", method = "parse(String)")
	public void parseString_shouldCorrectlyParseExpressionWithOnlyAggregatorAndToken() throws Exception {
		
		ObsDataSource obsDataSource = (ObsDataSource) Context.getLogicService().getLogicDataSource("obs");
		obsDataSource.addKey("WEIGHT (KG)");
		
		LogicCriteria criteria = Context.getLogicService().parse("\"WEIGHT (KG)\"");
		Result result = Context.getLogicService().eval(new Patient(3), criteria);
		Assert.assertEquals(2, result.size());
		
		LogicCriteria lastCriteria = Context.getLogicService().parse("LAST \"WEIGHT (KG)\"");
		Result lastResult = Context.getLogicService().eval(new Patient(3), lastCriteria);
		Assert.assertEquals(1, lastResult.size());
		Assert.assertEquals(70.0d, lastResult.toNumber().doubleValue(), 0);
		
		LogicCriteria firstCriteria = Context.getLogicService().parse("FIRST \"WEIGHT (KG)\"");
		Result firstResult = Context.getLogicService().eval(new Patient(3), firstCriteria);
		Assert.assertEquals(firstResult.size(), 1);
		Assert.assertEquals(60.0d, firstResult.toNumber().doubleValue(), 0);
	}
	
	/**
	 * @see {@link LogicServiceImpl#getRule(String)}
	 */
	@Test
	@SkipBaseSetup
	@Verifies(value = "should return ReferenceRule when the token are already registered", method = "getRule(String)")
	public void getRule_shouldReturnReferenceRuleWhenTheTokenAreAlreadyRegistered() throws Exception {
		LogicService logicService = Context.getLogicService();
		Rule rule = logicService.getRule("CD4 COUNT");
		Assert.assertNotNull(rule);
	}
	
	/**
	 * @see {@link LogicServiceImpl#getRule(String)}
	 */
	@Test
	@SkipBaseSetup
	@Verifies(value = "should return new ReferenceRule when the special string token are passed", method = "getRule(String)")
	public void getRule_shouldReturnNewReferenceRuleWhenTheSpecialStringTokenArePassed() throws Exception {
		LogicService logicService = Context.getLogicService();
		Rule rule = logicService.getRule("%%obs.CD4 COUNT");
		Assert.assertNotNull(rule);
	}
	
}
