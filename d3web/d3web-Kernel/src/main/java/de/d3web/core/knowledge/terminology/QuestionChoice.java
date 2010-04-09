/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package de.d3web.core.knowledge.terminology;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import de.d3web.abstraction.inference.PSMethodQuestionSetter;
import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.knowledge.terminology.info.Num2ChoiceSchema;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.core.session.Value;
import de.d3web.core.session.XPSCase;
import de.d3web.core.session.blackboard.CaseQuestionChoice;
import de.d3web.core.session.values.AnswerChoice;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.core.utilities.Tester;
import de.d3web.core.utilities.Utils;

/**
 * Storage for Questions with predefined answers (alternatives).
 * Abstract because you can choose from multiple/single choices (answers).<BR>
 * Part of the Composite design pattern (see QASet for further description)
 * 
 * @author joba, Christian Betz
 * @see QASet
 */
public abstract class QuestionChoice extends Question {
	
	protected List<AnswerChoice> alternatives;

	public QuestionChoice(String id) {
		super(id);
		this.setAlternatives(new LinkedList<AnswerChoice>());
	}

	/**
	 * Gives you all the answers (alternatives) and does not
	 * care about any rules which could possibly suppress an answer.
	 * @param theCase currentCase
	 * @return a List of all alternatives that are not suppressed by any RuleSuppress
	 **/
	public List<AnswerChoice> getAllAlternatives() {
		return alternatives;
	}

	private AnswerChoice findAlternative(List<? extends Answer> alternativesArg, final String id) {
		return (AnswerChoice) Utils.findIf(alternativesArg, new Tester() {
			public boolean test(Object testObj) {
				if ((testObj instanceof AnswerChoice)
					&& (((AnswerChoice) testObj).getId().equalsIgnoreCase(id))) {
					return true;
				} else {
					return false;
				}
			}
		});
	}

	/**
	 * if theCase == null, find the alternative in all alternatives,
	 * else find the alternative in all currently (depend on the case) available alternatives
	 * @return Answer (either instanceof AnswerChoice or AnswerUnknown)
	 **/
	public Answer getAnswer(XPSCase theCase, String id) {
		if (id == null)
			return null;
		if (theCase == null)
			return findAlternative(alternatives, id);
		else
			return findAlternative(getAlternatives(theCase), id);
	}

	public AnswerChoice findChoice(String choiceID) {
		if (choiceID == null) {
			return null;
		}
		else {
			return findAlternative(alternatives, choiceID);
		}
	}

	/**
	 * Gives you only the possible answers (alternatives) which
	 * are not suppressed by any rule.
	 *	@param theCase currentCase
	 *	@return a Vector of all alternatives that are not suppressed by any RuleSuppress
	 **/
	public List<Answer> getAlternatives(XPSCase theCase) {
		CaseQuestionChoice caseQ = (CaseQuestionChoice) theCase.getCaseObject(this);
		List<Answer> suppVec = caseQ.getMergedSuppressAlternatives();
		List<Answer> result = new LinkedList<Answer>();
        Iterator<AnswerChoice> e = alternatives.iterator();
		while (e.hasNext()) {
			Answer elem = e.next();
			if (!suppVec.contains(elem))
				result.add(elem);
		}
		return result;
	}

	@Override
	public abstract Value getValue(XPSCase theCase);


	/**
	 * sets the answer alternatives from which a user or rule can
	 * choose one or more to answer this question.
	 */
	public void setAlternatives(List<AnswerChoice> alternatives) {
		if (alternatives != null) {
			this.alternatives = alternatives;
			Iterator<AnswerChoice> iter = this.alternatives.iterator();
			while (iter.hasNext()) {
				iter.next().setQuestion(this);
			}
		} else
			setAlternatives(new LinkedList<AnswerChoice>());

	}
    
    public void addAlternative(AnswerChoice answer) {
        if ((answer != null) && (!getAllAlternatives().contains(answer))) {
            alternatives.add(answer);
            answer.setQuestion(this);
        }
    }

	@Override
	public String toString() {
		String res = super.toString();
		return res;
	}

	public String verbalizeWithoutValue(XPSCase theCase) {
		String res = "\n " + super.toString();
		Iterator<Answer> iter = getAlternatives(theCase).iterator();
		while (iter.hasNext())
			res += "\n  " + iter.next().toString();
		return res;
	}

	@Override
	public String verbalizeWithValue(XPSCase theCase) {
		return verbalizeWithoutValue(theCase)
			+ "\n Wert -> "
			+ getValue(theCase);
	}

	/**
	 * @return the current numerical value of the question
	 * according to a give XPSCase. This value is used to
	 * be processed by a Num2ChoiceSchema.
	 */
	public Double getNumericalSchemaValue(XPSCase theCase) {
		return ((CaseQuestionChoice) theCase.getCaseObject(this)).getNumericalSchemaValue();
	}


	private void setNumericalSchemaValue(XPSCase theCase, Double newValue) {
		((CaseQuestionChoice) theCase.getCaseObject(this)).setNumericalSchemaValue(newValue);
	}
	
	/**
	 * @return the Num2ChoiceSchema that has been set to this question, null, if no such schema exists.
	 */
	public Num2ChoiceSchema getSchemaForQuestion() {
		KnowledgeSlice schemaCol =
			getKnowledge(PSMethodQuestionSetter.class, PSMethodQuestionSetter.NUM2CHOICE_SCHEMA);
		if (schemaCol != null) {
			return (Num2ChoiceSchema) schemaCol;
		} else {
			return null;
		}
	}
	
	protected Value convertNumericalValue(XPSCase theCase, double doubleValue) {
		Num2ChoiceSchema schema = getSchemaForQuestion();
		if (schema != null) {
			Double numValue = null;
			if (Boolean.TRUE.equals(getProperties().getProperty(Property.TIME_VALUED))) {
				numValue = new Double(doubleValue);
			} else {
				numValue = new Double(getNumericalSchemaValue(theCase).doubleValue()
						+ doubleValue);
			}
			setNumericalSchemaValue(theCase, numValue);
			return schema.getValueForNum(numValue, getAllAlternatives(), theCase);
		} else {
			Logger.getLogger(this.getClass().getName()).throwing(
				this.getClass().getName(),
				"convertNumericalValue",
					new RuntimeException("No Num2ChoiceSchema defined for " + getId() + ":"
							+ getName()));
			return UndefinedValue.getInstance();
		}
	}
	
	public Value getValue(XPSCase theCase, Double value) {
		if (value == null) {
			return UndefinedValue.getInstance();
		}
		else {
			Num2ChoiceSchema schema = getSchemaForQuestion();
			if (schema != null) {
				return schema.getValueForNum(value, getAllAlternatives(), theCase);
			}
			else {
				return UndefinedValue.getInstance();
			}
		}
	}
}