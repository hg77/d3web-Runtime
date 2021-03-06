/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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

import java.util.Collection;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.session.Session;

/**
 * A terminal condition that always returns true. In addition a set of terminals
 * can be specified to which objects the condition "is connected to" (e.g. on
 * what changes a rule using that condition should be evaluated.
 * <p>
 * This class is not intended to be used directly by users. In contrast it is a
 * utility condition to handle specific problem solver issues.
 * 
 * @author Reinhard Hatko
 * @created on: 09.10.2009
 */
public final class ConditionTrue extends TerminalCondition {

	public static final Condition INSTANCE = new ConditionTrue();

	public ConditionTrue(TerminologyObject... connectedObjects) {
		super(connectedObjects);
	}

	public ConditionTrue(Collection<? extends TerminologyObject> connectedObjects) {
		super(connectedObjects);
	}

	@Override
	public boolean eval(Session theCase) throws NoAnswerException, UnknownAnswerException {
		return true;
	}
}