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
 *
 */
public abstract class LogicCacheProvider {
    
    protected Map<String, LogicCache> cacheList = new HashMap<String, LogicCache>();

    public abstract LogicCache getCache(String name);
    
    public abstract LogicCache getDefaultCache();

    public Collection<String> getCacheNames() {
        return cacheList.keySet();
    }

}
