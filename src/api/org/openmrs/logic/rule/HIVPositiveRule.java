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
package org.openmrs.logic.rule;

import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.impl.LogicCriteriaImpl;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;

/**
 * 
 */
public class HIVPositiveRule implements Rule {
	
	@SuppressWarnings("unused")
	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, java.lang.Integer,
	 *      java.util.Map)
	 */
	@Override
	public Result eval(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {
		Result allDiagnoses = Result.emptyResult();
		Boolean ageOK = null;
		
		try {
			ageOK = context.read(patientId, context.getLogicDataSource("obs"), new LogicCriteriaImpl("AGE").gt(1)).toBoolean();
			if (!ageOK)
				return Result.emptyResult();
			
			// we find the first HIV diagnosis
			allDiagnoses.add(context.eval(patientId,
				new LogicCriteriaImpl("PROBLEM ADDED").contains("HUMAN IMMUNODEFICIENCY VIRUS").first(), null));
			allDiagnoses.add(context.eval(patientId, 
			    new LogicCriteriaImpl("PROBLEM ADDED").contains("HIV INFECTED").first(), null));
			allDiagnoses.add(context.eval(patientId,
			    new LogicCriteriaImpl("PROBLEM ADDED").contains("ASYMPTOMATIC HIV INFECTION").first(), null));
			
			// first viral load
			allDiagnoses.add(context.eval(patientId,
				new LogicCriteriaImpl("HIV VIRAL LOAD").first(), null));
			
			// first qualitative viral load
			allDiagnoses.add(context.eval(patientId,
			    new LogicCriteriaImpl("HIV VIRAL LOAD, QUALITATIVE").first(), null));
			
			// first CD4 COUNT < 200
			allDiagnoses.add(context.eval(patientId,
				new LogicCriteriaImpl("CD4 COUNT").lt(200).first(), null));
			
			return allDiagnoses.earliest();
			
		}
		catch (LogicException e) {
			return Result.emptyResult();
		}
	}
	
	/**
	 * @see org.openmrs.logic.Rule#getParameterList()
	 */
	public Set<RuleParameterInfo> getParameterList() {
		return null;
	}
	
	/**
	 * @see org.openmrs.logic.Rule#getDependencies()
	 */
	public String[] getDependencies() {
		return new String[] { "HUMAN IMMUNODEFICIENCY VIRUS", "HIV INFECTED", "ASYMPTOMATIC HIV INFECTION",
		        "HIV VIRAL LOAD", "HIV VIRAL LOAD, QUALITATIVE", "CD4 COUNT" };
	}
	
	/**
	 * @see org.openmrs.logic.Rule#getTTL()
	 */
	public int getTTL() {
		return 60 * 30; // 30 minutes
	}
	
	/**
	 * @see org.openmrs.logic.Rule#getDefaultDatatype()
	 */
	public Datatype getDefaultDatatype() {
		return Datatype.CODED;
	}
	
}
