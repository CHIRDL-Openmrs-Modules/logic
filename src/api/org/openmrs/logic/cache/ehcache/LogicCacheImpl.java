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
import org.openmrs.logic.cache.LogicCache;
import org.openmrs.logic.cache.LogicCacheConfig;

/**
 *
 */
public class LogicCacheImpl implements LogicCache {
    private LogicCacheConfig logicCacheConfig;
    private final Cache cache;

    public LogicCacheImpl(Cache cache) {
        this.cache = cache;
        logicCacheConfig = new LogicCacheConfig();
        //TODO restore logicConfiguration from it`s possible store
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
    public void flush() {
        cache.flush();
    }

    @Override
    public void remove(Object key) {
        cache.remove(key);
    }

    @Override
    public Long getCacheHits() {
        return cache.getStatistics().getCacheHits();
    }

    @Override
    public Long getCacheMisses() {
        return cache.getStatistics().getCacheMisses();
    }

    @Override
    public String getCacheSpecificStats() {
        return cache.toString();
    }

    public LogicCacheConfig getLogicCacheConfig() {
        return logicCacheConfig;
    }

    public void setLogicCacheConfig(LogicCacheConfig logicCacheConfig) {
        this.logicCacheConfig = logicCacheConfig;
    }

}
