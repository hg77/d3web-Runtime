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

package de.d3web.core.terminology;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Contains e.g. the information if a symptom is abstract knowledge or 
 * if it can be answered by a dialog (or both)
 * @author Joachim Baumeister
 */
public class DerivationType {
	/**
	 * used for SIs (derived knowledge)
	 */
	public final static DerivationType DERIVED = new DerivationType("DERIVED");
	
	/**
	 * used for Questions that can be answered in a dialog
	 */
	public final static DerivationType BASIC = new DerivationType("BASIC");
	
	/**
	 * used, when a symptom is DERIVED and BASIC
	 */
	public final static DerivationType MIXED = new DerivationType("MIXED");

	private java.lang.String name;

	private DerivationType(String newName) {
		setName(newName);
	}

	/**
	 * Compares the names of the DerivationTypes
	 * @return true, if names are equal
	 * @param dt DerivationType to compare with
	 */
	public boolean equals(DerivationType dt) {
		return (dt.getName().equals(getName()));
	}

	/**
	 * @return the String value of this derivation type
	 */
	public String getName() {
		return name;
	}

	private void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the name of the DerivationType
	 */
	public String toString() {
		return getName();
	}
	
	
	/**
	 * This method is called immediately after an object of this class is deserialized.
	 * To avoid that several instances of a unique object are created, this method returns
	 * the current unique instance that is equal to the object that was deserialized.
	 * @author georg
	 */
	private Object readResolve() {
		Iterator iter = Arrays.asList(new DerivationType[] {
			DerivationType.BASIC,
			DerivationType.DERIVED,
			DerivationType.MIXED,
		}).iterator();
		while (iter.hasNext()) {
			DerivationType d = (DerivationType) iter.next();
			if (d.equals(this)) {
				return d;
			}
		}
		return this;
	}
}