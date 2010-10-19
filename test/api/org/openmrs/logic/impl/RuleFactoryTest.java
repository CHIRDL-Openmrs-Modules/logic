package org.openmrs.logic.impl;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.Rule;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.openmrs.test.Verifies;

public class RuleFactoryTest extends BaseModuleContextSensitiveTest {
	
	@Before
	public void prepareData() throws Exception {
		initializeInMemoryDatabase();
		executeDataSet("org/openmrs/logic/include/LogicTests-patients.xml");
		executeDataSet("org/openmrs/logic/include/LogicBasicTest.concepts.xml");
		executeDataSet("org/openmrs/logic/include/GlobalPropertiesTest.xml");
		executeDataSet("org/openmrs/logic/include/LogicStandardDatasets.xml");
		authenticate();
	}
	
	/**
	 * @see {@link RuleFactory#getRule(String)}
	 * 
	 */
	@Test
	@SkipBaseSetup
	@Verifies(value = "should load and create rule class from registered concept derived", method = "getRule(String)")
	public void getRule_shouldLoadAndCreateRuleClassFromRegisteredConceptDerived() throws Exception {
		// register the concept derived as a rule
		executeDataSet("org/openmrs/logic/include/ConceptDerivedRuleTest.xml");
		LogicService logicService = Context.getLogicService();
		Rule rule = logicService.getRule("PREGNANT WOMAN");
		Assert.assertNotNull(rule);
	}
	
	/**
	 * @see {@link RuleFactory#getRule(String)}
	 * 
	 */
	@Test
	@Verifies(value = "should load and create rule class from unregistered concept derived", method = "getRule(String)")
	public void getRule_shouldLoadAndCreateRuleClassFromUnregisteredConceptDerived() throws Exception {
		// register the concept derived as a rule
		executeDataSet("org/openmrs/logic/include/ConceptDerivedTest.xml");
		LogicService logicService = Context.getLogicService();
		Rule rule = logicService.getRule("Baseline Hgb Reminder Rule");
		Assert.assertNotNull(rule);
	}

	/**
     * @see {@link RuleFactory#getRule(String)}
     * 
     */
    @Test
    @Verifies(value = "should load and create rule class from plain java from file system", method = "getRule(String)")
    public void getRule_shouldLoadAndCreateRuleClassFromPlainJavaFromFileSystem() throws Exception {
		// register the concept derived as a rule
		executeDataSet("org/openmrs/logic/include/ConceptDerivedRuleTest.xml");
		LogicService logicService = Context.getLogicService();
		// registered rule
		Rule rule = logicService.getRule("PREGNANT MAN");
		Assert.assertNotNull(rule);
    }
}
