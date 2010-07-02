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
import org.openmrs.logic.cache.LogicCacheConfigBean;

/**
 *
 */
public class LogicCacheConfigImpl implements LogicCacheConfig {
    private final Cache cache;

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
    public long getDefaultTTL() throws UnsupportedOperationException {
        return cache.getCacheConfiguration().getTimeToLiveSeconds();
    }

    @Override
    public boolean setDefaultTTL(long ttl) throws UnsupportedOperationException {
        cache.getCacheConfiguration().setTimeToLiveSeconds(ttl);
        //TODO: true if restart is needed
        return false;
    }

    @Override
    public boolean setMaxElementsInMemory(Integer maxInMem) throws UnsupportedOperationException {
        cache.getCacheConfiguration().setMaxElementsInMemory(maxInMem);
        //TODO: true if restart is needed
        return false;
    }

    @Override
    public boolean setMaxElementsOnDisk(Integer maxOnDisk) throws UnsupportedOperationException {
        cache.getCacheConfiguration().setMaxElementsOnDisk(maxOnDisk);
        //TODO: true if restart is needed
        return false;
    }

    @Override
    public Long getCacheHits() throws UnsupportedOperationException {
        return cache.getStatistics().getCacheHits();
    }

    @Override
    public Long getCacheMisses() throws UnsupportedOperationException {
        return cache.getStatistics().getCacheMisses();
    }

    @Override
    public LogicCacheConfigBean getConfigBean() {
        LogicCacheConfigBean retVal = new LogicCacheConfigBean();
        if(getFeature(Features.DEFAULT_TTL)) retVal.setDefaultTTL(getDefaultTTL());
        if(getFeature(Features.MAX_ELEMENTS_IN_MEMORY)) retVal.setMaxElementsInMemory(getMaxElementsInMemory());
        if(getFeature(Features.MAX_ELEMENTS_ON_DISK)) retVal.setMaxElementsOnDisk(getMaxElementsOnDisk());

        return retVal;
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
            case CACHE_HITS:
                result = true;
                break;
            case CACHE_MISSES:
                result = true;
                break;
        }

        return result;
    }

}
