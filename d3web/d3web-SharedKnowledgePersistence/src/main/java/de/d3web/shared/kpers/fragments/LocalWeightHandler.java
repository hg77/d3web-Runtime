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
package de.d3web.shared.kpers.fragments;

import java.io.IOException;
import java.util.Enumeration;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.d3web.core.kpers.fragments.FragmentHandler;
import de.d3web.core.kpers.utilities.XMLUtil;
import de.d3web.kernel.domainModel.Answer;
import de.d3web.kernel.domainModel.Diagnosis;
import de.d3web.kernel.domainModel.KnowledgeBase;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.domainModel.qasets.QuestionChoice;
import de.d3web.kernel.psMethods.shared.LocalWeight;
/**
 * Handles LocalWeights
 * @author Norman Brümmer, Markus Friedrich (denkbares GmbH)
 */
public class LocalWeightHandler implements FragmentHandler {

	@Override
	public boolean canRead(Element element) {
		return XMLUtil.checkNameAndType(element, "KnowledgeSlice", "localweight");
	}

	@Override
	public boolean canWrite(Object object) {
		return (object instanceof LocalWeight);
	}

	@Override
	public Object read(KnowledgeBase kb, Element n) throws IOException {
		String questionID = null;
		String diagnosisID = null;
		Question q = null;
		Diagnosis d = null;
		questionID = n.getAttributes().getNamedItem("questionID")
				.getNodeValue();
		diagnosisID = n.getAttributes().getNamedItem("diagnosisID")
				.getNodeValue();

		q = kb.searchQuestion(questionID);
		d = kb.searchDiagnosis(diagnosisID);

		if (q instanceof QuestionChoice) {
			LocalWeight lw = new LocalWeight();
			lw.setQuestion(q);
			lw.setDiagnosis(d);
			NodeList abChildren = n.getChildNodes();
			for (int k = 0; k < abChildren.getLength(); ++k) {
				Node abChild = abChildren.item(k);
				if (abChild.getNodeName().equalsIgnoreCase("values")) {
					NodeList vals = abChild.getChildNodes();
					for (int l = 0; l < vals.getLength(); ++l) {
						Node valChild = vals.item(l);
						if (valChild.getNodeName().equalsIgnoreCase(
								"localweight")) {
							String ansID = valChild.getAttributes()
									.getNamedItem("ID").getNodeValue();
							Answer ans = XMLUtil.getAnswer(null, q, ansID);
							String value = valChild.getAttributes()
									.getNamedItem("value").getNodeValue();
							lw.setValue(ans, LocalWeight
									.convertConstantStringToValue(value));
						}
					}
				}
			}
			return lw;
		} else {
			throw new IOException(
					"no abnormality handling for questions of type "
							+ q.getClass());
		}
	}

	@Override
	public Element write(Object object, Document doc) throws IOException {
		LocalWeight localWeight = (LocalWeight) object;
		Element element = doc.createElement("KnowledgeSlice");
		element.setAttribute("ID", "W"+localWeight.getQuestion().getId());
		element.setAttribute("type", "localweight");
		element.setAttribute("questionID", localWeight.getQuestion().getId());
		element.setAttribute("diagnosisID", localWeight.getDiagnosis().getId());
		Element valuesNode = doc.createElement("values");
		Enumeration<Answer> answers = localWeight.getAnswerEnumeration();
		while (answers.hasMoreElements()) {
			Answer answer = answers.nextElement();
			Element localweightNode = doc.createElement("localweight");
			localweightNode.setAttribute("ID", answer.getId());
			localweightNode.setAttribute("value", LocalWeight.convertValueToConstantString(localWeight.getValue(answer)));
			valuesNode.appendChild(localweightNode);
		}
		element.appendChild(valuesNode);
		return element;
	}

}