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
package org.openmrs.logic.rule.definition.db;

import java.util.List;

import org.openmrs.api.db.DAOException;
import org.openmrs.logic.rule.definition.RuleDefinition;


/**
 * Data Access for user-defined rules
 */
public interface RuleDefinitionDAO {

	/**
	 * @param id the primary key of the {@link RuleDefinition} to look up
	 * @return the {@link RuleDefinition} whose primary key id matches the passed id
	 * @throws DAOException
	 */
	public RuleDefinition getRuleDefinition(Integer id) throws DAOException;
	
	/**
	 * @param name the name of the {@link RuleDefinition} to look up
	 * @return the {@link RuleDefinition} whose name matches the passed name
	 * @throws DAOException
	 */
	public RuleDefinition getRuleDefinition(String name) throws DAOException;
	
	/**
	 * @param includeRetired if true, includes retired {@link RuleDefinition}s
	 * @return all {@link RuleDefinition}s saved to the database
	 * @throws DAOException
	 */
	public List<RuleDefinition> getAllRuleDefinitions(boolean includeRetired) throws DAOException;
	
	/**
	 * @param ruleDefinition the {@link RuleDefinition} to save to the database
	 * @return the saved {@link RuleDefinition}
	 * @throws DAOException
	 */
	public RuleDefinition saveRuleDefinition(RuleDefinition ruleDefinition) throws DAOException;
	
	/**
	 * @param ruleDefinition the {@link RuleDefinition} to delete from the database
	 * @throws DAOException
	 */
	public void deleteRuleDefinition(RuleDefinition ruleDefinition) throws DAOException;

}
