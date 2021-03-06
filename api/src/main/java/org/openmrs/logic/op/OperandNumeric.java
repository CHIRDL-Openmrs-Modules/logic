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

import org.openmrs.util.OpenmrsUtil;

/**
 *
 */
public class OperandNumeric implements Operand {
	
	private Double value;
	
	/**
	 * The Operators that this Operand {@link #supports(ComparisonOperator)}
	 */
	private static List<ComparisonOperator> validOperators = new ArrayList<ComparisonOperator>();
	
	static {
		validOperators.add(ComparisonOperator.CONTAINS);
		validOperators.add(ComparisonOperator.EQUALS);
		validOperators.add(ComparisonOperator.LT);
		validOperators.add(ComparisonOperator.GT);
		validOperators.add(ComparisonOperator.LTE);
		validOperators.add(ComparisonOperator.GTE);
	}
	
	public OperandNumeric(Double value) {
		this.value = value;
	}
	
	public OperandNumeric(Float value) {
		this.value = value.doubleValue();
	}
	
	public OperandNumeric(Integer value) {
		this.value = value.doubleValue();
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
	    return value == null ? 0 : value.hashCode();
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other) {
		if (other != null && other instanceof OperandNumeric)
			return OpenmrsUtil.nullSafeEquals(this.value, ((OperandNumeric) other).value);
		else
			return false;
	}
	
	public Double asDouble() {
		return value;
	}
	
	public Float asFloat() {
		return value == null ? null : value.floatValue();
	}
	
	public Integer asInteger() {
		return value == null ? null : value.intValue();
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return value == null ? "null" : value.toString();
	}
	
	/**
	 * @see org.openmrs.logic.op.Operand#supports(org.openmrs.logic.op.ComparisonOperator)
	 */
	public boolean supports(ComparisonOperator operator) {
		return validOperators.contains(operator);
	}
}
