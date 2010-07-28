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
import net.sf.ehcache.Element;
import net.sf.ehcache.Status;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.logic.cache.LogicCache;
import org.openmrs.logic.cache.LogicCacheConfig;
import org.openmrs.logic.cache.LogicCacheConfigBean;

import java.io.IOException;

/**
 *
 */
public class LogicCacheImpl implements LogicCache {
    private final Log log = LogFactory.getLog(getClass());

    private Cache cache;
    private final EhCacheProviderImpl ehCacheProvider;

    private LogicCacheConfig logicCacheConfig;

    public LogicCacheImpl(Cache cache, EhCacheProviderImpl ehCacheProvider) {
        this.cache = cache;
        logicCacheConfig = new LogicCacheConfigImpl(cache);
        this.ehCacheProvider = ehCacheProvider;
    }

    private Cache getCache() {
        if (!Status.STATUS_ALIVE.equals(cache.getStatus())) {
            log.warn(cache.getName() + " has invalid status. Trying to get cache from cache-manager.");
            synchronized (ehCacheProvider) {
                cache = ehCacheProvider.getCacheManager().getCache(cache.getName());
                ehCacheProvider.notifyAll();
            }
        }

        return cache;
    }

    @Override
    public void storeConfig() throws IOException {
        ehCacheProvider.storeConfig();
    }

    @Override
    public void restoreConfig() {
        LogicCacheConfigBean cacheConfigBean = ehCacheProvider.restoreConfig(getName());
        if (logicCacheConfig.getFeature(LogicCacheConfig.Features.DEFAULT_TTL))
            logicCacheConfig.setDefaultTTL(cacheConfigBean.getDefaultTTL());
        if (logicCacheConfig.getFeature(LogicCacheConfig.Features.MAX_ELEMENTS_IN_MEMORY))
            logicCacheConfig.setMaxElementsInMemory(cacheConfigBean.getMaxElementsInMemory());
        if (logicCacheConfig.getFeature(LogicCacheConfig.Features.MAX_ELEMENTS_ON_DISK))
            logicCacheConfig.setMaxElementsOnDisk(cacheConfigBean.getMaxElementsOnDisk());
        if (logicCacheConfig.getFeature(LogicCacheConfig.Features.USING_DISK_STORE) && logicCacheConfig.isUsingDiskStore() != cacheConfigBean.isUsingDiskStore())
            logicCacheConfig.setUsingDiskStore(cacheConfigBean.isUsingDiskStore());
        if (logicCacheConfig.getFeature(LogicCacheConfig.Features.DISABLE))
            logicCacheConfig.setDisabled(cacheConfigBean.isDisabled());
    }

    @Override
    public LogicCache restart() {
        return ehCacheProvider.restartCache(getCache().getName());
    }

    @Override
    public String getName() {
        return getCache().getName();
    }

    @Override
    public void put(Object key, Object value, int ttl) {
        getCache().put(new Element(key, value, false, ttl, ttl));
        log.debug("Put new object into the logicCache");
    }

    @Override
    public void put(Object key, Object value) {
        getCache().put(new Element(key, value));
        log.debug("Put new object into the logicCache");
    }

    @Override
    public Object get(Object key) {
        Element element = getCache().get(key);
        return element == null ? null : element.getValue();
    }

    @Override
    public int getSize() {
        return getCache().getSize();
    }

    @Override
    public int getMaxSize() {
        int retVal = logicCacheConfig.getMaxElementsInMemory();
        retVal += logicCacheConfig.isUsingDiskStore() ? logicCacheConfig.getMaxElementsOnDisk() : 0;

        return retVal;
    }

    @Override
    public void remove(Object key) {
        getCache().remove(key);
    }

    @Override
    public LogicCacheConfig getLogicCacheConfig() {
        return logicCacheConfig;
    }

    @Override
    public void clean() {
        getCache().removeAll();
        log.debug(getCache().getName() + ": cleaning.");
    }


    @Override
    public void flush() {
        getCache().flush();
        log.debug(getCache().getName() + ": flushing.");
    }

    @Override
    public String getCacheSpecificStats() {
        return getCache().toString();
    }

    @Override
    public boolean getFeature(Features name) {
        boolean retVal = false;

        switch (name) {
            case FLUSH:
                retVal = false;
                break;
            case RESTART:
                retVal = true;
                break;
            case MAX_SIZE:
                retVal = logicCacheConfig.getFeature(LogicCacheConfig.Features.MAX_ELEMENTS_IN_MEMORY) || logicCacheConfig.getFeature(LogicCacheConfig.Features.MAX_ELEMENTS_ON_DISK);
                break;
        }

        return retVal;
    }
}
