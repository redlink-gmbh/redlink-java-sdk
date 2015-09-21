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
 * Staging API {@link io.redlink.sdk.Credentials} implementation. This implementation
 * should be used as the most simple way to access to the RedLink staging platform
 * An user valid API key is necessary for building the credential object.
 *
 * @author jakob.frank@redlink.co
 * @author sergio.fernandez@redlink.co
 */
public final class StagingCredentials extends AbstractCredentials {

    public static final String ENDPOINT = "https://api.staging.redlink.io";

    public static final String KEY_PARAM = "key";

    public static final String DATAHUB = "http://data.staging.redlink.io";

    private static Logger log = LoggerFactory.getLogger(StagingCredentials.class);

    private final ResteasyClientBuilder clientBuilder;

    public StagingCredentials(String apiKey) {
        this(apiKey, ApiHelper.getApiVersion());
    }

    public StagingCredentials(String apiKey, String version) {
        super(ENDPOINT, version, apiKey, DATAHUB);
        this.clientBuilder = new RedLinkClientBuilder();
        log.debug("created credentials over {}/{}", ENDPOINT, version);
    }

    @Override
    public WebTarget buildUrl(UriBuilder builder) throws MalformedURLException, IllegalArgumentException, UriBuilderException {
        URI uri = builder.queryParam(KEY_PARAM, apiKey).build();
        synchronized (clientBuilder) {
            return clientBuilder.build().target(uri.toString());
        }
    }

}
