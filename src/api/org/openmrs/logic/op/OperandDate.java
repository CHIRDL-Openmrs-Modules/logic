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

import java.util.Date;

/**
 *
 */
public class OperandDate extends Date implements Operand {

    private static final long serialVersionUID = 2726925287599642390L;
    
	/**
	 * Copies the value of the given date into
	 * this OperandDate object.  The original date 
	 * object is not referenced after turning into
	 * an OpenrandDate object
	 * 
	 * @param date the Date for this OperandDate
	 */
	public OperandDate(Date date) {
		this.setTime(date.getTime());
	}
	
}
