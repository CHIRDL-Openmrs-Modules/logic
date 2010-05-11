package org.openmrs.logic.web.controller;

import org.openmrs.api.context.Context;
import org.openmrs.logic.init.InitStatusImpl;
import org.openmrs.logic.init.JSONWriter;
import org.openmrs.logic.init.ProcessStatus;
import org.openmrs.logic.util.LogicUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LogicInitController {
	
	private JSONWriter jsonWriter = new JSONWriter();
	
	private int isInitRunning = ProcessStatus.STATUS_OFF;
	
	/**
	 * Place holder for the default logic rule registration form 
	 */
	@RequestMapping("/module/logic/init")
	public void initialize() {}

	/**
	 * Returns the current status of the default logic rule registration process
	 * 
	 * @param model The ModelMap to be used by view to render page
	 */
	@RequestMapping("/module/logic/status")
	public void initStatus(ModelMap model) {
		ProcessStatus processStatus = new InitStatusImpl();
		processStatus.setStatus(isInitRunning);
		model.addAttribute("jsonOutput", jsonWriter.write(processStatus));
	}
	
	/**
	 * Runs LogicUtil.registerDefaultRules (called via JQuery/AJAX)
	 */
	@RequestMapping("/module/logic/load")
	public void runInit() {
		if (Context.hasPrivilege("View Administration Functions")) {
			isInitRunning = ProcessStatus.STATUS_ON;
			LogicUtil.registerDefaultRules();
			isInitRunning = ProcessStatus.STATUS_OFF;
		}
	}
	
}
