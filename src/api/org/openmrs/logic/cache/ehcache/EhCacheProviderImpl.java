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

import net.sf.ehcache.CacheManager;
import org.openmrs.logic.cache.LogicCache;
import org.openmrs.logic.cache.LogicCacheProvider;

import java.net.URL;

/**
 *
 */
public class EhCacheProviderImpl extends LogicCacheProvider {
    private final String LOGIC_CACHE_NAME = "org.openmrs.logic.defaultCache";
    private String LOGIC_CACHE_CONFIG = "/logic-ehcache.xml";

    private CacheManager cacheManager;

    @Override
    public LogicCache getCache(String name) {
        LogicCache logicCache = cacheList.get(name);
        if(null != logicCache)
            return logicCache;

        logicCache = new LogicCacheImpl(getCacheManager().getCache(name));
        cacheList.put(name, logicCache);
        return logicCache;
    }

    @Override
    public LogicCache getDefaultCache() {
        return getCache(LOGIC_CACHE_NAME);
    }

    public CacheManager getCacheManager() {
        if(null == cacheManager) {
            URL url = EhCacheProviderImpl.class.getResource(LOGIC_CACHE_CONFIG);
            cacheManager = new CacheManager(url);
        }
        
        return cacheManager;
    }

    public EhCacheProviderImpl() {

    }

    public EhCacheProviderImpl(String pathToConfig) {
        LOGIC_CACHE_CONFIG = pathToConfig;
        getCacheManager();
    }
}
