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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.api.context.Context;
import org.openmrs.logic.token.TokenRegistration;
import org.openmrs.logic.token.TokenService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * Controller for listing tokens
 */
@Controller
public class ManageTokensController {

	@RequestMapping("/module/logic/manageTokens")
	public void listTokens() {
		// do nothing -- tokens will be shown via ajax
	}
	
	@RequestMapping("/module/logic/listTokensQuery")
	public @ResponseBody Map<String, Object> listTokensQuery(@RequestParam(value="sSearch", required=false) String query,
	                              @RequestParam(value="iDisplayStart", required=false) Integer start,
	                              @RequestParam(value="iDisplayLength", required=false) Integer length,
	                              @RequestParam(value="sEcho", required=false) String echo) {
		if (echo == null) {
			echo = "0";
		}
		
		TokenService service = Context.getService(TokenService.class);
		List<TokenRegistration> tokens = service.getTokenRegistrations(query, start, length);
		Long count = service.getCountOfTokenRegistrations(query); // TODO skip if all results returned 
		
		// form the results dataset
		List<Object> results = new ArrayList<Object>();
		for (TokenRegistration token : tokens) {
			results.add(splitTokenRegistration(token));
		}
		
		// build the response
		Map<String, Object> response = new HashMap<String, Object>();
		response.put("iTotalRecords", service.getCountOfTokenRegistrations(null));
		response.put("iTotalDisplayRecords", count);
		response.put("sEcho", echo);
		response.put("aaData", results.toArray());
		
		// send it
		return response;
	}

	/**
	 * Create an object array for a given TokenRegistration
	 *
	 * @param token TokenRegistration object
	 * @return object array for use with datatables
	 */
	private Object[] splitTokenRegistration(TokenRegistration token) {
		// try to stick to basic types; String, Integer, etc (not Date)
		return new Object[] { "", token.getToken(), token.getProviderClassName(),
		        token.getConfiguration(), token.getId() };
	}
}
