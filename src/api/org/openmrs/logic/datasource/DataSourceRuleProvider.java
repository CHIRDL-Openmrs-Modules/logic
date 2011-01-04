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
package org.openmrs.logic.datasource;

import java.util.Collection;

import org.openmrs.api.context.Context;
import org.openmrs.logic.Rule;
import org.openmrs.logic.rule.ReferenceRule;
import org.openmrs.logic.rule.provider.AbstractRuleProvider;
import org.openmrs.logic.rule.provider.RuleProvider;
import org.openmrs.logic.token.TokenService;


/**
 * Helper class for data sources that want to register their keys as reference rules at startup. 
 */
public abstract class DataSourceRuleProvider extends AbstractRuleProvider implements RuleProvider {
	
	/**
	 * @return the name of this data source, to be used in reference rules
	 */
	public abstract String getDataSourceName();
	
	/**
	 * @return the keys this data source supports
	 */
	public abstract Collection<String> getKeys();
	
	/**
	 * @see org.openmrs.logic.rule.provider.RuleProvider#getRule(java.lang.String)
	 */
	@Override
	public Rule getRule(String configuration) {
		return new ReferenceRule(getDataSourceName() + "." + configuration);
	}
	
	/**
	 * Registers tokens for all keys this data source supports (and delete any it doesn't)
	 * @see org.openmrs.logic.rule.provider.RuleProvider#afterStartup()
	 */
	@Override
	public void afterStartup() {
		TokenService service = Context.getService(TokenService.class);
		
		// delete any keys we'd previously registered, but no longer support
		service.keepOnlyValidConfigurations(this, getKeys());

		// register all current keys
		for (String key : getKeys()) {
			service.registerToken(key, this, key);
		}		
	}
	
}
