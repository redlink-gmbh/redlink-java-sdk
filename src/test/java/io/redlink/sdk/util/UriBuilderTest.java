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
import org.junit.Assert;
import org.junit.Test;

import java.net.URISyntaxException;

/**
 * Some test to warranty the completeness of UriBuilder
 *
 * @author sergio.fernandez@redlink.co
 */
public class UriBuilderTest {

    @Test
    public void testDefaultConstructor() throws URISyntaxException {
        final UriBuilder uriBuilder1 = new UriBuilder();
        final URIBuilder uriBuilder2 = new URIBuilder();
        Assert.assertEquals(-1, uriBuilder1.getPort());
        Assert.assertEquals(uriBuilder2.build(), uriBuilder1.build());
    }

    @Test
    public void testStringConstructor() throws URISyntaxException {
        final String uri = "https://example.org/test#foo";
        final UriBuilder uriBuilder1 = new UriBuilder(uri);
        final URIBuilder uriBuilder2 = new URIBuilder(uri);
        Assert.assertEquals(uriBuilder2.build(), uriBuilder1.build());
        Assert.assertEquals(uri, uriBuilder1.build().toString());
    }

    @Test
    public void testPath() throws URISyntaxException {
        final UriBuilder uriBuilder = new UriBuilder("https://example.org/test");
        uriBuilder.path("2nd");
        Assert.assertEquals("https://example.org/test/2nd", uriBuilder.build().toString());
    }

    @Test
    public void testRootPath() throws URISyntaxException {
        final String host = "https://example.org";
        final UriBuilder uriBuilder1 = new UriBuilder(host);
        uriBuilder1.setPath("foo");
        final UriBuilder uriBuilder2 = new UriBuilder(host);
        uriBuilder2.path("foo");
        Assert.assertEquals(uriBuilder2.build(), uriBuilder1.build());
    }

    @Test
    public void testRootSetPath() throws URISyntaxException {
        final String host = "https://example.org";
        final UriBuilder uriBuilder1 = new UriBuilder(host);
        uriBuilder1.setPath("/foo");
        final UriBuilder uriBuilder2 = new UriBuilder(host);
        uriBuilder2.setPath("foo");
        Assert.assertEquals(uriBuilder2.build(), uriBuilder1.build());
    }

}
