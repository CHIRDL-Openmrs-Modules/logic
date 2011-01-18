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
package org.openmrs.logic.task;

import org.openmrs.api.context.Context;
import org.openmrs.logic.token.TokenService;
import org.openmrs.scheduler.StatefulTask;
import org.openmrs.scheduler.Task;


/**
 *
 */
public class InitializeLogicRuleProvidersTask extends StatefulTask implements Task {
	
	public static String NAME = "Initialize Logic Rule Providers";
	
	/**
	 * @see org.openmrs.scheduler.tasks.AbstractTask#execute()
	 */
	@Override
	public void execute() {
		boolean supportsDaemon = false;
		try {
			Class.forName("org.openmrs.api.context.Daemon");
			supportsDaemon = true;
		} catch (ClassNotFoundException ex) {
			// we're in 1.6.x
		}
		if (!supportsDaemon) {
			Context.openSession();
		}
		authenticate();
		Context.getService(TokenService.class).onStartup();
		if (!supportsDaemon) {
			Context.closeSession();
		}
	}
	
}
