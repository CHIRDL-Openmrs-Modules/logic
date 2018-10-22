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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicConstants;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.stereotype.Component;

/**
 * Plain java language handler. The user must write their own java class file and then enter the
 * rule java to the concept derived's rule section when creating a new concept.
 */
@Component
public class JavaLanguageHandler extends CompilableLanguageHandler {
	
	/**
     * @see org.openmrs.logic.rule.definition.LanguageHandler#getName()
     */
    public String getName() {
	    return "Java";
    }
    
    /**
	 * @see CompilableLanguageHandler#prepareSource(RuleDefinition,String)
	 */
	public void prepareSource(RuleDefinition logicRule, String className) {
		
		AdministrationService as = Context.getAdministrationService();
		String javaDirectory = as.getGlobalProperty(LogicConstants.RULE_DEFAULT_SOURCE_FOLDER);
		File sourceDirectory = OpenmrsUtil.getDirectoryInApplicationDataDirectory(javaDirectory);
		
		String path = className.replace('.', File.separatorChar);
				
		File javaFile = new File(sourceDirectory, path + JAVA_EXTENSION);
		if (!javaFile.getParentFile().exists())
			javaFile.getParentFile().mkdirs();
		
		Date modifiedDate = logicRule.getDateChanged();
		if (modifiedDate == null) {
			modifiedDate = logicRule.getDateCreated();
		}
		
		// only compile when the java file is not exist or the concept derived is updated after the source file last modified
		if (!javaFile.exists() || modifiedDate.after(new Date(javaFile.lastModified()))) {
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(javaFile))) {
				
				writer.write(logicRule.getRuleContent());
			}
			catch (IOException e) {
				log.error("Failed saving java rule file ...", e);
			}
		}
	}

}
