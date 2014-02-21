package io.redlink.sdk;

import io.redlink.sdk.impl.DefaultCredentials;
import io.redlink.sdk.impl.analysis.RedLinkAnalysisImpl;
import io.redlink.sdk.impl.data.RedLinkDataImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * RedLink SDK Factory. This class eases the creation of the different RedLink
 * services' clients. A single client for each configured Application should
 * be used.
 *
 * @author sergio.fernandez@redlink.co
 */
public class RedLinkFactory {

    private static Logger log = LoggerFactory.getLogger(RedLinkFactory.class);

    private RedLinkFactory() {
    }

    /**
     * Get a singlenton oif the factory. Deprecated,
     * this method will me removed in upcoming versions.
     *
     * @return {@link io.redlink.sdk.RedLinkFactory} instance
     */
    @Deprecated
    public synchronized static RedLinkFactory getInstance() {
        log.warn("Deprecated: this is not a singleton anymore, so you can directly call the factory methods");
        return new RedLinkFactory();
    }

    private static Credentials buildCredentials(String key) {
        return new DefaultCredentials(key);
    }

    /**
     * Create an {@link io.redlink.sdk.RedLink.Analysis} client associated to an user API key
     *
     * @param apiKey RedLink valid API key
     * @return RedLink's {@link io.redlink.sdk.RedLink.Analysis} service client
     */
    public static RedLink.Analysis createAnalysisClient(String apiKey) {
        return createAnalysisClient(buildCredentials(apiKey));
    }

    /**
     * Create an {@link io.redlink.sdk.RedLink.Analysis} client associated to an user {@link Credentials}
     *
     * @param credentials RedLink valid {@link Credentials}
     * @return RedLink's {@link io.redlink.sdk.RedLink.Analysis} service client
     */
    public static RedLink.Analysis createAnalysisClient(Credentials credentials) {
        return new RedLinkAnalysisImpl(credentials);
    }

    /**
     * Create a {@link io.redlink.sdk.RedLink.Data} client associated to an user API key
     *
     * @param apiKey RedLink valid API key
     * @return RedLink's {@link io.redlink.sdk.RedLink.Data} service client
     */
    public static RedLink.Data createDataClient(String apiKey) {
        return createDataClient(buildCredentials(apiKey));
    }

    /**
     * Create a {@link io.redlink.sdk.RedLink.Data} client associated to an user {@link Credentials}
     *
     * @param credentials RedLink valid {@link Credentials}
     * @return RedLink's {@link io.redlink.sdk.RedLink.Data} service client
     */
    public static RedLink.Data createDataClient(Credentials credentials) {
        return new RedLinkDataImpl(credentials);
    }

    /**
     * Create an {@link io.redlink.sdk.RedLink.Search} client associated to an user {@link Credentials}
     * (currently not yet implemented).
     *
     * @param apiKey RedLink valid API key
     * @return RedLink's {@link io.redlink.sdk.RedLink.Search} service client
     */
    public static RedLink.Search createSearchClient(String apiKey) {
        throw new NotImplementedException();
        //return createSearchClient(new DefaultCredentials(apiKey));
    }

    /**
     * Create an {@link io.redlink.sdk.RedLink.Search} client associated to an user {@link Credentials}
     * (currently not yet implemented).
     *
     * @param credentials RedLink valid {@link Credentials}
     * @return RedLink's {@link io.redlink.sdk.RedLink.Search} service client
     */
    public static RedLink.Search createSearchClient(Credentials credentials) {
        throw new NotImplementedException();
        //return new RedLinkSearchImpl(credentials);
    }

}