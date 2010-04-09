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

package de.d3web.core.knowledge.terminology.info;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * DCMarkup is for characterising an object according to the Dublin Core
 * @link http://dublincore.org/
 * @see de.d3web.core.knowledge.terminology.info.DCMarkedUp
 * @author hoernlein
 */
public class DCMarkup {
	
	private Map<DCElement, String> data = new HashMap<DCElement, String>(15);
	
	/**
	 * if content == null the saved content is ""
	 * @param dc DCElement
	 * @param content String
	 * @throws NullPointerException if dc == null
	 */
	public void setContent(DCElement dc, String content) {
		if (dc == null) throw new NullPointerException();
		if (content == null) content = "";
		data.put(dc, content);
	}

	/**
	 * if content == null the returned content is ""
	 * @param dc DCElement
	 * @throws NullPointerException if dc == null
	 */
	public String getContent(DCElement dc) {
		if (dc == null) throw new NullPointerException();
		String result = (String) data.get(dc);
		if (result == null) result = "";
		return result;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof DCMarkup)) return false;
		Iterator<DCElement> iter = DCElement.getIterator();
		while (iter.hasNext()) {
			DCElement dc = iter.next();
			if (!((DCMarkup) obj).getContent(dc).equalsIgnoreCase(this.getContent(dc))) return false;
		}
		return true;
	}
	
	/**
	 * Returns a new instance of DCMarkup which with the same values
	 * as this instance.
	 */
	public Object clone() {
		DCMarkup clonedDC = new DCMarkup();
		Iterator<DCElement> iter = data.keySet().iterator();
		while (iter.hasNext()) {
			DCElement dc = iter.next();
			clonedDC.setContent(dc, new String(getContent(dc)));
		}
		return clonedDC;
	}
	
	public boolean isEmpty() {
		return data.isEmpty();
	}

}