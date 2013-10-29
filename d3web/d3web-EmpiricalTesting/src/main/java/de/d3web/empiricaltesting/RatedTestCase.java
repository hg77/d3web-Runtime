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

package de.d3web.empiricaltesting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class RatedTestCase {

	/**
	 * The name of the Rated Test Case
	 */
	private String name = "";

	/**
	 * A note related to this Rated Test Case. The note may be used if some
	 * additional description is required for that case.
	 */
	private String note = null;

	/**
	 * This Rated Testcase's List of Findings
	 */
	private List<Finding> findings;

	/**
	 * This Rated Testcase's List of expected Solutions (will be loaded from
	 * TestCaseRepository)
	 */
	private List<RatedSolution> expectedSolutions;

	/**
	 * List of expected findings (derived by a psmethod)
	 */
	private List<Finding> expectedFindings;

	/**
	 * This Rated Testcase's List of derived Solutions (derived from the
	 * KnowledgeBase while in TestRun)
	 */
	private List<RatedSolution> derivedSolutions;

	/**
	 * Indication of when this ratedTestCase was tested
	 */
	private String lastTested;

	/**
	 * If this case has been tested before
	 */
	private boolean wasTestedBefore;

	/**
	 * Propagation time
	 */
	private Date timeStamp;

	/**
	 * After deriving the actual solutions from the knowledge base, this must be
	 * set to true. Every change on this testCase (i.e. adding a finding) will
	 * reset this flag to false.
	 */
	@Deprecated
	private boolean derivedSolutionsAreUpToDate;

	/**
	 * Default Constructor.
	 */
	public RatedTestCase() {
		super();
		findings = new ArrayList<Finding>(1); // often only one finding
		// contained in the rtc
		expectedSolutions = new ArrayList<RatedSolution>();
		expectedFindings = new ArrayList<Finding>();
		derivedSolutions = new ArrayList<RatedSolution>();
		lastTested = "";
		wasTestedBefore = false;
	}

	/**
	 * Appends a Finding to this RatedTestCase's List of findings.
	 * 
	 * @param findings The Finding to be added
	 * @return True if the Finding was successfully appended
	 */
	public boolean add(Finding finding) {
		boolean result = findings.add(finding);
		return result;
	}

	/**
	 * Appends a whole list of Findings to this RatedTestCase's List of
	 * findings.
	 * 
	 * @param findings The List of Findings to be added
	 * @return True if the Findings were successfully appended
	 */
	public boolean addFindings(List<Finding> findings) {
		boolean result = true;
		for (Finding finding : findings) {
			result = add(finding) && result;
		}
		return result;
	}

	/**
	 * Appends some RatedSolution instances to this RatedTestCase's List of
	 * expected solutions.
	 * 
	 * @param solutions The RatedSolution instances to be added
	 * @return True if the solutions were successfully appended
	 */
	public boolean addExpected(RatedSolution... solutions) {
		boolean result = true;
		for (RatedSolution solution : solutions) {
			result = result && expectedSolutions.add(solution);
		}
		return result;
	}

	/**
	 * Appends a whole list of RatedSolutions to this RatedTestCase's List of
	 * expected Solutions.
	 * 
	 * @param solutions The List of RatedSolutions to be added
	 * @return True if the RatedSolutions were successfully appended
	 */
	public boolean addExpected(List<RatedSolution> solutions) {
		boolean result = true;
		for (RatedSolution ratedSolution : solutions) {
			result = addExpected(ratedSolution) && result;
		}
		return result;
	}

	public boolean addExpectedFinding(Finding... findings) {
		boolean allAdded = true;
		for (Finding finding : findings) {
			allAdded = allAdded && this.expectedFindings.add(finding);
		}
		return allAdded;
	}

	public boolean addExpectedFindings(Collection<Finding> expectedFindings) {
		return this.expectedFindings.addAll(expectedFindings);
	}

	/**
	 * Appends some RatedSolution instances to this RatedTestCase´s List of
	 * derived solutions.
	 * 
	 * @param solutions The RatedSolution instances to be added
	 * @return True if RatedSolution was successfully appended
	 */
	public boolean addDerived(RatedSolution... solutions) {
		boolean result = true;
		for (RatedSolution solution : solutions) {
			result = result && derivedSolutions.add(solution);
		}
		return result;
	}

	/**
	 * Appends a whole list of RatedSolutions to this RatedTestCase's List of
	 * derived Solutions.
	 * 
	 * @param solutions The List of RatedSolutions to be added
	 * @deprecated no longer use this method, it will be removed with the next
	 *             release
	 * @return True if the RatedSolutions were successfully appended
	 */
	@Deprecated
	public boolean addDerived(List<RatedSolution> solutions) {
		boolean result = true;
		for (RatedSolution ratedSolution : solutions) {
			result = addDerived(ratedSolution) && result;
		}
		return result;
	}

	/**
	 * Returns the Date on which this RatedTestCase was last tested.
	 * 
	 * @return the lastTested
	 */
	public String getLastTested() {
		return lastTested;
	}

	/**
	 * @deprecated no longer use this method, it will be removed with the next
	 *             release
	 * @return the derivedSolutionsAreUpToDate
	 */
	@Deprecated
	public boolean getDerivedSolutionsAreUpToDate() {
		return derivedSolutionsAreUpToDate;
	}

	/**
	 * @deprecated no longer use this method, it will be removed with the next
	 *             release
	 * @param derivedSolutionsAreUpToDate the derivedSolutionsAreUpToDate to set
	 */
	public void setDerivedSolutionsAreUpToDate(
			boolean derivedSolutionsAreUpToDate) {
		this.derivedSolutionsAreUpToDate = derivedSolutionsAreUpToDate;
	}

	/**
	 * Sets TestingDate to a specified date.
	 * 
	 * @param date String formatted date
	 */
	public void setTestingDate(String date) {
		lastTested = date;
	}

	public void inverseSortSolutions() {
		Collections.sort(expectedSolutions,
				new RatedSolution.InverseRatingComparator());
		Collections.sort(derivedSolutions,
				new RatedSolution.InverseRatingComparator());
	}

	/**
	 * String Representation of this RatedTestCase. <name( findings : Expected:
	 * expectedSolutions; Derived: derivedSolutions; )>
	 */
	@Override
	public String toString() {
		return "<" + name + " (\n\tFindings:" + findings + "; \n\tExpected:"
				+ expectedSolutions + ", " + expectedFindings + "; \n\tDerived:" + derivedSolutions
				+ "; \n\t)>";
	}

	/**
	 * Returns the Findings of this RatedTestCase.
	 * 
	 * @return List of Findings
	 */
	public synchronized List<Finding> getFindings() {
		return findings;
	}

	/**
	 * Returns the ExpectedSolutions of this RatedTestCase.
	 * 
	 * @return List of RatedSolutions
	 */
	public synchronized List<RatedSolution> getExpectedSolutions() {
		return expectedSolutions;
	}

	public synchronized void setExpectedSolutions(List<RatedSolution> expectedSolutions) {
		this.expectedSolutions = expectedSolutions;
	}

	/**
	 * Returns the derivedSolutions of this RatedTestCase.
	 * 
	 * @return List of RatedSolutions
	 */
	public synchronized List<RatedSolution> getDerivedSolutions() {
		return derivedSolutions;
	}

	// @Deprecated
	// public void update(Solution solution, Rating rating) {
	// RatedSolution rsolution = getBySolution(solution);
	// if (rsolution == null) {
	// addExpected(new RatedSolution(solution, rating));
	// }
	// else {
	// rsolution.update(rating);
	// }
	// }
	//
	// @Deprecated
	// private RatedSolution getBySolution(Solution solution) {
	// for (RatedSolution rsol : expectedSolutions) {
	// if (rsol.solution.equals(solution)) return rsol;
	// }
	// return null;
	// }

	/**
	 * A new instance is created and the lists solutions and findings are copied
	 * into the new instance.
	 * 
	 * @return a flat clone of the instance
	 */
	public RatedTestCase flatClone() {
		RatedTestCase newRTC = new RatedTestCase();
		newRTC.name = name;
		newRTC.findings = findings;
		newRTC.expectedFindings = expectedFindings;
		newRTC.expectedSolutions = expectedSolutions;
		newRTC.derivedSolutions = derivedSolutions;
		newRTC.timeStamp = timeStamp;
		return newRTC;
	}

	/**
	 * Returns the name of this RatedTestCase.
	 * 
	 * @return String representing the name of this RatedTestCase
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of this RatedTestCase.
	 * 
	 * @param name desired name
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((expectedFindings == null) ? 0 : expectedFindings.hashCode());
		result = prime * result + ((expectedSolutions == null) ? 0 : expectedSolutions.hashCode());
		result = prime * result + ((findings == null) ? 0 : findings.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((timeStamp == null) ? 0 : timeStamp.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		RatedTestCase other = (RatedTestCase) obj;
		if (expectedFindings == null) {
			if (other.expectedFindings != null) return false;
		}
		else if (!expectedFindings.equals(other.expectedFindings)) return false;
		if (expectedSolutions == null) {
			if (other.expectedSolutions != null) return false;
		}
		else if (!expectedSolutions.equals(other.expectedSolutions)) return false;
		if (findings == null) {
			if (other.findings != null) return false;
		}
		else if (!findings.equals(other.findings)) return false;
		if (name == null) {
			if (other.name != null) return false;
		}
		else if (!name.equals(other.name)) return false;
		if (timeStamp == null) {
			if (other.timeStamp != null) return false;
		}
		else if (!timeStamp.equals(other.timeStamp)) return false;
		return true;
	}

	/**
	 * Returns true if this RatedTestCase was tested before.
	 * 
	 * @return True if this RatedTestCase was tested before. Else false.
	 */
	public boolean wasTestedBefore() {
		return wasTestedBefore;
	}

	/**
	 * Sets if this RatedTestCase was tested before.
	 * 
	 * @param wasTestedBefore Boolean value.
	 */
	public void setWasTestedBefore(boolean wasTestedBefore) {
		this.wasTestedBefore = wasTestedBefore;
	}

	public Date getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

	public Collection<Finding> getExpectedFindings() {
		return Collections.unmodifiableCollection(expectedFindings);
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getNote() {
		return note;
	}
}