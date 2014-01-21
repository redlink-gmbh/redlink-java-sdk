package io.redlink.sdk;

import io.redlink.sdk.impl.DefaultCredentials;
import io.redlink.sdk.impl.analysis.RedLinkAnalysisImpl;
import io.redlink.sdk.impl.data.RedLinkDataImpl;
import io.redlink.sdk.impl.search.RedLinkSearchImpl;
import org.openrdf.query.parser.QueryParserRegistry;
import org.openrdf.query.parser.sparql.SPARQLParserFactory;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.RDFParserFactory;
import org.openrdf.rio.RDFParserRegistry;
import org.openrdf.rio.rdfxml.RDFXMLParserFactory;
import org.openrdf.rio.turtle.TurtleParserFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * RedLink SDK Factory
 *
 * @author sergio.fernandez@redlink.co
 */
public class RedLinkFactory {

    private static RedLinkFactory instance;

    private static Map<String, Credentials> credentials = new HashMap<String, Credentials>();

    private  RedLinkFactory() {
        Iterator<RDFParserFactory> iter = ServiceLoader.load(RDFParserFactory.class, this.getClass().getClassLoader()).iterator();
        RDFParserRegistry registry = RDFParserRegistry.getInstance();
        while (iter.hasNext()) {
            final RDFParserFactory factory = iter.next();
            registry.add(factory);
        }
        registry.add(new TurtleParserFactory());
        registry.add(new RDFXMLParserFactory());

        QueryParserRegistry registry2 = QueryParserRegistry.getInstance();
        registry2.add(new SPARQLParserFactory());
    }

    public synchronized static RedLinkFactory getInstance() {
        if (instance == null) {
            instance = new RedLinkFactory();
        }
        return instance;
    }

    private Credentials getCredentials(String key) {
        if (credentials.containsKey(key)) {
            return credentials.get(key);
        }  else {
            Credentials c = new DefaultCredentials(key);
            credentials.put(key, c);
            return c;
        }
    }

    public RedLink.Analysis createAnalysisClient(String apiKey) {
        return createAnalysisClient(getCredentials(apiKey));
    }

    public RedLink.Analysis createAnalysisClient(Credentials credentials) {
        return new RedLinkAnalysisImpl(credentials);
    }

    public RedLink.Data createDataClient(String apiKey) {
        return createDataClient(getCredentials(apiKey));
    }

    public RedLink.Data createDataClient(Credentials credentials) {
        return new RedLinkDataImpl(credentials);
    }

    public RedLink.Search createSearchClient(String apiKey) {
        return createSearchClient(new DefaultCredentials(apiKey));
    }

    public RedLink.Search createSearchClient(Credentials credentials) {
        return new RedLinkSearchImpl(credentials);
    }

}
