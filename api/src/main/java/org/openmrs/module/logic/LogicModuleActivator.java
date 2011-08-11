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
package org.openmrs.module.logic;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicConstants;
import org.openmrs.logic.util.LogicUtil;
import org.openmrs.module.Activator;
import org.openmrs.util.OpenmrsUtil;

/**
 *
 */
public class LogicModuleActivator implements Activator {
	
	private static final Log log = LogFactory.getLog(LogicModuleActivator.class);
	
	/**
	 * @see org.openmrs.module.Activator#startup()
	 */
	public void startup() {
		log.debug("Starting logic module ...");
		
		log.info("Creating default directory structure for logic module ...");
		String[] properties = {
				Context.getAdministrationService().getGlobalProperty(LogicConstants.RULE_DEFAULT_CLASS_FOLDER),
				Context.getAdministrationService().getGlobalProperty(LogicConstants.RULE_DEFAULT_SOURCE_FOLDER)
		};
		for (String property : properties)
	        OpenmrsUtil.getDirectoryInApplicationDataDirectory(property);
	
		LogicUtil.initialize();
	}
	
	/**
	 * @see org.openmrs.module.Activator#shutdown()
	 */
	public void shutdown() {
		log.debug("Shutting down logic module ...");
	}
	
}
