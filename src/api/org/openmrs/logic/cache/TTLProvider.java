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
package org.openmrs.logic.cache;

import org.apache.commons.lang.StringUtils;
import org.openmrs.logic.datasource.EncounterDataSource;
import org.openmrs.logic.datasource.ObsDataSource;
import org.openmrs.logic.datasource.PersonDataSource;
import org.openmrs.logic.datasource.ProgramDataSource;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class TTLProvider {
    private static final int ENCOUNTER_DS_TTL = 60 * 30;   // 30 minutes
    private static final int OBS_DS_TTL = 60 * 30;           // 30 minutes
    private static final int PERSON_DS_TTL = 60 * 60 * 4;   // 4 hours
    private static final int PROGRAM_DS_TTL = 60 * 30;     // 30 minutes
    private static final int AGE_RULE_TTL = 60 * 60 * 24;    // 1 day
    private static final int ENROLLED_BEFORE_DATE_RULE_TTL = 0;    // 1 day
    private static final int HIV_POSITIVE_RULE_TTL = 60 * 60;    // 30 day
    
    //TODO: dataSource.getDefaultTTL();
    private static final int REFERENCE_RULE_TTL = 0;    // ? according to the datasource

    private static int DEFAULT_TTL = 60 * 30;         // 30 minutes

    private static Map<String, Integer> ttl;

    static {
        ttl = new HashMap<String, Integer>();
        ttl.put(EncounterDataSource.class.getCanonicalName(), ENCOUNTER_DS_TTL);
        ttl.put(PersonDataSource.class.getCanonicalName(), PERSON_DS_TTL);
        ttl.put(ObsDataSource.class.getCanonicalName(), OBS_DS_TTL);
        ttl.put(ProgramDataSource.class.getCanonicalName(), PROGRAM_DS_TTL);
    }

    public static int getTTL(Object object) {
        if(null != object && ttl.containsKey(object.getClass().getCanonicalName()))
            return ttl.get(object.getClass().getCanonicalName());

        return DEFAULT_TTL;
    }

    public static void updateTTL(String key, Integer ttlValue) throws IllegalArgumentException {
        if(StringUtils.isBlank(key) || null == ttlValue)
            throw new IllegalArgumentException("key and ttl must be not null.");

        ttl.put(key, ttlValue);
    }

    public static void setDefaultTTL(int defaultTTL) {
        TTLProvider.DEFAULT_TTL = defaultTTL;




    }

    public static void removeTTL(String key) {
        if(StringUtils.isBlank(key))
            return;

        ttl.remove(key);
    }
}
