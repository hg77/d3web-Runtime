/*
 * Created on 13.10.2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.d3web.kernel.domainModel.formula;

import java.util.Date;

import de.d3web.kernel.XPSCase;
import de.d3web.kernel.domainModel.Answer;
import de.d3web.kernel.domainModel.answers.AnswerDate;
import de.d3web.kernel.domainModel.qasets.Question;

/**
 * Encapsulates a FormulaDateElement and ensures the return of an answer date
 * </p>
 * @author Tobias Vogele
 * <P>
 * @see FormulaExpression
 */
public class FormulaDateExpression implements java.io.Serializable {

	/** the Question this expression belongs to */
	private Question question;

	/** The encapsulated formula date element */
	private FormulaDateElement fElement;

	public String toString() {
		return "[FormulaDateExpression, " + question.getId() + "] " + fElement.toString();
	}

	/**
	 * creates a new FormulaDateExpression by the given Question and FormulaDateElement
	 */
	public FormulaDateExpression(Question question, FormulaDateElement fElement) {
		super();
		this.question = question;
		this.fElement = fElement;
	}

	/**
	 * Evaluates the formulaDateElement and creates the returned value into an AnswerDate
	 * @return an AnswerDate containing the evaluated value
	 */
	public Answer eval(XPSCase theCase) {
		Date answer = fElement.eval(theCase);
		if (answer != null) {
			AnswerDate answerD = new AnswerDate();
			answerD.setQuestion(question);
			answerD.setValue(answer);
			return answerD;
		} else
			return null;
	}

	public FormulaDateElement getFormulaDateElement() {
		return fElement;
	}

	public Question getQuestionDate() {
		return question;
	}

	/**
	 * the XML-representation of this FormulaExpression
	 */
	public java.lang.String getXMLString() {
		StringBuffer sb = new StringBuffer();
		if ((question != null) && (fElement != null)) {
			sb.append("<FormulaDateExpression>\n");
			sb.append(question.getXMLString());
			sb.append(fElement.getXMLString());
			sb.append("</FormulaDateExpression>\n");
		} else {
			System.err.println("ERROR: could not create xml string for FormulaDateExpression!");
		}
		return sb.toString();
	}
}
