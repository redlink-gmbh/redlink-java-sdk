package io.redlink.sdk.impl.analysis.model;

import io.redlink.sdk.analysis.AnalysisRequest.OutputFormat;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.util.RDFInserter;
import org.openrdf.rio.*;
import org.openrdf.rio.helpers.BasicParserSettings;
import org.openrdf.sail.memory.MemoryStore;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.StringReader;
import java.util.UUID;

/**
 * {@link EnhancementsParser} Factory. The RDF Enhancement Structure parser is the one used by default. JSON and XML parser can be also
 * created. The proper parser to be returned is automatically inferred from a {@link Response} object
 *
 * @author rafa.haro@redlink.co
 */
public final class EnhancementsParserFactory {

    private static final String REDLINK = "X-Redlink-Instance";

    /**
     * Create an {@link RDFStructureParser} as default {@link EnhancementsParser}. The method will try to parse an Enhancement Structure in
     * RDF format, ignoring the {@link Response} {@link MediaType}. Users need to ensure that the {@link Response} contains the Enhancement
     * Structure in that format
     *
     * @param response
     * @return
     * @throws EnhancementParserException
     */
    public static final EnhancementsParser createDefaultParser(Response response) throws EnhancementParserException {
        String result = response.readEntity(String.class);
        ParserConfig config = new ParserConfig();
        // Prevent malformed datetime values
        // TODO review - added to prevent errors when parsing invalid dates
        config.set(BasicParserSettings.VERIFY_DATATYPE_VALUES, false);
        String uri = response.getHeaderString(REDLINK);
        if (uri == null
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
            } catch (RDFHandlerException | RepositoryException ex) {
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
     * Create an {@link EnhancementsParser} depending on the {@link Response} {@link MediaType}. Supported
     * {@link MediaType}s are enumerated in the class {@link OutputFormat}
     *
     * @param response
     * @return
     * @throws EnhancementParserException
     */
    public static final EnhancementsParser createParser(Response response) throws EnhancementParserException {
        OutputFormat format =
                OutputFormat.get(response.getMediaType().toString());
        switch (format) {
//		case XML: 
//		case JSON:
////			FlatEnhancementStructure result = 
////				response.readEntity(FlatEnhancementStructure.class);
////			return new FlatStructureParser(result);
            default:
                return createDefaultParser(response);
        }
    }
}
