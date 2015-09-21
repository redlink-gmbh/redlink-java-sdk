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

import java.net.MalformedURLException;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

/**
 * {@link Credentials} template implementation. The verify method and getters are invariant
 * for any implementation of the RedLink Credentials API
 *
 * @author rafa.haro@redlink.co
 * @author sergio.fernandez@redlink.co
 */
abstract class AbstractCredentials implements Credentials {

    protected final String endpoint;

    protected final String version;

    protected final String apiKey;

    protected final String datahub;

    private Status status;

    AbstractCredentials(String endpoint, String version, String apiKey, String datahub) {
        this.endpoint = endpoint;
        this.version = version;
        this.apiKey = apiKey;
        this.datahub = datahub;
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
    public synchronized Status getStatus() throws MalformedURLException {
        WebTarget target = buildUrl(UriBuilder.fromUri(getEndpoint()).path(getVersion()));
        Invocation.Builder request = target.request();
        request.accept("application/json");
        try {
            Response response = request.get();
            try {
                if (response.getStatus() == 200) {
                /* Response is directly serialized to an Status object containing information of the
                 * current User APP status
                 */
                    return response.readEntity(Status.class);
                } else {
                /*
                 * If the response is not an HTTP 200, then deserialize to an StatusError object containing
                 * detailed and customized information of the error in the server. Throws informative exception
                */
                    StatusError error = response.readEntity(StatusError.class);
                    throw new RuntimeException("Status check failed: HTTP error code "
                            + error.getError() + "\n Endpoint: " + target.getUri().toString()
                            + "\n Message: " + error.getMessage());
                }
            } finally {
                response.close();
            }
        } catch (Exception e) {
            throw new RuntimeException("Status check failed: " + e.getMessage(), e);
        }
    }

}
