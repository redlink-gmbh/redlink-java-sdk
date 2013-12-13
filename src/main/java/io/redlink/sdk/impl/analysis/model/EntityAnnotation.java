package io.redlink.sdk.impl.analysis.model;

import java.util.Collection;

/**
 * Entity annotation, suggested/linked entities recognized within the text
 * 
 * @author sergio.fernandez@redlink.co
 * @author rafa.haro@redlink.co
 * 
 * @see https://stanbol.apache.org/docs/trunk/components/enhancer/enhancementstructure#fiseentityannotation
 *
 */
public final class EntityAnnotation extends Enhancement {
	
	// properties
	private String entityLabel = null; // http://fise.iks-project.eu/ontology/entity-label
	private Entity entityReference = null; // http://fise.iks-project.eu/ontology/entity-reference
	private Collection<String> entityTypes = null; // http://fise.iks-project.eu/ontology/entity-type
	private String dataset = null; // http://stanbol.apache.org/ontology/entityhub/entityhub#"
	
	public EntityAnnotation(){
		super();
	}

	public String getEntityLabel() {
		return entityLabel;
	}

	void setEntityLabel(String entityLabel) {
		this.entityLabel = entityLabel;
	}

	public Entity getEntityReference() {
		return entityReference;
	}

	void setEntityReference(Entity entityReference) {
		this.entityReference = entityReference;
	}

	public Collection<String> getEntityTypes() {
		return entityTypes;
	}

	void setEntityTypes(Collection<String> entityTypes) {
		this.entityTypes = entityTypes;
	}

	public String getDataset() {
		return dataset;
	}

	void setDataset(String dataset) {
		this.dataset = dataset;
	}
}
