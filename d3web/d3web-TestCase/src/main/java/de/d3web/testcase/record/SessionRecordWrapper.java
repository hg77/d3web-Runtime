/*
 * Copyright (C) 2012 denkbares GmbH
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
package de.d3web.testcase.record;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.records.SessionRecord;
import de.d3web.core.session.Value;
import de.d3web.core.session.protocol.FactProtocolEntry;
import de.d3web.core.session.protocol.Protocol;
import de.d3web.core.session.protocol.ProtocolEntry;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.indication.inference.PSMethodUserSelected;
import de.d3web.testcase.model.Check;
import de.d3web.testcase.model.DefaultFinding;
import de.d3web.testcase.model.Finding;
import de.d3web.testcase.model.TestCase;

/**
 * Wraps a {@link Protocol} to provide a {@link TestCase}
 * 
 * @author Markus Friedrich (denkbares GmbH)
 * @created 26.01.2012
 */
public class SessionRecordWrapper implements TestCase {

	private final KnowledgeBase kb;
	private final SessionRecord record;

	public SessionRecordWrapper(SessionRecord record, KnowledgeBase kb) {
		this.record = record;
		this.kb = kb;
	}

	@Override
	public Collection<Date> chronology() {
		Set<Date> dates = new TreeSet<Date>();
		for (ProtocolEntry entry : record.getProtocol().getProtocolHistory()) {
			dates.add(entry.getDate());
		}
		return Collections.unmodifiableCollection(dates);
	}

	@Override
	public Collection<Finding> getFindings(Date date) {
		List<Finding> findings = new LinkedList<Finding>();
		for (ProtocolEntry entry : record.getProtocol().getProtocolHistory()) {
			if (entry instanceof FactProtocolEntry && entry.getDate().equals(date)) {
				FactProtocolEntry fpe = (FactProtocolEntry) entry;
				if (fpe.getSolvingMethodClassName().equals(PSMethodUserSelected.class.getName())) {
					TerminologyObject object = kb.getManager().search(
							fpe.getTerminologyObjectName());
					Value value = fpe.getValue();
					findings.add(new DefaultFinding(object, value, entry.getDate()));
				}
			}
		}
		return findings;
	}

	@Override
	public Finding getFinding(Date date, TerminologyObject object) {
		for (ProtocolEntry entry : record.getProtocol().getProtocolHistory()) {
			if (entry instanceof FactProtocolEntry && entry.getDate().equals(date)) {
				FactProtocolEntry fpe = (FactProtocolEntry) entry;
				if (fpe.getSolvingMethodClassName().equals(PSMethodUserSelected.class.getName())) {
					if (object == kb.getManager().search(fpe.getTerminologyObjectName())) {
						return new DefaultFinding(object, fpe.getValue(), entry.getDate());
					}
				}
			}
		}
		return null;
	}

	@Override
	public Collection<Check> getChecks(Date date) {
		return Collections.emptyList();
	}

	@Override
	public Date getStartDate() {
		return record.getCreationDate();
	}

	public Collection<String> check() {
		Collection<String> errors = new LinkedList<String>();
		for (ProtocolEntry entry : record.getProtocol().getProtocolHistory()) {
			if (entry instanceof FactProtocolEntry) {
				FactProtocolEntry fpe = (FactProtocolEntry) entry;
				if (fpe.getSolvingMethodClassName().equals(PSMethodUserSelected.class.getName())) {
					TerminologyObject object = kb.getManager().search(
							fpe.getTerminologyObjectName());
					if (object == null) {
						errors.add("TerminologyObject " + fpe.getTerminologyObjectName()
								+ " is not contained in the KB.");
					}
					else if (object instanceof QuestionChoice
							&& fpe.getValue() instanceof ChoiceValue) {
						ChoiceValue cv = (ChoiceValue) fpe.getValue();
						if (cv.getChoice((QuestionChoice) object) == null) {
							errors.add("The question " + object.getName() + " has no choice "
									+ cv.getAnswerChoiceID());
						}
					}
					else if (object instanceof QuestionMC
							&& fpe.getValue() instanceof MultipleChoiceValue) {
						MultipleChoiceValue mcv = (MultipleChoiceValue) fpe.getValue();
						if (mcv.asChoiceList((QuestionChoice) object).contains(null)) {
							errors.add("The question " + object.getName()
									+ " does not contain all choices of " + mcv.toString());
						}
					}
				}
			}
		}
		return errors;
	}
}