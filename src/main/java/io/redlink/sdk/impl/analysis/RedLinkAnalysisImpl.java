package io.redlink.sdk.impl.analysis;

import io.redlink.sdk.Credentials;
import io.redlink.sdk.RedLink;
import io.redlink.sdk.analysis.AnalysisRequest;
import io.redlink.sdk.analysis.AnalysisRequest.InputFormat;
import io.redlink.sdk.analysis.AnalysisRequest.OutputFormat;
import io.redlink.sdk.impl.RedLinkAbstractImpl;
import io.redlink.sdk.impl.analysis.model.Enhancements;
import io.redlink.sdk.impl.analysis.model.EnhancementsParser;
import io.redlink.sdk.impl.analysis.model.EnhancementsParserFactory;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Map.Entry;

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
 * @author rafa.haro@redlink.co
 *
 */
public class RedLinkAnalysisImpl extends RedLinkAbstractImpl implements RedLink.Analysis {

	private static final Logger logger = LoggerFactory.getLogger(RedLinkAnalysisImpl.class);

	public RedLinkAnalysisImpl(Credentials credentials) {
		super(credentials);
	}

	/*
	 * (non-Javadoc)
	 * @see io.redlink.sdk.RedLink.Analysis#enhance(io.redlink.sdk.analysis.AnalysisRequest)
	 */
	@Override
	public Enhancements enhance(AnalysisRequest request) {
		try {
			
			// Build RESTEasy Endpoint
			WebTarget target = credentials.buildUrl(getEnhanceUriBuilder(request.getAnalysis())); // Change URI based on the analysis name
			target.queryParam("in", request.getInputFormat()); // InputFormat parameter
			target.queryParam("out", request.getOutputFormat()); // OutputFormat parameter
			target.queryParam("summary", request.getSummary()); // Entities' summaries parameter
			target.queryParam("thumbnail", request.getThumbnail()); // Entities' thumbnails parameter

			// Accepted Media-Type setup
			Builder httpRequest = target.request();
			httpRequest.accept(request.getOutputMediaType());
			MediaType type = MediaType.TEXT_PLAIN_TYPE;
			if(!request.isContentString() && 
					!request.getInputMediaType().equals(InputFormat.TEXT.value()))
				type = MediaType.APPLICATION_OCTET_STREAM_TYPE;
			
			Entity<?> entity = Entity.entity(request.getContent(), type);

			return execEnhance(target.getUri().toString(), httpRequest, entity);
		} catch (MalformedURLException | IllegalArgumentException | UriBuilderException e) {
			throw new RuntimeException(e);
		}
	}

	private final UriBuilder getEnhanceUriBuilder(String analysis) {
		return initiateUriBuilding().path(PATH).path(analysis).path(ENHANCE);
	}

	private final Enhancements execEnhance(String uri, Builder request, Entity<?> entity) {
		try {
			
			logger.info("Making Request to User Endpoint " + uri);
			long pre = System.currentTimeMillis();
			Response response = request.post(entity);
			long time = System.currentTimeMillis() - pre;
			logger.info("Server Response Time " + time + " ms. Status: " + response.getStatus());

			if (response.getStatus() != 200) {
				String message = "Enhancement failed: HTTP error code " 
						+ response.getStatus() + ". Message: " + response.getStatusInfo().getReasonPhrase();
				
				logger.error(message);
				String stackTrace = response.readEntity(String.class);
				logger.debug("X-Redlink-Worker: " + 
						response.getHeaderString("X-Redlink-Worker"));
				logger.debug(stackTrace);
				throw new RuntimeException(message);
			} else {
				pre = System.currentTimeMillis();
				EnhancementsParser parser = EnhancementsParserFactory.createParser(response);
				Enhancements enhancements = parser.createEnhancements();
				time = System.currentTimeMillis() - pre;
				logger.info("Response Parse Time: " + time + " ms");
				return enhancements;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Enhancement failed: " + e.getMessage(), e);
		}
	}

}
