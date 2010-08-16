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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Singleton class where all known class to convert to Rule object are registered. The arden and
 * java class are registered by default.
 */
public class LanguageHandlerInstance {
	
	private static Log log = LogFactory.getLog(LanguageHandlerInstance.class);
	
	private static LanguageHandlerInstance instance;
	
	private Map<String, LanguageHandler> languageHandlerMapping;
	
	private LanguageHandlerInstance() {
		registerDefaultHandler();
	}
	
	/**
	 * @return
	 */
	private synchronized static LanguageHandlerInstance getInstance() {
		if (instance == null)
			instance = new LanguageHandlerInstance();
		return instance;
	}
	
	private void registerDefaultHandler() {
		if (log.isDebugEnabled())
			log.debug("Registering default handler ...");
		getLanguageHandlerMapping().put("Java", new JavaLanguageHandler());
		getLanguageHandlerMapping().put("Arden", new ArdenLanguageHandler());
	}
	
	/**
	 * Return the value of the languageHandlerMapping
	 * 
	 * @return the languageHandlerMapping
	 */
	private Map<String, LanguageHandler> getLanguageHandlerMapping() {
		if (languageHandlerMapping == null)
			languageHandlerMapping = new HashMap<String, LanguageHandler>();
		return languageHandlerMapping;
	}
	
	/**
	 * Register a new handler to create Rule object
	 * 
	 * @param language the language name
	 * @param handler the handler class
	 */
	public static void registerHandler(String hint, LanguageHandler handler) {
		getInstance().getLanguageHandlerMapping().put(hint, handler);
	}
	
	/**
	 * Search for a language handler in all registered handler
	 * 
	 * @param languange the language name
	 * @return handler of the language or null if no handler is registered for the language
	 */
	public static LanguageHandler getHandler(String languange) {
		return getInstance().getLanguageHandlerMapping().get(languange);
	}
	
	/**
	 * Unregister a language handler
	 * 
	 * @param languange the language that will be unregistered
	 */
	public static void unregisterHandler(String languange) {
		getInstance().getLanguageHandlerMapping().remove(languange);
	}
	
}
