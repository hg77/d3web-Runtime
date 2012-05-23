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
package cc.denkbares.testing;

/**
 * 
 * @author jochenreutelshofer
 * @created 04.05.2012
 */
public interface TestResult {

	/**
	 * Returns the message attached to this TestResult.
	 * 
	 * @created 22.05.2012
	 * @return
	 */
	public Message getMessage();

	/**
	 * Returns the arguments/parameters with which the test was executed.
	 * 
	 * @created 22.05.2012
	 * @return
	 */
	public String getConfiguration();

	/**
	 * Returns the type of the attached message.
	 * 
	 * @created 22.05.2012
	 * @return
	 */
	public Message.Type getType();

	public String getTestName();

}