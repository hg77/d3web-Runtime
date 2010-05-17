/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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
package de.d3web.core.session.interviewmanager;

import java.util.ArrayList;
import java.util.List;

import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;

/**
 * This class always creates a new {@link Form} that contains the
 * {@link QContainer}, that should be presented/asked next in the dialog system.
 * This {@link QContainer} is not a non-terminal QContainer (i.e., it does not
 * contain other {@link QContainer} instances) and it contains at least one
 * unansered but active question.
 * 
 * For Dialog implementations: Please use helper methods in {@link Interview} to
 * find out, whether a {@link Question} or follow-up {@link Question} is active
 * or not.
 * 
 * @author joba
 * 
 */
public class CurrentQContainerFormStrategy extends AbstractFormStrategy {

	@Override
	public Form nextForm(List<InterviewObject> agendaEnties, Session session) {
		if (agendaEnties.isEmpty()) {
			return EmptyForm.getInstance();
		} else {
			InterviewObject object = agendaEnties.get(0);
			if (object instanceof Question) {
				return new DefaultForm(((Question) object).getName(), object);
			} else if (object instanceof QContainer) {
				QContainer nextQContainer = retrieveNextUnfinishedQContainer(
						(QContainer) object, session);
				if (nextQContainer == null) {
					return EmptyForm.getInstance();
				}
				return new DefaultForm(nextQContainer.getName(), nextQContainer);
			}
			return null;
		}
	}

	/**
	 * Staring with the specified {@link QContainer}, recursively find the next
	 * {@link QContainer} instance, that only contains {@link Question}
	 * instances (i.e., a terminal {@link QContainer}) and that contains active
	 * questions with respect to the specified {@link Session} instance.
	 * 
	 * @param container
	 *            the specified {@link QContainer}
	 * @param session
	 *            the specified session
	 * @return an active {@link QContainer} instance
	 */
	private QContainer retrieveNextUnfinishedQContainer(QContainer container,
			Session session) {
		if (isTerminalQContainer(container)
				&& hasActiveQuestions(container, session)) {
			return (QContainer) container;
		}
		// container is not a terminal qcontainer, i.e., contains further
		// qcontainers
		// => traverse to the next active child in a DFS style
		else {
			TerminologyObject[] children = container.getChildren();
			for (TerminologyObject child : children) {
				if (child instanceof QContainer) {
					QContainer candidate = retrieveNextUnfinishedQContainer(
							(QContainer) child, session);
					if (candidate != null) {
						return candidate;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Checks, whether the specified container contains active (follow-up)
	 * questions or direct child questions, that have no value with respect to
	 * the specified {@link Session} instance.
	 * 
	 * @param container
	 *            the specified {@link QContainer} instance
	 * @param session
	 *            the specified {@link Session} instance
	 * @return true, when it contains an unanswered direct question or an active
	 *         (possible follow-up) question
	 */
	private boolean hasActiveQuestions(QContainer container, Session session) {
		for (TerminologyObject child : container.getChildren()) {
			if (child instanceof Question) {
				Question question = (Question) child;
				if (hasValueUndefined(question, session)) {
					return true;
				} else {
					List<Question> follow_up_questions = collectFollowUpQuestions(question);
					for (Question follow_up : follow_up_questions) {
						if (isActiveOnAgenda(follow_up, session)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	private List<Question> collectFollowUpQuestions(Question question) {
		List<Question> children = new ArrayList<Question>();
		for (TerminologyObject object : question.getChildren()) {
			if (object instanceof Question) {
				Question child = (Question) object;
				children.add(child);
				children.addAll(collectFollowUpQuestions(child));
			} else {
				System.err.println("UNHANDLED QASET TYPE");
				// TODO: throw a bad logger message here.
			}
		}
		return children;
	}

	private boolean isTerminalQContainer(QContainer container) {
		for (TerminologyObject terminologyObject : container.getChildren()) {
			if ((terminologyObject instanceof Question) == false) {
				return false;
			}
		}
		return true;
	}


}