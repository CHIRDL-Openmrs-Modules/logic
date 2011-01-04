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
package org.openmrs.logic.rule.definition;

import org.openmrs.logic.Rule;

/**
 * Interface that defines the capability to compile a {@link RuleDefinition} into a {@link Rule}.
 * TODO see if we can replace this with ScriptEngine/Compilable from JSR-223 in Java 6
 */
public interface LanguageHandler {
	
	/**
     * @return a unique name that identifies this language handler
     */
    String getName();
    
    /**
	 * Handle the {@link RuleDefinition}. This handler will control how the Rule object will be created.
	 * Different types of language can register their own language handler.
	 * 
	 * @param ruleDefinition the {@link RuleDefinition} that will be processed
	 * @return the rule object or null if no rule object can be created for the {@link RuleDefinition}
	 */
	Rule compile(RuleDefinition ruleDefinition);
	
}
