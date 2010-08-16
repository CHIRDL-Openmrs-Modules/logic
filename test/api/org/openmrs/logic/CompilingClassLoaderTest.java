package org.openmrs.logic;

import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.openmrs.ConceptDerived;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.arden.ArdenService;
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
		
		ConceptDerived derived = Context.getConceptService().getConceptDerived(111);
		Assert.assertNotNull(derived);
		Assert.assertEquals("Arden", derived.getLanguage());
		
		AdministrationService adminService = Context.getAdministrationService();
		String ruleJavaDir = adminService.getGlobalProperty(LogicConstants.RULE_DEFAULT_SOURCE_FOLDER);
		File ruleSourceDir = OpenmrsUtil.getDirectoryInApplicationDataDirectory(ruleJavaDir);
		
		ArdenService ardenService = Context.getArdenService();
		ardenService.compile(derived.getRule(), ruleSourceDir.getAbsolutePath());
		
		CompilingClassLoader loader = new CompilingClassLoader();
		
		File[] files = ruleSourceDir.listFiles();
		Assert.assertTrue(files.length > 0);
		
		String javaFilename = derived.getClassName();
		Class<?> c = loader.loadClass(javaFilename);
		Object object = c.newInstance();
		Assert.assertNotNull(object);
		Assert.assertTrue(Rule.class.isAssignableFrom(object.getClass()));
	}
}
