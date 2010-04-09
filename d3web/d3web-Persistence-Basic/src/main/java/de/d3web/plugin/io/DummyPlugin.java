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
package de.d3web.plugin.io;

import de.d3web.plugin.Plugin;
import de.d3web.plugin.Resource;


/**
 * Objects of this class represent plugins, which are not available.
 * This class offers the possibility, to keep the configuration of these plugins in the kb.
 * @author Markus Friedrich (denkbares GmbH)
 */
public class DummyPlugin implements Plugin {

	private String id;
	
	public DummyPlugin(String id) {
		this.id=id;
	}
	
	@Override
	public String getPluginID() {
		return id;
	}

	@Override
	public Resource[] getResources() {
		return null;
	}

}