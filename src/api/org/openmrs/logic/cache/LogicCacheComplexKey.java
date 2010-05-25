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

import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.datasource.LogicDataSource;

import java.util.Date;
import java.util.Map;

/**
 *
 */
public class LogicCacheComplexKey {
    private Date indexDate;
    private Map<String, Object> parameters;
    private LogicCriteria criteria;
    private LogicDataSource dataSource;
    //private Operation

    public LogicCacheComplexKey() {
    }

    public LogicCacheComplexKey(Date indexDate, Map<String, Object> parameters, LogicCriteria criteria, LogicDataSource dataSource) {
        this.indexDate = indexDate;
        this.parameters = parameters;
        this.criteria = criteria;
        this.dataSource = dataSource;
    }

    public Date getIndexDate() {
        return indexDate;
    }

    public void setIndexDate(Date indexDate) {
        this.indexDate = indexDate;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public LogicCriteria getCriteria() {
        return criteria;
    }

    public void setCriteria(LogicCriteria criteria) {
        this.criteria = criteria;
    }

    public LogicDataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(LogicDataSource dataSource) {
        this.dataSource = dataSource;
    }
}
