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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.redlink.sdk.Credentials;
import io.redlink.sdk.RedLink;
import io.redlink.sdk.impl.RedLinkAbstractImpl;
import io.redlink.sdk.impl.data.model.LDPathResult;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import io.redlink.sdk.util.UriBuilder;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.marmotta.client.model.rdf.BNode;
import org.apache.marmotta.client.model.rdf.Literal;
import org.apache.marmotta.client.model.rdf.RDFNode;
import org.apache.marmotta.client.model.rdf.URI;
import org.apache.marmotta.client.model.sparql.SPARQLResult;
import org.apache.marmotta.client.util.RDFJSONParser;
import org.openrdf.model.Model;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.query.Binding;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryResultHandler;
import org.openrdf.query.QueryResultHandlerException;
import org.openrdf.query.resultio.QueryResultIO;
import org.openrdf.query.resultio.QueryResultParseException;
import org.openrdf.query.resultio.QueryResultParser;
import org.openrdf.query.resultio.TupleQueryResultFormat;
import org.openrdf.query.resultio.UnsupportedQueryResultFormatException;
import org.openrdf.query.resultio.helpers.QueryResultCollector;
import org.openrdf.rio.ParserConfig;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.ParseErrorLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RedLink's {@link Data} services implementation. To be instantiated, this implementation needs a valid {@link Credentials} object that
 * must contain a RedLink API key which will be used in each request to the server.
 *
 * @author sergio.fernandez@redlink.co
 */
public class RedLinkDataImpl extends RedLinkAbstractImpl implements RedLink.Data {

    private static final long serialVersionUID = 4326250513301297236L;

    private static Logger log = LoggerFactory.getLogger(RedLinkDataImpl.class);

    public RedLinkDataImpl(Credentials credentials) {
        super(credentials);
    }

    @Override
    public boolean importDataset(Model data, String dataset) throws RDFHandlerException {
        return importDataset(data, dataset, false);
    }

    @Override
    public boolean importDataset(Model data, String dataset, boolean cleanBefore) throws RDFHandlerException {
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
        log.debug("Importing {} data into dataset {}", format.getName(), dataset);
        try {
            java.net.URI target = credentials.buildUrl(getDatasetUriBuilder(dataset));
            CloseableHttpResponse response;
            if (cleanBefore) {
                response = client.put(target, in, format);
            } else {
                response = client.post(target, in, format);
            }
            try {
                log.debug("Request resolved with {} status code: {}", response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase());
                return (response.getStatusLine().getStatusCode() == 200);
            } finally {
                response.close();
            }
        } catch (IllegalArgumentException | URISyntaxException | IOException e) {
            log.error("Error importing dataset: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Model exportDataset(String dataset) {
        RDFFormat format = RDFFormat.TURTLE;
        try {
            final java.net.URI target = credentials.buildUrl(getDatasetUriBuilder(dataset));
            log.debug("Exporting {} data from dataset {}", format.getName(), dataset);
            final String entity = client.get(target, format.getDefaultMIMEType());
            return Rio.parse(new StringReader(entity), target.toString(), format, new ParserConfig(), ValueFactoryImpl.getInstance(), new ParseErrorLogger());
        } catch (IllegalArgumentException | URISyntaxException | RDFParseException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean cleanDataset(String dataset) {
        try {
            java.net.URI target = credentials.buildUrl(getDatasetUriBuilder(dataset));
            log.debug("Cleaning data from dataset {}", dataset);
            CloseableHttpResponse response = client.delete(target);
            log.debug("Request resolved with {} status code: {}", response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase());
            try {
                final int status = response.getStatusLine().getStatusCode();
                return (status >= 200 && status < 300);
            } finally {
                response.close();
            }
        } catch (IllegalArgumentException | URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Model getResource(String resource) {
        try {
            return getResource(getResourceUriBuilder(resource));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Model getResource(String resource, String dataset) {
        try {
            return getResource(getResourceUriBuilder(dataset, resource));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private Model getResource(UriBuilder uriBuilder) {
        RDFFormat format = RDFFormat.TURTLE;
        try {
            java.net.URI target = credentials.buildUrl(uriBuilder);
            log.debug("Exporting {} data from resource {}", format.getName(), target.toString());
            String entity = client.get(target, format.getDefaultMIMEType());
            return Rio.parse(new StringReader(entity), target.toString(), format, new ParserConfig(), ValueFactoryImpl.getInstance(), new ParseErrorLogger());
        } catch (IllegalArgumentException | URISyntaxException | RDFParseException | IOException e) {
            if (e instanceof ClientProtocolException && "Unexpected response status: 404".compareTo(e.getMessage())==0) {
                //keeping old behavior, should not be silently fail (i.e. return empty model)?
                return new LinkedHashModel();
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public boolean importResource(String resource, Model data, String dataset) {
        return importResource(resource, data, dataset, false);
    }

    @Override
    public boolean importResource(String resource, Model data, String dataset, boolean cleanBefore) {
        RDFFormat format = RDFFormat.TURTLE;
        log.debug("Importing {} data for resource {} in {}", format.getName(), resource, dataset);
        try {
            java.net.URI target = credentials.buildUrl(getResourceUriBuilder(dataset, resource));
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Rio.write(data, out, format);
            InputStream in = new ByteArrayInputStream(out.toByteArray());
            CloseableHttpResponse response;
            if (cleanBefore) {
                response = client.put(target, in, format);
            } else {
                response = client.post(target, in, format);
            }
            try {
                log.debug("Request resolved with {} status code: {}", response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase());
                return (response.getStatusLine().getStatusCode() == 200);
            } finally {
                response.close();
            }
        } catch (IllegalArgumentException | URISyntaxException | RDFHandlerException | IOException e) {
            log.error("Error importing resource: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean deleteResource(String resource, String dataset) {
        try {
            java.net.URI target = credentials.buildUrl(getResourceUriBuilder(dataset, resource));
            log.debug("Cleaning data from resource {} in {}", resource, dataset);
            CloseableHttpResponse response = client.delete(target);
            log.debug("Request resolved with {} status code: {}", response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase());
            try {
                final int status = response.getStatusLine().getStatusCode();
                return (status >= 200 && status < 300);
            } finally {
                response.close();
            }
        } catch (IllegalArgumentException | URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SPARQLResult sparqlTupleQuery(String query) {
        try {
            java.net.URI target = credentials.buildUrl(getSparqlSelectUriBuilder());
            return execTupleQuery(target, query);
        } catch (MalformedURLException | IllegalArgumentException | URISyntaxException e) {
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
            java.net.URI target = credentials.buildUrl(getSparqlSelectUriBuilder(dataset));
            return execTupleQuery(target, query);
        } catch (MalformedURLException | IllegalArgumentException | URISyntaxException e) {
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
            java.net.URI target = credentials.buildUrl(getSparqlSelectUriBuilder());
            return execGraphQuery(target, query);
        } catch (MalformedURLException | IllegalArgumentException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Model sparqlGraphQuery(String query, String dataset) {
        try {
            java.net.URI target = credentials.buildUrl(getSparqlSelectUriBuilder(dataset));
            return execGraphQuery(target, query);
        } catch (MalformedURLException | IllegalArgumentException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean sparqlUpdate(String query, String dataset) {
        try {
            java.net.URI target = credentials.buildUrl(getSparqlUpdateUriBuilder(dataset));
            return execUpdate(target, query);
        } catch (MalformedURLException | IllegalArgumentException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public LDPathResult ldpath(String uri, String dataset, String program) {
        try {
            java.net.URI target = credentials.buildUrl(getLDPathUriBuilder(dataset, uri));
            return execLDPath(target, uri, program);
        } catch (MalformedURLException | IllegalArgumentException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public LDPathResult ldpath(String uri, String program) {
        try {
            java.net.URI target = credentials.buildUrl(getLDPathUriBuilder(uri));
            return execLDPath(target, uri, program);
        } catch (MalformedURLException | IllegalArgumentException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean release(String dataset) {
        try {
            java.net.URI target = credentials.buildUrl(getReleaseUriBuilder(dataset));
            log.debug("Releasing dataset {}", dataset);
            CloseableHttpResponse response = client.post(target, "application/json");
            log.debug("Request resolved with {} status code: {}", response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase());
            try {
                final int status = response.getStatusLine().getStatusCode();
                return (status >= 200 && status < 300);
            } finally {
                response.close();
            }
        } catch (IllegalArgumentException | URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final UriBuilder getDatasetUriBuilder(String dataset) throws URISyntaxException {
        return initiateUriBuilding().path(PATH).path(dataset);
    }

    private final UriBuilder getResourceUriBuilder(String resource) throws URISyntaxException {
        return initiateUriBuilding().path(PATH).path(RESOURCE).queryParam(RedLink.URI, resource);
    }

    private final UriBuilder getResourceUriBuilder(String dataset, String resource) throws URISyntaxException {
        return initiateUriBuilding().path(PATH).path(dataset).path(RESOURCE).queryParam(RedLink.URI, resource);
    }

    private final UriBuilder getSparqlSelectUriBuilder() throws URISyntaxException {
        return initiateUriBuilding().path(PATH).path(SPARQL);
    }

    private final UriBuilder getSparqlSelectUriBuilder(String dataset) throws URISyntaxException {
        return getDatasetUriBuilder(dataset).path(SPARQL).path(SELECT);
    }

    private final UriBuilder getSparqlUpdateUriBuilder(String dataset) throws URISyntaxException {
        return getDatasetUriBuilder(dataset).path(SPARQL).path(UPDATE);
    }

    private final UriBuilder getLDPathUriBuilder(String uri) throws URISyntaxException {
        return initiateUriBuilding().path(PATH).path(LDPATH).queryParam(RedLink.URI, uri);
    }

    private final UriBuilder getLDPathUriBuilder(String dataset, String uri) throws URISyntaxException {
        return initiateUriBuilding().path(PATH).path(dataset).path(LDPATH).queryParam(RedLink.URI, uri);
    }

    private final UriBuilder getReleaseUriBuilder(String dataset) throws URISyntaxException {
        return initiateUriBuilding().path(PATH).path(dataset).path(RELEASE);
    }

    private SPARQLResult execTupleQuery(java.net.URI target, String query) {
        try {
            log.debug("Executing SPARQL tuple query: {}", query.replaceAll("\\s*[\\r\\n]+\\s*", " ").trim());
            TupleQueryResultFormat format = TupleQueryResultFormat.JSON;
            CloseableHttpResponse response = client.post(target, query, format.getDefaultMIMEType());
            final int status = response.getStatusLine().getStatusCode();
            log.debug("Request resolved with {} status code: {}", status, response.getStatusLine().getReasonPhrase());
            try {
                if (status >= 200 && status < 300) {
                    QueryResultCollector results = new QueryResultCollector();
                    parse(EntityUtils.toString(response.getEntity()), format, results, ValueFactoryImpl.getInstance());
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
                } else {
                    // TODO: improve this feedback from the sdk (400, 500, etc)
                    throw new RuntimeException("Query failed: HTTP error code " + status + ": " + response.getStatusLine().getReasonPhrase());
                }
            } catch (QueryResultParseException | QueryResultHandlerException e) {
                log.error("Error parsing query results: {}", e.getMessage(), e);
                throw new RuntimeException(e);
            } finally {
                response.close();
            }
        } catch (IllegalArgumentException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Model execGraphQuery(java.net.URI target, String query) {
        try {
            log.debug("Executing SPARQL tuple query: {}", query.replaceAll("\\s*[\\r\\n]+\\s*", " ").trim());
            RDFFormat format = RDFFormat.TURTLE;
            CloseableHttpResponse response = client.post(target, query, format.getDefaultMIMEType(),SPARQL_QUERY_MIME_TYPE);
            final int status = response.getStatusLine().getStatusCode();
            log.debug("Request resolved with {} status code: {}", status, response.getStatusLine().getReasonPhrase());
            try {
                if (status >= 200 && status < 300) {
                    String entity = EntityUtils.toString(response.getEntity());
                    return Rio.parse(new StringReader(entity), target.toString(), RDFFormat.TURTLE, new ParserConfig(), ValueFactoryImpl.getInstance(), new ParseErrorLogger());
                } else {
                    // TODO: improve this feedback from the sdk (400, 500, etc)
                    throw new RuntimeException("Query failed: HTTP error code " + status + ": " + response.getStatusLine().getReasonPhrase());
                }
            } catch (RDFParseException e) {
                log.error("Error parsing query results: {}", e.getMessage(), e);
                throw new RuntimeException(e);
            } finally {
                response.close();
            }
        } catch (IllegalArgumentException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean execUpdate(java.net.URI target, String query) {
        try {
            log.debug("Executing SPARQL update query: {}", query.replaceAll("\\s*[\\r\\n]+\\s*", " ").trim());
            CloseableHttpResponse response = client.post(target, query, "application/json", "application/sparql-update");
            log.debug("Request resolved with {} status code: {}", response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase());
            try {
                final int status = response.getStatusLine().getStatusCode();
                return (status >= 200 && status < 300);
            } finally {
                response.close();
            }
        } catch (IllegalArgumentException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private LDPathResult execLDPath(java.net.URI target, String uri, String program) {
        try {
            log.debug("Executing LDpath program over resource {}", uri);
            CloseableHttpResponse response = client.post(target, program, "application/json");
            final int status = response.getStatusLine().getStatusCode();
            log.debug("Request resolved with {} status code: {}", status, response.getStatusLine().getReasonPhrase());
            try {
                if (status >= 200 && status < 300) {
                    LDPathResult result = new LDPathResult();
                    final Map<String, List<Map<String, String>>> fields = (new ObjectMapper()).readValue(response.getEntity().getContent(), Map.class);
                    for (Map.Entry<String, List<Map<String, String>>> field : fields.entrySet()) {
                        List<RDFNode> row = new ArrayList<>();
                        for (Map<String, String> node : field.getValue()) {
                            row.add(RDFJSONParser.parseRDFJSONNode(node));
                        }
                        result.add(field.getKey(), row);
                    }
                    return result;
                } else {
                    // TODO: improve this feedback from the sdk (400, 500, etc)
                    throw new RuntimeException("Query failed: HTTP error code " + status);
                }
            } finally {
                response.close();
            }
        } catch (IllegalArgumentException | IOException e) {
            throw new RuntimeException(e);
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
