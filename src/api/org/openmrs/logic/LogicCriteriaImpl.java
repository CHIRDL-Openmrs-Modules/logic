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
package org.openmrs.logic;

import java.util.Date;
import java.util.Map;

import org.openmrs.logic.op.Operand;
import org.openmrs.logic.op.OperandDate;
import org.openmrs.logic.op.OperandNumeric;
import org.openmrs.logic.op.OperandText;
import org.openmrs.logic.op.Operator;

/**
 * Used to create a hierarchical representation of a criteria (e.g., similar to a parse tree).
 * Criteria can be generated through a series of method calls; each method call returns another
 * criteria object containing the prior criteria and the newly added criteria. This class has two
 * purposes:
 * <ol>
 * <li>provide a mechanism for building criteria</li>
 * <li>provide a structure that can be passed to the DAO level for analysis &amp; execution</li>
 * </ol>
 * In its simplest form, a criteria is equivalent to a token &mdash; e.g., the following two methods
 * should return the same result:
 * <ul>
 * <li><code>LogicService.eval(myPatient, "CD4 COUNT");</code></li>
 * <li><code>LogicService.eval(myPatient, new LogicCriteria("CD4 COUNT"));</code></li>
 * </ul>
 * However, when criteria or restrictions need to be placed on the token, then a LogicCriteria can
 * be used to define these restrictions, e.g.
 * <code>new LogicCriteriaImpl("CD4 COUNT").lt(200).within(Duration.months(6))</code>
 */

public class LogicCriteriaImpl implements LogicCriteria {
	
	private Map<String, Object> logicParameters = null;
	
	private LogicExpression expression = null;
	
	/**
	 * Used for creating a simple token-based criteria, which can later be refined by using
	 * LogicCriteria methods.
	 * 
	 * @param token
	 */
	public LogicCriteriaImpl(String token) {
		this(null, new OperandText(token));
	}
	
	/**
	 * Used for passing arguments to a rule
	 * 
	 * @param token <code>String</code> token
	 * @param logicParameters <code>Map</code> of the parameters
	 */
	public LogicCriteriaImpl(String token, Map<String, Object> logicParameters) {
		this(token);
		this.logicParameters = logicParameters;
	}
	
	public LogicCriteriaImpl(Operator operator, Operand operand) {
		if (operator == Operator.NOT) {
			this.expression = new LogicExpressionUnary(operand, operator);
			
		} else {
			this.expression = new LogicExpressionBinary(null, operand, operator);
		}
	}
	
	public LogicCriteriaImpl(Operator operator, String operand) {
		this(operator, new OperandText(operand));
	}
	
	public LogicCriteriaImpl(Operator operator, Operand operand, Map<String, Object> logicParameters) {
		this(operator, operand);
		this.logicParameters = logicParameters;
	}
	
	/**
	 * @see org.openmrs.logic.LogicCriteria#appendExpression(org.openmrs.logic.op.Operator,
	 *      java.lang.Object)
	 */
	public LogicCriteria appendExpression(Operator operator, Operand operand) {
		this.expression = new LogicExpressionBinary(this.expression, operand, operator);
		
		return this;
	}
	
	public LogicCriteria appendExpression(Operator operator, String operand) {
		return appendExpression(operator, new OperandText(operand));
	}
	
	public LogicCriteria appendExpression(Operator operator, double operand) {
		return appendExpression(operator, new OperandNumeric(operand));
	}
	
	private LogicCriteria appendExpression(Operator operator, LogicExpression expression) {
		if (expression != null) {
			this.expression = new LogicExpressionBinary(this.expression, expression, operator);
		} else {
			this.expression = new LogicExpressionUnary(this.expression, operator);
		}
		
		return this;
	}
	
	private LogicCriteria appendTransform(Operator operator, Integer numResults, String sortColumn) {
		
		LogicTransform transform = new LogicTransform(operator);
		if (numResults != null) {
			transform.setNumResults(numResults);
		}
		if (sortColumn != null) {
			transform.setSortColumn(sortColumn);
		}
		this.expression.setTransform(transform);
		
		return this;
	}
	
	/**
	 * @see org.openmrs.logic.LogicCriteria#applyTransform(org.openmrs.logic.op.Operator)
	 */
	public LogicCriteria applyTransform(Operator operator) {
		if (operator == Operator.LAST) {
			return last();
		} else if (operator == Operator.FIRST) {
			return first();
		} else if (operator == Operator.EXISTS) {
			return exists();
		} else if (operator == Operator.NOT_EXISTS) {
			return notExists();
		} else if (operator == Operator.COUNT) {
			return count();
		} else if (operator == Operator.AVERAGE) {
			return average();
		}
		
		return this; // no valid transform
		
	}
	
	// --Logic Operators joining criteria
	/**
	 * @see org.openmrs.logic.LogicCriteria#appendCriteria(org.openmrs.logic.op.Operator,
	 *      org.openmrs.logic.LogicCriteria)
	 */
	public LogicCriteria appendCriteria(Operator operator, LogicCriteria logicCriteria) {
		return appendExpression(operator, logicCriteria.getExpression());
	}
	
	/**
	 * @see org.openmrs.logic.LogicCriteria#and(org.openmrs.logic.LogicCriteria)
	 */
	public LogicCriteria and(LogicCriteria logicCriteria) {
		return appendExpression(Operator.AND, logicCriteria.getExpression());
	}
	
	/**
	 * @see org.openmrs.logic.LogicCriteria#or(org.openmrs.logic.LogicCriteria)
	 */
	public LogicCriteria or(LogicCriteria logicCriteria) {
		return appendExpression(Operator.OR, logicCriteria.getExpression());
	}
	
	/**
	 * @see org.openmrs.logic.LogicCriteria#not()
	 */
	public LogicCriteria not() {
		return appendExpression(Operator.NOT, (Operand) null);
	}
	
	//--Transform Operators
	/**
	 * @see org.openmrs.logic.LogicCriteria#count()
	 */
	public LogicCriteria count() {
		return this.appendTransform(Operator.COUNT, null, null);
	}
	
	/**
	 * @see org.openmrs.logic.LogicCriteria#average()
	 */
	public LogicCriteria average() {
		return this.appendTransform(Operator.AVERAGE, null, null);
	}
	
	/**
	 * @see org.openmrs.logic.LogicCriteria#last()
	 */
	public LogicCriteria last() {
		return this.appendTransform(Operator.LAST, null, null);
	}
	
	/**
	 * @see org.openmrs.logic.LogicCriteria#last(java.lang.Integer)
	 */
	public LogicCriteria last(Integer numResults) {
		return this.appendTransform(Operator.LAST, numResults, null);
	}
	
	//TODO implement this method
	//after implementing switch to public
	@SuppressWarnings("unused")
	private LogicCriteria last(String sortComponent) {
		return this.appendTransform(Operator.LAST, null, sortComponent);
	}
	
	//TODO implement this method
	//after implementing switch to public
	@SuppressWarnings("unused")
	private LogicCriteria last(Integer numResults, String sortComponent) {
		return this.appendTransform(Operator.LAST, numResults, sortComponent);
	}
	
	/**
	 * @see org.openmrs.logic.LogicCriteria#first()
	 */
	public LogicCriteria first() {
		return this.appendTransform(Operator.FIRST, null, null);
	}
	
	/**
	 * @see org.openmrs.logic.LogicCriteria#first(java.lang.Integer)
	 */
	public LogicCriteria first(Integer numResults) {
		return this.appendTransform(Operator.FIRST, numResults, null);
	}
	
	/**
	 * @see org.openmrs.logic.LogicCriteria#first(java.lang.String)
	 */
	public LogicCriteria first(String sortComponent) {
		return this.appendTransform(Operator.FIRST, null, sortComponent);
	}
	
	/**
	 * @see org.openmrs.logic.LogicCriteria#first(java.lang.Integer, java.lang.String)
	 */
	public LogicCriteria first(Integer numResults, String sortComponent) {
		return this.appendTransform(Operator.FIRST, numResults, sortComponent);
	}
	
	/**
	 * @see org.openmrs.logic.LogicCriteria#distinct()
	 */
	public LogicCriteria distinct() {
		return this.appendTransform(Operator.DISTINCT, null, null);
	}
	
	/**
	 * @see org.openmrs.logic.LogicCriteria#exists()
	 */
	public LogicCriteria exists() {
		return this.appendTransform(Operator.EXISTS, null, null);
	}
	
	/**
	 * @see org.openmrs.logic.LogicCriteria#notExists()
	 */
	public LogicCriteria notExists() {
		return this.appendTransform(Operator.NOT_EXISTS, null, null);
	}
	
	//--Comparison Operators
	/**
	 * @see org.openmrs.logic.LogicCriteria#asOf(java.util.Date)
	 */
	public LogicCriteria asOf(Date value) {
		return appendExpression(Operator.ASOF, new OperandDate(value));
	}
	
	/**
	 * @see org.openmrs.logic.LogicCriteria#before(java.util.Date)
	 */
	public LogicCriteria before(Date value) {
		
		return appendExpression(Operator.BEFORE, new OperandDate(value));
	}
	
	/**
	 * @see org.openmrs.logic.LogicCriteria#after(java.util.Date)
	 */
	public LogicCriteria after(Date value) {
		return appendExpression(Operator.AFTER, new OperandDate(value));
	}
	
	/**
	 * @see org.openmrs.logic.LogicCriteria#contains(org.openmrs.logic.op.Operand)
	 */
	public LogicCriteria contains(Operand value) {
		return appendExpression(Operator.CONTAINS, value);
	}
	
	/**
	 * @see org.openmrs.logic.LogicCriteria#contains(int)
	 */
	public LogicCriteria contains(int value) {
		return appendExpression(Operator.CONTAINS, new OperandNumeric(value));
	}
	
	/**
	 * @see org.openmrs.logic.LogicCriteria#contains(float)
	 */
	public LogicCriteria contains(float value) {
		return appendExpression(Operator.CONTAINS, new OperandNumeric(value));
	}
	
	/**
	 * @see org.openmrs.logic.LogicCriteria#contains(double)
	 */
	public LogicCriteria contains(double value) {
		return appendExpression(Operator.CONTAINS, new OperandNumeric(value));
	}
	
	/**
	 * @see org.openmrs.logic.LogicCriteria#contains(String)
	 */
	public LogicCriteria contains(String value) {
		return appendExpression(Operator.CONTAINS, new OperandText(value));
	}
	
	/**
	 * @see org.openmrs.logic.LogicCriteria#equalTo(org.openmrs.logic.op.Operand)
	 */
	public LogicCriteria equalTo(Operand value) {
		return appendExpression(Operator.EQUALS, value);
	}
	
	/**
	 * @see org.openmrs.logic.LogicCriteria#equalTo(int)
	 */
	public LogicCriteria equalTo(int value) {
		return appendExpression(Operator.EQUALS, new OperandNumeric(value));
	}
	
	/**
	 * @see org.openmrs.logic.LogicCriteria#equalTo(float)
	 */
	public LogicCriteria equalTo(float value) {
		return appendExpression(Operator.EQUALS, new OperandNumeric(value));
	}
	
	/**
	 * @see org.openmrs.logic.LogicCriteria#equalTo(double)
	 */
	public LogicCriteria equalTo(double value) {
		return appendExpression(Operator.EQUALS, new OperandNumeric(value));
	}
	
	/**
	 * @see org.openmrs.logic.LogicCriteria#equalTo(String)
	 */
	public LogicCriteria equalTo(String value) {
		return appendExpression(Operator.EQUALS, new OperandText(value));
	}
	
	/**
	 * @see org.openmrs.logic.LogicCriteria#gte(org.openmrs.logic.op.Operand)
	 */
	public LogicCriteria gte(Operand value) {
		return appendExpression(Operator.GTE, value);
	}
	
	/**
	 * @see org.openmrs.logic.LogicCriteria#gte(int)
	 */
	public LogicCriteria gte(int value) {
		return appendExpression(Operator.GTE, new OperandNumeric(value));
	}
	
	/**
	 * @see org.openmrs.logic.LogicCriteria#gte(float)
	 */
	public LogicCriteria gte(float value) {
		return appendExpression(Operator.GTE, new OperandNumeric(value));
	}
	
	/**
	 * @see org.openmrs.logic.LogicCriteria#gte(double)
	 */
	public LogicCriteria gte(double value) {
		return appendExpression(Operator.GTE, new OperandNumeric(value));
	}
	
	/**
	 * @see org.openmrs.logic.LogicCriteria#gt(org.openmrs.logic.op.Operand)
	 */
	public LogicCriteria gt(Operand value) {
		return appendExpression(Operator.GT, value);
	}
	
	/**
	 * @see org.openmrs.logic.LogicCriteria#gt(int)
	 */
	public LogicCriteria gt(int value) {
		return appendExpression(Operator.GT, new OperandNumeric(value));
	}
	
	/**
	 * @see org.openmrs.logic.LogicCriteria#gt(float)
	 */
	public LogicCriteria gt(float value) {
		return appendExpression(Operator.GT, new OperandNumeric(value));
	}
	
	/**
	 * @see org.openmrs.logic.LogicCriteria#gt(double)
	 */
	public LogicCriteria gt(double value) {
		return appendExpression(Operator.GT, new OperandNumeric(value));
	}
	
	/**
	 * @see org.openmrs.logic.LogicCriteria#lt(org.openmrs.logic.op.Operand)
	 */
	public LogicCriteria lt(Operand value) {
		return appendExpression(Operator.LT, value);
	}
	
	/**
	 * @see org.openmrs.logic.LogicCriteria#lt(int)
	 */
	public LogicCriteria lt(int value) {
		return appendExpression(Operator.LT, new OperandNumeric(value));
	}
	
	/**
	 * @see org.openmrs.logic.LogicCriteria#lt(float)
	 */
	public LogicCriteria lt(float value) {
		return appendExpression(Operator.LT, new OperandNumeric(value));
	}
	
	/**
	 * @see org.openmrs.logic.LogicCriteria#lt(double)
	 */
	public LogicCriteria lt(double value) {
		return appendExpression(Operator.LT, new OperandNumeric(value));
	}
	
	/**
	 * @see org.openmrs.logic.LogicCriteria#lte(org.openmrs.logic.op.Operand)
	 */
	public LogicCriteria lte(Operand value) {
		return appendExpression(Operator.LTE, value);
	}
	
	/**
	 * @see org.openmrs.logic.LogicCriteria#lte(int)
	 */
	public LogicCriteria lte(int value) {
		return appendExpression(Operator.LTE, new OperandNumeric(value));
	}
	
	/**
	 * @see org.openmrs.logic.LogicCriteria#lte(float)
	 */
	public LogicCriteria lte(float value) {
		return appendExpression(Operator.LTE, new OperandNumeric(value));
	}
	
	/**
	 * @see org.openmrs.logic.LogicCriteria#lte(double)
	 */
	public LogicCriteria lte(double value) {
		return appendExpression(Operator.LTE, new OperandNumeric(value));
	}
	
	/**
	 * @see org.openmrs.logic.LogicCriteria#within(org.openmrs.logic.Duration)
	 */
	public LogicCriteria within(Duration duration) {
		return appendExpression(Operator.WITHIN, duration);
	}
	
	/**
	 * @see org.openmrs.logic.LogicCriteria#getLogicParameters()
	 */
	public Map<String, Object> getLogicParameters() {
		return logicParameters;
	}
	
	/**
	 * @see org.openmrs.logic.LogicCriteria#setLogicParameters(java.util.Map)
	 */
	public void setLogicParameters(Map<String, Object> logicParameters) {
		this.logicParameters = logicParameters;
	}
	
	/**
	 * @see org.openmrs.logic.LogicCriteria#toString()
	 */
	public String toString() {
		return this.expression.toString();
	}
	
	/**
	 * @see org.openmrs.logic.LogicCriteria#getRootToken()
	 */
	public String getRootToken() {
		return this.expression.getRootToken();
	}
	
	/**
	 * Parses a query string into a LogicCriteria object. For example, a phrase like <em>"LAST
	 * {CD4 COUNT} > 200"</em> is parsed into a LogicCriteria object equivalent to:
	 * <code>new LogicCriteria("CD4 COUNT").gt(200).last()</code>. This function will fail quietly.
	 * If an exception occurs during parsing, then this method will return a LogicCriteria
	 * constructed with the given query string without any parsing. The actual work of parsing is
	 * performed by the LogicQueryParser class.
	 * 
	 * @param query a logic query to be parsed
	 * @return the equivalent LogicCriteria to the given query string
	 * @throws LogicException
	 * @see org.openmrs.logic.LogicQueryParser
	 */
	public static LogicCriteria parse(String query) throws LogicException {
		try {
			return LogicQueryParser.parse(query);
		}
		catch (LogicQueryParseException e) {
			return new LogicCriteriaImpl(query);
		}
	}
	
	/**
	 * @see org.openmrs.logic.LogicCriteria#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		
		result = prime * result + ((this.expression == null) ? 0 : this.expression.hashCode());
		result = prime * result + ((logicParameters == null) ? 0 : logicParameters.hashCode());
		return result;
	}
	
	/**
	 * @see org.openmrs.logic.LogicCriteria#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		
		if (!(obj instanceof LogicCriteriaImpl)) {
			return false;
		}
		
		LogicCriteria compCriteria = (LogicCriteria) obj;
		
		if (!safeEquals(this.expression, compCriteria.getExpression())) {
			return false;
		}
		
		if (!safeEquals(this.logicParameters, compCriteria.getLogicParameters())) {
			return false;
		}
		
		return true;
	}
	
	private boolean safeEquals(Object a, Object b) {
		if (a == null && b == null)
			return true;
		if (a == null || b == null)
			return false;
		return a.equals(b);
	}
	
	/**
	 * @see org.openmrs.logic.LogicCriteria#getExpression()
	 */
	public LogicExpression getExpression() {
		return expression;
	}
}
