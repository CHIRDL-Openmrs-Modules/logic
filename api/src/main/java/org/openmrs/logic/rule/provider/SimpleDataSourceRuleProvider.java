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

import org.openmrs.logic.rule.ReferenceRule;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 * A helper class for {@link RuleProvider}s that want to register all their keys at startup, as
 * {@link ReferenceRule}s, with preferred tokens identical to the keys.
 * (E.g. prefix="person", key="birthdate" -> token="birthdate", reference rule="person.birthdate") 
 */
public abstract class SimpleDataSourceRuleProvider extends RegisterAtStartupDataSourceRuleProvider {
	
	/**
	 * @return a complete set of the keys this data source supports
	 */
	public abstract Collection<String> getKeys();
	
	/**
	 * @see org.openmrs.logic.rule.provider.RegisterAtStartupDataSourceRuleProvider#getAllKeysAndTokens()
	 */
	@Override
	public Map<String, String> getAllKeysAndTokens() {
		// TODO, find an implementation of an identity map in some collections package
		Map<String, String> ret = new HashMap<String, String>();
		for (String key : getKeys())
			ret.put(key, key);
		return ret;
	}
	
}
