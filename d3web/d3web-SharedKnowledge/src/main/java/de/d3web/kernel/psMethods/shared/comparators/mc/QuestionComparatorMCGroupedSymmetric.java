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

package de.d3web.kernel.psMethods.shared.comparators.mc;

import java.util.Iterator;
import java.util.List;

import de.d3web.kernel.psMethods.shared.comparators.GroupedComparatorSymmetric;
import de.d3web.kernel.psMethods.shared.comparators.PairRelation;

public class QuestionComparatorMCGroupedSymmetric extends
		QuestionComparatorMCGrouped implements GroupedComparatorSymmetric{
	
	public double compare(List<?> ans1, List<?> ans2) {
        if (isSameAnswerListContent(ans1, ans2)) { return 1; }

        double value = 0;
        double countComp = 0;

        Iterator<PairRelation> iter = pairRelations.iterator();
        while (iter.hasNext()) {
            PairRelation rel = iter.next();
            if ((ans1.contains(rel.getAnswer1()) && ans2.contains(rel
                    .getAnswer1()))
                    || (ans1.contains(rel.getAnswer2()) && ans2.contains(rel
                            .getAnswer2()))) {
                value += 1.0;
                countComp++;
            } else if ((ans1.contains(rel.getAnswer1()) && ans2.contains(rel
                    .getAnswer2()))
                    || (ans1.contains(rel.getAnswer2()) && ans2.contains(rel
                            .getAnswer1()))) {
                value += rel.getValue();
                countComp++;
            }
        }
        if (countComp == 0) {
            return 0;
        } else {
            return value / countComp;
        }
    }
	
	private boolean isSameAnswerListContent(List<?> l1, List<?> l2) {
        Iterator<?> iter1 = l1.iterator();
        while (iter1.hasNext()) {
            if (!l2.contains(iter1.next())) { return false; }
        }

        Iterator<?> iter2 = l2.iterator();
        while (iter2.hasNext()) {
            if (!l1.contains(iter2.next())) { return false; }
        }

        return true;
    }
}
