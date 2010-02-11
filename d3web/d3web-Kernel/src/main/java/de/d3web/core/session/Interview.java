package de.d3web.core.session;

import de.d3web.core.InterviewObject;
import de.d3web.core.terminology.Diagnosis;
import de.d3web.core.terminology.Question;

public interface Interview {

	/**
	 * Returns the interview object to be answered next / now. Answers should
	 * usually only be given by a source solver. Usually three types of
	 * interviews should be distinguished:
	 * <ul>
	 * <li> {@link Questionnaire}: All successor questions (direct and indirect
	 * children) of the questionnaire should be answered by the caller as long
	 * as they are relevant, see
	 * {@link Question#isValid(de.d3web.kernel.XPSCase)}.
	 * <li> {@link Question}: The caller should only answer exactly that
	 * question.
	 * <li> {@link Diagnosis}: The caller should present the user exactly that
	 * solution and "answer" it by setting a state for this solution.
	 * </ul>
	 * 
	 * @return
	 */
	InterviewObject getCurrentInterviewObject();
	
	

}