/*
 * Copyright (C) 2010 denkbares GmbH
 * 
 * This is free software for non commercial use
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 */

package de.d3web.core.knowledge;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.core.utilities.Pair;
import de.d3web.core.utilities.Triple;

public class DefaultInfoStore implements InfoStore {

	private final Map<Pair<Property<?>, Locale>, Object> entries =
			new HashMap<Pair<Property<?>, Locale>, Object>();

	@Override
	public Collection<Triple<Property<?>, Locale, Object>> entries() {
		Collection<Triple<Property<?>, Locale, Object>> result =
				new LinkedList<Triple<Property<?>, Locale, Object>>();
		for (Entry<Pair<Property<?>, Locale>, Object> entry : this.entries.entrySet()) {
			result.add(new Triple<Property<?>, Locale, Object>(
					entry.getKey().getA(),
					entry.getKey().getB(),
					entry.getValue()));
		}
		return result;
	}

	@Override
	public <StoredType> StoredType getValue(Property<StoredType> key) {
		if (key == null) throw new NullPointerException("The key must not be null.");
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
		if (key == null) throw new NullPointerException("The key must not be null.");
		// check for entry
		StoredType value = getEntry(key, language);
		if (value != null) {
			return value;
		}
		// if not available use no-language method
		return getValue(key);
	}

	private <StoredType> StoredType getEntry(Property<StoredType> key, Locale language) {
		Object object = this.entries.get(createEntryKey(key, language));
		return key.getStoredClass().cast(object);
	}

	private <StoredType> Pair<Property<?>, Locale> createEntryKey(Property<StoredType> key, Locale language) {
		return new Pair<Property<?>, Locale>(key, language);
	}

	@Override
	public boolean remove(Property<?> key) {
		if (key == null) throw new NullPointerException("The key must not be null.");
		return remove(key, NO_LANGUAGE);
	}

	@Override
	public boolean remove(Property<?> key, Locale language) {
		if (key == null) throw new NullPointerException("The key must not be null.");
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
		if (key == null) throw new NullPointerException("The key must not be null.");
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
