package io.redlink.sdk;

import io.redlink.sdk.impl.DefaultCredentials;
import io.redlink.sdk.impl.analysis.RedLinkAnalysisImpl;
import io.redlink.sdk.impl.data.RedLinkDataImpl;
import io.redlink.sdk.impl.search.RedLinkSearchImpl;
import org.openrdf.rio.RDFParserFactory;
import org.openrdf.rio.RDFParserRegistry;

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
        final ServiceLoader<RDFParserFactory> serviceLoader = ServiceLoader.load(RDFParserFactory.class, this.getClass().getClassLoader());
        Iterator<RDFParserFactory> iter = serviceLoader.iterator();
        RDFParserRegistry registry = RDFParserRegistry.getInstance();
        while (iter.hasNext()) {
            final RDFParserFactory factory = iter.next();
            registry.add(factory);
        }
    }

    public synchronized RedLinkFactory getInstance() {
        if (instance == null) {
            instance = new RedLinkFactory();
        }
        return instance;
    }

    private static Credentials getCredentials(String key) {
        if (credentials.containsKey(key)) {
            return credentials.get(key);
        }  else {
            Credentials c = new DefaultCredentials(key);
            credentials.put(key, c);
            return c;
        }
    }

    public static RedLink.Analysis createAnalysisClient(String apiKey) {
        return createAnalysisClient(getCredentials(apiKey));
    }

    public static RedLink.Analysis createAnalysisClient(Credentials credentials) {
        return new RedLinkAnalysisImpl(credentials);
    }

    public static RedLink.Data createDataClient(String apiKey) {
        return createDataClient(getCredentials(apiKey));
    }

    public static RedLink.Data createDataClient(Credentials credentials) {
        return new RedLinkDataImpl(credentials);
    }

    public static RedLink.Search createSearchClient(String apiKey) {
        return createSearchClient(new DefaultCredentials(apiKey));
    }

    public static RedLink.Search createSearchClient(Credentials credentials) {
        return new RedLinkSearchImpl(credentials);
    }

}
