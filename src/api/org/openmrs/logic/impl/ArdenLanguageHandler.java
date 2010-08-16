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

import java.io.File;
import java.util.Date;

import org.openmrs.ConceptDerived;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicConstants;
import org.openmrs.util.OpenmrsUtil;

/**
 * Class to create rule java source file from arden syntax. This class use arden parser in core to
 * generate the java source file
 */
public class ArdenLanguageHandler extends CompilableLanguageHandler {
	
	/**
	 * @see org.openmrs.logic.impl.CompilableLanguageHandler#prepareSource(org.openmrs.ConceptDerived)
	 */
	@Override
	public void prepareSource(ConceptDerived conceptDerived) {
		log.info("Processing arden rule ...");
		
		String javaDirectory = Context.getAdministrationService().getGlobalProperty(
		    LogicConstants.RULE_DEFAULT_SOURCE_FOLDER);
		File sourceDirectory = OpenmrsUtil.getDirectoryInApplicationDataDirectory(javaDirectory);
		
		String fullClassName = conceptDerived.getClassName();
		String fullClassPath = fullClassName.replace('.', File.separatorChar);
		
		File javaFile = new File(sourceDirectory, fullClassPath + JAVA_EXTENSION);
		
		Date modifiedDate = conceptDerived.getDateChanged();
		if (modifiedDate == null)
			modifiedDate = conceptDerived.getDateCreated();
		// only compile when the java file is not exist or the concept derived is updated after the source file last modified
		if (!javaFile.exists() || modifiedDate.after(new Date(javaFile.lastModified())))
			Context.getArdenService().compile(conceptDerived.getRule(), sourceDirectory.getAbsolutePath());
	}
	
}
