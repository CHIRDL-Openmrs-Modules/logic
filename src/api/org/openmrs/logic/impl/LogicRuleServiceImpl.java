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

import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.logic.LogicRule;
import org.openmrs.logic.LogicRuleService;
import org.openmrs.logic.TokenService;
import org.openmrs.logic.db.LogicRuleDAO;
import org.openmrs.logic.rule.LogicRuleRuleProvider;
import org.openmrs.logic.token.TokenRegistration;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Provides access to user-specified LogicRules
 */
public class LogicRuleServiceImpl extends BaseOpenmrsService implements LogicRuleService {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	private LogicRuleDAO dao;
	
	@Autowired
	List<LanguageHandler> languageHandlers;
	
	@Autowired
	LogicRuleRuleProvider ruleProvider;
	
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
		TokenService tokenService = Context.getService(TokenService.class);
		TokenRegistration existingRegistration = null;
		if (logicRule.getId() != null)
			existingRegistration = tokenService.getTokenRegistrationByProviderAndConfiguration(ruleProvider, logicRule.getId().toString());
		logicRule = dao.saveLogicRule(logicRule);
		boolean replace = existingRegistration != null && !existingRegistration.getProviderToken().equals(logicRule.getName());
		if (replace)
			tokenService.deleteTokenRegistration(existingRegistration);
		if (replace || existingRegistration == null)
			tokenService.registerToken(logicRule.getName(), ruleProvider, logicRule.getId().toString());
		return logicRule;
	}

	/**
	 * @see LogicRuleService#purgeLogicRule(LogicRule)
	 */
	public void purgeLogicRule(LogicRule logicRule) {
		dao.purgeLogicRule(logicRule);
		Context.getService(TokenService.class).removeToken(ruleProvider, logicRule.getName());
	}

	/**
	 * @return the dao
	 */
	public LogicRuleDAO getDao() {
		return dao;
	}

	/**
	 * @param dao the dao to set
	 */
	public void setDao(LogicRuleDAO dao) {
		this.dao = dao;
	}

	/**
     * @see org.openmrs.logic.LogicRuleService#getAllLanguageHandlers()
     */
    @Override
    public List<LanguageHandler> getAllLanguageHandlers() {
	    return Collections.unmodifiableList(languageHandlers);
    }

	/**
     * @see org.openmrs.logic.LogicRuleService#getLanguageHandler(java.lang.String)
     */
    @Override
    public LanguageHandler getLanguageHandler(String name) {
	    for (LanguageHandler h : languageHandlers) {
	    	if (h.getName().equals(name))
	    		return h;
	    }
	    return null;
    }
}
