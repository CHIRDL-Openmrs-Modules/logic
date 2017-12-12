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

import java.util.ArrayList;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.util.OpenmrsUtil;

/**
 * @see Operand
 */
public class OperandConcept implements Operand {
	
	private Concept concept;
	
	/**
	 * The Operators that this Operand {@link #supports(Operator)}
	 */
	private static List<ComparisonOperator> validOperators = new ArrayList<ComparisonOperator>();
	
	static {
		validOperators.add(ComparisonOperator.CONTAINS);
		validOperators.add(ComparisonOperator.EQUALS);
	}
	
	/**
	 * Holds a pointer of the given concept as this Operand
	 * 
	 * @param concept the {@link Concept} to act on
	 */
	public OperandConcept(Concept concept) {
		this.concept = concept;
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
	    return concept == null ? 0 : concept.hashCode();
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other) {
		if (other != null && other instanceof OperandConcept)
			return OpenmrsUtil.nullSafeEquals(this.concept, ((OperandConcept) other).concept);
		else
			return false;
	}
	
	/**
	 * Get the {@link Concept} pointer behind this Operand
	 * 
	 * @return Concept
	 */
	public Concept asConcept() {
		return concept;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return concept == null ? "null" : concept.getName(Context.getLocale(), false).getName(); // CHICA-1151 Replace call to getBestName() with getName().
	}
	
	/**
	 * @see org.openmrs.logic.op.Operand#supports(org.openmrs.logic.op.ComparisonOperator)
	 */
	public boolean supports(ComparisonOperator operator) {
		return validOperators.contains(operator);
	}
	
}
