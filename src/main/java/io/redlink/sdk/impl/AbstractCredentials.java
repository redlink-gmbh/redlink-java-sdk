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
import io.redlink.sdk.util.RedLinkClient;
import io.redlink.sdk.util.UriBuilder;

import java.net.MalformedURLException;
import java.net.URI;

/**
 * {@link Credentials} template implementation. The verify method and getters are invariant
 * for any implementation of the RedLink Credentials API
 *
 * @author sergio.fernandez@redlink.co
 * @author rafa.haro@redlink.co
 */
abstract class AbstractCredentials implements Credentials {

    protected final String endpoint;

    protected final String version;

    protected final String apiKey;

    protected final String datahub;

    protected Status status;

    protected RedLinkClient client;

    AbstractCredentials(String endpoint, String version, String apiKey, String datahub) {
        this.endpoint = endpoint;
        this.version = version;
        this.apiKey = apiKey;
        this.datahub = datahub;
        this.client = new RedLinkClient();
    }

    AbstractCredentials(String endpoint, String version, String apiKey) {
        this(endpoint, version, apiKey, endpoint.replace("api", "data").replace("https://", "http://"));
    }

    @Override
    public String getEndpoint() {
        return endpoint;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getApiKey() {
        return apiKey;
    }

    @Override
    public String getDataHub() {
        return datahub;
    }

    @Override
    public boolean verify() throws MalformedURLException {
        if (status == null) {
            status = getStatus();
        }
        return status.isAccessible();
    }

    @Override
    public synchronized Status getStatus()  {
        try {
            final URI target = new UriBuilder(endpoint).setPath(version).build();
            return client.get(target, Status.class, "application/json");
        } catch (Exception e) {
            throw new RuntimeException("Status check failed: " + e.getMessage(), e);
        }
    }

}
