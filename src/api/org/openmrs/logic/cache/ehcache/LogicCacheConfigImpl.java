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
import org.openmrs.logic.cache.LogicCacheConfig;

/**
 *
 */
public class LogicCacheConfigImpl implements LogicCacheConfig {
    private Cache cache;

    public LogicCacheConfigImpl(Cache cache) {
        this.cache = cache;
    }

    @Override
    public Integer getMaxElementsInMemory() throws UnsupportedOperationException {
        return cache.getCacheConfiguration().getMaxElementsInMemory();
    }

    @Override
    public Integer getMaxElementsOnDisk() throws UnsupportedOperationException {
        return cache.getCacheConfiguration().getMaxElementsOnDisk();
    }

    @Override
    public long getDefaultTTl() throws UnsupportedOperationException {
        return cache.getCacheConfiguration().getTimeToLiveSeconds();
    }

    @Override
    public String getDiskStorePath() throws UnsupportedOperationException {
        return cache.getCacheConfiguration().getDiskStorePath();
    }

    @Override
    public void setDefaultTTl(long ttl) throws UnsupportedOperationException {
        cache.getCacheConfiguration().setTimeToLiveSeconds(ttl);
    }

    @Override
    public void setMaxElementsInMemory(Integer maxInMem) throws UnsupportedOperationException {
        cache.getCacheConfiguration().setMaxElementsInMemory(maxInMem);
    }

    @Override
    public void setMaxElementsOnDisk(Integer maxOnDisk) throws UnsupportedOperationException {
        cache.getCacheConfiguration().setMaxElementsOnDisk(maxOnDisk);
    }

    @Override
    public boolean getFeature(Features name) {
        boolean result = false;

        switch (name) {
            case MAX_ELEMENTS_IN_MEMORY:
                result = true;
                break;
            case MAX_ELEMENTS_ON_DISK:
                result = true;
                break;
            case DEFAULT_TTL:
                result = true;
                break;
            case DISK_STORE_PATH:
                result = true;
                break;
        }

        return result;
    }
}
