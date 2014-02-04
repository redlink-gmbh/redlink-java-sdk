package io.redlink.sdk.impl;

import io.redlink.sdk.Credentials;
import io.redlink.sdk.RedLink;

import javax.ws.rs.core.UriBuilder;

/**
 * RedLink Client API (abstract) template implementation. Any RedLink client concrete implementation must extend this class and use a
 * {@link Credentials} object that will be used in any request to the RedLink API
 *
 * @author rafa.haro@redlink.co
 */
public abstract class RedLinkAbstractImpl implements RedLink {

    protected final Credentials credentials;

    public RedLinkAbstractImpl(Credentials credentials) {
        this.credentials = credentials;
    }

    /**
     * Create an {@link UriBuilder} for RedLink services endpoints URIs based on the current {@link Credentials}.
     * The resultant {@link UriBuilder} will contain the common endpoint prefix for all the services. The rest
     * of the URI will depend on the requested service and the passed parameters
     *
     * @return RedLink API Endpoint URI prefix builder
     */
    protected final UriBuilder initiateUriBuilding() {
        return UriBuilder.fromUri(credentials.getEndpoint()).path(credentials.getVersion());
    }

}
