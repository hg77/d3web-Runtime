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

/*
 * Created on 30.09.2003
 */
package de.d3web.caserepository;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.d3web.caserepository.CaseObject.Solution;

/**
 * 30.09.2003 12:09:05
 * @author hoernlein
 */
public class SolutionContainerImpl implements ISolutionContainer {

    public static String getXMLCode(Collection solutions) {
        StringBuffer sb = new StringBuffer();
        sb.append("<Solutions>\n");
        Iterator iter = solutions.iterator();
        while (iter.hasNext()) {
            CaseObject.Solution s = (CaseObject.Solution) iter.next();
            sb.append("<Solution" +
                    " id=\"" + s.getDiagnosis().getId() + "\"" +
                    " weight=\"" + s.getWeight() + "\"" +
                    " psmethod=\"" + s.getPSMethodClass().getName() + "\"" +
                    " state=\"" + s.getState().toString() + "\"" +
            "/>\n");
        }
        sb.append("</Solutions>\n");
        return sb.toString();
    }
    
	private Set solutions = new HashSet();

	/*
	 * (non-Javadoc)
	 * @see de.d3web.caserepository.ISolutionContainer#addSolution(de.d3web.caserepository.CaseObject.Solution)
	 */
	public void addSolution(CaseObject.Solution solution) {
		solutions.add(solution);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.d3web.caserepository.ISolutionContainer#removeSolution(de.d3web.caserepository.CaseObject.Solution)
	 */
	public void removeSolution(CaseObject.Solution solution) {
		solutions.remove(solution);
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.CaseObject#getSolutions()
	 */
	public Set getSolutions() {
		return Collections.unmodifiableSet(solutions);
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.CaseObject#getSolution(de.d3web.kernel.domainModel.Diagnosis, java.lang.Class)
	 */
	public Solution getSolution(de.d3web.core.knowledge.terminology.Solution d, Class psMethodClass) {
		Iterator iter = solutions.iterator();
		while (iter.hasNext()) {
			CaseObject.Solution s = (CaseObject.Solution) iter.next();
			if (s.getDiagnosis().equals(d) && s.getPSMethodClass().equals(psMethodClass))
				return s;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.CaseObject#getSolutions(java.lang.Class)
	 */
	public Set getSolutions(Class psMethodClass) {
		Set result = new HashSet();
		Iterator iter = solutions.iterator();
		while (iter.hasNext()) {
			CaseObject.Solution s = (CaseObject.Solution) iter.next();
			if (s.getPSMethodClass() != null && s.getPSMethodClass().equals(psMethodClass))
				result.add(s);
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see de.d3web.caserepository.ISolutionContainer#getXMLCode()
	 */
	public String getXMLCode() {
	    return getXMLCode(solutions);
	}

}
