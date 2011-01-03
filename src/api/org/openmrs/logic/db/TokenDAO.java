package org.openmrs.logic.db;

import java.util.List;

import org.openmrs.logic.TokenRegistration;
import org.openmrs.logic.TokenService;
import org.openmrs.logic.rule.provider.RuleProvider;

/**
 * Data access for TokenRegistration
 */
public interface TokenDAO {

	/**
	 * @see TokenService#getAllTokens()
	 */
	List<String> getAllTokens();

	/**
	 * @see TokenService#getTokens(String)
	 */
	List<String> getTokens(String query);

	/**
	 * @see TokenService#getCountOfTokenRegistrations(String)
	 */
	int getCountOfTokenRegistrations(String query);

	/**
	 * @see TokenService#getTokenRegistration(Integer)
	 */
	TokenRegistration getTokenRegistration(Integer id);

	/**
	 * @see TokenService#getTokenRegistrationByToken(String)
	 */
	TokenRegistration getTokenRegistrationByToken(String token);

	/**
	 * @see TokenService#getTokenRegistrationByUuid(String)
	 */
	TokenRegistration getTokenRegistrationByUuid(String uuid);
	
	/**
	 * @see TokenService#getTokenRegistrationByProvider(RuleProvider, String)
	 */
    TokenRegistration getTokenRegistrationByProvider(RuleProvider provider, String providerToken);

    /**
	 * @see TokenService#getTokenRegistrations(String, Integer, Integer)
	 */
	List<TokenRegistration> getTokenRegistrations(String query, Integer start, Integer length);
	
	/**
	 * @see TokenService#getTokenRegistrationsByProvider(RuleProvider)
	 */
    List<TokenRegistration> getTokenRegistrationsByProvider(RuleProvider ruleProvider);

    /**
	 * @see TokenService#saveTokenRegistration(TokenRegistration)
	 */
	TokenRegistration saveTokenRegistration(TokenRegistration tokenRegistration);
	
	/**
	 * @see TokenService#deleteTokenRegistration(TokenRegistration)
	 */
	void deleteTokenRegistration(TokenRegistration tokenRegistration);

	/**
	 * @see TokenService#getTags(String)
	 */
    List<String> getTags(String partialTag);
    
    /**
	 * @see TokenService#getTokensByTag(String)
	 */
    List<String> getTokensByTag(String tag);

    /**
	 * @see TokenService#keepOnlyValidConfigurations(RuleProvider, java.util.Collection)
	 */
    void deleteConfigurationsNotIn(RuleProvider provider, List<String> validConfigurations);

}
