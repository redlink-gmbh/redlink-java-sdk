package io.redlink.sdk.impl.analysis.model;

import io.redlink.sdk.analysis.AnalysisRequest.OutputFormat;
import io.redlink.sdk.impl.analysis.model.server.FlatEnhancementStructure;

import java.io.IOException;
import java.io.StringReader;
import java.util.UUID;

import javax.ws.rs.core.Response;

import org.openrdf.model.Model;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.util.RDFInserter;
import org.openrdf.rio.*;
import org.openrdf.rio.helpers.BasicParserSettings;
import org.openrdf.rio.helpers.ParseErrorLogger;
import org.openrdf.sail.memory.MemoryStore;

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
            Repository repository = new SailRepository(new MemoryStore());
            repository.initialize();

            RepositoryConnection con = repository.getConnection();
            try {
                con.begin();

                RDFParser p = Rio.createParser(RDFFormat.forMIMEType(response.getMediaType().toString()), repository.getValueFactory());
                p.setRDFHandler(new RDFInserter(con));
                p.setParserConfig(config);
                p.parse(new StringReader(result), uri);

                con.commit();
            } catch(RDFHandlerException | RepositoryException ex) {
                con.rollback();
            } finally {
                con.close();
            }

            return new RDFStructureParser(repository);
		} catch (RepositoryException | RDFParseException | UnsupportedRDFormatException | IOException e) {
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
