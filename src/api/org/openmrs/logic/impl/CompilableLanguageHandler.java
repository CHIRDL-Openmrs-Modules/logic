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
package org.openmrs.logic.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.ConceptDerived;
import org.openmrs.logic.CompilingClassLoader;
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
	 * @param conceptDerived the concept derived that need to be processed
	 */
	public abstract void prepareSource(ConceptDerived conceptDerived);
	
	/**
	 * @see org.openmrs.logic.impl.LanguageHandler#handle(org.openmrs.ConceptDerived)
	 */
	@Override
	public Rule handle(ConceptDerived conceptDerived) {
		CompilingClassLoader classLoader = new CompilingClassLoader();
		try {
			prepareSource(conceptDerived);
			Class<?> c = classLoader.loadClass(conceptDerived.getClassName());
			Object obj = c.newInstance();
			return (Rule) obj;
		}
		catch (Exception e) {
			log.error("Creating rule object failed ...", e);
		}
		// creating rule object throwing exception, return null
		return null;
	}
	
}
