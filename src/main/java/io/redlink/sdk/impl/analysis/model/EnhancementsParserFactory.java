package io.redlink.sdk.impl.analysis.model;

import io.redlink.sdk.analysis.AnalysisRequest;
import io.redlink.sdk.analysis.AnalysisRequest.OutputFormat;

import java.io.IOException;
import java.io.StringReader;

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
	
	private static final RDFFormat format = RDFFormat.TURTLE;

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
		try {
			Model model = Rio.parse(new StringReader(result), 
					response.getLocation().toString(), 
					format, config, 
					ValueFactoryImpl.getInstance(), 
					new ParseErrorLogger());
			return new OpenRDFEnhancementsParser(model);
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
		case XML: // TODO XML Parser
			return null;
		case JSON: // JSON Parser
			return null;
		default:
			return createDefaultParser(response);
		}
	}
}
