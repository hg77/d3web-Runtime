package de.d3web.core.session.blackboard;

import java.util.Collection;
import java.util.List;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;

/**
 * The Blackboard manages all dynamic values created within the case and
 * propagated throughout the inference system.
 * 
 * @author volker_belli
 * 
 */
public interface Blackboard {

	/**
	 * Returns the session this blackboard has been created for.
	 * 
	 * @return the session of this blackboard
	 */
	public Session getSession();

	/**
	 * Adds a new value fact to this blackboard. If an other fact for the same
	 * terminology object and with the same source has already been added, that
	 * fact will be replaced by the specified one.
	 * 
	 * @param fact the fact to be added
	 */
	public void addValueFact(Fact fact);

	/**
	 * Removes a value fact from this blackboard. If the fact does not exists in
	 * the blackboard, this method has no effect.
	 * 
	 * @param fact the fact to be removed
	 */
	public void removeValueFact(Fact fact);

	/**
	 * Removes all value facts with the specified source from this blackboard
	 * for the specified terminology object. If no such fact exists in the
	 * blackboard, this method has no effect.
	 * 
	 * @param termObject the terminology object to remove the value facts from
	 * @param source the fact source to be removed
	 */
	public void removeValueFact(TerminologyObject terminologyObject, Object source);

	/**
	 * Returns the merged fact for all value facts of the specified terminology
	 * object.
	 * 
	 * @param terminologyObject the terminology object to access the merged fact
	 *        for
	 * @return the merged fact
	 */
	public Fact getValueFact(TerminologyObject terminologyObject);

	/**
	 * Returns a collection of all terminology objects that have a value. This
	 * means this method delivers all terminology objects that currently have at
	 * lead one value fact added for it to this blackboard. The collection may
	 * be unmodifiable.
	 * 
	 * @return the collection of valued terminology objects
	 */
	public Collection<TerminologyObject> getValuedObjects();

	/**
	 * Returns a collection of all questions that have a value. This means this
	 * method delivers all questions that currently have at lead one value fact
	 * added for it to this blackboard. The collection may be unmodifiable.
	 * 
	 * @return the collection of valued questions
	 */
	public Collection<Question> getValuedQuestions();

	/**
	 * Returns a collection of all diagnoses that have a value. This means this
	 * method delivers all diagnoses that currently have at lead one value fact
	 * added for it to this blackboard. The collection may be unmodifiable.
	 * 
	 * @return the collection of valued diagnoses
	 */
	public Collection<Solution> getValuedSolutions();

	/**
	 * Adds a new interview fact to this blackboard. If an other interview fact
	 * for the same terminology object and with the same source has already been
	 * added, that fact will be replaced by the specified one.
	 * 
	 * @param fact the fact to be added
	 */
	public void addInterviewFact(Fact fact);

	/**
	 * Removes a interview fact from this blackboard. If the interview fact does
	 * not exists in the blackboard, this method has no effect.
	 * 
	 * @param fact the fact to be removed
	 */
	public void removeInterviewFact(Fact fact);

	/**
	 * Removes all interview facts with the specified source from this
	 * blackboard for the specified terminology object. If no such fact exists
	 * in the blackboard, this method has no effect.
	 * 
	 * @param termObject the terminology object to remove the interview facts
	 *        from
	 * @param source the fact source to be removed
	 */
	public void removeInterviewFact(TerminologyObject terminologyObject, Object source);

	/**
	 * Removes all interview facts from this blackboard for the specified
	 * terminology object. If no such fact exists in the blackboard, this method
	 * has no effect.
	 * 
	 * @param termObject the terminology object to remove the interview facts
	 *        from
	 */
	public void removeInterviewFacts(TerminologyObject terminologyObject);

	/**
	 * Returns the merged fact for all interview facts of the specified
	 * terminology object.
	 * 
	 * @param terminologyObject the terminology object to access the merged fact
	 *        for
	 * @return the merged fact
	 */
	public Fact getInterviewFact(TerminologyObject terminologyObject);

	/**
	 * Returns a collection of all terminology objects that have been rated for
	 * the usage in the interview. This means the method delivers all
	 * terminology objects that currently have at lead one interview fact added
	 * for it to this blackboard.
	 * 
	 * @return the collection of interview rated terminology objects
	 */
	public Collection<TerminologyObject> getInterviewObjects();

	/**
	 * Returns the current rating of the diagnosis. The returned rating is the
	 * merged rating over all problem solvers available. This is a typed
	 * shortcut for accessing the value {@link Fact} of the {@link Solution} and
	 * read out its current value.
	 * 
	 * @param solution the solution to take the rating from
	 * @return the total rating of the solution
	 */
	public Rating getRating(Solution solution);

	/**
	 * Returns the current answer of the question. The returned answer is the
	 * merged answer over all problem solvers available. This is a typed
	 * shortcut for accessing the value {@link Fact} of the {@link Question} and
	 * read out its current value.
	 * 
	 * @param question the question to take the rating from
	 * @return the answer of the question
	 */
	// public Answer getAnswer(Question question) {
	// return (Answer) getValueFact(question).getValue();
	// }

	public Value getValue(Question question);

	/**
	 * Returns the Value of a TerminologyObject, calculated by the specified
	 * psmethod
	 * 
	 * @param object TerminologyObject
	 * @param psmethod PSMethod
	 * @return Value
	 */
	public Value getValue(TerminologyObject object, PSMethod psmethod);

	/**
	 * Returns the current indication state of the interview element. The
	 * returned indication state is the merged indication over all strategic
	 * solvers available. This is a typed shortcut for accessing the interview
	 * {@link Fact} of the {@link QASet} and read out its current value.
	 * 
	 * @param question the question to take the rating from
	 * @return the indication of the interview element
	 */
	public Indication getIndication(InterviewObject interviewElement);

	/**
	 * Return a list of all answered questions
	 * 
	 * @author Markus Friedrich (denkbares GmbH)
	 * @created 11.05.2010
	 * @return List of answered questions
	 */
	public List<Question> getAnsweredQuestions();

	/**
	 * Returns all {@link Solution} instances, that hold the specified
	 * {@link Rating}.
	 * 
	 * @param state the Rating the diagnoses must have to be returned
	 * @return a list of diagnoses in this case that have the state 'state'
	 */
	public List<Solution> getSolutions(Rating.State state);
}
