package io.redlink.sdk.impl;

import io.redlink.sdk.Credentials;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

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
	
	@Override
	public boolean verify() {
		ClientRequest request = new ClientRequest(endpoint);
		try {
			ClientResponse<?> response = request.head();
			return (response.getStatus() == 200);
		} catch (Exception e) {
			return false;
		}
	}
	
}
