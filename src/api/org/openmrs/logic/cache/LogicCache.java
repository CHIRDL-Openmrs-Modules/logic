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
package org.openmrs.logic.cache;

import java.io.IOException;

/**
 *   The Logic Cache is the central interface. It provides all necessary methods to cache objects.
 * It is managed by the {@link org.openmrs.logic.cache.LogicCacheManager}. This interface hides work
 * of the certain caching framework`s cache. Some methods may not be supported, such methods as getMaxSize, restart, flush.
 * They may throw UnsupportedOperationException, so you have to ask cache if supports certain method next way:
 * <pre>
 * if(logicCache.getFeature(LogicCache.Features.RESTART))
 *      logicCache.restart();
 * </pre>
 */
public interface LogicCache {
    /**
         *  Enumeration of those methods which may not be supported by the certain caching framework.
         * Used only by {@link LogicCache#getFeature} method. 
         */
    public enum Features {FLUSH, RESTART, MAX_SIZE}

    /**
         *  Each cache has it`s unique name
         *
         * @return current cache`s name
         */
    String getName();

    /**
         *  Puts value object into the current cache. If the object with this key exists it will be updated with the new value.
         *
         * @param key unique object which must have equals and hashCode methods for comparison
         * @param value object we want to cache
         * @param ttl time-to-live in seconds
         */
    void put(Object key, Object value, int ttl);

    /**
         *  Puts value object into the current cache. If the object with this key exists it will be updated with the new value.
         *
         * @param key unique object which must have equals and hashCode methods for comparison
         * @param value object we want to cache
         */
    void put(Object key, Object value);

    /**
         *  Gets object by the key.
         *
         * @param key which identifies cached object
         * @return found cached object or null
         */
    Object get(Object key);

    /**
         *   Returns actual elements in the cache. This includes elements in memory and on disk.
         *
         * @return count of cached elements
         */
    int getSize();

    /**
         *  Calculates maximum cache size. If there is disk store it will be diskStoreSize and inMemorySize.
         * Or just memory store size if no disk store.
         *
         * @return maximum cache size
         * @throws UnsupportedOperationException throws exception if this method is not supported
         */
    int getMaxSize() throws UnsupportedOperationException;

    /**
         *  Removes cached element from a cache by it`s key. If there is no such element method does nothing.
         *
         * @param key which identifies cached element
         */
    void remove(Object key);

    /**
         *  Some methods may not be supported by caching framework. Such methods may throw UnsupportedOperationException.
         * To ask is the method is supported we may call getFeature method with a proper feature name.
         *
         * @param name feature`s name according to a method`s name
         * @return true if feature is supported else false
         *
         * @see org.openmrs.logic.cache.LogicCache.Features
         */
    boolean getFeature(Features name);

    /**
         *  Returns current cache`s configuration implementation of {link org.openmrs.logic.cache.LogicCacheConfig} interface.
         *
         * @return current cache`s configuration object
         */
    LogicCacheConfig getLogicCacheConfig();

    /**
         *  Removes all cached objects. This includes all stores.
         *
         */
    void clean();

    /**
         *  Stores cache`s configuration to an XML file.
         *
         * @throws IOException if there is problem with read/write to filesystem
         */
    void storeConfig() throws IOException;

    /**
         *  Restores cache`s configuration if it was saved before. If there is no such configuration or some else problems
         *  this method logs warning message.
         */
    void restoreConfig();

    /**
         *  Restarts the cache. This method disposes current cache and creates a new one.
         *
         * @return just created cache
         * @throws UnsupportedOperationException throws exception if this method is not supported
         */
    LogicCache restart() throws UnsupportedOperationException;

    /**
         *  Moves all cached elements from memory store to the disk store.
         *
         * @throws UnsupportedOperationException throws exception if this method is not supported
         */
    void flush() throws UnsupportedOperationException;

}
