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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 *   This bean is used to create complex keys. To cache result of the
 * {@link org.openmrs.logic.LogicContextImpl#eval(org.openmrs.Patient, org.openmrs.logic.LogicCriteria, java.util.Map)} method or the
 * {@link org.openmrs.logic.LogicContextImpl#read(org.openmrs.Patient, org.openmrs.logic.datasource.LogicDataSource, org.openmrs.logic.LogicCriteria)}
 * method we need to include into the key all parameters result depends on.
 * <p/>This class and all it`s properties are serializable to store cached elements (key, value) to a disk store. As well as all properties are serializable
 * they must have equals and hashCode methods.
 * <p/>It has indexDate field like {@link org.openmrs.logic.LogicContext}, but stores it truncated to days {@link LogicCacheKey#truncDate(java.util.Date)}
 * for comparison if indexDate was for the "past time". 
 */
public class LogicCacheKey implements Serializable {
    private Map<String, Object> parameters;
    private LogicCriteria criteria;
    private String dataSource;
    private Date indexDate;
    private Integer patientId;
    private Set<Integer> memberIds;

    public LogicCacheKey() {
    }

    public LogicCacheKey(Map<String, Object> parameters, LogicCriteria criteria, LogicDataSource dataSource, Date indexDate, Integer patientId) {
        this.parameters = parameters;
        this.criteria = criteria;
        this.dataSource = dataSource != null ? dataSource.getClass().getCanonicalName() : null;
        this.indexDate = truncDate(indexDate);
        this.patientId = patientId;
    }

    public LogicCacheKey(Map<String, Object> parameters, LogicCriteria criteria, LogicDataSource dataSource, Date indexDate, Set<Integer> memberIds) {
        this.parameters = parameters;
        this.criteria = criteria;
        this.dataSource = dataSource != null ? dataSource.getClass().getCanonicalName() : null;
        this.indexDate = truncDate(indexDate);
        this.memberIds = memberIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LogicCacheKey that = (LogicCacheKey) o;

        if (criteria != null ? !criteria.equals(that.criteria) : that.criteria != null) return false;
        if (dataSource != null ? !dataSource.equals(that.dataSource) : that.dataSource != null) return false;
        if (indexDate != null ? !indexDate.equals(that.indexDate) : that.indexDate != null) return false;
        if (memberIds != null ? !Arrays.equals(memberIds.toArray(), that.memberIds.toArray()) : that.memberIds != null) return false;
        if (parameters != null ? !parameters.equals(that.parameters) : that.parameters != null) return false;
        if (patientId != null ? !patientId.equals(that.patientId) : that.patientId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = parameters != null ? parameters.hashCode() : 0;
        result = 31 * result + (criteria != null ? criteria.hashCode() : 0);
        result = 31 * result + (dataSource != null ? dataSource.hashCode() : 0);
        result = 31 * result + (indexDate != null ? indexDate.hashCode() : 0);
        result = 31 * result + (patientId != null ? patientId.hashCode() : 0);
        result = 31 * result + (memberIds != null ? memberIds.hashCode() : 0);
        return result;
    }

    /**
         *   Truncates date to days. If indexDate`d put '2010-07-16 20:38:40' it would be '2010-07-16 00:00:00'
         * 
         * @param date - date from {@link org.openmrs.logic.LogicContext#getIndexDate()}
         * @return truncated to days date
         */
    private Date truncDate(Date date) {
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
        this.indexDate = truncDate(indexDate);
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
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

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public Set<Integer> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(Set<Integer> memberIds) {
        this.memberIds = memberIds;
    }
}
