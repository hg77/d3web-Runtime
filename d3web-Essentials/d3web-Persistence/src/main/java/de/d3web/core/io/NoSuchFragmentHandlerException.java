/*
 * Copyright (C) 2009 denkbares GmbH
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
package de.d3web.core.io;

import java.io.IOException;

/**
 * This Exception is thrown, when a fragment cannot be parsed because their is
 * no fragment handler, who can handle it
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class NoSuchFragmentHandlerException extends IOException {

	private static final long serialVersionUID = -2682313594826456739L;

	public NoSuchFragmentHandlerException(String message) {
		super(message);
	}
}
