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

import io.redlink.sdk.impl.DefaultCredentials;
import io.redlink.sdk.util.ApiHelper;
import io.redlink.sdk.util.UriBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

/**
 * Generic tests and utilities shared by other tests in the suite
 *
 * @author sergio.fernandez@redlink.co
 *
 */
public class GenericTest {

    private static Logger log = LoggerFactory.getLogger(GenericTest.class);

    protected static final String API_KEY_FILE = "/api.key";

    /**
     * Build the credentials for testing the SDK. It dynamically
     * check for a file 'api.key' in the same path where the test
     * is, creating a DefaultCredentials with the API key,
     * CustomCredentials against the demo otherwise.
     *
     * @return credentials
     */
    protected static final Credentials buildCredentials() {
        return buildCredentials(GenericTest.class);
    }

    protected static final String buildDatasetsBaseUri(Credentials credentials, int user) throws URISyntaxException {
        return UriBuilder.fromUri(credentials.getDataHub()).path(String.valueOf(user)).build().toString() + "/";
    }

    protected static final String buildDatasetBaseUri(Credentials credentials, int user, String dataset) throws URISyntaxException {
        return UriBuilder.fromUri(credentials.getDataHub()).path(String.valueOf(user)).path(dataset).build().toString() + "/";
    }

    /**
     * Build the credentials for testing the SDK. It dynamically
     * check for a file 'api.key' in the same path where the test
     * is, creating a DefaultCredentials with the API key,
     * CustomCredentials against the demo otherwise.
     *
     * @param klass Class to use for classpath-based file lookup
     *
     * @return credentials
     */
    protected static final Credentials buildCredentials(Class<?> klass) {
        InputStream is = klass.getResourceAsStream(API_KEY_FILE);
        if (is != null) {
            try {
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String apiKey = br.readLine();
                return new DefaultCredentials(apiKey, getVersion());
            } catch (IOException e) {
                log.error("error reading api key file: {}", e.getMessage());
                return null;
            }
        } else {
            log.error("api key file not found");
            return null;
        }
    }

    private static String getVersion() {
        //FIXME: this does not work
        return ApiHelper.getApiVersion(System.getProperty("projectVersion"));
    }

}
