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
package org.openmrs.logic.util;

import java.io.File;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.LogicTransform;
import org.openmrs.logic.op.Operator;
import org.openmrs.logic.result.EmptyResult;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.rule.AgeRule;
import org.openmrs.logic.rule.HIVPositiveRule;
import org.openmrs.logic.rule.InvalidReferenceRuleException;
import org.openmrs.logic.rule.provider.ClassRuleProvider;
import org.openmrs.logic.task.InitializeLogicRuleProvidersTask;
import org.openmrs.logic.token.TokenService;
import org.openmrs.scheduler.SchedulerException;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.util.OpenmrsConstants;

/**
 * 
 */
public class LogicUtil {
	
	private static final Log log = LogFactory.getLog(LogicUtil.class);
	
	/**
	 * Programmatically applies aggregators like COUNT, AVERAGE, etc
	 * 
	 * @param finalResult result map of patient id to result list
	 * @param criteria provides type of transform
	 */
	public static void applyAggregators(Map<Integer, Result> finalResult, LogicCriteria criteria, Cohort patients) {
		Set<Integer> personIds = finalResult.keySet();
		LogicTransform transform = criteria.getExpression().getTransform();
		
		// finalResult is empty so populate it with empty counts/averages
		if (personIds.size() == 0) {
			for (Integer personId : patients.getMemberIds()) {
				if (transform != null && transform.getTransformOperator() == Operator.COUNT) {
					Result newResult = new Result();
					newResult.add(new Result(0));
					finalResult.put(personId, newResult);
				} else if (transform != null && transform.getTransformOperator() == Operator.AVERAGE) {
					Result newResult = Result.emptyResult();
					finalResult.put(personId, newResult);
				}
			}
			return;
		}
		
		for (Integer personId : personIds) {
			// if this was a count, then return the actual count of results
			// instead of the objects
			
			Result r = finalResult.get(personId);
			if (transform != null && transform.getTransformOperator() == Operator.COUNT) {
				Result newResult = new Result();
				newResult.add(new Result(r.size()));
				finalResult.put(personId, newResult);
			} else if (transform != null && transform.getTransformOperator() == Operator.AVERAGE) {
				int count = 0;
				double sum = 0;
				for (Result currResult : r) {
					if (!(currResult instanceof EmptyResult)) {
						count++;
						sum += currResult.toNumber();
					}
				}
				double average = 0;
				if (count > 0 && sum > 0) {
					average = sum / count;
				}
				Result newResult = new Result();
				newResult.add(new Result(average));
				finalResult.put(personId, newResult);
			}
		}
	}
	
	/**
	 * Initialize global settings. Load default rules at startup, creating if necessary
	 * 
	 * @param p properties from runtime configuration
	 */
	public static void registerDefaultRules() throws InvalidReferenceRuleException {
		ClassRuleProvider crp = new ClassRuleProvider();
		Context.getService(TokenService.class).registerToken("AGE", crp, AgeRule.class.getName());
		Context.getService(TokenService.class).registerToken("HIV POSITIVE", crp, HIVPositiveRule.class.getName());

		Context.getService(TokenService.class).initialize();
	}
	
	/**
	 * Generic method to fork a new command for the operating system to run.
	 * 
	 * @param commands the command and the parameters
	 * @param workingDirectory the directory where the commands should be invoked
	 * @return true when the command executed succesfully.
	 */
	public static final boolean executeCommand(String[] commands, File workingDirectory) {
		
		boolean executed = false;
		try {
			Runtime runtime = Runtime.getRuntime();
			Process process = null;
			if (OpenmrsConstants.UNIX_BASED_OPERATING_SYSTEM)
				process = runtime.exec(commands, null, workingDirectory);
			else
				process = runtime.exec(commands);
			
			ExecutorService executorService = Executors.newCachedThreadPool();
			
			StreamHandler errorHandler = new StreamHandler(process.getErrorStream(), "ERROR");
			StreamHandler outputHandler = new StreamHandler(process.getInputStream(), "OUTPUT");
			
			if (executorService == null)
				executorService = Executors.newCachedThreadPool();
			
			executorService.execute(errorHandler);
			executorService.execute(outputHandler);
			
			int exitValue = process.waitFor();
			log.info("Process execution completed with exit value: " + exitValue + " ...");
			
			executed = true;
		}
		catch (Exception e) {
			log.error("Error generated", e);
		}
		
		return executed;
	}

	/**
     * This is a hacky way of making sure we can run something Context-sensitive, as a superuser, after
     * module startup, in 1.6 and later.
     */
    public static void initialize() {
    	try {
    		// use proxy privileges for 1.6.x compatibility (starting in 1.7.x module startup and
    		// scheduled tasks are run as the daemon user)
    		// Context.addProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_SCHEDULER); // TODO CHICA-1151 Commenting this out, not sure what to do here yet
    		
			TaskDefinition def = Context.getSchedulerService().getTaskByName(InitializeLogicRuleProvidersTask.NAME);
			if (def == null) {
				def = new TaskDefinition();
				def.setName(InitializeLogicRuleProvidersTask.NAME);
				def.setTaskClass(InitializeLogicRuleProvidersTask.class.getName());
				def.setStartOnStartup(false);
				def.setStarted(true);
			}
	
			def.setStartTime(new Date(System.currentTimeMillis() + 30000));
			// in 1.6.x it's impossible to schedule a task to run just once, but not right now. Instead we set a very large repeat interval.
			def.setRepeatInterval(1999999999l);
			try {
				if (def.getUuid() == null) {
					// manual workaround for a bug in 1.6.x
					def.setUuid(UUID.randomUUID().toString());
				}
				Context.getSchedulerService().scheduleTask(def);
			} catch (SchedulerException ex) {
				log.error("Error scheduling logic initialization task at startup", ex);
			}
			
		} finally {
			// Context.removeProxyPrivilege(OpenmrsConstants.PRIV_MANAGE_SCHEDULER); // TODO CHICA-1151 Commenting this out, not sure what to do here yet
		}
    }
}
