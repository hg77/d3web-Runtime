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

package de.d3web.persistence.tests;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.io.KnowledgeBasePersistence;
import de.d3web.core.io.Persistence;
import de.d3web.core.io.PersistenceManager;
import de.d3web.core.io.fragments.QuestionHandler;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.persistence.tests.utils.XMLTag;
import com.denkbares.plugin.test.InitPluginManager;

import static org.junit.Assert.assertEquals;

/**
 * @author merz
 */
public class QuestionChoiceTest {

	private QuestionOC q1;
	private QuestionHandler qw;
	private XMLTag answersTag;

	private XMLTag isTag;
	private XMLTag shouldTag;

	private Persistence<KnowledgeBase> persistence;

	@Before
	public void setUp() throws IOException {
		InitPluginManager.init();
		KnowledgeBase kb = new KnowledgeBase();
		persistence = new KnowledgeBasePersistence(PersistenceManager.getInstance(), kb);
		q1 = new QuestionOC(kb, "q1");

		qw = new QuestionHandler();

		shouldTag = new XMLTag("Question");
		shouldTag.addAttribute("name", "q1");
		shouldTag.addAttribute("type", "OC");
	}

	@Test
	public void testQuestionOCSimple() throws Exception {
		answersTag = new XMLTag("Answers");
		shouldTag.addChild(answersTag);
		isTag = new XMLTag(qw.write(q1, persistence));
		assertEquals("(0)", shouldTag, isTag);
	}

	@Test
	public void testQuestionOCWithAnswers() throws Exception {
		answersTag = new XMLTag("Answers");
		shouldTag.addChild(answersTag);

		List<Choice> alternatives = new LinkedList<>();

		Choice a1 = new Choice("q1a1");
		alternatives.add(a1);

		Choice a2 = new Choice("q1a2");
		alternatives.add(a2);

		q1.setAlternatives(alternatives);

		XMLTag answerTag1 = new XMLTag("Answer");
		answerTag1.addAttribute("name", "q1a1");
		answerTag1.addAttribute("type", "AnswerChoice");
		answersTag.addChild(answerTag1);

		XMLTag answerTag2 = new XMLTag("Answer");
		answerTag2.addAttribute("name", "q1a2");
		answerTag2.addAttribute("type", "AnswerChoice");
		answersTag.addChild(answerTag2);

		isTag = new XMLTag(qw.write(q1, persistence));

		assertEquals("(1)", shouldTag, isTag);

		// replace special chars with XML entities:
		// answerText = answerText.replaceAll("&", "&amp;");
		// answerText = answerText.replaceAll("<", "&lt;");
		// answerText = answerText.replaceAll(">", "&gt;");
	}

	@Test
	public void testQuestionWithProperties() throws Exception {
		q1.getInfoStore().addValue(BasicProperties.COST, 20d);

		// Set propertyKeys = q1.getPropertyKeys();
		// MockPropertyDescriptor mpd = new
		// MockPropertyDescriptor(q1,propertyKeys);

		XMLTag propertiesTag = new XMLTag("infoStore");

		XMLTag propertyTag2 = new XMLTag("entry");
		propertyTag2.addAttribute("property", "cost");
		propertyTag2.setContent("20.0");

		propertiesTag.addChild(propertyTag2);

		shouldTag.addChild(propertiesTag);

		answersTag = new XMLTag("Answers");
		shouldTag.addChild(answersTag);

		isTag = new XMLTag(qw.write(q1, persistence));

		assertEquals("(3)", shouldTag, isTag);
	}
}
