package io.redlink.sdk.impl.vocabulary.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

public class Entity {
	private static final String NON_LANGUAGE = "NONE";

	/**
	 * Entity URI
	 */
	private String uri;

	/**
	 * Properties' -> <Property URI, Multimap<Language, Property Value>>
	 */
	private Map<String, Multimap<String, String>> properties;

	public Entity() {
		this.properties = Maps.newHashMap();
	}

	public Entity(String uri) {
		this();
		this.uri = uri;
	}

	/**
	 * 
	 * @return
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * 
	 * @param uri
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}

	/**
	 * 
	 * @param property
	 * @param value
	 */
	public void addPropertyValue(String property, String value) {
		Multimap<String, String> entry = properties.get(property);
		if (entry == null) {
			entry = HashMultimap.create();
			properties.put(property, entry);
		}
		entry.put(NON_LANGUAGE, value);
	}

	/**
	 * 
	 * @param property
	 * @param language
	 * @param value
	 */
	public void addPropertyValue(String property, String language, String value) {
		Multimap<String, String> entry = properties.get(property);
		if (entry == null) {
			entry = HashMultimap.create();
			properties.put(property, entry);
		}
		entry.put(language, value);
	}

	/**
	 * 
	 * @param property
	 * @return
	 */
	public Collection<String> getValues(String property) {
		Multimap<String, String> values = properties.get(property);
		if (values == null) {
			return Collections.emptyList();
		}

		return values.values();
	}

	/**
	 * 
	 * @param property
	 * @param language
	 * @return
	 */
	public String getValue(String property, String language) {
		Multimap<String, String> values = properties.get(property);
		if (values == null) {
			return null;
		}

		if (values.containsKey(language)) {
			return values.get(language).iterator().next();
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @return
	 */
	public Collection<String> getProperties() {
		return properties.keySet();
	}

	/**
	 * 
	 * @param property
	 * @return
	 */
	public String getFirstPropertyValue(String property) {
		Iterator<String> it = getValues(property).iterator();
		if (it.hasNext())
			return it.next();
		else
			return null;
	}
}
