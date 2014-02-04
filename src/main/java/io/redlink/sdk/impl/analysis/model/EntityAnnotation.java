package io.redlink.sdk.impl.analysis.model;

import java.util.Collection;

/**
 * Entity annotation, suggested/linked entities recognized within the text
 *
 * @author sergio.fernandez@redlink.co
 * @author rafa.haro@redlink.co
 * @see https://stanbol.apache.org/docs/trunk/components/enhancer/enhancementstructure#fiseentityannotation
 */
public final class EntityAnnotation extends Enhancement {

    // properties
    private String entityLabel = null; // http://fise.iks-project.eu/ontology/entity-label
    private Entity entityReference = null; // http://fise.iks-project.eu/ontology/entity-reference
    private Collection<String> entityTypes = null; // http://fise.iks-project.eu/ontology/entity-type
    private String dataset = null; // http://stanbol.apache.org/ontology/entityhub/entityhub#"

    public EntityAnnotation() {
        super();
    }

    /**
     * Returns the preferred entity label
     *
     * @return
     */
    public String getEntityLabel() {
        return entityLabel;
    }

    void setEntityLabel(String entityLabel) {
        this.entityLabel = entityLabel;
    }

    /**
     * Returns the dereferenced {@link Entity} associated to the {@link EntityAnnotation}
     *
     * @return
     */
    public Entity getEntityReference() {
        return entityReference;
    }

    void setEntityReference(Entity entityReference) {
        this.entityReference = entityReference;
    }

    /**
     * Returns the {@link Collection} of types associated to the entity
     *
     * @return
     */
    public Collection<String> getEntityTypes() {
        return entityTypes;
    }

    void setEntityTypes(Collection<String> entityTypes) {
        this.entityTypes = entityTypes;
    }

    /**
     * Returns the name of the dataset which the entity has been linked with
     *
     * @return
     */
    public String getDataset() {
        return dataset;
    }

    void setDataset(String dataset) {
        this.dataset = dataset;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((entityReference == null) ? 0 : entityReference.hashCode());
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
        EntityAnnotation other = (EntityAnnotation) obj;
        if (entityReference == null) {
            if (other.entityReference != null)
                return false;
        } else if (!entityReference.equals(other.entityReference))
            return false;
        return true;
    }
}
