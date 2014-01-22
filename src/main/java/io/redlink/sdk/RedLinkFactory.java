package io.redlink.sdk;

import io.redlink.sdk.impl.DefaultCredentials;
import io.redlink.sdk.impl.analysis.RedLinkAnalysisImpl;
import io.redlink.sdk.impl.data.RedLinkDataImpl;
import io.redlink.sdk.impl.search.RedLinkSearchImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * RedLink SDK Factory. This class eases the creation of the different RedLink services' clients. A single client for each 
 * configured Application should be used.
 *
 * @author sergio.fernandez@redlink.co
 */
public class RedLinkFactory {

    private static RedLinkFactory instance;

    private Map<String, Credentials> credentials = new HashMap<String, Credentials>();

    private  RedLinkFactory() {
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

    /**
     * Create an {@link Analysis} client associated to an user API key
	 * 
	 * @param apiKey RedLink valid API key
	 * @return RedLink's {@link Analysis} service client 
	 */
    public RedLink.Analysis createAnalysisClient(String apiKey) {
        return createAnalysisClient(getCredentials(apiKey));
    }

    /**
     * Create an {@link Analysis} client associated to an user {@link Credentials}
     * 
     * @param credentials RedLink valid {@link Credentials}
     * @return RedLink's {@link Analysis} service client
     */
    public RedLink.Analysis createAnalysisClient(Credentials credentials) {
        return new RedLinkAnalysisImpl(credentials);
    }

    /**
	 * Create a {@link Data} client associated to an user API key
	 * 
	 * @param apiKey RedLink valid API key
	 * @return RedLink's {@link Data} service client 
	 */
    public RedLink.Data createDataClient(String apiKey) {
        return createDataClient(getCredentials(apiKey));
    }

    /**
     * Create an {@link Data} client associated to an user {@link Credentials}
     * 
     * @param credentials RedLink valid {@link Credentials}
     * @return RedLink's {@link Data} service client
     */
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