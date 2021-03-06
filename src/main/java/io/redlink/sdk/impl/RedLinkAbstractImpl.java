/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.redlink.sdk.impl;

import io.redlink.sdk.Credentials;
import io.redlink.sdk.RedLink;
import io.redlink.sdk.util.UriBuilder;
import org.apache.http.client.utils.URIBuilder;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * RedLink Client API (abstract) template implementation. Any RedLink client concrete implementation must extend this class and use a
 * {@link Credentials} object that will be used in any request to the RedLink API
 *
 * @author rafa.haro@redlink.co
 */
public abstract class RedLinkAbstractImpl implements RedLink {

    private static final long serialVersionUID = -2644378258418145067L;

    protected final Credentials credentials;
    protected final Status status;
    protected final RedLinkClient client;

    public RedLinkAbstractImpl(Credentials credentials) {
        this.credentials = credentials;
        try {
            this.status = credentials.getStatus();
            if (!this.status.isAccessible()) {
                throw new IllegalArgumentException("invalid credentials: not accessible api key");
            }
        } catch (URISyntaxException | IOException e) {
            throw new IllegalArgumentException("invalid credentials: " + e.getMessage(), e);
        }
        this.client = new RedLinkClient();
    }

    /**
     * Create an {@link URIBuilder} for RedLink services endpoints URIs based on the current {@link Credentials}.
     * The resultant {@link URIBuilder} will contain the common endpoint prefix for all the services. The rest
     * of the URI will depend on the requested service and the passed parameters
     *
     * @return RedLink API Endpoint URI prefix builder
     */
    protected final UriBuilder initiateUriBuilding() throws URISyntaxException {
        return (UriBuilder) new UriBuilder(credentials.getEndpoint()).setPath(credentials.getVersion());
    }

}
