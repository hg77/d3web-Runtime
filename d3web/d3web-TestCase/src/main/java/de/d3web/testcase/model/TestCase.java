/*
 * Copyright (C) 2012 denkbares GmbH
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
package de.d3web.testcase.model;

import java.util.Collection;
import java.util.Date;

import de.d3web.core.knowledge.TerminologyObject;

/**
 * Interface describing a repeatable and testable case for knowledge base
 * evaluation. Its intended to be implemented for various case formats.
 * 
 * @author Volker Belli & Markus Friedrich (denkbares GmbH)
 * @created 23.01.2012
 */
public interface TestCase {

	/**
	 * Collection of Dates to iterate through the entries of this
	 * {@link TestCase}'s entries.
	 * <P>
	 * The iteration is ordered by the time of the entries. Therefore, the
	 * iteration starts at the time of the oldest entry and proceeds to the
	 * newer values.
	 * 
	 * @return Collection of Dates chronological ordered
	 */
	Collection<Date> chronology();

	/**
	 * Returns all findings of this TestCase associated to the specified Date.
	 * If there is no such finding in this TestCase, an empty Collection is
	 * returend.
	 * 
	 * @created 23.01.2012
	 * @param date the Date to get the Findings for
	 * @return Findings at the specified Date
	 */
	Collection<Finding> getFindings(Date date);

	/**
	 * Finding of the defined {@link TerminologyObject} at the specified Date.
	 * If there is no such finding, null is returned.
	 * 
	 * @created 23.01.2012
	 * @param date the Date to get the Finding for
	 * @param object the {@link TerminologyObject} to get the Finding for
	 * @return Finding of the {@link TerminologyObject} at the specified Date
	 */
	Finding getFinding(Date date, TerminologyObject object);

	/**
	 * Returns all checks of this TestCase associated to the specified Date. If
	 * there is no such check in this TestCase, an empty Collection is returend.
	 * 
	 * @created 23.01.2012
	 * @param date the Date to get the Checks for
	 * @return Checks at the specified date
	 */
	Collection<Check> getChecks(Date date);

	/**
	 * Returns the Date when the TestCase was originally started
	 * 
	 * @created 24.01.2012
	 * @return Date when the TestCase was started
	 */
	Date getStartDate();
}