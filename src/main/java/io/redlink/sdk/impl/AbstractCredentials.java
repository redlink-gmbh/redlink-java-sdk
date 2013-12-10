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
