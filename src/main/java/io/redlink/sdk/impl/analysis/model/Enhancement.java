package io.redlink.sdk.impl.analysis.model;

import java.util.Collection;

/**
 * 
 * @author sergio.fernandez@redlink.co
 * @author rafa.haro@redlink.co
 * 
 */
public abstract class Enhancement implements Comparable<Enhancement>
{
    // properties
    protected Double confidence = null; // http://fise.iks-project.eu/ontology/confidence
    protected Collection<Enhancement> relations = null; // http://purl.org/dc/terms/relation
    private String language = null; // http://purl.org/dc/terms/language

    public Enhancement()
    {
        
    }

    public Double getConfidence()
    {
        return confidence;
    }

    void setConfidence(Double confidence)
    {
        this.confidence = confidence;
    }

    public Collection<Enhancement> getRelations()
    {
        return relations;
    }

    void setRelations(Collection<Enhancement> relations)
    {
        this.relations = relations;
    }
    
	public String getLanguage() {
		return language;
	}

	void setLanguage(String language) {
		this.language = language;
	}

	@Override
    public int compareTo(Enhancement o)
    {
        if (this.equals(o))
            return 0;

        if (this.confidence > o.getConfidence())
            return -1;
        else if (this.confidence < o.getConfidence())
            return 1;
        else
            return 0;
    }
}
