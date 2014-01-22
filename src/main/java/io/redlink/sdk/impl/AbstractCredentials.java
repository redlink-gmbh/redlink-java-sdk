package io.redlink.sdk.impl;

import java.net.MalformedURLException;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import io.redlink.sdk.Credentials;

/**
 * {@link Credentials} template implementation. The verify method and getters are invariant for any implementation of the RedLink Credentials API
 * 
 * @author rafa.haro@redlink.co
 *
 */
abstract class AbstractCredentials implements Credentials {
	
	protected final String endpoint;
	
	protected final String version;
	
	protected final String apiKey;
	
	AbstractCredentials(String endpoint, String version, String apiKey) {
		this.endpoint = endpoint;
		this.version = version;
		this.apiKey = apiKey;
	}

	/*
	 * (non-Javadoc)
	 * @see io.redlink.sdk.Credentials#verify()
	 */
    @Override
	public boolean verify() throws MalformedURLException {
        WebTarget target = buildUrl(UriBuilder.fromUri(getEndpoint()).path(getVersion()));
        Invocation.Builder request = target.request();
        request.accept("application/json");
        try {
            Response response = request.get();
            if (response.getStatus() == 200) {
            	/* Response is directly serialized to an Status object containing information of the
            	 * current User APP status 
            	 */
                Status status = response.readEntity(Status.class);
                return status.isAccessible();
            } else {
            	/*
            	 * If the response is not an HTTP 200, then deserialize to an StatusError object containing
            	 * detailed and customized information of the error in the server. Throws informative exception
            	 */
            	StatusError error = response.readEntity(StatusError.class);
            	throw new RuntimeException("Status check failed: HTTP error code " 
                		+ error.getError() + "\n Endpoint: " + target.getUri().toString()
                		+ "\n Message: " + error.getMessage());
            }
        } catch (Exception e) {
            throw new RuntimeException("Status check failed: " + e.getMessage(), e);
        }
	}
	
    /*
     * (non-Javadoc)
     * @see io.redlink.sdk.Credentials#getEndpoint()
     */
	@Override
	public String getEndpoint() {
		return endpoint; 
	}
	
	/*
	 * (non-Javadoc)
	 * @see io.redlink.sdk.Credentials#getVersion()
	 */
	@Override
	public String getVersion() {
		return version; 
	}
	
	/*
	 * (non-Javadoc)
	 * @see io.redlink.sdk.Credentials#getApiKey()
	 */
	@Override
	public String getApiKey() {
		return apiKey;
	}
}
