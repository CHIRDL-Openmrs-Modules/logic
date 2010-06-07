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
package org.openmrs.logic.cache.ehcache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import java.net.URL;


/**
 *
 */
public class LogicCacheManagerTMP {
    private static final String LOGIC_CACHE_NAME = "org.openmrs.logic.defaultCache";
    private static final String LOGIC_CACHE_CONFIG = "/logic-ehcache.xml";

    private static Cache logicEhCache;
    private static CacheManager logicCacheManager;

    public static CacheManager getOrCreate() {
        if(null == logicCacheManager) {
            URL url = LogicCacheManagerTMP.class.getResource(LOGIC_CACHE_CONFIG);
            logicCacheManager = new CacheManager(url);
        }
        return logicCacheManager;
    }

    public static Cache getLogicEhCache() {
        if(null == logicEhCache) {
            logicEhCache = getOrCreate().getCache(LOGIC_CACHE_NAME);
        }

        return logicEhCache;
    }

}
