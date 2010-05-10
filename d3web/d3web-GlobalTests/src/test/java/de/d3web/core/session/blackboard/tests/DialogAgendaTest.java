package de.d3web.core.session.blackboard.tests;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.Value;
import de.d3web.core.session.interviewmanager.InterviewAgenda;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.plugin.test.InitPluginManager;

public class DialogAgendaTest {

	KnowledgeBaseManagement kbm;
	Session session;
	InterviewAgenda agenda;
	
	QContainer pregnancyQuestions, heightWeightQuestions;
	QuestionOC sex, pregnant, ask_for_pregnancy, initQuestion;
	QuestionNum weight, height;
	ChoiceValue female, dont_ask; 

	
	@Before
	public void setUp() throws Exception {
		InitPluginManager.init();
		kbm = KnowledgeBaseManagement.createInstance();
		
		QASet root = kbm.getKnowledgeBase().getRootQASet();

		// root {container}
		// - pregnancyQuestions {container} 
		//   - sex [oc]
		//   -- pregnant [oc]
		//   - ask_for_pregnancy [oc]
		//
		// - heightWeightQuestions {container}
		//   - weight [num]
		//   - height [num]
		
		// Container: pregnancyQuestions = { sex {pregnant}, ask_for_pregnancy } 
		pregnancyQuestions = kbm.createQContainer("pregnancyQuestions", root);
		sex = kbm.createQuestionOC("sex", pregnancyQuestions, new String[] {"male", "female"});
		female = new ChoiceValue(kbm.findChoice(sex, "female"));
		pregnant = kbm.createQuestionOC("pregnant", sex, new String[] {"yes", "no"});
		ask_for_pregnancy = kbm.createQuestionOC("ask for pregnancy", pregnancyQuestions, 
				new String[] {"yes", "no"});
		
		// Container: heightWeightQuestions = { weight, height } 
		heightWeightQuestions = kbm.createQContainer("heightWeightQuestions", root);
		weight = kbm.createQuestionNum("weight", "weight", heightWeightQuestions);
		height = kbm.createQuestionNum("height", "height", heightWeightQuestions);
		
		initQuestion = kbm.createQuestionOC("initQuestion", root, 
				new String[] {"all","pregnacyQuestions","height+weight"});
		session = SessionFactory.createSession(kbm.getKnowledgeBase());
		agenda = new InterviewAgenda(this.kbm.getKnowledgeBase());
	}

	@Test
	public void testEmptynessOfAgenda() {
		// initially the agenda is empty
		assertTrue(agenda.isEmpty());

		// put the questionnaire 'pregnancyQuestions' onto the agenda 
		agenda.append(pregnancyQuestions);
		assertFalse(agenda.isEmpty());
		
		// deactivate the questionnaire 'pregnancyQuestions', so agenda should be "empty" again
		agenda.deactivate(pregnancyQuestions);
		assertTrue(agenda.isEmpty());
		
		// put the questionnaire 'heightWeightQuestions' onto the agenda: thus, agenda is not empty 
		agenda.append(heightWeightQuestions);
		assertFalse(agenda.isEmpty());
	}
	
	@Test
	public void testAgendaSortingForQContainers() {
		// initially the agenda is empty
		assertTrue(agenda.isEmpty());

		// ADD: qcontainers in 'wrong' order
		agenda.append(heightWeightQuestions);
		agenda.append(pregnancyQuestions);
		// EXPECT 1: both are on the agenda 
		assertTrue(agenda.onAgenda(heightWeightQuestions));
		assertTrue(agenda.onAgenda(pregnancyQuestions));
		// EXPECT 2: the order as in the tree: [pregnancyQuestions,heightWeightQuestions]
		int posPreg = agenda.getCurrentlyActiveObjects().indexOf(pregnancyQuestions);
		int posHeig = agenda.getCurrentlyActiveObjects().indexOf(heightWeightQuestions);
		assertTrue(posPreg < posHeig);
	}
	
	@Test
	public void testAgendaSortingForQuestions() {
		// initially the agenda is empty
		assertTrue(agenda.isEmpty());

		// ADD: questions in arbitrary order
		agenda.append(height);
		agenda.append(sex);
		agenda.append(weight);
		agenda.append(pregnant);
		agenda.append(ask_for_pregnancy);
		
		// EXPECT 1: all are on the agenda 
		assertTrue(agenda.onAgenda(height));
		assertTrue(agenda.onAgenda(sex));
		assertTrue(agenda.onAgenda(pregnant));
		assertTrue(agenda.onAgenda(weight));
		assertTrue(agenda.onAgenda(ask_for_pregnancy));
		// EXPECT 2: the order as in the tree: [sex, pregnant, ask_for_pregnancy, weight, height]
		List expectedAgenda = Arrays.asList(sex, pregnant, ask_for_pregnancy, weight, height);
		assertEquals(expectedAgenda, agenda.getCurrentlyActiveObjects());
	}
	

//	@Test
//	public void testDeactivationOfQuestions() {
//		// initially the agenda is empty
//		assertTrue(agenda.isEmpty());
//		// put two questions onto the agenda, both should be ACTIVE
//		agenda.append(sex);
//		agenda.append(height);
//		assertFalse(agenda.isEmpty());
//		assertTrue(agenda.hasState(sex, InterviewState.ACTIVE));
//		assertTrue(agenda.hasState(height, InterviewState.ACTIVE));
//		
//		// SET:    sex = female
//		// EXPECT: sex is INACTVE on the agenda
//		setValue(sex, female);
//		assertTrue(agenda.hasState(sex, InterviewState.INACTIVE));
//		
//		// SET:    sex = undefined
//		// EXPECT: sex is active on the agenda again
//		setValue(sex, UndefinedValue.getInstance());
//		assertTrue(agenda.hasState(sex, InterviewState.ACTIVE));
//	}

	private void setValue(Question question, Value value) {
		session.setValue(question, value);
	}
	
}