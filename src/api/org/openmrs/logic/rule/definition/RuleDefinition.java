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
package org.openmrs.logic.rule.definition;

import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.OpenmrsObject;

/**
 * Represents a user-defined Rule which is compiled dynamically at Runtime
 * @version 1.0
 */
public class RuleDefinition extends BaseOpenmrsMetadata implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	//***** PROPERTIES *****
	
	private Integer id;
	private String ruleContent;
	private String language;
	
	//***** CONSTRUCTORS *****
	
	/**
	 * Default Constructor
	 */
	public RuleDefinition() { }
	
	//***** INSTANCE METHODS *****
	
	/**
	 * @see Object#equals()
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof RuleDefinition)) {
			return false;
		}
		RuleDefinition that = (RuleDefinition)obj;
		if (this.getId() != null && that.getId() != null) {
			return (this.getId().equals(that.getId()));
		}
		return this == that;
	}

	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		if (getId() != null) {
			return 6 + getId().hashCode() * 31;
		}
		return super.hashCode();
	}
	
	//***** PROPERTY ACCESS *****
	
	/**
	 * @see org.openmrs.OpenmrsObject#getId()
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @see OpenmrsObject#setId(Integer)
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return rule for the derived concept
	 */
	public String getRuleContent() {
		return ruleContent;
	}
	
	/**
	 * @param ruleContent new rule for derived concept
	 */
	public void setRuleContent(String ruleContent) {
		this.ruleContent = ruleContent;
	}
	
	/**
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}
	
	/**
	 * @param language the language to set
	 */
	public void setLanguage(String language) {
		this.language = language;
	}
	
}
