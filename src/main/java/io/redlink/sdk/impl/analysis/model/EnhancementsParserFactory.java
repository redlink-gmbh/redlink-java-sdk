/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.redlink.sdk.impl.analysis.model;

import com.google.common.net.MediaType;
import io.redlink.sdk.analysis.AnalysisRequest.OutputFormat;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.openrdf.model.Model;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.TreeModel;
import org.openrdf.rio.*;
import org.openrdf.rio.helpers.BasicParserSettings;
import org.openrdf.rio.helpers.ContextStatementCollector;
import org.openrdf.sail.memory.model.MemValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.UUID;

/**
 * {@link EnhancementsParser} Factory. The RDF Enhancement Structure parser is the one used by default. JSON and XML parser can be also
 * created. The proper parser to be returned is automatically inferred from a {@link HttpResponse} object
 *
 * @author rafa.haro@redlink.co
 * @author rupert.westenthaler@redlink.co
 */
public final class EnhancementsParserFactory {

    private static final Logger log = LoggerFactory.getLogger(EnhancementsParserFactory.class);
    
    private static final String REDLINK = "X-Redlink-Instance";

    private static final Charset UTF8 = Charset.forName("UTF-8");

    /**
     * Create an {@link RDFStructureParser} as default {@link EnhancementsParser}. The method will try to parse an Enhancement Structure in
     * RDF format, ignoring the {@link HttpResponse} {@link MediaType}. Users need to ensure that the {@link HttpResponse} contains the Enhancement
     * Structure in that format
     *
     * @param response
     * @return
     * @throws EnhancementParserException
     */
    public static final EnhancementsParser createDefaultParser(HttpResponse response) throws EnhancementParserException, IOException {
        ParserConfig config = new ParserConfig();
        // Prevent malformed datetime values
        // TODO review - added to prevent errors when parsing invalid dates
        config.set(BasicParserSettings.VERIFY_DATATYPE_VALUES, false);
        String uri = response.getFirstHeader(REDLINK).getValue();
        if (uri == null || uri.isEmpty()) {
            uri = "urn:uuid-" + UUID.randomUUID().toString();
        }
        Model result = new TreeModel();
        //Prepare to read the response
        String charsetStr = ContentType.getOrDefault(response.getEntity()).getCharset().displayName();
        Charset charset;
        if(charsetStr == null){
            charset =  UTF8;
        } else {
            try {
                charset = Charset.forName(charsetStr);
            }catch (IllegalCharsetNameException | UnsupportedCharsetException e){
                log.warn("Unable to use charset '"+ charsetStr +"'. Will fallback to UTF-8", e);
                charset = UTF8;
            }
        }
        Reader reader = new InputStreamReader(response.getEntity().getContent(), charset);
        try {
            ValueFactory vf = new MemValueFactory();
            RDFFormat format = RDFFormat.forMIMEType(ContentType.getOrDefault(response.getEntity()).getMimeType());
            RDFParser parser = Rio.createParser(format, vf);
            parser.setParserConfig(config);
            parser.setRDFHandler(new ContextStatementCollector(result, vf));
            parser.parse(reader, uri);
        } catch (RDFHandlerException | RDFParseException e) {
            throw new EnhancementParserException("Error Parsing Analysis results" ,e);
        } catch (IOException e) {
            throw new EnhancementParserException("Unable to read Analysis response" ,e);
        } finally {
            IOUtils.closeQuietly(reader);
        }
        return new RDFStructureParser(result);
    }

    /**
     * Create an {@link EnhancementsParser} depending on the {@link HttpResponse} {@link MediaType}. Supported
     * {@link MediaType}s are enumerated in the class {@link OutputFormat}
     *
     * @param response
     * @return
     * @throws EnhancementParserException
     */
    public static final EnhancementsParser createParser(HttpResponse response) throws EnhancementParserException, IOException {
    	String type = ContentType.getOrDefault(response.getEntity()).getMimeType();
        OutputFormat format = OutputFormat.get(type);

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
