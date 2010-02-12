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

package de.d3web.core.io.utilities;

import java.util.Comparator;

import de.d3web.core.inference.KnowledgeSlice;

/**
 * For Ordering KnowledgeSlices after their id.
 * i.e: R1,R2,R3,...
 * 
 * @author Johannes Dienst
 */
public class KnowledgeSliceComparator implements Comparator<KnowledgeSlice> {

	@Override
	public int compare(KnowledgeSlice r1, KnowledgeSlice r2) {
		return(r1.getId().compareTo(r2.getId()));
	}

}