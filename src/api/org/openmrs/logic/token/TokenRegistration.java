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

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.openmrs.Auditable;
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.User;
import org.openmrs.logic.rule.provider.RuleProvider;

/**
 * This class represents a token that has been registered, plus instructions on how to instantiate
 * a rule from that token.<br/>
 * Each registered token has a {@link RuleProvider} and a configuration (set by the provider) that the
 * provider users to instantiate a rule.<br/>
 * A registered token also stores (as the providerToken property) the original token name that a
 * RuleProvider requested, which may differ from the registered token if the user edits this
 * TokenRegistration or if the requested token was already in use.<br/> 
 * A token may also have multiple tags associated with it. 
 */
public class TokenRegistration extends BaseOpenmrsObject implements Auditable {
	
	private Integer tokenRegistrationId;
	
	private User creator;
	
	private Date dateCreated;
	
	private User changedBy;
	
	private Date dateChanged;
	
	// token to be registered
	private String token;
	
	// class name of the provider that will be used to instantiate the rule
	private String providerClassName;
	
	// configuration that the provider will use to instantiate the rule
	private String configuration;
	
	// what the RuleProvider or module originally tried to register this token as. (It can differ from
	// token if the user changes it, or if the requested token was already taken by another provider.)
	private String providerToken;
	
	// tags associated with this token
	private Set<String> tags;
	
	/**
     * Default constructor
     */
	public TokenRegistration() {
	}
		
	/**
	 * Sets providerToken = token
     * @param token
     * @param provider
     * @param configuration
     */
    public TokenRegistration(String token, RuleProvider provider, String configuration) {
	    this.token = token;
	    this.providerClassName = provider.getClass().getName();
	    this.configuration = configuration;
	    this.setProviderToken(token);
    }

	/**
     * @param token
     * @param provider
     * @param configuration
     * @param providerToken
     */
    public TokenRegistration(String token, RuleProvider provider, String configuration, String providerToken) {
	    this(token, provider, configuration);
	    this.providerToken = providerToken;
    }

	/**
	 * @see org.openmrs.Auditable#getChangedBy()
	 */
	public User getChangedBy() {
		return changedBy;
	}
	
	/**
	 * @see org.openmrs.Auditable#getCreator()
	 */
	public User getCreator() {
		return creator;
	}
	
	/**
	 * @see org.openmrs.Auditable#getDateChanged()
	 */
	public Date getDateChanged() {
		return dateChanged;
	}
	
	/**
	 * @see org.openmrs.Auditable#getDateCreated()
	 */
	public Date getDateCreated() {
		return dateCreated;
	}
	
	/**
	 * @see org.openmrs.Auditable#setChangedBy(org.openmrs.User)
	 */
	public void setChangedBy(User changedBy) {
		this.changedBy = changedBy;
	}
	
	/**
	 * @see org.openmrs.Auditable#setCreator(org.openmrs.User)
	 */
	public void setCreator(User creator) {
		this.creator = creator;
	}
	
	/**
	 * @see org.openmrs.Auditable#setDateChanged(java.util.Date)
	 */
	public void setDateChanged(Date dateChanged) {
		this.dateChanged = dateChanged;
	}
	
	/**
	 * @see org.openmrs.Auditable#setDateCreated(java.util.Date)
	 */
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	
	/**
	 * @see org.openmrs.OpenmrsObject#getId()
	 */
	public Integer getId() {
		return getTokenRegistrationId();
	}
	
	/**
	 * @see org.openmrs.OpenmrsObject#setId(java.lang.Integer)
	 */
	public void setId(Integer id) {
		setTokenRegistrationId(id);
	}
	
	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}
	
	/**
	 * @param token the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}
	
	/**
	 * @return the providerClassName
	 */
	public String getProviderClassName() {
		return providerClassName;
	}
	
	/**
	 * @param providerClassName the providerClassName to set
	 */
	public void setProviderClassName(String providerClassName) {
		this.providerClassName = providerClassName;
	}
	
	/**
	 * @return the configuration
	 */
	public String getConfiguration() {
		return configuration;
	}
	
	/**
	 * @param state the configuration to set
	 */
	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}
	
	/**
     * @param providerToken the providerToken to set
     */
    public void setProviderToken(String providerToken) {
	    this.providerToken = providerToken;
    }

	/**
     * @return the providerToken
     */
    public String getProviderToken() {
	    return providerToken;
    }

	/**
	 * @return the tags
	 */
	public Set<String> getTags() {
		if (tags == null)
			tags = new HashSet<String>();
		return tags;
	}
	
	/**
	 * @param tags the tags to set
	 */
	public void setTags(Set<String> tags) {
		this.tags = tags;
	}
	
	/**
	 * @return the tokenId
	 */
	public Integer getTokenRegistrationId() {
		return tokenRegistrationId;
	}
	
	/**
	 * @param tokenId the tokenId to set
	 */
	public void setTokenRegistrationId(Integer tokenId) {
		this.tokenRegistrationId = tokenId;
	}
	
	/**
	 * @param tag the tag that will be added to the set of tags for this token
	 */
	public void addTag(String tag) {
		getTags().add(tag);
	}
	
	/**
	 * @param tag the tag to be removed from set of tags for this token
	 */
	public void removeTag(String tag) {
		getTags().remove(tag);
	}
	
	/**
	 * @param tag
	 * @return
	 */
	public boolean hasTag(String tag) {
		return getTags().contains(tag);
	}
}
