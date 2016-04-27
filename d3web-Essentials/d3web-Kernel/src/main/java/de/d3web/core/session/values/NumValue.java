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
package de.d3web.core.session.values;

import de.d3web.core.session.QuestionValue;
import de.d3web.core.session.Value;

/**
 * Represents a numerical value (internally stored as a {@link Double}).
 * 
 * @author joba
 * 
 */
public class NumValue implements QuestionValue {

	private final Double value;

	public NumValue(double value) {
		this(Double.valueOf(value));
	}

	/**
	 * Constructs a new NumValue
	 * 
	 * @param value the Double for which a new NumValue should be instantiated
	 * @throws NullPointerException if a null object was passed in
	 */
	public NumValue(Double value) {
		if (value == null) {
			throw new NullPointerException();
		}
		this.value = value;
	}

	/**
	 * @return the {@link Double} of this num value
	 */
	@Override
	public Object getValue() {
		return value;
	}

	public Double getDouble() {
		return value;
	}

	@Override
	public int compareTo(Value o) {
		if (o instanceof NumValue) {
			return value.compareTo(((NumValue) o).value);
		}
		return -1;
	}

	@Override
	public String toString() {
		return getValue().toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + value.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		NumValue other = (NumValue) obj;
		if (!value.equals(other.value)) {
			return false;
		}
		return true;
	}

	// @Override
	// public boolean equals(Object o) {
	// if (o instanceof NumValue) {
	// NumValue nv = (NumValue) o;
	// return nv.value.equals(value);
	// }
	// return super.equals(o);
	// }

}