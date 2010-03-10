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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptName;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.LogicTransform;
import org.openmrs.logic.Rule;
import org.openmrs.logic.datasource.LogicDataSource;
import org.openmrs.logic.op.Operator;
import org.openmrs.logic.result.EmptyResult;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.rule.AgeRule;
import org.openmrs.logic.rule.HIVPositiveRule;
import org.openmrs.logic.rule.InvalidReferenceRuleException;
import org.openmrs.logic.rule.ReferenceRule;
import org.springframework.util.StringUtils;

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
		
		Map<String, LogicDataSource> dataSources = Context.getLogicService().getLogicDataSources();
		
		// Register tokens for Reference Rules based on available LogicDataSources
		
		for (String dataSourceName : dataSources.keySet()) {
			LogicDataSource dataSource = dataSources.get(dataSourceName);
			
			if ("obs".equalsIgnoreCase(dataSourceName)) {
				int counter = 0;
				
				// Register Tokens for all Concepts in specified classes
				List<ConceptClass> conceptClasses = new ArrayList<ConceptClass>();
				String classProp = Context.getAdministrationService()
				        .getGlobalProperty("logic.defaultTokens.conceptClasses");
				if (StringUtils.hasText(classProp)) {
					for (String className : classProp.split(",")) {
						conceptClasses.add(Context.getConceptService().getConceptClassByName(className));
					}
				} else {
					conceptClasses = Context.getConceptService().getAllConceptClasses();
				}
				
				Locale conceptNameLocale = Locale.US;
				String localeProp = Context.getAdministrationService().getGlobalProperty(
				    "logic.defaultTokens.conceptNameLocale");
				if (localeProp != null) {
					conceptNameLocale = new Locale(localeProp);
				}
				
				for (ConceptClass currClass : conceptClasses) {
					for (Concept c : Context.getConceptService().getConceptsByClass(currClass)) {
						if (!c.getDatatype().isAnswerOnly()) {
							ConceptName conceptName = c.getPreferredName(conceptNameLocale);
							if (conceptName != null && dataSource.hasKey(conceptName.getName())) {
								Rule r = new ReferenceRule(dataSourceName + "." + conceptName.getName());
								registerRule(conceptName.getName(), r);
								++counter;
								if (counter > 50) {
									counter = 0;
									Context.flushSession();
								}
							}
						}
					}
				}
			} else {
				for (String key : dataSource.getKeys()) {
					Rule r = new ReferenceRule(dataSourceName + "." + key);
					registerRule(key, r);
				}
			}
		}
		
		// Register tokens for additional Rule classes
		registerRule("HIV POSITIVE", new HIVPositiveRule());
		registerRule("AGE", new AgeRule());
	}
	
	private static void registerRule(String token, Rule rule) {
		try {
			Context.getLogicService().addRule(token.toUpperCase(), rule);
		}
		catch (LogicException e) {
			log.debug("Rule with token <" + token.toUpperCase() + "> already in RuleMap.  Ignoring: " + rule);
		}
	}
}
