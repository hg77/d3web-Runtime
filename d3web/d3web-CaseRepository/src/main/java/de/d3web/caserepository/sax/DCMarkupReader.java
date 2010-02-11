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

/*
 * Created on 06.10.2003
 */
package de.d3web.caserepository.sax;

import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

import org.xml.sax.Attributes;

import de.d3web.caserepository.CaseObjectImpl;
import de.d3web.core.KnowledgeBase;
import de.d3web.core.terminology.info.DCElement;
import de.d3web.core.terminology.info.DCMarkup;

/**
 * 06.10.2003 18:24:53
 * @author hoernlein
 */
public class DCMarkupReader extends AbstractTagReader {

	protected DCMarkupReader(String id) { super(id); }
	private static DCMarkupReader instance;
	private DCMarkupReader() { this("DCMarkupReader"); }
	public static AbstractTagReader getInstance() {
		if (instance == null)
			instance = new DCMarkupReader();
		return instance;
	}

	private int[] currentCreationDate = null;
	private DCMarkup currentDCMarkup = null;
	private DCElement currentDCElement = null;
	private boolean dcMarkupSet = false;
	private boolean ignoreStackedMarkup = false;

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.sax.AbstractTagReader#getTagNames()
	 */
	public List getTagNames() {
		return Arrays.asList(new String[] {
			"DCMarkup", "Metadata",
			"DCElement",
			"CreationDate",
			"Date",
			"Time",
			"Author",
			"Title",
			"Id", "ID", "id"
		});
	}
	
	/* (non-Javadoc)
	 * @see de.d3web.caserepository.sax.AbstractTagReader#initialize(de.d3web.kernel.domainModel.KnowledgeBase, de.d3web.caserepository.CaseObjectImpl)
	 */
	public void initialize(KnowledgeBase knowledgeBase, CaseObjectImpl caseObject) {
		dcMarkupSet = false;
		ignoreStackedMarkup = false;
		super.initialize(knowledgeBase, caseObject);
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.sax.AbstractTagReader#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	protected void startElement(String uri, String localName, String qName, Attributes attributes) {
		if (ignoreStackedMarkup)
			return;
		if ("DCMarkup".equals(qName) || "Metadata".equals(qName)) {
			startDCMarkup(attributes);
		} else if ("DCElement".equals(qName)) {
			startDCElement(null, attributes);
		} else if (Arrays.asList(new String[] {
				"Author",
				"Title",
				"Id", "ID", "id"
			}).contains(qName)) {
			startDCElement(qName.toLowerCase(), attributes);
		} else if ("CreationDate".equals(qName)) {
			startCreationDate(attributes);
		} else if ("Date".equals(qName)) {
			startDate(attributes);
		} else if ("Time".equals(qName)) {
			startTime(attributes);
		}
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.sax.AbstractTagReader#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	protected void endElement(String uri, String localName, String qName) {
		if ("DCMarkup".equals(qName) || "Metadata".equals(qName)) {
			endDCMarkup();
		} else if ("CreationDate".equals(qName)) {
			if (!ignoreStackedMarkup)
				endCreationDate();
		} else if ("DCElement".equals(qName)) {
			if (!ignoreStackedMarkup)
				endDCElement();
		} else if (Arrays.asList(new String[] {
				"Author",
				"Title",
				"Id", "ID", "id"
			}).contains(qName)) {
			if (!ignoreStackedMarkup)
				endDCElement();
		}
	}

	private void startDCMarkup(Attributes attributes) {
		// [MISC]:aha:super-duper legacy code
		if (currentDCMarkup != null) {
			ignoreStackedMarkup = true;
			return;
		}
		currentDCMarkup = new DCMarkup();
	} 

	private void endDCMarkup() {
		if (ignoreStackedMarkup) {
			ignoreStackedMarkup = false;
			return;
		}
		// this should have the effect, that only once per case
		// this reader sets the case-DCMarkup
		if (!dcMarkupSet) {
			getCaseObject().setDCMarkup(currentDCMarkup);
			dcMarkupSet = true;
		}
		currentDCMarkup = null;
	}
	
	private void startDCElement(String string, Attributes attributes) {
		currentDCElement = null;
		if (currentDCMarkup != null) {
			String value = attributes.getValue("value");

			DCElement dce = null;
			if (string != null) {
				if ("author".equals(string))
					dce = DCElement.CREATOR;
				else if ("title".equals(string))
					dce = DCElement.TITLE;
				else if ("id".equals(string))
					dce = DCElement.IDENTIFIER;
			} else {
				String label = attributes.getValue("label");
				if (label == null)
					label = attributes.getValue("name");
				dce = DCElement.getDCElementFor(label);
			}
			if (dce != null) {
				if (value == null) {
					// either old-style or a cdata-value
					currentDCElement = dce;
				} else {
					currentDCMarkup.setContent(dce, value);
					currentDCElement = null;
				}
			}
		}
	}

	private void endDCElement() {
		if (currentDCElement != null) {
			currentDCMarkup.setContent(currentDCElement, getTextBetweenCurrentTag());
			currentDCElement = null;
		}
	}

	private void startCreationDate(Attributes attributes) {
		currentCreationDate = new int[5];
	}

	private void endCreationDate() {
		GregorianCalendar cal =
			new GregorianCalendar(
				currentCreationDate[0],
				currentCreationDate[1],
				currentCreationDate[2],
				currentCreationDate[3],
				currentCreationDate[4]);

		currentDCMarkup.setContent(DCElement.DATE, DCElement.date2string(cal.getTime()));
		
		currentCreationDate = null;
	}
	
	private void startDate(Attributes attributes) {
		String m = attributes.getValue("m");
		String y = attributes.getValue("y");
		String d = attributes.getValue("d");

		currentCreationDate[0] = Integer.parseInt(y);
		currentCreationDate[1] = Integer.parseInt(m);
		currentCreationDate[2] = Integer.parseInt(d);
	}

	private void startTime(Attributes attributes) {
		String h = attributes.getValue("h");
		String m = attributes.getValue("m");

		currentCreationDate[3] = Integer.parseInt(h);
		currentCreationDate[4] = Integer.parseInt(m);
	}


}
