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

package de.d3web.core.session.interviewmanager;

import java.util.List;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Value;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.XPSCaseObject;
import de.d3web.core.session.values.UndefinedValue;

/**
 * This type of Question will be returned by a moveTo... method of
 * DialogController, if the history limit has been reached
 * 
 * @see ExceptionQuestionNext
 * @see ExceptionQuestionPrevious
 * @author Norman Brümmer
 */
public abstract class ExceptionQuestion extends Question {

	/**
     * fulfils the super-contructor
     */
    public ExceptionQuestion(KnowledgeBase kb, String id, String text) {
	super(id);
	setKnowledgeBase(kb);
	setName(text);
    }

    /**
     * fulfils the super-contructor
     */
    public ExceptionQuestion(KnowledgeBase kb, String id, String text,
	    List<NamedObject> children) {
	super(id);
	setKnowledgeBase(kb);
	setName(text);
	setChildren(children);
    }

    /**
     * does nothing here
     */
    public void addContraReason(Object source, Session theCase) {/*
								     * does
								     * nothing
								     */
    }

    /**
     * does nothing here
     */
    public void addProReason(Object source, Session theCase) {/* does nothing */
    }

    /**
     * Won´t return a XPSCaseObject, because this is just a "marker"-Class
     * 
     * @return null
     */
    public XPSCaseObject createCaseObject(Session session) {
	return null;
    }

    /**
     * this marker class must not return any answer
     * 
     * @return null
     */
    public de.d3web.core.knowledge.terminology.Answer getAnswer(Object value) {
	return null;
    }

    /**
     * has no value
     */
    @Override
	public Value getValue(Session theCase) {
		return UndefinedValue.getInstance();
    }

    /**
     * this marker class never gets a value
     * 
     * @return false
     */
    @Override
	public boolean hasValue(Session theCase) {
	return false;
    }

    /**
     * @return true
     */
    @Override
	public boolean isDone(Session theCase) {
	return true;
    }

    /**
     * has no pro or contra reasons. just a marker class
     */
    public void removeContraReason(Object source,
	    de.d3web.core.session.Session theCase) {
    }

    /**
     * has no pro or contra reasons. just a marker class
     */
    public void removeProReason(Object source, de.d3web.core.session.Session theCase) {
    }

    /**
     * does nothing because this marker class needs no value
     */
    @Override
	public void setValue(Session theCase, Value value) {
	}

    @Override
	public String toString() {
	return "ExQ";
    }

}