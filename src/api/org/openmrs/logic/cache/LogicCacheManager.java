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

/**
 *   This is the central class which provides access to logic cache by it`s name. You need not create logic cache manually, you have to
 * get it from the LogicCacheManager.
 */
public class LogicCacheManager {
    private static final Log log = LogFactory.getLog(LogicCacheManager.class);
    
    private static final String EHCACHE = "ehcache";

    private static final LogicCacheProvider logicCacheProvider = getCacheProvider();

    /**
         * Keeps current caching framework`s name.
         */
    public static final String CURRENT_CACHE_FRAMEWORK = EHCACHE;

    private static LogicCacheProvider getCacheProvider() {
        if(EHCACHE.equals(CURRENT_CACHE_FRAMEWORK)) {
            log.info("Initializing new LogicCacheManager with LogicCacheProvider - " + CURRENT_CACHE_FRAMEWORK);
            return new EhCacheProviderImpl();
        } else
            return null;
    }

    /**
         *  Gets or creates a logic cache with specified name.
         *
         * @param name the name of the cache we want to get or create
         * @return initialized and ready to work implementation of the LogicCache
         */
    public static LogicCache getLogicCache(String name) {
        return logicCacheProvider.getCache(name);
    }

    /**
         *  Returns the default logic cache.
         *
         * @return initialized and ready to work implementation of the LogicCache
         */
    public static LogicCache getDefaultLogicCache() {
        return logicCacheProvider.getDefaultCache();
    }

    /**
         *  This method returns all names of logic caches which were created.
         *
         * @return a collection of strings which are names of all logic caches
         */
    public static Collection<String> getCacheNames() {
        return logicCacheProvider.getCacheNames();
    }

    /**
         *  Removes all caches and releases all resources, does all necessary actions.
         */
    public static void shutDown() {
        logicCacheProvider.shutDownCacheManager();
    }
}
