package io.redlink.sdk.impl.analysis.model;

import io.redlink.sdk.analysis.AnalysisRequest.OutputFormat;
import io.redlink.sdk.impl.analysis.model.server.FlatEnhancementStructure;

import java.io.IOException;
import java.io.StringReader;
import java.util.UUID;

import javax.ws.rs.core.Response;

import org.openrdf.model.Model;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.rio.ParserConfig;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.Rio;
import org.openrdf.rio.UnsupportedRDFormatException;
import org.openrdf.rio.helpers.BasicParserSettings;
import org.openrdf.rio.helpers.ParseErrorLogger;

/**
 * 
 * 
 * @author rafa.haro@redlink.co
 *
 */
public final class EnhancementsParserFactory {
	
	private static final String REDLINK = "X-Redlink-Instance";

	/**
	 * 
	 * 
	 * @param response
	 * @return
	 * @throws EnhancementParserException
	 */
	public static final EnhancementsParser createDefaultParser(Response response) throws EnhancementParserException{
		String result = response.readEntity(String.class);
		ParserConfig config = new ParserConfig();
		// Prevent malformed datetime values
		// TODO review - added to prevent errors when parsing invalid dates
		config.set(BasicParserSettings.VERIFY_DATATYPE_VALUES, false);
		String uri = response.getHeaderString(REDLINK);
		if(uri == null
				|| uri.isEmpty())
			uri = UUID.randomUUID().toString();
		try {
			Model model = Rio.parse(new StringReader(result), 
					uri, 
					RDFFormat.forMIMEType(response.getMediaType().toString()), 
					config, 
					ValueFactoryImpl.getInstance(), 
					new ParseErrorLogger());
			return new RDFStructureParser(model);
		} catch (RDFParseException | UnsupportedRDFormatException | IOException e) {
			throw new EnhancementParserException("Error Parsing Enhancement Structure", e);
		}
		
	}
	
	/**
	 * 
	 * @param response
	 * @return
	 * @throws EnhancementParserException 
	 */
	public static final EnhancementsParser createParser(Response response) throws EnhancementParserException{
		OutputFormat format = OutputFormat.get(response.getMediaType().toString());
		switch (format) {
		case XML: 
		case JSON:
			FlatEnhancementStructure result = 
				response.readEntity(FlatEnhancementStructure.class);
			return new FlatStructureParser(result);
		default:
			return createDefaultParser(response);
		}
	}
}
