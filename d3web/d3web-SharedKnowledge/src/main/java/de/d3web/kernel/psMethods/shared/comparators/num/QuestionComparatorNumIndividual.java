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

package de.d3web.kernel.psMethods.shared.comparators.num;
import java.util.List;

import de.d3web.core.session.values.AnswerNum;
import de.d3web.kernel.psMethods.shared.comparators.IndividualComparator;

/**
 * Creation date: (10.08.2001 22:55:40)
 * @author: Norman Brümmer
 */
public class QuestionComparatorNumIndividual extends QuestionComparatorNum implements IndividualComparator {

	private static final long serialVersionUID = -2716446824920270409L;

	public double compare(List<?> ans1, List<?> ans2) {
		double x1 = 0;
		double x2 = 0;
		try {
			x1 = ((Double) ((AnswerNum) ans1.get(0)).getValue(null)).doubleValue();
			x2 = ((Double) ((AnswerNum) ans2.get(0)).getValue(null)).doubleValue();

			return (x1 == x2) ? 1 : 0;

		} catch (Exception e) {
			return super.compare(ans1, ans2);
		}

	}
}