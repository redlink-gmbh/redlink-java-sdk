package io.redlink.sdk;

import java.net.MalformedURLException;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;

/**
 * RedLink SDK Credentials. A Credential object must be used in any request to the RedLink services
 * 
 * @author sergio.fernandez@redlink.co
 * @author rafa.haro@redlink.co
 *
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
	 * Verify User Current Profile
	 * 
	 * @return
	 */
	boolean verify() throws MalformedURLException;
	
	/**
	 * JAX-RS Endpoint Builder for RedLink. This method uses the credential information and an {@link UriBuilder} to build an
	 * endpoint client ready for performing requests to the user RedLink application services  
	 * 
	 * @param builder
	 * @return
	 * @throws MalformedURLException
	 * @throws IllegalArgumentException
	 * @throws UriBuilderException
	 */
	WebTarget buildUrl(UriBuilder builder) throws MalformedURLException, IllegalArgumentException, UriBuilderException;

}
