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

package de.d3web.kernel.domainModel.ruleCondition;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.answers.AnswerChoice;
import de.d3web.kernel.domainModel.qasets.QuestionYN;

/**
 * This condition checks, if a YES/NO question has the NO value.
 * The composite pattern is used for this. This class is a "leaf".
 * 
 * @author joba
 */
@Deprecated
public class CondChoiceNo extends CondEqual {

	/**
	 * Creates a new equal-condition. 
	 * @param quest the question to check
	 */
	public CondChoiceNo(QuestionYN question) {
		super(question, question.no);
	}

	/**
	 * Checks if the question has the value(s) specified in the constructor.
	 */
	public boolean eval(XPSCase theCase)
		throws NoAnswerException, UnknownAnswerException {
		checkAnswer(theCase);
		return ((AnswerChoice) question.getValue(theCase).get(0)).isAnswerNo();
	}

	/**
	 * Verbalizes the condition.
	 */
	public String toString() {
		return "<condition operator=\"choiceNo\" id=\""
			+ question.getId()
			+ "\"/>\n";
	}


	public AbstractCondition copy() {
		return new CondChoiceNo((QuestionYN)getQuestion());
	}
	
	
}
