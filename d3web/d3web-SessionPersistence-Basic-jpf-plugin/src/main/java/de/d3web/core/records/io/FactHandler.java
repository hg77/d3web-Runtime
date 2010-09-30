/*
 * Copyright (C) 2010 denkbares GmbH
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
package de.d3web.core.records.io;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.core.io.NoSuchFragmentHandlerException;
import de.d3web.core.io.progress.ProgressListener;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.records.FactRecord;
import de.d3web.core.records.SessionRecord;
import de.d3web.core.session.Value;

/**
 * Handels the facts element in an xml case repository
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 15.09.2010
 */
public class FactHandler implements SessionPersistenceHandler {

	@Override
	public void read(Element sessionElement, SessionRecord sessionRecord, ProgressListener listener) throws IOException {
		List<Element> elementList = XMLUtil.getElementList(sessionElement.getChildNodes());
		for (Element e : elementList) {
			if (e.getNodeName().equals("facts")) {
				List<Element> factCategorieList = XMLUtil.getElementList(e.getChildNodes());
				List<FactRecord> valueFacts = new LinkedList<FactRecord>();
				List<FactRecord> interviewFacts = new LinkedList<FactRecord>();
				for (Element factCategorieElement : factCategorieList) {
					if (factCategorieElement.getNodeName().equals("valueFacts")) {
						getFacts(sessionRecord.getKnowledgeBase(), factCategorieElement, valueFacts);
					}
					else if (factCategorieElement.getNodeName().equals("interviewFacts")) {
						getFacts(sessionRecord.getKnowledgeBase(), factCategorieElement,
								interviewFacts);
					}
				}
				for (FactRecord fact : valueFacts) {
					sessionRecord.addValueFact(fact);
				}
				for (FactRecord fact : interviewFacts) {
					sessionRecord.addInterviewFact(fact);
				}
			}
		}
	}

	private void getFacts(KnowledgeBase kb, Element factCategorieElement, List<FactRecord> facts) throws NoSuchFragmentHandlerException, IOException {
		for (Element factElement : XMLUtil.getElementList(factCategorieElement.getChildNodes())) {
			String oName = factElement.getAttribute("objectName");
			TerminologyObject idObject = kb.searchObjectForName(
					oName);
			String psmName = factElement.getAttribute("psm");
			if (psmName.length() == 0) psmName = null;
			List<Element> valueNodes = XMLUtil.getElementList(factElement.getChildNodes());
			Object readFragment = SessionPersistenceManager.getInstance().readFragment(
					valueNodes.get(0), kb);
			FactRecord fact = new FactRecord(idObject, psmName,
					(Value) readFragment);
			facts.add(fact);
		}
	}

	@Override
	public void write(Element sessionElement, SessionRecord sessionRecord, ProgressListener listener) throws IOException {
		Document doc = sessionElement.getOwnerDocument();
		Element factsElement = doc.createElement("facts");
		sessionElement.appendChild(factsElement);
		Element valueFactsElement = doc.createElement("valueFacts");
		factsElement.appendChild(valueFactsElement);
		Element interviewFactsElement = doc.createElement("interviewFacts");
		factsElement.appendChild(interviewFactsElement);
		addFacts(sessionRecord.getValueFacts(), doc, valueFactsElement);
		addFacts(sessionRecord.getInterviewFacts(), doc, interviewFactsElement);
	}

	private void addFacts(List<FactRecord> facts, Document doc, Element factsElement) throws NoSuchFragmentHandlerException, IOException {
		for (FactRecord fact : facts) {
			Element factElement = doc.createElement("fact");
			factsElement.appendChild(factElement);
			factElement.setAttribute("objectName", fact.getObject().getName());
			String psm = fact.getPsm();
			if (psm != null) factElement.setAttribute("psm", psm);
			factElement.appendChild(SessionPersistenceManager.getInstance().writeFragment(
					fact.getValue(), doc));
		}
	}
}