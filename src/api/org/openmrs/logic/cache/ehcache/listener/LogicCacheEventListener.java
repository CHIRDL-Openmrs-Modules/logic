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
package org.openmrs.logic.cache.ehcache.listener;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *   Callback methods to listen the Cache`s events.
 */
public class LogicCacheEventListener implements CacheEventListener {

    private static Log log = LogFactory.getLog(LogicCacheEventListener.class);

    @Override
    public void notifyElementRemoved(Ehcache ehcache, Element element) throws CacheException {
        log.info("Cache ElementRemoved");
    }

    @Override
    public void notifyElementPut(Ehcache ehcache, Element element) throws CacheException {
        log.info("Cache ElementPut");
    }

    @Override
    public void notifyElementUpdated(Ehcache ehcache, Element element) throws CacheException {
        log.info("Cache ElementUpdated");
    }

    @Override
    public void notifyElementExpired(Ehcache ehcache, Element element) {
        log.info("Cache ElementExpire");
    }

    @Override
    public void notifyElementEvicted(Ehcache ehcache, Element element) {
        log.info("Cache ElementEvicted");
    }

    @Override
    public void notifyRemoveAll(Ehcache ehcache) {
        log.info("Cache RemoveAll");
    }

    @Override
    public void dispose() {
        log.info("Cache disposed");
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
