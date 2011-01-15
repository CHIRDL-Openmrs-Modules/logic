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

import java.util.List;

import org.openmrs.annotation.Authorized;
import org.openmrs.api.OpenmrsService;
import org.openmrs.api.db.DAOException;
import org.openmrs.logic.PrivilegeConstants;
import org.openmrs.logic.token.TokenService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Provides access to user-specified {@link RuleDefinition}s
 */
public interface RuleDefinitionService extends OpenmrsService {
	
	/**
	 * @param id the primary key of the {@link RuleDefinition} to look up
	 * @return the {@link RuleDefinition} whose primary key id matches the passed id
	 * @throws DAOException
	 */
	@Transactional(readOnly=true)
	@Authorized(PrivilegeConstants.VIEW_RULE_DEFINITIONS)
	public RuleDefinition getRuleDefinition(Integer id);
	
	/**
	 * @param name the name of the {@link RuleDefinition} to look up
	 * @return the {@link RuleDefinition} whose name matches the passed name
	 * @throws DAOException
	 */
	@Transactional(readOnly=true)
	@Authorized(PrivilegeConstants.VIEW_RULE_DEFINITIONS)
	public RuleDefinition getRuleDefinition(String name);
	
	/**
     * @return all {@link RuleDefinition}s in the database, including retired ones
     */
	@Authorized(PrivilegeConstants.VIEW_RULE_DEFINITIONS)
    List<RuleDefinition> getAllRuleDefinitions();

    /**
	 * @param includeRetired if true, includes retired {@link RuleDefinition}s
	 * @return all {@link RuleDefinition}s saved to the database possibly including retired ones
	 * @throws DAOException
	 */
	@Transactional(readOnly=true)
	@Authorized(PrivilegeConstants.VIEW_RULE_DEFINITIONS)
	public List<RuleDefinition> getAllRuleDefinitions(boolean includeRetired);
	
	/**
	 * Does the following:
	 * <ol>
	 * <li>Saves a {@link RuleDefinition} to the database</li>
	 * <li>Registers it with {@link TokenService} (by ruleDefinition.name)</li>
	 * <li>Deletes any previous existing token (by ruleDefinition.id)</li>
	 * </ol>
	 * @param ruleDefinition the {@link RuleDefinition} to save to the database
	 * @return the saved {@link RuleDefinition}
	 * @throws DAOException
	 */
	@Transactional
	@Authorized(PrivilegeConstants.MANAGE_RULE_DEFINITIONS)
	public RuleDefinition saveRuleDefinition(RuleDefinition ruleDefinition);
	
	/**
	 * @param ruleDefinition the {@link RuleDefinition} to delete from the database
	 * @throws DAOException
	 */
	@Transactional
	@Authorized(PrivilegeConstants.MANAGE_RULE_DEFINITIONS)
	public void purgeRuleDefinition(RuleDefinition ruleDefinition);
	
	/**
	 * @return all available LanguageHandlers
	 */
	public List<LanguageHandler> getAllLanguageHandlers();
	
	/**
	 * @param name
	 * @return the LanguageHandler with the specified name
	 */
	public LanguageHandler getLanguageHandler(String name);
	
}
