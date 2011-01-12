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
import org.openmrs.test.SkipBaseSetup;
import org.openmrs.test.Verifies;

public class PersonDataSourceTest extends BaseModuleContextSensitiveTest {
	
	@Before
	public void prepareData() throws Exception {
		initializeInMemoryDatabase();
		executeDataSet("org/openmrs/logic/include/LogicTests-patients.xml");
		executeDataSet("org/openmrs/logic/include/LogicBasicTest.concepts.xml");
		authenticate();
	}
	
	/**
	 * @verifies {@link PersonDataSource#read(LogicContext,Cohort,LogicCriteria)} test = should get
	 *           birthdate
	 */
	@Test
	@SkipBaseSetup
	public void read_shouldGetBirthdate() throws Exception {
		LogicDataSource lds = Context.getLogicService().getLogicDataSource("person");
		Patient who = Context.getPatientService().getPatient(2);
		LogicContext context = new LogicContextImpl(who);
		
		LogicCriteria criteria = new LogicCriteriaImpl("BIRTHDATE");
		Result result = context.read(who, lds, criteria);
		Assert.assertEquals(Context.getDateFormat().parse("01/01/2000"), result.toDatetime());
	}
	
	/**
	 * @verifies {@link PersonDataSource#read(LogicContext,Cohort,LogicCriteria)} test = should get
	 *           birthdate_estimated
	 */
	@Test
	@SkipBaseSetup
	public void read_shouldGetBirthdate_estimated() throws Exception {
		LogicDataSource lds = Context.getLogicService().getLogicDataSource("person");
		Patient who = Context.getPatientService().getPatient(2);
		LogicContext context = new LogicContextImpl(who);
		
		LogicCriteria criteria = new LogicCriteriaImpl("BIRTHDATE ESTIMATED");
		Result result = context.read(who, lds, criteria);
		Assert.assertEquals(false, result.toBoolean());
	}
	
	/**
	 * @verifies {@link PersonDataSource#read(LogicContext,Cohort,LogicCriteria)} test = should get
	 *           gender
	 */
	@Test
	@SkipBaseSetup
	public void read_shouldGetGender() throws Exception {
		LogicDataSource lds = Context.getLogicService().getLogicDataSource("person");
		Patient who = Context.getPatientService().getPatient(2);
		LogicContext context = new LogicContextImpl(who);
		
		LogicCriteria criteria = new LogicCriteriaImpl("GENDER");
		Result result = context.read(who, lds, criteria);
		Assert.assertEquals("M", result.toString());
	}
	
	/**
	 * @verifies {@link PersonDataSource#read(LogicContext,Cohort,LogicCriteria)} test = should get
	 *           gender equals value
	 */
	@Test
	@SkipBaseSetup
	public void read_shouldGetGenderEqualsValue() throws Exception {
		LogicDataSource lds = Context.getLogicService().getLogicDataSource("person");
		Patient who = Context.getPatientService().getPatient(2);
		LogicContext context = new LogicContextImpl(who);
		
		LogicCriteria criteria = new LogicCriteriaImpl("GENDER").equalTo("M");
		Result result = context.read(who, lds, criteria);
		Assert.assertTrue(result.toBoolean());
		
		criteria = new LogicCriteriaImpl("GENDER").equalTo("F");
		result = context.read(who, lds, criteria);
		Assert.assertFalse(result.toBoolean());
	}
	
	/**
	 * @verifies {@link PersonDataSource#read(LogicContext,Cohort,LogicCriteria)} test = should get
	 *           cause_of_death
	 */
	@Test
	@SkipBaseSetup
	public void read_shouldGetCause_of_death() throws Exception {
		LogicDataSource lds = Context.getLogicService().getLogicDataSource("person");
		Patient who = Context.getPatientService().getPatient(6);
		LogicContext context = new LogicContextImpl(who);
		
		LogicCriteria criteria = new LogicCriteriaImpl("CAUSE OF DEATH");
		Result result = context.read(who, lds, criteria);
		Assert.assertEquals("ASPIRIN", result.toString());
		
		who = Context.getPatientService().getPatient(2);
		context = new LogicContextImpl(who);
		result = context.read(who, lds, criteria);
		Assert.assertTrue(result.isEmpty());
	}
	
	/**
	 * @verifies {@link PersonDataSource#read(LogicContext,Cohort,LogicCriteria)} test = should get
	 *           dead
	 */
	@Test
	@SkipBaseSetup
	public void read_shouldGetDead() throws Exception {
		LogicDataSource lds = Context.getLogicService().getLogicDataSource("person");
		Patient who = Context.getPatientService().getPatient(6);
		LogicContext context = new LogicContextImpl(who);
		
		LogicCriteria criteria = new LogicCriteriaImpl("DEAD");
		Result result = context.read(who, lds, criteria);
		Assert.assertTrue(result.toBoolean());
		
		who = Context.getPatientService().getPatient(2);
		context = new LogicContextImpl(who);
		result = context.read(who, lds, criteria);
		Assert.assertTrue(result.isEmpty());
	}
	
	/**
	 * @verifies {@link PersonDataSource#read(LogicContext,Cohort,LogicCriteria)} test = should get
	 *           death_date
	 */
	@Test
	@SkipBaseSetup
	public void read_shouldGetDeath_date() throws Exception {
		LogicDataSource lds = Context.getLogicService().getLogicDataSource("person");
		Patient who = Context.getPatientService().getPatient(6);
		LogicContext context = new LogicContextImpl(who);
		
		LogicCriteria criteria = new LogicCriteriaImpl("DEATH DATE");
		Result result = context.read(who, lds, criteria);
		Assert.assertEquals(Context.getDateFormat().parse("07/11/2008"), result.toDatetime());
		
		who = Context.getPatientService().getPatient(2);
		context = new LogicContextImpl(who);
		result = context.read(who, lds, criteria);
		Assert.assertTrue(result.isEmpty());
	}
	
	/**
	 * @see {@link PersonDataSource#read(LogicContext,Cohort,LogicCriteria)}
	 */
	@Test
	@Verifies(value = "should get family name", method = "read(LogicContext,Cohort,LogicCriteria)")
	public void read_shouldGetFamilyName() throws Exception {
		LogicDataSource lds = Context.getLogicService().getLogicDataSource("person");
		Patient who = Context.getPatientService().getPatient(4);
		
		LogicContext context = new LogicContextImpl(who);
		LogicCriteria criteria = new LogicCriteriaImpl("FAMILY NAME").equalTo("Doe");
		Result result = context.read(who, lds, criteria);
		Assert.assertEquals("Doe", result.toString());
		
		who = Context.getPatientService().getPatient(2);
		context = new LogicContextImpl(who);
		criteria = new LogicCriteriaImpl("FAMILY NAME").equalTo("Does");
		result = context.read(who, lds, criteria);
		Assert.assertTrue(result.isEmpty());
	}
	
	/**
	 * @see {@link PersonDataSource#read(LogicContext,Cohort,LogicCriteria)}
	 */
	@Test
	@Verifies(value = "should get family name2", method = "read(LogicContext,Cohort,LogicCriteria)")
	public void read_shouldGetFamilyName2() throws Exception {
		LogicDataSource lds = Context.getLogicService().getLogicDataSource("person");
		Patient who = Context.getPatientService().getPatient(2);
		
		LogicContext context = new LogicContextImpl(who);
		LogicCriteria criteria = new LogicCriteriaImpl("FAMILY NAME2").equalTo("Jr.");
		Result result = context.read(who, lds, criteria);
		Assert.assertEquals("Jr.", result.toString());
		
		who = Context.getPatientService().getPatient(4);
		context = new LogicContextImpl(who);
		result = context.read(who, lds, criteria);
		Assert.assertTrue(result.isEmpty());
	}
	
	/**
	 * @see {@link PersonDataSource#read(LogicContext,Cohort,LogicCriteria)}
	 */
	@Test
	@Verifies(value = "should get first name", method = "read(LogicContext,Cohort,LogicCriteria)")
	public void read_shouldGetFirstName() throws Exception {
		LogicDataSource lds = Context.getLogicService().getLogicDataSource("person");
		Patient who = Context.getPatientService().getPatient(3);
		
		LogicContext context = new LogicContextImpl(who);
		LogicCriteria criteria = new LogicCriteriaImpl("GIVEN NAME").equalTo("Other John");
		Result result = context.read(who, lds, criteria);
		Assert.assertEquals("Other John", result.toString());
		
		who = Context.getPatientService().getPatient(4);
		context = new LogicContextImpl(who);
		result = context.read(who, lds, criteria);
		Assert.assertTrue(result.isEmpty());
	}
	
	/**
	 * @see {@link PersonDataSource#read(LogicContext,Cohort,LogicCriteria)}
	 */
	@Test
	@Verifies(value = "should get middle name", method = "read(LogicContext,Cohort,LogicCriteria)")
	public void read_shouldGetMiddleName() throws Exception {
		LogicDataSource lds = Context.getLogicService().getLogicDataSource("person");
		Patient who = Context.getPatientService().getPatient(4);
		
		LogicContext context = new LogicContextImpl(who);
		LogicCriteria criteria = new LogicCriteriaImpl("MIDDLE NAME").equalTo("Ugly");
		Result result = context.read(who, lds, criteria);
		Assert.assertEquals("Ugly", result.toString());
		
		who = Context.getPatientService().getPatient(2);
		context = new LogicContextImpl(who);
		result = context.read(who, lds, criteria);
		Assert.assertTrue(result.isEmpty());
	}
}
