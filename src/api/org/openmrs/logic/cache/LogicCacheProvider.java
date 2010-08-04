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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *   It is an abstraction layer between LogicCacheManager and the certain caching framework. This class keeps all created logic cache`s names.
 * It has a set of abstract methods cache provider of the certain caching system must have.
 */
public abstract class LogicCacheProvider {
    
    protected Map<String, LogicCache> cacheList = new HashMap<String, LogicCache>();

    /**
         *  Gets or creates a logic cache with specified name.
         *
         * @param name the name of the cache we want to get or create
         * @return initialized and ready to work implementation of the LogicCache
         */
    public abstract LogicCache getCache(String name);

    /**
         *  Gets or creates the default logic cache.
         *
         * @return initialized and ready to work implementation of the LogicCache
         */
    public abstract LogicCache getDefaultCache();

    /**
         *  Removes all caches and releases all resources, does all necessary actions.
         */
    public abstract void shutDownCacheManager();

    public Collection<String> getCacheNames() {
        return cacheList.keySet();
    }

}
