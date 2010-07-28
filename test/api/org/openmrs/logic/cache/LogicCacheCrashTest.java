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

import static org.junit.Assert.*;

/**
 *
 */
public class LogicCacheCrashTest {
    private static int MAX_ELEMENTS_IN_MEM = 100;
    private static int MAX_ELEMENTS_ON_DISK = 100;
    private static int COUNT_OBJECTS_TO_CACHE = MAX_ELEMENTS_IN_MEM * 10;

    private LogicCache logicCache;
    private LogicCacheConfig cacheConfig;

    @Before
    public void setUp() throws Exception {
        logicCache = LogicCacheManager.getDefaultLogicCache();
        assertNotNull("logicCache is null", logicCache);

        cacheConfig = logicCache.getLogicCacheConfig();
        assertNotNull("cacheConfig is null", cacheConfig);

        logicCache.storeConfig();
    }

    @After
    public void tearDown() throws Exception {
        logicCache.clean();

        logicCache.restoreConfig();
    }

    @Test
    public void testPutNoDiskStore() throws Exception {
        if (!(cacheConfig.getFeature(LogicCacheConfig.Features.MAX_ELEMENTS_IN_MEMORY) ||
              cacheConfig.getFeature(LogicCacheConfig.Features.USING_DISK_STORE)))
            return;
        cacheConfig.setMaxElementsInMemory(MAX_ELEMENTS_IN_MEM);
        cacheConfig.setUsingDiskStore(false);
        if (cacheConfig.isRestartNeeded() && logicCache.getFeature(LogicCache.Features.RESTART)) {
            logicCache.storeConfig();
            logicCache.restart();
        }

        for (int i = 0; i < COUNT_OBJECTS_TO_CACHE; i++)
            logicCache.put(i, i);

        assertEquals("Cache is not full", logicCache.getMaxSize(), logicCache.getSize());
    }

    @Test
    public void testPutWithDiskStore() throws Exception {
        if (!(cacheConfig.getFeature(LogicCacheConfig.Features.MAX_ELEMENTS_IN_MEMORY) ||
              cacheConfig.getFeature(LogicCacheConfig.Features.USING_DISK_STORE)))
            return;
        cacheConfig.setMaxElementsInMemory(MAX_ELEMENTS_IN_MEM);
        cacheConfig.setMaxElementsOnDisk(MAX_ELEMENTS_ON_DISK);
        cacheConfig.setUsingDiskStore(true);
        if (cacheConfig.isRestartNeeded() && logicCache.getFeature(LogicCache.Features.RESTART)) {
            logicCache.storeConfig();
            logicCache.restart();
        }

        for (int i = 0; i < COUNT_OBJECTS_TO_CACHE; i++)
            logicCache.put(i, i);

        assertEquals("Cache is not full", logicCache.getMaxSize(), logicCache.getSize());
    }

    @Test
    public void testGetNoDiskStore() throws Exception {
        if (!(cacheConfig.getFeature(LogicCacheConfig.Features.MAX_ELEMENTS_IN_MEMORY) ||
              cacheConfig.getFeature(LogicCacheConfig.Features.USING_DISK_STORE)))
            return;
        cacheConfig.setMaxElementsInMemory(MAX_ELEMENTS_IN_MEM);
        cacheConfig.setUsingDiskStore(false);
        if (cacheConfig.isRestartNeeded() && logicCache.getFeature(LogicCache.Features.RESTART)) {
            logicCache.storeConfig();
            logicCache.restart();
        }

        //use "logicCache.getMaxSize()-1" here to prevent running eviction method by caching framework
        for (int i = 0; i < logicCache.getMaxSize()-1; i++)
            logicCache.put(i, i);

        for (int i = 0; i < logicCache.getMaxSize()-1; i++)
            assertEquals("Cached object not equals to original", i, logicCache.get(i));
    }

    @Test
    public void testGetWithDiskStore() throws Exception {
        if (!(cacheConfig.getFeature(LogicCacheConfig.Features.MAX_ELEMENTS_IN_MEMORY) ||
              cacheConfig.getFeature(LogicCacheConfig.Features.USING_DISK_STORE)))
            return;
        cacheConfig.setMaxElementsInMemory(MAX_ELEMENTS_IN_MEM);
        cacheConfig.setMaxElementsOnDisk(MAX_ELEMENTS_ON_DISK);
        cacheConfig.setUsingDiskStore(true);
        if (cacheConfig.isRestartNeeded() && logicCache.getFeature(LogicCache.Features.RESTART)) {
            logicCache.storeConfig();
            logicCache.restart();
        }

        //use "logicCache.getMaxSize()-1" here to prevent running eviction method by caching framework
        for (int i = 0; i < logicCache.getMaxSize()-1; i++)
            logicCache.put(i, i);

        for (int i = 0; i < logicCache.getMaxSize()-1; i++)
            assertEquals("Cached object not equals to original", i, logicCache.get(i));
    }
    
}
