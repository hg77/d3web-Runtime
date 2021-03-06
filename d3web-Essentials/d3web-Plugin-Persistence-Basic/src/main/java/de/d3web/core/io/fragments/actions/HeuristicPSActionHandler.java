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
package de.d3web.core.io.fragments.actions;

import java.io.IOException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.d3web.core.io.Persistence;
import de.d3web.core.io.fragments.FragmentHandler;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.scoring.ActionHeuristicPS;
import de.d3web.scoring.Score;

/**
 * Handels HeuristicPSActions
 * 
 * @author Norman Brümmer, Markus Friedrich(denkbares GmbH)
 */
public class HeuristicPSActionHandler implements FragmentHandler<KnowledgeBase> {

	@Override
	public boolean canRead(Element element) {
		return XMLUtil.checkNameAndType(element, "Action", "ActionHeuristicPS");
	}

	@Override
	public boolean canWrite(Object object) {
		return (object instanceof ActionHeuristicPS);
	}

	@Override
	public Object read(Element element, Persistence<KnowledgeBase> persistence) throws IOException {
		Score score = null;
		Solution diag = null;
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); ++i) {
			Node child = children.item(i);
			if (child.getNodeName().equalsIgnoreCase("Score")) {
				String value = child.getAttributes().getNamedItem("value").getNodeValue();
				score = XMLUtil.getScore(value);
			}
			else if (child.getNodeName().equalsIgnoreCase("Diagnosis")) {
				String id = child.getAttributes().getNamedItem("name").getNodeValue();
				diag = persistence.getArtifact().getManager().searchSolution(id);
			}
		}
		ActionHeuristicPS actionHeuristicPS = new ActionHeuristicPS();
		actionHeuristicPS.setSolution(diag);
		actionHeuristicPS.setScore(score);
		return actionHeuristicPS;
	}

	@Override
	public Element write(Object object, Persistence<KnowledgeBase> persistence) throws IOException {
		ActionHeuristicPS action = (ActionHeuristicPS) object;
		Element element = persistence.getDocument().createElement("Action");
		element.setAttribute("type", "ActionHeuristicPS");
		Score theScore = action.getScore();
		Solution theDiag = action.getSolution();
		String scoreSymbol = "";
		String diagName = "";
		if (theScore != null) {
			scoreSymbol = theScore.getSymbol();
			if ((scoreSymbol == null) || (scoreSymbol.equals(""))) {
				scoreSymbol = theScore.getScore() + "";
			}
		}
		if (theDiag != null) {
			diagName = theDiag.getName();
		}
		Element scoreElement = persistence.getDocument().createElement("Score");
		scoreElement.setAttribute("value", scoreSymbol);
		Element diagElement = persistence.getDocument().createElement("Diagnosis");
		diagElement.setAttribute("name", diagName);
		element.appendChild(scoreElement);
		element.appendChild(diagElement);
		return element;
	}

}
