/*
 * Copyright (C) 2009 denkbares GmbH
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
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import de.d3web.core.io.Persistence;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.InfoStoreUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.knowledge.terminology.QuestionYN;
import de.d3web.core.knowledge.terminology.QuestionZC;
import de.d3web.core.knowledge.terminology.info.NumericalInterval;
import de.d3web.core.knowledge.terminology.info.Property.Autosave;

/**
 * FragmentHandler for Questions Children are ignored, hierarchies are
 * read/written by the knowledge readers/writers.
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class QuestionHandler implements FragmentHandler<KnowledgeBase> {

	@Override
	public boolean canRead(Element element) {
		return (element.getNodeName().equals("Question"));
	}

	@Override
	public boolean canWrite(Object object) {
		return (object instanceof Question);
	}

	@Override
	public Object read(Element element, Persistence<KnowledgeBase> persistence) throws IOException {
		String type = element.getAttribute("type");
		String name = element.getAttribute("name");
		List<Element> childNodes = XMLUtil.getElementList(element.getChildNodes());
		ArrayList<NumericalInterval> intervals = null;
		Element answersElement = null;
		KnowledgeBase kb = persistence.getArtifact();
		Question question;
		switch (type) {
			case "YN":
				question = new QuestionYN(kb, name);
				break;
			case QuestionZC.XML_IDENTIFIER:
				question = new QuestionZC(kb, name);
				break;
			case "OC":
				question = new QuestionOC(kb, name);
				break;
			case "MC":
				question = new QuestionMC(kb, name);
				break;
			case "Num":
				question = new QuestionNum(kb, name);
				break;
			case "Text":
				question = new QuestionText(kb, name);
				break;
			case "Date":
				question = new QuestionDate(kb, name);
				break;
			default:
				throw new IOException("Unknown question type " + type);
		}
		PropertiesHandler ph = new PropertiesHandler();
		for (Element child : childNodes) {
			if (child.getNodeName().equals("Answers")) {
				answersElement = child;
			}
			else if (child.getNodeName().equals(XMLUtil.INFO_STORE)) {
				XMLUtil.fillInfoStore(persistence, question.getInfoStore(), child);
			}
			else if (ph.canRead(child)) {
				InfoStoreUtil.copyEntries(ph.read(persistence, child), question.getInfoStore());
			}
			// If the child is none of the types above and it doesn't contain
			// the children or the costs,
			// it contains the properties or the Intervalls.
			// Costs are no longer stored in IDObjects, so they are ignored
			else if (!child.getNodeName().equals("Children")
					&& !child.getNodeName().equals("Costs")) {
				Object readFragment = persistence.readFragment(child);
				if (readFragment instanceof List<?>) {
					intervals = new ArrayList<>();
					List<?> list = (List<?>) readFragment;
					for (Object o : list) {
						if (o instanceof NumericalInterval) {
							intervals.add((NumericalInterval) o);
						}
					}
				}
				else {
					throw new IOException("Object " + readFragment
							+ " is not a valid child of a question.");
				}
			}
		}
		if (intervals != null) {
			((QuestionNum) question).setValuePartitions(intervals);
		}
		if (answersElement != null && question instanceof QuestionChoice) {
			QuestionChoice qc = (QuestionChoice) question;
			List<Element> answerNodes = XMLUtil.getElementList(answersElement.getChildNodes());
			List<Choice> answers = new ArrayList<>();
			for (Element answerElement : answerNodes) {
				answers.add((Choice) persistence.readFragment(answerElement));
			}
			qc.setAlternatives(answers);
		}
		return question;
	}

	@Override
	public Element write(Object object, Persistence<KnowledgeBase> persistence) throws IOException {
		Question q = (Question) object;
		Element e = persistence.getDocument().createElement("Question");
		e.setAttribute("name", q.getName());
		if (q instanceof QuestionYN) {
			e.setAttribute("type", "YN");
		}
		else if (q instanceof QuestionZC) {
			e.setAttribute("type", QuestionZC.XML_IDENTIFIER);
		}
		else if (q instanceof QuestionOC) {
			e.setAttribute("type", "OC");
		}
		else if (q instanceof QuestionMC) {
			e.setAttribute("type", "MC");
		}
		else if (q instanceof QuestionNum) {
			e.setAttribute("type", "Num");
			// adding intervalls
			List<?> valuePartitions = ((QuestionNum) q).getValuePartitions();
			if (valuePartitions != null) {
				e.appendChild(persistence.writeFragment(valuePartitions));
			}
		}
		else if (q instanceof QuestionText) {
			e.setAttribute("type", "Text");
		}
		else if (q instanceof QuestionDate) {
			e.setAttribute("type", "Date");
		}

		if (q instanceof QuestionChoice) {
			QuestionChoice qc = (QuestionChoice) q;
			List<Choice> children = qc.getAllAlternatives();
			if (children != null) {
				Element answerNodes = persistence.getDocument().createElement("Answers");
				for (Choice child : children) {
					answerNodes.appendChild(persistence.writeFragment(child));
				}
				e.appendChild(answerNodes);
			}
		}
		XMLUtil.appendInfoStore(persistence, e, q, Autosave.basic);
		return e;
	}

}
