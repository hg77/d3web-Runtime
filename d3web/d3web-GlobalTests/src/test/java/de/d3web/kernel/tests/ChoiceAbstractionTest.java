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

import org.junit.BeforeClass;
import org.junit.Test;

import de.d3web.core.inference.condition.CondEqual;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.core.manage.RuleFactory;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.plugin.test.InitPluginManager;

/**
 * This test is designed to control the setting
 * of abstract choice questions' values
 * 
 * The tested knowledgebase contains the following terminology
 * objects:
 * 
 * <b>Questions</b>
 * Weekday [oc]
 * - Monday
 * - Tuesday
 * - Wednesday
 * - Thursday
 * - Friday
 * - Saturday
 * - Sunday
 * Day [oc] <abstract>
 * - Workday
 * - Weekend
 * 
 * The problem solving is based on the following <b>Rules</b>:
 * 
 * Weekday = Monday => Day = Workday
 * Weekday = Tuesday => Day = Workday
 * Weekday = Wednesday => Day = Workday
 * Weekday = Thursday => Day = Workday
 * Weekday = Friday => Day = Workday
 * Weekday = Saturday => Day = Weekend
 * Weekday = Sunday => Day = Weekend
 * 
 * 
 * @author Sebastian Furth
 *
 */
public class ChoiceAbstractionTest {

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
		
		// Question 'Weekday'
		String weekday = "Weekday";
		String[] weekdayAlternatives = new String[] {"Monday", "Tuesday", "Wednesday", "Thursday", 
													 "Friday", "Saturday", "Sunday"};
		kbm.createQuestionOC(weekday, kbm.getKnowledgeBase().getRootQASet(), weekdayAlternatives);
		
		// Question 'Day'
		String day = "Day";
		String[] dayAlternatives = new String[] {"Workday", "Weekend"};
		Question questionDay = kbm.createQuestionOC(day, kbm.getKnowledgeBase().getRootQASet(), dayAlternatives);
		questionDay.getProperties().setProperty(Property.ABSTRACTION_QUESTION, Boolean.TRUE);
	}
	
	private static void addRules() {
		
		Question day = kbm.findQuestion("Day");
		Value workday = kbm.findValue(day, "Workday");
		Value weekend = kbm.findValue(day, "Weekend");
		
		Question weekday = kbm.findQuestion("Weekday");
		
		// Weekday = Monday => Day = Workday
		Value monday = kbm.findValue(weekday, "Monday");
		Condition condition = new CondEqual(weekday, monday);
		RuleFactory.createSetValueRule(kbm.createRuleID(), day, workday, condition);

		// Weekday = Tuesday => Day = Workday
		Value tuesday = kbm.findValue(weekday, "Tuesday");
		condition = new CondEqual(weekday, tuesday);
		RuleFactory.createSetValueRule(kbm.createRuleID(), day, workday, condition);
		
		// Weekday = Wednesday => Day = Workday
		Value wednesday = kbm.findValue(weekday, "Wednesday");
		condition = new CondEqual(weekday, wednesday);
		RuleFactory.createSetValueRule(kbm.createRuleID(), day, workday, condition);
		
		// Weekday = Thursday => Day = Workday
		Value thursday = kbm.findValue(weekday, "Thursday");
		condition = new CondEqual(weekday, thursday);
		RuleFactory.createSetValueRule(kbm.createRuleID(), day, workday, condition);
		
		// Weekday = Friday => Day = Workday
		Value friday = kbm.findValue(weekday, "Friday");
		condition = new CondEqual(weekday, friday);
		RuleFactory.createSetValueRule(kbm.createRuleID(), day, workday, condition);
		
		// Weekday = Saturday => Day = Weekend
		Value saturday = kbm.findValue(weekday, "Saturday");
		condition = new CondEqual(weekday, saturday);
		RuleFactory.createSetValueRule(kbm.createRuleID(), day, weekend, condition);
		
		// Weekday = Sunday => Day = Weekend
		Value sunday = kbm.findValue(weekday, "Sunday");
		condition = new CondEqual(weekday, sunday);
		RuleFactory.createSetValueRule(kbm.createRuleID(), day, weekend, condition);
	}
	
	@Test
	public void testTerminlogyObjectExistence() {
		
		// Question 'Weekday'
		Question weekday = kbm.findQuestion("Weekday");
		assertNotNull("Question 'Weekday' isn't in the Knowledgebase.", weekday);
		
		// Values of 'Weekday'
		Value monday = kbm.findValue(weekday, "Monday");
		assertNotNull("Value 'Monday' for Question 'Weekday' isn't in the Knowledgebase", monday);
		Value tuesday = kbm.findValue(weekday, "Tuesday");
		assertNotNull("Value 'Tuesday' for Question 'Weekday' isn't in the Knowledgebase", tuesday);
		Value wednesday = kbm.findValue(weekday, "Wednesday");
		assertNotNull("Value 'Wednesday' for Question 'Weekday' isn't in the Knowledgebase", wednesday);
		Value thursday = kbm.findValue(weekday, "Thursday");
		assertNotNull("Value 'Thursday' for Question 'Weekday' isn't in the Knowledgebase", thursday);
		Value friday = kbm.findValue(weekday, "Friday");
		assertNotNull("Value 'Friday' for Question 'Weekday' isn't in the Knowledgebase", friday);
		Value saturday = kbm.findValue(weekday, "Saturday");
		assertNotNull("Value 'Saturday' for Question 'Weekday' isn't in the Knowledgebase", saturday);
		Value sunday = kbm.findValue(weekday, "Sunday");
		assertNotNull("Value 'Sunday' for Question 'Weekday' isn't in the Knowledgebase", sunday);
		
		// Question 'Day'
		Question day = kbm.findQuestion("Day");
		assertNotNull("Question 'Day' isn't in the Knowledgebase.", day);
		
		// Values of 'Day'
		Value workday = kbm.findValue(day, "Workday");
		assertNotNull("Value 'Workday' for Question 'Day' isn't in the Knowledgebase", workday);
		Value weekend = kbm.findValue(day, "Weekend");
		assertNotNull("Value 'Weekend' for Question 'Day' isn't in the Knowledgebase", weekend);
	}
	
	@Test
	public void testAbstractionProperty() {
		
		// TEST 'Day' <abstract> ?
		Question day = kbm.findQuestion("Day");
		Boolean abstractionProperty = (Boolean) day.getProperties().getProperty(Property.ABSTRACTION_QUESTION);
		assertEquals("Question 'Day' isn't abstract.", Boolean.TRUE, abstractionProperty);
	}

	@Test
	public void testSetAndChangeValue() {
		
		Question weekday = kbm.findQuestion("Weekday");
		Question day = kbm.findQuestion("Day");
		
		// SET 'Weekday' = 'Monday'
		Value monday = kbm.findValue(weekday, "Monday");
		session.setValue(weekday, monday);
		
		// TEST 'Weekday' == 'Monday'
		Value weekdayValue = session.getValue(weekday);
		assertEquals("Question 'Weekday' has wrong value", monday, weekdayValue);
		
		// TEST 'Day' == 'Workday'
		Value workday = kbm.findValue(day, "Workday");
		Value dayValue = session.getValue(day);
		assertEquals("Abstract question 'Day' has wrong value", workday, dayValue);
		
		// SET 'Weekday' = 'Saturday'
		Value saturday = kbm.findValue(weekday, "Saturday");
		session.setValue(weekday, saturday);
		
		// TEST 'Weekday' == 'Saturday'
		weekdayValue = session.getValue(weekday);
		assertEquals("Question 'Weekday' has wrong value", saturday, weekdayValue);
		
		// TEST 'Day' == 'Weekend'
		Value weekend = kbm.findValue(day, "Weekend");
		dayValue = session.getValue(day);
		assertEquals("Abstract question 'Day' has wrong value", weekend, dayValue);
	}
	
	@Test
	public void testSetUndefinedValue() {

		Question weekday = kbm.findQuestion("Weekday");
		Question day = kbm.findQuestion("Day");

		// SET 'Weekday' = 'UNDEFINED'
		session.setValue(weekday, UndefinedValue.getInstance());
		
		// TEST 'Weekday' == 'UNDEFINED' 
		Value weekdayValue = session.getValue(weekday);
		assertEquals("Question 'Weekday' has wrong value", UndefinedValue.getInstance(), weekdayValue);
		
		// TEST 'Day' == 'UNDEFINED'
		Value dayValue = session.getValue(day);
		assertEquals("Abstract question 'Day' has wrong value", UndefinedValue.getInstance(), dayValue);
	}

}