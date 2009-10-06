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
package org.openmrs.logic.result;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Encounter;

/**
 * Tests all methods on the {@link Result} object
 * 
 * @see Result
 */
public class ResultTest {
	
	/**
	 * @verifies {@link Result#toObject()}
	 * test = should return all results for result list
	 */
	@SuppressWarnings("deprecation")
    @Test
	public void toObject_shouldReturnAllResultsForResultList() throws Exception {
		Result parentResult = new Result();
		Result firstResult = new Result(new Date("2008/08/12"), "some value", new Encounter(123));
		Result secondResult = new Result(new Date("2008/08/15"), "some other value", new Encounter(124));
		
		parentResult.add(firstResult);
		parentResult.add(secondResult);
		
		Object toObject = parentResult.toObject();
		Assert.assertTrue(toObject instanceof Object[]);
	}

	/**
	 * @verifies {@link Result#toObject()}
	 * test = should return resultObject for single results
	 */
	@SuppressWarnings("deprecation")
    @Test
	public void toObject_shouldReturnResultObjectForSingleResults()
			throws Exception {
		Result firstResult = new Result(new Date("2008/08/12"), "some value", new Encounter(123));
		
		Assert.assertEquals(new Encounter(123), firstResult.toObject());
	}
}
