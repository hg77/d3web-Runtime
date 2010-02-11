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

package de.d3web.indication;

import java.util.ArrayList;

import de.d3web.core.inference.RuleAction;
import de.d3web.core.terminology.QASet;

/**
 * ActionIndication represents the general indication of a QASet.
 * Creation date: (21.02.2002 13:14:06)
 * @author Christian Betz
 */
public class ActionIndication extends ActionNextQASet {

	private static final long serialVersionUID = 272476961557836941L;

	/**
	 * Creates a new indication action for the given corresponding rule
	 */
	public ActionIndication() {
		super();
	}
	
	public RuleAction copy() {
		ActionIndication a = new ActionIndication();
		a.setRule(getCorrespondingRule());
		a.setQASets(new ArrayList<QASet>(getQASets()));
		return a;
	}
	
	public int hashCode() {
		if(getQASets() != null)
			return (getQASets().hashCode());
		return 0;
	}
	
	public boolean equals(Object o) {
		if (o==this) 
			return true;
		if (o instanceof ActionIndication) {
			ActionIndication a = (ActionIndication)o;
			return isSame(a.getQASets(), getQASets());
		}
		else
			return false;
	}
	
	protected boolean isSame(Object obj1, Object obj2) {
		if(obj1 == null && obj2 == null) return true;
		if(obj1 != null && obj2 != null) return obj1.equals(obj2);
		return false;
	}
	
}