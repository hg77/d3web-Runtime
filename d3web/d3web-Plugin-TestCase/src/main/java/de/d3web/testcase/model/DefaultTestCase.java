/*
 * Copyright (C) 2015 denkbares GmbH, Germany
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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import de.d3web.collections.DefaultMultiMap;
import de.d3web.collections.MultiMap;
import de.d3web.collections.MultiMaps;

/**
 * Default implementation of the TestCase interface.
 *
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 27.10.15
 */
public class DefaultTestCase implements TemplateTestCase, DescribedTestCase {

	@SuppressWarnings("Convert2Diamond") // type inference not working properly here, seems to be needed...
	private MultiMap<Date, FindingTemplate> findingTemplates = new DefaultMultiMap<Date, FindingTemplate>(MultiMaps.treeFactory(), MultiMaps
			.linkedFactory());

	@SuppressWarnings("Convert2Diamond")
	private MultiMap<Date, CheckTemplate> checkTemplates = new DefaultMultiMap<Date, CheckTemplate>(MultiMaps.treeFactory(), MultiMaps
			.linkedFactory());

	private Map<Date, String> descriptionMap = new HashMap<>();

	private TreeSet<Date> chronology = new TreeSet<>();

	private String description;
	private Date startDate;

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	public void addFinding(Date date, FindingTemplate... findingTemplate) {
		chronology.add(date);
		for (FindingTemplate template : findingTemplate) {
			findingTemplates.put(date, template);
		}
	}

	public void addCheck(Date date, CheckTemplate... checkTemplate) {
		chronology.add(date);
		for (CheckTemplate template : checkTemplate) {
			checkTemplates.put(date, template);
		}
	}

	public void addDescription(Date date, String comment) {
		chronology.add(date);
		this.descriptionMap.put(date, comment);
	}

	@Override
	public Collection<FindingTemplate> getFindingTemplates(Date date) {
		return Collections.unmodifiableCollection(findingTemplates.getValues(date));
	}

	@Override
	public Collection<CheckTemplate> getCheckTemplates(Date date) {
		return Collections.unmodifiableCollection(checkTemplates.getValues(date));
	}

	@Override
	public Collection<Date> chronology() {
		return Collections.unmodifiableSet(chronology);
	}

	@Override
	public Date getStartDate() {
		if (this.startDate != null) {
			return this.startDate;
		}
		else {
			return chronology.first();
		}
	}

	@Override
	public String getDescription(Date date) {
		return descriptionMap.get(date);
	}

	@Override
	public boolean hasDescriptions() {
		return !descriptionMap.isEmpty();
	}

	@Override
	public String toString() {
		return getDescription();
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
}
