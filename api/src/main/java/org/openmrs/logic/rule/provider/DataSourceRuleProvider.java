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

import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.rule.ReferenceRule;
import org.openmrs.logic.token.TokenService;


/**
 * Helper class for {@link RuleProvider}s that return {@link ReferenceRule}s
 */
public abstract class DataSourceRuleProvider extends AbstractRuleProvider {

	String getName() {
		try {
			return (String) getClass().getField("NAME").get(this);
		} catch (Exception ex) {
			throw new LogicException("Must declare a public static NAME field");
		}
	}
	
    /**
	 * Returns referenceRulePrefix + dot + configuration
	 * @see org.openmrs.logic.rule.provider.RuleProvider#getRule(java.lang.String)
	 */
	public Rule getRule(String configuration) {
		return new ReferenceRule(getName() + "." + configuration);
	}

	/**
	 * Takes a complete set of all keys supported by this provider, deletes any currently-registered
	 * tokens that are not in that set, and registers any new ones.
	 * 
	 * @param allKeysAndTokens a map from key to preferred token of all keys this provider supports
	 */
	public void registerCompleteTokens(Map<String, String> allKeysAndTokens) {
		TokenService service = Context.getService(TokenService.class);
		
		// delete any keys we'd previously registered, but no longer support
		service.keepOnlyValidConfigurations(this, allKeysAndTokens.keySet());

		// register all current keys
		int counter = 0;
		for (Map.Entry<String, String> keyAndToken : allKeysAndTokens.entrySet()) {
			service.registerToken(keyAndToken.getValue(), this, keyAndToken.getKey());
			if (++counter % 50 == 0)
				Context.flushSession();
		}
	}
}
