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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.datasource.LogicDataSource;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.Date;

import static org.junit.Assert.*;

/**
 *
 */
public class LogicCacheTest extends BaseModuleContextSensitiveTest {
    private final int CACHE_OBJS_COUNT = 800;
    private final int DEF_TTL = 100;

    private LogicCache logicCache;

    @Before
    public void setUp() throws Exception {
        logicCache = LogicCacheManager.getDefaultLogicCache();
        assertNotNull("logicCache is null", logicCache);
    }

    @After
    public void tearDown() throws Exception {
        logicCache.clean();
    }

    @Test
    public void testLogicCachePutGet() {
        for(int i = 0; i < CACHE_OBJS_COUNT; ++i)
            logicCache.put("key"+i, i, DEF_TTL);

        assertEquals(CACHE_OBJS_COUNT, logicCache.getSize());

        for(int i = 0; i < CACHE_OBJS_COUNT; ++i) {
            assertEquals(i, logicCache.get("key"+i));
        }
    }

    @Test
    public void testRemove() {
        Integer key0 = 0, value0 = 1;
        Integer gotValue0;

        logicCache.put(key0, value0);
        gotValue0 = (Integer) logicCache.get(key0);
        assertEquals("Getting value from cache by the key the value was put.", value0, gotValue0);

        logicCache.remove(key0);
        gotValue0 = (Integer) logicCache.get(key0);
        assertNull("Getting removed object, expected null.", gotValue0);

        //nothing must happen!
        logicCache.remove(key0);
    }

    @Test
    public void testClean() {
        for(Integer i = 0; i < CACHE_OBJS_COUNT; ++i)
            logicCache.put(i, i, DEF_TTL);

        assertEquals(CACHE_OBJS_COUNT, logicCache.getSize());

        logicCache.clean();
        assertEquals(0, logicCache.getSize());

        //nothing must happen!
        logicCache.clean();
    }

    @Test
    public void testLogicCacheKey() {
        LogicService logicService = Context.getLogicService();
        LogicCriteria lc = logicService.parse("\"AGE\"");
        LogicDataSource logicDataSource = logicService.getLogicDataSource("obs");
//        Patient patient = Context.getPatientService().getPatient(2);
//        LogicContext logicContext = new LogicContextImpl(patient);

        LogicCacheKey logicCacheKey = new LogicCacheKey(null, lc, logicDataSource, new Date(), 2);

        Integer testValue = 1;

        logicCache.put(logicCacheKey, testValue, DEF_TTL);
        Integer gotValue = (Integer) logicCache.get(logicCacheKey);
        assertEquals("Getting value from cache by the key the value was put.", testValue, gotValue);        
    }
}
