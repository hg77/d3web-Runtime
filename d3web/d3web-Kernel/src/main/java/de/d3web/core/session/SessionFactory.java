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

package de.d3web.core.session;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import de.d3web.abstraction.inference.PSMethodAbstraction;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.PSMethodInit;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.session.interviewmanager.FormStrategy;
import de.d3web.core.session.interviewmanager.NextUnansweredQuestionFormStrategy;
import de.d3web.core.session.interviewmanager.PSMethodInterview;
import de.d3web.indication.inference.PSMethodDialogControlling;
import de.d3web.indication.inference.PSMethodStrategic;
import de.d3web.indication.inference.PSMethodUserSelected;
import de.d3web.scoring.inference.PSMethodHeuristic;

/**
 * Factory for Session objects.
 * 
 * @author joba, Norman Brümmer, Georg
 */
public final class SessionFactory {

	private static final List<PSMethod> commonPSMethods = Arrays.asList(
			PSMethodUserSelected.getInstance(),
			new PSMethodDialogControlling(),
			PSMethodStrategic.getInstance(),
			PSMethodAbstraction.getInstance(),
			PSMethodHeuristic.getInstance(),
			PSMethodInit.getInstance(),
			PSMethodInterview.getInstance()
			);

	private SessionFactory() { // enforce noninstantiability
	}

	/**
	 * Returns a list of all default {@link PSMethod}s that will be used at
	 * least when creating a knowledge base.
	 * 
	 * @created 19.10.2010
	 * @return the default solvers
	 */
	public static final List<PSMethod> getDefaultPSMethods() {
		return Collections.unmodifiableList(commonPSMethods);
	}

	/**
	 * Factory-method that creates instances of Session.
	 * 
	 * @param knowledgeBase the knowledge base used in the case.
	 * @return new Session instance based on the specified knowledge base
	 */
	public static synchronized Session createSession(KnowledgeBase knowledgeBase) {
		return createSession(null, knowledgeBase, new NextUnansweredQuestionFormStrategy(),
				new Date());
	}

	/**
	 * Factory-method that creates instances of Session
	 * 
	 * @created 28.01.2011
	 * @param kb {@link KnowledgeBase}
	 * @param creationDate Date of creation
	 * @return {@link Session}
	 */
	public static synchronized DefaultSession createSession(KnowledgeBase kb, Date creationDate) {
		return createSession(null, kb, new NextUnansweredQuestionFormStrategy(), creationDate);
	}

	/**
	 * Factory-method that creates instances of Session
	 * 
	 * @created 27.09.2010
	 * @param id the ID
	 * @param kb {@link KnowledgeBase}
	 * @param creationDate Date of creation
	 * @return {@link Session}
	 */
	public static synchronized DefaultSession createSession(String id, KnowledgeBase kb, Date creationDate) {
		return createSession(id, kb, new NextUnansweredQuestionFormStrategy(), creationDate);
	}

	/**
	 * Factory-method that creates instances of Session.
	 * 
	 * @param id the ID
	 * @param knowledgeBase the knowledge base used in the case.
	 * @param formStrategy the specified {@link FormStrategy}
	 * @return new Session instance based on the specified id, knowledge base
	 *         and form strategy
	 */
	public static synchronized DefaultSession createSession(String id,
			KnowledgeBase knowledgeBase,
			FormStrategy formStrategy, Date creationDate) {
		return new DefaultSession(id, knowledgeBase, formStrategy, creationDate);
	}
}