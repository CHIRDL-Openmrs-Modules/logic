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

import org.openmrs.util.OpenmrsUtil;

/**
 * @see Operand
 */
public class OperandText implements Operand {
	
	private String string;
	
	/**
	 * Constructor to create this Operand with the String backing object. A pointer to the String is
	 * kept.
	 * 
	 * @param string the backing String
	 */
	public OperandText(String string) {
		this.string = string;
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return string == null ? 0 : string.hashCode();
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other) {
		if (other != null && other instanceof OperandText)
			return OpenmrsUtil.nullSafeEquals(this.string, ((OperandText) other).string);
		else
			return false;
	}
	
	/**
	 * Return the String object that is behind this Operand
	 * 
	 * @return the backing String object
	 */
	public String asString() {
		return string;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return string == null ? "null" : string.toString();
	}
	
	/**
	 * @see org.openmrs.logic.op.Operand#supports(org.openmrs.logic.op.ComparisonOperator)
	 */
	public boolean supports(ComparisonOperator operator) {
		return (ComparisonOperator.EQUALS.equals(operator) || ComparisonOperator.CONTAINS.equals(operator));
	}
	
}
