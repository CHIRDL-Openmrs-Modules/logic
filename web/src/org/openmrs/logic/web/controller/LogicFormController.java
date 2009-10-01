package org.openmrs.logic.web.controller;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicCriteriaImpl;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LogicFormController {
	
	@RequestMapping("/module/logic/logic")
	public void showLogicResult(ModelMap model) {
		
		model.addAttribute("authenticatedUser", Context.getAuthenticatedUser());

		try {
			// get a simple rule for testing
			
			LogicService logicService = Context.getLogicService();
	        Rule rule = logicService.getRule("%%person.birthdate");
	        
	        // register the rule
	        logicService.addRule("birthdate", rule);
	        
	        // get a sample person
	        Patient patient = Context.getPatientService().getPatient(2);
	        
	        Result result = logicService.eval(patient, new LogicCriteriaImpl("birthdate"));
	        
	        model.addAttribute("result", result);
        	model.addAttribute("error", "-N/A-");
        }
        catch (LogicException e) {
        	model.addAttribute("error", "Failed to load birthdate rule ...");
        }
	}
}
