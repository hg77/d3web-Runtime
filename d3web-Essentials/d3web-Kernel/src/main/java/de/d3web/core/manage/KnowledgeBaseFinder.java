/*
 * Copyright (C) 2013 University Wuerzburg, Computer Science VI
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
package de.d3web.core.manage;

import java.util.Collection;
import java.util.Collections;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.NamedObject;


/**
 * A finder for the KnowledgeBase itself.
 * 
 * @author Reinhard Hatko
 * @created 16.05.2013
 */
public class KnowledgeBaseFinder implements NamedObjectFinder {

	public static final String KNOWLEDGEBASE_ID = "KNOWLEDGEBASE";

	@Override
	public Collection<NamedObject> find(String name, KnowledgeBase kb) {
		if (name.equals(KNOWLEDGEBASE_ID) || name.equals(kb.getName())) {
			return Collections.singletonList(kb);
		}
		else {
			return Collections.emptyList();
		}
	}

}
