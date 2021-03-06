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
package de.d3web.costbenefit.model.ids;

import java.util.List;

import de.d3web.core.inference.condition.Condition;
import de.d3web.core.inference.condition.Conditions;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.costbenefit.CostBenefitUtil;
import de.d3web.costbenefit.inference.StateTransition;
import de.d3web.costbenefit.model.SearchModel;

/**
 * QContainer Node for the virtual graph. Provides easy access to cost-benefit
 * issues of QContainers.
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class Node {

	private final QContainer qContainer;
	private final StateTransition st;
	private final SearchModel cbm;

	public Node(QContainer qcon, SearchModel cbm) {
		this.cbm = cbm;
		this.qContainer = qcon;
		this.st = qContainer.getKnowledgeStore().getKnowledge(StateTransition.KNOWLEDGE_KIND);
	}

	/**
	 * Checks if the Node is applicable in session. A node is applicable if the
	 * preconditions of the associated QContainer are matching session.
	 */
	public boolean isApplicable(Session session) {
		if (st == null) return true;
		Condition activationCondition = st.getActivationCondition();
		if (activationCondition == null) return true;
		return Conditions.isTrue(activationCondition, session);
	}

	public QContainer getQContainer() {
		return qContainer;
	}

	public StateTransition getStateTransition() {
		return st;
	}

	public double getCosts(Session session) {
		if (cbm == null) return getStaticCosts();
		return cbm.getCostFunction().getCosts(qContainer, session);
	}

	public double getStaticCosts() {
		return qContainer.getInfoStore().getValue(BasicProperties.COST);
	}

	/**
	 * Ensures that all questions of the Node's QContainer are answered. For
	 * unanswered Questions the expected values are set.
	 * 
	 * @param session the Session where the values should be set
	 * @return all Facts that are used to set the values
	 */
	public List<Fact> setNormalValues(Session session) {
		return CostBenefitUtil.setNormalValues(session, qContainer, this);
	}

	@Override
	public boolean equals(Object o) {
		return (o instanceof Node) && (this.qContainer.equals(((Node) o).qContainer));
	}

	@Override
	public int hashCode() {
		return qContainer.hashCode();
	}

	@Override
	public String toString() {
		return qContainer.getName();
	}
}
