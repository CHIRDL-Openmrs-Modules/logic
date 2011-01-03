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
import java.util.Iterator;
import java.util.List;

import org.openmrs.api.context.Context;
import org.openmrs.logic.TokenService;
import org.openmrs.logic.token.TokenRegistration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Controller for listing and editing tokens
 */
@Controller
public class ManageTokensController {

	@RequestMapping("/module/logic/manageTokens")
	public void listTokens() {
		// do nothing -- tokens will be shown via ajax
	}
	
	@RequestMapping("/module/logic/listTokensQuery")
	public String listTokensQuery(@RequestParam(value="sSearch", required=false) String query,
	                              @RequestParam(value="iDisplayStart", required=false) Integer start,
	                              @RequestParam(value="iDisplayLength", required=false) Integer length,
	                              @RequestParam(value="sEcho", required=false) String echo,
	                              Model model) {
		if (echo == null)
			echo = "0";
		TokenService service = Context.getService(TokenService.class);
		List<TokenRegistration> results = service.getTokenRegistrations(query, start, length);
		Integer count = service.getCountOfTokenRegistrations(query); // TODO skip if all results returned 
		
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"sEcho\": " + echo);
		sb.append(", \"iTotalRecords\": \"?\"");
		sb.append(", \"iTotalDisplayRecords\": " + count);
		sb.append(", \"aaData\": [");
		for (Iterator<TokenRegistration> i = results.iterator(); i.hasNext(); ) {
			TokenRegistration r = i.next();
			List<Object> list = new ArrayList<Object>();
			list.add("");
			list.add(r.getToken());
			list.add(r.getProviderClassName());
			list.add(r.getConfiguration());
			list.add(r.getId());
			sb.append("[");
			for (Iterator<Object> j = list.iterator(); j.hasNext(); ) {
				Object o = j.next();
				if (o instanceof Number)
					sb.append(o);
				else
					sb.append("\"" + o + "\"");
				if (j.hasNext())
					sb.append(", ");
			}
			sb.append("]");
			if (i.hasNext())
				sb.append(", ");
		}
		sb.append("]");
		sb.append("}");
		model.addAttribute("output", sb);
		return "/module/logic/output";
	}

	
}
