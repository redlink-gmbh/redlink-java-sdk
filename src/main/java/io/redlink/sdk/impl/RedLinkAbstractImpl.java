package io.redlink.sdk.impl;

import io.redlink.sdk.Credentials;
import io.redlink.sdk.RedLink;

import javax.ws.rs.core.UriBuilder;

public abstract class RedLinkAbstractImpl implements RedLink {
	
	protected final Credentials credentials;

	public RedLinkAbstractImpl(Credentials credentials) {
		this.credentials = credentials;
	}
	
	protected final UriBuilder initiateUriBuilding() { 
		return UriBuilder.fromUri(credentials.getEndpoint()).path(credentials.getVersion());
	}
	
}
