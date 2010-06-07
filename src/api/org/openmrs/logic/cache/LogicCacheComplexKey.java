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

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public class LogicCacheComplexKey implements Serializable {
    private Map<String, Object> parameters;
    private LogicCriteria criteria;
    private LogicDataSource dataSource;
    private Date indexDate;
    private Set<Integer> membersIds;

    public LogicCacheComplexKey() {
    }

    public LogicCacheComplexKey(Map<String, Object> parameters, LogicCriteria criteria, LogicDataSource dataSource, Date indexDate, Set<Integer> membersIds) {
        this.parameters = parameters;
        this.criteria = criteria;
        this.dataSource = dataSource;

        this.indexDate = updateTime(indexDate);


        this.membersIds = membersIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LogicCacheComplexKey that = (LogicCacheComplexKey) o;
        that.indexDate = updateTime(that.indexDate);

        if (criteria != null ? !criteria.equals(that.criteria) : that.criteria != null) return false;
        if (dataSource != null ? !dataSource.equals(that.dataSource) : that.dataSource != null) return false;
        if (indexDate != null ? !indexDate.equals(that.indexDate) : that.indexDate != null) return false;
        if (membersIds != null ? !membersIds.equals(that.membersIds) : that.membersIds != null) return false;
        if (parameters != null ? !parameters.equals(that.parameters) : that.parameters != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = parameters != null ? parameters.hashCode() : 0;
        result = 31 * result + (criteria != null ? criteria.hashCode() : 0);
        result = 31 * result + (dataSource != null ? dataSource.hashCode() : 0);
        result = 31 * result + (indexDate != null ? indexDate.hashCode() : 0);
        result = 31 * result + (membersIds != null ? membersIds.hashCode() : 0);
        return result;
    }

    private Date updateTime(Date date) {
        if(null == date) return null;
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    public Date getIndexDate() {
        return indexDate;
    }

    public void setIndexDate(Date indexDate) {
        this.indexDate = updateTime(indexDate);
    }

    public Set<Integer> getMembersIds() {
        return membersIds;
    }

    public void setMembersIds(Set<Integer> membersIds) {
        this.membersIds = membersIds;
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