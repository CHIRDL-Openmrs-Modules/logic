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

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.impl.LogicServiceImpl;
import org.openmrs.logic.result.Result;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Hashtable;
import java.util.Map;

import static org.junit.Assert.*;

/**
 *
 */
public class LogicCacheComplexKeyTest {

    private LogicCacheComplexKey logicCacheComplexKey1;
    private LogicCacheComplexKey logicCacheComplexKey2;
    private LogicService logicService;
    private LogicCriteria logicCriteria1;
    private LogicCriteria logicCriteria2;
    private Element element = null;
    private Element simpleElement = null;

    @Before
    public void beforeTests() {
        if(null != element) return; //initialize only once;

        logicService = new LogicServiceImpl();
        logicCriteria1 = logicService.parse("\"AGE\"");
        logicCriteria2 = logicService.parse("\"AGE\"");

        logicCacheComplexKey1 = new LogicCacheComplexKey(null, null, logicCriteria1, null);
        logicCacheComplexKey2 = new LogicCacheComplexKey(null, null, logicCriteria2, null);

        Map<Integer, Result> resultMap = new Hashtable<Integer, Result>();
        Result result = new Result(true);
        resultMap.put(1, result);
        element = new Element(logicCacheComplexKey1, resultMap);

        simpleElement = new Element("key1", "value1");
    }

    @Test
    public void testEquals() throws Exception {
        assertEquals("Comparing keys with criteria.", logicCacheComplexKey1, logicCacheComplexKey2);
        assertTrue("Not serialized by element.", element.getSerializedSize() > 0);

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(out);
            oos.writeObject(element);
            oos.close();
            assertTrue("Not serialized by stream.", out.toByteArray().length > 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDiskCache () {
        Cache cache = LogicCacheManager.getLogicEhCache();
        CacheManager cacheManager = LogicCacheManager.getOrCreate();
        assertNotNull("Cache manager is NULL!", cacheManager);
        System.out.println(cacheManager.getDiskStorePath());
        assertTrue("Empty cache!", cacheManager.getCacheNames().length > 0);

//        cache.put(element);
        cache.put(simpleElement);
        cache.flush();
        System.out.println("Disk store = " + cache.getDiskStoreSize());
        //assertTrue("Not flushed!", cache.getDiskStoreSize() > 0);
    }
}
