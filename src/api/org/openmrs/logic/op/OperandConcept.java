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
package org.openmrs.logic.op;

import org.openmrs.Concept;

/**
 *
 */
public class OperandConcept implements Operand {

	private Concept concept;
	
    public OperandConcept(Concept concept) {
	    this.concept = concept;
    }
    
    public Concept asConcept() {
    	return concept;
    }
    
    public String toString() {
		return concept == null ? "null" : concept.getName().getName();
	}
    
}
