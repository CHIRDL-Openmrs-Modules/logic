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
package org.openmrs.logic.token;

import java.util.Collection;
import java.util.List;

import org.openmrs.annotation.Authorized;
import org.openmrs.api.OpenmrsService;
import org.openmrs.logic.PrivilegeConstants;
import org.openmrs.logic.Rule;
import org.openmrs.logic.rule.provider.RuleProvider;

/**
 * Service used to register tokens, look them up, and get the rules registered to them [There are no @Transactional
 * annotations on this service because they are in HibernateTokenDAO.]
 */
public interface TokenService extends OpenmrsService {
	
	/**
	 * Run the afterStartup() method on all RuleProviders, generally getting them to re-register all
	 * their rules.
	 */
	void initialize();
	
	/**
	 * Gets a rule given a (user-facing) token. RuleProviders and modules should typically use
	 * {@link #getRule(RuleProvider, String)} instead, since there is no guarantee that the user
	 * hasn't changed token names since you registered a rule.
	 * 
	 * @param token
	 * @return
	 */
	Rule getRule(String token);
	
	/**
	 * Gets a rule given a provider and providerToken. RuleProviders and modules should typically
	 * use this method, since the providerToken cannot be changed by the user after you register a
	 * rule.
	 * 
	 * @param provider
	 * @param providerToken
	 * @return
	 */
	Rule getRule(RuleProvider provider, String providerToken);
	
	/**
	 * RuleProviders should call this method to register their rules
	 * <ul>
	 * <li>If there is already a TokenRegistration for this provider with providerToken=token, that
	 * will be overwritten.</li>
	 * <li>Otherwise, if token is not yet taken, the rule will be registered with the requested
	 * token.</li>
	 * <li>Otherwise, the rule will be registered with an autogenerated token name, and
	 * providerToken equal to the requested token</li>
	 * </ul>
	 * 
	 * @param token
	 * @param provider
	 * @param configuration
	 * @return the TokenRegistration created
	 * @should register a rule
	 * @should expire cached rule for this token
	 */
	@Authorized(PrivilegeConstants.MANAGE_TOKENS)
	TokenRegistration registerToken(String token, RuleProvider provider, String configuration);
	
	/**
	 * This service caches rules it gets from its {@link RuleProvider}s. If the definition of one of
	 * those rules changes (e.g. it is based on a Java file which is changed on disk) then you must
	 * call this method to clear it from the cache
	 * 
	 * @param provider
	 * @param providerToken
	 */
	void notifyRuleDefinitionChanged(RuleProvider provider, String providerToken);
	
	/**
	 * Deletes the TokenRegistration for the given token
	 * 
	 * @param token
	 */
	@Authorized(PrivilegeConstants.MANAGE_TOKENS)
	void removeToken(String token);
	
	/**
	 * Deletes the TokenRegistration for the given provider and providerToken
	 * 
	 * @param provider
	 * @param providerToken
	 */
	@Authorized(PrivilegeConstants.MANAGE_TOKENS)
	void removeToken(RuleProvider provider, String providerToken);
	
	/**
	 * Get all registered tokens
	 * 
	 * @return list of all registered rule tokens
	 */
	List<String> getAllTokens();
	
	/**
	 * Get a list of tokens matching an input token. The list will also contains partially matched
	 * token from set of registered tokens. Example: - AGE will match "AGE", "PAGE", "MAGE", "AGED"
	 * 
	 * @param query
	 * @return
	 */
	List<String> getTokens(String query);
	
	/**
	 * Get the TokenRegistration with the given primary key
	 * 
	 * @param id
	 * @return
	 */
	TokenRegistration getTokenRegistration(Integer id);
	
	/**
	 * Get the TokenRegistration with the given uuid
	 * 
	 * @param uuid
	 * @return
	 */
	TokenRegistration getTokenRegistrationByUuid(String uuid);
	
	/**
	 * Get the TokenRegistration with the given token
	 * 
	 * @param token
	 * @return
	 */
	TokenRegistration getTokenRegistrationByToken(String token);
	
	/**
	 * Get the TokenRegistration registered by the given provider under the given providerToken
	 * 
	 * @param provider
	 * @param providerToken
	 * @return
	 */
	TokenRegistration getTokenRegistrationByProviderAndToken(RuleProvider provider, String providerToken);
	
	/**
	 * Get the TokenRegistration registered by the given provider with the given configuration
	 * 
	 * @param ruleProvider
	 * @param configuration
	 * @return
	 */
	TokenRegistration getTokenRegistrationByProviderAndConfiguration(RuleProvider ruleProvider, String configuration);
	
	/**
	 * Save a TokenRegistration to the database.
	 * 
	 * @param tokenRegistration
	 * @return
	 */
	@Authorized(PrivilegeConstants.MANAGE_TOKENS)
	TokenRegistration saveTokenRegistration(TokenRegistration tokenRegistration);
	
	/**
	 * Remove a TokenRegistration from the database.
	 * 
	 * @param tokenRegistration
	 */
	@Authorized(PrivilegeConstants.MANAGE_TOKENS)
	void deleteTokenRegistration(TokenRegistration tokenRegistration);
	
	/**
	 * Gets a page of TokenRegistrations that match the given query
	 * 
	 * @param query
	 * @param start
	 * @param length
	 * @return
	 */
	List<TokenRegistration> getTokenRegistrations(String query, Integer start, Integer length);
	
	/**
	 * Counts how many TokenRegistrations match the given query
	 * 
	 * @param query
	 * @return
	 */
	int getCountOfTokenRegistrations(String query);
	
	/**
	 * Returns all tags that match a given string
	 * 
	 * @param partialTag any tags containing this string will be returned
	 * @return <code>List<String></code> of the matching tags
	 */
	List<String> getTags(String partialTag);
	
	/**
	 * Returns all tokens related to a given tag
	 * 
	 * @param tag <code>String</code> tag to search for
	 * @return <code>List<String></code> object of all tokens related to the given tag
	 */
	List<String> getTokensByTag(String tag);
	
	/**
	 * @param ruleProvider
	 * @return all token registrations registered by the given provider
	 */
	List<TokenRegistration> getTokenRegistrationsByProvider(RuleProvider ruleProvider);
	
	/**
	 * Used by a RuleProvider to delete all of its registered configurations that it no longer
	 * supports
	 * 
	 * @param provider
	 * @param validConfigurations any configurations not equal to the toString() of an element in
	 *            this collection will be removed
	 */
	@Authorized(PrivilegeConstants.MANAGE_TOKENS)
	void keepOnlyValidConfigurations(RuleProvider provider, Collection<?> validConfigurations);
	
	/**
	 * Gets the {@link RuleProvider} matching the specified configuration and associated to a
	 * provider with a class name matching the specified providerClassName
	 * 
	 * @param ruleProviderClassName the provider class name to match against
	 * @param ruleConfiguration the configuration to match against
	 * @return the matching RuleProvider
	 * @should get the rule matching the parameter values
	 */
	public Rule getRule(String ruleProviderClassName, String ruleConfiguration);
}
