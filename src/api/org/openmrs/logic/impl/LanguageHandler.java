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
package org.openmrs.logic.impl;

import org.openmrs.logic.LogicRule;
import org.openmrs.logic.Rule;

/**
 * Root interface for all LogicRule language handlers. The contract for this class is all LogicRules
 * must be converted to a Rule object. Implementing classes can define their own ways to create
 * the Rule object and register the class for future use.
 */
public interface LanguageHandler {
	
	/**
	 * Handle the LogicRule. This handler will control how the Rule object will be created.
	 * Different types of language can register their own language handler.
	 * 
	 * @param logicRule the LogicRule that will be processed
	 * @return the rule object or null if no rule object can be created for the LogicRule
	 */
	Rule handle(LogicRule logicRule);
	
}
