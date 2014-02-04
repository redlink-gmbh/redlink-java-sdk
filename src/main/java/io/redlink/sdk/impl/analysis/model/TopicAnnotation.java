package io.redlink.sdk.impl.analysis.model;


/**
 * TopicAnnotation are used to categorize/classify the parsed content along some categorization system
 *
 * @author rafa.haro@redlink.co
 * @see https://stanbol.apache.org/docs/trunk/components/enhancer/enhancementstructure#fisetopicannotation
 */
public class TopicAnnotation extends Enhancement {

    // properties
    private String topicLabel = null; // http://fise.iks-project.eu/ontology/entity-label
    private String topicReference = null; // http://fise.iks-project.eu/ontology/entity-reference
    private String dataset = null; // http://stanbol.apache.org/ontology/entityhub/entityhub#"
    private String summary = null; // Entity (Concept) Description

    /**
     * Returns the category preferred label
     *
     * @return
     */
    public String getTopicLabel() {
        return topicLabel;
    }

    void setTopicLabel(String topicLabel) {
        this.topicLabel = topicLabel;
    }

    /**
     * Returns the URI of the category
     *
     * @return
     */
    public String getTopicReference() {
        return topicReference;
    }

    void setTopicReference(String topicReference) {
        this.topicReference = topicReference;
    }

    /**
     * Returns the name of the dataset which the category belongs to
     *
     * @return
     */
    public String getDataset() {
        return dataset;
    }

    void setDataset(String dataset) {
        this.dataset = dataset;
    }

    /**
     * Returns a description of the category
     *
     * @return
     */
    public String getSummary() {
        return summary;
    }

    void setSummary(String summary) {
        this.summary = summary;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((topicReference == null) ? 0 : topicReference.hashCode());
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
        TopicAnnotation other = (TopicAnnotation) obj;
        if (topicReference == null) {
            if (other.topicReference != null)
                return false;
        } else if (!topicReference.equals(other.topicReference))
            return false;
        return true;
    }
}
