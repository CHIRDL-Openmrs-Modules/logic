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
import org.openmrs.logic.token.TokenRegistration;
import org.openmrs.logic.token.TokenRegistrationValidator;
import org.openmrs.logic.token.TokenService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Controller for viewing and editing a single TokenRegistration
 */
@Controller
public class EditTokenRegistrationController {
	
	@ModelAttribute("tokenRegistration")
	TokenRegistration getTokenRegistration(@RequestParam(required=false, value="id") Integer id) {
		if (id != null) {
			return Context.getService(TokenService.class).getTokenRegistration(id);
		} else {
			return new TokenRegistration();
		}
	}
	
	@RequestMapping(value="/module/logic/editTokenRegistration.form", method=RequestMethod.GET)
	public void showEditTokenRegistrationPage() {
	}
	
	@RequestMapping(value="/module/logic/editTokenRegistration.form", method=RequestMethod.POST)
	public String doEditTokenRegistration(@ModelAttribute("tokenRegistration") TokenRegistration tokenRegistration,
	   	                             	Errors errors,
	   	                             	Model model) {
		new TokenRegistrationValidator().validate(tokenRegistration, errors);
		if (errors.hasErrors()) {
			return null;
		} else {
			Context.getService(TokenService.class).saveTokenRegistration(tokenRegistration);
			return "redirect:manageTokens.list";
		}
	}
	
	@RequestMapping("/module/logic/deleteToken.form")
	public String deleteTokenRegistration(@ModelAttribute("tokenRegistration") TokenRegistration tokenRegistration) {
		Context.getService(TokenService.class).deleteTokenRegistration(tokenRegistration);
		return "redirect:manageTokens.list";
	}

}
