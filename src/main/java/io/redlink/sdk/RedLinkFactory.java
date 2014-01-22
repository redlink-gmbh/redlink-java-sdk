package io.redlink.sdk;

import io.redlink.sdk.RedLink.Analysis;
import io.redlink.sdk.impl.DefaultCredentials;
import io.redlink.sdk.impl.analysis.RedLinkAnalysisImpl;
import io.redlink.sdk.impl.data.RedLinkDataImpl;
/**
 * RedLink SDK Factory. This class eases the creation of the different RedLink services' clients. A single client for each 
 * configured Application should be used.
 *
 * @author sergio.fernandez@redlink.co
 */
public class RedLinkFactory {

	/**
	 * Create an {@link Analysis} client associated to an user API key
	 * 
	 * @param apiKey RedLink valid API key
	 * @return RedLink's {@link Analysis} service client 
	 */
    public static RedLink.Analysis createAnalysisClient(String apiKey) {
        return createAnalysisClient(new DefaultCredentials(apiKey));
    }

    /**
     * Create an {@link Analysis} client associated to an user {@link Credentials}
     * 
     * @param credentials RedLink valid {@link Credentials}
     * @return RedLink's {@link Analysis} service client
     */
    public static RedLink.Analysis createAnalysisClient(Credentials credentials) {
        return new RedLinkAnalysisImpl(credentials);
    }

    /**
	 * Create a {@link Data} client associated to an user API key
	 * 
	 * @param apiKey RedLink valid API key
	 * @return RedLink's {@link Data} service client 
	 */
    public static RedLink.Data createDataClient(String apiKey) {
        return createDataClient(new DefaultCredentials(apiKey));
    }

    /**
     * Create an {@link Data} client associated to an user {@link Credentials}
     * 
     * @param credentials RedLink valid {@link Credentials}
     * @return RedLink's {@link Data} service client
     */
    public static RedLink.Data createDataClient(Credentials credentials) {
        return new RedLinkDataImpl(credentials);
    }

        
//    public static RedLink.Search createSearchClient(String apiKey) {
//        return createSearchClient(new DefaultCredentials(apiKey));
//    }
//
//    
//    @Deprecated
//    public static RedLink.Search createSearchClient(Credentials credentials) {
//        return new RedLinkSearchImpl(credentials);
//    }

}
