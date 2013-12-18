package io.redlink.sdk.impl;

import java.net.MalformedURLException;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import io.redlink.sdk.Credentials;

/**
 * 
 * @author rafa.haro@redlink.co
 *
 */
public abstract class AbstractCredentials implements Credentials {
	
	protected final String endpoint;
	
	protected final String version;
	
	protected final String apiKey;
	
	public AbstractCredentials(String endpoint, String version, String apiKey) {
		this.endpoint = endpoint;
		this.version = version;
		this.apiKey = apiKey;
	}

    @Override
	public boolean verify() throws MalformedURLException {
        WebTarget target = buildUrl(UriBuilder.fromUri(getEndpoint()).path(getVersion()));
        Invocation.Builder request = target.request();
        request.accept("application/json");
        try {
            Response response = request.get();
            if (response.getStatus() == 200) {
                Status status = response.readEntity(Status.class);
                return status.isAccessible();
            } else {
            	throw new RuntimeException("Status check failed: HTTP error code " 
                		+ response.getStatus() + ". Endpoint: " + target.getUri().toString());
            }
        } catch (Exception e) {
            throw new RuntimeException("Status check failed: " + e.getMessage(), e);
        }
	}
	
	@Override
	public String getEndpoint() {
		return endpoint; 
	}
	
	@Override
	public String getVersion() {
		return version; 
	}
	
	@Override
	public String getApiKey() {
		return apiKey;
	}
	
}
