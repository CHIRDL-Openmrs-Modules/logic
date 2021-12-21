package org.openmrs.logic.datasource;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.impl.LogicContextImpl;
import org.openmrs.logic.impl.LogicCriteriaImpl;
import org.openmrs.logic.result.Result;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;

public class PatientDataSourceTest extends BaseModuleContextSensitiveTest {
	
	@BeforeEach
	public void prepareData() throws Exception {
		initializeInMemoryDatabase();
		executeDataSet("org/openmrs/logic/include/LogicTests-patients.xml");
		executeDataSet("org/openmrs/logic/include/LogicBasicTest.concepts.xml");
		authenticate();
	}
	
	/**
	 * @see {@link PatientDataSource#read(LogicContext,Cohort,LogicCriteria)}
	 */
	@Test
	public void read_shouldReadIdentifier() throws Exception {
		LogicDataSource lds = Context.getLogicService().getLogicDataSource("patient");
		Patient who = Context.getPatientService().getPatient(4);
		
		LogicContext context = new LogicContextImpl(who.getPatientId());
		LogicCriteria criteria = new LogicCriteriaImpl("IDENTIFIER").equalTo("123456");
		Result result = context.read(who.getPatientId(), lds, criteria);
		Assertions.assertEquals("123456", result.toString());
		
		who = Context.getPatientService().getPatient(2);
		context = new LogicContextImpl(who.getPatientId());
		result = context.read(who.getPatientId(), lds, criteria);
		Assertions.assertTrue(result.isEmpty());
	}
}
