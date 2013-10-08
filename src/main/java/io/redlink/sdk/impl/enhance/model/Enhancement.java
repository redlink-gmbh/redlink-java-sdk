package io.redlink.sdk.impl.enhance.model;

import java.util.Collection;
import java.util.Date;

/**
 * 
 * @author sergio.fernandez@redlink.co
 * @author rafa.haro@redlink.co
 * 
 */
public abstract class Enhancement implements Comparable<Enhancement>
{

    protected String uri = null;

    // properties
    protected Date created = null; // http://purl.org/dc/terms/created
    protected String creator = null; // http://purl.org/dc/terms/creator
    protected String language = null; // http://purl.org/dc/terms/language
    protected Double confidence = null; // http://fise.iks-project.eu/ontology/confidence
    protected String extractedFrom = null; // http://fise.iks-project.eu/ontology/extracted-from
    protected Collection<Enhancement> relations = null; // http://purl.org/dc/terms/relation

    public Enhancement()
    {
        super();
    }
    
    public Enhancement(String uri){
    	super();
    	this.uri = uri;
    }

    public String getUri()
    {
        return uri;
    }

    public void setUri(String uri)
    {
        this.uri = uri;
    }

    public Date getCreated()
    {
        return created;
    }

    public void setCreated(Date created)
    {
        this.created = created;
    }

    public String getCreator()
    {
        return creator;
    }

    public void setCreator(String creator)
    {
        this.creator = creator;
    }

    public String getLanguage()
    {
        return language;
    }

    public void setLanguage(String language)
    {
        this.language = language;
    }

    public Double getConfidence()
    {
        return confidence;
    }

    public void setConfidence(Double confidence)
    {
        this.confidence = confidence;
    }

    public String getExtractedFrom()
    {
        return extractedFrom;
    }

    public void setExtractedFrom(String extractedFrom)
    {
        this.extractedFrom = extractedFrom;
    }

    public Collection<Enhancement> getRelations()
    {
        return relations;
    }

    public void setRelations(Collection<Enhancement> relations)
    {
        this.relations = relations;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((uri == null) ? 0 : uri.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Enhancement other = (Enhancement) obj;
        if (uri == null)
        {
            if (other.uri != null)
                return false;
        }
        else if (!uri.equals(other.uri))
            return false;
        return true;
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
