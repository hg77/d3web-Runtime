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

package de.d3web.empiricalTesting.caseVisualization.dot;

import de.d3web.empiricalTesting.Finding;
import de.d3web.empiricalTesting.caseVisualization.dot.DDBuilder.caseType;

public class DDEdge {
	DDNode begin;
	DDNode end;
	Finding label;
	caseType theCasetype;

	public DDEdge(DDNode begin, DDNode end, Finding label, caseType theCasetype) {
		setLabel(label);
		setBegin(begin);
		setEnd(end);
		setTheCasetype(theCasetype);
	}

	public DDEdge(DDNode begin, DDNode end, Finding label) {
		this(begin, end, label, caseType.new_case);
	}

	public DDEdge(DDNode begin, DDNode end) {
		this(begin, end, null, caseType.new_case);
	}

	public DDEdge(DDNode begin, DDNode end, caseType theCasetype) {
		this(begin, end, null, theCasetype);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((begin == null) ? 0 : begin.hashCode());
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof DDEdge))
			return false;
		DDEdge other = (DDEdge) obj;
		if (begin == null) {
			if (other.begin != null)
				return false;
		} else if (!begin.equals(other.begin))
			return false;
		if (end == null) {
			if (other.end != null)
				return false;
		} else if (!end.equals(other.end))
			return false;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		return true;
	}

	public DDNode getBegin() {
		return begin;
	}

	public DDNode getEnd() {
		return end;
	}

	public Finding getLabel() {
		return label;
	}

	public void setBegin(DDNode begin) {
		this.begin = begin;
	}

	public void setEnd(DDNode end) {
		this.end = end;
	}

	public void setLabel(Finding label) {
		this.label = label;
	}

	public caseType getTheCasetype() {
		return theCasetype;
	}

	public void setTheCasetype(caseType theCasetype) {
		this.theCasetype = theCasetype;
	}
}