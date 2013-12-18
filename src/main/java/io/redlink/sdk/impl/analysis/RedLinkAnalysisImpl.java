package io.redlink.sdk.impl.analysis;

import io.redlink.sdk.Credentials;
import io.redlink.sdk.RedLink;
import io.redlink.sdk.analysis.AnalysisRequest;
import io.redlink.sdk.impl.RedLinkAbstractImpl;
import io.redlink.sdk.impl.analysis.model.Enhancements;
import io.redlink.sdk.impl.analysis.model.EnhancementsParser;
import io.redlink.sdk.impl.analysis.model.EnhancementsParserFactory;

import java.net.MalformedURLException;

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
 * 
 * @author rafa.haro@redlink.co
 *
 */
public class RedLinkAnalysisImpl extends RedLinkAbstractImpl implements RedLink.Analysis {

	private static final Logger logger = LoggerFactory.getLogger(RedLinkAnalysisImpl.class);

	public RedLinkAnalysisImpl(Credentials credentials) {
		super(credentials);
	}

	@Override
	public Enhancements enhance(AnalysisRequest request) {
		try {
			WebTarget target = credentials.buildUrl(getEnhanceUriBuilder(request.getAnalysis()));
			target.queryParam("in", request.getInputFormat());
			target.queryParam("out", request.getOutputFormat());
			target.queryParam("summary", request.getSummary());

			Builder httpRequest = target.request();
			httpRequest.accept(request.getOutputFormat());
			MediaType type = MediaType.TEXT_PLAIN_TYPE;
			if(!request.isContentString())
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
