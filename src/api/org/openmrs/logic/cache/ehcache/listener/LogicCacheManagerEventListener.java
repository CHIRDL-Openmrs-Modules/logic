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
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Status;
import net.sf.ehcache.event.CacheManagerEventListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 *
 */
public class LogicCacheManagerEventListener implements CacheManagerEventListener {
	
	private static Log log = LogFactory.getLog(LogicCacheManagerEventListener.class);

	@Override
    public void dispose() throws CacheException {
	    log.info("Cache Manager disposed");
    }

	@Override
    public Status getStatus() {
	    return CacheManager.getInstance().getStatus();
    }

	@Override
    public void init() throws CacheException {
		log.info("Cache Manager init");	    
    }

	@Override
    public void notifyCacheAdded(String arg0) {
		log.info("Cache Manager CacheAdded");	    
    }

	@Override
    public void notifyCacheRemoved(String arg0) {
		log.info("Cache Manager CacheRemoved");	    
    }

}
