/*
 * (c) 2013 Redlink GmbH. All rights reserved.
 */
package io.redlink.sdk.impl.analysis.model.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * RedLink's Annotations Simplified Representation POJO
 * 
 * @author rafa.haro@redlink.co
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Annotation {
	
	/*
	 * Preferred label of entity if available, or one of the labels occurring in the text otherwise 
	 */
	@XmlElement(name="label")
	private String label;
	
	/*
	 * Entity Annotation language 
	 */
	@XmlElement(name="language")
	private String language;
	
	/*
	 * Dataset where the entity belongs to
	 */
	@XmlElement(name="dataset")
	private String dataset;
	
	/*
	 * Types of the Label 
	 */
	@XmlElementWrapper(name="types")
	@XmlElement(name="type")
	private List<String> types;
	
	/*
	 * URI of the Referenced Entity 
	 */
	@XmlElement
	private String reference;
	
	/*
	 * Entity Linking Confidence Value
	 */
	@XmlElement
	private Double confidence;
	
	/*
	 * Depiction Thumbnail Image URI 
	 */
	@XmlElement
	private String thumbnail;
	
	/*
	 * Referenced Entity Description Summary
	 */
	@XmlElementWrapper(name="summaries")
	@XmlElement(name="summary")
	private List<PlainLiteral> summaries;
	
	/*
	 * Occurrences of the referenced entity in the text
	 */
	@XmlElementWrapper(name="positions")
	@XmlElement(name="position")
	private List<Position> positions;
	
	/*
	 * Entity Dereferenced Data
	 */
	@XmlElement(name="entity")
	private DerefencedEntity entity;
	
	public Annotation(){
		positions = new ArrayList<Annotation.Position>();
		types = new ArrayList<String>();
		summaries = new ArrayList<Annotation.PlainLiteral>();
		entity = new DerefencedEntity();
	}
		
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getDataset() {
		return dataset;
	}

	public void setDataset(String dataset) {
		this.dataset = dataset;
	}

	public List<String> getTypes() {
		return types;
	}


	public void setTypes(List<String> types) {
		this.types = types;
	}

	public void addType(String type){
		this.types.add(type);
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}
	
	public Double getConfidence() {
		return confidence;
	}

	public void setConfidence(Double confidence) {
		this.confidence = confidence;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public List<PlainLiteral> getSummaries() {
		return summaries;
	}

	public void setSummaries(List<PlainLiteral> summaries) {
		this.summaries = summaries;
	}

	public void addSummary(PlainLiteral summary){
		this.summaries.add(summary);
	}

	public List<Position> getPositions() {
		return positions;
	}

	public void setPositions(List<Position> positions) {
		this.positions = positions;
	}
	
	public void addPosition(Position position){
		this.positions.add(position);
	}

	public DerefencedEntity getEntity() {
		return entity;
	}

	public void setEntity(DerefencedEntity entity) {
		this.entity = entity;
	}



	/**
	 * 
	 * TextAnnotation Occurrence
	 *
	 */
	@XmlRootElement
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Position {

		/*
		 * TextAnnotation Start
		 */
		@XmlElement
		Integer start;
		
		/*
		 * TextAnnotation End 
		 */
		@XmlElement
		Integer end;
		
		/*
		 * Annotation Confidence
		 */
		@XmlElement
		Double confidence;
		
		/*
		 * TextAnnotation Surface Form
		 */
		@XmlElement
		String text;
		
		/*
		 * TextAnnotation Mention Context
		 */
		@XmlElement
		String context;
		
		/*
		 * TextAnnotation language
		 */
		String language;
		
		public Position(){
			
		}

		public Position(Integer start, Integer end, Double confidence,
				String text, String context, String language) {
			this.start = start;
			this.end = end;
			this.confidence = confidence;
			this.text = text;
			this.context = context;
		}

		public Integer getStart() {
			return start;
		}

		public void setStart(Integer start) {
			this.start = start;
		}

		public Integer getEnd() {
			return end;
		}

		public void setEnd(Integer end) {
			this.end = end;
		}

		public Double getConfidence() {
			return confidence;
		}

		public void setConfidence(Double confidence) {
			this.confidence = confidence;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		public String getContext() {
			return context;
		}

		public void setContext(String context) {
			this.context = context;
		}

		public String getLanguage() {
			return language;
		}

		public void setLanguage(String language) {
			this.language = language;
		}
	}
	
	/**
	 * 
	 * Entity's Label
	 *
	 */
	@XmlRootElement
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class PlainLiteral{
		
		/*
		 * Label's Language
		 */
		@XmlElement
		private String language;
		
		/*
		 * Label's Text Content
		 */
		@XmlElement
		private String text;
		
		public PlainLiteral(){
			
		}

		public PlainLiteral(String language, String text) {
			this.language = language;
			this.text = text;
		}

		public String getLanguage() {
			return language;
		}

		public void setLanguage(String language) {
			this.language = language;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}
	}
	
	/**
	 * 
	 * Deferenced Entity Data
	 *
	 */
	@XmlRootElement
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class DerefencedEntity{
		
		/**
		 * Properties' -> <Property URI, Multimap<Language, Property Value>>
		 */
		@XmlElement
		private Map<String, List<PlainLiteral>> properties;
		
		public DerefencedEntity(){
			properties = new HashMap<String, List<PlainLiteral>>();
		}

		public Map<String, List<PlainLiteral>> getProperties() {
			return properties;
		}

		public void setProperties(Map<String, List<PlainLiteral>> properties) {
			this.properties = properties;
		}
		
		
	}

}
