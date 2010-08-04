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
 *   Implementation of the LogicCacheProvider with the ehcache caching framework.
 */
public class EhCacheProviderImpl extends LogicCacheProvider {
    private final Log log = LogFactory.getLog(getClass());

    private final String DEFAULT_LOGIC_CACHE_NAME = "org.openmrs.logic.defaultCache";

    private String EHCACHE_CONFIG_PATH = "/logic-ehcache.xml";

    private CacheManager cacheManager;

    /**
         *  Keeps cache configurations to restore one after the cache restart.
         */
    private Map<String, CacheConfiguration> predefinedConfigs;

    public EhCacheProviderImpl() {
    }

    /**
         *  Create cache provider with specified path to the ehcache configuration xml.
         *
         * @param pathToConfig path to ehcache configuration file
         */
    public EhCacheProviderImpl(String pathToConfig) {
        EHCACHE_CONFIG_PATH = pathToConfig;
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
        return getCache(DEFAULT_LOGIC_CACHE_NAME);
    }

    @Override
    public void shutDownCacheManager() {
        getCacheManager().shutdown();
    }

    /**
         *  Restarts cache with the specified name. This includes disposing of the cache and creating a new one with the same configuration.
         * <p/>You need to restart cache only to activate changed configuration which cannot be changed dynamically. This depends on the
         * caching framework. 
         *
         * @param name cache`s name
         * @return initialized logic cache with updated configuration
         *
         * @see org.openmrs.logic.cache.LogicCache#restart() 
         */
    public LogicCache restartCache(String name) {
        LogicCache logicCache = cacheList.get(name);
        if(null == logicCache) return createLogicCache(name);

        //to prevent situation of getting cache when it is down.
        synchronized (this) {
            cacheList.remove(name);
            getCacheManager().removeCache(name);

            logicCache = createLogicCache(name);

            this.notifyAll();
        }

        return logicCache;
    }

    /**
         *  Stores all cache configurations to a XML file by  {@link EhCacheProviderImpl#getLogicCacheConfigPath()} path.
        * Uses XMLEncoder to store bean to XML.
        *
         * @throws IOException if there are issues with r/w of the filesystem
         *
         * @see org.openmrs.logic.cache.ehcache.LogicCacheConfigBean
         */
    public void storeConfig() throws IOException {
        XMLEncoder xmlEncoder = null;
        Map<String, LogicCacheConfigBean> configs = new HashMap<String, LogicCacheConfigBean>();

        try {
            xmlEncoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(getLogicCacheConfigPath())));

            for(String cacheName : LogicCacheManager.getCacheNames()) {

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
            throw new IOException(e);
        } finally {
            if(null != xmlEncoder)
                xmlEncoder.close();
        }

    }

    /**
         *  If stored configuration exists, restores it from the XML file by {@link EhCacheProviderImpl#getLogicCacheConfigPath()} path. 
         * Uses XMLDecoder to restore bean from XML.
         *
         * @param cacheName cache`s name
         * @return restored configuration bean or null
         */
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

    /**
         *  Creates new cache of the ehcache caching framework with predefined configuration certainly for current
         * cache name or default predefined configuration.
         * <p/>To add predefined configuration  TODO:complete 
         *
         *
         * @param name
         * @return
         */
    private LogicCache createLogicCache(String name) {
        String preConfigCacheName = "prefix."+name;

        CacheConfiguration defConfig = getPredefinedConfiguration(preConfigCacheName);
        if(null == defConfig)
            defConfig = getPredefinedConfiguration("preConfiguredCache");    

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
        //configuration.setStatistics(true); //NOTE: this may slow cache. but useful for debugging
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
        if(null != configStored) cache.setDisabled(configStored.isDisabled());
        
        getCacheManager().addCache(cache);

        LogicCache logicCache = new LogicCacheImpl(cache, this);
        cacheList.put(name, logicCache);

        return logicCache;
    }

    public CacheManager getCacheManager() {
        if(null == cacheManager) {
            URL url = EhCacheProviderImpl.class.getResource(EHCACHE_CONFIG_PATH);
            cacheManager = new CacheManager(url);
        }
        
        return cacheManager;
    }

    private CacheConfiguration getPredefinedConfiguration(String name) {
        if(null == predefinedConfigs) {
            predefinedConfigs = new HashMap<String, CacheConfiguration>();
            String [] cacheNames = getCacheManager().getCacheNames();
            for(String cacheName : cacheNames) {
                predefinedConfigs.put(cacheName, getCacheManager().getCache(cacheName).getCacheConfiguration());
                getCacheManager().removeCache(cacheName); //clean mem, we don`t need this cache.
            }
        }

        CacheConfiguration config = predefinedConfigs.get(name);
        if(null == config)
            config = new CacheConfiguration(name, 0);
        
        return config;
    }

    private String getLogicCacheConfigPath() {
        return getCacheManager().getDiskStorePath() + System.getProperty("file.separator") + "logic-cache.config";
    }
}
