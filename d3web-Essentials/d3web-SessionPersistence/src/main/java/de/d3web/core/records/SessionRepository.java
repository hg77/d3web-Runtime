/*
 * Copyright (C) 2011 denkbares GmbH
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
package de.d3web.core.records;

import java.util.Collection;
import java.util.Iterator;

import de.d3web.core.records.filter.Filter;

/**
 * This interface specifies the methods required for all SessionRepository
 * implementations.
 * 
 * A SessionRepository stores multiple {@link DefaultSessionRecord} instances.
 * SessionRecords can be added and removed directly.
 * 
 * The stored {@link DefaultSessionRecord} instances are accessible via an
 * {@link Iterator}.
 * 
 * @author Sebastian Furth (denkbares GmbH)
 * 
 */
public interface SessionRepository {

	/**
	 * Tries to add the specified {@link DefaultSessionRecord} to this
	 * SessionRepository.
	 * 
	 * If the SessionRecord was successfully added, the true is returned.
	 * Otherwise the returned value will be false.
	 * 
	 * If you try to add null, an {@link IllegalArgumentException} will be
	 * thrown.
	 * 
	 * @param sessionRecord the SessionRecord which will be added (null
	 *        is not allowed)
	 * @return true, if the SessionRecord was added to the SessionRepository;
	 *         false otherwise
	 */
	boolean add(SessionRecord sessionRecord);

	/**
	 * Tries to remove the specified {@link DefaultSessionRecord} from this
	 * SessionRepository.
	 * 
	 * If the SessionRecord was successfully removed, then true will be
	 * returned. Otherwise the returned value will be false.
	 * 
	 * If you try to remove null, then an {@link IllegalArgumentException} will
	 * be thrown.
	 * 
	 * @param sessionRecord the SessionRecord to be removed (null is not
	 *        allowed).
	 * @return true, if the SessionRecord was removed from the
	 *         SessionRepository, false otherwise.
	 */
	boolean remove(SessionRecord sessionRecord);

	/**
	 * Returns an {@link Iterator} instance, that offers access to the
	 * {@link SessionRecord} instances stored in this SessionRepository.
	 * 
	 * @return the Iterator which offers access to the stored SessionRecords.
	 */
	Iterator<SessionRecord> iterator();

	/**
	 * Returns the number of {@link SessionRecord}s currently stored in this
	 * SessionRepository.
	 * 
	 * @created 07.11.2012
	 * @return the number of session records
	 */
	int size();

	/**
	 * Traverses the SessionRepository for a {@link SessionRecord} with the
	 * specified unique identifier. If a SessionRecord with this identifier was
	 * found, the it will be returned. Otherwise the returned value will be
	 * null.
	 * 
	 * @param id the specified identifier of the desired SessionRecord
	 * @return the SessionRecord with the specified ID if it exists, otherwise
	 *         null.
	 */
	SessionRecord getSessionRecordById(String id);

	/**
	 * Returns all SessionRecords that are matched by the filter
	 * 
	 * @created 08.03.2011
	 * @param filter {@link Filter}
	 * @return Collection of matching SessionRecords
	 */
	Collection<SessionRecord> getSessionRecords(Filter filter);

}
