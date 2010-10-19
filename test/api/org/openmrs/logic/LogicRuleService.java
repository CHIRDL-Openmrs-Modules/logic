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
package org.openmrs.logic;

import java.util.List;

import org.openmrs.api.OpenmrsService;
import org.openmrs.api.db.DAOException;
import org.springframework.transaction.annotation.Transactional;

/**
 * Provides access to user-specified LogicRules
 */
public interface LogicRuleService extends OpenmrsService {
	
	/**
	 * @param id the primary key of the LogicRule to look up
	 * @return the LogicRule whose primary key id matches the passed id
	 * @throws DAOException
	 */
	@Transactional(readOnly=true)
	public LogicRule getLogicRule(Integer id);
	
	/**
	 * @param name the name of the LogicRule to look up
	 * @return the LogicRule whose name matches the passed name
	 * @throws DAOException
	 */
	@Transactional(readOnly=true)
	public LogicRule getLogicRule(String name);
	
	/**
	 * @param includeRetired if true, includes retired LogicRules
	 * @return all LogicRules saved to the database
	 * @throws DAOException
	 */
	@Transactional(readOnly=true)
	public List<LogicRule> getAllLogicRules(boolean includeRetired);
	
	/**
	 * @param logicRule the LogicRule to save to the database
	 * @return the saved LogicRule
	 * @throws DAOException
	 */
	@Transactional
	public LogicRule saveLogicRule(LogicRule logicRule);
	
	/**
	 * @param logicRule the LogicRule to delete from the database
	 * @throws DAOException
	 */
	@Transactional
	public void purgeLogicRule(LogicRule logicRule);
}
