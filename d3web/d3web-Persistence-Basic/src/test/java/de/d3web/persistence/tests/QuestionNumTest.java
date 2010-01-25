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

package de.d3web.persistence.tests;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import de.d3web.core.kpers.fragments.NumericalIntervalHandler;
import de.d3web.core.kpers.fragments.QuestionHandler;
import de.d3web.core.kpers.utilities.Util;
import de.d3web.kernel.domainModel.NumericalInterval;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.domainModel.qasets.QuestionNum;
import de.d3web.kernel.supportknowledge.Property;
import de.d3web.persistence.tests.utils.XMLTag;
import de.d3web.plugin.test.InitPluginManager;

/**
 * @author merz
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class QuestionNumTest extends TestCase {
	
	private Question q1;
	private QuestionHandler qw;
	private XMLTag isTag;
	private XMLTag shouldTag;
	

	/**
	 * Constructor for QuestionNumOutput.
	 * @param arg0
	 */
	public QuestionNumTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(QuestionNumTest.suite());
	}

	public static Test suite() {
		return new TestSuite(QuestionNumTest.class);
	}

	protected void setUp() {
		try {
			InitPluginManager.init();
		}
		catch (IOException e1) {
			assertTrue("Error initialising plugin framework", false);
		}
		q1 = new QuestionNum("q1");
		q1.setText("q1-text");
		 
		qw = new QuestionHandler();

		shouldTag = new XMLTag("Question");
		shouldTag.addAttribute("ID", "q1");
		shouldTag.addAttribute("type", "Num");
		XMLTag child = new XMLTag("Text");
		child.setContent("q1-text");
		shouldTag.addChild(child);
	}
	
	public void testQuestionWithProperties() throws Exception {
		q1.getProperties().setProperty(Property.HIDE_IN_DIALOG, new Boolean(true));
		q1.getProperties().setProperty(Property.COST, new Double(20));
		
		// Set propertyKeys = q1.getPropertyKeys();
		// MockPropertyDescriptor mpd = new MockPropertyDescriptor(q1,propertyKeys);
		
		XMLTag propertiesTag = new XMLTag("Properties");
		
		XMLTag propertyTag1 = new XMLTag("Property");
		propertyTag1.addAttribute("name", "hide_in_dialog");
		// old: propertyTag1.addAttribute("descriptor", "hide_in_dialog");
		propertyTag1.addAttribute("class", "java.lang.Boolean");
		propertyTag1.setContent("true");
		
		XMLTag propertyTag2 = new XMLTag("Property");
		propertyTag2.addAttribute("name", "cost");
		// old: propertyTag2.addAttribute("descriptor", "cost");
		propertyTag2.addAttribute("class", "java.lang.Double");
		propertyTag2.setContent("20.0");
		
		propertiesTag.addChild(propertyTag1);
		propertiesTag.addChild(propertyTag2);
		
		shouldTag.addChild(propertiesTag);
		
		isTag = new XMLTag(new QuestionHandler().write(q1, Util.createEmptyDocument()));		
					
		assertEquals("(2)", shouldTag, isTag);
	}



	public void testQuestionNumTestSimple() throws Exception{
		isTag = new XMLTag(qw.write(q1, Util.createEmptyDocument()));

		assertEquals("(0)", shouldTag, isTag);
	}
	
	public void testQuestionWithIntervals() throws Exception {
		
		List<NumericalInterval> intervals = new LinkedList<NumericalInterval>();
		intervals.add(new NumericalInterval(Double.NEGATIVE_INFINITY, 30, true, false));
		intervals.add(new NumericalInterval(30, 300.03, true, true));
		intervals.add(new NumericalInterval(300.03, Double.POSITIVE_INFINITY, false, true));
		((QuestionNum) q1).setValuePartitions(intervals);

		XMLTag intervalsTag = new XMLTag(NumericalIntervalHandler.GROUPTAG);
		
		XMLTag intervalTag1 = new XMLTag(NumericalIntervalHandler.TAG);
		intervalTag1.addAttribute("lower", "-INFINITY");
		intervalTag1.addAttribute("upper", "30.0");
		intervalTag1.addAttribute("type", "LeftOpenRightClosedInterval");

		XMLTag intervalTag2 = new XMLTag(NumericalIntervalHandler.TAG);
		intervalTag2.addAttribute("lower", "30.0");
		intervalTag2.addAttribute("upper", "300.03");
		intervalTag2.addAttribute("type", "LeftOpenRightOpenInterval");

		XMLTag intervalTag3 = new XMLTag(NumericalIntervalHandler.TAG);
		intervalTag3.addAttribute("lower", "300.03");
		intervalTag3.addAttribute("upper", "+INFINITY");
		intervalTag3.addAttribute("type", "LeftClosedRightOpenInterval");
		
		intervalsTag.addChild(intervalTag1);
		intervalsTag.addChild(intervalTag2);
		intervalsTag.addChild(intervalTag3);
		
		shouldTag.addChild(intervalsTag);
		
		isTag = new XMLTag(qw.write(q1, Util.createEmptyDocument()));	
		assertEquals("(intervals)", shouldTag, isTag);
	}
	
	public void testQContainerWithCosts() throws Exception{
		q1.getProperties().setProperty(Property.TIME, new Double(20));
		q1.getProperties().setProperty(Property.RISK, new Double(50.5));
		
		XMLTag shouldCostsTag = new XMLTag("Properties");
		
		XMLTag costTag1 = new XMLTag("Property");
		costTag1.addAttribute("name", "timeexpenditure");
		costTag1.addAttribute("class", Double.class.getName());
		costTag1.setContent(Double.toString(20));
		shouldCostsTag.addChild(costTag1);
		
		XMLTag costTag2 = new XMLTag("Property");
		costTag2.addAttribute("name", "risk");
		costTag2.addAttribute("class", Double.class.getName());
		costTag2.setContent(Double.toString(50.5));
		shouldCostsTag.addChild(costTag2);
		
		shouldTag.addChild(shouldCostsTag);
		
		isTag = new XMLTag(qw.write(q1, Util.createEmptyDocument()));

		assertEquals("(3)", shouldTag, isTag);
	}

}