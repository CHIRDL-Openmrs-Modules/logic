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
package org.openmrs.logic.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.logic.Duration;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.LogicExpression;
import org.openmrs.logic.LogicExpressionBinary;
import org.openmrs.logic.op.ComparisonOperator;
import org.openmrs.logic.op.Operand;
import org.openmrs.logic.op.OperandConcept;
import org.openmrs.logic.op.OperandDate;
import org.openmrs.logic.op.OperandNumeric;
import org.openmrs.logic.op.OperandText;
import org.openmrs.logic.op.Operator;

/**
 *
 */
public class LogicExpressionToCriterion {
	
	protected static final Map<String, String> map = new HashMap<String, String>();
	
	public List<Criterion> evaluateBefore(String leftItem, Operand leftOperand, Operand rightOperand, Operator operator,
	                                      Date indexDate, List<Criterion> c, Criteria criteria) throws LogicException {
		if (!rightOperand.supports(ComparisonOperator.BEFORE))
			throw new LogicException("'before' is not a valid operator on " + leftItem + " and " + rightOperand);
		//leftItem needs to support Criterion.alias for EncounterType
		nullSafeCriterionAdd(c, Restrictions.lt(mapHelper(leftItem), rightOperand));
		return c;
	}
	
	public List<Criterion> evaluateAfter(String leftItem, Operand leftOperand, Operand rightOperand, Operator operator,
	                                     Date indexDate, List<Criterion> c, Criteria criteria) throws LogicException {
		if (!rightOperand.supports(ComparisonOperator.AFTER))
			throw new LogicException("'after' is not a valid operator on " + leftItem + " and " + rightOperand);
		nullSafeCriterionAdd(c, Restrictions.gt(mapHelper(leftItem), rightOperand));
		return c;
	}
	
	public List<Criterion> evaluateOr(String leftItem, Operand leftOperand, Operand rightOperand, Operator operator,
	                                  Date indexDate, List<Criterion> c, Criteria criteria) throws LogicException {
		Criterion leftCriteria = null;
		Criterion rightCriteria = null;
		
		if (leftOperand instanceof LogicExpression) {
			leftCriteria = this.getCriterion((LogicExpression) leftOperand, indexDate, criteria);
		}
		if (rightOperand instanceof LogicExpression) {
			rightCriteria = this.getCriterion((LogicExpression) rightOperand, indexDate, criteria);
		}
		
		if (leftCriteria != null && rightCriteria != null) {
			nullSafeCriterionAdd(c, Restrictions.gt(mapHelper(leftItem), rightOperand));
		}
		return c;
	}
	
	public List<Criterion> evaluateAnd(String leftItem, Operand leftOperand, Operand rightOperand, Operator operator,
	                                   Date indexDate, List<Criterion> c, Criteria criteria) throws LogicException {
		Criterion leftCriteria = null;
		Criterion rightCriteria = null;
		
		if (leftOperand instanceof LogicExpression) {
			leftCriteria = this.getCriterion((LogicExpression) leftOperand, indexDate, criteria);
		}
		if (rightOperand instanceof LogicExpression) {
			rightCriteria = this.getCriterion((LogicExpression) rightOperand, indexDate, criteria);
		}
		
		if (leftCriteria != null && rightCriteria != null) {
			nullSafeCriterionAdd(c, Restrictions.and(leftCriteria, rightCriteria));
		}
		return c;
	}
	
	public List<Criterion> evaluateNot(String leftItem, Operand leftOperand, Operand rightOperand, Operator operator,
	                                   Date indexDate, List<Criterion> c, Criteria criteria) throws LogicException {
		
		Criterion rightCriteria = null;
		if (rightOperand instanceof LogicExpression) {
			rightCriteria = this.getCriterion((LogicExpression) rightOperand, indexDate, criteria);
		}
		if (rightCriteria != null) {
			nullSafeCriterionAdd(c, Restrictions.not(rightCriteria));
		}
		return c;
	}
	
	public List<Criterion> evaluateExists(String leftItem, Operand leftOperand, Operand rightOperand, Operator operator,
	                                      Date indexDate, List<Criterion> c, Criteria criteria) {
		return c;
	}
	
	public List<Criterion> evaluateAsOf(String leftItem, Operand leftOperand, Operand rightOperand, Operator operator,
	                                    Date indexDate, List<Criterion> c, Criteria criteria) throws LogicException {
		if (!(rightOperand instanceof OperandDate))
			throw new LogicException("'asof' is not a valid operator on " + leftItem + " and " + rightOperand);
		
		Date newIndexDate = (Date) rightOperand;
		nullSafeCriterionAdd(c, Restrictions.le(mapHelper(leftItem), newIndexDate));
		return c;
	}
	
	public List<Criterion> evaluateWithin(String leftItem, Operand leftOperand, Operand rightOperand, Operator operator,
	                                      Date indexDate, List<Criterion> c, Criteria criteria) throws LogicException {
		if (!rightOperand.supports(ComparisonOperator.WITHIN))
			throw new LogicException("'within' is not a valid operator on " + leftItem + " and " + rightOperand);
		
		Duration duration = (Duration) rightOperand;
		Calendar within = Calendar.getInstance();
		within.setTime(indexDate);
		
		if (duration.getUnits() == Duration.Units.YEARS) {
			within.add(Calendar.YEAR, duration.getDuration().intValue());
		} else if (duration.getUnits() == Duration.Units.MONTHS) {
			within.add(Calendar.MONTH, duration.getDuration().intValue());
		} else if (duration.getUnits() == Duration.Units.WEEKS) {
			within.add(Calendar.WEEK_OF_YEAR, duration.getDuration().intValue());
		} else if (duration.getUnits() == Duration.Units.DAYS) {
			within.add(Calendar.DAY_OF_YEAR, duration.getDuration().intValue());
		} else if (duration.getUnits() == Duration.Units.MINUTES) {
			within.add(Calendar.MINUTE, duration.getDuration().intValue());
		} else if (duration.getUnits() == Duration.Units.SECONDS) {
			within.add(Calendar.SECOND, duration.getDuration().intValue());
		}
		if (indexDate.compareTo(within.getTime()) > 0) {
			nullSafeCriterionAdd(c, Restrictions.between(mapHelper(leftItem), within.getTime(), indexDate));
		} else {
			nullSafeCriterionAdd(c, Restrictions.between(mapHelper(leftItem), indexDate, within.getTime()));
		}
		return c;
	}
	
	public List<Criterion> evaluateContains(String leftItem, Operand leftOperand, Operand rightOperand, Operator operator,
	                                        Date indexDate, List<Criterion> c, Criteria criteria) throws LogicException {
		if (!rightOperand.supports(ComparisonOperator.CONTAINS))
			throw new LogicException("'contains' is not a valid operator on " + leftItem + " and " + rightOperand);
		if (rightOperand instanceof OperandNumeric) {
			Concept concept = Context.getConceptService().getConcept(((OperandNumeric) rightOperand).asInteger());
			nullSafeCriterionAdd(c, Restrictions.eq(mapHelper(leftItem), concept));
		} else if (rightOperand instanceof OperandText) {
			Concept concept = Context.getConceptService().getConceptByName(((OperandText) rightOperand).asString());
			nullSafeCriterionAdd(c, Restrictions.eq(mapHelper(leftItem), concept));
		} else if (rightOperand instanceof OperandConcept)
			nullSafeCriterionAdd(c, Restrictions.eq(mapHelper(leftItem), ((OperandConcept) rightOperand).asConcept()));
		return c;
	}
	
	public List<Criterion> evaluateEquals(String leftItem, Operand leftOperand, Operand rightOperand, Operator operator,
	                                      Date indexDate, List<Criterion> c, Criteria criteria) throws LogicException {
		if (!rightOperand.supports(ComparisonOperator.EQUALS))
			throw new LogicException("'equals' is not a valid operator on " + leftItem + " and " + rightOperand);
		
		if (rightOperand instanceof OperandNumeric) {
			nullSafeCriterionAdd(c, Restrictions.eq(mapHelper(leftItem), ((OperandNumeric) rightOperand).asDouble()));
		} else if (rightOperand instanceof OperandText)
			nullSafeCriterionAdd(c, Restrictions.eq(mapHelper(leftItem), ((OperandText) rightOperand).asString()));
		else if (rightOperand instanceof OperandDate)
			nullSafeCriterionAdd(c, Restrictions.eq(mapHelper(leftItem), rightOperand));
		else if (rightOperand instanceof OperandConcept)
			nullSafeCriterionAdd(c, Restrictions.eq(mapHelper(leftItem), ((OperandConcept) rightOperand).asConcept()));
		return c;
	}
	
	public List<Criterion> evaluateGT(String leftItem, Operand leftOperand, Operand rightOperand, Operator operator,
	                                  Date indexDate, List<Criterion> c, Criteria criteria) throws LogicException {
		if (!rightOperand.supports(ComparisonOperator.GT))
			throw new LogicException("'gt' is not a valid operator on " + leftItem + " and " + rightOperand);
		if (rightOperand instanceof OperandNumeric)
			nullSafeCriterionAdd(c, Restrictions.gt(mapHelper(leftItem), ((OperandNumeric) rightOperand).asDouble()));
		else if (rightOperand instanceof OperandDate)
			nullSafeCriterionAdd(c, Restrictions.gt(mapHelper(leftItem), rightOperand));
		return c;
	}
	
	public List<Criterion> evaluateGTE(String leftItem, Operand leftOperand, Operand rightOperand, Operator operator,
	                                   Date indexDate, List<Criterion> c, Criteria criteria) throws LogicException {
		if (!rightOperand.supports(ComparisonOperator.GTE))
			throw new LogicException("'gte' is not a valid operator on " + leftItem + " and " + rightOperand);
		if (rightOperand instanceof OperandNumeric)
			nullSafeCriterionAdd(c, Restrictions.ge(mapHelper(leftItem), ((OperandNumeric) rightOperand).asDouble()));
		else if (rightOperand instanceof OperandDate)
			nullSafeCriterionAdd(c, Restrictions.ge(mapHelper(leftItem), rightOperand));
		return c;
	}
	
	public List<Criterion> evaluateLT(String leftItem, Operand leftOperand, Operand rightOperand, Operator operator,
	                                  Date indexDate, List<Criterion> c, Criteria criteria) throws LogicException {
		if (!rightOperand.supports(ComparisonOperator.LT))
			throw new LogicException("'lt' is not a valid operator on " + leftItem + " and " + rightOperand);
		if (rightOperand instanceof OperandNumeric)
			nullSafeCriterionAdd(c, Restrictions.lt(mapHelper(leftItem), ((OperandNumeric) rightOperand).asDouble()));
		else if (rightOperand instanceof OperandDate)
			nullSafeCriterionAdd(c, Restrictions.lt(mapHelper(leftItem), rightOperand));
		return c;
	}
	
	public List<Criterion> evaluateLTE(String leftItem, Operand leftOperand, Operand rightOperand, Operator operator,
	                                   Date indexDate, List<Criterion> c, Criteria criteria) throws LogicException {
		if (!rightOperand.supports(ComparisonOperator.LTE))
			throw new LogicException("'lte' is not a valid operator on " + leftItem + " and " + rightOperand);
		if (rightOperand instanceof OperandNumeric)
			nullSafeCriterionAdd(c, Restrictions.le(mapHelper(leftItem), ((OperandNumeric) rightOperand).asDouble()));
		else if (rightOperand instanceof OperandDate)
			nullSafeCriterionAdd(c, Restrictions.le(mapHelper(leftItem), rightOperand));
		return c;
	}
	
	public Criterion getCriterion(LogicExpression logicExpression, Date indexDate, Criteria criteria) throws LogicException {
		
		Operator operator = logicExpression.getOperator();
		Operand rightOperand = logicExpression.getRightOperand();
		Operand leftOperand = null;
		if (logicExpression instanceof LogicExpressionBinary) {
			leftOperand = ((LogicExpressionBinary) logicExpression).getLeftOperand();
		}
		List<Criterion> criterion = new ArrayList<Criterion>();
		
		//the root token can be a concept name for the obs datasource
		String rootToken = logicExpression.getRootToken();
		
		if (rootToken != null) {
			if (rightOperand != null) {
				
				if (operator == Operator.BEFORE)
					this.evaluateBefore(rootToken, leftOperand, rightOperand, operator, indexDate, criterion, criteria);
				else if (operator == Operator.AFTER)
					this.evaluateAfter(rootToken, leftOperand, rightOperand, operator, indexDate, criterion, criteria);
				else if (operator == Operator.AND)
					this.evaluateAnd(null, leftOperand, rightOperand, operator, indexDate, criterion, criteria);
				else if (operator == Operator.OR)
					this.evaluateOr(null, leftOperand, rightOperand, operator, indexDate, criterion, criteria);
				else if (operator == Operator.NOT)
					this.evaluateNot(null, leftOperand, rightOperand, operator, indexDate, criterion, criteria);
				else if (operator == Operator.EXISTS)
					this.evaluateExists(null, leftOperand, rightOperand, operator, indexDate, criterion, criteria);
				else if (operator == Operator.ASOF)
					this.evaluateAsOf(rootToken, leftOperand, rightOperand, operator, indexDate, criterion, criteria);
				else if (operator == Operator.WITHIN)
					this.evaluateWithin(rootToken, leftOperand, rightOperand, operator, indexDate, criterion, criteria);
				else if (operator == Operator.GTE)
					this.evaluateGTE(rootToken, leftOperand, rightOperand, operator, indexDate, criterion, criteria);
				else if (operator == Operator.GT)
					this.evaluateGT(rootToken, leftOperand, rightOperand, operator, indexDate, criterion, criteria);
				else if (operator == Operator.LTE)
					this.evaluateLTE(rootToken, leftOperand, rightOperand, operator, indexDate, criterion, criteria);
				else if (operator == Operator.LT)
					this.evaluateLT(rootToken, leftOperand, rightOperand, operator, indexDate, criterion, criteria);
				else if (operator == Operator.CONTAINS)
					this.evaluateContains(rootToken, leftOperand, rightOperand, operator, indexDate, criterion, criteria);
				else if (operator == Operator.EQUALS)
					this.evaluateEquals(rootToken, leftOperand, rightOperand, operator, indexDate, criterion, criteria);
				
			} else
				throw new LogicException(
				        "rightOperand is null.  You must pass a valid key to the logic datasource. Here's the logic expression: "
				                + logicExpression.toString());
			
		} else
			throw new LogicException(
			        "No root token detected.  You must pass in a non-null key to the logic datasource. Here's the logic expression: "
			                + logicExpression.toString());
		
		Criterion c = null;
		
		for (Criterion crit : criterion) {
			if (c == null) {
				c = crit;
			} else {
				c = Restrictions.and(c, crit);
			}
		}
		return c;
	}
	
	public String mapHelper(String str) {
		if (map.containsKey(str))
			return map.get(str);
		else
			return str;
	}
	
	public List<Criterion> nullSafeCriterionAdd(List<Criterion> cList, Criterion crit) {
		if (cList == null)
			cList = new ArrayList<Criterion>();
		cList.add(crit);
		return cList;
	}
}
