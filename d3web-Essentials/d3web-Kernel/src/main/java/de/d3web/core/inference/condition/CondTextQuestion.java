/*
 * Copyright (C) 2010 denkbares GmbH, Würzburg, Germany
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */

package de.d3web.core.inference.condition;

import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.session.Session;
import de.d3web.core.session.values.TextValue;

public abstract class CondTextQuestion extends CondQuestion {

	protected CondTextQuestion(Question idobject, String value) {
		super(idobject);
		this.value = value;
	}

	private final String value;

	@Override
	public boolean eval(Session session)
			throws NoAnswerException, UnknownAnswerException {
		TextValue textValue = (TextValue) checkAnswer(session);
		String textString = textValue.getValue().toString();
		if (textString != null) {
			return compare(textString);
		}
		else {
			return false;
		}
	}

	protected abstract boolean compare(String caseValue);

	/**
	 * Returns the {@link String} value, that has to be contained in the answer
	 * of the contained {@link QuestionText}.
	 * 
	 * @return the conditioned String value
	 */
	public String getValue() {
		return this.value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		CondTextQuestion other = (CondTextQuestion) obj;
		if (value == null) {
			if (other.value != null) {
				return false;
			}
		}
		else if (!value.equals(other.value)) {
			return false;
		}
		return true;
	}

}
