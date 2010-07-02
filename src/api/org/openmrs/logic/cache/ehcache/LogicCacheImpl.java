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

/**
 * 
 */
public class LogicCacheImpl implements LogicCache {
    private final Log log = LogFactory.getLog(getClass());

    private final Cache cache;
    private final EhCacheProviderImpl ehCacheProvider;
//    private final CacheManager cacheManager;
//    private final String cacheConfigPath;

    private LogicCacheConfig logicCacheConfig;

    public LogicCacheImpl(Cache cache, EhCacheProviderImpl ehCacheProvider) {
        this.cache = cache;
        logicCacheConfig = new LogicCacheConfigImpl(cache);
        this.ehCacheProvider = ehCacheProvider;
    }

    /*

        public LogicCacheImpl(CacheConfiguration configuration, CacheManager cacheManager) {
            this.cacheManager = cacheManager;
            cacheConfigPath = configuration.getName() + ".config";
            LogicCacheConfigBean config = restoreConfig();
            if(null != config) {
                configuration.setMaxElementsInMemory(config.getMaxElementsInMemory());
                configuration.setMaxElementsOnDisk(config.getMaxElementsOnDisk());
                configuration.setTimeToLiveSeconds(config.getDefaultTTL());
                configuration.setTimeToIdleSeconds(config.getDefaultTTL());
            }

            cache = new Cache(configuration);
            logicCacheConfig = new LogicCacheConfigImpl(cache);
            this.cacheManager.addCache(cache);
        }

    */
    private Cache getCache() {
        if(!Status.STATUS_ALIVE.equals(cache.getStatus())) {
            log.warn(cache.getName() + " has invalid status. Cache may not work.");
        }

        return cache;
    }

    @Override
    public void storeConfig() {
        ehCacheProvider.storeConfig();
    }
/*

    @Override
    public void storeConfig() {
        XMLEncoder xmlEncoder = null;
        CacheConfiguration cacheConfig = getCache().getCacheConfiguration();

        LogicCacheConfigBean configToStore = new LogicCacheConfigBean();
        configToStore.setDefaultTTL(cacheConfig.getTimeToLiveSeconds());
        configToStore.setMaxElementsInMemory(cacheConfig.getMaxElementsInMemory());
        configToStore.setMaxElementsOnDisk(cacheConfig.getMaxElementsOnDisk());

        try {
            xmlEncoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(cacheConfigPath)));
            xmlEncoder.writeObject(configToStore);
        } catch (FileNotFoundException e) {
            log.error("Cache configuration is not saved.", e);
        } finally {
            if(null != xmlEncoder)
                xmlEncoder.close();
        }
    }

    public LogicCacheConfigBean restoreConfig() {
        LogicCacheConfigBean configRestored = null;
        Object restoredObj = null;
        XMLDecoder xmlDecoder = null;

        try {
            xmlDecoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(cacheConfigPath)));
            restoredObj = xmlDecoder.readObject();
        } catch (FileNotFoundException e) {
            log.warn("Cache configuration not found.", e);
        } finally {
            if(null != xmlDecoder)
                xmlDecoder.close();
        }

        if(restoredObj != null && restoredObj instanceof LogicCacheConfigBean)
            configRestored = (LogicCacheConfigBean) restoredObj;

        return configRestored;
    }

*/

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
    }


    @Override
    public void flush() throws UnsupportedOperationException {
        getCache().flush();
        log.debug("Flush logicCache");
    }

    @Override
    public String getCacheSpecificStats() {
        return getCache().toString();
    }

    @Override
    public boolean getFeature(Features name) {
        boolean result = false;

        switch (name) {
            case FLUSH:
                result = true;
                break;
        }

        return result;
    }
}
