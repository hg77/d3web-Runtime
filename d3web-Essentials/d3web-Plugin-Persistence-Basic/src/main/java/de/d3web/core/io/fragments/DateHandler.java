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
package de.d3web.core.io.fragments;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

import de.d3web.core.io.Persistence;
import de.d3web.core.knowledge.KnowledgeBase;
import com.denkbares.strings.Strings;

/**
 * @author Markus Friedrich (denkbares GmbH)
 * @created 21.01.2011
 */
public class DateHandler implements FragmentHandler<KnowledgeBase> {

	private final static String ELEMENT_NAME = "Date";

	@Override
	public Object read(Element element, Persistence<KnowledgeBase> persistence) throws IOException {
		String textContent;
		try {
			textContent = element.getTextContent();
		}
		catch (DOMException e) {
			throw new IOException(e);
		}
		try {
			return Strings.readDate(textContent);
		}
		catch (ParseException e) {
			throw new IOException(e);
		}
	}

	@Override
	public Element write(Object object, Persistence<KnowledgeBase> persistence) throws IOException {
		Element element = persistence.getDocument().createElement(ELEMENT_NAME);
		element.setTextContent(Strings.writeDate((Date) object));
		return element;
	}

	@Override
	public boolean canRead(Element element) {
		return element.getNodeName().equals(ELEMENT_NAME);
	}

	@Override
	public boolean canWrite(Object object) {
		return object instanceof Date;
	}

}
