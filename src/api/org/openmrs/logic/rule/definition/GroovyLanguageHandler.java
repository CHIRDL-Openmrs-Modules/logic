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

import groovy.lang.GroovyClassLoader;

import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.springframework.stereotype.Component;


/**
 * Language handler that allows you to write rules as a Groovy script that will be the body
 * of the eval method of a rule. 
 */
@Component
public class GroovyLanguageHandler implements LanguageHandler {
	
	private GroovyClassLoader gcl = new GroovyClassLoader();
	
	/**
	 * @see org.openmrs.logic.rule.definition.LanguageHandler#getName()
	 */
	@Override
	public String getName() {
		return "Groovy";
	}
	
	/**
	 * @see org.openmrs.logic.rule.definition.LanguageHandler#compile(RuleDefinition)
	 */
	@SuppressWarnings("unchecked")
    @Override
	public Rule compile(RuleDefinition ruleDefinition) {
		StringBuilder sb = new StringBuilder();
		sb.append("package org.openmrs.module.logic.rule;\n");
		sb.append("import java.util.Map;\n");
		sb.append("import org.openmrs.Patient;\n");
		sb.append("import org.openmrs.logic.*;\n");
		sb.append("import org.openmrs.logic.rule.*;\n");
		sb.append("import org.openmrs.logic.result.*;\n");
		sb.append("\n");
		sb.append("public class GroovyRule" + ruleDefinition.getId() + " extends AbstractRule {\n");
		sb.append("    public Result eval(LogicContext context, Patient patient, Map<String, Object> parameters) throws LogicException {\n");
		sb.append(ruleDefinition.getRuleContent());
		sb.append("    }\n");
		sb.append("}");
		try {
			Class<Rule> clazz = gcl.parseClass(sb.toString());
	        return clazz.newInstance();
        } catch (Exception ex) {
        	throw new LogicException("Error compiling or instantiating rule", ex);
        }
	}
	
}
