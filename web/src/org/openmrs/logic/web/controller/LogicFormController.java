package org.openmrs.logic.web.controller;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Status;
import org.apache.commons.lang.StringUtils;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.cache.ehcache.LogicCacheManagerTMP;
import org.openmrs.logic.result.Result;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
public class LogicFormController {

	/**
	 * Provides auto-complete functionality via JQuery/AJAX the role field
	 *
	 * @param token The full or partial logic rule token
	 * @param model The ModelMap to be used by view to render page
	 */
	@RequestMapping("/module/logic/tokens")
	public void tokenAutoComplete(@RequestParam("q") String token, ModelMap model) {
		if (Context.hasPrivilege("View Administration Functions")) {
			LogicService logicService = Context.getLogicService();
			List<String> tokens = logicService.getTokens(token);
			Collections.sort(tokens);
			model.addAttribute("listOutput", tokens);
		} else {
			model.addAttribute("listOutput", new ArrayList<String>().add(Context.getMessageSourceService().getMessage(
			    "logic.tester.error.auth")));
		}
	}

	/**
	 * Place holder for the logic tester form
	 *
	 * @param modelMap The ModelMap to be used by view to render page
	 */
	@RequestMapping(value = "/module/logic/logic", method = RequestMethod.GET)
	public void showTestPage(@RequestParam(required = false, value = "patientId") Integer patientId, ModelMap modelMap) {
		modelMap.addAttribute("authenticatedUser", Context.getAuthenticatedUser());
		modelMap.addAttribute("patientId", patientId == null ? 0 : patientId.intValue());

		if (patientId != null && patientId.intValue() > 0) {
			Patient patient = Context.getPatientService().getPatient(patientId);
			modelMap.addAttribute("patient", patient);
		}
	}

	/**
	 * Runs the logic test using the LogicService
	 *
	 * @param patientId The ID of the patient
	 * @param logicRule The logic rule token
	 * @param modelMap The ModelMap to be used by view to render page
	 * @throws Exception
	 */
	@RequestMapping("/module/logic/run")
	public void runTest(@RequestParam(required = false, value = "patientId") Integer patientId,
	                    @RequestParam(required = false, value = "patientIdentifier") String patientIdentifier,
	                    @RequestParam(required = false, value = "patientName") String patientName,
	                    @RequestParam("logicRule") String logicRule, ModelMap modelMap) throws Exception {

		if (patientId > 0 && logicRule != null && logicRule.length() > 0) {
			try {
				Patient patient = Context.getPatientService().getPatient(patientId);

				LogicService logicService = Context.getLogicService();

				Result result = logicService.eval(patient, logicService.parse(logicRule));

				modelMap.addAttribute("patient", patient);
				modelMap.addAttribute("logicRule", logicRule);
				modelMap.addAttribute("result", result);
			}
			catch (LogicException e) {
				modelMap.addAttribute("error", "Invalid Logic Rule.");
			}
			catch (Exception e) {
				modelMap.addAttribute("error", e.toString());
				modelMap.addAttribute("detail", exception2String(e));
			}

			modelMap.addAttribute("patientId", patientId.intValue());
			modelMap.addAttribute("patientIdentifier", patientIdentifier);
			modelMap.addAttribute("patientName", patientName);

		} else {
			modelMap.addAttribute("error", "Invalid parameters");
		}
	}

    @RequestMapping("/module/logic/cache")
	public void handlePath(@RequestParam(required = false, value = "action") String action, ModelMap modelMap) throws Exception {
//        CacheManager cacheManager = CacheManager.getInstance();
        CacheManager cacheManager = LogicCacheManagerTMP.getOrCreate();
        String []cacheNames = null;
        String status = "", cacheName = "", cacheStat = "", cacheDir = "", diskStoreSize = "", cacheToStr = "";


        if(null != cacheManager) {
            cacheNames = cacheManager.getCacheNames();
            cacheDir = cacheManager.getDiskStorePath();
        }

        Cache cache = LogicCacheManagerTMP.getLogicEhCache();

        if(null != cache) {
            if(!StringUtils.isBlank(action) && Status.STATUS_ALIVE.equals(cache.getStatus())) {
                cache.evictExpiredElements();//cache.flush();
            }
            status = cache.getStatus().toString();
            cacheName = cache.getName();
            cacheStat = cache.getStatistics().toString();
            diskStoreSize = Integer.toString(cache.getDiskStoreSize());

            cacheToStr = cache.toString();
        }

		modelMap.addAttribute("cacheNames", cacheNames);
        modelMap.addAttribute("cachesCount", cacheNames != null ? cacheNames.length : "-");

        modelMap.addAttribute("status", status);
        modelMap.addAttribute("cacheName", cacheName);
        modelMap.addAttribute("cacheStat", cacheStat);
        modelMap.addAttribute("cacheDir", cacheDir);
        modelMap.addAttribute("diskStoreSize", diskStoreSize);

        modelMap.addAttribute("cacheToStr", cacheToStr);
	}

	/***********************************************************************************************************
	 * Formats exception into a printable string
	 * 
	 * @param exception Exception
	 * @return Formated String Stack Trace
	 */
	private String exception2String(Exception exception) {
		try {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			exception.printStackTrace(pw);
			return "------\r\n" + sw.toString() + "------\r\n";
		}
		catch (Exception e2) {
			return "Error parsing exception2String";
		}
	}
}
