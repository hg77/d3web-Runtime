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

package de.d3web.core.manage;

import java.util.Date;

import de.d3web.core.session.values.AnswerChoice;
import de.d3web.core.session.values.AnswerDate;
import de.d3web.core.session.values.AnswerNo;
import de.d3web.core.session.values.AnswerNum;
import de.d3web.core.session.values.AnswerText;
import de.d3web.core.session.values.AnswerYes;

/**
 * This is a factory class for Answer objects
 * It contains several static methods which create the most popular answer types
 * Creation date: (26.06.2001 12:41:20)
 * @author Joachim Baumeister
 */
public class AnswerFactory {

	public static AnswerChoice createAnswerChoice(
		String theId,
		String theValue) {
		AnswerChoice theAnswer = new AnswerChoice(theId);
		theAnswer.setText(theValue);
		return theAnswer;
	}

	public static AnswerNo createAnswerNo(String theId, String theValue) {
		AnswerNo theAnswer = new AnswerNo(theId);
		theAnswer.setText(theValue);
		return theAnswer;
	}

	public static AnswerYes createAnswerYes(String theId, String theValue) {
		AnswerYes theAnswer = new AnswerYes(theId);
		theAnswer.setText(theValue);
		return theAnswer;
	}
	public static AnswerNum createAnswerNum(double value) {
		AnswerNum theAnswer = new AnswerNum();
		theAnswer.setValue(value);
		return theAnswer;
	}
	public static AnswerText createAnswerText(String text) {
		AnswerText answer = new AnswerText();
		answer.setText(text);
		return answer;
	}
	public static AnswerDate createAnswerDate(Date date) {
		AnswerDate theAnswer = new AnswerDate();
		theAnswer.setValue(date);
		return theAnswer;
	}
}