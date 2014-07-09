package io.redlink.sdk.impl.analysis.model;

import com.google.common.base.Optional;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Represent a Dereferenced Entity from a RedLink Dataset. Encapsulates all the entity properties
 *
 * @author rafa.haro@redlink.co
 */
public class Entity {

    /**
     * Entity URI
     */
    private String uri;
    
    /**
     * Entity Dataset
     */
    private String dataset;

    /**
     * Properties' -> <Property URI, Multimap<Language, Property Value>>
     */
    private Map<String, Multimap<Optional<String>, String>> properties;

    public Entity() {
        this.properties = Maps.newHashMap();
    }

    public Entity(String uri, String dataset) {
        this();
        this.uri = uri;
        this.dataset = dataset;
    }

    /**
     * Returns the Entity URI
     *
     * @return
     */
    public String getUri() {
        return uri;
    }

    void setUri(String uri) {
        this.uri = uri;
    }
    
    /**
     * Returns the Entity Dataset
     * 
     * @return
     */
    public String getDataset() {
		return dataset;
	}

	void setDataset(String dataset) {
		this.dataset = dataset;
	}

	void addPropertyValue(String property, String value) {

        if (property != null && value != null) {
            Multimap<Optional<String>, String> entry = properties.get(property);

            if (entry == null) {
                entry = HashMultimap.create();
                properties.put(property, entry);
            }
            Optional<String> nullable = Optional.absent();
            entry.put(nullable, value);
        }
    }

    void addPropertyValue(String property, String language, String value) {
        if (language == null)
            addPropertyValue(property, value);

        if (property != null && value != null) {
            Multimap<Optional<String>, String> entry = properties.get(property);
            if (entry == null) {
                entry = HashMultimap.create();
                properties.put(property, entry);
            }
            entry.put(Optional.of(language), value);
        }
    }

    /**
     * Get all literal values for the property passed by parameter
     *
     * @param property Property URI
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
     * Get a collection of pairs <language,value> for the property passed by parameter
     *
     * @param property Property URI
     * @return
     */
    public Multimap<String, String> getValuesByLanguage(String property) {
        Multimap<String, String> result = HashMultimap.create();
        if (properties.containsKey(property)) {
            Multimap<Optional<String>, String> values = properties.get(property);
            for (Entry<Optional<String>, String> entry : values.entries())
                if (entry.getKey().isPresent())
                    result.put(entry.getKey().get(), entry.getValue());
        }
        return result;
    }

    /**
     * Get a literal value for a property and language passed by parameters
     *
     * @param property Property URI
     * @param language Language code
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
     * Get the {@link Collection} of entity's properties
     *
     * @return
     */
    public Collection<String> getProperties() {
        return properties.keySet();
    }

    /**
     * Return the first value associated to the property passed by parameter
     *
     * @param property Property URI
     * @return
     */
    public String getFirstPropertyValue(String property) {
        Iterator<String> it = getValues(property).iterator();
        return it.hasNext() ? it.next() : null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((uri == null) ? 0 : uri.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Entity other = (Entity) obj;
        if (uri == null) {
            if (other.uri != null)
                return false;
        } else if (!uri.equals(other.uri))
            return false;
        return true;
    }
}
