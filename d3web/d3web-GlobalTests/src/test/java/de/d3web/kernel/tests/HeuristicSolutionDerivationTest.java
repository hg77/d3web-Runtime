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

package de.d3web.kernel.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.terminology.DiagnosisState;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.core.manage.RuleFactory;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.plugin.test.InitPluginManager;
import de.d3web.scoring.Score;

/**
 * This test is designed to control the solution derivation process of the
 * heuristic problem solver.
 * 
 * The tested knowledge base contains the following terminology objects:
 * 
 * <b>Questions</b> Exhaust fumes [oc] - black - blue - invisible Fuel [oc] -
 * unleaded gasoline - diesel
 * 
 * <b>Solutions</b> Clogged air filter
 * 
 * The problem solving is based on the following <b>Rules</b>:
 * 
 * Exhaust fumes = black => Clogged air filter = P3 Fuel = unleaded gasoline =>
 * Clogged air filter = P5
 * 
 * 
 * @author Sebastian Furth
 * 
 */
public class HeuristicSolutionDerivationTest {

	private static KnowledgeBaseManagement kbm;
	private static Session session;

	@BeforeClass
	public static void setUp() throws Exception {
		InitPluginManager.init();
		kbm = KnowledgeBaseManagement.createInstance();
		addTerminologyObjects();
		addRules();
		session = SessionFactory.createSession(kbm.getKnowledgeBase());
	}

	private static void addTerminologyObjects() {

		// Question 'Exhaust fumes'
		String exhaustFumes = "Exhaust fumes";
		String[] exhaustFumesAlternatives = new String[] {
				"black", "blue", "invisible" };
		kbm.createQuestionOC(exhaustFumes, kbm.getKnowledgeBase().getRootQASet(),
				exhaustFumesAlternatives);

		// Question 'Fuel'
		String fuel = "Fuel";
		String[] fuelAlternatives = new String[] {
				"diesel", "unleaded gasoline" };
		kbm.createQuestionOC(fuel, kbm.getKnowledgeBase().getRootQASet(),
				fuelAlternatives);

		// Solution 'Clogged air filter'
		kbm.createSolution("Clogged air filter");
	}

	private static void addRules() {

		Solution cloggedAirFilter = kbm.findSolution("Clogged air filter");

		// Exhaust fumes = black => Clogged air filter = P3
		Question exhaustFumes = kbm.findQuestion("Exhaust fumes");
		Value black = kbm.findValue(exhaustFumes, "black");
		Condition condition = new CondEqual(exhaustFumes, black);
		RuleFactory.createHeuristicPSRule(kbm.createRuleID(), cloggedAirFilter, Score.P3,
				condition);

		// Fuel = unleaded gasoline => Clogged air filter = P5
		Question fuel = kbm.findQuestion("Fuel");
		Value unleadedGasoline = kbm.findValue(fuel, "unleaded gasoline");
		condition = new CondEqual(fuel, unleadedGasoline);
		RuleFactory.createHeuristicPSRule(kbm.createRuleID(), cloggedAirFilter, Score.P5,
				condition);
	}

	@Test
	public void testTerminlogyObjectExistence() {

		// Question 'Exhaust fumes'
		Question exhaustFumes = kbm.findQuestion("Exhaust fumes");
		assertNotNull("Question 'Exhaust fumes' isn't in the Knowledgebase.",
				exhaustFumes);

		// Values of 'Exhaust fumes'
		Value black = kbm.findValue(exhaustFumes, "black");
		assertNotNull(
				"Value 'black' for Question 'Exhaust fumes' isn't in the Knowledgebase",
				black);
		Value blue = kbm.findValue(exhaustFumes, "blue");
		assertNotNull(
				"Value 'blue' for Question 'Exhaust fumes' isn't in the Knowledgebase",
				blue);
		Value invisible = kbm.findValue(exhaustFumes, "invisible");
		assertNotNull(
				"Value 'blue' for Question 'Exhaust fumes' isn't in the Knowledgebase",
				invisible);

		// Question 'Fuel'
		Question fuel = kbm.findQuestion("Fuel");
		assertNotNull("Question 'Fuel' isn't in the Knowledgebase.", fuel);

		// Values of 'Fuel'
		Value unleadedGasoline = kbm.findValue(fuel, "unleaded gasoline");
		assertNotNull(
				"Value 'unleaded gasoline' for Question 'Fuel' isn't in the Knowledgebase",
				unleadedGasoline);
		Value diesel = kbm.findValue(fuel, "diesel");
		assertNotNull("Value 'diesel' for Question 'Fuel' isn't in the Knowledgebase",
				diesel);

		// Solution 'Clogged air filter'
		Solution cloggedAirFilter = kbm.findSolution("Clogged air filter");
		assertNotNull("Solution 'Clogged air filter' isn't in the Knowledgebase",
				cloggedAirFilter);
	}

	@Test
	public void testSetValue() {

		Question exhaustFumes = kbm.findQuestion("Exhaust fumes");
		Question fuel = kbm.findQuestion("Fuel");
		Solution solution = kbm.findSolution("Clogged air filter");

		// SET 'Exhaust fumes' = 'black'
		Value black = kbm.findValue(exhaustFumes, "black");
		session.setValue(exhaustFumes, black);

		// TEST 'Exhaust fumes' == 'black'
		Value exhaustFumesValue = session.getValue(exhaustFumes);
		assertEquals("Question 'Exhaust fumes' has wrong value", black, exhaustFumesValue);

		// TEST 'Clogged air filter' == SUGGESTED
		DiagnosisState cloggedAirFilterState = session.getBlackboard().getState(solution);
		assertTrue("Solution 'Clogged air filter' has wrong state. Expected 'SUGGESTED'",
				cloggedAirFilterState.hasState(DiagnosisState.State.SUGGESTED));

		// SET 'Fuel' = 'unleaded gasoline'
		Value unleadedGasoline = kbm.findValue(fuel, "unleaded gasoline");
		session.setValue(fuel, unleadedGasoline);

		// TEST 'Fuel' == 'unleaded gasoline'
		Value fuelValue = session.getValue(fuel);
		assertEquals("Question 'Fuel' has wrong value", unleadedGasoline, fuelValue);

		// TEST 'Clogged air filter' == ESTABLISHED
		cloggedAirFilterState = session.getBlackboard().getState(solution);
		assertTrue(
				"Solution 'Clogged air filter' has wrong state. Expected 'ESTABLISHED'",
				cloggedAirFilterState.hasState(DiagnosisState.State.ESTABLISHED));
	}

	@Test
	public void testChangeValue() {

		Question fuel = kbm.findQuestion("Fuel");
		Question exhaustFumes = kbm.findQuestion("Exhaust fumes");
		Solution cloggedAirFilter = kbm.findSolution("Clogged air filter");

		// SET 'Fuel' = 'diesel'
		Value diesel = kbm.findValue(fuel, "diesel");
		session.setValue(fuel, diesel);

		// TEST 'Fuel' == 'diesel'
		Value fuelValue = session.getValue(fuel);
		assertEquals("Question 'Fuel' has wrong value", diesel, fuelValue);

		// TEST 'Clogged air filter' == SUGGESTED
		DiagnosisState cloggedAirFilterState = session.getBlackboard().getState(
				cloggedAirFilter);
		assertTrue("Solution 'Clogged air filter' has wrong state. Expected 'SUGGESTED'",
				cloggedAirFilterState.hasState(DiagnosisState.State.SUGGESTED));

		// SET 'Exhaust fumes' = 'blue'
		Value blue = kbm.findValue(exhaustFumes, "blue");
		session.setValue(exhaustFumes, blue);

		// TEST 'Exhaust fumes' == 'blue'
		Value exhaustFumesValue = session.getValue(exhaustFumes);
		assertEquals("Question 'Exhaust fumes' has wrong value", blue, exhaustFumesValue);

		// TEST 'Clogged air filter' == UNCLEAR
		cloggedAirFilterState = session.getBlackboard().getState(cloggedAirFilter);
		assertTrue("Solution 'Clogged air filter' has wrong state. Expected 'UNCLEAR'",
				cloggedAirFilterState.hasState(DiagnosisState.State.UNCLEAR));
	}

	@Test
	public void testSetUndefinedValue() {

		Question exhaustFumes = kbm.findQuestion("Exhaust fumes");
		Solution cloggedAirFilter = kbm.findSolution("Clogged air filter");

		// SET 'Exhaust fumes' = 'black'
		Value black = kbm.findValue(exhaustFumes, "black");
		session.setValue(exhaustFumes, black);

		// TEST 'Exhaust fumes' == 'black'
		Value exhaustFumesValue = session.getValue(exhaustFumes);
		assertEquals("Question 'Exhaust fumes' has wrong value", black, exhaustFumesValue);

		// TEST 'Clogged air filter' == SUGGESTED
		DiagnosisState cloggedAirFilterState = session.getBlackboard().getState(
				cloggedAirFilter);
		assertTrue("Solution 'Clogged air filter' has wrong state. Expected 'SUGGESTED'",
				cloggedAirFilterState.hasState(DiagnosisState.State.SUGGESTED));

		// SET 'Exhaust fumes' = 'UNDEFINED'
		session.setValue(exhaustFumes, UndefinedValue.getInstance());

		// TEST 'Exhaust fumes' == 'UNDEFINED'
		exhaustFumesValue = session.getValue(exhaustFumes);
		assertEquals("Question 'Exhaust fumes' has wrong value",
				UndefinedValue.getInstance(), exhaustFumesValue);

		// TEST 'Clogged air filter' == UNCLEAR
		cloggedAirFilterState = session.getBlackboard().getState(cloggedAirFilter);
		assertTrue("Solution 'Clogged air filter' has wrong state. Expected 'UNCLEAR'",
				cloggedAirFilterState.hasState(DiagnosisState.State.UNCLEAR));
	}

}