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
import io.redlink.sdk.impl.DefaultCredentials;
import io.redlink.sdk.impl.RedLinkAbstractImpl;
import io.redlink.sdk.impl.analysis.model.EnhancementParserException;
import io.redlink.sdk.impl.analysis.model.Enhancements;
import io.redlink.sdk.impl.analysis.model.EnhancementsParser;
import io.redlink.sdk.impl.analysis.model.EnhancementsParserFactory;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public Enhancements enhance(AnalysisRequest request) {
        Response response = execEnhance(request);
        try {
            return parseResponse(response);
        } finally {
            response.close();
        }
    }

    private Response execEnhance(AnalysisRequest request) {
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

            // Build RESTEasy Endpoint
            WebTarget target = credentials.buildUrl(getEnhanceUriBuilder(analysis)); // Change URI based on the analysis name
            target = target.queryParam(RedLink.IN, request.getInputFormat()) // InputFormat parameter
                    .queryParam(RedLink.OUT, request.getOutputFormat()) // OutputFormat parameter
                    .queryParam(SUMMARY, request.getSummary()) // Entities' summaries parameter;
                    .queryParam(THUMBNAIL, request.getThumbnail()) // Entities' thumbnails parameter
                    .queryParam(LDPATH, request.getLDPathProgram()); // LDPath program for dereferencing
            if (!request.getFieldsToDereference().isEmpty()) {
                Iterator<String> it = request.getFieldsToDereference().iterator();
                while (it.hasNext()) {
                    target = target.queryParam(DEREF_FIELDS, it.next()); // Fields to be dereferenced
                }
            }

            // Accepted Media-Type setup
            Builder httpRequest = target.request();
            httpRequest.accept(request.getOutputMediaType());
            MediaType type = MediaType.TEXT_PLAIN_TYPE;
            if (!request.getInputMediaType().equals(InputFormat.TEXT.value()))
                type = MediaType.APPLICATION_OCTET_STREAM_TYPE;

            Entity<?> entity = Entity.entity(request.getContent(), type);

            if (log.isDebugEnabled()) {
                log.debug("Making Request to User Endpoint " + target.getUriBuilder().replaceQueryParam(DefaultCredentials.KEY_PARAM).build().toString());
            }
            long pre = System.currentTimeMillis();
            Response response = httpRequest.post(entity);
            long time = System.currentTimeMillis() - pre;
            log.debug("Server Response Time " + time + " ms. Status: " + response.getStatus());
            //log.debug("X-Redlink-Worker: {}", response.getHeaderString("X-Redlink-Worker"));

            if (response.getStatus() != 200) {
                String message = "Enhancement failed: HTTP error code "
                        + response.getStatus() + ". Message: " + response.getStatusInfo().getReasonPhrase();

                log.error(message);
                //log.debug("X-Redlink-Worker: {}", response.getHeaderString("X-Redlink-Worker"));
                String stackTrace = response.readEntity(String.class);
                log.trace(stackTrace);
                throw new RuntimeException(message);
            }

            return response;
        } catch (MalformedURLException | IllegalArgumentException | UriBuilderException e) {
            throw new RuntimeException(e);
        }
    }

    private Enhancements parseResponse(Response response) {
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

    private final UriBuilder getEnhanceUriBuilder(String analysis) {
        return initiateUriBuilding().path(PATH).path(analysis).path(ENHANCE);
    }

//    private final Enhancements execEnhance(String uri, Builder request, Entity<?> entity) {
//        try {
//
//            log.debug("Making Request to User Endpoint " + uri);
//            long pre = System.currentTimeMillis();
//            Response response = request.post(entity);
//            long time = System.currentTimeMillis() - pre;
//            log.debug("Server Response Time " + time + " ms. Status: " + response.getStatus());
//
//            if (response.getStatus() != 200) {
//                String message = "Enhancement failed: HTTP error code "
//                        + response.getStatus() + ". Message: " + response.getStatusInfo().getReasonPhrase();
//
//                log.error(message);
//                String stackTrace = response.readEntity(String.class);
//                //log.debug("X-Redlink-Worker: {}", response.getHeaderString("X-Redlink-Worker"));
//                log.trace(stackTrace);
//                throw new RuntimeException(message);
//            } else {
//                pre = System.currentTimeMillis();
//                EnhancementsParser parser = EnhancementsParserFactory.createParser(response);
//                Enhancements enhancements = parser.createEnhancements();
//                time = System.currentTimeMillis() - pre;
//                log.debug("Response Parse Time: " + time + " ms");
//                return enhancements;
//            }
//        } catch (Exception e) {
//            //e.printStackTrace();
//            throw new RuntimeException("Enhancement failed: " + e.getMessage(), e);
//        }
//    }

    /*
     * (non-Javadoc)
     * @see io.redlink.sdk.RedLink.Analysis#enhance(io.redlink.sdk.analysis.AnalysisRequest, java.lang.Class)
     */
    @Override
    public <T> T enhance(AnalysisRequest request, Class<T> responseType) {
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
            Response response = execEnhance(request);
            try {
                result = response.readEntity(String.class);
            } finally {
                response.close();
            }
        } else if(responseType.isAssignableFrom(InputStream.class)){
            Response response = execEnhance(request);
            try {
                result = response.readEntity(InputStream.class);
            } catch (ProcessingException | IllegalStateException e){
                response.close(); //ensure the close is called
                throw e;
            }
        } else {
            throw new UnsupportedOperationException("Unsupported Response Type " + responseType.getCanonicalName());
        }

        return responseType.cast(result);
    }

}
