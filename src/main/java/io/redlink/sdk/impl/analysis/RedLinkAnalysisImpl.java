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
package io.redlink.sdk.impl.analysis;

import io.redlink.sdk.Credentials;
import io.redlink.sdk.RedLink;
import io.redlink.sdk.analysis.AnalysisRequest;
import io.redlink.sdk.analysis.AnalysisRequest.InputFormat;
import io.redlink.sdk.analysis.AnalysisRequest.OutputFormat;
import io.redlink.sdk.impl.RedLinkAbstractImpl;
import io.redlink.sdk.impl.analysis.model.EnhancementParserException;
import io.redlink.sdk.impl.analysis.model.Enhancements;
import io.redlink.sdk.impl.analysis.model.EnhancementsParser;
import io.redlink.sdk.impl.analysis.model.EnhancementsParserFactory;
import io.redlink.sdk.util.UriBuilder;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * RedLink's {@link Analysis} Service Client implementation. The implementation follows a basic workflow: using the user
 * {@link Credentials} and an {@link AnalysisRequest} object, build the endpoint URI, add the parameters of the service and
 * inject the content to be analyzed and, finally, make the request to RedLink platform. The response of the service is parsed
 * using the proper parser for the {@link OutputFormat} selected by the user
 *
 * @author Rafa Haro
 * @author Sergio Fernández
 */
public class RedLinkAnalysisImpl extends RedLinkAbstractImpl implements RedLink.Analysis {

    private static final Logger log = LoggerFactory.getLogger(RedLinkAnalysisImpl.class);

    public RedLinkAnalysisImpl(Credentials credentials) {
        super(credentials);
    }

    @Override
    public Enhancements enhance(AnalysisRequest request) throws IOException {
        CloseableHttpResponse response = execEnhance(request);
        try {
            return parseResponse(response);
        } finally {
            response.close();
        }
    }

    @Override
    public <T> T enhance(AnalysisRequest request, Class<T> responseType) throws IOException {
        Object result = null;
        if (responseType.isAssignableFrom(Enhancements.class)) {
            AnalysisRequest finalRequest = request;
            if (request.getOutputMediaType().
                    equals(OutputFormat.JSON.value()) ||
                    request.getOutputMediaType().
                            equals(OutputFormat.XML.value())) {
                finalRequest = AnalysisRequest.builder().
                        setAnalysis(request.getAnalysis()).
                        setContent(request.getContent()).
                        setInputFormat(InputFormat.valueOf(request.getInputFormat())).
                        setOutputFormat(OutputFormat.TURTLE).
                        setSummaries(request.getSummary()).
                        setThumbnails(request.getThumbnail()).build();
            }
            result = enhance(finalRequest);
        } else if (responseType.isAssignableFrom((String.class))) {
            CloseableHttpResponse response = execEnhance(request);
            try {
                result = EntityUtils.toString(response.getEntity());
            } finally {
                response.close();
            }
        } else if(responseType.isAssignableFrom(InputStream.class)) {
            CloseableHttpResponse response = execEnhance(request);
            try {
                result = IOUtils.toBufferedInputStream(response.getEntity().getContent());
            } finally {
                response.close();
            }
        } else {
            throw new UnsupportedOperationException("Unsupported Response Type " + responseType.getCanonicalName());
        }
        return responseType.cast(result);
    }

    private CloseableHttpResponse execEnhance(AnalysisRequest request) {
        try {

            // Find out the target analysis
            String analysis = request.getAnalysis();
            if (analysis == null) {
                final List<String> analyses = status.getAnalyses();
                if (analyses.size() == 1) {
                    log.debug("using default analysis '{}'");
                    analysis = analyses.get(0);
                } else {
                    throw new IllegalArgumentException("not analysis found in the request");
                }
            }

            // Build uri
            UriBuilder uriBuilder = getEnhanceUriBuilder(analysis)                   // Change URI based on the analysis name
                    .queryParam(RedLink.IN, request.getInputFormat())                // InputFormat parameter
                    .queryParam(RedLink.OUT, request.getOutputFormat())              // OutputFormat parameter
                    .queryParam(SUMMARY, Boolean.toString(request.getSummary()))     // Entities' summaries parameter;
                    .queryParam(THUMBNAIL, Boolean.toString(request.getThumbnail())) // Entities' thumbnails parameter
                    .queryParam(LDPATH, request.getLDPathProgram());                 // LDPath program for dereferencing
            for (String field: request.getFieldsToDereference()) {
                uriBuilder = uriBuilder.queryParam(DEREF_FIELDS, field);             // Fields to be dereferenced
            }

            log.debug("Making analysis request to " + uriBuilder.build().toString());

            final URI target = credentials.buildUrl(uriBuilder);

            final String format = request.getInputMediaType().is(InputFormat.TEXT.value())
                                            ? InputFormat.TEXT.value().toString()
                                            : InputFormat.OCTETSTREAM.value().toString();

            final long pre = System.currentTimeMillis();
            final CloseableHttpResponse res = client.post(target, request.getContent(), request.getOutputMediaType().toString(), format);
            final int status = res.getStatusLine().getStatusCode();
            final String time = String.format("%,d", System.currentTimeMillis() - pre);
            log.debug("Server response time was {} ms (status={})", time, status);

            if (status >= 200 && status < 300) {
                return res;
            } else {
                String msg = String.format("Enhancement failed: HTTP error code %d, message: %s", status, res.getStatusLine().getReasonPhrase());
                log.error(msg);
                log.trace(EntityUtils.toString(res.getEntity()));
                throw new RuntimeException(msg);
            }
        } catch (IllegalArgumentException | URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Enhancements parseResponse(HttpResponse response) throws IOException {
        try {
            long pre = System.currentTimeMillis();
            EnhancementsParser parser = EnhancementsParserFactory.createParser(response);
            Enhancements enhancements = parser.createEnhancements();
            long time = System.currentTimeMillis() - pre;
            log.debug("Response Parse Time: " + time + " ms");
            return enhancements;
        } catch (EnhancementParserException e) {
            throw new RuntimeException("Enhancement failed: " + e.getMessage(), e);
        }

    }

    private final UriBuilder getEnhanceUriBuilder(String analysis) throws URISyntaxException {
        return initiateUriBuilding().path(PATH).path(analysis).path(ENHANCE);
    }

}
