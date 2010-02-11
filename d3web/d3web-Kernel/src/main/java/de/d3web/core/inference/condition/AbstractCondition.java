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

import de.d3web.core.inference.Rule;
import de.d3web.core.session.XPSCase;
import de.d3web.core.session.values.AnswerUnknown;
import de.d3web.core.terminology.Diagnosis;
import de.d3web.core.terminology.IDObject;
import de.d3web.core.terminology.Question;
import de.d3web.xcl.XCLRelation;

/**
 * Abstract superclass to represent conditions, to be used
 * for example in rules ({@link Rule}) and set-covering relations
 * ({@link XCLRelation}). 
 * Every condition holds a collection of objects, that are constrained
 * in this condition, and an eval method to evaluate this condition
 * with respect to a given {@link XPSCase}. 
 * @author Joachim Baumeister, Christian Betz
 */
public abstract class AbstractCondition implements java.io.Serializable {

	private static final long serialVersionUID = -1427421083780321072L;

	/**
	 * Evaluates this condition with respect to the findings
	 * given in the specified {@link XPSCase}. {@link NoAnswerException} and 
	 * {@link UnknownAnswerException} exceptions are thrown in the case, when 
	 * the condition cannot be strictly evaluated: If this condition is contained 
	 * in a {@link CondNot} condition, then returning true/false is not
	 * appropriate.
	 * 
	 * @param theCase the given {@link XPSCase}
	 * @return true/false for positive/negative evaluation; 
	 *         an appropriate {@link Exception} otherwise
	 * @throws NoAnswerException when a required sub-condition of this 
	 *         condition has a question with no answer currently set
	 * @throws UnknownAnswerException when a required sub-conditions contains a 
	 *         question having an {@link AnswerUnknown} assigned
	 */
	public abstract boolean eval(XPSCase theCase)
		throws NoAnswerException, UnknownAnswerException;

	/**
	 * Returns the collection of {@link Question} and {@link Diagnosis} 
	 * instances, that are constrained in this condition.
	 * @return all used questions and diagnoses used in this condition
	 */
	public abstract List<? extends IDObject> getTerminalObjects();

	/**
	 * Returns a simple {@link String} verbalization of this condition. 
	 * @return a simple {@link String} verbalization of this condition
	 */
	public String toString() {
		return "[" + getTerminalObjects() + "]";
	}
	
	/**
	 * Compares this condition with another condition.
	 * @return true when a condition with the same content is given
	 * @param obj another condition to compare
	 */
	public abstract boolean equals(Object obj);

	/**
	 * Returns the hash code of this condition.
	 * @param the hash code of this condition
	 */
	public abstract int hashCode();
	
	/**
	 * Create a deep copy of this condition instance.
	 * (see Prototype pattern)
	 * @return a deep copy of this condition
	 */
	public abstract AbstractCondition copy();

}