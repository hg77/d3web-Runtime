/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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

import java.util.Collections;

import de.d3web.core.session.Session;

/**
 * This condition creates a negation of the enclosed condition. The composite
 * pattern is used for this. This class is a "leaf".
 * 
 * @author Michael Wolber, joba
 */
public class CondNot extends NonTerminalCondition {

	/**
	 * The enclosed condition the be negated.
	 */
	private final Condition condition;

	/**
	 * Creates a new condition, where the specified condition must not be true
	 * to fulfill this condition.
	 * 
	 * @param condition the enclosed condition to be negated
	 */
	public CondNot(Condition condition) {
		super(Collections.singletonList(condition));
		this.condition = condition;
	}

	@Override
	public boolean eval(Session session)
			throws NoAnswerException, UnknownAnswerException {
		return (!condition.eval(session));
	}

	@Override
	public String toString() {
		return "NOT(" + condition + ")";
	}
}