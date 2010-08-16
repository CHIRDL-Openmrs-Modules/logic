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
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicConstants;
import org.openmrs.util.OpenmrsUtil;

/**
 * Plain java language handler. The user must write their own java class file and then enter the
 * rule java to the concept derived's rule section when creating a new concept.
 */
public class JavaLanguageHandler extends CompilableLanguageHandler {
	
	/**
	 * @see org.openmrs.logic.impl.CompilableLanguageHandler#prepareSource(org.openmrs.ConceptDerived)
	 */
	@Override
	public void prepareSource(ConceptDerived conceptDerived) {
		
		String javaDirectory = Context.getAdministrationService().getGlobalProperty(
		    LogicConstants.RULE_DEFAULT_SOURCE_FOLDER);
		File sourceDirectory = OpenmrsUtil.getDirectoryInApplicationDataDirectory(javaDirectory);
		
		String name = conceptDerived.getClassName();
		String path = name.replace('.', File.separatorChar);
		
		File javaFile = new File(sourceDirectory, path + JAVA_EXTENSION);
		
		Date modifiedDate = conceptDerived.getDateChanged();
		if (modifiedDate == null)
			modifiedDate = conceptDerived.getDateCreated();
		
		// only compile when the java file is not exist or the concept derived is updated after the source file last modified
		if (!javaFile.exists() || modifiedDate.after(new Date(javaFile.lastModified())))
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(javaFile));
				writer.write(conceptDerived.getRuleContent());
				writer.close();
			}
			catch (IOException e) {
				log.error("Failed saving java rule file ...", e);
			}
	}
}
