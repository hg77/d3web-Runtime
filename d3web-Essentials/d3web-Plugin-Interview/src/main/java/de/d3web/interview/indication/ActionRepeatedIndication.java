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

package de.d3web.interview.indication;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.PropagationEntry;
import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.Indication.State;
import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.indication.ActionNextQASet;
import de.d3web.interview.Interview;
import de.d3web.interview.inference.PSMethodInterview;

/**
 * This action type indicates a list of {@link QASet} instances no matter,
 * whether this {@link InterviewObject} objects has been already answered or
 * not. That way, a question can be repeatedly presented in the interview.
 * 
 * @author Joachim Baumeister
 */
public class ActionRepeatedIndication extends ActionNextQASet {

	public ActionRepeatedIndication(QASet... qaSets) {
		setQASets(qaSets);
	}

	@Override
	public State getState() {
		return State.REPEATED_INDICATED;
	}

	@Override
	public void doIt(Session session, Object source, PSMethod psmethod) {
		for (QASet qaset : getQASets()) {

			if (qaset == null) continue;

			Indication oldIndication = session.getBlackboard().getIndication(qaset);
			Indication indication = new Indication(getState(),
					qaset.getKnowledgeBase().getManager().getTreeIndex(qaset));
			Fact fact = FactFactory.createFact(qaset, indication, source,
					psmethod);
			session.getBlackboard().addInterviewFact(fact);
			// notify immediately to enable usage in condition
			PropagationEntry entry = new PropagationEntry(qaset, oldIndication, indication);
			Interview interview = session.getSessionObject(session.getPSMethodInstance(PSMethodInterview.class));
			interview.notifyFactChange(entry);
		}
	}

	@Override
	public String toString() {
		return "RepeatedIndicate: " + getQASets();
	}

}