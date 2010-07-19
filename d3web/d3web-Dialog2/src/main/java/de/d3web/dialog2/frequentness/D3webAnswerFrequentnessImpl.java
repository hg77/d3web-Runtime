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

package de.d3web.dialog2.frequentness;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import de.d3web.caserepository.CaseObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.ValueFactory;
import de.d3web.dialog2.basics.knowledge.CaseManager;
import de.d3web.dialog2.basics.knowledge.CaseObjectDescriptor;
import de.d3web.dialog2.util.DialogUtils;
import de.d3web.utilities.caseLoaders.CaseRepository;

public class D3webAnswerFrequentnessImpl implements FrequentnessInterface {

	Collection<SelectItem> items;

	List<String> selectedData;

	private List<DataGroup> dataGroupWithFrequentnessData;

	private List<Session> savedCases;

	private int getAbsoluteFreq(QuestionChoice q, String ansID, Session theCase) {
		if (savedCases == null) {
			setUpSavedCases(theCase);
		}
		// absolute frequency of this answer: how often is this answer set in
		// all cases...
		int absoluteFreq = 0;
		for (Session oneCase : savedCases) {
			Value answer = oneCase.getBlackboard().getValue(q);
			if (ValueFactory.getID_or_Value(answer).equals(ansID)) {
				absoluteFreq++;
			}
		}
		return absoluteFreq;
	}

	@Override
	public List<DataGroup> getDataGroupWithFrequentnessData() {
		dataGroupWithFrequentnessData = new ArrayList<DataGroup>();
		// add stuff!!!
		if (selectedData != null) {
			for (String qID : selectedData) {
				Session theCase = DialogUtils.getDialog().getSession();
				Question q = theCase.getKnowledgeBase().searchQuestion(qID);
				if (q != null && q instanceof QuestionChoice) {
					QuestionChoice qCh = (QuestionChoice) q;
					DataGroup group = new DataGroup(qCh.getName());
					List<Choice> answers = qCh.getAllAlternatives();
					for (Choice a : answers) {
						int absoluteFreq = getAbsoluteFreq(qCh, a.getId(),
								theCase);
						double relFreq = getRelFreq(absoluteFreq);
						DataWithFrequentness data = new DataWithFrequentness(a
								.getName(), absoluteFreq, relFreq);
						group.addDataWithFrequentness(data);
					}
					dataGroupWithFrequentnessData.add(group);
				}
			}
		}
		return dataGroupWithFrequentnessData;
	}

	private double getRelFreq(int absoluteFreq) {
		if (absoluteFreq == 0 || savedCases == null || savedCases.size() == 0) {
			return 0.0;
		}
		else {
			return absoluteFreq / (double) savedCases.size();
		}

	}

	public Collection<SelectItem> getSelectData() {
		if (items == null) {
			List<Question> qList = DialogUtils.getDialog().getSession()
					.getKnowledgeBase().getQuestions();
			items = new ArrayList<SelectItem>();
			for (Question q : qList) {
				if (q instanceof QuestionChoice) {
					SelectItem item = new SelectItem();
					item.setLabel(q.getName());
					item.setValue(q.getId());
					items.add(item);
				}
			}
		}
		return items;
	}

	public List<String> getSelectedData() {
		return selectedData;
	}

	@Override
	public boolean isDataAvailable() {
		Session theCase = DialogUtils.getDialog().getSession();
		if (theCase == null) {
			return false;
		}
		Collection<CaseObjectDescriptor> casesForKB = CaseManager.getInstance()
				.getCaseObjectDescriptorsForKb(
				theCase.getKnowledgeBase().getId());
		if (casesForKB.size() > 0) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isDataSelected() {
		if (selectedData != null && selectedData.size() > 0) {
			return true;
		}
		return false;
	}

	public void setSelectedData(List<String> selectedData) {
		this.selectedData = selectedData;
	}

	private void setUpSavedCases(Session theCase) {
		savedCases = new ArrayList<Session>();
		Collection<CaseObjectDescriptor> codForKB = CaseManager.getInstance()
				.getCaseObjectDescriptorsForKb(
				theCase.getKnowledgeBase().getId());
		for (CaseObjectDescriptor cod : codForKB) {
			CaseObject o = CaseRepository.getInstance().getCaseById(
					theCase.getKnowledgeBase().getId(), cod.getCaseId());
			Session newCase = DialogUtils.createNewAnsweredCase(o, theCase
					.getKnowledgeBase());
			savedCases.add(newCase);
		}

	}

}
