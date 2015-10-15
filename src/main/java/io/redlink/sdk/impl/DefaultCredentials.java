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
import io.redlink.sdk.util.ApiHelper;
import io.redlink.sdk.util.RedLinkClient;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import io.redlink.sdk.util.UriBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Public API {@link Credentials} implementation. This implementation should
 * be used as the most simple way to access to the RedLink public platform
 * An user valid API key is necessary for building the credential object.
 *
 * @author sergio.fernandez@redlink.co
 * @author jakob.frank@redlink.co
 */
public final class DefaultCredentials extends AbstractCredentials {

    public static final String ENDPOINT = "https://api.redlink.io";

    public static final String KEY_PARAM = "key";

    public static final String DATAHUB = "http://data.redlink.io";

    private static Logger log = LoggerFactory.getLogger(DefaultCredentials.class);

    public DefaultCredentials(String apiKey) {
        this(apiKey, ApiHelper.getApiVersion());
    }

    public DefaultCredentials(String apiKey, String version) {
        super(ENDPOINT, version, apiKey, DATAHUB);
        log.debug("created credentials over {}/{}", ENDPOINT, version);
    }

    @Override
    public URI buildUrl(UriBuilder builder) throws MalformedURLException, IllegalArgumentException, URISyntaxException {
        synchronized (builder) {
            return builder.addParameter(KEY_PARAM, apiKey).build();
        }
    }

}
