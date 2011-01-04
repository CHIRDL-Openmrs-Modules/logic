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
import org.openmrs.logic.LogicRule;
import org.openmrs.logic.LogicRuleService;
import org.openmrs.logic.TokenService;
import org.openmrs.logic.impl.LanguageHandler;
import org.openmrs.logic.rule.LogicRuleValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Edit a user-defined logic rule
 */
@Controller
public class EditLogicRuleController {
	
	@ModelAttribute("ruleLanguages")
	public List<LanguageHandler> getRuleLanguages() {
		return Context.getService(LogicRuleService.class).getAllLanguageHandlers();
	}
	
	@ModelAttribute("rule")
	LogicRule getRule(@RequestParam(required=false, value="id") Integer id) {
		if (id != null) {
			return Context.getService(LogicRuleService.class).getLogicRule(id);
		} else {
			return new LogicRule();
		}
	}
	
	@RequestMapping(value="/module/logic/editLogicRule.form", method=RequestMethod.GET)
	public void showEditRulePage() {
	}

	@RequestMapping(value="/module/logic/editLogicRule.form", method=RequestMethod.POST)
	public String doEditRulePage(@ModelAttribute("rule") LogicRule rule,
	                             Errors errors,
	                             Model model) {
		new LogicRuleValidator().validate(rule, errors);
		if (errors.hasErrors()) {
			model.addAttribute("rule", rule);
			return null;
		} else {
			Context.getService(LogicRuleService.class).saveLogicRule(rule);
			return "redirect:manageLogicRules.list";
		}
	}
	
	@RequestMapping("/module/logic/deleteLogicRule.form")
	public String deleteRule(@ModelAttribute("rule") LogicRule rule) {
		Context.getService(LogicRuleService.class).purgeLogicRule(rule);
		return "redirect:manageLogicRules.list";
	}

}
