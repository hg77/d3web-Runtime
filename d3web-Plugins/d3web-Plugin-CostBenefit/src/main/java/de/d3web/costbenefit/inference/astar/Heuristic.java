/*
 * Copyright (C) 2011 denkbares GmbH
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
package de.d3web.costbenefit.inference.astar;

import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.costbenefit.model.Path;
import de.d3web.costbenefit.model.SearchModel;

/**
 * @author Markus Friedrich (denkbares GmbH)
 * @created 22.06.2011
 */
public interface Heuristic {

	/**
	 * Returns the estimated costs to reach the target {@link QContainer} under
	 * the specified state. The costs must be optimistic; thus under any
	 * circumstances the returned distance must lower or equal to the best path
	 * costs to reach that target.
	 *
	 * @param model  the actual SearchModel
	 * @param path   Path from the start of the search to the state
	 * @param state  the state to start from
	 * @param target the target to be reached
	 * @return optimistic estimation of the minimal distance
	 * @created 06.09.2011
	 */
	double getDistance(SearchModel model, Path path, State state, QContainer target);

	/**
	 * Initializes the heuristic to be used for a specific {@link SearchModel}.
	 * The heuristic is guaranteed to be used only for that search at the same
	 * time, until #init(SearchModel) is called for an other search
	 * model.
	 *
	 * @param searchModel the SearchModel the heuristic should be used for
	 * @created 06.09.2011
	 */
	void init(SearchModel searchModel);
}
