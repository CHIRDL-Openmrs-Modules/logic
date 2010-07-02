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
 *
 */
public interface LogicCacheConfig {
    public enum Features {MAX_ELEMENTS_IN_MEMORY, MAX_ELEMENTS_ON_DISK, DEFAULT_TTL, DISK_STORE_PATH, CACHE_HITS, CACHE_MISSES}

    Integer getMaxElementsInMemory() throws UnsupportedOperationException;

    Integer getMaxElementsOnDisk() throws UnsupportedOperationException;

    Long getDefaultTTL() throws UnsupportedOperationException;

    boolean setDefaultTTL(Long ttl) throws UnsupportedOperationException;

    boolean setMaxElementsInMemory(Integer maxInMem) throws UnsupportedOperationException;

    boolean setMaxElementsOnDisk(Integer maxOnDisk) throws UnsupportedOperationException;
    
    boolean getFeature(Features name) throws UnsupportedOperationException;

    Long getCacheHits() throws UnsupportedOperationException;

    Long getCacheMisses() throws UnsupportedOperationException;

    LogicCacheConfigBean getConfigBean();

}
