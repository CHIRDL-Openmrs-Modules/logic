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
package org.openmrs.logic.web.controller;

import org.openmrs.api.context.Context;
import org.openmrs.logic.rule.definition.RuleDefinitionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * Controller for listing user-defined rules
 */
@Controller
public class ManageRuleDefinitionsController {
	
	@RequestMapping(value="/module/logic/manageRuleDefinitions")
	public void listLogicRules(Model model) {
		model.addAttribute("rules", Context.getService(RuleDefinitionService.class).getAllRuleDefinitions(true));
	}

}
