package org.openmrs.logic.datasource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.impl.LogicContextImpl;
import org.openmrs.logic.impl.LogicCriteriaImpl;
import org.openmrs.logic.result.Result;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

public class PatientDataSourceTest extends BaseModuleContextSensitiveTest {
	
	@Before
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
	@Verifies(value = "should read identifier", method = "read(LogicContext,Cohort,LogicCriteria)")
	public void read_shouldReadIdentifier() throws Exception {
		LogicDataSource lds = Context.getLogicService().getLogicDataSource("patient");
		Patient who = Context.getPatientService().getPatient(4);
		
		LogicContext context = new LogicContextImpl(who.getPatientId());
		LogicCriteria criteria = new LogicCriteriaImpl("IDENTIFIER").equalTo("123456");
		Result result = context.read(who.getPatientId(), lds, criteria);
		Assert.assertEquals("123456", result.toString());
		
		who = Context.getPatientService().getPatient(2);
		context = new LogicContextImpl(who.getPatientId());
		result = context.read(who.getPatientId(), lds, criteria);
		Assert.assertTrue(result.isEmpty());
	}
}
