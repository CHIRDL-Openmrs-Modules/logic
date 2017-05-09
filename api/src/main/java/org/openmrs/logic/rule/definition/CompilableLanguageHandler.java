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
package org.openmrs.logic.rule.definition;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.logic.CompilingClassLoader;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;

/**
 * Base handler for all language that need to be compiled. The implementing class must implement how
 * the java source file will be generated and stored. Implementing class must ensure that the source
 * file conform to the Rule interface.
 */
public abstract class CompilableLanguageHandler implements LanguageHandler {
	
	protected static final Log log = LogFactory.getLog(CompilableLanguageHandler.class);
	
	protected static final String JAVA_EXTENSION = ".java";
	
	/**
	 * Prepare the java source file that will be compiled to create the rule object. The source file
	 * must match the concept derived java class name. For example:
	 * 
	 * <pre>
	 * org.openmrs.rule.example.PregnantMan
	 * </pre>
	 * 
	 * Must be stored under:
	 * 
	 * <pre>
	 * [logic.default.ruleJavaDirectory (global property)]/org/openmrs/rule/example/PregnantMan.java
	 * </pre>
	 * 
	 * @param ruleDefinition the LogicRule that need to be processed
	 * @param className the suggested class name
	 */
	public abstract void prepareSource(RuleDefinition ruleDefinition, String className);
	
	/**
	 * @see LanguageHandler#compile(RuleDefinition)
	 */
	public Rule compile(RuleDefinition ruleDefinition) {
		CompilingClassLoader classLoader = new CompilingClassLoader();
		try {
			String className = getClassName(ruleDefinition);
			prepareSource(ruleDefinition, className);
			Class<?> c = classLoader.loadClass(className);
			Object obj = c.newInstance();
			return (Rule) obj;
		}
		catch (Exception e) {
			log.error("Creating rule object failed ...", e);
			throw new LogicException(e);
		}
	}
	
	public static String getClassName(RuleDefinition ruleDefinition) {
	    return "org.openmrs.module.logic.rule.CompiledRule" + ruleDefinition.getId();
    }
	
}
