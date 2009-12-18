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
package org.openmrs.logic.db.hibernate;

import static org.openmrs.logic.datasource.EncounterDataSource.ENCOUNTER_KEY;
import static org.openmrs.logic.datasource.EncounterDataSource.LOCATION_KEY;
import static org.openmrs.logic.datasource.EncounterDataSource.PROVIDER_KEY;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Cohort;
import org.openmrs.Encounter;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.Duration;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.LogicExpression;
import org.openmrs.logic.LogicExpressionBinary;
import org.openmrs.logic.LogicTransform;
import org.openmrs.logic.db.LogicEncounterDAO;
import org.openmrs.logic.op.OperandCollection;
import org.openmrs.logic.op.OperandDate;
import org.openmrs.logic.op.OperandText;
import org.openmrs.logic.op.Operator;
import org.openmrs.logic.util.LogicExpressionToCriterion;

/**
 * This class builds the hibernate statements needed to execute logic evaluations for the
 * EncounterDatasource
 */
public class HibernateLogicEncounterDAO extends LogicExpressionToCriterion implements LogicEncounterDAO {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	/**
	 * Hibernate session factory
	 */
	private SessionFactory sessionFactory;
	
	/**
	 * Set session factory
	 * 
	 * @param sessionFactory
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	/**
	 * Convenience method to get the list of hibernate queries for this expression
	 * 
	 * @param logicExpression
	 * @param indexDate
	 * @param criteria Criteria object so that certain expressions can add aliases, etc
	 * @return Criterion to be added to the Criteria
	 */
	public Criterion getCriterion(LogicExpression logicExpression, Date indexDate, Criteria criteria) {
		Operator operator = logicExpression.getOperator();
		Object rightOperand = logicExpression.getRightOperand();
		Object leftOperand = null;
		if (logicExpression instanceof LogicExpressionBinary) {
			leftOperand = ((LogicExpressionBinary) logicExpression).getLeftOperand();
		}
		List<Criterion> criterion = new ArrayList<Criterion>();
		
		//if the leftOperand is a String and does not match any components,
		//see if it is a concept name and restrict accordingly
		//a null operator implies a concept restriction
		if (leftOperand instanceof LogicExpression) {
			// no restrictions if there is no operator
			// TODO restrict on provider != null for encounterProvider token?
		}
		
		String token = logicExpression.getRootToken();
		
		if (operator == null) {
			// no restrictions if there is no operator
			// TODO restrict on provider != null for encounterProvider token?
		} else if (operator == Operator.BEFORE || operator == Operator.LT) {
			if (ENCOUNTER_KEY.equalsIgnoreCase(token) && rightOperand instanceof OperandDate) {
				criterion.add(Restrictions.lt("encounterDatetime", rightOperand));
			} else {
				throw new LogicException("'before' is not a valid operator on " + token + " and " + rightOperand);
			}
		} else if (operator == Operator.AFTER || operator == Operator.GT) {
			if (ENCOUNTER_KEY.equalsIgnoreCase(token) && rightOperand instanceof OperandDate) {
				criterion.add(Restrictions.gt("encounterDatetime", rightOperand));
			} else {
				throw new LogicException("'after' is not a valid operator on " + token + " and " + rightOperand);
			}
		} else if (operator == Operator.AND || operator == Operator.OR) {
			if (ENCOUNTER_KEY.equalsIgnoreCase(token) && rightOperand instanceof OperandDate) {
				Criterion leftCriteria = null;
				Criterion rightCriteria = null;
				if (leftOperand instanceof LogicExpression) {
					leftCriteria = this.getCriterion((LogicExpression) leftOperand, indexDate, criteria);
				}
				if (rightOperand instanceof LogicExpression) {
					rightCriteria = this.getCriterion((LogicExpression) rightOperand, indexDate, criteria);
				}
				
				if (leftCriteria != null && rightCriteria != null) {
					if (operator == Operator.AND) {
						criterion.add(Restrictions.and(leftCriteria, rightCriteria));
					}
					if (operator == Operator.OR) {
						criterion.add(Restrictions.or(leftCriteria, rightCriteria));
					}
				}
			} else {
				throw new LogicException("'and/or' are not valid operators on " + token + " and " + rightOperand);
			}
		} else if (operator == Operator.NOT) {
			
			Criterion rightCriteria = null;
			
			if (rightOperand instanceof LogicExpression) {
				rightCriteria = this.getCriterion((LogicExpression) rightOperand, indexDate, criteria);
			}
			
			if (rightCriteria != null) {
				criterion.add(Restrictions.not(rightCriteria));
			}
			
		} else if (operator == Operator.CONTAINS) {
			if (ENCOUNTER_KEY.equalsIgnoreCase(token) && rightOperand instanceof OperandText) {
				criteria.createAlias("encounterType", "encounterType");
				criterion.add(Expression.eq("encounterType.name", ((OperandText) rightOperand).asString()));
			} else if (LOCATION_KEY.equalsIgnoreCase(token) && rightOperand instanceof OperandText) {
				criteria.createAlias("location", "location");
				criterion.add(Restrictions.eq("location.name", ((OperandText) rightOperand).asString()));
			} else if (PROVIDER_KEY.equalsIgnoreCase(token) && rightOperand instanceof OperandText) {
				criteria.createAlias("provider", "provider");
				criterion.add(Restrictions.eq("provider.systemId", ((OperandText) rightOperand).asString()));
			} else {
				throw new LogicException("'contains' is not a valid operator on " + token + " and " + rightOperand);
			}
		}  else if (operator == Operator.IN) {
			if (ENCOUNTER_KEY.equalsIgnoreCase(token) && rightOperand instanceof OperandCollection) {
				criteria.createAlias("encounterType", "encounterType");
				criterion.add(Expression.in("encounterType.name", ((OperandCollection) rightOperand).asCollection()));
			} else if (LOCATION_KEY.equalsIgnoreCase(token) && rightOperand instanceof OperandCollection) {
				criteria.createAlias("location", "location");
				criterion.add(Restrictions.in("location.name", ((OperandCollection) rightOperand).asCollection()));
			} else if (PROVIDER_KEY.equalsIgnoreCase(token) && rightOperand instanceof OperandCollection) {
				criteria.createAlias("provider", "provider");
				criterion.add(Restrictions.in("provider.systemId", ((OperandCollection) rightOperand).asCollection()));
			} else {
				throw new LogicException("'in' is not a valid operator on " + token + " and " + rightOperand);
			}
		} else if (operator == Operator.EQUALS) {
			if (ENCOUNTER_KEY.equalsIgnoreCase(token) && rightOperand instanceof OperandDate) {
				criterion.add(Restrictions.eq("encounterDatetime", rightOperand));
			} else if (ENCOUNTER_KEY.equalsIgnoreCase(token) && rightOperand instanceof OperandText) {
				criteria.createAlias("encounterType", "encounterType");
				criterion.add(Restrictions.eq("encounterType.name", ((OperandText) rightOperand).asString()));
			} else if (LOCATION_KEY.equalsIgnoreCase(token) && rightOperand instanceof OperandText) {
				criteria.createAlias("location", "location");
				criterion.add(Restrictions.eq("location.name", ((OperandText) rightOperand).asString()));
			} else if (PROVIDER_KEY.equalsIgnoreCase(token) && rightOperand instanceof OperandText) {
				criteria.createAlias("provider", "provider");
				criterion.add(Restrictions.eq("provider.systemId", ((OperandText) rightOperand).asString()));
			} else {
				throw new LogicException("'equals' is not a valid operator on " + token + " and " + rightOperand);
			}
		} else if (operator == Operator.LTE) {
			if (rightOperand instanceof OperandDate)
				criterion.add(Restrictions.le("encounterDatetime", rightOperand));
			else
				throw new LogicException("'less than or equals' is not a valid operator on " + token + " and "
				        + rightOperand);
		} else if (operator == Operator.GTE) {
			if (rightOperand instanceof OperandDate)
				criterion.add(Restrictions.ge("encounterDatetime", rightOperand));
			else
				throw new LogicException("'greater than or equals' is not a valid operator on " + token + " and "
				        + rightOperand);
		} else if (operator == Operator.LT) {
			if (rightOperand instanceof OperandDate)
				criterion.add(Restrictions.lt("encounterDatetime", rightOperand));
			else
				throw new LogicException("'less than' is not a valid operator on " + token + " and " + rightOperand);
			
		} else if (operator == Operator.GT) {
			if (rightOperand instanceof OperandDate)
				criterion.add(Restrictions.gt("encounterDatetime", rightOperand));
			else
				throw new LogicException("'greater than' is not a valid operator on " + token + " and " + rightOperand);
			
		} else if (operator == Operator.EXISTS) {
			// EXISTS can be handled on the higher level (above
			// LogicService, even) by coercing the Result into a Boolean for
			// each patient
		} else if (operator == Operator.ASOF && rightOperand instanceof Date) {
			indexDate = (Date) rightOperand;
			criterion.add(Restrictions.le("encounterDatetime", indexDate));
			
		} else if (operator == Operator.WITHIN) {
			if (rightOperand instanceof Duration) {
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
				} else if (duration.getUnits() == Duration.Units.HOURS) {
					within.add(Calendar.HOUR_OF_DAY, duration.getDuration().intValue());
				} else if (duration.getUnits() == Duration.Units.MINUTES) {
					within.add(Calendar.MINUTE, duration.getDuration().intValue());
				} else if (duration.getUnits() == Duration.Units.SECONDS) {
					within.add(Calendar.SECOND, duration.getDuration().intValue());
				}
				
				if (indexDate.compareTo(within.getTime()) > 0) {
					criterion.add(Restrictions.between("encounterDatetime", within.getTime(), indexDate));
				} else {
					criterion.add(Restrictions.between("encounterDatetime", indexDate, within.getTime()));
				}
			} else {
				throw new LogicException("'within' is not a valid operator on " + token + " and " + rightOperand);
			}
		}
		
		Criterion c = null;
		
		for (Criterion crit : criterion) {
			if (c == null) {
				c = crit;
			} else {
				c = Restrictions.and(c, crit);
			}
		}
		System.out.println(c);
		return c;
	}
	
	// Helper function, converts logic service's criteria into Hibernate's
	// criteria
	@SuppressWarnings("unchecked")
	private List<Encounter> logicToHibernate(LogicExpression expression, Cohort who, LogicContext logicContext)
	                                                                                                           throws LogicException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Encounter.class);
		
		Date indexDate = logicContext.getIndexDate();
		Operator transformOperator = null;
		LogicTransform transform = expression.getTransform();
		Integer numResults = null;
		
		if (transform != null) {
			transformOperator = transform.getTransformOperator();
			numResults = transform.getNumResults();
		}
		
		if (numResults == null) {
			numResults = 1;
		}
		
		// set the transform and evaluate the right criteria
		// if there is any
		if (transformOperator == Operator.LAST) {
			criteria.addOrder(Order.desc("encounterDatetime")).addOrder(Order.desc("dateCreated")).addOrder(
			    Order.desc("encounterId"));
		} else if (transformOperator == Operator.FIRST) {
			criteria.addOrder(Order.asc("encounterDatetime")).addOrder(Order.asc("encounterId"));
		} else if (transformOperator == Operator.DISTINCT) {
			criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		} else {
			criteria.addOrder(Order.desc("encounterDatetime"));
		}
		
		Criterion c = this.getCriterion(expression, indexDate, criteria);
		if (c != null) {
			criteria.add(c);
		}
		
		List<Encounter> results = new ArrayList<Encounter>();
		
		criteria.add(Restrictions.eq("voided", false));
		criteria.add(Restrictions.in("patient.personId", who.getMemberIds()));
		results.addAll(criteria.list());
		
		//return a single result per patient for these operators
		//I don't see an easy way to do this in hibernate so I am
		//doing some postprocessing
		if (transformOperator == Operator.FIRST || transformOperator == Operator.LAST) {
			HashMap<Integer, ArrayList<Encounter>> nResultMap = new HashMap<Integer, ArrayList<Encounter>>();
			
			for (Encounter currResult : results) {
				Integer currPersonId = currResult.getPatient().getPersonId();
				ArrayList<Encounter> prevResults = nResultMap.get(currPersonId);
				if (prevResults == null) {
					prevResults = new ArrayList<Encounter>();
					nResultMap.put(currPersonId, prevResults);
				}
				
				if (prevResults.size() < numResults) {
					prevResults.add(currResult);
				}
			}
			
			if (nResultMap.values().size() > 0) {
				results.clear();
				
				for (ArrayList<Encounter> currPatientEncounter : nResultMap.values()) {
					results.addAll(currPatientEncounter);
				}
			}
		}
		return results;
	}
	
	/**
	 * @see org.openmrs.api.db.EncounterDAO#getEncounters(org.openmrs.Patient, org.openmrs.Location,
	 *      Date, Date, java.util.Collection, java.util.Collection, java.util.Collection, boolean)
	 */
	public List<Encounter> getEncounters(Cohort who, LogicCriteria logicCriteria, LogicContext logicContext)
	                                                                                                        throws LogicException {
		return logicToHibernate(logicCriteria.getExpression(), who, logicContext);
	}
	
}
