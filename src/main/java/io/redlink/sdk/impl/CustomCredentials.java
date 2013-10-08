package io.redlink.sdk.impl;

import java.net.MalformedURLException;
import java.net.URI;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

public class CustomCredentials extends AbstractCredentials {
	
	public static final String DEVELOPMENT_ENDPOINT = "http://localhost:8080/api";
	
	public CustomCredentials() {
		this(DEVELOPMENT_ENDPOINT);
	}
	
	public CustomCredentials(String endpoint) {
		super(endpoint, null, null);
	}

	@Override
	public WebTarget buildUrl(UriBuilder builder) throws MalformedURLException, IllegalArgumentException, UriBuilderException {
		ResteasyClientBuilder clientBuilder = new ResteasyClientBuilder();
		URI uri = builder.build();
		return clientBuilder.build().target(uri.toString());
	}
	
}
