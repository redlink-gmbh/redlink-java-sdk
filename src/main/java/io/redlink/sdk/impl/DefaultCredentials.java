package io.redlink.sdk.impl;


import io.redlink.sdk.util.ApiHelper;
import io.redlink.sdk.util.RedLinkClientBuilder;

import java.net.MalformedURLException;
import java.net.URI;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default credentials against the public api
 * 
 * @author sergiofernandez@redlink.co
 * @author jakob.frank@redlink.co
 *
 */
public final class DefaultCredentials extends AbstractCredentials {

    private static Logger log = LoggerFactory.getLogger(DefaultCredentials.class);
	
	private static final String ENDPOINT = "https://api.redlink.io";
	
	private static final String KEY_PARAM = "key";
	
	public DefaultCredentials(String apiKey){
		this(apiKey, ApiHelper.getApiVersion());
	}

    public DefaultCredentials(String apiKey, String version) {
        super(ENDPOINT, version, apiKey);
        log.debug("created credentials over {}/{}", ENDPOINT, version);
    }

    @Override
	public WebTarget buildUrl(UriBuilder builder) throws MalformedURLException, IllegalArgumentException, UriBuilderException {
		ResteasyClientBuilder clientBuilder = new RedLinkClientBuilder();
		URI uri = builder.queryParam(KEY_PARAM, apiKey).build();
		return clientBuilder.build().target(uri.toString());
	}
	
}
