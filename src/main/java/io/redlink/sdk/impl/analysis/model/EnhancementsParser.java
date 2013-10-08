package io.redlink.sdk.impl.analysis.model;

import io.redlink.sdk.impl.vocabulary.model.Entity;

import java.util.Collection;

/**
 * 
 * 
 * @author rafa.haro@redlink.co
 *
 */
public interface EnhancementsParser {

	/**
	 * 
	 * @return
	 * @throws EnhancementParserException
	 */
	public Enhancements createEnhancements() throws EnhancementParserException;
	
	/**
	 * 
	 * @return
	 * @throws EnhancementParserException
	 */
	public Collection<Enhancement> parseEnhancements() throws EnhancementParserException;
	
	/**
	 * 
	 * @return
	 * @throws EnhancementParserException
	 */
	public Collection<String> parseLanguages() throws EnhancementParserException;
	
	/**
	 * 
	 * @return
	 * @throws EnhancementParserException
	 */
	public Collection<TextAnnotation> parseTextAnnotations() throws EnhancementParserException;
	
	
	/**
	 * 
	 * @return
	 * @throws EnhancementParserException
	 */
	public Collection<EntityAnnotation> parseEntityAnnotations() throws EnhancementParserException;
	
	/**
	 * 
	 * @param entityUri
	 * @return
	 * @throws EnhancementParserException
	 */
	public Entity parseEntity(String entityUri) throws EnhancementParserException;
}
