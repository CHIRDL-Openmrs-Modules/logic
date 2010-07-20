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

import static org.junit.Assert.*; //TODO ctrl+alt+O

/**
 *
 */
public class LogicCacheConfigTest {

    private LogicCache logicCache;
    private LogicCacheConfig cacheConfig;

    @Before
    public void setUp() throws Exception {
        logicCache = LogicCacheManager.getDefaultLogicCache();
        assertNotNull("logicCache is null", logicCache);

        cacheConfig = logicCache.getLogicCacheConfig();
        assertNotNull("cacheConfig is null", cacheConfig);
    }

    @After
    public void tearDown() throws Exception {
        logicCache.clean();
    }

    @Test
    public void testDefaultTTl() throws Exception {
        //check if current feature is supported
        if(cacheConfig.getFeature(LogicCacheConfig.Features.DEFAULT_TTL))
            return;
        
        int extTtl = 100;
        Long defTTL = cacheConfig.getDefaultTTL();
        assertNotNull("Got null def ttl", defTTL);
        assertTrue("Default ttl must be positive value", defTTL >= 0);

        Long newTTL = defTTL + extTtl;

        cacheConfig.setDefaultTTL(newTTL);

        assertEquals("Property default ttl is not set", newTTL, cacheConfig.getDefaultTTL());

        //restore old ttl
        cacheConfig.setDefaultTTL(defTTL);
    }

    @Test
    public void testMaxElementsInMemory() throws Exception {
        //check if current feature is supported
        if(cacheConfig.getFeature(LogicCacheConfig.Features.MAX_ELEMENTS_IN_MEMORY))
            return;
    }

    @Test
    public void testMaxElementsOnDisk() throws Exception {
        //check if current feature is supported
        if(cacheConfig.getFeature(LogicCacheConfig.Features.MAX_ELEMENTS_ON_DISK))
            return;
    }
}
