package org.openmrs.logic.impl;

import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.ConceptDerived;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicConstants;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.Rule;
import org.openmrs.logic.datasource.ObsDataSource;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.rule.ReferenceRule;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.openmrs.test.Verifies;
import org.openmrs.util.OpenmrsUtil;

public class LogicServiceImplTest extends BaseModuleContextSensitiveTest {
	
	@Before
	public void prepareData() throws Exception {
		initializeInMemoryDatabase();
		executeDataSet("org/openmrs/logic/include/LogicTests-patients.xml");
		executeDataSet("org/openmrs/logic/include/LogicBasicTest.concepts.xml");
		executeDataSet("org/openmrs/logic/include/ConceptDerivedTest.xml");
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
		Context.getLogicService().updateRule("WEIGHT (KG)", new ReferenceRule("obs.WEIGHT (KG)"));
		
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
	
	/**
	 * @see {@link LogicServiceImpl#getRule(String)}
	 */
	@Test
	@SkipBaseSetup
	@Verifies(value = "should return Rule when concept derived name are passed", method = "getRule(String)")
	public void getRule_shouldReturnRuleWhenConceptDerivedNameArePassed() throws Exception {
		// delete the class and java file in the default rule class output folder
		AdministrationService adminService = Context.getAdministrationService();
		String ruleJavaDir = adminService.getGlobalProperty("logic.default.ruleJavaDirectory");
		String ruleClassDir = adminService.getGlobalProperty("logic.default.ruleClassDirectory");
		
		ConceptDerived conceptDerived = Context.getConceptService().getConceptDerived(111);
		String javaFilename = conceptDerived.getClassName().replace('.', File.separatorChar) + ".java";
		String classFilename = conceptDerived.getClassName().replace('.', File.separatorChar) + ".class";
		
		File javaFile = new File(OpenmrsUtil.getDirectoryInApplicationDataDirectory(ruleJavaDir), javaFilename);
		if (javaFile.exists())
			Assert.assertTrue(javaFile.delete());
		File classFile = new File(OpenmrsUtil.getDirectoryInApplicationDataDirectory(ruleClassDir), classFilename);
		if (classFile.exists())
			Assert.assertTrue(classFile.delete());
		
		Assert.assertFalse(javaFile.exists());
		Assert.assertFalse(classFile.exists());
		
		LogicService logicService = Context.getLogicService();
		Rule rule = logicService.getRule("PREGNANT WOMAN");
		Assert.assertNotNull(rule);
		Assert.assertEquals("org.openmrs.logic.rule.BaselineHgbReminder", rule.getClass().getName());
		Assert.assertFalse(ReferenceRule.class.isAssignableFrom(rule.getClass()));
	}
	
	/**
	 * @see {@link LogicServiceImpl#getRule(String)}
	 */
	@Test
	@SkipBaseSetup
	@Verifies(value = "should return Rule when registered concept derived name are passed", method = "getRule(String)")
	public void getRule_shouldReturnRuleWhenRegisteredConceptDerivedNameArePassed() throws Exception {
		// delete the class file in the default rule class output folder
		AdministrationService adminService = Context.getAdministrationService();
		String ruleJavaDir = adminService.getGlobalProperty(LogicConstants.RULE_DEFAULT_SOURCE_FOLDER);
		String ruleClassDir = adminService.getGlobalProperty(LogicConstants.RULE_DEFAULT_CLASS_FOLDER);
		
		ConceptDerived conceptDerived = Context.getConceptService().getConceptDerived(111);
		String javaFilename = conceptDerived.getClassName().replace('.', File.separatorChar) + ".java";
		String classFilename = conceptDerived.getClassName().replace('.', File.separatorChar) + ".class";
		
		File oldJavaFile = new File(OpenmrsUtil.getDirectoryInApplicationDataDirectory(ruleJavaDir), javaFilename);
		if (oldJavaFile.exists())
			Assert.assertTrue(oldJavaFile.delete());
		File oldClassFile = new File(OpenmrsUtil.getDirectoryInApplicationDataDirectory(ruleClassDir), classFilename);
		if (oldClassFile.exists())
			Assert.assertTrue(oldClassFile.delete());
		
		Assert.assertFalse(oldJavaFile.exists());
		Assert.assertFalse(oldClassFile.exists());
		
		LogicService logicService = Context.getLogicService();
		Rule rule = logicService.getRule("PREGNANT WOMAN");
		Assert.assertNotNull(rule);
		Assert.assertEquals("org.openmrs.logic.rule.BaselineHgbReminder", rule.getClass().getName());
		Assert.assertFalse(ReferenceRule.class.isAssignableFrom(rule.getClass()));
		
		// make sure the java file is exists and gets created
		File javaFile = new File(OpenmrsUtil.getDirectoryInApplicationDataDirectory(ruleJavaDir), javaFilename);
		Assert.assertTrue(javaFile.exists());
		File classFile = new File(OpenmrsUtil.getDirectoryInApplicationDataDirectory(ruleClassDir), classFilename);
		Assert.assertTrue(classFile.exists());
		
		// this time the rule java and class file should not be re-created
		rule = logicService.getRule("PREGNANT WOMAN");
		Assert.assertNotNull(rule);
		Assert.assertEquals("org.openmrs.logic.rule.BaselineHgbReminder", rule.getClass().getName());
		Assert.assertFalse(ReferenceRule.class.isAssignableFrom(rule.getClass()));
		
		File newJavaFile = new File(OpenmrsUtil.getDirectoryInApplicationDataDirectory(ruleJavaDir), javaFilename);
		Assert.assertEquals(javaFile.lastModified(), newJavaFile.lastModified());
		File newClassFile = new File(OpenmrsUtil.getDirectoryInApplicationDataDirectory(ruleClassDir), classFilename);
		Assert.assertEquals(classFile.lastModified(), newClassFile.lastModified());
	}
	
}
