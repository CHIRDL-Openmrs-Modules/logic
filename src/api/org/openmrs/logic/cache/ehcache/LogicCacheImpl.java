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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.logic.cache.LogicCache;
import org.openmrs.logic.cache.LogicCacheConfig;

/**
 *
 */
public class LogicCacheImpl implements LogicCache {
    private final Log log = LogFactory.getLog(getClass());

    private final Cache cache;
    private LogicCacheConfig logicCacheConfig;

//    private LogicCacheConfigBean logicCacheConfig;

    public LogicCacheImpl(Cache cache) {
        this.cache = cache;
        logicCacheConfig = new LogicCacheConfigImpl(cache);
    }

    @Override
    public void put(Object key, Object value, int ttl) {
        cache.put(new Element(key, value, false, ttl, ttl));
    }

    @Override
    public void put(Object key, Object value) {
        cache.put(new Element(key, value));
    }

    @Override
    public Object get(Object key) {
        Element element = cache.get(key);
        return element == null ? null : element.getValue();
    }

    @Override
    public int getSize() {
        return cache.getSize();
    }

    @Override
    public void flush() throws UnsupportedOperationException {
        cache.flush();
    }

    @Override
    public void remove(Object key) {
        cache.remove(key);
    }

    @Override
    public boolean getFeature(Features name) {
        boolean result = false;

        switch (name) {
            case FLUSH:
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

    @Override
    public LogicCacheConfig getLogicCacheConfig() {
        return logicCacheConfig;
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
    public String getCacheSpecificStats() {
        return cache.toString();
    }

}
