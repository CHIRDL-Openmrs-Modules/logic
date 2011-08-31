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
package org.openmrs.logic.token.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.rule.ReferenceRule;
import org.openmrs.logic.rule.provider.RuleProvider;
import org.openmrs.logic.token.TokenRegistration;
import org.openmrs.logic.token.TokenService;
import org.openmrs.logic.token.db.TokenDAO;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Implementation of {@link TokenService}
 */
public class TokenServiceImpl extends BaseOpenmrsService implements TokenService {
	
	private final Log log = LogFactory.getLog(getClass());
	
	@Autowired
	private List<RuleProvider> ruleProviders;
	
	private TokenDAO dao;
	
	private Map<String, Rule> ruleCache = new HashMap<String, Rule>();
	
	/**
	 * @param dao the TokenDAO to set
	 */
	public void setDao(TokenDAO dao) {
		this.dao = dao;
	}
	
	/**
	 * @see org.openmrs.api.impl.BaseOpenmrsService#onStartup()
	 */
	@Override
	public void onStartup() {
		initialize();
	}
	
	/**
	 * @see org.openmrs.logic.token.TokenService#initialize()
	 */
	public void initialize() {
	    for (RuleProvider provider : ruleProviders) {
	    	startupProviderAsDaemon(provider);
	    }
	    ruleCache.clear();
	}
	
	/**
     * Calls provider.afterStartup() in a Daemon thread (or, if we are pre-OpenMRS 1.8 another way)
     * 
     * @param provider
     */
    private void startupProviderAsDaemon(final RuleProvider provider) {
		try {
			Method m = Class.forName("org.openmrs.api.context.Daemon").getMethod("runInNewDaemonThread");
			m.invoke(null, new Runnable() {
				public void run() {
					log.debug("Starting " + provider.getClass());
					provider.afterStartup();
					log.debug("Finished starting " + provider.getClass());
				};
			});
		} catch (IllegalAccessException ex) {
			startupProviderWithoutDaemon(provider);
		} catch (IllegalArgumentException ex) {
			startupProviderWithoutDaemon(provider);
		} catch (ClassNotFoundException ex) {
			startupProviderWithoutDaemon(provider);
		} catch (NoSuchMethodException ex) {
			startupProviderWithoutDaemon(provider);
		} catch (InvocationTargetException ex) {
			log.error("Error starting " + provider.getClass(), ex.getCause());
		}
    }

	/**
     * Starts up a rule provider without using Daemon.runInNewDaemonThread (pre OpenMRS 1.8)
     * 
     * @param provider
     */
    private void startupProviderWithoutDaemon(RuleProvider provider) {
	    try {
	    	provider.afterStartup();
	    } catch (Exception ex) {
	    	log.error("Error starting " + provider.getClass(), ex);
	    }
    }

	/**
	 * @see org.openmrs.logic.token.TokenService#getTokens(java.lang.String)
	 */
	public List<String> getTokens(String query) {
		return dao.getTokens(query);
	}
	
	/**
	 * @see org.openmrs.logic.token.TokenService#getCountOfTokenRegistrations(java.lang.String)
	 */
	public int getCountOfTokenRegistrations(String query) {
		return dao.getCountOfTokenRegistrations(query);
	}
	
	/**
	 * @see org.openmrs.logic.token.TokenService#getRule(java.lang.String)
	 */
	public Rule getRule(String token) {
		if (token == null)
			throw new LogicException("Token cannot be null");
		
		if (token.startsWith("%%")) {
			return new ReferenceRule(token.substring(2));
		}

		TokenRegistration tr = justOne(dao.getTokenRegistrations(token, null, null, null));
		if (tr == null) {
			throw new LogicException("Unregistered token: " + token);
		}
		
		return getRule(tr);
	}

	
	/**
     * @see org.openmrs.logic.token.TokenService#getRule(org.openmrs.logic.rule.provider.RuleProvider, java.lang.String)
     */
    public Rule getRule(RuleProvider provider, String providerToken) {
	    TokenRegistration tr = justOne(dao.getTokenRegistrations(null, provider, providerToken, null));
	    if (tr == null) {
	    	throw new LogicException("Cannot find token with provider=" + provider + " and providerToken=" + providerToken);
	    } 
	    return getRule(tr);
    }

    /**
     * Instantiates a {@link Rule}, given a TokenRegistration. Results are cached.
     * 
     * @param tokenRegistration
     * @return
     */
	private Rule getRule(TokenRegistration tokenRegistration) {
		RuleProvider provider = getRuleProvider(tokenRegistration);
		if (provider == null) {
			throw new LogicException("Token registered but provider missing: " + tokenRegistration.getToken() + " -> " + tokenRegistration.getProviderClassName());
		}
		
		String token = tokenRegistration.getToken();
		
		Rule cached = ruleCache.get(token);
		if (cached != null)
			return cached;
		
		Rule rule = provider.getRule(tokenRegistration.getConfiguration());
		if (rule != null) {
			ruleCache.put(token, rule);
		}
		
		return rule;
	}
	
	
	/**
     * @see org.openmrs.logic.token.TokenService#registerToken(java.lang.String, org.openmrs.logic.rule.provider.RuleProvider, java.lang.String)
     */
    public TokenRegistration registerToken(String token, RuleProvider provider, String configuration) {
    	TokenRegistration existing = getTokenRegistrationByProviderAndToken(provider, token);
    	if (existing == null) {
    		// we haven't registered this before
    		TokenRegistration registered = getTokenRegistrationByToken(token);
    		if (registered == null) {
    			// the requested token is available
    			TokenRegistration tr = new TokenRegistration(token, provider, configuration);
    	    	return Context.getService(TokenService.class).saveTokenRegistration(tr);
    		} else {
    			String newToken = findFreeToken(token);
    			TokenRegistration tr = new TokenRegistration(newToken, provider, configuration, token);
    			return Context.getService(TokenService.class).saveTokenRegistration(tr);
    		}
    	} else {
    		// we've already registered this token, so we overwrite that registration
    		if (existing.getConfiguration().equals(configuration)) {
    			ruleCache.remove(existing.getToken());
    			// don't do an unnecessary update if nothing has changed
    			return existing;
    		}
    		existing.setConfiguration(configuration);
    		return Context.getService(TokenService.class).saveTokenRegistration(existing);
    	}
    }
    
    /**
     * Find a free token similar to the one requested. (Assumes that requested itself is unavailable.)
     * 
     * @param requested
     * @return
     */
    private String findFreeToken(String requested) {
		int i = 1;
		while (true) {
			++i;
			String candidate = requested + " " + i;
			TokenRegistration attempt = getTokenRegistrationByToken(candidate);
			if (attempt == null)
				return candidate;
		}
    }
	
	/**
	 * @see org.openmrs.logic.token.TokenService#getTokenRegistration(java.lang.Integer)
	 */
	public TokenRegistration getTokenRegistration(Integer id) {
		return dao.getTokenRegistration(id);
	}
	
	/**
	 * @see org.openmrs.logic.token.TokenService#getTokenRegistrationByToken(java.lang.String)
	 */
	public TokenRegistration getTokenRegistrationByToken(String token) {
		return justOne(dao.getTokenRegistrations(token, null, null, null));
	}
	
	/**
	 * @see org.openmrs.logic.token.TokenService#getTokenRegistrationByUuid(java.lang.String)
	 */
	public TokenRegistration getTokenRegistrationByUuid(String uuid) {
		return dao.getTokenRegistrationByUuid(uuid);
	}
	
	/**
	 * @see org.openmrs.logic.token.TokenService#getTokenRegistrationByProviderAndToken(org.openmrs.logic.rule.provider.RuleProvider, java.lang.String)
	 */
	public TokenRegistration getTokenRegistrationByProviderAndToken(RuleProvider provider, String providerToken) {
	    return justOne(dao.getTokenRegistrations(null, provider, providerToken, null));
	}
	
    /**
     * @see org.openmrs.logic.token.TokenService#getTokenRegistrationByProviderAndConfiguration(org.openmrs.logic.rule.definition.RuleDefinitionRuleProvider, java.lang.String)
     */
    public TokenRegistration getTokenRegistrationByProviderAndConfiguration(RuleProvider provider,
                                                                            String configuration) {
        return justOne(dao.getTokenRegistrations(null, provider, null, configuration));
    }
	
	/**
	 * @see org.openmrs.logic.token.TokenService#getTokenRegistrations(java.lang.String, java.lang.Integer, java.lang.Integer)
	 */
	public List<TokenRegistration> getTokenRegistrations(String query, Integer start, Integer length) {
		return dao.getTokenRegistrations(query, start, length);
	}
	
	/**
	 * @see org.openmrs.logic.token.TokenService#removeToken(java.lang.String)
	 */
	public void removeToken(String token) {
		TokenRegistration tr = getTokenRegistrationByToken(token);
		if (tr != null)
			Context.getService(TokenService.class).deleteTokenRegistration(tr);
	}
	
	/**
	 * @see org.openmrs.logic.token.TokenService#removeToken(org.openmrs.logic.rule.definition.RuleDefinitionRuleProvider, java.lang.String)
	 */
	public void removeToken(RuleProvider provider, String providerToken) {
		TokenRegistration tr = getTokenRegistrationByProviderAndToken(provider, providerToken);
		if (tr != null)
			Context.getService(TokenService.class).deleteTokenRegistration(tr);
	}
	
	/**
	 * @see org.openmrs.logic.token.TokenService#saveTokenRegistration(org.openmrs.logic.token.TokenRegistration)
	 */
	public TokenRegistration saveTokenRegistration(TokenRegistration tokenRegistration) {
		TokenRegistration ret = dao.saveTokenRegistration(tokenRegistration);
		ruleCache.remove(tokenRegistration.getToken());
		return ret;
	}

	/**
	 * @see org.openmrs.logic.token.TokenService#deleteTokenRegistration(org.openmrs.logic.token.TokenRegistration)
	 */
    public void deleteTokenRegistration(TokenRegistration tokenRegistration) {
	    dao.deleteTokenRegistration(tokenRegistration);
	    ruleCache.remove(tokenRegistration.getToken());
    }

	/**
     * @see org.openmrs.logic.token.TokenService#getAllTokens()
     */
    public List<String> getAllTokens() {
	    return dao.getAllTokens();
    }

	/**
     * @see org.openmrs.logic.token.TokenService#getTags(java.lang.String)
     */
    public List<String> getTags(String partialTag) {
	    return dao.getTags(partialTag);
    }

	/**
     * @see org.openmrs.logic.token.TokenService#getTokensByTag(java.lang.String)
     */
    public List<String> getTokensByTag(String tag) {
	    return dao.getTokensByTag(tag);
    }

    /**
     * @see org.openmrs.logic.token.TokenService#getTokenRegistrationsByProvider(org.openmrs.logic.rule.provider.RuleProvider)
     */
    public List<TokenRegistration> getTokenRegistrationsByProvider(RuleProvider ruleProvider) {
        return dao.getTokenRegistrations(null, ruleProvider, null, null);
    }
    
    /**
     * Gets the provider registered for a given token registration. This will only
     * return providers that are active now, so if a module registered a token, then
     * the module was removed, this method will return null for that rule.
     * 
     * @param tokenRegistration
     * @return
     */
	private RuleProvider getRuleProvider(TokenRegistration tokenRegistration) {
	    for (RuleProvider provider : ruleProviders) {
	    	if (provider.getClass().getName().equals(tokenRegistration.getProviderClassName())) {
	    		return provider;
	    	}
	    }
	    return null;
    }

	/**
	 * @see org.openmrs.logic.token.TokenService#keepOnlyValidConfigurations(org.openmrs.logic.rule.provider.RuleProvider, java.util.Collection)
	 */
	public void keepOnlyValidConfigurations(RuleProvider provider, Collection<?> validConfigurations) {
	    List<String> validConfigsAsStrings = new ArrayList<String>();
	    for (Object o : validConfigurations)
	    	validConfigsAsStrings.add(o.toString());
	    dao.deleteConfigurationsNotIn(provider, validConfigsAsStrings);
	}
	
	/**
	 * @see org.openmrs.logic.token.TokenService#notifyRuleDefinitionChanged(org.openmrs.logic.rule.provider.RuleProvider, java.lang.String)
	 */
	public void notifyRuleDefinitionChanged(RuleProvider provider, String providerToken) {
		TokenRegistration tr = getTokenRegistrationByProviderAndToken(provider, providerToken);
		if (tr == null)
			throw new LogicException("No token registered for provider=" + provider.getClass().getName() + " providerToken=" + providerToken);
	    ruleCache.remove(tr.getToken());
	}
	
	private <T> T justOne(Collection<T> collection) {
		if (collection == null || collection.size() == 0)
			return null;
		else if (collection.size() == 1)
			return collection.iterator().next();
		else throw new LogicException("Should not return more than one");
	}

}
