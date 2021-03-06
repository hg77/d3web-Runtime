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

package de.d3web.core.knowledge;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import de.d3web.core.knowledge.terminology.info.Property;
import com.denkbares.utils.Pair;
import com.denkbares.utils.Triple;

public class DefaultInfoStore implements InfoStore {

	private static final String KEY_MUST_NOT_BE_NULL = "The key must not be null.";

	private final Map<Pair<Property<?>, Locale>, Object> entries =
			new HashMap<>();

	@Override
	public Collection<Triple<Property<?>, Locale, Object>> entries() {
		Collection<Triple<Property<?>, Locale, Object>> result = new LinkedList<>();
		for (Entry<Pair<Property<?>, Locale>, Object> entry : this.entries.entrySet()) {
			result.add(new Triple<>(
					entry.getKey().getA(),
					entry.getKey().getB(),
					entry.getValue()));
		}
		return result;
	}

	@Override
	public <StoredType> Map<Locale, StoredType> entries(Property<StoredType> key) {
		Map<Locale, StoredType> result = new HashMap<>();
		for (Entry<Pair<Property<?>, Locale>, Object> entry : this.entries.entrySet()) {
			if (entry.getKey().getA() == key) {
				result.put(entry.getKey().getB(), key.castToStoredValue(entry.getValue()));
			}
		}
		return result;
	}

	@Override
	public <StoredType> StoredType getValue(Property<StoredType> key) {
		if (key == null) {
			throw new NullPointerException(KEY_MUST_NOT_BE_NULL);
		}
		// if not available check for entry with no language
		StoredType value = getEntry(key, NO_LANGUAGE);
		if (value != null) {
			return value;
		}
		// if not available use default value or null
		return key.getDefaultValue();
	}

	@Override
	public <StoredType> StoredType getValue(Property<StoredType> key, Locale language) {
		if (key == null) throw new NullPointerException(KEY_MUST_NOT_BE_NULL);
		if (language != null) {
			// check for entry
			StoredType value = getEntry(key, language);
			if (value != null) {
				return value;
			}
			// Try to find the locale without variant
			String variant = language.getVariant();
			String country = language.getCountry();
			if (variant != null && !variant.isEmpty()) {
				value = getEntry(key, new Locale(language.getLanguage(), country));
				if (value != null) {
					return value;
				}
			}
			// Try to find the locale without the country
			if (country != null && !country.isEmpty()) {
				value = getEntry(key, new Locale(language.getLanguage()));
				if (value != null) {
					return value;
				}
			}
		}
		// if not available use no-language method
		return getValue(key);
	}

	private <StoredType> StoredType getEntry(Property<StoredType> key, Locale language) {
		Object object = this.entries.get(createEntryKey(key, language));
		return key.getStoredClass().cast(object);
	}

	private <StoredType> Pair<Property<?>, Locale> createEntryKey(Property<StoredType> key, Locale language) {
		return new Pair<>(key, language);
	}

	@Override
	public boolean remove(Property<?> key) {
		if (key == null) throw new NullPointerException(KEY_MUST_NOT_BE_NULL);
		return remove(key, NO_LANGUAGE);
	}

	@Override
	public boolean remove(Property<?> key, Locale language) {
		if (key == null) throw new NullPointerException(KEY_MUST_NOT_BE_NULL);
		return (this.entries.remove(createEntryKey(key, language)) != null);
	}

	@Override
	public boolean contains(Property<?> key) {
		return contains(key, NO_LANGUAGE);
	}

	@Override
	public boolean contains(Property<?> key, Locale language) {
		return this.entries.containsKey(createEntryKey(key, language));
	}

	@Override
	public void addValue(Property<?> key, Object value) {
		addValue(key, NO_LANGUAGE, value);
	}

	@Override
	public void addValue(Property<?> key, Locale language, Object value) {
		if (value == null) throw new NullPointerException("The value must not be null.");
		if (key == null) throw new NullPointerException(KEY_MUST_NOT_BE_NULL);
		if (language != NO_LANGUAGE && !key.isMultilingual()) {
			throw new IllegalArgumentException("The property " + key
					+ " does not support a language");
		}
		if (!key.getStoredClass().isInstance(value)) {
			throw new ClassCastException("value '" + value +
							"' is not compatible with defined storage class "
							+ key.getStoredClass());
		}
		entries.put(createEntryKey(key, language), value);
	}

	@Override
	public boolean isEmpty() {
		return entries.isEmpty();
	}

}
