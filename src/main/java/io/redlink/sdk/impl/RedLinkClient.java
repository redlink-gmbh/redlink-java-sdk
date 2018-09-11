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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.redlink.sdk.util.VersionHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.DeflateInputStream;
import org.apache.http.client.entity.InputStreamFactory;
import org.apache.http.client.methods.*;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.openrdf.rio.RDFFormat;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * Redlink client wrapping HttpClient
 *
 * @author sergio.fernandez@redlink.co
 */
public class RedLinkClient implements Serializable {

    private static final long serialVersionUID = -6399964450824289653L;

    public static final int REQUEST_TIMEOUT = 60;  //TODO: configuration

    public static final Map<String,InputStreamFactory> decoderRegistry;

    public static final InputStreamFactory GZIP = new InputStreamFactory() {
        @Override
        public InputStream create(InputStream in) throws IOException {
            return new GZIPInputStream(in);
        }
    };

    public static final InputStreamFactory DEFLATE = new InputStreamFactory() {
        @Override
        public InputStream create(InputStream in) throws IOException {
            return new DeflateInputStream(in);
        }
    };

    public static final InputStreamFactory IDENTITY = new InputStreamFactory() {
        @Override
        public InputStream create(InputStream in) throws IOException {
            return in;
        }
    };

    static {
        Map<String, InputStreamFactory> dr = new HashMap<>();
        dr.put("gzip", GZIP);
        dr.put("x-gzip", GZIP);
        dr.put("deflate", DEFLATE);
        dr.put("none", IDENTITY);
        decoderRegistry = Collections.unmodifiableMap(dr);
    }

    private transient CloseableHttpClient client;
    private final ObjectMapper mapper;

    public RedLinkClient() {
        mapper = new ObjectMapper();
        client = buildHttpClient();
    }

    private CloseableHttpClient buildHttpClient() {
        final HttpClientBuilder builder = HttpClientBuilder.create();

        final RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(REQUEST_TIMEOUT * 1000)
                .setConnectionRequestTimeout(REQUEST_TIMEOUT * 1000)
                .setSocketTimeout(REQUEST_TIMEOUT * 1000)
                .build();
        builder.setDefaultRequestConfig(config);

        builder.setUserAgent(String.format("RedlinkJavaSDK/%s", VersionHelper.getVersion()));

        // Workaround for SEARCH-230: we use our own Content-Encoding decoder registry.
        builder.setContentDecoderRegistry(decoderRegistry);

        return builder.build();
    }

    public String get(final URI target) throws IOException {
        return get(target, "");
    }

    public String get(final URI target, final String accept) throws IOException {
        final ResponseHandler<String> handler = new ResponseHandler<String>() {
            @Override
            public String handleResponse(final HttpResponse response) throws IOException {
                final int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            }
        };
        return get(target, accept, handler);
    }

    public <T> T get(final URI target, final Class<T> clazz) throws IOException {
        return get(target, clazz, null);
    }

    public <T> T get(final URI target, final Class<T> clazz, String accept) throws IOException {
        final ResponseHandler<T> handler = new ResponseHandler<T>() {
            @Override
            public T handleResponse(final HttpResponse response) throws IOException {
                final int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    return mapper.readValue(entity.getContent(), clazz);
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            }
        };
        return get(target, accept, handler);
    }

    private <T> T get(final URI target, String accept, ResponseHandler<T> handler) throws IOException {
        final HttpGet get = new HttpGet(target);
        if (StringUtils.isNotBlank(accept)) {
            get.setHeader("Accept", accept);
        }
        if (client == null) {
            client = buildHttpClient();
        }
        return client.execute(get, handler);
    }

    public CloseableHttpResponse put(URI target, InputStream in, RDFFormat format) throws IOException {
        return exec(new HttpPut(target), in, format);
    }

    public CloseableHttpResponse post(URI target, InputStream in, RDFFormat format) throws IOException {
        return exec(new HttpPost(target), in, format);
    }

    public CloseableHttpResponse post(URI target) throws IOException {
        return post(target, null);
    }

    public CloseableHttpResponse post(URI target, String accept) throws IOException {
        final HttpPost post = new HttpPost(target);
        if (StringUtils.isNotBlank(accept)) {
            post.setHeader("Accept", accept);
        }
        if (client == null) {
            client = buildHttpClient();
        }
        return client.execute(post);
    }

    public CloseableHttpResponse post(URI target, String body, String accept) throws IOException {
        return exec(new HttpPost(target), new StringEntity(body), accept, null);
    }

    public CloseableHttpResponse post(URI target, String body, String accept, String contentType) throws IOException {
        return exec(new HttpPost(target), new StringEntity(body), accept, contentType);
    }

    public CloseableHttpResponse post(URI target, InputStream in, String accept, String contentType) throws IOException {
        return exec(new HttpPost(target), new InputStreamEntity(in), accept, contentType);
    }

    private CloseableHttpResponse exec(HttpEntityEnclosingRequestBase req, HttpEntity entity, String accept, String format) throws IOException {
        req.setEntity(entity);
        if (StringUtils.isNotBlank(accept)) {
            req.setHeader("Accept", accept);
        }
        if (StringUtils.isNotBlank(format)) {
            req.setHeader("Content-Type", format);
        }
        if (client == null) {
            client = buildHttpClient();
        }
        return client.execute(req);
    }

    private CloseableHttpResponse exec(HttpEntityEnclosingRequestBase req, InputStream in, RDFFormat format) throws IOException {
        return exec(req, new InputStreamEntity(in), format);
    }

    private CloseableHttpResponse exec(HttpEntityEnclosingRequestBase req, HttpEntity entity, RDFFormat format) throws IOException {
        req.setEntity(entity);
        req.setHeader("Content-Type", format.getDefaultMIMEType());
        if (client == null) {
            client = buildHttpClient();
        }
        return client.execute(req);
    }

    public CloseableHttpResponse delete(URI target) throws IOException {
        final HttpDelete delete = new HttpDelete(target);
        if (client == null) {
            client = buildHttpClient();
        }
        return client.execute(delete);
    }

}
