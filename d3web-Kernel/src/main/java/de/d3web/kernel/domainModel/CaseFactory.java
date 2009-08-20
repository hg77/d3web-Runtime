package de.d3web.kernel.domainModel;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.dialogControl.DefaultQASetManagerFactory;
import de.d3web.kernel.dialogControl.MQDialogController;
import de.d3web.kernel.dialogControl.OQDialogController;
import de.d3web.kernel.dialogControl.QASetManager;
import de.d3web.kernel.dialogControl.QASetManagerFactory;
import de.d3web.kernel.dialogControl.proxy.DialogProxy;
import de.d3web.kernel.domainModel.qasets.QContainer;
import de.d3web.kernel.domainModel.qasets.Question;
import de.d3web.kernel.psMethods.PSMethod;
import de.d3web.kernel.psMethods.userSelected.PSMethodUserSelected;

/**
 * Factory for XPSCase objects
 * @author Norman Brümmer, Georg
 */
public class CaseFactory {

	private static class QASetManagerFactoryAdapter
		implements QASetManagerFactory {
		private Class qaSetManagerClass = null;
		public QASetManagerFactoryAdapter(Class qaSetManagerClass) {
			super();
			if (!QASetManager.class.isAssignableFrom(qaSetManagerClass)) {
				throw new ClassCastException();
			}
			this.qaSetManagerClass = qaSetManagerClass;
		}
		/* (non-Javadoc)
		 * @see de.d3web.kernel.dialogControl.QASetManagerFactory#createQASetManager(de.d3web.kernel.XPSCase)
		 */
		public QASetManager createQASetManager(XPSCase theCase) {
			try {
				Constructor constructor =
					qaSetManagerClass.getConstructor(new Class[] { XPSCase.class });
				return (QASetManager) constructor.newInstance(
					new Object[] { theCase });
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

/**
 *	Factory-method that produces instances of XPSCase with default QASetManagerFactory
 *	@param kb Knowledgebase used in the case.
 *  @return new XPSCase-object with KnowledgeBase kb
 */
public static synchronized XPSCase createXPSCase(KnowledgeBase kb) {
	return new D3WebCase(kb, new DefaultQASetManagerFactory());
}

/**
 *	Factory-method that produces instances of XPSCase
 *	@param kb Knowledgebase used in the case.
 *  @return new XPSCase-object with KnowledgeBase kb
 */
public static synchronized XPSCase createXPSCase(
	KnowledgeBase kb,
	QASetManagerFactory factory) {
	return new D3WebCase(kb, factory);
}

/**
 *	Factory-method that produces instances of XPSCase
 *	@param kb Knowledgebase used in the case.
 *  @return new XPSCase-object with KnowledgeBase kb
 */
public static synchronized XPSCase createXPSCase(
	KnowledgeBase kb,
	Class qaManagerClass) {
	return new D3WebCase(kb, new QASetManagerFactoryAdapter(qaManagerClass));
}

	private static void addUsedPSMethods(XPSCase newCase, List psMethods) {
		Iterator iter = psMethods.iterator();
		while (iter.hasNext()) {
			PSMethod psm = (PSMethod) iter.next();
			((D3WebCase) newCase).addUsedPSMethod(psm);
			psm.init(newCase);
		}
	}

	/**
	 * Factory-method that returns an instance of XPSCase. All normally indicated questions are answered
	 * with the values of "proxy" (if any). The questions are answered in the order that is
	 * defined by the MQDialogController. 
	 * @param kb Knowledgebase used in the case.
	 * @param dialogControllerType used in the case
	 * @param proxy DialogProxy that contains values of questions
	 * @return XPSCase
	 */
	public static XPSCase createAnsweredXPSCase(
		KnowledgeBase kb,
		Class dialogControllerType,
		DialogProxy proxy,
		List usedPSMethods) {
		return createAnsweredXPSCase(kb, dialogControllerType, proxy, null, usedPSMethods);
	}

	/**
	 * Factory-method that returns an instance of XPSCase. All questions that are indicated normally
	 * or that are children of registered containers are answered with the values of "proxy" (if any).
	 * The questions are answered in the order that is defined by the MQDialogController. 
	 * @param kb Knowledgebase used in the case.
	 * @param dialogControllerType used in the case (MQ- or OQDialogController)
	 * @param proxy DialogProxy that contains values of questions
	 * @param registeredQContainers Collection that contains all containers that have to be asked in any
	 * 			circumstance (esp. user selected containers); 
	 * 			(see de.d3web.caserepository.CaseObject#getAllQContainers())
	 * @return XPSCase
	 */
	public static XPSCase createAnsweredXPSCase(
		KnowledgeBase kb,
		Class dialogControllerType,
		DialogProxy proxy,
		Collection registeredQContainers,
		List usedPSMethods) {

		XPSCase newCase = createXPSCase(kb, dialogControllerType);
		addUsedPSMethods(newCase, usedPSMethods);
		
		MQDialogController mqdc = null;

		if (dialogControllerType.equals(OQDialogController.class)) {
			OQDialogController oqdc = (OQDialogController) newCase.getQASetManager();
			mqdc = oqdc.getMQDialogController();

		} else if (dialogControllerType.equals(MQDialogController.class)) {
			mqdc = (MQDialogController) newCase.getQASetManager();
		}
		
		if (mqdc != null) {
			answerQuestionsWithMQdc(mqdc, newCase, kb, proxy, registeredQContainers);
		}
		

		return (newCase);
	}
	
	/**
	 * Answers the case with the help of an MQDialogController.
	 * @see #createAnsweredXPSCase
	 */
	private static void answerQuestionsWithMQdc(MQDialogController mqdc,
			XPSCase newCase, 
			KnowledgeBase kb,
			DialogProxy proxy,
			Collection registeredQContainers) {
		
		// first answer all normally indicated questions
		// go through all remaining QASets
		QASet next = mqdc.moveToNextRemainingQASet();
		while (next != null) {
			answerQuestions(next, proxy, newCase, mqdc);
			next = mqdc.moveToNextRemainingQASet();
		}

		// then process the registered containers
		if ((registeredQContainers != null) && (registeredQContainers.size() > 0)) {
			Iterator regIter = registeredQContainers.iterator();
			while (regIter.hasNext()) {
				QASet qaSet = (QASet) regIter.next();
				List pros = qaSet.getProReasons(newCase);
				if ((pros == null) || (pros.size() == 0)) {
					qaSet.addProReason(new QASet.Reason(null, PSMethodUserSelected.class), newCase);
				}
				// all questions in user-selected containers will also be answered
				answerQuestions(qaSet, proxy, newCase, mqdc);
			}
			// now remove the user-selection if possible
			regIter = registeredQContainers.iterator();
			while (regIter.hasNext()) {
				QASet qaSet = (QASet) regIter.next();
				List pros = qaSet.getProReasons(newCase);
				if (((pros != null) && (pros.size() > 1))
					|| ((qaSet instanceof QContainer) && (mqdc.nothingIsDoneInContainer((QContainer) qaSet)))) {
					qaSet.removeProReason(new QASet.Reason(null, PSMethodUserSelected.class), newCase);
				}
			}
			// now, there are maybe some more indicated containers...
			next = mqdc.moveToNextRemainingQASet();
			while (next != null) {
				answerQuestions(next, proxy, newCase, mqdc);
				next = mqdc.moveToNextRemainingQASet();
			}
		}
	}

	/**
	 * Answers all valid questions in the given container with values of the given proxy 
	 * in an order that is defined by the given MQDialogController.
	 * @param qaSet (QASet the container whose question-children are to answer)
	 * @param proxy (DialogProxy contains answers for the questions)
	 * @param inCase (XPSCase in which unanswered questions shall be answered)
	 * @param mqdc (MQDialogController defines the order of the questions to answer)
	 */
	private static void answerQuestions(QASet qaSet, DialogProxy proxy, XPSCase inCase, MQDialogController mqdc) {
		if (qaSet instanceof QContainer) {
			// determine the valid questions of the current container as long as
			// they change (due to a possible activation of follow-questions)
			List formerValidQuestions = new LinkedList();
			List validQuestions = mqdc.getAllValidQuestionsOf((QContainer) qaSet);
			while ((validQuestions.size() > 0) && (!formerValidQuestions.containsAll(validQuestions))) {
				// try to answer all questions of the current container
				Iterator qIter = validQuestions.iterator();
				while (qIter.hasNext()) {
					Question q = (Question) qIter.next();
					Collection answers = proxy.getAnswers(q.getId());
					if (answers != null) {
						inCase.setValue(q, answers.toArray());
					}
				}
				formerValidQuestions = validQuestions;
				validQuestions = mqdc.getAllValidQuestionsOf((QContainer) qaSet);
			}
		} else {
			Question q = (Question) qaSet;
			Collection answers = proxy.getAnswers(q.getId());
			if (answers != null)
				inCase.setValue(q, answers.toArray());
		}
	}
}