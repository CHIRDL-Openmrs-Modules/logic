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

import java.util.List;

import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicRuleService;
import org.openmrs.logic.impl.LanguageHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * Controller for listing, creating, and editing user-defined rules
 */
@Controller
public class ManageLogicRulesController {
	
	@RequestMapping(value="/module/logic/manageLogicRules")
	public void listLogicRules(Model model) {
		model.addAttribute("rules", Context.getService(LogicRuleService.class).getAllLogicRules(true));
	}

}
