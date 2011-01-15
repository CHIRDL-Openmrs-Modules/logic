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
package org.openmrs.logic.rule.definition.impl;

import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.logic.rule.definition.LanguageHandler;
import org.openmrs.logic.rule.definition.RuleDefinition;
import org.openmrs.logic.rule.definition.RuleDefinitionRuleProvider;
import org.openmrs.logic.rule.definition.RuleDefinitionService;
import org.openmrs.logic.rule.definition.db.RuleDefinitionDAO;
import org.openmrs.logic.token.TokenRegistration;
import org.openmrs.logic.token.TokenService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Provides access to user-specified LogicRules
 */
public class RuleDefinitionServiceImpl extends BaseOpenmrsService implements RuleDefinitionService {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	private RuleDefinitionDAO dao;
	
	@Autowired
	List<LanguageHandler> languageHandlers;
	
	@Autowired
	RuleDefinitionRuleProvider ruleProvider;
	
	/**
	 * @see RuleDefinitionService#getRuleDefinition(Integer)
	 */
	@Override
	public RuleDefinition getRuleDefinition(Integer id) {
		return dao.getRuleDefinition(id);
	}

	/**
	 * @see RuleDefinitionService#getRuleDefinition(String)
	 */
	@Override
	public RuleDefinition getRuleDefinition(String name) {
		return dao.getRuleDefinition(name);
	}
	
	/**
	 * @return all user-defined {@link RuleDefinition}s saved to the database
	 */
	@Override
	public List<RuleDefinition> getAllRuleDefinitions() {
		return getAllRuleDefinitions(true);
	}
	
	/**
	 * @param includeRetired if true, includes retired {@link RuleDefinition}s
	 * @return all user-defined {@link RuleDefinition}s saved to the database
	 */
	@Override
	public List<RuleDefinition> getAllRuleDefinitions(boolean includeRetired) {
		return dao.getAllRuleDefinitions(includeRetired);
	}

	/**
	 * @see RuleDefinitionService#saveRuleDefinition(RuleDefinition)
	 */
	@Override
	public RuleDefinition saveRuleDefinition(RuleDefinition ruleDefinition) {
		TokenService tokenService = Context.getService(TokenService.class);
		TokenRegistration existingRegistration = null;
		if (ruleDefinition.getId() != null)
			existingRegistration = tokenService.getTokenRegistrationByProviderAndConfiguration(ruleProvider, ruleDefinition.getId().toString());
		ruleDefinition = dao.saveRuleDefinition(ruleDefinition);
		boolean replace = existingRegistration != null && !existingRegistration.getProviderToken().equals(ruleDefinition.getName());
		if (replace)
			tokenService.deleteTokenRegistration(existingRegistration);
		if (replace || existingRegistration == null)
			tokenService.registerToken(ruleDefinition.getName(), ruleProvider, ruleDefinition.getId().toString());
		// make sure to clear any cached rules TokenService has (necessary if replace is false)
		tokenService.notifyRuleDefinitionChanged(ruleProvider, ruleDefinition.getName());
		return ruleDefinition;
	}

	/**
	 * @see RuleDefinitionService#purgeRuleDefinition(RuleDefinition)
	 */
	@Override
	public void purgeRuleDefinition(RuleDefinition ruleDefinition) {
		dao.deleteRuleDefinition(ruleDefinition);
		Context.getService(TokenService.class).removeToken(ruleProvider, ruleDefinition.getName());
	}

	/**
	 * @return the dao
	 */
	public RuleDefinitionDAO getDao() {
		return dao;
	}

	/**
	 * @param dao the dao to set
	 */
	public void setDao(RuleDefinitionDAO dao) {
		this.dao = dao;
	}

	/**
     * @see org.openmrs.logic.rule.definition.RuleDefinitionService#getAllLanguageHandlers()
     */
    @Override
	public List<LanguageHandler> getAllLanguageHandlers() {
	    return Collections.unmodifiableList(languageHandlers);
    }

	/**
     * @see org.openmrs.logic.rule.definition.RuleDefinitionService#getLanguageHandler(java.lang.String)
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
