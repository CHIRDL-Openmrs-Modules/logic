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
import org.openmrs.logic.cache.LogicCacheConfigBean;
import org.openmrs.logic.cache.LogicCacheProvider;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
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

    public void storeConfig() {
        XMLEncoder xmlEncoder = null;
        CacheManager cacheManager = getCacheManager();
        Map<String, LogicCacheConfigBean> configs = new HashMap<String, LogicCacheConfigBean>();

        try {
            xmlEncoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(getLogicCacheConfigPath())));

            for(String cacheName : cacheManager.getCacheNames()) {
                LogicCacheConfigBean configToStore = cacheList.get(cacheName).getLogicCacheConfig().getConfigBean();
                configs.put(cacheName, configToStore);
            }

            xmlEncoder.writeObject(configs);
        } catch (FileNotFoundException e) {
            log.error("Cache configuration is not saved.", e);
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
            //TODO check if file exists
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
        CacheConfiguration configuration = new CacheConfiguration();
        configuration.setName(name);
        configuration.setMaxElementsInMemory(500);
        configuration.setMaxElementsOnDisk(5000);
        configuration.setTimeToLiveSeconds(120);
        configuration.setTimeToIdleSeconds(120);
        configuration.setOverflowToDisk(true);
        configuration.setDiskPersistent(false);
        configuration.setClearOnFlush(false);
        configuration.setEternal(false);
        configuration.setStatistics(true); //TODO temporary
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
        }
        Cache cache = new Cache(configuration);
        getCacheManager().addCache(cache);

        LogicCache logicCache = new LogicCacheImpl(cache, this);
        cacheList.put(name, logicCache);

        return logicCache;
    }

    private CacheManager getCacheManager() {
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
