package io.redlink.sdk.impl.analysis.model;

import java.util.Collection;

/**
 * 
 * 
 * @author rafa.haro@redlink.co
 *
 */
public abstract class EnhancementsParser {

	/**
	 * 
	 * @return
	 * @throws EnhancementParserException
	 */
	public final Enhancements createEnhancements() throws EnhancementParserException{
		Enhancements enhancements = new Enhancements();
		enhancements.setEnhancements(parseEnhancements());
		enhancements.setLanguages(parseLanguages());
		// Categories
				// TODO Write classes for topic annotations
		return enhancements;
	}
	
	/**
	 * 
	 * @return
	 * @throws EnhancementParserException
	 */
	public abstract Collection<Enhancement> parseEnhancements() throws EnhancementParserException;
	
	/**
	 * 
	 * @return
	 * @throws EnhancementParserException
	 */
	public abstract Collection<String> parseLanguages() throws EnhancementParserException;
	
	/**
	 * 
	 * @return
	 * @throws EnhancementParserException
	 */
	public abstract Collection<TextAnnotation> parseTextAnnotations() throws EnhancementParserException;
	
	
	/**
	 * 
	 * @return
	 * @throws EnhancementParserException
	 */
	public abstract Collection<EntityAnnotation> parseEntityAnnotations() throws EnhancementParserException;
	
	/**
	 * 
	 * @param entityUri
	 * @return
	 * @throws EnhancementParserException
	 */
	public abstract Entity parseEntity(String entityUri) throws EnhancementParserException;
}
