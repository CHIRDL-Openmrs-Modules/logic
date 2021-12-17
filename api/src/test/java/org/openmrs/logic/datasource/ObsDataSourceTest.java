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
import org.openmrs.test.SkipBaseSetup;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;

@SkipBaseSetup
public class ObsDataSourceTest extends BaseModuleContextSensitiveTest {
	
	/**
	 * @see org.springframework.test.AbstractTransactionalSpringContextTests#onSetUpInTransaction()
	 */
	@BeforeEach
	public void runBeforeEachTest() throws Exception {
		initializeInMemoryDatabase();
		executeDataSet("org/openmrs/logic/include/LogicTests-patients.xml");
		executeDataSet("org/openmrs/logic/include/LogicBasicTest.concepts.xml");
		authenticate();
	}
	
	/**
	 * @verifies {@link ObsDataSource#read(LogicContext,Cohort,LogicCriteria)} test = should get all
	 *           obs
	 */
	@Test
	public void read_shouldGetAllObs() throws Exception {
		Patient who = Context.getPatientService().getPatient(3);
		LogicContext context = new LogicContextImpl(who.getPatientId());
		
		LogicCriteria criteria = new LogicCriteriaImpl("CD4 COUNT");
		Result result = context.read(who.getPatientId(), criteria);
		Assertions.assertEquals(2, result.size(), "Wrong number of CD4s returned");
		Assertions.assertEquals(Double.valueOf(600d), result.latest().toNumber(), "Last result incorrect");
	}
	
	/**
	 * @verifies {@link ObsDataSource#read(LogicContext,Cohort,LogicCriteria)} test = should get
	 *           first obs
	 */
	@Test
	public void read_shouldGetFirstObs() throws Exception {
		Patient who = Context.getPatientService().getPatient(3);
		LogicContext context = new LogicContextImpl(who.getPatientId());
		
		LogicCriteria criteria = new LogicCriteriaImpl("CD4 COUNT").first();
		Result result = context.read(who.getPatientId(), criteria);
		Assertions.assertEquals(1, result.size(), "Wrong number of CD4s returned");
		Assertions.assertEquals(Double.valueOf(100d), result.toNumber(), "Result incorrect");
	}
	
	/**
	 * @verifies {@link ObsDataSource#read(LogicContext,Cohort,LogicCriteria)} test = should get
	 *           last obs
	 */
	@Test
	public void read_shouldGetLastObs() throws Exception {
		Patient who = Context.getPatientService().getPatient(3);
		LogicContext context = new LogicContextImpl(who.getPatientId());
		
		LogicCriteria criteria = new LogicCriteriaImpl("CD4 COUNT").last();
		Result result = context.read(who.getPatientId(), criteria);
		Assertions.assertEquals(1, result.size(), "Wrong number of CD4s returned");
		Assertions.assertEquals(Double.valueOf(600d), result.toNumber(), "Result incorrect");
	}
	
	/**
	 * @verifies {@link ObsDataSource#read(LogicContext,Cohort,LogicCriteria)} test = should get
	 *           last obs if it is lt value
	 */
	@Test
	public void read_shouldGetLastObsIfItIsLtValue() throws Exception {
		Patient who = Context.getPatientService().getPatient(3);
		LogicContext context = new LogicContextImpl(who.getPatientId());
		
		LogicCriteria criteria = new LogicCriteriaImpl("CD4 COUNT").last().lt(200);
		Result result = context.read(who.getPatientId(), criteria);
		Assertions.assertEquals(1, result.size(), "Wrong number of CD4s returned");
	}
	
	/**
	 * @verifies {@link ObsDataSource#read(LogicContext,Cohort,LogicCriteria)} test = should get
	 *           last obs of those lt value
	 */
	@Test
	public void read_shouldGetLastObsOfThoseLtValue() throws Exception {
		Patient who = Context.getPatientService().getPatient(3);
		LogicContext context = new LogicContextImpl(who.getPatientId());
		
		LogicCriteria criteria = new LogicCriteriaImpl("CD4 COUNT").lt(200).last();
		Result result = context.read(who.getPatientId(), criteria);
		Assertions.assertEquals(1, result.size(), "Wrong number of CD4s returned");
		Assertions.assertEquals(Double.valueOf(100d), result.toNumber(), "Result incorrect");
	}
	
	/**
	 * @verifies {@link ObsDataSource#read(LogicContext,Cohort,LogicCriteria)} test = should get obs
	 *           gt value
	 */
	@Test
	public void read_shouldGetObsGtValue() throws Exception {
		Patient who = Context.getPatientService().getPatient(3);
		LogicContext context = new LogicContextImpl(who.getPatientId());
		
		LogicCriteria criteria = new LogicCriteriaImpl("CD4 COUNT").gt(200);
		Result result = context.read(who.getPatientId(), criteria);
		Assertions.assertEquals(1, result.size(), "Wrong number of CD4s returned");
		Assertions.assertEquals(Double.valueOf(600d), result.toNumber(), "Result incorrect");
	}
	
	/**
	 * @verifies {@link ObsDataSource#read(LogicContext,Cohort,LogicCriteria)} test = should get obs
	 *           lt value
	 */
	@Test
	public void read_shouldGetObsLtValue() throws Exception {
		Patient who = Context.getPatientService().getPatient(3);
		LogicContext context = new LogicContextImpl(who.getPatientId());
		
		LogicCriteria criteria = new LogicCriteriaImpl("CD4 COUNT").lt(200);
		Result result = context.read(who.getPatientId(), criteria);
		Assertions.assertEquals(1, result.size(), "Wrong number of CD4s returned");
		Assertions.assertEquals(Double.valueOf(100d), result.toNumber(), "Result incorrect");
	}
	
	/**
	 * @verifies {@link ObsDataSource#read(LogicContext,Cohort,LogicCriteria)} test = should get obs
	 *           after date
	 */
	@Test
	public void read_shouldGetObsAfterDate() throws Exception {
		Patient who = Context.getPatientService().getPatient(3);
		LogicContext context = new LogicContextImpl(who.getPatientId());
		
		LogicCriteria criteria = new LogicCriteriaImpl("CD4 COUNT").after(Context.getDateFormat().parse("01/01/2007"));
		Result result = context.read(who.getPatientId(), criteria);
		Assertions.assertEquals(2, result.size(), "Wrong number of CD4s returned");
		Assertions.assertEquals(Double.valueOf(600d), result.toNumber(), "Result incorrect");
	}
	
	/**
	 * @verifies {@link ObsDataSource#read(LogicContext,Cohort,LogicCriteria)} test = should get obs
	 *           before date
	 */
	@Test
	public void read_shouldGetObsBeforeDate() throws Exception {
		Patient who = Context.getPatientService().getPatient(3);
		LogicContext context = new LogicContextImpl(who.getPatientId());
		
		LogicCriteria criteria = new LogicCriteriaImpl("CD4 COUNT").before(Context.getDateFormat().parse("03/03/2007"));
		Result result = context.read(who.getPatientId(), criteria);
		Assertions.assertEquals(1, result.size(), "Wrong number of CD4s returned");
		Assertions.assertEquals(Double.valueOf(100d), result.toNumber(), "Result incorrect");
	}
	
	/**
	 * @verifies {@link ObsDataSource#read(LogicContext,Cohort,LogicCriteria)} test = should get obs
	 *           eq value
	 */
	@Test
	public void read_shouldGetObsEqValue() throws Exception {
		Patient who = Context.getPatientService().getPatient(3);
		LogicContext context = new LogicContextImpl(who.getPatientId());
		
		LogicCriteria criteria = new LogicCriteriaImpl("CD4 COUNT").equalTo(100d);
		Result result = context.read(who.getPatientId(), criteria);
		Assertions.assertEquals(1, result.size(), "Wrong number of CD4s returned");
		Assertions.assertEquals(Double.valueOf(100d), result.toNumber(), "Result incorrect");
	}
	
	/**
	 * @verifies {@link ObsDataSource#read(LogicContext,Cohort,LogicCriteria)} test = should get obs
	 *           ge value
	 */
	@Test
	public void read_shouldGetObsGeValue() throws Exception {
		Patient who = Context.getPatientService().getPatient(3);
		LogicContext context = new LogicContextImpl(who.getPatientId());
		
		LogicCriteria criteria = new LogicCriteriaImpl("CD4 COUNT").gte(600);
		Result result = context.read(who.getPatientId(), criteria);
		Assertions.assertEquals(1, result.size(), "Wrong number of CD4s returned");
		Assertions.assertEquals(Double.valueOf(600d), result.toNumber(), "Result incorrect");
		
		criteria = new LogicCriteriaImpl("CD4 COUNT").gte(601);
		result = context.read(who.getPatientId(), criteria);
		Assertions.assertEquals(0, result.size(), "Wrong number of CD4s returned");
	}
	
	/**
	 * @verifies {@link ObsDataSource#read(LogicContext,Cohort,LogicCriteria)} test = should get obs
	 *           le value
	 */
	@Test
	public void read_shouldGetObsLeValue() throws Exception {
		Patient who = Context.getPatientService().getPatient(3);
		LogicContext context = new LogicContextImpl(who.getPatientId());
		
		LogicCriteria criteria = new LogicCriteriaImpl("CD4 COUNT").lte(100);
		Result result = context.read(who.getPatientId(), criteria);
		Assertions.assertEquals(1, result.size(), "Wrong number of CD4s returned");
		Assertions.assertEquals(Double.valueOf(100d), result.toNumber(), "Result incorrect");
		
		criteria = new LogicCriteriaImpl("CD4 COUNT").lte(99);
		result = context.read(who.getPatientId(), criteria);
		Assertions.assertEquals(0, result.size(), "Wrong number of CD4s returned");
	}
	
	/**
	 * @verifies {@link ObsDataSource#read(LogicContext,Cohort,LogicCriteria)} test = should get
	 *           count of obs when obs
	 */
	@Test
	public void read_shouldGetCountOfObsWhenObs() throws Exception {
		Patient who = Context.getPatientService().getPatient(3);
		LogicContext context = new LogicContextImpl(who.getPatientId());
		
		LogicCriteria criteria = new LogicCriteriaImpl("CD4 COUNT").count();
		Result result = context.read(who.getPatientId(), criteria);
		Assertions.assertEquals(1, result.size(), "Wrong number of results returned");
		Assertions.assertEquals(Double.valueOf(2d), result.toNumber(), "Result incorrect");
	}
	
	/**
	 * @verifies {@link ObsDataSource#read(LogicContext,Cohort,LogicCriteria)} test = should get
	 *           count of zero when no obs
	 */
	@Test
	public void read_shouldGetCountOfZeroWhenNoObs() throws Exception {
		Patient who = Context.getPatientService().getPatient(2);
		LogicContext context = new LogicContextImpl(who.getPatientId());
		
		LogicCriteria criteria = new LogicCriteriaImpl("CD4 COUNT").count();
		Result result = context.read(who.getPatientId(), criteria);
		Assertions.assertEquals(1, result.size(), "Wrong number of results returned");
		Assertions.assertEquals(Double.valueOf(0d), result.toNumber(), "Result incorrect");
	}
	
	/**
	 * @verifies {@link ObsDataSource#read(LogicContext,Cohort,LogicCriteria)} test = should get
	 *           average of null when no obs
	 */
	@Test
	public void read_shouldGetAverageOfNullWhenNoObs() throws Exception {
		Patient who = Context.getPatientService().getPatient(2);
		LogicContext context = new LogicContextImpl(who.getPatientId());
		
		LogicCriteria criteria = new LogicCriteriaImpl("CD4 COUNT").average();
		Result result = context.read(who.getPatientId(), criteria);
		Assertions.assertTrue(result.isEmpty(), "Wrong result returned");
	}
	
	/**
	 * @verifies {@link ObsDataSource#read(LogicContext,Cohort,LogicCriteria)} test = should get
	 *           average of obs
	 */
	@Test
	public void read_shouldGetAverageOfObs() throws Exception {
		Patient who = Context.getPatientService().getPatient(3);
		LogicContext context = new LogicContextImpl(who.getPatientId());
		
		LogicCriteria criteria = new LogicCriteriaImpl("CD4 COUNT").average();
		Result result = context.read(who.getPatientId(), criteria);
		Assertions.assertEquals(1, result.size(), "Wrong number of results returned");
		Assertions.assertEquals(Double.valueOf(350.0d), result.toNumber(), "Result incorrect");
	}
	
	/**
	 * @verifies {@link ObsDataSource#read(LogicContext,Cohort,LogicCriteria)} test = should get obs
	 *           gt value after date
	 */
	@Test
	public void read_shouldGetObsGtValueAfterDate() throws Exception {
		Patient who = Context.getPatientService().getPatient(3);
		LogicContext context = new LogicContextImpl(who.getPatientId());
		
		LogicCriteria criteria = new LogicCriteriaImpl("CD4 COUNT").gt(200).after(
		    Context.getDateFormat().parse("01/01/2007"));
		Result result = context.read(who.getPatientId(), criteria);
		Assertions.assertEquals(2, result.size(), "Wrong number of CD4s returned");
		Assertions.assertEquals(Double.valueOf(600d), result.toNumber(), "Result incorrect");
		
		criteria = new LogicCriteriaImpl("CD4 COUNT").gt(200).after(Context.getDateFormat().parse("01/01/2008"));
		result = context.read(who.getPatientId(), criteria);
		Assertions.assertEquals(0, result.size(), "Wrong number of CD4s returned");
		
		criteria = new LogicCriteriaImpl("CD4 COUNT").gt(900).after(Context.getDateFormat().parse("01/01/2007"));
		result = context.read(who.getPatientId(), criteria);
		Assertions.assertEquals(2, result.size(), "Wrong number of CD4s returned");
	}
	
	/**
	 * @verifies {@link ObsDataSource#read(LogicContext,Cohort,LogicCriteria)} test = should get
	 *           first n obs
	 */
	@Test
	public void read_shouldGetFirstNObs() throws Exception {
		Patient who = Context.getPatientService().getPatient(4);
		LogicContext context = new LogicContextImpl(who.getPatientId());
		LogicCriteria criteria = new LogicCriteriaImpl("CD4 COUNT").first(3);
		Result result = context.read(who.getPatientId(), criteria);
		Assertions.assertEquals(3, result.size(), "Wrong number of CD4s returned");
		Assertions.assertEquals(Double.valueOf(100d), result.get(0).toNumber(), "Result incorrectly ordered");
		Assertions.assertEquals(Double.valueOf(300d), result.get(1).toNumber(), "Result incorrectly ordered");
		Assertions.assertEquals(Double.valueOf(200d), result.get(2).toNumber(), "Result incorrectly ordered");
		
		// there are only 4
		criteria = new LogicCriteriaImpl("CD4 COUNT").first(5);
		result = context.read(who.getPatientId(), criteria);
		Assertions.assertEquals(4, result.size(), "Wrong number of CD4s returned");
	}
	
	/**
	 * @verifies {@link ObsDataSource#read(LogicContext,Cohort,LogicCriteria)} test = should get
	 *           last n obs
	 */
	@Test
	public void read_shouldGetLastNObs() throws Exception {
		Patient who = Context.getPatientService().getPatient(4);
		LogicContext context = new LogicContextImpl(who.getPatientId());
		
		LogicCriteria criteria = new LogicCriteriaImpl("CD4 COUNT").last(3);
		Result result = context.read(who.getPatientId(), criteria);
		Assertions.assertEquals(3, result.size(), "Wrong number of CD4s returned");
		Assertions.assertEquals(Double.valueOf(400d), result.get(0).toNumber(), "Result incorrectly ordered");
		Assertions.assertEquals(Double.valueOf(200d), result.get(1).toNumber(), "Result incorrectly ordered");
		Assertions.assertEquals(Double.valueOf(300d), result.get(2).toNumber(), "Result incorrectly ordered");
		
		// there are only 4
		criteria = new LogicCriteriaImpl("CD4 COUNT").last(5);
		result = context.read(who.getPatientId(), criteria);
		Assertions.assertEquals(4, result.size(), "Wrong number of CD4s returned");
	}
	
	/**
	 * @verifies {@link ObsDataSource#read(LogicContext,Cohort,LogicCriteria)} test = should get
	 *           first n obs if they are lt value
	 */
	@Test
	public void read_shouldGetFirstNObsIfTheyAreLtValue() throws Exception {
		Patient who = Context.getPatientService().getPatient(4);
		LogicContext context = new LogicContextImpl(who.getPatientId());
		
		LogicCriteria criteria = new LogicCriteriaImpl("CD4 COUNT").first(2).lt(400);
		Result result = context.read(who.getPatientId(), criteria);
		Assertions.assertEquals(2, result.size(), "Wrong number of CD4s returned");
		Assertions.assertEquals(Double.valueOf(100d), result.get(0).toNumber(), "Result incorrectly ordered");
	}
	
	/**
	 * @verifies {@link ObsDataSource#read(LogicContext,Cohort,LogicCriteria)} test = should get
	 *           first n obs of those lt value
	 */
	@Test
	public void read_shouldGetFirstNObsOfThoseLtValue() throws Exception {
		Patient who = Context.getPatientService().getPatient(4);
		LogicContext context = new LogicContextImpl(who.getPatientId());
		
		LogicCriteria criteria = new LogicCriteriaImpl("CD4 COUNT").lt(250).first(2);
		Result result = context.read(who.getPatientId(), criteria);
		Assertions.assertEquals(2, result.size(), "Wrong number of CD4s returned");
		Assertions.assertEquals(Double.valueOf(100d), result.get(0).toNumber(), "Result incorrectly ordered");
		Assertions.assertEquals(Double.valueOf(200d), result.get(1).toNumber(), "Result incorrectly ordered");
	}
	
	/**
	 * @verifies {@link ObsDataSource#read(LogicContext,Cohort,LogicCriteria)} test = should get
	 *           last obs if it is before date
	 */
	@Test
	public void read_shouldGetLastObsIfItIsBeforeDate() throws Exception {
		Patient who = Context.getPatientService().getPatient(4);
		LogicContext context = new LogicContextImpl(who.getPatientId());
		
		LogicCriteria criteria = new LogicCriteriaImpl("CD4 COUNT").last().before(
		    Context.getDateFormat().parse("01/01/2005"));
		Result result = context.read(who.getPatientId(), criteria);
		Assertions.assertEquals(0, result.size(), "Wrong number of CD4s returned");
	}
	
	/**
	 * @verifies {@link ObsDataSource#read(LogicContext,Cohort,LogicCriteria)} test = should get
	 *           last obs of those before date
	 */
	@Test
	public void read_shouldGetLastObsOfThoseBeforeDate() throws Exception {
		Patient who = Context.getPatientService().getPatient(4);
		LogicContext context = new LogicContextImpl(who.getPatientId());
		
		LogicCriteria criteria = new LogicCriteriaImpl("CD4 COUNT").before(Context.getDateFormat().parse("01/01/2007"))
		        .last();
		Result result = context.read(who.getPatientId(), criteria);
		Assertions.assertEquals(1, result.size(), "Wrong number of CD4s returned");
		Assertions.assertEquals(Double.valueOf(300d), result.toNumber(), "Result incorrectly ordered");
		
	}

	/**
     * @see {@link ObsDataSource#read(LogicContext,Cohort,LogicCriteria)}
     * 
     */
    @Test
    public void read_shouldGetReturnObsOrderedByDatetime() throws Exception {
		Patient who = Context.getPatientService().getPatient(4);
		LogicContext context = new LogicContextImpl(who.getPatientId());
		
		LogicCriteria criteria = new LogicCriteriaImpl("CD4 COUNT");
		Result result = context.read(who.getPatientId(), criteria);
		
		int counter = 0;
		Result previousResult = null;
		Result currentResult = null;
		while(counter < result.size()) {
			previousResult  = currentResult;
			currentResult = result.get(counter);
			if (previousResult != null) {
				Assertions.assertTrue(previousResult.getResultDate().after(currentResult.getResultDate()));
			}
			counter ++;
		}
    }
	
}
