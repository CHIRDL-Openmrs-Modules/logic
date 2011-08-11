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
package org.openmrs.module.logic;

import java.util.LinkedHashMap;
import java.util.Map;

import org.openmrs.module.Extension;
import org.openmrs.module.web.extension.AdministrationSectionExt;

/**
 * Links on the Admin page
 */
public class AdminList extends AdministrationSectionExt {
	
	public Extension.MEDIA_TYPE getMediaType() {
		return Extension.MEDIA_TYPE.html;
	}
	
	public String getTitle() {
		return "Logic Module";
	}
	
	public Map<String, String> getLinks() {
		Map<String, String> localHashMap = new LinkedHashMap<String, String>();
		localHashMap.put("/module/logic/manageTokens.list", "logic.token.manage.title");
		localHashMap.put("/module/logic/manageRuleDefinitions.list", "logic.rule.manage.title");
		localHashMap.put("/module/logic/logic.form", "logic.tester.title");
		localHashMap.put("/module/logic/init.form", "logic.init.title");
		return localHashMap;
	}
}