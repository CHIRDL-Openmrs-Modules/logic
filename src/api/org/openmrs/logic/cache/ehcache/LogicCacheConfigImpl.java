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
import net.sf.ehcache.config.CacheConfiguration;
import org.openmrs.logic.cache.LogicCacheConfig;
import org.openmrs.logic.cache.LogicCacheConfigBean;

/**
 *
 */
public class LogicCacheConfigImpl implements LogicCacheConfig {
    private final Cache cache;
    private LogicCacheConfigBean configBean;
    private boolean restartNeeded = false;

    public LogicCacheConfigImpl(Cache cache) {
        this.cache = cache;
        CacheConfiguration cacheConfig = cache.getCacheConfiguration();
        configBean = new LogicCacheConfigBean();
        if(getFeature(Features.DEFAULT_TTL)) configBean.setDefaultTTL(cacheConfig.getTimeToLiveSeconds());
        if(getFeature(Features.MAX_ELEMENTS_IN_MEMORY)) configBean.setMaxElementsInMemory(cacheConfig.getMaxElementsInMemory());
        if(getFeature(Features.MAX_ELEMENTS_ON_DISK)) configBean.setMaxElementsOnDisk(cacheConfig.getMaxElementsOnDisk());
        if(getFeature(Features.USING_DISK_STORE)) configBean.setUsingDiskStore(cacheConfig.isOverflowToDisk());
        if(getFeature(Features.DISABLE)) configBean.setDisabled(cache.isDisabled());
    }

    @Override
    public Integer getMaxElementsInMemory() {
        return cache.getCacheConfiguration().getMaxElementsInMemory();
    }

    @Override
    public Integer getMaxElementsOnDisk() {
        return cache.getCacheConfiguration().getMaxElementsOnDisk();
    }

    @Override
    public Long getDefaultTTL() {
        return cache.getCacheConfiguration().getTimeToLiveSeconds();
    }

    @Override
    public boolean isUsingDiskStore() {
        //use configBean here because ehcache config will be changed after cache restart.
        return configBean.isUsingDiskStore();
    }

    @Override
    public boolean isDisabled() {
        return cache.isDisabled();
    }

    @Override
    public void setDefaultTTL(Long ttl) {
        cache.getCacheConfiguration().setTimeToLiveSeconds(ttl);
        configBean.setDefaultTTL(ttl);
    }

    @Override
    public void setMaxElementsInMemory(Integer maxInMem) {
        cache.getCacheConfiguration().setMaxElementsInMemory(maxInMem);
        configBean.setMaxElementsInMemory(maxInMem);
    }

    @Override
    public void setMaxElementsOnDisk(Integer maxOnDisk) {
        cache.getCacheConfiguration().setMaxElementsOnDisk(maxOnDisk);
        configBean.setMaxElementsOnDisk(maxOnDisk);
    }

    @Override
    public void setUsingDiskStore(boolean isDiskStore) {
        cache.getCacheConfiguration().setOverflowToDisk(isDiskStore);
        configBean.setUsingDiskStore(isDiskStore);

        restartNeeded = true;
    }

    @Override
    public void setDisabled(boolean disabled) {
        cache.setDisabled(disabled);
        configBean.setDisabled(disabled);
    }

    @Override
    public boolean isRestartNeeded() {
        return restartNeeded;
    }

    ///////////////////////TODO: delete later
    @Override
    public Long getCacheHits() {
        return cache.getStatistics().getCacheHits();
    }

    @Override
    public Long getCacheMisses() {
        return cache.getStatistics().getCacheMisses();
    }
    ///////////////////////

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
            case USING_DISK_STORE:
                result = true;
                break;
            case DISABLE:
                result = true;
                break;
        }

        return result;
    }

}
