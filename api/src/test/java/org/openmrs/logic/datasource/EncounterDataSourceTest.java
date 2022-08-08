/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.logic.datasource;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.Cohort;
import org.openmrs.Encounter;
import org.openmrs.api.context.Context;
import org.openmrs.logic.Duration;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.db.hibernate.HibernateLogicEncounterDAO;
import org.openmrs.logic.impl.LogicContextImpl;
import org.openmrs.logic.impl.LogicCriteriaImpl;
import org.openmrs.logic.op.OperandDate;
import org.openmrs.logic.result.Result;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;

/**
 * Tests for the {@link EncounterDataSource} and {@link HibernateLogicEncounterDAO}
 */
public class EncounterDataSourceTest extends BaseModuleContextSensitiveTest {
	
	private LogicDataSource dataSource = null;
	
	/**
	 * Run before each test to get the logic source
	 */
	@BeforeEach
	public void doSetup() {
		dataSource = Context.getLogicService().getLogicDataSource("encounter");
	}
	
	/**
	 * @verifies {@link EncounterDataSource#read(LogicContext,Cohort,LogicCriteria)} test = should
	 *           return date result for encounter key
	 */
	@Test
	public void read_shouldReturnDateResultForEncounterKey() throws Exception {
		Cohort patients = new Cohort("7");
		LogicContext context = new LogicContextImpl(patients);
		
		Map<Integer, Result> results = dataSource.read(context, patients, new LogicCriteriaImpl("encounter"));
		Assertions.assertEquals(1, results.size());
		Assertions.assertEquals(3, results.get(7).size());
		Assertions.assertEquals("Scheduled", results.get(7).get(0).toString());
		Assertions.assertEquals("Scheduled", results.get(7).get(1).toString());
		Assertions.assertEquals("Emergency", results.get(7).get(2).toString());
	}
	
	/**
	 * @verifies {@link EncounterDataSource#read(LogicContext,Cohort,LogicCriteria)} test = should
	 *           return text result for encounter location key
	 */
	@Test
	public void read_shouldReturnTextResultForEncounterlocationKey() throws Exception {
		Cohort patients = new Cohort("7");
		LogicContext context = new LogicContextImpl(patients);
		
		Map<Integer, Result> results = dataSource.read(context, patients, new LogicCriteriaImpl("encounterLocation").last());
		Assertions.assertEquals(1, results.size());
		Assertions.assertEquals(1, results.get(7).size());
		Assertions.assertEquals("Xanadu", results.get(7).get(0).toString());
	}
	
	/**
	 * @verifies {@link EncounterDataSource#read(LogicContext,Cohort,LogicCriteria)} test = should
	 *           return text result for encounterprovider key
	 */
	@Test
	public void read_shouldReturnTextResultForEncounterproviderKey() throws Exception {
		executeDataSet("org/openmrs/logic/include/EncounterDataSourceTest.xml");
		Cohort patients = new Cohort("7");
		LogicContext context = new LogicContextImpl(patients);
		
		Map<Integer, Result> results = dataSource.read(context, patients, new LogicCriteriaImpl("encounterProvider").last());
		Assertions.assertEquals(1, results.size());
		Assertions.assertEquals(1, results.get(7).size());
		Assertions.assertEquals("Mr. Hippocrates of Cos", results.get(7).get(0).toString());
	}
	
	/**
	 * @verifies {@link EncounterDataSource#read(LogicContext,Cohort,LogicCriteria)} test = should
	 *           not fail with null encounter type
	 */
	@Test
	public void read_shouldNotFailWithNullEncounterType() throws Exception {
		executeDataSet("org/openmrs/logic/include/EncounterDataSourceTest.xml");
		Cohort patients = new Cohort("7");
		LogicContext context = new LogicContextImpl(patients);
		dataSource.read(context, patients, new LogicCriteriaImpl("encounter").last());
	}
	
	/**
	 * @verifies {@link EncounterDataSource#read(LogicContext,Cohort,LogicCriteria)} test = should
	 *           return text result for encounter key
	 */
	@Test
	public void read_shouldReturnTextResultForEncounterKey() throws Exception {
		Cohort patients = new Cohort("7");
		LogicContext context = new LogicContextImpl(patients);
		
		Map<Integer, Result> results = dataSource.read(context, patients, new LogicCriteriaImpl("encounter").first());
		Assertions.assertEquals(1, results.size());
		Assertions.assertEquals(1, results.get(7).size());
		Assertions.assertEquals("Emergency", results.get(7).get(0).toString());
	}
	
	/**
	 * @verifies {@link EncounterDataSource#read(LogicContext,Cohort,LogicCriteria)} test = should
	 *           return text result for encounter key and after operator
	 */
	@Test
	public void read_shouldReturnTextResultForEncounterKeyAndAfterOperator() throws Exception {
		Cohort patients = new Cohort("7");
		LogicContext context = new LogicContextImpl(patients);
		
		Map<Integer, Result> results = dataSource.read(context, patients, new LogicCriteriaImpl("encounter").after(Context
		        .getDateFormat().parse("18/08/2008")));
		Assertions.assertEquals(1, results.size());
		Assertions.assertEquals(1, results.get(7).size());
		Assertions.assertEquals("Scheduled", results.get(7).get(0).toString());
		Assertions.assertEquals("2008-08-19 00:00:00.0", results.get(7).get(0).getResultDate().toString());
	}
	
	/**
	 * @verifies {@link EncounterDataSource#read(LogicContext,Cohort,LogicCriteria)} test = should
	 *           return text result for encounter key and before operator
	 */
	@Test
	public void read_shouldReturnTextResultForEncounterKeyAndBeforeOperator() throws Exception {
		Cohort patients = new Cohort("7");
		LogicContext context = new LogicContextImpl(patients);
		
		Map<Integer, Result> results = dataSource.read(context, patients, new LogicCriteriaImpl("encounter").before(Context
		        .getDateFormat().parse("02/08/2008")));
		Assertions.assertEquals(1, results.size());
		Assertions.assertEquals(1, results.get(7).size());
		Assertions.assertEquals("Emergency", results.get(7).get(0).toString());
		Assertions.assertEquals("2008-08-01 00:00:00.0", results.get(7).get(0).getResultDate().toString());
	}
	
	/**
	 * @verifies {@link EncounterDataSource#read(LogicContext,Cohort,LogicCriteria)} test = should
	 *           return text result for encounter key and contains
	 */
	@Test
	public void read_shouldReturnTextResultForEncounterKeyAndContains() throws Exception {
		Cohort patients = new Cohort("7");
		LogicContext context = new LogicContextImpl(patients);
		
		Map<Integer, Result> results = dataSource.read(context, patients, new LogicCriteriaImpl("encounter")
		        .contains("Scheduled"));
		Assertions.assertEquals(1, results.size());
		Assertions.assertEquals(2, results.get(7).size()); // two "scheduled" encounter types for this user
	}
	
	/**
	 * @verifies {@link EncounterDataSource#read(LogicContext,Cohort,LogicCriteria)} test = should
	 *           return text result for encounter key and equals
	 */
	@Test
	public void read_shouldReturnTextResultForEncounterKeyAndEquals() throws Exception {
		Cohort patients = new Cohort("7");
		LogicContext context = new LogicContextImpl(patients);
		
		Map<Integer, Result> results = dataSource.read(context, patients, new LogicCriteriaImpl("encounter")
		        .contains("Emergency"));
		Assertions.assertEquals(1, results.size());
		Assertions.assertEquals(1, results.get(7).size()); // one "emergency" encounter type for this user
	}
	
	/**
	 * @verifies {@link EncounterDataSource#read(LogicContext,Cohort,LogicCriteria)} test = should
	 *           return text result for encounter key and gte
	 */
	@Test
	public void read_shouldReturnTextResultForEncounterKeyAndGte() throws Exception {
		Cohort patients = new Cohort("7");
		LogicContext context = new LogicContextImpl(patients);
		
		Map<Integer, Result> results = dataSource.read(context, patients, new LogicCriteriaImpl("encounter")
		        .gte(new OperandDate(Context.getDateFormat().parse("15/08/2008"))));
		Assertions.assertEquals(1, results.size());
		Assertions.assertEquals(2, results.get(7).size());
		Assertions.assertEquals(5, ((Encounter) results.get(7).get(0).toObject()).getEncounterId().intValue());
		Assertions.assertEquals(4, ((Encounter) results.get(7).get(1).toObject()).getEncounterId().intValue());
	}
	
	/**
	 * @verifies {@link EncounterDataSource#read(LogicContext,Cohort,LogicCriteria)} test = should
	 *           return text result for encounter key and lte
	 */
	@Test
	public void read_shouldReturnTextResultForEncounterKeyAndLte() throws Exception {
		Cohort patients = new Cohort("7");
		LogicContext context = new LogicContextImpl(patients);
		
		Map<Integer, Result> results = dataSource.read(context, patients, new LogicCriteriaImpl("encounter")
		        .lte(new OperandDate(Context.getDateFormat().parse("01/08/2008"))));
		Assertions.assertEquals(1, results.size());
		Assertions.assertEquals(1, results.get(7).size());
		Assertions.assertEquals(3, ((Encounter) results.get(7).get(0).toObject()).getEncounterId().intValue());
	}
	
	/**
	 * @verifies {@link EncounterDataSource#read(LogicContext,Cohort,LogicCriteria)} test = should
	 *           return text result for encounter key and within
	 */
	@Test
	public void read_shouldReturnTextResultForEncounterKeyAndWithin() throws Exception {
		Cohort patients = new Cohort("7");
		LogicContext context = new LogicContextImpl(patients);
		context.setIndexDate(Context.getDateFormat().parse("03/08/2008"));
		
		Map<Integer, Result> results = dataSource.read(context, patients, new LogicCriteriaImpl("encounter").within(Duration
		        .days(-5.0)));
		Assertions.assertEquals(1, results.size());
		Assertions.assertEquals(1, results.get(7).size());
		Assertions.assertEquals(3, ((Encounter) results.get(7).get(0).toObject()).getEncounterId().intValue());
	}
	
	/**
	 * @verifies {@link EncounterDataSource#read(LogicContext,Cohort,LogicCriteria)} test = should
	 *           return text result for location key and contains
	 */
	@Test
	public void read_shouldReturnTextResultForLocationKeyAndContains() throws Exception {
		Cohort patients = new Cohort("7");
		LogicContext context = new LogicContextImpl(patients);
		context.setIndexDate(Context.getDateFormat().parse("03/08/2008"));
		
		Map<Integer, Result> results = dataSource.read(context, patients, new LogicCriteriaImpl("encounterLocation")
		        .contains("Unknown Location"));
		Assertions.assertEquals(1, results.size());
		Assertions.assertEquals(2, results.get(7).size());
		Assertions.assertEquals("2008-08-15 00:00:00.0", results.get(7).get(0).getResultDate().toString());
		Assertions.assertEquals("2008-08-01 00:00:00.0", results.get(7).get(1).getResultDate().toString());
	}
	
	/**
	 * @verifies {@link EncounterDataSource#read(LogicContext,Cohort,LogicCriteria)} test = should
	 *           return text result for location key and equals
	 */
	@Test
	public void read_shouldReturnTextResultForLocationKeyAndEquals() throws Exception {
		Cohort patients = new Cohort("7");
		LogicContext context = new LogicContextImpl(patients);
		context.setIndexDate(Context.getDateFormat().parse("03/08/2008"));
		
		Map<Integer, Result> results = dataSource.read(context, patients, new LogicCriteriaImpl("encounterLocation")
		        .contains("Unknown Location"));
		Assertions.assertEquals(1, results.size());
		Assertions.assertEquals(2, results.get(7).size());
		Assertions.assertEquals("2008-08-15 00:00:00.0", results.get(7).get(0).getResultDate().toString());
		Assertions.assertEquals("2008-08-01 00:00:00.0", results.get(7).get(1).getResultDate().toString());
	}
	
	/**
	 * @verifies {@link EncounterDataSource#read(LogicContext,Cohort,LogicCriteria)} test = should
	 *           return text result for provider key and contains
	 */
	@Test
	public void read_shouldReturnTextResultForProviderKeyAndContains() throws Exception {
		executeDataSet("org/openmrs/logic/include/EncounterDataSourceTest.xml");
		Cohort patients = new Cohort("7");
		LogicContext context = new LogicContextImpl(patients);
		context.setIndexDate(Context.getDateFormat().parse("03/08/2008"));
		
		Map<Integer, Result> results = dataSource.read(context, patients, new LogicCriteriaImpl("encounterProvider")
		        .contains(502));
		Assertions.assertEquals(1, results.size());
		Assertions.assertEquals(3, results.get(7).size());
		Assertions.assertEquals("2008-08-19 00:00:00.0", results.get(7).get(0).getResultDate().toString());
		Assertions.assertEquals("2008-08-15 00:00:00.0", results.get(7).get(1).getResultDate().toString());
		Assertions.assertEquals("2008-08-01 00:00:00.0", results.get(7).get(2).getResultDate().toString());
	}
	
	/**
	 * @verifies {@link EncounterDataSource#read(LogicContext,Cohort,LogicCriteria)} test = should
	 *           return text result for provider key and equals
	 */
	@Test
	public void read_shouldReturnTextResultForProviderKeyAndEquals() throws Exception {
		executeDataSet("org/openmrs/logic/include/EncounterDataSourceTest.xml");
		Cohort patients = new Cohort("7");
		LogicContext context = new LogicContextImpl(patients);
		context.setIndexDate(Context.getDateFormat().parse("03/08/2008"));
		
		Map<Integer, Result> results = dataSource.read(context, patients, new LogicCriteriaImpl("encounterProvider")
		        .contains(502));
		Assertions.assertEquals(1, results.size());
		Assertions.assertEquals(3, results.get(7).size());
		Assertions.assertEquals("2008-08-19 00:00:00.0", results.get(7).get(0).getResultDate().toString());
		Assertions.assertEquals("2008-08-15 00:00:00.0", results.get(7).get(1).getResultDate().toString());
		Assertions.assertEquals("2008-08-01 00:00:00.0", results.get(7).get(2).getResultDate().toString());
	}
	
}
