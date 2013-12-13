/*
 * (c) 2013 Redlink GmbH. All rights reserved.
 */
package io.redlink.sdk.impl.analysis.model.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Enhancement Structure Simplified Representation for JSON/XML Serialization
 * 
 * @author rafa.haro@redlink.co
 *
 */
@XmlRootElement(name="enhancements")
@XmlAccessorType
public class FlatEnhancementStructure {

	/*
	 * Languages extracted by LanguageDetection Engine
	 */
	@XmlElementWrapper(name="languages")
	@XmlElement(name="language")
	private Set<String> languages;
	
	//TODO 
//	private SentimentAnnotation sentiment;
	
	/*
	 * Annotation's List
	 */
	@XmlElementWrapper(name="annotations")
	@XmlElement(name="annotation",type=Annotation.class)
	private List<Annotation> annotations;
	
	/*
	 * Category's List
	 */
	@XmlElementWrapper(name="categories")
	@XmlElement(name="category",type=Category.class)
	private Set<Category> categories;
	
	public FlatEnhancementStructure(){
		languages = new HashSet<String>();
		annotations = new ArrayList<Annotation>();
		categories = new HashSet<Category>();
	}
	
	public Collection<String> getLanguages(){
		return languages;
	}
	
	public Collection<Annotation> getAnnotations(){
		return annotations;
	}
	
	public Collection<Category> getCategories(){
		return categories;
	}
	
	public void addLanguage(String language){
		languages.add(language);
	}
	
	public void addAnnotation(Annotation annotation){
		annotations.add(annotation);
	}
	
	public void addCategory(Category category){
		categories.add(category);
	}
}