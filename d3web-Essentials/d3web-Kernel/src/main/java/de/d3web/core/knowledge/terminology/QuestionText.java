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

package de.d3web.core.knowledge.terminology;

import de.d3web.core.knowledge.KnowledgeBase;

/**
 * A question which asks for a string text.
 * 
 * @author Joachim Baumeister
 */
public class QuestionText extends Question {

	private int height;
	private int width;

	/**
	 * Creates a new QuestionText and adds it to the knowledgebase, so no manual
	 * adding of the created object to the kb is needed
	 * 
	 * @param kb {@link KnowledgeBase} in which the QuestionText should be
	 *        inserted
	 * @param name the name of the new QuestionText
	 */
	public QuestionText(KnowledgeBase kb, String name) {
		super(kb, name);
	}

	/**
	 * Creates a new QuestionText, adds it to the knowledgebase and to the
	 * parent. No manual adding of the created object to the kb is needed
	 * 
	 * @param parent the parent {@link QASet}
	 * @param name the name of the new QuestionText
	 */
	public QuestionText(QASet parent, String name) {
		this(parent.getKnowledgeBase(), name);
		parent.addChild(this);
	}

	/**
	 * @return the height of the displayed text field asking the text question.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @return the width of the displayed text field asking the text question.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Sets the height of the displayed text field asking the text question. The
	 * specified value must be greater 0.
	 * 
	 * @param height int the specified height
	 */
	public void setHeight(int height) {
		if (height > 0) {
			this.height = height;
		}
	}

	/**
	 * Sets the width of the displayed text field asking the text question. The
	 * specified value must be greater 0.
	 * 
	 * @param width int the specified width
	 */
	public void setWidth(int width) {
		if (width > 0) {
			this.width = width;
		}
	}
}
