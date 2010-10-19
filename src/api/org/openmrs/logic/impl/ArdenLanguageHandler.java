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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import org.openmrs.ConceptDerived;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicConstants;
import org.openmrs.logic.LogicRule;
import org.openmrs.util.OpenmrsUtil;

/**
 * Class to create rule java source file from Arden syntax. This class use Arden parser in core to
 * generate the java source file
 */
public class ArdenLanguageHandler extends CompilableLanguageHandler {
	
	public static final String MLM_EXTENSION = ".mlm";
	
	/**
	 * @see CompilableLanguageHandler#prepareSource(ConceptDerived)
	 */
	public void prepareSource(LogicRule logicRule) {
		log.info("Processing arden rule ...");
		
		AdministrationService as = Context.getAdministrationService();
		String javaDirectory = as.getGlobalProperty(LogicConstants.RULE_DEFAULT_SOURCE_FOLDER);
		File sourceDirectory = OpenmrsUtil.getDirectoryInApplicationDataDirectory(javaDirectory);
		
		String fullClassName = logicRule.getClassName();
		String fullClassPath = fullClassName.replace('.', File.separatorChar);
		
		File mlmFile = new File(sourceDirectory, fullClassPath + MLM_EXTENSION);
		File javaFile = new File(sourceDirectory, fullClassPath + JAVA_EXTENSION);
		
		Date modifiedDate = logicRule.getDateChanged();
		if (modifiedDate == null) {
			modifiedDate = logicRule.getDateCreated();
		}
		// only compile when the file is not exist or the concept derived is updated after the source file last modified
		boolean mlmChanged = (!mlmFile.exists() || modifiedDate.after(new Date(mlmFile.lastModified())));
		boolean javaChanged = (!javaFile.exists() || modifiedDate.after(new Date(javaFile.lastModified())));

		if  (mlmChanged || javaChanged) {
			// First, write the Arden rule out to a file, since the 1.6 version of the Arden Service requires a file for processing
			BufferedWriter writer = null;
			try {
				File parentFile = mlmFile.getParentFile();
				if (!parentFile.exists()) {
					parentFile.mkdirs();
				}
				writer = new BufferedWriter(new FileWriter(mlmFile));
				writer.write(logicRule.getRuleContent());
			}
			catch (IOException e) {
				log.error("Failed saving java rule file ...", e);
			}
			finally {
				if (writer != null) {
					try {
						writer.close();
					}
					catch (IOException e) {
						log.error("Failed closing writer...", e);
					}
				}
			}
			Context.getArdenService().compileFile(mlmFile.getAbsolutePath(), javaFile.getParent());
		}
	}
	
}
