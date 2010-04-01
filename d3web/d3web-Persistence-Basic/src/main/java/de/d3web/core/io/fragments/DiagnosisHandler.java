/*
 * Copyright (C) 2009 denkbares GmbH
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
package de.d3web.core.io.fragments;

import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.d3web.core.io.PersistenceManager;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.Util;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.info.Properties;
import de.d3web.scoring.Score;
/**
 * FragmentHanler for Diagnosis
 * Children are ignored, hierarchies are read/written by the knowledge readers/writers.
 *
 * @author Markus Friedrich (denkbares GmbH)
 */
public class DiagnosisHandler implements FragmentHandler {

	@Override
	public boolean canRead(Element element) {
		return element.getNodeName().equals("Diagnosis");
	}

	@Override
	public boolean canWrite(Object object) {
		return (object instanceof Solution);
	}

	@Override
	public Object read(KnowledgeBase kb, Element element) throws IOException {
		String id = element.getAttribute("ID");
		String apriori = element.getAttribute("aPriProb");
		Solution diag = new Solution(id);
		if (apriori != null) {
			diag.setAprioriProbability(Util.getScore(apriori));
		}
		Properties properties = null;
		for (Element child: XMLUtil.getElementList(element.getChildNodes())) {
			if (child.getNodeName().equals("Text")) {
				diag.setName(child.getTextContent());
			}
			//If the child is none of the types above and it doesn't contain the children or the costs,
			//it contains the properties.
			//Costs are no longer stored in IDObjects, so they are ignored.
			else if (!child.getNodeName().equals("Children")&&!child.getNodeName().equals("Costs")) {
				properties=(Properties) PersistenceManager.getInstance().readFragment(child, kb);
			}
		}
		if (properties != null) {
			diag.setProperties(properties);
		}
		diag.setKnowledgeBase(kb);
		return diag;
	}

	@Override
	public Element write(Object object, Document doc) throws IOException {
		Solution diag = (Solution) object;
		Element element = doc.createElement("Diagnosis");
		element.setAttribute("ID", diag.getId());
		XMLUtil.appendTextNode(diag.getName(), element);
		Score apriori = diag.getAprioriProbability();
		if (apriori != null) {
			element.setAttribute("aPriProb", apriori.getSymbol());
		}
		Properties properties = diag.getProperties();
		if (properties!=null && !properties.isEmpty()) {
			element.appendChild(PersistenceManager.getInstance().writeFragment(properties, doc));
		}
		return element;
	}
}
