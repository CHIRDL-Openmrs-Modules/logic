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

import java.util.Map;


/**
 * Helper class for {@link RuleProvider}s that can enumerate a complete set of keys to
 * register as reference rules at startup (also deleting keys that are no longer supported).
 * If you have a very large number of keys, you should probably not extend this class, but rather
 * implement some inexpensive startup process that avoids unnecessarily re-registering tokens.
 */
public abstract class RegisterAtStartupDataSourceRuleProvider extends DataSourceRuleProvider {
	
	/**
	 * @return a complete set of all keys this {@link RuleProvider} supports, mapped to their preferred tokens
	 */
	public abstract Map<String, String> getAllKeysAndTokens();
	
	/**
	 * Registers tokens for all keys this provider supports (and delete any it doesn't)
	 * @see org.openmrs.logic.rule.provider.RuleProvider#afterStartup()
	 */
	@Override
	public void afterStartup() {
		registerCompleteTokens(getAllKeysAndTokens());
	}

}
