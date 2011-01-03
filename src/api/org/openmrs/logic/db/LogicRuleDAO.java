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
package org.openmrs.logic.db;

import java.util.List;

import org.openmrs.api.db.DAOException;
import org.openmrs.logic.LogicRule;


/**
 * Data Access for user-defined rules
 */
public interface LogicRuleDAO {

	/**
	 * @param id the primary key of the LogicRule to look up
	 * @return the LogicRule whose primary key id matches the passed id
	 * @throws DAOException
	 */
	public LogicRule getLogicRule(Integer id) throws DAOException;
	
	/**
	 * @param name the name of the LogicRule to look up
	 * @return the LogicRule whose name matches the passed name
	 * @throws DAOException
	 */
	public LogicRule getLogicRule(String name) throws DAOException;
	
	/**
	 * @param includeRetired if true, includes retired LogicRules
	 * @return all LogicRules saved to the database
	 * @throws DAOException
	 */
	public List<LogicRule> getAllLogicRules(boolean includeRetired) throws DAOException;
	
	/**
	 * @param logicRule the LogicRule to save to the database
	 * @return the saved LogicRule
	 * @throws DAOException
	 */
	public LogicRule saveLogicRule(LogicRule logicRule) throws DAOException;
	
	/**
	 * @param logicRule the LogicRule to delete from the database
	 * @throws DAOException
	 */
	public void purgeLogicRule(LogicRule logicRule) throws DAOException;

}
