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
import io.redlink.sdk.util.UriBuilder;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * On-Premise {@link Credentials} implementation. This implementation should be used as the most simple way to access to a RedLink On-Premise platform
 * The local endpoint is fixed and can be changed by constructor. API key is not necessary. Customizations of this class can be done by extending it
 *
 * @author rafa.haro@redlink.co
 */
public class CustomCredentials extends AbstractCredentials {

    public static final String CUSTOM_ENDPOINT = "http://api.redlink.localhost";

    public static final String CUSTOM_DATAHUB = "http://api.redlink.localhost";

    public CustomCredentials() {
        this(CUSTOM_ENDPOINT);
    }

    public CustomCredentials(String endpoint) {
        super(endpoint, "", null);
    }

    /*
     * (non-Javadoc)
     * @see io.redlink.sdk.Credentials#buildUrl(javax.ws.rs.core.UriBuilder)
     */
    @Override
    public URI buildUrl(UriBuilder builder) throws MalformedURLException, IllegalArgumentException, URISyntaxException {
        synchronized (builder) {
            return builder.build();
        }
    }

}
