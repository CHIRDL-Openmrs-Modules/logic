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

import org.openmrs.ConceptDerived;
import org.openmrs.logic.Rule;

/**
 * Root class for all concept derived's language handler. The contract for this class is all concept
 * derived must be converted to a Rule object. Implementing class can define their own way to create
 * the Rule object and register the class for future use.
 */
public interface LanguageHandler {
	
	/**
	 * Handle the concept derived. This handler will control how Rule object will be created.
	 * Different type of concept derived's language can register their own language handler.
	 * 
	 * @param conceptDerived the concept derived that will be processed
	 * @return the rule object or null if no rule object can be created for the concept derived
	 */
	Rule handle(ConceptDerived conceptDerived);
	
}
