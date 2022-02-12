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
package org.openmrs.logic;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.logic.datasource.LogicDataSource;
import org.openmrs.logic.impl.LogicContextImpl;
import org.openmrs.logic.impl.LogicCriteriaImpl;
import org.openmrs.logic.result.Result;
import org.openmrs.test.SkipBaseSetup;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;

/**
 * Tests the ObsDataSource functionality
 */
public class ObsDataSourceTest extends BaseModuleContextSensitiveTest {
	
	@BeforeEach
	public void runBeforeEachTest() throws Exception {
		initializeInMemoryDatabase();
		executeDataSet("org/openmrs/logic/include/LogicStandardDatasets.xml");
		executeDataSet("org/openmrs/logic/include/ObsDataSourceTest.xml");
		authenticate();
	}
	
	/**
	 * TODO change to use the in memory database
	 */
	@SkipBaseSetup
	@Test
	public void shouldObsDataSource() throws Exception {
		LogicDataSource lds = Context.getLogicService().getLogicDataSource("obs");
		Cohort patients = new Cohort();
		
		patients.addMember(2);
		patients.addMember(3);
		
		Assertions.assertEquals(2, patients.size());
		LogicContextImpl context = new LogicContextImpl(patients);
		Map<Integer, Result> result = lds.read(context, patients, new LogicCriteriaImpl("CD4 COUNT"));
		context = null;
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(2, result.size());
	}
}
