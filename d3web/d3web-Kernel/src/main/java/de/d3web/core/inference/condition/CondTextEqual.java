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
 * Condition for text questions, where the value
 * has to be equal to a given value (String value).
 * The composite pattern is used for this. This class is a "leaf".
 * 
 * @author joba
 */
public class CondTextEqual extends CondQuestion {
	
	private static final long serialVersionUID = 5624328022212587525L;
	private String value;

	/**
	 * Creates a new condition, where the specified text question needs to
	 * be equal to the specified value.
	 * @param question the specified text question
	 * @param value the specified value (String)
	 */
	public CondTextEqual(QuestionText question, String value) {
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
			return (value.equals(this.value));
		} else {
			return false;
		}
	}

	/** 
	 * Returns the {@link String} value that has to be 
	 * equal to the answer given to the text value.
	 * @return the required String value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the {@link String} value that has to be 
	 * equal to the answer given to the text value. 
	 * @param newValue the required String value
	 */
	public void setValue(String newValue) {
		value = newValue;
	}

	@Override
	public String toString() {
		return "\u2190 CondTextEqual question: "
			+ question.getId()
			+ " value: "
			+ value;
	}
	
	@Override
	public boolean equals(Object other) {
		if (!super.equals(other)) return false;
	
		if (this.getValue() != null && ((CondTextEqual)other).getValue() != null)
			return this.getValue().equals(((CondTextEqual)other).getValue());
		else return this.getValue() == ((CondTextEqual)other).getValue();	
	}
	
	@Override
	public AbstractCondition copy() {
		return new CondTextEqual((QuestionText)getQuestion(), getValue());
	}
}