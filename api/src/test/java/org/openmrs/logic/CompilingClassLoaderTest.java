package org.openmrs.logic;

import java.io.File;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.rule.definition.JavaLanguageHandler;
import org.openmrs.logic.rule.definition.RuleDefinition;
import org.openmrs.logic.rule.definition.RuleDefinitionService;
import org.openmrs.test.SkipBaseSetup;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;
import org.openmrs.util.OpenmrsUtil;

public class CompilingClassLoaderTest extends BaseModuleContextSensitiveTest {
	
	@BeforeEach
	public void prepareData() throws Exception {
		initializeInMemoryDatabase();
		executeDataSet("org/openmrs/logic/include/RuleDefinitionTest.xml");
		executeDataSet("org/openmrs/logic/include/GlobalPropertiesTest.xml");
		authenticate();
	}
	
	/**
	 * @see {@link CompilingClassLoader#loadClass(String,boolean)}
	 */
	@SkipBaseSetup
	@Test
	@Disabled
	public void loadClass_shouldCompileAndLoadJavaFileAtRuntime() throws Exception {
		
		RuleDefinition logicRule = Context.getService(RuleDefinitionService.class).getRuleDefinition(1);
		Assertions.assertNotNull(logicRule);
		Assertions.assertEquals("Java", logicRule.getLanguage());
		
		AdministrationService adminService = Context.getAdministrationService();
		String ruleJavaDir = adminService.getGlobalProperty(LogicConstants.RULE_DEFAULT_SOURCE_FOLDER);
		File ruleSourceDir = OpenmrsUtil.getDirectoryInApplicationDataDirectory(ruleJavaDir);
		
		JavaLanguageHandler h = new JavaLanguageHandler();
		h.compile(logicRule);
		
		CompilingClassLoader loader = new CompilingClassLoader();
		
		File[] files = ruleSourceDir.listFiles();
		Assertions.assertTrue(files.length > 0);
		
		String javaFilename = h.getClassName(logicRule);
		Class<?> c = loader.loadClass(javaFilename);
		Object object = c.newInstance();
		Assertions.assertNotNull(object);
		Assertions.assertTrue(Rule.class.isAssignableFrom(object.getClass()));
	}
}
