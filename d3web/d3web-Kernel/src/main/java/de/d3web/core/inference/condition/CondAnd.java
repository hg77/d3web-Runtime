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
import java.util.List;

import de.d3web.core.session.XPSCase;
/**
 * Implements an "and"-condition, where all sub-conditions have to be true.
 * The composite pattern is used for this. This class is a "composite".
 * 
 * @author Michael Wolber, joba
 */
public class CondAnd extends NonTerminalCondition {

	/**
	 * 
	 */
	private static final long serialVersionUID = 130666501884397100L;

	/**
	 * Creates a new AND-condition based on the conjunction of the specified
	 * terms ({@link AbstractCondition} instances).
	 * @param terms a collection of {@link AbstractCondition} instances 
	 */
	public CondAnd(List<AbstractCondition> terms) {
		super(terms);
	}

	/**
	 * Returns true, when <b>all</b> conjunctive elements are evaluated as true
	 * based on the findings given in the specified {@link XPSCase}.
	 * @param theCase the given {@link XPSCase}
	 */
	@Override
	public boolean eval(XPSCase theCase) throws NoAnswerException, UnknownAnswerException {
		boolean wasNoAnswer = false;
		boolean wasUnknownAnswer = false;

		for (AbstractCondition condition : terms) {
			try {
				if (!condition.eval(theCase)) {
					return false;
				}
			} catch (NoAnswerException nae) {
				wasNoAnswer = true;
			} catch (UnknownAnswerException uae) {
				wasUnknownAnswer = true;
			}
		}
		
		if (wasNoAnswer) {
			throw NoAnswerException.getInstance();
		}

		if (wasUnknownAnswer) {
			throw UnknownAnswerException.getInstance();
		}
		return true;
	}

	@Override
	public String toString() {
		String ret = "\u2190 CondAnd {";
		for (AbstractCondition condition : terms) {
			if (condition != null)
				ret += condition.toString();
		}
		ret += "}";
		return ret;
	}

	/**
	 * This method is used to simplify the copy action 
	 * of a {@link NonTerminalCondition}. Do not use in the wild.
	 * 
	 */
	protected AbstractCondition createInstance(List<AbstractCondition> theTerms, AbstractCondition o) {
		return new CondAnd(theTerms);
	}
	
	
}