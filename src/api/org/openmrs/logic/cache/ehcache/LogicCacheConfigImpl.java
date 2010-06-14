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

import org.openmrs.logic.cache.LogicCacheConfig;

/**
 * TODO: implement config of the cache
 */
public class LogicCacheConfigImpl implements LogicCacheConfig {
    @Override
    public Integer getMaxElementsInMemory() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Integer getMaxElementsOnDisk() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Integer getDefaultTTl() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getEvictionAlgorithm() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setDefaultTTl(Integer ttl) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setMaxElementsInMemory(Integer maxInMem) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setMaxElementsOnDisk(Integer maxOnDisk) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setEvictionAlgorithm(String name) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean getFeature(Features name) {
        boolean result = false;

        switch (name) {
            case MAX_ELEMENTS_IN_MEMORY:
                result = true;
                break;
            case MAX_ELEMENTS_ON_DISK:
                result = true;
                break;
            case DEFAULT_TTL:
                result = true;
                break;
            case EVICTION_ALGORITHM:
                result = true;
                break;
        }

        return result;
    }
}
