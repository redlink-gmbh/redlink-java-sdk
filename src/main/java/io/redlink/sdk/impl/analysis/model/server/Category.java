/*
 * (c) 2013 Redlink GmbH. All rights reserved.
 */
package io.redlink.sdk.impl.analysis.model.server;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * TopicAnnotation POJO
 * 
 * @author rafa.haro@redlink.co
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Category {

	/*
	 * Preferred label of the category
	 */
	@XmlElement
	private String label;
	
	/*
	 * URI of the category
	 */
	@XmlElement
	private String reference;
	
	/*
	 * Concept Summary/Description
	 */
	@XmlElement
	private String summary;
	
	/**
	 * Topic Assignment Confidence
	 */
	@XmlElement
	private Double confidence;
	
	public Category(){
		
	}

	public Category(String label, String reference, String summary,
			Double confidence) {
		this.label = label;
		this.reference = reference;
		this.summary = summary;
		this.confidence = confidence;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public Double getConfidence() {
		return confidence;
	}

	public void setConfidence(Double confidence) {
		this.confidence = confidence;
	}
}
