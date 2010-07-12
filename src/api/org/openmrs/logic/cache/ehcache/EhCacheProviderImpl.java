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
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.logic.cache.LogicCache;
import org.openmrs.logic.cache.LogicCacheConfig;
import org.openmrs.logic.cache.LogicCacheConfigBean;
import org.openmrs.logic.cache.LogicCacheManager;
import org.openmrs.logic.cache.LogicCacheProvider;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class EhCacheProviderImpl extends LogicCacheProvider {
    private final Log log = LogFactory.getLog(getClass());
    private final String LOGIC_CACHE_NAME = "org.openmrs.logic.defaultCache";
    private String EHCACHE_CONFIG = "/logic-ehcache.xml";
    private CacheManager cacheManager;

    public EhCacheProviderImpl() {
    }

    public EhCacheProviderImpl(String pathToConfig) {
        EHCACHE_CONFIG = pathToConfig;
        getCacheManager();
    }

    @Override
    public LogicCache getCache(String name) {
        LogicCache logicCache = cacheList.get(name);
        if(null != logicCache) return logicCache;

        logicCache = createLogicCache(name);

        return logicCache;
    }

    @Override
    public LogicCache getDefaultCache() {
        return getCache(LOGIC_CACHE_NAME);
    }

    @Override
    public void shutDownCacheManager() {
        getCacheManager().shutdown();
    }

    public LogicCache restartCache(String name) {
        LogicCache logicCache = cacheList.get(name);
        if(null == logicCache) return null;

        //to prevent situation of getting cache when it is down.
        synchronized (this) {
            cacheList.remove(name);
            getCacheManager().removeCache(name);

            logicCache = createLogicCache(name);

            this.notifyAll();
        }

        return logicCache;
    }

    public void storeConfig() throws IOException {
        XMLEncoder xmlEncoder = null;
        Map<String, LogicCacheConfigBean> configs = new HashMap<String, LogicCacheConfigBean>();

        try {
            xmlEncoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(getLogicCacheConfigPath())));

            for(String cacheName : LogicCacheManager.getCacheNames()) {
//                LogicCacheConfigBean configToStore = cacheList.get(cacheName).getLogicCacheConfig().getConfigBean();
                LogicCacheConfig cacheConfig = cacheList.get(cacheName).getLogicCacheConfig();
                LogicCacheConfigBean configToStore = new LogicCacheConfigBean();

                if (cacheConfig.getFeature(LogicCacheConfig.Features.DEFAULT_TTL))
                    configToStore.setDefaultTTL(cacheConfig.getDefaultTTL());
                if (cacheConfig.getFeature(LogicCacheConfig.Features.MAX_ELEMENTS_IN_MEMORY))
                    configToStore.setMaxElementsInMemory(cacheConfig.getMaxElementsInMemory());
                if (cacheConfig.getFeature(LogicCacheConfig.Features.MAX_ELEMENTS_ON_DISK))
                    configToStore.setMaxElementsOnDisk(cacheConfig.getMaxElementsOnDisk());
                if (cacheConfig.getFeature(LogicCacheConfig.Features.USING_DISK_STORE))
                    configToStore.setUsingDiskStore(cacheConfig.isUsingDiskStore());
                if (cacheConfig.getFeature(LogicCacheConfig.Features.DISABLE))
                    configToStore.setDisabled(cacheConfig.isDisabled());

                configs.put(cacheName, configToStore);
            }

            xmlEncoder.writeObject(configs);
        } catch (FileNotFoundException e) {
            log.error("Cache configuration is not saved.", e);
            throw new IOException("Error has occured during storing cache`s configuration.");
        } finally {
            if(null != xmlEncoder)
                xmlEncoder.close();
        }

    }

    public LogicCacheConfigBean restoreConfig(String cacheName) {
        Object restoredObj = null;
        XMLDecoder xmlDecoder = null;
        Map<String, LogicCacheConfigBean> configs = new HashMap<String, LogicCacheConfigBean>();

        try {
            xmlDecoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(getLogicCacheConfigPath())));
            restoredObj = xmlDecoder.readObject();
        } catch (FileNotFoundException e) {
            log.warn("Cache configuration not found.");
        } finally {
            if(null != xmlDecoder)
                xmlDecoder.close();
        }

        if(restoredObj != null && restoredObj instanceof Map)
            configs = (Map<String, LogicCacheConfigBean>) restoredObj;

        return configs.get(cacheName);
    }

    private LogicCache createLogicCache(String name) {
        String preConfigCacheName = "prefix."+name;
        Cache preConfigCache = getCacheManager().getCache(preConfigCacheName);
        if(null == preConfigCache)
            preConfigCache = getCacheManager().getCache("preConfiguredCache");
        
        CacheConfiguration defConfig = preConfigCache.getCacheConfiguration();

        CacheConfiguration configuration = new CacheConfiguration();
        configuration.setName(name);
        configuration.setMaxElementsInMemory(defConfig.getMaxElementsInMemory());
        configuration.setMaxElementsOnDisk(defConfig.getMaxElementsOnDisk());
        configuration.setTimeToLiveSeconds(defConfig.getTimeToLiveSeconds());
        configuration.setTimeToIdleSeconds(defConfig.getTimeToIdleSeconds());
        configuration.setOverflowToDisk(defConfig.isOverflowToDisk());
        configuration.setDiskPersistent(defConfig.isDiskPersistent());
        configuration.setClearOnFlush(defConfig.isClearOnFlush());
        configuration.setEternal(defConfig.isEternal());
        configuration.setStatistics(true); //TODO: temporary, this may slow cache. needed for cache monitor.
        configuration.setDiskStorePath(getCacheManager().getDiskStorePath());

        LogicCacheConfigBean configStored = restoreConfig(name);
        if(null != configStored) {
            if(configStored.getMaxElementsInMemory() != null)
                configuration.setMaxElementsInMemory(configStored.getMaxElementsInMemory());
            if(configStored.getMaxElementsOnDisk() != null)
                configuration.setMaxElementsOnDisk(configStored.getMaxElementsOnDisk());
            if(configStored.getDefaultTTL() != null) {
                configuration.setTimeToLiveSeconds(configStored.getDefaultTTL());
                configuration.setTimeToIdleSeconds(configStored.getDefaultTTL());
            }
            configuration.setOverflowToDisk(configStored.isUsingDiskStore());
        }
        Cache cache = new Cache(configuration);
        getCacheManager().addCache(cache);

        LogicCache logicCache = new LogicCacheImpl(cache, this);
        cacheList.put(name, logicCache);

        getCacheManager().removeCache(preConfigCacheName); //remove predefined cache from memory

        return logicCache;
    }

    public CacheManager getCacheManager() {
        if(null == cacheManager) {
            URL url = EhCacheProviderImpl.class.getResource(EHCACHE_CONFIG);
            cacheManager = new CacheManager(url);
        }
        
        return cacheManager;
    }

    private String getLogicCacheConfigPath() {
        return getCacheManager().getDiskStorePath() + System.getProperty("file.separator") + "logic-cache.config";
    }
}
