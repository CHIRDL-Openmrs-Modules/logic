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

/**
 *  This class provides interface to cache`s configuration. Some of properties can be changed dynamically,
 * but some of them need restart of the cache. So after changing of a property you have to ask is restart needed
 * ({@link LogicCacheConfig#isRestartNeeded()} ). Some methods may not be supported by caching framework.
 * They may throw UnsupportedOperationException, so you have to ask cache if supports certain method next way:
 * <pre>
 * if(logicCacheConfig.getFeature(LogicCacheConfig.Features.DISABLE))
 *      logicCacheConfig.setDisabled(true);
 * </pre>
 *
 * There are several properties you may change:<br/>
 * maxElementsInMemory - memory store size.<br/>
 * maxElementsOnDisk - disk store size.<br/>
 * defaultTTL - if you use {@link LogicCache#put(Object, Object)}, which without ttl parameter, so defaultTTL is used.<br/>
 * usingDiskStore - shows does this cache use disk store or not.<br/>
 * disable - shows does this cache is disabled. If true then does not work, it will not put objects, and will always return null on get(). 
 */
public interface LogicCacheConfig {
    /**
         *  Enumeration of those methods which may not be supported by the certain caching framework.
         * Used only by {@link LogicCacheConfig#getFeature} method.
         */
    public enum Features {
        /**
                 *  Name of feature to ask if {@link LogicCacheConfig#setMaxElementsInMemory(Integer)} or
                 * {@link LogicCacheConfig#getMaxElementsInMemory()} method is supported
                 */
        MAX_ELEMENTS_IN_MEMORY,
        /**
                 *  Name of feature to ask if {@link LogicCacheConfig#setMaxElementsOnDisk(Integer)} or
                 *  {@link LogicCacheConfig#getMaxElementsOnDisk()} method if supported
                 */
        MAX_ELEMENTS_ON_DISK,
        /**
                 *  Name of feature to ask if {@link LogicCacheConfig#setDefaultTTL(Long)} or
                 *  {@link LogicCacheConfig#getDefaultTTL()} method is supported
                 */
        DEFAULT_TTL,
        /**
                 *  Name of feature to ask if {@link LogicCacheConfig#setUsingDiskStore(boolean)} or
                 *  {@link LogicCacheConfig#isUsingDiskStore()} method is supported
                 */
        USING_DISK_STORE,
        /**
                 *  Name of feature to ask if {@link LogicCacheConfig#setDisabled(boolean)} or
                 *  {@link LogicCacheConfig#isDisabled()} method is supported
                 */
        DISABLE
    }

    Integer getMaxElementsInMemory() throws UnsupportedOperationException;

    Integer getMaxElementsOnDisk() throws UnsupportedOperationException;

    Long getDefaultTTL() throws UnsupportedOperationException;

    boolean isUsingDiskStore() throws UnsupportedOperationException;

    boolean isDisabled() throws UnsupportedOperationException;

    void setDefaultTTL(Long ttl) throws UnsupportedOperationException;

    void setMaxElementsInMemory(Integer maxInMem) throws UnsupportedOperationException;

    void setMaxElementsOnDisk(Integer maxOnDisk) throws UnsupportedOperationException;

    void setUsingDiskStore(boolean isDiskStore) throws UnsupportedOperationException;

    void setDisabled(boolean disabled) throws UnsupportedOperationException;

    /**
         *  Shows does this cache need restarting to activate changed configuration.
         *
         * @return true if restart is needed else false
         */
    boolean isRestartNeeded();

    /**
         *  Some methods may not be supported by caching framework. Such methods may throw UnsupportedOperationException.
         * To ask is the method supported we may call this method with a proper feature name.
         *
         * @param name feature`s name according to a method`s name
         * @return true if feature is supported else false
         *
         * @see org.openmrs.logic.cache.LogicCacheConfig.Features
         */
    boolean getFeature(Features name);

}
