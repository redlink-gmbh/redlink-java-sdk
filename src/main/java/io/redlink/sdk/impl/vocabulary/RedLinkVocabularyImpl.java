package io.redlink.sdk.impl.vocabulary;

import io.redlink.sdk.Credentials;
import io.redlink.sdk.RedLink;
import io.redlink.sdk.impl.RedLinkAbstractImpl;
import io.redlink.sdk.impl.vocabulary.model.Profile;
import io.redlink.sdk.util.FormatHelper;

import java.net.MalformedURLException;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.openrdf.rio.RDFFormat;

public class RedLinkVocabularyImpl extends RedLinkAbstractImpl implements RedLink.Vocabulary {

	public RedLinkVocabularyImpl(Credentials credentials) {
		super(credentials);
	}
	
	@Override
	public Vocabulary createVocabulary(String vocabulary, RDFFormat format, Profile profile) {
		try {
			String service = credentials.buildUrl(getVocabularyUriBuilder(format, profile)).toString();
			return execCreateVocabulary(service, vocabulary, format);
		} catch (MalformedURLException | IllegalArgumentException | UriBuilderException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Vocabulary createVocabulary(String id, String vocabulary, RDFFormat format, Profile profile) {
		try {
			String service = credentials.buildUrl(getVocabularyUriBuilder(id, format, profile)).toString();
			return execCreateVocabulary(service, vocabulary, format);
		} catch (MalformedURLException | IllegalArgumentException | UriBuilderException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean deleteVocabulary(String id) {
		try {
			String service = credentials.buildUrl(getVocabularyUriBuilder(id)).toString();
			return execDeleteVocabulary(service);
		} catch (MalformedURLException | IllegalArgumentException | UriBuilderException e) {
			throw new RuntimeException(e);
		}
	}
	
	private final UriBuilder getVocabularyUriBuilder(RDFFormat format, Profile profile) {
		return initiateUriBuilding().path(PATH).
				queryParam(FORMAT, FormatHelper.getLabel(format)).
				queryParam(PROFILE, profile.name());
	}
	
	private final UriBuilder getVocabularyUriBuilder(String id) {
		return initiateUriBuilding().path(PATH).path(id);
	}

	private final UriBuilder getVocabularyUriBuilder(String id, RDFFormat format, Profile profile) {
		return getVocabularyUriBuilder(id).
				queryParam(FORMAT, FormatHelper.getLabel(format)).
				queryParam(PROFILE, profile.name());
	}
	
	private final Vocabulary execCreateVocabulary(String uri, String vocabulary, RDFFormat format) {
		ClientRequest request = new ClientRequest(uri);
		request.body(format.getDefaultMIMEType(), vocabulary);
		request.accept("application/json");
		try {
			//TODO> use try-with-resources statement of java7 for properly releasing the connection
			//		which requires a RestEasy ClientResponse implementing AutoCloseable
			ClientResponse<Vocabulary> response = request.post(Vocabulary.class);
			if (response.getStatus() != 201) {
				//TODO: improve this feedback from the sdk (405, 415, etc)
				throw new RuntimeException("Content creation failed: HTTP error code " + response.getStatus());
			} else {
				return response.getEntity();
			}
		} catch (Exception e) {
			throw new RuntimeException("Content creation failed: " + e.getMessage(), e);
		}
	}
	
	private final boolean execDeleteVocabulary(String uri) {
		ClientRequest request = new ClientRequest(uri);
		try {
			ClientResponse<String> response = request.delete(String.class);
			if (response.getStatus() != 200) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
	}
	
}
