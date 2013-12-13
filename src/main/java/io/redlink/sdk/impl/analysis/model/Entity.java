package io.redlink.sdk.impl.analysis.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Optional;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

/**
 * 
 * @author rafa.haro@redlink.co
 *
 */
public class Entity {
	
	/**
	 * Entity URI
	 */
	private String uri;

	/**
	 * Properties' -> <Property URI, Multimap<Language, Property Value>>
	 */
	private Map<String, Multimap<Optional<String>, String>> properties;

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
		
		if(property != null && value != null){
			Multimap<Optional<String>, String> entry = properties.get(Optional.of(property));
			
			if (entry == null) {
				entry = HashMultimap.create();
				properties.put(property, entry);
			}
			Optional<String> nullable = Optional.absent();
			entry.put(nullable, value);
		}
	}

	/**
	 * 
	 * @param property
	 * @param language
	 * @param value
	 */
	public void addPropertyValue(String property, String language, String value) {
		if(language == null)
			addPropertyValue(property, value);
		
		if(property != null && value != null){
			Multimap<Optional<String>, String> entry = properties.get(property);
			if (entry == null) {
				entry = HashMultimap.create();
				properties.put(property, entry);
			}
			entry.put(Optional.of(language), value);
		}
	}

	/**
	 * 
	 * @param property
	 * @return
	 */
	public Collection<String> getValues(String property) {
		Multimap<Optional<String>, String> values = properties.get(property);
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
	public Multimap<String, String> getValuesByLanguage (String property){
		Multimap<String, String> result = HashMultimap.create();
		if(properties.containsKey(property)){
			Multimap<Optional<String>, String> values = properties.get(property);
			for(Entry<Optional<String>, String> entry:values.entries())
				if(entry.getKey().isPresent())
					result.put(entry.getKey().get(), entry.getValue());
		}
		return result;
	}

	/**
	 * 
	 * @param property
	 * @param language
	 * @return
	 */
	public String getValue(String property, String language) {
		Multimap<Optional<String>, String> values = properties.get(property);
		
		if (values == null) {
			return null;
		}

		Iterator<String> it = values.get(Optional.of(language)).iterator();
		return it.hasNext() ? it.next() : null;
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
		return it.hasNext() ? it.next() : null;
	}
}
