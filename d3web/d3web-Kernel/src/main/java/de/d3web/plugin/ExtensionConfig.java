/*
 * Copyright (C) 2009 denkbares GmbH
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
package de.d3web.plugin;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.d3web.core.KnowledgeBase;
import de.d3web.core.inference.KnowledgeSlice;
import de.d3web.core.inference.MethodKind;
import de.d3web.core.inference.PSMethod;
import de.d3web.core.session.XPSCase;

/**
 * This KnowledgeSlice is used to store the configuration of the extensions
 * at the KnowledgeBase
 * @author Markus Friedrich (denkbares GmbH)
 */
public class ExtensionConfig implements KnowledgeSlice {

	private static final long serialVersionUID = -3148626378108269574L;

	private KnowledgeBase kb;
	private List<ExtensionEntry> entries = new ArrayList<ExtensionEntry>();
	
	public static MethodKind EXTENSIONCONFIG = new MethodKind("ExtensionConfig");
	
	public ExtensionConfig(KnowledgeBase kb) {
		super();
		this.kb = kb;
		kb.addKnowledge(getProblemsolverContext(), this, EXTENSIONCONFIG);
	}

	public ExtensionConfig(KnowledgeBase kb, List<ExtensionEntry> entries) {
		super();
		this.kb = kb;
		this.entries = entries;
	}

	@Override
	public String getId() {
		return "ExtensionConfig";
	}

	@Override
	public Class<? extends PSMethod> getProblemsolverContext() {
		//TODO change
		return PSMethod.class;
	}

	@Override
	public boolean isUsed(XPSCase theCase) {
		return false;
	}

	@Override
	public void remove() {
		kb.removeKnowledge(getProblemsolverContext(), this);
	}
	
	/**
	 * Adds a ExtensionEntry to this KnowledgeSlice
	 * @param entry ExtensionEntry containing the configuration of one Extension
	 */
	public void addEntry(ExtensionEntry entry) {
		entries.add(entry);
	}
	
	/**
	 * Returns an unmodifiable list of all ExtensionEntries contained in this KnowledgeSlice
	 * @return a list of ExtensionEntries
	 */
	public List<ExtensionEntry> getEntries() {
		return Collections.unmodifiableList(entries);
	}

}