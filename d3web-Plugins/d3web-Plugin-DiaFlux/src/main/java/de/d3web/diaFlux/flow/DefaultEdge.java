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

package de.d3web.diaFlux.flow;

import de.d3web.core.inference.condition.Condition;

/**
 * @author Reinhard Hatko
 * 
 */
public class DefaultEdge implements Edge {

	private final Node startNode;
	private final Node endNode;
	private final Condition condition;
	private final String id;

	public DefaultEdge(String id, Node startNode, Node endNode, Condition condition) {

		if (startNode == null) {
			throw new IllegalArgumentException("startNode must not be null");
		}
		if (endNode == null) {
			throw new IllegalArgumentException("endNode must not be null");
		}
		if (startNode.getFlow() != endNode.getFlow()) {
			throw new IllegalArgumentException("Both nodes must be in the same flow.");

		}
		if (condition == null) {
			throw new IllegalArgumentException("condition must not be null");
		}

		this.startNode = startNode;
		this.endNode = endNode;
		this.condition = condition;
		this.id = id;

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((endNode == null) ? 0 : endNode.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((startNode == null) ? 0 : startNode.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		DefaultEdge other = (DefaultEdge) obj;
		if (endNode == null) {
			if (other.endNode != null) return false;
		}
		else if (!endNode.equals(other.endNode)) return false;
		if (id == null) {
			if (other.id != null) return false;
		}
		else if (!id.equals(other.id)) return false;
		if (startNode == null) {
			if (other.startNode != null) return false;
		}
		else if (!startNode.equals(other.startNode)) return false;
		return true;
	}

	@Override
	public Condition getCondition() {
		return condition;
	}

	@Override
	public Node getEndNode() {
		return endNode;
	}

	@Override
	public Node getStartNode() {
		return startNode;
	}

	@Override
	public Flow getFlow() {
		return startNode.getFlow();
	}

	@Override
	public String toString() {
		return "Edge [" + getStartNode() + " -> " + getEndNode() + "]@"
				+ Integer.toHexString(hashCode());
	}

	@Override
	public String getID() {
		return id;
	}

}
