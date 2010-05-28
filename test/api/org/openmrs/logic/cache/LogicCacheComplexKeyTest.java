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
import net.sf.ehcache.config.CacheConfiguration;
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

    private final int DISK_CACHE_COUNT = 55;

    private LogicCacheComplexKey logicCacheComplexKey1;
    private LogicCacheComplexKey logicCacheComplexKey2;
    private LogicService logicService;
    private LogicCriteria logicCriteria1;
    private LogicCriteria logicCriteria2;
    private Element element = null;
    private Cache cache;

    @Before
    public void beforeTests() {
        if(null != element) return; //initialize only once;

        logicService = new LogicServiceImpl();
        logicCriteria1 = logicService.parse("\"AGE\"");
        logicCriteria2 = logicService.parse("\"AGE\"");

        logicCacheComplexKey1 = new LogicCacheComplexKey(null, logicCriteria1, null);
        logicCacheComplexKey2 = new LogicCacheComplexKey(null, logicCriteria2, null);

        Map<Integer, Result> resultMap = new Hashtable<Integer, Result>();
        Result result = new Result(true);
        resultMap.put(1, result);
        element = new Element(logicCacheComplexKey1, resultMap);

        cache = LogicCacheManager.getLogicEhCache();
        CacheConfiguration config = cache.getCacheConfiguration();
        config.setTimeToLiveSeconds(3);
        config.setTimeToIdleSeconds(2);
    }

    @Test
    public void testSerialization() throws Exception {
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
        Element simpleElement = null;

        CacheManager cacheManager = LogicCacheManager.getOrCreate();
        assertNotNull("Cache manager is NULL!", cacheManager);
//        System.out.println(cacheManager.getDiskStorePath());
        assertTrue("Empty cache!", cacheManager.getCacheNames().length > 0);

        for(int i = 0; i < DISK_CACHE_COUNT; i++) {
            simpleElement = new Element("key"+i, "value"+i);
            cache.put(simpleElement);
        }

        cache.put(element);

        assertTrue("No elements in memory", cache.getSize()  > 0);

        cache.flush();
//        System.out.println("Disk store = " + cache.getDiskStoreSize());
//        System.out.println("getSize = "+cache.getSize());
//        System.out.println("getMemoryStoreSize = "+cache.getMemoryStoreSize());

        assertTrue("Not flushed!", cache.getDiskStoreSize() > 0);

        synchronized (this) {
            try {
                wait(Long.valueOf("6000"));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        cache.evictExpiredElements();
        assertTrue("Not evicted!", cache.getDiskStoreSize() == 0);
//        System.out.println("after expiration:");
//        System.out.println("Disk store = " + cache.getDiskStoreSize());
//        System.out.println("getSize = "+cache.getSize());
//        System.out.println("getMemoryStoreSize = "+cache.getMemoryStoreSize());
    }
}
