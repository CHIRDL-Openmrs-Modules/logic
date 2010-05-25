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

import java.net.URL;


/**
 *
 */
public class LogicCacheManager {
    private static final String LOGIC_CACHE_NAME = "org.openmrs.logic.defaultCache";
    private static final String LOGIC_CACHE_CONFIG = "/logic-ehcache.xml";

    private static Cache logicEhCache;
    private static CacheManager logicCacheManager;

    public static CacheManager getOrCreate() {
        if(null == logicCacheManager) {
            URL url = LogicCacheManager.class.getResource(LOGIC_CACHE_CONFIG);
            logicCacheManager = CacheManager.create(url);
        }
        return logicCacheManager;
    }

    public static Cache getLogicEhCache() {
        if(null == logicEhCache) {
            logicEhCache = getOrCreate().getCache(LOGIC_CACHE_NAME);
//
//            CacheConfiguration cacheConfiguration = new CacheConfiguration(LOGIC_CACHE_NAME, 1000)
//                    .maxElementsOnDisk(1000)
//                    .diskSpoolBufferSizeMB(30)
//                    .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LRU)
//                    .overflowToDisk(true)
//                    .eternal(false)
//                    .timeToLiveSeconds(600)
//                    .timeToIdleSeconds(300)
//                    .diskPersistent(false)
//                    .diskExpiryThreadIntervalSeconds(150);
//
//            logicEhCache = new Cache(cacheConfiguration);
        }

        return logicEhCache;
    }
}
