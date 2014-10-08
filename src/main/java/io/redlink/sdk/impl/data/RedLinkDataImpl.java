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
package io.redlink.sdk.impl.data;

import io.redlink.sdk.Credentials;
import io.redlink.sdk.RedLink;
import io.redlink.sdk.impl.RedLinkAbstractImpl;
import io.redlink.sdk.impl.data.model.LDPathResult;
import org.apache.marmotta.client.model.rdf.BNode;
import org.apache.marmotta.client.model.rdf.Literal;
import org.apache.marmotta.client.model.rdf.RDFNode;
import org.apache.marmotta.client.model.rdf.URI;
import org.apache.marmotta.client.model.sparql.SPARQLResult;
import org.apache.marmotta.client.util.RDFJSONParser;
import org.openrdf.model.Model;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.TreeModel;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.query.Binding;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryResultHandler;
import org.openrdf.query.QueryResultHandlerException;
import org.openrdf.query.resultio.*;
import org.openrdf.query.resultio.helpers.QueryResultCollector;
import org.openrdf.rio.*;
import org.openrdf.rio.helpers.ParseErrorLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;
import java.io.*;
import java.net.MalformedURLException;
import java.util.*;

/**
 * RedLink's {@link Data} services implementation. To be instantiated, this implementation needs a valid {@link Credentials} object that
 * must contain a RedLink API key which will be used in each request to the server.
 *
 * @author sergio.fernandez@redlink.co
 */
public class RedLinkDataImpl extends RedLinkAbstractImpl implements RedLink.Data {

    private static Logger log = LoggerFactory.getLogger(RedLinkDataImpl.class);

    public RedLinkDataImpl(Credentials credentials) {
        super(credentials);
    }

    @Override
    public boolean importDataset(Model data, String dataset) throws IOException, RDFHandlerException {
        return importDataset(data, dataset, false);
    }

    @Override
    public boolean importDataset(Model data, String dataset, boolean cleanBefore) throws RDFHandlerException, IOException {
        RDFFormat format = RDFFormat.TURTLE;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Rio.write(data, out, format);
        return importDataset(new ByteArrayInputStream(out.toByteArray()), format, dataset, cleanBefore);
    }

    @Override
    public boolean importDataset(File file, String dataset) throws FileNotFoundException {
        return importDataset(file, dataset, false);
    }

    @Override
    public boolean importDataset(File file, String dataset, boolean cleanBefore) throws FileNotFoundException {
        return importDataset(new FileInputStream(file), RDFFormat.forFileName(file.getAbsolutePath()), dataset, cleanBefore);
    }

    @Override
    public boolean importDataset(InputStream in, RDFFormat format, String dataset) {
        return importDataset(in, format, dataset, false);
    }

    @Override
    public boolean importDataset(InputStream in, RDFFormat format, String dataset, boolean cleanBefore) {
        try {
            WebTarget target = credentials.buildUrl(getDatasetUriBuilder(dataset));
            Invocation.Builder request = target.request();
            log.debug("Importing {} data into dataset {}", format.getName(), dataset);
            //this is not safe for handling large content...
            //but anyway with API-211 we're gonna provide an alternative to the rest api
            Response response;
            if (cleanBefore) {
                response = request.put(Entity.entity(in, MediaType.valueOf(format.getDefaultMIMEType())));
            } else {
                response = request.post(Entity.entity(in, MediaType.valueOf(format.getDefaultMIMEType())));
            }
            log.debug("Request resolved with {} status code: {}", response.getStatus(), response.getStatusInfo().getReasonPhrase());
            //log.debug("Worker: {}", response.getHeaderString("X-Redlink-Worker"));
            return (response.getStatus() == 200);
        } catch (MalformedURLException | IllegalArgumentException | UriBuilderException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Model exportDataset(String dataset) {
        RDFFormat format = RDFFormat.TURTLE;
        try {
            WebTarget target = credentials.buildUrl(getDatasetUriBuilder(dataset));
            Invocation.Builder request = target.request();
            request.header("Accept", format.getDefaultMIMEType());
            log.debug("Exporting {} data from dataset {}", format.getName(), dataset);
            Response response = request.get();
            log.debug("Request resolved with {} status code: {}", response.getStatus(), response.getStatusInfo().getReasonPhrase());
            if (response.getStatus() == 200) {
                ParserConfig config = new ParserConfig();
                String entity = response.readEntity(String.class);
                return Rio.parse(new StringReader(entity), target.getUri().toString(), format, config, ValueFactoryImpl.getInstance(), new ParseErrorLogger());
            } else {
                log.error("Unexpected error exporting dataset {}: request returned with {} status code", dataset, response.getStatus());
                throw new RuntimeException("Unexpected error exporting dataset");
            }
        } catch (IllegalArgumentException | UriBuilderException | IOException | RDFParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean cleanDataset(String dataset) {
        try {
            WebTarget target = credentials.buildUrl(getDatasetUriBuilder(dataset));
            Invocation.Builder request = target.request();
            log.debug("Cleaning data from dataset {}", dataset);
            Response response = request.delete();
            log.debug("Request resolved with {} status code: {}", response.getStatus(), response.getStatusInfo().getReasonPhrase());
            //log.debug("Worker: {}", response.getHeaderString("X-Redlink-Worker"));
            return (response.getStatus() == 200);
        } catch (IllegalArgumentException | UriBuilderException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Model getResource(String resource) {
        return getResource(getResourceUriBuilder(resource));
    }

    @Override
    public Model getResource(String resource, String dataset) {
        return getResource(getResourceUriBuilder(dataset, resource));
    }

    private Model getResource(UriBuilder uriBuilder) {
        RDFFormat format = RDFFormat.TURTLE;
        try {
            WebTarget target = credentials.buildUrl(uriBuilder);
            Invocation.Builder request = target.request();
            request.header("Accept", format.getDefaultMIMEType());
            log.debug("Retrieving resource as {}", format.getName());
            Response response = request.get();
            log.debug("Request resolved with {} status code: {}", response.getStatus(), response.getStatusInfo().getReasonPhrase());
            if (response.getStatus() == 200) {
                ParserConfig config = new ParserConfig();
                String entity = response.readEntity(String.class);
                return Rio.parse(new StringReader(entity), target.getUri().toString(), format, config, ValueFactoryImpl.getInstance(), new ParseErrorLogger());
            } else if (response.getStatus() == 404) {
                log.error("resource not found");
                return new TreeModel();
            } else {
                log.error("Unexpected error retrieving resource: request returned with {} status code", response.getStatus());
                throw new RuntimeException("Unexpected error retrieving resource");
            }
        } catch (IllegalArgumentException | UriBuilderException | IOException | RDFParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean importResource(String resource, Model data, String dataset) {
        return importResource(resource, data, dataset, false);
    }

    @Override
    public boolean importResource(String resource, Model data, String dataset, boolean cleanBefore) {
        RDFFormat format = RDFFormat.TURTLE;
        try {
            WebTarget target = credentials.buildUrl(getResourceUriBuilder(dataset, resource));
            Invocation.Builder request = target.request();
            log.debug("Importing resource {} into dataset {}", resource, dataset);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Rio.write(data, out, format);
            InputStream in = new ByteArrayInputStream(out.toByteArray());
            Response response;
            if (cleanBefore) {
                response = request.put(Entity.entity(in, MediaType.valueOf(format.getDefaultMIMEType())));
            } else {
                response = request.post(Entity.entity(in, MediaType.valueOf(format.getDefaultMIMEType())));
            }
            log.debug("Request resolved with {} status code: {}", response.getStatus(), response.getStatusInfo().getReasonPhrase());
            //log.debug("Worker: {}", response.getHeaderString("X-Redlink-Worker"));
            return (response.getStatus() == 200);
        } catch (IllegalArgumentException | UriBuilderException | RDFHandlerException | IOException e) {
            log.error("Error importing resource: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean deleteResource(String resource, String dataset) {
        try {
            WebTarget target = credentials.buildUrl(getResourceUriBuilder(dataset, resource));
            Invocation.Builder request = target.request();
            log.debug("Deleting resource {} from datataset {}", resource, dataset);
            Response response = request.delete();
            log.debug("Request resolved with {} status code: {}", response.getStatus(), response.getStatusInfo().getReasonPhrase());
            return (response.getStatus() == 200);
        } catch (IllegalArgumentException | UriBuilderException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SPARQLResult sparqlTupleQuery(String query) {
        try {
            WebTarget target = credentials.buildUrl(getSparqlSelectUriBuilder());
            return execTupleQuery(target, query);
        } catch (MalformedURLException | IllegalArgumentException | UriBuilderException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Deprecated
    public SPARQLResult sparqlSelect(String query) {
        return sparqlTupleQuery(query);
    }

    @Override
    public SPARQLResult sparqlTupleQuery(String query, String dataset) {
        try {
            WebTarget target = credentials.buildUrl(getSparqlSelectUriBuilder(dataset));
            return execTupleQuery(target, query);
        } catch (MalformedURLException | IllegalArgumentException | UriBuilderException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Deprecated
    public SPARQLResult sparqlSelect(String query, String dataset) {
        return sparqlTupleQuery(query, dataset);
    }

    @Override
    public Model sparqlGraphQuery(String query) {
        try {
            WebTarget target = credentials.buildUrl(getSparqlSelectUriBuilder());
            return execGraphQuery(target, query);
        } catch (MalformedURLException | IllegalArgumentException | UriBuilderException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Model sparqlGraphQuery(String query, String dataset) {
        try {
            WebTarget target = credentials.buildUrl(getSparqlSelectUriBuilder(dataset));
            return execGraphQuery(target, query);
        } catch (MalformedURLException | IllegalArgumentException | UriBuilderException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean sparqlUpdate(String query, String dataset) {
        try {
            WebTarget target = credentials.buildUrl(getSparqlUpdateUriBuilder(dataset));
            return execUpdate(target, query);
        } catch (MalformedURLException | IllegalArgumentException | UriBuilderException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public LDPathResult ldpath(String uri, String dataset, String program) {
        try {
            WebTarget target = credentials.buildUrl(getLDPathUriBuilder(dataset, uri));
            return execLDPath(target, uri, program);
        } catch (MalformedURLException | IllegalArgumentException | UriBuilderException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public LDPathResult ldpath(String uri, String program) {
        try {
            WebTarget target = credentials.buildUrl(getLDPathUriBuilder(uri));
            return execLDPath(target, uri, program);
        } catch (MalformedURLException | IllegalArgumentException | UriBuilderException e) {
            throw new RuntimeException(e);
        }
    }

    private LDPathResult execLDPath(WebTarget target, String uri, String program) {
        Invocation.Builder request = target.request();
        request.accept(MediaType.APPLICATION_JSON);
        try {
            log.debug("Executing LDpath program over resource {}", uri);
            Response response = request.post(Entity.text(program));
            log.debug("Request resolved with {} status code", response.getStatus());
            //log.debug("Worker: {}", response.getHeaderString("X-Redlink-Worker"));
            if (response.getStatus() != 200) {
                // TODO: improve this feedback from the sdk (400, 500, etc)
                throw new RuntimeException("Query failed: HTTP error code " + response.getStatus());
            } else {
                LDPathResult result = new LDPathResult();
                final Map<String, List<Map<String, String>>> fields = response.readEntity(Map.class);
                for (Map.Entry<String, List<Map<String, String>>> field : fields.entrySet()) {
                    List<RDFNode> row = new ArrayList<RDFNode>();
                    for (Map<String, String> node : field.getValue()) {
                        row.add(RDFJSONParser.parseRDFJSONNode(node));
                    }
                    result.add(field.getKey(), row);
                }
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Query execution failed: " + e.getMessage(), e);
        }
    }

    private final UriBuilder getDatasetUriBuilder(String dataset) {
        return initiateUriBuilding().path(PATH).path(dataset);
    }

    private final UriBuilder getResourceUriBuilder(String resource) {
        return initiateUriBuilding().path(PATH).path(RESOURCE).queryParam(RedLink.URI, resource);
    }

    private final UriBuilder getResourceUriBuilder(String dataset, String resource) {
        return initiateUriBuilding().path(PATH).path(dataset).path(RESOURCE).queryParam(RedLink.URI, resource);
    }

    private final UriBuilder getSparqlSelectUriBuilder() {
        return initiateUriBuilding().path(PATH).path(SPARQL);
    }

    private final UriBuilder getSparqlSelectUriBuilder(String dataset) {
        return getDatasetUriBuilder(dataset).path(SPARQL).path(SELECT);
    }

    private final UriBuilder getSparqlUpdateUriBuilder(String dataset) {
        return getDatasetUriBuilder(dataset).path(SPARQL).path(UPDATE);
    }

    private final UriBuilder getLDPathUriBuilder(String uri) {
        return initiateUriBuilding().path(PATH).path(LDPATH).queryParam(RedLink.URI, uri);
    }

    private final UriBuilder getLDPathUriBuilder(String dataset, String uri) {
        return initiateUriBuilding().path(PATH).path(dataset).path(LDPATH).queryParam(RedLink.URI, uri);
    }

    private SPARQLResult execTupleQuery(WebTarget target, String query) {
        Invocation.Builder request = target.request();
        TupleQueryResultFormat format = TupleQueryResultFormat.JSON;
        request.accept(format.getDefaultMIMEType());
        try {
            log.debug("Executing SPARQL tuple query: {}", query.replaceAll("\\s*[\\r\\n]+\\s*", " ").trim());
            Response response = request.post(Entity.text(query));
            log.debug("Request resolved with {} status code", response.getStatus());
            //log.debug("Worker: {}", response.getHeaderString("X-Redlink-Worker"));
            if (response.getStatus() != 200) {
                // TODO: improve this feedback from the sdk (400, 500, etc)
                throw new RuntimeException("Query failed: HTTP error code " + response.getStatus());
            } else {
                QueryResultCollector results = new QueryResultCollector();
                parse(response.readEntity(String.class), format, results, ValueFactoryImpl.getInstance());
                if (!results.getHandledTuple() || results.getBindingSets().isEmpty()) {
                    return new SPARQLResult(new LinkedHashSet<String>());
                } else {
                    List<String> fieldNames = results.getBindingNames();

                    //TODO: find sesame classes for removing this code
                    SPARQLResult result = new SPARQLResult(new LinkedHashSet<String>(fieldNames));

                    for (BindingSet nextRow : results.getBindingSets()) {
                        Map<String, RDFNode> row = new HashMap<String, RDFNode>();

                        for (String nextBindingName : fieldNames) {
                            if (nextRow.hasBinding(nextBindingName)) {
                                Binding nextBinding = nextRow.getBinding(nextBindingName);
                                Value nodeDef = nextBinding.getValue();
                                RDFNode node = null;
                                if (nodeDef instanceof org.openrdf.model.URI) {
                                    node = new URI(nodeDef.stringValue());
                                } else if (nodeDef instanceof org.openrdf.model.BNode) {
                                    node = new BNode(((org.openrdf.model.BNode) nodeDef).getID());
                                } else if (nodeDef instanceof org.openrdf.model.Literal) {
                                    org.openrdf.model.Literal nodeLiteral = (org.openrdf.model.Literal) nodeDef;
                                    if (nodeLiteral.getLanguage() != null) {
                                        node = new Literal(nodeLiteral.getLabel(), nodeLiteral.getLanguage());
                                    } else if (nodeLiteral.getDatatype() != null) {
                                        node = new Literal(nodeLiteral.getLabel(), new URI(nodeLiteral.getDatatype().stringValue()));
                                    } else {
                                        node = new Literal(nodeLiteral.getLabel());
                                    }
                                }

                                if (node != null) {
                                    row.put(nextBindingName, node);
                                }
                            }
                        }
                        result.add(row);
                    }
                    return result;
                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
            throw new RuntimeException("Query execution failed: " + e.getMessage(), e);
        }
    }

    private Model execGraphQuery(WebTarget target, String query) {
        Invocation.Builder request = target.request();
        RDFFormat format = RDFFormat.TURTLE;
        request.accept(format.getDefaultMIMEType());
        try {
            log.debug("Executing SPARQL graph query: {}", query.replaceAll("\\s*[\\r\\n]+\\s*", " ").trim());
            Response response = request.post(Entity.text(query));
            log.debug("Request resolved with {} status code", response.getStatus());
            //log.error("Worker: {}", response.getHeaderString("X-Redlink-Worker"));
            if (response.getStatus() != 200) {
                // TODO: improve this feedback from the sdk (400, 500, etc)
                throw new RuntimeException("Query failed: HTTP error code " + response.getStatus());
            } else {
                ParserConfig config = new ParserConfig();
                String entity = response.readEntity(String.class);
                return Rio.parse(new StringReader(entity), target.getUri().toString(), format, config, ValueFactoryImpl.getInstance(), new ParseErrorLogger());
            }
        } catch (Exception e) {
            //e.printStackTrace();
            throw new RuntimeException("Query execution failed: " + e.getMessage(), e);
        }
    }

    private boolean execUpdate(WebTarget target, String query) {
        Invocation.Builder request = target.request();
        try {
            log.debug("Executing SPARQL update query: {}", query.replaceAll("\\s*[\\r\\n]+\\s*", " ").trim());
            Response response = request.post(Entity.entity(query, new MediaType("application", "sparql-update")));
            log.debug("Request resolved with {} status code", response.getStatus());
            //log.debug("Worker: {}", response.getHeaderString("X-Redlink-Worker"));
            return (response.getStatus() == 200);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Query execution failed: " + e.getMessage(), e);
        }
    }

    private void parse(InputStream in, TupleQueryResultFormat format, QueryResultHandler handler, ValueFactory valueFactory) throws IOException, QueryResultParseException, QueryResultHandlerException, UnsupportedQueryResultFormatException {
        QueryResultParser parser = QueryResultIO.createParser(format);
        parser.setValueFactory(valueFactory);
        parser.setQueryResultHandler(handler);
        parser.parseQueryResult(in);
    }

    private void parse(String str, TupleQueryResultFormat format, QueryResultHandler handler, ValueFactory valueFactory) throws IOException, QueryResultParseException, QueryResultHandlerException, UnsupportedQueryResultFormatException {
        parse(new ByteArrayInputStream(str.getBytes("UTF-8")), format, handler, valueFactory);
    }

}
