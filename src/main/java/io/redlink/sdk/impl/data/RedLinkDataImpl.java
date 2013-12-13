package io.redlink.sdk.impl.data;

import io.redlink.sdk.Credentials;
import io.redlink.sdk.RedLink;
import io.redlink.sdk.impl.RedLinkAbstractImpl;
import org.apache.marmotta.client.model.rdf.BNode;
import org.apache.marmotta.client.model.rdf.Literal;
import org.apache.marmotta.client.model.rdf.RDFNode;
import org.apache.marmotta.client.model.rdf.URI;
import org.apache.marmotta.client.model.sparql.SPARQLResult;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.query.Binding;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryResultHandler;
import org.openrdf.query.QueryResultHandlerException;
import org.openrdf.query.resultio.*;
import org.openrdf.query.resultio.helpers.QueryResultCollector;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class RedLinkDataImpl extends RedLinkAbstractImpl implements RedLink.Data {

    public RedLinkDataImpl(Credentials credentials) {
        super(credentials);
    }

    private final UriBuilder getSparqlSelectUriBuilder(String query, String dataset) {
        return initiateUriBuilding().path(PATH).path(dataset).path(SPARQL).path(SELECT).queryParam(QUERY, query);
    }

    private final UriBuilder getSparqlSelectUriBuilder(String query) {
        return initiateUriBuilding().path(PATH).path(SPARQL).queryParam(QUERY, query);
    }

    private SPARQLResult execSelect(WebTarget target) {
        Invocation.Builder request = target.request();
        TupleQueryResultFormat format = TupleQueryResultFormat.JSON;
        request.accept(format.getDefaultMIMEType());
        try {
            Response response = request.post(Entity.json(""));
            if (response.getStatus() != 200) {
                // TODO: improve this feedback from the sdk (400, 500, etc)
                throw new RuntimeException("Query failed: HTTP error code " + response.getStatus());
            } else {
                String entity = response.readEntity(String.class);
                QueryResultCollector results = new QueryResultCollector();
                parse(entity, format, results, ValueFactoryImpl.getInstance());
                if(!results.getHandledTuple() || results.getBindingSets().isEmpty()) {
                    return null;
                } else {
                    List<String> fieldNames = results.getBindingNames();

                    SPARQLResult result = new SPARQLResult(new LinkedHashSet<String>(fieldNames));

                    //List<?> bindings = resultMap.get("results").get("bindings");
                    for(BindingSet nextRow : results.getBindingSets()) {
                        Map<String,RDFNode> row = new HashMap<String, RDFNode>();

                        for(String nextBindingName : fieldNames) {
                            if(nextRow.hasBinding(nextBindingName)) {
                                Binding nextBinding = nextRow.getBinding(nextBindingName);
                                Value nodeDef = nextBinding.getValue();
                                RDFNode node = null;
                                if(nodeDef instanceof org.openrdf.model.URI) {
                                    node = new URI(nodeDef.stringValue());
                                } else if(nodeDef instanceof org.openrdf.model.BNode) {
                                    node = new BNode(((org.openrdf.model.BNode)nodeDef).getID());
                                } else if(nodeDef instanceof org.openrdf.model.Literal) {
                                    org.openrdf.model.Literal nodeLiteral = (org.openrdf.model.Literal)nodeDef;
                                    if(nodeLiteral.getLanguage() != null) {
                                        node = new Literal(nodeLiteral.getLabel(), nodeLiteral.getLanguage());
                                    } else if(nodeLiteral.getDatatype() != null) {
                                        node = new Literal(nodeLiteral.getLabel(), new URI(nodeLiteral.getDatatype().stringValue()));
                                    } else {
                                        node = new Literal(nodeLiteral.getLabel());
                                    }
                                }

                                if(node != null) {
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
            e.printStackTrace();
            throw new RuntimeException("Query failed: " + e.getMessage(), e);
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

    @Override
    public SPARQLResult sparqlSelect(String query, String dataset) {
        try {
            WebTarget target = credentials.buildUrl(getSparqlSelectUriBuilder(query, dataset));
            return execSelect(target);
        } catch (MalformedURLException | IllegalArgumentException | UriBuilderException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SPARQLResult sparqlSelect(String query) {
        try {
            WebTarget target = credentials.buildUrl(getSparqlSelectUriBuilder(query));
            return execSelect(target);
        } catch (MalformedURLException | IllegalArgumentException | UriBuilderException e) {
            throw new RuntimeException(e);
        }
    }

}
