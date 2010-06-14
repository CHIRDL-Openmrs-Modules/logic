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
package org.openmrs.logic.cache;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.datasource.LogicDataSource;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.util.LogicUtil;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 *
 */
public class LogicCacheKeyTest extends BaseModuleContextSensitiveTest {
//    private LogicCache logicCache;
    private LogicCacheKey logicCacheKey1;
    private LogicCacheKey logicCacheKey2;
    private LogicService logicService;

    @Before
    public void setUp() throws Exception {
//        logicCache = LogicCacheManager.getDefaultLogicCache();
//        assertNotNull("logicCache is null", logicCache);

        logicService = Context.getLogicService();
        assertNotNull(logicService);

        LogicCriteria lc1 = logicService.parse("\"AGE\"");
        LogicDataSource logicDataSource1 = logicService.getLogicDataSource("obs");
        Map<String, Object> parameters1 = new HashMap<String, Object>();
        parameters1.put("1", 1);
        logicCacheKey1 = new LogicCacheKey(parameters1, lc1, logicDataSource1, new Date(), 2);
        assertNotNull("logicCacheKey1 is NULL", logicCacheKey1);

        LogicCriteria lc2 = logicService.parse("\"AGE\"");
        LogicDataSource logicDataSource2 = logicService.getLogicDataSource("obs");
        Map<String, Object> parameters2 = new HashMap<String, Object>();
        parameters2.put("1", 1);
        logicCacheKey2 = new LogicCacheKey(parameters2, lc2, logicDataSource2, new Date(), 2);
        assertNotNull("logicCacheKey2 is NULL", logicCacheKey2);
    }

    @Test
    public void testEquals() throws Exception {
        assertEquals("logicCacheKey1 and logicCacheKey2 are not equals", logicCacheKey1, logicCacheKey2);
    }

    @Test
    public void testCacheKeySerialization() throws IOException, ClassNotFoundException {
        LogicCacheKey deserializedLogicCacheKey = (LogicCacheKey) roundTripSerialization(logicCacheKey1);

        assertEquals("logicCacheKey1 and deserializedLogicCacheKey are not equals", logicCacheKey1, deserializedLogicCacheKey);
    }

    @Test
    public void testResultSerialization() throws ClassNotFoundException, IOException {
        LogicUtil.registerDefaultRules();
        Patient patient = Context.getPatientService().getPatient(2);
        Result result = logicService.eval(patient, logicService.parse("\"AGE\""));
        Result deserializedResult = (Result) roundTripSerialization(result);

        assertEquals("result and deserializedResult are not equals", result, deserializedResult);
    }

    private Object roundTripSerialization(Object forSerialization) throws IOException, ClassNotFoundException {
        //serialize
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(out);
        oos.writeObject(forSerialization);
        oos.close();
        assertTrue("Not serialized by stream.", out.toByteArray().length > 0);

        //deserialize
        byte[] pickled = out.toByteArray();
        InputStream in = new ByteArrayInputStream(pickled);
        ObjectInputStream ois = new ObjectInputStream(in);
        Object serializedObj = ois.readObject();

        return serializedObj;
    }
}
