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

package de.d3web.scoring;

import java.util.Arrays;
import java.util.List;

import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Solution;

/**
 * Stores the score of a diagnosis in context to a problem-solving method. The
 * score has meaning to the state of a diagnosis.
 * 
 * @author joba
 * @see Solution
 * @see Rating
 */
public final class Score implements Comparable<Object> {

	public static final Score P7 = new Score(999, 1, "P7");
	public static final Score P6 = new Score(80, 1, "P6");
	public static final Score P5x = new Score(50, 1, "P5x");
	public static final Score P5 = new Score(40, 1.8, "P5");
	public static final Score P4 = new Score(20, 1.4, "P4");
	public static final Score P3 = new Score(10, 1.2, "P3");
	public static final Score P2 = new Score(5, 1.1, "P2");
	public static final Score P1 = new Score(2, 1, "P1");
	public static final Score N7 = new Score(Double.NEGATIVE_INFINITY, 0, "N7");
	public static final Score N6 = new Score(-80, 0, "N6");
	public static final Score N5x = new Score(-50, 0, "N5x");
	public static final Score N5 = new Score(-40, -40, "N5");
	public static final Score N4 = new Score(-20, -20, "N4");
	public static final Score N3 = new Score(-10, -10, "N3");
	public static final Score N2 = new Score(-5, -5, "N2");
	public static final Score N1 = new Score(-2, 0, "N1");
	public static final List<Score> APRIORI = Arrays.asList(P5, P4, P3, P2, N2, N3, N4, N5);
	private static final List<Score> allScores = Arrays.asList(P7, P6, P5x, P5, P4, P3, P2, P1, N1, N2, N3, N4, N5, N5x, N6, N7);

	private final String symbol;
	private final double score;
	private final double aPriori;

	/**
	 * Creates a new score given to a diagnosis with a fixed amount of points
	 * and an symbolic name.
	 * 
	 * @param theAPriori is the score a Diagnosis has as "offset". If it is
	 *        &lt;0, it will be added, otherwise multiplied with theScore.
	 */
	private Score(double theScore, double theAPriori, String symbol) {
		score = theScore;
		aPriori = theAPriori;
		this.symbol = symbol;
	}

	/**
	 * Compares this object with the specified object for order. Returns a
	 * negative integer, zero, or a positive integer as this object is less
	 * than, equal to, or greater than the specified object.
	 */
	@Override
	public int compareTo(Object o) {
		if (o instanceof Score) {
			double ret = getScore() - ((Score) o).getScore();
			if (ret > 0) {
				return 1;
			}
			if (ret < 0) {
				return -1;
			}
			return 0;
		}
		else {
			throw new ClassCastException();
		}
	}

	/**
	 * hashCode() method taking "symbol" and "score" into account.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(score);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + symbol.hashCode();
		return result;
	}

	/**
	 * Compares the internal scores of the two instances of DiagnosisScore. If
	 * the symbol is available for both, then these are checked as well.
	 * 
	 * @return boolean the equality of the two int-values
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Score other = (Score) obj;
		return other.getScore() == getScore();
	}

	public double getAPriori() {
		return aPriori;
	}

	public double getScore() {
		return score;
	}

	public String getSymbol() {
		return symbol;
	}

	/**
	 * @return the name of the symbol
	 */
	@Override
	public String toString() {
		return getSymbol();
	}

	/**
	 * @return a List of all predefined scores.
	 */
	public static List<Score> getAllScores() {
		return allScores;
	}
}