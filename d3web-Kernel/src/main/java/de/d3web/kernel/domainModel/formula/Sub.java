package de.d3web.kernel.domainModel.formula;

import de.d3web.kernel.XPSCase;
/**
 * Substraction term
 * Creation date: (14.08.2000 16:40:08)
 * @author Norman Brümmer
 */
public class Sub extends FormulaNumberArgumentsTerm implements FormulaNumberElement{

	/** 
	 * Creates a new FormulaTerm with null-arguments.
	 */
	public Sub() {
		this(null, null);
	}
	/**
	 *	Creates a new FormulaTerm (arg1 - arg2)
	 *	@param arg1 first argument of the term
	 *	@param arg2 second argument of the term 
	 **/
	public Sub(FormulaNumberElement arg1, FormulaNumberElement arg2) {
		setArg1(arg1);
		setArg2(arg2);
		setSymbol("-");
	}

	/**
	 * @return substracted evaluated arguments
	 */
	public Double eval(XPSCase theCase) {
		if (super.eval(theCase) == null)
			return null;
		else
			return new Double(
				getEvaluatedArg1().doubleValue()
					- getEvaluatedArg2().doubleValue());
	}
}
