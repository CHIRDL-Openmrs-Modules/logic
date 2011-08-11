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
import java.util.Date;
import java.util.List;

/**
 * Operand for dates in the logic service. This Operand is slightly different from other Operands in
 * that it extends Date and so can be casted and used directly as a Date object
 * 
 * @see Operand
 */
public class OperandDate extends Date implements Operand {
	
	private static final long serialVersionUID = 2726925287599642390L;
	
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
		validOperators.add(ComparisonOperator.BEFORE);
		validOperators.add(ComparisonOperator.AFTER);
		validOperators.add(ComparisonOperator.WITHIN);
	}
	
	/**
	 * Copies the value of the given date into this OperandDate object. The original date object is
	 * not referenced after turning into an OpenrandDate object
	 * 
	 * @param date the Date for this OperandDate
	 */
	public OperandDate(Date date) {
		this.setTime(date.getTime());
	}
	
	/**
	 * @see org.openmrs.logic.op.Operand#supports(org.openmrs.logic.op.ComparisonOperator)
	 */
	public boolean supports(ComparisonOperator operator) {
		return validOperators.contains(operator);
	}
	
}
