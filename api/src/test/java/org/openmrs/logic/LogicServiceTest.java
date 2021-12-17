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
package org.openmrs.logic;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.logic.impl.LogicCriteriaImpl;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.rule.AgeRule;
import org.openmrs.logic.rule.ReferenceRule;
import org.openmrs.logic.rule.provider.ClassRuleProvider;
import org.openmrs.logic.token.TokenService;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;

/**
 * TODO: add more tests to this test case
 */
public class LogicServiceTest extends BaseModuleContextSensitiveTest {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * @see org.springframework.test.AbstractTransactionalSpringContextTests#onSetUpInTransaction()
	 */
	@Before
	public void runBeforeEachTest() throws Exception {
		executeDataSet("org/openmrs/logic/include/LogicStandardDatasets.xml");
		executeDataSet("org/openmrs/logic/include/LogicTests-patients.xml");
		executeDataSet("org/openmrs/logic/include/LogicBasicTest.concepts.xml");
	}
	
	/**
	 * TODO make this test use assert statements instead of printing to stdout
	 */
	@Test
	public void shouldObservationRule() {
		LogicService logicService = Context.getLogicService();
		Cohort patients = new Cohort();
		Map<Integer, Result> result = null;
		
		patients.addMember(2);
		
		try {
			Result r = logicService.eval(new Patient(2).getPatientId(), "CD4 COUNT"); // CHICA-1151 pass in patientId instead of patient
			Assertions.assertNotNull(r);
			Assertions.assertEquals(0, r.size());
			
			result = logicService.eval(patients, "CD4 COUNT");
			Assertions.assertNotNull(result);
			
			result = logicService.eval(patients, new LogicCriteriaImpl("CD4 COUNT").lt(170));
			Assertions.assertNotNull(result);
			
			result = logicService.eval(patients, new LogicCriteriaImpl("CD4 COUNT").gt(185));
			Assertions.assertNotNull(result);
			
			result = logicService.eval(patients, new LogicCriteriaImpl("CD4 COUNT").equalTo(190));
			Assertions.assertNotNull(result);
			
			Calendar cal = Calendar.getInstance();
			cal.set(2006, 3, 11);
			result = logicService.eval(patients, new LogicCriteriaImpl("CD4 COUNT").before(cal.getTime()));
			Assertions.assertNotNull(result);
			
			result = logicService.eval(patients, new LogicCriteriaImpl("CD4 COUNT").lt(190).before(cal.getTime()));
			Assertions.assertNotNull(result);
			
			result = logicService.eval(patients, new LogicCriteriaImpl("CD4 COUNT").after(cal.getTime()));
			Assertions.assertNotNull(result);
			
			result = logicService.eval(patients, new LogicCriteriaImpl("CD4 COUNT").last());
			Assertions.assertNotNull(result);
			
			result = logicService.eval(patients, new LogicCriteriaImpl("CD4 COUNT").last().before(cal.getTime()));
			Assertions.assertNotNull(result);
			
			result = logicService.eval(patients, new LogicCriteriaImpl("CD4 COUNT").not().lt(150));
			Assertions.assertNotNull(result);
			
			result = logicService.eval(patients, new LogicCriteriaImpl("CD4 COUNT").not().not().lt(150));
			Assertions.assertNotNull(result);
			
			patients.addMember(2);
			patients.addMember(3);
			result = logicService.eval(patients, new LogicCriteriaImpl("CD4 COUNT").lt(200));
			Assertions.assertNotNull(result);
			
			patients.addMember(39);
			result = logicService.eval(patients, new LogicCriteriaImpl("PROBLEM ADDED")
			        .contains("HUMAN IMMUNODEFICIENCY VIRUS"));
			Assertions.assertNotNull(result);
			
			result = logicService.eval(patients, "CD4 COUNT");
			Assertions.assertNotNull(result);
			
			result = logicService.eval(patients, new LogicCriteriaImpl("CD4 COUNT").within(Duration.months(1)));
			Assertions.assertNotNull(result);
			
		}
		catch (LogicException e) {
			log.error("testObservationRule: Error generated", e);
		}
	}
	
	/**
	 * TODO make this test use assert statements instead of printing to stdout
	 */
	@Test
	public void shouldDemographicsRule() {
		LogicService logicService = Context.getLogicService();
		Cohort patients = new Cohort();
		Map<Integer, Result> result;
		
		patients.addMember(2);
		
		try {
			result = logicService.eval(patients, "BIRTHDATE");
			Assertions.assertNotNull(result);
			
			result = logicService.eval(patients, "AGE");
			Assertions.assertNotNull(result);
			
			result = logicService.eval(patients, new LogicCriteriaImpl("AGE").gt(10));
			Assertions.assertNotNull(result);
			
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, 2000);
			result = logicService.eval(patients, new LogicCriteriaImpl("BIRTHDATE").after(cal.getTime()));
			Assertions.assertNotNull(result);
			
			result = logicService.eval(patients, new LogicCriteriaImpl("BIRTHDATE").before(cal.getTime()));
			Assertions.assertNotNull(result);
			
			// test the index date functionality
			Calendar index = Calendar.getInstance();
			index.set(1970, 0, 1);
			result = logicService.eval(patients, "BIRTHDATE");
			Assertions.assertNotNull(result);
			
			result = logicService.eval(patients, new LogicCriteriaImpl("BIRTHDATE").within(Duration.years(5)));
			Assertions.assertNotNull(result);
		}
		catch (LogicException e) {
			log.error("testDemographicsRule: Error generated", e);
		}
		
	}
	
	/**
	 * TODO make this test use assert statements instead of printing to stdout
	 */
	@Test
	public void shouldHIVPositiveRule() {
		LogicService logicService = Context.getLogicService();
		Cohort patients = new Cohort();
		Map<Integer, Result> result = null;
		
		patients.addMember(2);
		
		try {
			
			result = logicService.eval(patients, new LogicCriteriaImpl("PROBLEM ADDED").contains(
			    "HUMAN IMMUNODEFICIENCY VIRUS").first());
			Assertions.assertNotNull(result);
			
			result = logicService.eval(patients, new LogicCriteriaImpl("PROBLEM ADDED").contains("HIV INFECTED").first());
			Assertions.assertNotNull(result);
			
			result = logicService.eval(patients, new LogicCriteriaImpl("PROBLEM ADDED").contains(
			    "ASYMPTOMATIC HIV INFECTION").first());
			Assertions.assertNotNull(result);
			
			result = logicService.eval(patients, new LogicCriteriaImpl("HIV VIRAL LOAD").first());
			Assertions.assertNotNull(result);
			
			result = logicService.eval(patients, new LogicCriteriaImpl("HIV VIRAL LOAD, QUALITATIVE").first());
			Assertions.assertNotNull(result);
			
			result = logicService.eval(patients, new LogicCriteriaImpl("CD4 COUNT").lt(200).first());
			Assertions.assertNotNull(result);
			
			result = logicService.eval(patients, "HIV POSITIVE");
			Assertions.assertNotNull(result);
		}
		catch (LogicException e) {
			log.error("Error generated", e);
		}
	}
	
	/**
	 * TODO make this test use assert statements instead of printing to stdout
	 */
	@Test
	public void shouldReferenceRule() {
		LogicService logicService = Context.getLogicService();
		Cohort patients = new Cohort();
		Map<Integer, Result> result = null;
		
		patients.addMember(2);
		
		try {
			
			result = logicService.eval(patients, "%%obs.CD4 COUNT");
			Assertions.assertNotNull(result);
			
			result = logicService.eval(patients, new LogicCriteriaImpl("%%obs.CD4 COUNT").first());
			Assertions.assertNotNull(result);
			
			result = logicService.eval(patients, "%%person.BIRTHDATE");
			Assertions.assertNotNull(result);
			
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, 1980);
			cal.set(Calendar.MONTH, 0);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			result = logicService.eval(patients, new LogicCriteriaImpl("%%person.BIRTHDATE").before(cal.getTime()));
			Assertions.assertNotNull(result);
		}
		catch (LogicException e) {
			log.error("Error generated", e);
		}
	}
		
	/**
	 * @see {@link LogicService#addTokenTag(String,String)}
	 */
	@Test
	public void addTokenTag_shouldAddTagForAToken() throws Exception {
		LogicService logicService = Context.getLogicService();
		
		int initialTags = logicService.getTokenTags("HIV POSITIVE").size();
		
		logicService.addTokenTag("HIV POSITIVE", "a tag");
		
		int finalTags = logicService.getTokenTags("HIV POSITIVE").size();
		
		Assertions.assertEquals(initialTags + 1, finalTags);
	}
	
	/**
	 * @see {@link LogicService#findTags(String)}
	 */
	@Test
	public void getTags_shouldReturnSetOfTagsMatchingInputTagPartially() throws Exception {
		LogicService logicService = Context.getLogicService();
		TokenService tokenService = Context.getService(TokenService.class);
		
		String[] similarTags = { "tags01", "tags02", "tags03", "tags04", "tags05" };
		Rule rule = logicService.getRule("AGE");
		Assertions.assertNotNull(rule);
		
		tokenService.registerToken("ANOTHER AGE", new ClassRuleProvider(), AgeRule.class.getName());
		for (String tag : similarTags)
			logicService.addTokenTag("ANOTHER AGE", tag);
		
		List<String> tags = logicService.getTags("tags");
		
		Assertions.assertEquals(similarTags.length, tags.size());
	}
	
	/**
	 * @see {@link LogicService#findToken(String)}
	 */
	@Test
	public void getTokens_shouldReturnAllRegisteredTokenMatchingTheInputFully() throws Exception {
		LogicService logicService = Context.getLogicService();
		List<String> tokens = logicService.getTokens("AGE");
		Assertions.assertEquals(1, tokens.size());
	}
	
	/**
	 * @see {@link LogicService#findToken(String)}
	 */
	@Test
	public void getTokens_shouldReturnAllRegisteredTokenMatchingTheInputPartially() throws Exception {
		LogicService logicService = Context.getLogicService();

		Context.getService(TokenService.class).registerToken("One Another", new ClassRuleProvider(), "one");
		Context.getService(TokenService.class).registerToken("Two Another", new ClassRuleProvider(), "two");
		
		List<String> tokens = logicService.getTokens("ANOTHER");
		Assertions.assertEquals(2, tokens.size());
	}
	
	/**
	 * @see {@link LogicService#findToken(String)}
	 */
	@Test
	public void getTokens_shouldNotFailWhenInputIsNull() throws Exception {
		Collection<String> tokens = Context.getLogicService().getTokens(null);
		Assertions.assertNotNull(tokens);
	}
	
	/**
	 * @see {@link LogicService#getRule(String)}
	 */
	@Test
	public void getRule_shouldReturnRuleAssociatedWithTheInputToken() throws Exception {
		Rule ageRule = Context.getLogicService().getRule("AGE");
		Assertions.assertNotNull(ageRule);
		Assertions.assertTrue(AgeRule.class.isAssignableFrom(ageRule.getClass()));
	}
	
	/**
	 * @see {@link LogicService#getRule(String)}
	 */
	@Test
	public void getRule_shouldFailWhenNoRuleIsAssociatedWithTheInputToken() throws Exception {
		LogicService logicService = Context.getLogicService();
		Assertions.assertThrows(LogicException.class, () -> {
			logicService.getRule("UNKNOWN RULE");
	    });
	}
	
	/**
	 * @see {@link LogicService#getRule(String)}
	 */
	@Test
	public void getRule_shouldReturnReferenceRule() throws Exception {
		Rule rule = Context.getLogicService().getRule("%%person.birthdate");
		Assertions.assertNotNull(rule);
		Assertions.assertTrue(rule.getClass().isAssignableFrom(ReferenceRule.class));
	}
	
	/**
	 * @see {@link LogicService#getTagsByToken(String)}
	 */
	@Test
	public void getTokenTags_shouldReturnSetOfTagsForACertainToken() throws Exception {
		LogicService logicService = Context.getLogicService();
		
		String[] setOfTags = { "birth", "date", "born" };
		
		Context.getService(TokenService.class).registerToken("ANOTHER AGE RULE", new ClassRuleProvider(), AgeRule.class.getName());
		for (String tag : setOfTags)
			logicService.addTokenTag("ANOTHER AGE RULE", tag);
		
		Set<String> retrievedTags = logicService.getTokenTags("ANOTHER AGE RULE");
		Assertions.assertEquals(setOfTags.length, retrievedTags.size());
		
		logicService.addTokenTag("ANOTHER AGE RULE", "tag");
		
		retrievedTags = logicService.getTokenTags("ANOTHER AGE RULE");
		Assertions.assertEquals(setOfTags.length + 1, retrievedTags.size());
	}
	
	/**
	 * @see {@link LogicService#getTokens()}
	 */
	@Test
	public void getAllTokens_shouldReturnAllRegisteredToken() throws Exception {
		Collection<String> tokens = Context.getLogicService().getAllTokens();
		Assertions.assertEquals(16, tokens.size());
	}
	
	/**
	 * @see {@link LogicService#getTokensByTag(String)}
	 */
	@Test
	public void getTokensWithTag_shouldReturnSetOfTokenAssociatedWithATag() throws Exception {
		LogicService logicService = Context.getLogicService();
		
		String[] setOfTags = { "birth", "date", "born" };
		
		Context.getService(TokenService.class).registerToken("ANOTHER AGE RULE", new ClassRuleProvider(), AgeRule.class.getName());
		for (String tag : setOfTags)
			logicService.addTokenTag("ANOTHER AGE RULE", tag);
		
		Collection<String> tokens = logicService.getTokensWithTag("birth");
		Assertions.assertEquals(1, tokens.size());
	}
	
	/**
	 * @see {@link LogicService#removeRule(String)}
	 */
	@Test
	public void removeRule_shouldRemoveRule() throws Exception {
		LogicService logicService = Context.getLogicService();
		Rule ageRule = logicService.getRule("AGE");
		Assertions.assertNotNull(ageRule);
		
		logicService.removeRule("AGE");
		Assertions.assertThrows(LogicException.class, () -> {
			logicService.getRule("AGE");
	    });
	}
	
	/**
	 * @see {@link LogicService#removeTokenTag(String,String)}
	 */
	@Test
	public void removeTokenTag_shouldRemoveTagFromAToken() throws Exception {
		LogicService logicService = Context.getLogicService();
		
		String[] setOfTags = { "birth", "date", "born" };
		
		Context.getService(TokenService.class).registerToken("ANOTHER AGE RULE", new ClassRuleProvider(), AgeRule.class.getName());
		for (String tag : setOfTags)
			logicService.addTokenTag("ANOTHER AGE RULE", tag);
		
		Collection<String> retrievedTags = logicService.getTokenTags("ANOTHER AGE RULE");
		Assertions.assertEquals(setOfTags.length, retrievedTags.size());
		
		logicService.removeTokenTag("ANOTHER AGE RULE", "date");
		
		retrievedTags = logicService.getTokenTags("ANOTHER AGE RULE");
		Assertions.assertEquals(setOfTags.length - 1, retrievedTags.size());
	}
	
}
