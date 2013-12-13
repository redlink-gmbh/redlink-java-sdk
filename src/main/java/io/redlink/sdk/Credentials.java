package io.redlink.sdk;

import java.net.MalformedURLException;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;

/**
 * RedLink SDK Credentials
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
	 * 
	 * @param builder
	 * @return
	 * @throws MalformedURLException
	 * @throws IllegalArgumentException
	 * @throws UriBuilderException
	 */
	WebTarget buildUrl(UriBuilder builder) throws MalformedURLException, IllegalArgumentException, UriBuilderException;

}
