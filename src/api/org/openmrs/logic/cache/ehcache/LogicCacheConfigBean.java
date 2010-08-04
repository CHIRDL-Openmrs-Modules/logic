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

import java.io.Serializable;

/**
 *   This bean is used for storing cache`s configuration to a disk. It has properties according to setters/getters of the
 * {@link org.openmrs.logic.cache.LogicCacheConfig} interface. It is used by {@link org.openmrs.logic.cache.ehcache.EhCacheProviderImpl} with
 * {@link java.beans.XMLDecoder} and {@link java.beans.XMLEncoder} classes.
 *
 * @see EhCacheProviderImpl#storeConfig() 
 */
public class LogicCacheConfigBean implements Serializable {
	private Long defaultTTL;
    private Integer maxElementsInMemory;
    private Integer maxElementsOnDisk;
    private boolean usingDiskStore;
    private boolean disabled;

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isUsingDiskStore() {
        return usingDiskStore;
    }

    public void setUsingDiskStore(boolean usingDiskStore) {
        this.usingDiskStore = usingDiskStore;
    }

    public Long getDefaultTTL() {
		return defaultTTL;
	}
	
	public void setDefaultTTL(Long defaultTTL) {
		this.defaultTTL = defaultTTL;
	}

    public Integer getMaxElementsInMemory() {
        return maxElementsInMemory;
    }

    public void setMaxElementsInMemory(Integer maxElementsInMemory) {
        this.maxElementsInMemory = maxElementsInMemory;
    }

    public Integer getMaxElementsOnDisk() {
        return maxElementsOnDisk;
    }

    public void setMaxElementsOnDisk(Integer maxElementsOnDisk) {
        this.maxElementsOnDisk = maxElementsOnDisk;
    }
}
