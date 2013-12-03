package io.redlink.sdk;

import io.redlink.sdk.impl.DefaultCredentials;
import io.redlink.sdk.impl.analysis.RedLinkAnalysisImpl;
import io.redlink.sdk.impl.search.RedLinkSearchImpl;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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

    public static RedLink.Search createSearchClient(String apiKey) {
        throw new NotImplementedException();
        //return createSearchClient(new DefaultCredentials(apiKey));
    }

    public static RedLink.Search createSearchClient(Credentials credentials) {
        throw new NotImplementedException();
        //return new RedLinkSearchImpl(credentials);
    }

}
