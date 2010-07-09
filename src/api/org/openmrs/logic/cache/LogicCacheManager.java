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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.logic.cache.ehcache.EhCacheProviderImpl;

import java.util.Collection;
import java.util.Map;

/**
 *
 */
public class LogicCacheManager {
    private static final Log log = LogFactory.getLog(LogicCacheManager.class);
    
    private static final String EHCACHE = "ehcache";

    public static String CURRENT_CACHE_FRAMEWORK = EHCACHE;

    private static LogicCacheProvider logicCacheProvider = getCacheProvider();

    private static LogicCacheProvider getCacheProvider() {
        if(EHCACHE.equals(CURRENT_CACHE_FRAMEWORK)) {
            log.info("Initializing new LogicCacheManager with LogicCacheProvider - " + CURRENT_CACHE_FRAMEWORK);
            return new EhCacheProviderImpl();
        } else
            return null;
    }

    public static LogicCache getLogicCache(String name) {
        return logicCacheProvider.getCache(name);
    }

    public static LogicCache getDefaultLogicCache() {
        return logicCacheProvider.getDefaultCache();
    }

    public static Collection<String> getCacheNames() {
        return logicCacheProvider.getCacheNames();
    }

    public static void shutDown() {
        logicCacheProvider.shutDownCacheManager();
    }
}
