package io.redlink.sdk;

import io.redlink.sdk.impl.DefaultCredentials;

/**
 * RedLink SDK Factory
 * 
 * @author sergio.fernandez@redlink.co
 *
 */
public class RedLinkFactory {

    public static RedLink.Analysis createAnalysisClient(String apiKey) {
        return createAnalysisClient(new DefaultCredentials(apiKey));
    }

	public static RedLink.Analysis createAnalysisClient(Credentials credentials) {
		return createAnalysisClient(credentials);
	}
	
	public static RedLink.Data createDataClient(String apiKey){
		return createDataClient(new DefaultCredentials(apiKey));
	}
	
	public static RedLink.Data createDataClient(Credentials credentials){
		return createDataClient(credentials);
	}

    public static RedLink.Search createSearchClient(String apiKey) {
        return createSearchClient(new DefaultCredentials(apiKey));
    }

    public static RedLink.Search createSearchClient(Credentials credentials) {
    	return createSearchClient(credentials);
    }

}
