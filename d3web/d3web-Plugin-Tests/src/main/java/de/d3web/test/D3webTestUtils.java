/*
 * Copyright (C) 2013 University Wuerzburg, Computer Science VI
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
package de.d3web.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.regex.Pattern;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.testing.Message;
import de.d3web.testing.Message.Type;
import de.d3web.testing.MessageObject;
import de.d3web.testing.Utils;
import de.d3web.utils.Pair;

/**
 * Some utility methods.
 * 
 * @author Reinhard Hatko
 * @created 26.03.2013
 */
public class D3webTestUtils {

	/**
	 * Convenience method to create error messages when working with
	 * {@link TerminologyObject}s.
	 * 
	 * @created 06.03.2013
	 * @param erroneousObjects
	 * @param failedMessage
	 * @return s an error message containing
	 */
	public static Message createErrorMessage(Collection<? extends NamedObject> erroneousObjects, String failedMessage) {
		Collection<String> objectNames = new ArrayList<String>(erroneousObjects.size());
		for (NamedObject object : erroneousObjects) {
			objectNames.add(object.getName());
		}
		return Utils.createErrorMessage(objectNames, failedMessage, NamedObject.class);
	}

	/**
	 * Filters a list of {@link NamedObjects}s.
	 * 
	 * @created 16.05.2013
	 * @param objects
	 * @param ignores derived from ignore parameters of a test
	 * @param additionalIgnores additional ignores as given by specific test
	 *        (e.g. name of the rootQASet)
	 * @return s the filtered List
	 */
	public static <T extends NamedObject> Collection<T> filterNamed(Collection<T> objects, String[][] ignores, String... additionalIgnores) {
		Collection<Pattern> ignorePatterns = Utils.compileIgnores(ignores);

		for (String ignore : additionalIgnores) {
			ignorePatterns.add(Pattern.compile(ignore, Pattern.CASE_INSENSITIVE));
		}

		Collection<T> result = new LinkedList<T>();

		for (T object : objects) {
			if (Utils.isIgnored(object.getName(), ignorePatterns)) continue;

			result.add(object);
		}

		return result;
	}

	/**
	 * Creates a failure message for the given kb using the given notification
	 * text and storing the given terminology objects as message objects.
	 * 
	 * @created 16.07.2013
	 * @param loopObjects
	 * @param kbName
	 * @param notificationText
	 * @return
	 */
	public static Message createFailureMessageWithObjects(Collection<TerminologyObject> loopObjects, String kbName, String notificationText) {
		Message message = new Message(Type.FAILURE, notificationText);
		Collection<MessageObject> msgObjects = new ArrayList<MessageObject>();
		for (TerminologyObject loopObject : loopObjects) {
			msgObjects.add(new MessageObject(loopObject.getName(),
					NamedObject.class));
		}
		msgObjects.add(new MessageObject(kbName,
				NamedObject.class));

		message.setObjects(msgObjects);
		return message;
	}

	/**
	 * Filters a list of {@link TerminologyObject}s.
	 * 
	 * @created 26.03.2013
	 * @param objects
	 * @param ignores derived from ignore parameters of a test
	 * @param additionalIgnores additional ignores as given by specific test
	 *        (e.g. name of the rootQASet)
	 * @return s the filtered List
	 */
	public static Collection<TerminologyObject> filter(Collection<TerminologyObject> objects, String[][] ignores, String... additionalIgnores) {
		return filterNamed(objects, ignores, additionalIgnores);
	}

	public static Collection<Pair<Pattern, Boolean>> compileHierarchicalIgnores(String[][] ignores) {
		Collection<Pair<Pattern, Boolean>> ignorePatterns = new LinkedList<Pair<Pattern, Boolean>>();
		for (String[] ignore : ignores) {
			Pattern pattern = Pattern.compile(ignore[0], Pattern.CASE_INSENSITIVE);
			boolean hierarchical = ignore.length == 2
					&& ignore[1].trim().equalsIgnoreCase("true");
			ignorePatterns.add(new Pair<Pattern, Boolean>(pattern, hierarchical));
		}
		return ignorePatterns;
	}

	public static boolean isIgnored(TerminologyObject object, Collection<Pair<Pattern, Boolean>> ignorePatterns) {
		for (Pair<Pattern, Boolean> pair : ignorePatterns) {
			if (isMatching(object, pair)) return true;
		}

		TerminologyObject[] parents = object.getParents();

		for (int i = 0; i < parents.length; i++) {
			if (isIgnoredInHierarchy(parents[i], ignorePatterns)) return true;

		}

		return false;
	}

	/**
	 * Checks, if one of the parents of object is ignored, based on a list of
	 * Patterns. Does not check for the object itself!
	 * 
	 * @created 25.03.2013
	 * @param object the TerminologyObject to check
	 * @param ignorePatterns list of {@link Pattern}s to ignores
	 * @return true, if the object should be ignored, false otherwise
	 */
	private static boolean isIgnoredInHierarchy(TerminologyObject object, Collection<Pair<Pattern, Boolean>> ignorePatterns) {
		for (Pair<Pattern, Boolean> pair : ignorePatterns) {
			if (isMatching(object, pair)) return pair.getB();
		}

		TerminologyObject[] parents = object.getParents();

		for (int i = 0; i < parents.length; i++) {
			if (isIgnoredInHierarchy(parents[i], ignorePatterns)) return true;

		}

		return false;
	}

	private static boolean isMatching(TerminologyObject object, Pair<Pattern, Boolean> pair) {
		return pair.getA().matcher(object.getName()).matches();
	}

}
