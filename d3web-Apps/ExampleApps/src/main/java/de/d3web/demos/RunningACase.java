/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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

package de.d3web.demos;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.d3web.core.inference.condition.CondAnd;
import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.CondNumGreater;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.manage.RuleFactory;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.protocol.ProtocolEntry;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.NumValue;
import com.denkbares.plugin.test.InitPluginManager;
import de.d3web.scoring.Score;

public class RunningACase {

	private QContainer demoQuestions;
	private QuestionOC pregnant;
	private QuestionNum weight;
	private Solution dangerousMood;
	private ChoiceValue yes;

	public static void main(String[] args) throws IOException {
		RunningACase myApp = new RunningACase();
		myApp.runDemoCase();
	}

	public void runDemoCase() throws IOException {
		KnowledgeBase knowledgeBase = buildKnowledgeBase();

		PrintStream out = System.out;
		// Create a case (problem-solving session and set
		// all specified question/answers
		out.println("+++ Setting values +++");
		Session session = SessionFactory.createSession(knowledgeBase);

		// set: pregnant = yes
		Fact fact1 = FactFactory.createUserEnteredFact(
				pregnant, yes);
		session.getBlackboard().addValueFact(fact1);
		out.println(fact1);
		// set: weight = 80
		Fact fact2 = FactFactory.createUserEnteredFact(
				weight, new NumValue(80));
		session.getBlackboard().addValueFact(fact2);
		out.println(fact2);

		// Print all solutions with a state != UNCLEAR
		out.println("+++ Solutions +++");
		for (Solution solution : knowledgeBase.getManager().getSolutions()) {
			Rating state = session.getBlackboard().getRating(solution);
			if (!state.hasState(Rating.State.UNCLEAR)) {
				out.println("  " + solution + " (" + state + ")");
			}
		}

		// Show all entered findings
		out.println("+++ Entered Questions +++");
		for (ProtocolEntry entry : session.getProtocol().getProtocolHistory()) {
			out.println("  " + entry);
		}
	}

	private KnowledgeBase buildKnowledgeBase() throws IOException {
		// root {container}
		// - demoQuestion {containers}
		// - pregnant [oc]
		// - weight [num]

		// Solution: dangerousMood
		InitPluginManager.init();
		KnowledgeBase kb = KnowledgeBaseUtils.createKnowledgeBase();
		QASet root = kb.getRootQASet();

		demoQuestions = new QContainer(root, "demoQuestions");
		pregnant = new QuestionOC(demoQuestions, "pregnant", "yes", "no");
		yes = new ChoiceValue(KnowledgeBaseUtils.findChoice(pregnant, "yes"));
		weight = new QuestionNum(demoQuestions, "weight");

		dangerousMood = new Solution(kb.getRootSolution(), "dangerousMood");

		// Define the init questionnaire
		kb.setInitQuestions(Collections.singletonList(demoQuestions));

		// Define the magic rule: preganant=yes AND weight > 70 => dangerousMood
		// (P7)
		List<Condition> terms = new ArrayList<>();
		terms.add(new CondEqual(pregnant, yes));
		terms.add(new CondNumGreater(weight, (double) 70));
		RuleFactory.createHeuristicPSRule(dangerousMood, Score.P7, new CondAnd(terms));

		return kb;
	}

}
