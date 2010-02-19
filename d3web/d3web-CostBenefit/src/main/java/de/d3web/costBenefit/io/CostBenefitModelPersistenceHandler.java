package de.d3web.costBenefit.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.d3web.core.KnowledgeBase;
import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.condition.AbstractCondition;
import de.d3web.core.io.KnowledgeReader;
import de.d3web.core.io.KnowledgeWriter;
import de.d3web.core.io.PersistenceManager;
import de.d3web.core.io.progress.ProgressListener;
import de.d3web.core.io.utilities.KnowledgeSliceComparator;
import de.d3web.core.io.utilities.Util;
import de.d3web.core.io.utilities.XMLUtil;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.core.terminology.Answer;
import de.d3web.core.terminology.QContainer;
import de.d3web.core.terminology.Question;
import de.d3web.costBenefit.ConditionalValueSetter;
import de.d3web.costBenefit.CostBenefit;
import de.d3web.costBenefit.StateTransition;
import de.d3web.costBenefit.ValueTransition;
import de.d3web.costBenefit.inference.PSMethodCostBenefit;

/**
 * This PersistenceHandler saves and stores the default KnowledgeSlices of the
 * CostBenefitPackage
 * 
 * @author Markus Friedrich (denkbar GmbH)
 * 
 */
public class CostBenefitModelPersistenceHandler implements KnowledgeReader, KnowledgeWriter {

	public static String ID = "costbenefit";
	
	@Override
	public void read(KnowledgeBase kb, InputStream stream, ProgressListener listener) throws IOException {
		Document doc = Util.streamToDocument(stream);
		String message = "Loading cost benefit knowledge";
		listener.updateProgress(0, message);
		KnowledgeBaseManagement kbm = KnowledgeBaseManagement
				.createInstance(kb);
		NodeList cbmodels = doc.getElementsByTagName("CostBenefit");
		NodeList stmodels = doc.getElementsByTagName("StateTransition");
		int max = cbmodels.getLength()+stmodels.getLength();
		float count = 0;
		for (int i = 0; i < cbmodels.getLength(); i++) {
			Node current = cbmodels.item(i);
			addCBKnowledge(kbm, current);
			listener.updateProgress(++count/max, message);
		}
		for (int i = 0; i < stmodels.getLength(); i++) {
			Node current = stmodels.item(i);
			addSTKnowledge(kbm, current);
			listener.updateProgress(++count/max, message);
		}
	}

	@Override
	public int getEstimatedSize(de.d3web.core.KnowledgeBase kb) {
		Collection<KnowledgeSlice> relations = kb
				.getAllKnowledgeSlicesFor(PSMethodCostBenefit.class);
		int counter = 0;
		for (KnowledgeSlice ks : relations) {
			if (ks instanceof CostBenefit || ks instanceof StateTransition) {
				counter++;
			}
		}
		return counter;
	}

	@Override
	public void write(de.d3web.core.KnowledgeBase kb, OutputStream stream, de.d3web.core.io.progress.ProgressListener listener) throws IOException {
		Document doc = Util.createEmptyDocument();
		Element root = doc.createElement("KnowledgeBase");
		root.setAttribute("type", ID);
		root.setAttribute("system", "d3web");
		doc.appendChild(root);
		Element ksNode = doc.createElement("KnowledgeSlices");
		root.appendChild(ksNode);
		SortedSet<KnowledgeSlice> knowledgeSlices = new TreeSet<KnowledgeSlice>(new KnowledgeSliceComparator());
		for (KnowledgeSlice knowledgeSlice: kb.getAllKnowledgeSlices()) {
			if (knowledgeSlice != null) {
				knowledgeSlices.add(knowledgeSlice);
			}
		}
		for (KnowledgeSlice model : knowledgeSlices) {
			if (model instanceof CostBenefit) {
				ksNode.appendChild(getElement((CostBenefit) model, doc));
			} else if (model instanceof StateTransition){
				ksNode.appendChild(getElement((StateTransition) model, doc));
			} 
		}
		
		Util.writeDocumentToOutputStream(doc, stream);
	}
	
	private Element getElement(StateTransition st, Document doc) throws IOException {
		Element element = doc.createElement("StateTransition");
		element.setAttribute("ID", st.getId());
		element.setAttribute("QID", st.getQcontainer().getId());
		AbstractCondition activationCondition = st.getActivationCondition();
		if (activationCondition!=null) {
			Element aCElement = doc.createElement("activationCondition");
			aCElement.appendChild(PersistenceManager.getInstance().writeFragment(activationCondition, doc));
			element.appendChild(aCElement);
		}
		List<ValueTransition> postTransitions = st.getPostTransitions();
		for (ValueTransition vt: postTransitions) {
			element.appendChild(getElement(vt, doc));
			
		}
		return element;
	}

	private Element getElement(ValueTransition vt, Document doc) throws IOException {
		Element element = doc.createElement("ValueTransition");
		element.setAttribute("QID", vt.getQuestion().getId());
		List<ConditionalValueSetter> setters = vt.getSetters();
		for (ConditionalValueSetter cvs: setters) {
			element.appendChild(getElement(cvs, doc));
		}
		return element;
	}

	private Element getElement(ConditionalValueSetter cvs, Document doc) throws IOException {
		Element element = doc.createElement("ConditionalValueSetter");
		element.setAttribute("AID", cvs.getAnswer().getId());
		AbstractCondition condition = cvs.getCondition();
		if (condition!=null) {
			element.appendChild(PersistenceManager.getInstance().writeFragment(condition, doc));
		}
		return element;
	}

	private Element getElement(CostBenefit cb, Document doc) {
		Element element = doc.createElement("CostBenefit");
		element.setAttribute("costs", ""+cb.getCosts());
		element.setAttribute("maloperationProbability", ""+cb.getMaloperationProbability());
		element.setAttribute("taskType", cb.getTaskType());
		element.setAttribute("ID", cb.getId());
		element.setAttribute("QID", cb.getQcontainer().getId());
		return element;
	}

	private void addSTKnowledge(KnowledgeBaseManagement kbm, Node current) throws IOException {
		String qcontainerID = current.getAttributes().getNamedItem("QID").getTextContent();
		QContainer qcontainer = kbm.findQContainer(qcontainerID);
		NodeList children =  current.getChildNodes();
		AbstractCondition activationCondition = null;
		List<ValueTransition> postTransitions = new ArrayList<ValueTransition>();
		for (int i=0; i<children.getLength(); i++) {
			Node n = children.item(i);
			if (n.getNodeName().equals("activationCondition")) {
				for (Element child: XMLUtil.getElementList(n.getChildNodes())) {
					activationCondition = (AbstractCondition) PersistenceManager.getInstance().readFragment(child, kbm.getKnowledgeBase());
				}
			} else if (n.getNodeName().equals("ValueTransition")){
				String question = n.getAttributes().getNamedItem("QID").getTextContent();
				Question q = kbm.findQuestion(question);
				List<ConditionalValueSetter> cvss = new ArrayList<ConditionalValueSetter>();
				NodeList childNodes = n.getChildNodes();
				for (int j=0; j<childNodes.getLength(); j++) {
					Node child = childNodes.item(j);
					if (child.getNodeName().equals("ConditionalValueSetter")) {
						Answer answer = kbm.findAnswer(q, child.getAttributes().getNamedItem("AID").getTextContent());
						AbstractCondition condition = null;
						for (Element grandchild: XMLUtil.getElementList(child.getChildNodes())) {
							condition = (AbstractCondition) PersistenceManager.getInstance().readFragment(grandchild, kbm.getKnowledgeBase());
						}
						ConditionalValueSetter cvs = new ConditionalValueSetter(
								answer, condition);
						cvss.add(cvs);
					}
				}
				ValueTransition vt = new ValueTransition(q, cvss);
				postTransitions.add(vt);
			}
		}
		StateTransition st = new StateTransition(activationCondition, postTransitions, qcontainer);
		qcontainer.addKnowledge(st.getProblemsolverContext(), st, StateTransition.STATE_TRANSITION);
	}

	private void addCBKnowledge(KnowledgeBaseManagement kbm, Node current) {
		String qcontainerID = getAttribute("QID", current);
//		String ID = getAttribute("ID", current);
		String malOpString = getAttribute("maloperationProbability", current);
		String taskType = getAttribute("taskType", current);
		String costsString = getAttribute("costs", current);
		QContainer qcontainer = kbm.findQContainer(qcontainerID);
		Float maloperationProbability = Float.parseFloat(malOpString);
		int costs = Integer.parseInt(costsString);
		CostBenefit cb = new CostBenefit(costs, maloperationProbability, qcontainer, taskType);
		qcontainer.addKnowledge(cb.getProblemsolverContext(), cb, CostBenefit.COST_BENEFIT);
	}

	private String getAttribute(String string, Node current) {
		return current.getAttributes().getNamedItem(string).getTextContent();
	}

}
