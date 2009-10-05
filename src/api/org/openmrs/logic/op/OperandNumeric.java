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

/**
 *
 */
public class OperandNumeric implements Operand {

	private Double value;
	
    public OperandNumeric(Double value) {
    	this.value = value;
    }
    
    public OperandNumeric(Float value) {
    	this.value = value.doubleValue();
    }
    
    public OperandNumeric(Integer value) {
    	this.value = value.doubleValue();
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
    
    public String toString() {
		return value == null ? "null" : value.toString();
	}
	
}
