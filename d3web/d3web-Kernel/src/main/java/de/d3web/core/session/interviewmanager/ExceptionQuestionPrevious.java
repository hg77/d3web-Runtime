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

package de.d3web.core.session.interviewmanager;

import java.util.List;

import de.d3web.core.KnowledgeBase;

/**
 * This is a marker class that will be returned by moveToPrevious method of DialogController
 * if the history limit has been reached
 * @author Norman Brümmer
 */
public class ExceptionQuestionPrevious extends ExceptionQuestion {

	public ExceptionQuestionPrevious() {
		super();
	}

	public ExceptionQuestionPrevious(
		KnowledgeBase kb,
		String id,
		String text) {
		super(kb, id, text);
	}

	public ExceptionQuestionPrevious(
		KnowledgeBase kb,
		String id,
		String text,
		List children) {
		super(kb, id, text, children);
	}
	
	/**
	 * statically equals test (compares the classes!)
	 */
	public boolean equals(Object other) {
		return other.getClass() == this.getClass();
	}	
}