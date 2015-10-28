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

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import io.redlink.sdk.util.UriBuilder;
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

    private static final long serialVersionUID = 6763770459425653864L;

    public static final String ENDPOINT = "https://api.staging.redlink.io";

    public static final String KEY_PARAM = "key";

    public static final String DATAHUB = "http://data.staging.redlink.io";

    private static Logger log = LoggerFactory.getLogger(StagingCredentials.class);

    public StagingCredentials(String apiKey) {
        this(apiKey, ApiHelper.getApiVersion());
    }

    public StagingCredentials(String apiKey, String version) {
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
