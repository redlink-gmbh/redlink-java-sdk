package io.redlink.sdk.impl.enhance.model;

import java.util.Collection;

import io.redlink.sdk.impl.vocabulary.model.Entity;

/**
 * Entity annotation, suggested/linked entities recognized within the text
 * 
 * @author sergio.fernandez@redlink.co
 * @see https://stanbol.apache.org/docs/trunk/components/enhancer/enhancementstructure#fiseentityannotation
 *
 */
public final class EntityAnnotation extends Enhancement {
	
	// properties
	private String entityLabel = null; // http://fise.iks-project.eu/ontology/entity-label
	private Entity entityReference = null; // http://fise.iks-project.eu/ontology/entity-reference
	private Collection<String> entityTypes = null; // http://fise.iks-project.eu/ontology/entity-type
	private String site = null; // http://stanbol.apache.org/ontology/entityhub/entityhub#"
	
	public EntityAnnotation(String uri){
		super(uri);
	}

	public String getEntityLabel() {
		return entityLabel;
	}

	public void setEntityLabel(String entityLabel) {
		this.entityLabel = entityLabel;
	}

	public Entity getEntityReference() {
		return entityReference;
	}

	public void setEntityReference(Entity entityReference) {
		this.entityReference = entityReference;
	}

	public Collection<String> getEntityTypes() {
		return entityTypes;
	}

	public void setEntityTypes(Collection<String> entityTypes) {
		this.entityTypes = entityTypes;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}
}
