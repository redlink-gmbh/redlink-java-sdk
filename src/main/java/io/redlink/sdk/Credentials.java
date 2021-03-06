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
package io.redlink.sdk;

import io.redlink.sdk.impl.Status;
import io.redlink.sdk.util.UriBuilder;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * RedLink SDK Credentials. A Credential object must be used in any request to the RedLink services
 *
 * @author sergio.fernandez@redlink.co
 * @author rharo@zaizi.com
 */
public interface Credentials extends Serializable {

    /**
     * Get the API endpoint
     *
     * @return api endpoint
     */
    String getEndpoint();

    /**
     * Get the API version
     *
     * @return api verison
     */
    String getVersion();

    /**
     * Get the API Key
     *
     * @return api key
     */
    String getApiKey();

    /**
     * Get the base URI of the data hub
     *
     * @return datahub uri
     */
    String getDataHub();

    /**
     * Verify the current (cached) credentials are valid
     *
     * @return valid/invalid credentials
     */
    boolean verify() throws MalformedURLException;

    /**
     * Forces the update of the cached status
     *
     * @return fresh status
     * @throws MalformedURLException
     */
    Status getStatus() throws IOException, URISyntaxException;

    /**
     * URI Builder for RedLink. This method uses the credential information to build an
     * endpoint client ready for performing requests bound to the user RedLink application services
     *
     * @param builder base uri builder
     * @return {@link java.net.URI}
     * @throws MalformedURLException
     * @throws IllegalArgumentException
     */
    URI buildUrl(UriBuilder builder) throws MalformedURLException, IllegalArgumentException, URISyntaxException;

}
