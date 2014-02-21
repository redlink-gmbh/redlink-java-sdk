package io.redlink.sdk;

import io.redlink.sdk.impl.Status;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;
import java.net.MalformedURLException;

/**
 * RedLink SDK Credentials. A Credential object must be used in any request to the RedLink services
 *
 * @author sergio.fernandez@redlink.co
 * @author rafa.haro@redlink.co
 */
public interface Credentials {

    /**
     * Get the API endpoint
     *
     * @return api endpoint
     */
    String getEndpoint();

    /**
     * Get the API version
     *
     * @return api verison
     */
    String getVersion();

    /**
     * Get the API Key
     *
     * @return api key
     */
    String getApiKey();

    /**
     * Get the base URI of the data hub
     *
     * @return datahub uri
     */
    String getDataHub();

    /**
     * Verify the current (cached) credentials are valid
     *
     * @return valid/invalid credentials
     */
    boolean verify() throws MalformedURLException;

    /**
     * Forces the update of the cached status
     *
     * @return fresh status
     * @throws MalformedURLException
     */
    Status getStatus() throws MalformedURLException;

    /**
     * JAX-RS Endpoint Builder for RedLink. This method uses the credential information to build an
     * endpoint client ready for performing requests bound to the user RedLink application services
     *
     * @param builder base uri builder
     * @return {@link javax.ws.rs.client.WebTarget}
     * @throws MalformedURLException
     * @throws IllegalArgumentException
     * @throws UriBuilderException
     */
    WebTarget buildUrl(UriBuilder builder) throws MalformedURLException, IllegalArgumentException, UriBuilderException;

}
