package org.openmrs.logic;

import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.impl.ArdenLanguageHandler;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;
import org.openmrs.test.Verifies;
import org.openmrs.util.OpenmrsUtil;

public class CompilingClassLoaderTest extends BaseContextSensitiveTest {
	
	@Before
	public void prepareData() throws Exception {
		initializeInMemoryDatabase();
		executeDataSet("org/openmrs/logic/include/ConceptDerivedTest.xml");
		executeDataSet("org/openmrs/logic/include/GlobalPropertiesTest.xml");
		authenticate();
	}
	
	/**
	 * @see {@link CompilingClassLoader#loadClass(String,boolean)}
	 */
	@SkipBaseSetup
	@Verifies(value = "should compile and load java file at runtime", method = "loadClass(String,boolean)")
	public void loadClass_shouldCompileAndLoadJavaFileAtRuntime() throws Exception {
		
		LogicRule logicRule = Context.getService(LogicRuleService.class).getLogicRule(1);
		Assert.assertNotNull(logicRule);
		Assert.assertEquals("Arden", logicRule.getLanguage());
		
		AdministrationService adminService = Context.getAdministrationService();
		String ruleJavaDir = adminService.getGlobalProperty(LogicConstants.RULE_DEFAULT_SOURCE_FOLDER);
		File ruleSourceDir = OpenmrsUtil.getDirectoryInApplicationDataDirectory(ruleJavaDir);
		
		ArdenLanguageHandler h = new ArdenLanguageHandler();
		h.handle(logicRule);
		
		CompilingClassLoader loader = new CompilingClassLoader();
		
		File[] files = ruleSourceDir.listFiles();
		Assert.assertTrue(files.length > 0);
		
		String javaFilename = logicRule.getClassName();
		Class<?> c = loader.loadClass(javaFilename);
		Object object = c.newInstance();
		Assert.assertNotNull(object);
		Assert.assertTrue(Rule.class.isAssignableFrom(object.getClass()));
	}
}
