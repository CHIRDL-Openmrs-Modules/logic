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

import java.util.Collection;
import java.util.Iterator;

import org.openmrs.util.OpenmrsUtil;


/**
 *
 */
public class OperandCollection implements Operand {
	
	private Collection<?> collection;
	
	public OperandCollection(Collection<?> strings) {
		collection = strings;
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		if (collection != null)
			for (Object o : collection)
				result = prime * result + o.hashCode();
		return result;
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
	    if (obj != null && obj instanceof OperandCollection) {
	    	OperandCollection other = (OperandCollection) obj;
	    	if (this.collection == null || other.collection == null || this.collection.size() != other.collection.size())
	    		return false;
	    	Iterator<?> i = this.collection.iterator();
	    	Iterator<?> j = other.collection.iterator();
	    	while (i.hasNext()) { // we know that sizes are equal from our previous check
	    		Object a = i.next();
	    		Object b = j.next();
	    		if (!OpenmrsUtil.nullSafeEquals(a, b))
	    			return false;
	    	}
	    	return true;
	    } else {
	    	return false;
	    }
	}
	
	public Collection<?> asCollection() {
		return collection;
	}
	
	/**
	 * @see org.openmrs.logic.op.Operand#supports(org.openmrs.logic.op.ComparisonOperator)
	 */
	public boolean supports(ComparisonOperator operator) {
		return operator.equals(ComparisonOperator.IN);
	}
	
}
