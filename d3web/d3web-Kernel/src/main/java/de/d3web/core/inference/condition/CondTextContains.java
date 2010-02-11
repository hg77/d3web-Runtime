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

package de.d3web.core.inference.condition;

import de.d3web.core.session.XPSCase;
import de.d3web.core.session.values.AnswerText;
import de.d3web.core.terminology.QuestionText;

/**
 * Condition for text questions, where a specified value
 * has to be contained in the answer of a QuestionText.
 * The composite pattern is used for this. This class is a "leaf".
 * 
 * @author joba
 */
public class CondTextContains extends CondQuestion {
	
	private static final long serialVersionUID = -3269870203428058851L;
	private String value;

	/**
	 * Creates a new condition, where a specified {@link String} value 
	 * needs to be contained in the specified {@link QuestionText}.
	 * @param question the specified text question
	 * @param value the specified value (String)
	 */
	public CondTextContains(QuestionText question, String value) {
		super(question);
		this.value = value;
	}

	@Override
	public boolean eval(XPSCase theCase)
		throws NoAnswerException, UnknownAnswerException {
		checkAnswer(theCase);
		AnswerText answer = (AnswerText) question.getValue(theCase).get(0);
		String value = (String) answer.getValue(theCase);
		if (value != null) {
			return (value.indexOf(this.value) > -1);
		} else {
			return false;
		}
	}

	/**
	 * Returns the {@link String} value, that has to be contained in the answer
	 * of the contained {@link QuestionText}.
	 * @return the specified String value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the {@link String} value, that has to be contained in the answer
	 * of the contained {@link QuestionText}.
	 * @param newValue specified String value
	 */
	public void setValue(String newValue) {
		value = newValue;
	}

	@Override
	public String toString() {
		return "\u2190 CondTextContains question: "
			+ question.getId()
			+ " value: "
			+ value;
	}

	@Override
	public boolean equals(Object other) {
		if (!super.equals(other)) return false;
		
		if (this.getValue() != null && ((CondTextContains)other).getValue() != null)
					return this.getValue().equals(((CondTextContains)other).getValue());
				else return this.getValue() == ((CondTextContains)other).getValue();	
	}
	
	@Override
	public AbstractCondition copy() {
		return new CondTextContains((QuestionText)getQuestion(), getValue());
	}
	

}