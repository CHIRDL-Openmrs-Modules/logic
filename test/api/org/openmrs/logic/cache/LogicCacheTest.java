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
import static org.junit.Assert.assertNotNull;

/**
 *
 */
public class LogicCacheTest {
    private final String CACHE_NAME = "org.openmrs.logic.defaultCache";

    private LogicCache logicCache;

    private Boolean beforeOnce = false;

    @Before
    public void setUp() throws Exception {
        if(beforeOnce) return;
        beforeOnce = true;

        logicCache = LogicCacheManager.getLogicCache(CACHE_NAME);
    }

    @Test
    public void logicCachePutGetTest() {
        assertNotNull("logicCache is null", logicCache);
    }
}
