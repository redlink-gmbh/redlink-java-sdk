package io.redlink.sdk;

import io.redlink.sdk.impl.DefaultCredentials;
import io.redlink.sdk.impl.analysis.RedLinkAnalysisImpl;
import io.redlink.sdk.impl.data.RedLinkDataImpl;
import io.redlink.sdk.impl.search.RedLinkSearchImpl;

/**
 * RedLink SDK Factory
 *
 * @author sergio.fernandez@redlink.co
 */
public class RedLinkFactory {

    public static RedLink.Analysis createAnalysisClient(String apiKey) {
        return createAnalysisClient(new DefaultCredentials(apiKey));
    }

    public static RedLink.Analysis createAnalysisClient(Credentials credentials) {
        return new RedLinkAnalysisImpl(credentials);
    }

    public static RedLink.Data createDataClient(String apiKey) {
        return createDataClient(new DefaultCredentials(apiKey));
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
