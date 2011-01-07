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

import java.util.Date;

import org.openmrs.logic.Rule;

/**
 * A RuleProvider is able to take registered tokens, and instantiate rules from them.
 * When you implement this interface you should extend {@link AbstractRuleProvider} or a 
 * subclass so you are protected from possible future changed to this interface.
 * (Specifically, you should expect more event callbacks to be added to this interface
 * in the future.)
 */
public interface RuleProvider {
	
	/**
     * Instantiates a rule, based on the provided configuration. This method is expected to be
     * relatively expensive, so the returned rule may be cached by the logic infrastructure.
     * 
     * @param configuration
     * @return
     */
    Rule getRule(String configuration);
    
    /**
     * If the *definition* of your rules change over time (for example your rule is a script whose
     * source code is in a database table), then you need to implement this method.
     * 
     * If your rules do not change, you should always return false for this method.
     * 
     * @param configuration
     * @param sinceDate
     * @return whether the rule specified by configuration has changed since sinceDate
     */
    boolean hasRuleChanged(String configuration, Date sinceDate);

	/**
     * Will be called by the logic service after all its machinery is started up.
     */
    void afterStartup();

}
