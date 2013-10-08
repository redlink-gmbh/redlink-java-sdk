package io.redlink.sdk.impl.analysis.model;

import org.openrdf.model.Model;

//TODO Implement a real factory -> decoupled first the Service Response (RDF/XML) from OpenRDF Model Representation

/**
 * 
 * 
 * @author rafaharo
 *
 */
public final class EnhancementsParserFactory {

	public static final EnhancementsParser createDefaultParser(Model model) throws EnhancementParserException{
		return new OpenRDFEnhancementsParser(model);
	}
}
