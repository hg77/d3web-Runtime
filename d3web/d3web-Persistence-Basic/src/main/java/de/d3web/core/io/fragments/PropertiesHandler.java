/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg denkbares GmbH
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
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Element;

import de.d3web.core.io.PersistenceManager;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.DefaultInfoStore;
import de.d3web.core.knowledge.InfoStore;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.info.Property;

/**
 * Handler for properties
 * 
 * @author hoernlein, Markus Friedrich (denkbares GmbH)
 */
public class PropertiesHandler {

	public boolean canRead(Element element) {
		return element.getNodeName().equals("Properties");
	}

	public InfoStore read(KnowledgeBase kb, Element element) throws IOException {
		InfoStore infoStore = new DefaultInfoStore();
		List<Element> proplist = XMLUtil.getElementList(element.getChildNodes());
		for (Element prop : proplist) {
			if (prop.getNodeName().equals("Property")) {
				// [MISC]:aha:obsolete after supportknowledge refactoring is
				// propagated
				String name = "";
				if (prop.getAttributes().getNamedItem("name") != null) {
					name = prop.getAttributes().getNamedItem("name").getNodeValue();
				}
				else {
					name = prop.getAttributes().getNamedItem("descriptor").getNodeValue();
				}

				Property<?> property = null;
				try {
					property = Property.getUntypedProperty(name);
				}
				catch (NoSuchElementException e) {
					Logger.getLogger("Persistence").log(Level.WARNING,
							"Property " + name + " is not in use any more.");
					continue;
				}
				String textContent = prop.getTextContent();
				List<Element> elementList = XMLUtil.getElementList(prop.getChildNodes());
				if (elementList.size() == 0 && !textContent.trim().equals("")) {
					Object o = XMLUtil.getPrimitiveValue(textContent,
							prop.getAttribute("class"));
					infoStore.addValue(property, property.castToStoredValue(o));
				}
				else {
					List<Element> childNodes = elementList;
					if (childNodes.size() == 0) {
						infoStore.addValue(property, "");
					}
					else if (childNodes.size() == 1) {
						infoStore.addValue(property, PersistenceManager.getInstance().readFragment(
								childNodes.get(0), kb));
					}
					else {
						throw new IOException("Property must have exactly one child.");
					}
				}
			}
		}
		return infoStore;
	}
}
