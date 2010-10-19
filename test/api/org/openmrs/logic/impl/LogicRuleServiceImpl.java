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
package org.openmrs.logic.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.logic.LogicRule;
import org.openmrs.logic.LogicRuleService;
import org.openmrs.logic.db.LogicRuleTokenDAO;
import org.springframework.transaction.annotation.Transactional;

/**
 * Provides access to user-specified LogicRules
 */
@Transactional
public class LogicRuleServiceImpl extends BaseOpenmrsService implements LogicRuleService {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	private LogicRuleTokenDAO dao;
	
	/**
	 * @see LogicRuleService#getLogicRule(Integer)
	 */
	public LogicRule getLogicRule(Integer id) {
		return dao.getLogicRule(id);
	}

	/**
	 * @see LogicRuleService#getLogicRule(String)
	 */
	public LogicRule getLogicRule(String name) {
		return dao.getLogicRule(name);
	}
	
	/**
	 * @return all user-defined LogicRules saved to the database
	 */
	public List<LogicRule> getAllLogicRules() {
		return getAllLogicRules(false);
	}
	
	/**
	 * @param includeRetired if true, includes retired LogicRules
	 * @return all user-defined LogicRules saved to the database
	 */
	public List<LogicRule> getAllLogicRules(boolean includeRetired) {
		return dao.getAllLogicRules(includeRetired);
	}

	/**
	 * @see LogicRuleService#saveLogicRule(LogicRule)
	 */
	public LogicRule saveLogicRule(LogicRule logicRule) {
		return saveLogicRule(logicRule);
	}

	/**
	 * @see LogicRuleService#purgeLogicRule(LogicRule)
	 */
	public void purgeLogicRule(LogicRule logicRule) {
		purgeLogicRule(logicRule);
	}

	/**
	 * @return the dao
	 */
	public LogicRuleTokenDAO getDao() {
		return dao;
	}

	/**
	 * @param dao the dao to set
	 */
	public void setDao(LogicRuleTokenDAO dao) {
		this.dao = dao;
	}
}
