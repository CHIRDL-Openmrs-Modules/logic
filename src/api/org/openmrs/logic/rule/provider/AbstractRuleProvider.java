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
package org.openmrs.logic.rule.provider;



/**
 * This class provides no-op implementations of all event notification methods.
 * 
 * RuleProviders should extend this class to be be protected against changes in the RuleProvider
 * interface, in case we add more event notification methods in the future.
 */
public abstract class AbstractRuleProvider implements RuleProvider {
	
	/**
	 * @see org.openmrs.logic.rule.provider.RuleProvider#afterStartup()
	 */
	@Override
	public void afterStartup() {
	    // do nothing
	}
	
}
