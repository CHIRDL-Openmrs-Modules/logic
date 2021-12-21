package org.openmrs.logic.impl;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.Patient;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.Rule;
import org.openmrs.logic.datasource.ObsDataSource;
import org.openmrs.logic.result.Result;
import org.openmrs.test.SkipBaseSetup;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;

public class LogicServiceImplTest extends BaseModuleContextSensitiveTest {
	
	@BeforeEach
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
	public void parseString_shouldCorrectlyParseExpressionWithOnlyAggregatorAndToken() throws Exception {
		
		ObsDataSource obsDataSource = (ObsDataSource) Context.getLogicService().getLogicDataSource("obs");
		obsDataSource.addKey("WEIGHT (KG)");
		
		LogicCriteria criteria = Context.getLogicService().parse("\"WEIGHT (KG)\"");
		Result result = Context.getLogicService().eval(new Patient(3).getPatientId(), criteria); // CHICA-1151 pass in patientId instead of patient
		Assertions.assertEquals(2, result.size());
		
		LogicCriteria lastCriteria = Context.getLogicService().parse("LAST \"WEIGHT (KG)\"");
		Result lastResult = Context.getLogicService().eval(new Patient(3).getPatientId(), lastCriteria); // CHICA-1151 pass in patientId instead of patient
		Assertions.assertEquals(1, lastResult.size());
		Assertions.assertEquals(70.0d, lastResult.toNumber().doubleValue(), 0);
		
		LogicCriteria firstCriteria = Context.getLogicService().parse("FIRST \"WEIGHT (KG)\"");
		Result firstResult = Context.getLogicService().eval(new Patient(3).getPatientId(), firstCriteria); // CHICA-1151 pass in patientId instead of patient
		Assertions.assertEquals(firstResult.size(), 1);
		Assertions.assertEquals(60.0d, firstResult.toNumber().doubleValue(), 0);
	}
	
	/**
	 * @see {@link LogicServiceImpl#getRule(String)}
	 */
	@Test
	@SkipBaseSetup
	public void getRule_shouldReturnReferenceRuleWhenTheTokenAreAlreadyRegistered() throws Exception {
		LogicService logicService = Context.getLogicService();
		Rule rule = logicService.getRule("CD4 COUNT");
		Assertions.assertNotNull(rule);
	}
	
	/**
	 * @see {@link LogicServiceImpl#getRule(String)}
	 */
	@Test
	@SkipBaseSetup
	public void getRule_shouldReturnNewReferenceRuleWhenTheSpecialStringTokenArePassed() throws Exception {
		LogicService logicService = Context.getLogicService();
		Rule rule = logicService.getRule("%%obs.CD4 COUNT");
		Assertions.assertNotNull(rule);
	}
	
	@Test
	@SkipBaseSetup
	public void checkAuthorizationAnnotations() throws Exception {
		Method[] allMethods = LogicService.class.getDeclaredMethods();
		for (Method method : allMethods) {
		    if (Modifier.isPublic(method.getModifiers())) {
		        Authorized authorized = method.getAnnotation(Authorized.class);
		        Assertions.assertNotNull(authorized, "Authorized annotation not found on method " + method.getName());
		    }
		}
	}
	
}
