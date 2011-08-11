package org.openmrs.logic.token.db;

import org.openmrs.logic.rule.provider.RuleProvider;
import org.openmrs.logic.token.TokenRegistration;
import org.openmrs.logic.token.TokenService;

import java.util.List;

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
	 * @see TokenService#getTokenRegistrationByUuid(String)
	 */
	TokenRegistration getTokenRegistrationByUuid(String uuid);
	
    /**
	 * @see TokenService#getTokenRegistrations(String, Integer, Integer)
	 */
	List<TokenRegistration> getTokenRegistrations(String query, Integer start, Integer length);

    /**
     * Returns all TokenRegistrations that match the specified (nullable) criteria
     * 
     * @param token
     * @param provider
     * @param providerToken
     * @param configuration
     * @return
     */
    List<TokenRegistration> getTokenRegistrations(String token, RuleProvider provider, String providerToken, String configuration);

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
