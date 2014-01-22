package io.redlink.sdk.impl;

import io.redlink.sdk.Credentials;

import java.net.MalformedURLException;
import java.net.URI;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

/**
 * On-Premise {@link Credentials} implementation. This implementation should be used as the most simple way to access to a RedLink On-Premise platform
 * The local endpoint is fixed and can be changed by constructor. API key is not necessary. Customizations of this class can be done by extending it
 * 
 * @author rafa.haro@redlink.co
 *
 */
public class CustomCredentials extends AbstractCredentials {
	
	public static final String DEVELOPMENT_ENDPOINT = "http://localhost:8080/api";

	public CustomCredentials() {
		this(DEVELOPMENT_ENDPOINT);
	}
	
	public CustomCredentials(String endpoint) {
		super(endpoint, "", null);
	}

	/*
	 * (non-Javadoc)
	 * @see io.redlink.sdk.Credentials#buildUrl(javax.ws.rs.core.UriBuilder)
	 */
	@Override
	public WebTarget buildUrl(UriBuilder builder) throws MalformedURLException, IllegalArgumentException, UriBuilderException {
		ResteasyClientBuilder clientBuilder = new ResteasyClientBuilder();
		URI uri = builder.build();
		return clientBuilder.build().target(uri.toString());
	}
	
}
