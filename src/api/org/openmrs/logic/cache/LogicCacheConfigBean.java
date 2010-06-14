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
public class LogicCacheConfigBean {
    private Integer defaultTTL;
    private String evictionPolicy;
    private Integer maxElementsInMemory;
    private Integer maxElementsOnDisk;

    public Integer getDefaultTTL() {
        return defaultTTL;
    }

    public void setDefaultTTL(Integer defaultTTL) {
        this.defaultTTL = defaultTTL;
    }

    public String getEvictionPolicy() {
        return evictionPolicy;
    }

    public void setEvictionPolicy(String evictionPolicy) {
        this.evictionPolicy = evictionPolicy;
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
