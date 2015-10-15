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
package io.redlink.sdk.util;

import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Custom URIBuilder to keep compatible with UriBuilder from JAX-RS
 *
 * @author sergio.fernandez@redlink.co
 */
public class UriBuilder extends URIBuilder {

    public UriBuilder() {
        super();
    }

    public UriBuilder(String string) throws URISyntaxException {
        super(string);
    }

    public UriBuilder(URI uri) {
        super(uri);
    }

    public UriBuilder(URIBuilder builder) throws URISyntaxException {
        this(builder.build());
    }

    public static UriBuilder fromUri(String uri) throws URISyntaxException {
        return new UriBuilder(uri);
    }

    /**
     * Appends a path to the current one
     *
     * @param path
     * @return
     * @throws URISyntaxException
     */
    public UriBuilder path(String path) throws URISyntaxException {
        path = path.trim();
        if (getPath().endsWith("/")) {
            return new UriBuilder(setPath(String.format("%s%s", getPath(), path)));
        } else {
            return new UriBuilder(setPath(String.format("%s/%s", getPath(), path)));
        }
    }

    /**
     * Wrapper to addParameter()
     *
     * @param param
     * @param value
     * @return
     * @throws URISyntaxException
     */
    public UriBuilder queryParam(String param, String value) throws URISyntaxException {
        return new UriBuilder(addParameter(param, value));
    }

}
