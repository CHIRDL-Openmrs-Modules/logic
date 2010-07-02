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
package org.openmrs.logic;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.logic.cache.LogicCacheManager;
import org.openmrs.module.Activator;
import org.openmrs.module.BaseModuleActivator;

/**
 *
 */
public class LogicModuleActivator extends BaseModuleActivator {
	
	private static final Log log = LogFactory.getLog(LogicModuleActivator.class);

    @Override
    public void contextRefreshed() {

    }

    @Override
    public void started() {

    }

    @Override
    public void stopped() {

    }

    @Override
    public void willRefreshContext() {

    }

    @Override
    public void willStart() {
        log.debug("Shutting down logic module ...");
    }

    @Override
    public void willStop() {
        LogicCacheManager.shutDown();
        log.debug("Starting logic module ...");
    }

}
