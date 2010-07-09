package org.openmrs.logic.web.controller;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.cache.LogicCache;
import org.openmrs.logic.cache.LogicCacheConfig;
import org.openmrs.logic.cache.LogicCacheConfigBean;
import org.openmrs.logic.cache.LogicCacheManager;
import org.openmrs.logic.result.Result;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

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
        Collection<Cohort> cohorts = Context.getCohortService().getAllCohorts();
		modelMap.addAttribute("existingCohorts", cohorts);

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
	public void runTest(@RequestParam(value = "step1Type") String step1Type,
                        @RequestParam(required = false, value = "cohortId") String cohortId,
                        @RequestParam(required = false, value = "patientId") Integer patientId,
	                    @RequestParam(required = false, value = "patientIdentifier") String patientIdentifier,
	                    @RequestParam(required = false, value = "patientName") String patientName,
	                    @RequestParam("logicRule") String logicRule, ModelMap modelMap) throws Exception {

		if ((patientId > 0 || !StringUtils.isEmpty(cohortId)) && logicRule != null && logicRule.length() > 0) {
			try {
                Patient patient = null;
                if(patientId > 0)
				    patient = Context.getPatientService().getPatient(patientId);

                LogicService logicService = Context.getLogicService();
                Map<Integer, Result> mapResult = null;
				Result result = null;

                if("patient".equals(step1Type)) {
                    result = logicService.eval(patient, logicService.parse(logicRule));
                    modelMap.addAttribute("result", result);
                } else if("cohort".equals(step1Type)) {
                    Cohort cohort = Context.getCohortService().getCohort(cohortId);
                    mapResult = logicService.eval(cohort, logicService.parse(logicRule));
                    modelMap.addAttribute("cohortName", cohortId);
                    modelMap.addAttribute("cohortMap", mapResult != null ? mapResult.entrySet() : null);
                }
                
				modelMap.addAttribute("patient", patient);
				modelMap.addAttribute("logicRule", logicRule);
			}
			catch (LogicException e) {
				modelMap.addAttribute("error", "Invalid Logic Rule.");
			}
			catch (Exception e) {
				modelMap.addAttribute("error", e.toString());
				modelMap.addAttribute("detail", exception2String(e));
			}

            modelMap.addAttribute("patientId", patientId);
            modelMap.addAttribute("patientIdentifier", patientIdentifier);
            modelMap.addAttribute("patientName", patientName);

		} else {
			modelMap.addAttribute("error", "Invalid parameters");
		}
	}

    @RequestMapping("/module/logic/caches")
	public void showCaches(@RequestParam(required = false, value = "action") String action,
                           @RequestParam(required = false, value = "cacheName") String cacheName,
                           ModelMap modelMap) throws Exception {

        if(!StringUtils.isEmpty(action)) {
            LogicCache logicCache = null;
            if(!StringUtils.isEmpty(cacheName))
                logicCache = LogicCacheManager.getLogicCache(cacheName);

            if ("shutdown".equals(action)) {
                LogicCacheManager.shutDown();
            } else if ("flush".equals(action) && null != logicCache && logicCache.getFeature(LogicCache.Features.FLUSH)) {
                logicCache.flush();
            } else if ("clear".equals(action) && null != logicCache) {
                logicCache.clean();
            }
        }

        //creating cache if it isn`t
        LogicCacheManager.getDefaultLogicCache();
        LogicCacheManager.getLogicCache("org.openmrs.logic.criteriaCache");

        Collection<String> cacheNames = LogicCacheManager.getCacheNames();

    	modelMap.addAttribute("cacheNames", cacheNames);
        modelMap.addAttribute("cachesCount", cacheNames != null ? cacheNames.size() : "0");
	}

    @RequestMapping("/module/logic/cache")
    public void manageCache(@RequestParam(required = false, value = "cacheName") String cacheName,
                            @RequestParam(required = false, value = "maxElemInMem") Integer maxElemInMem,
                            @RequestParam(required = false, value = "maxElemOnDisk") Integer maxElemOnDisk,
                            @RequestParam(required = false, value = "defaultTTL") Long defaultTTL,
                            @RequestParam(required = false, value = "diskPersistence") Boolean diskPersistence,
                            @RequestParam(required = false, value = "restart") Boolean restart,
                            ModelMap modelMap) throws Exception {

        LogicCache logicCache;
        if (!StringUtils.isEmpty(cacheName))
            logicCache = LogicCacheManager.getLogicCache(cacheName);
        else {
            logicCache = LogicCacheManager.getDefaultLogicCache();
            cacheName = logicCache.getName();
        }

        if(null != restart && restart && logicCache.getFeature(LogicCache.Features.RESTART)) {
            logicCache = logicCache.restart();
        }

        LogicCacheConfig logicCacheConfig = logicCache.getLogicCacheConfig();
        String cacheHits, cacheMisses, cacheToStr, cacheSize;

        if (null != maxElemInMem || null != maxElemOnDisk || null != defaultTTL || null != diskPersistence) {
            if (null != maxElemInMem && maxElemInMem >= 0 && logicCacheConfig.getFeature(LogicCacheConfig.Features.MAX_ELEMENTS_IN_MEMORY))
                logicCacheConfig.setMaxElementsInMemory(maxElemInMem);

            if (null != maxElemOnDisk && maxElemOnDisk >= 0 && logicCacheConfig.getFeature(LogicCacheConfig.Features.MAX_ELEMENTS_ON_DISK))
                logicCacheConfig.setMaxElementsOnDisk(maxElemOnDisk);

            if (null != defaultTTL && defaultTTL > 0 && logicCacheConfig.getFeature(LogicCacheConfig.Features.DEFAULT_TTL))
                logicCacheConfig.setDefaultTTL(defaultTTL);

            if (null != diskPersistence && logicCacheConfig.getFeature(LogicCacheConfig.Features.USING_DISK_STORE) && !diskPersistence.equals(logicCacheConfig.getUsingDiskStore()))
                logicCacheConfig.setUsingDiskStore(diskPersistence);

            logicCache.storeConfig();
        }

        cacheSize = Integer.toString(logicCache.getSize());
        LogicCacheConfigBean configBean = logicCacheConfig.getConfigBean();

        if(logicCacheConfig.getFeature(LogicCacheConfig.Features.DEFAULT_TTL))
            modelMap.addAttribute("configTTL", configBean.getDefaultTTL().toString());
        if(logicCacheConfig.getFeature(LogicCacheConfig.Features.MAX_ELEMENTS_IN_MEMORY))
            modelMap.addAttribute("configMaxElInMem", configBean.getMaxElementsInMemory());
        if(logicCacheConfig.getFeature(LogicCacheConfig.Features.MAX_ELEMENTS_ON_DISK))
            modelMap.addAttribute("configMaxElOnDisk", configBean.getMaxElementsOnDisk());
        if(logicCacheConfig.getFeature(LogicCacheConfig.Features.USING_DISK_STORE))
            modelMap.addAttribute("diskPersistence", configBean.isUsingDiskStore());

        modelMap.addAttribute("cacheRestart", logicCache.getFeature(LogicCache.Features.RESTART));
        modelMap.addAttribute("cacheName", cacheName);
        modelMap.addAttribute("cacheSize", cacheSize);
        modelMap.addAttribute("isRestartNeeded", logicCacheConfig.isRestartNeeded());

        ///////////////////////TODO: delete later
        cacheHits = logicCacheConfig.getCacheHits().toString();
        cacheMisses = logicCacheConfig.getCacheMisses().toString();
        cacheToStr = logicCache.getCacheSpecificStats();
        
        modelMap.addAttribute("cacheHits", cacheHits);
        modelMap.addAttribute("cacheMisses", cacheMisses);
        modelMap.addAttribute("cacheToStr", cacheToStr);
        ///////////////////////
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
