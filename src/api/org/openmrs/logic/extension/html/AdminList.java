package org.openmrs.logic.extension.html;

import java.util.LinkedHashMap;
import java.util.Map;

import org.openmrs.module.Extension;
import org.openmrs.module.web.extension.AdministrationSectionExt;

public class AdminList extends AdministrationSectionExt {
	
	public Extension.MEDIA_TYPE getMediaType() {
		return Extension.MEDIA_TYPE.html;
	}
	
	public String getTitle() {
		return "Logic Module";
	}
	
	public Map<String, String> getLinks() {
		Map<String, String> localHashMap = new LinkedHashMap<String, String>();
		localHashMap.put("/module/logic/init.form", "logic.init.title");
		localHashMap.put("/module/logic/logic.form", "logic.start.title");
		return localHashMap;
	}
}
