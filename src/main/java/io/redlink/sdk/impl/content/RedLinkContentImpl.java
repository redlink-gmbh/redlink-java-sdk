package io.redlink.sdk.impl.content;

import io.redlink.sdk.Credentials;
import io.redlink.sdk.RedLink;
import io.redlink.sdk.impl.RedLinkAbstractImpl;
import io.redlink.sdk.impl.content.model.ContentItem;

import java.io.InputStream;
import java.net.MalformedURLException;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.core.BaseClientResponse;
import org.openrdf.model.Model;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.Rio;

public class RedLinkContentImpl extends RedLinkAbstractImpl implements RedLink.Content {
	
	public RedLinkContentImpl(Credentials credentials) {
		super(credentials);
	}
	
	@Override
	public String getContentStatus(String id) {
		try {
			String service = getContentUriBuilder(id).build().toURL().toString();
			return execGetContentStatus(service);
		} catch (MalformedURLException | IllegalArgumentException | UriBuilderException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Model getContent(String id) {
		try {
			String service = credentials.buildUrl(getContentUriBuilder(id)).toString();
			return execGetContent(service);
		} catch (MalformedURLException | IllegalArgumentException | UriBuilderException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ContentItem createContent(String content) {
		try {
			String service = credentials.buildUrl(getContentUriBuilder()).toString();
			return execCreateContent(service, content);
		} catch (MalformedURLException | IllegalArgumentException | UriBuilderException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ContentItem createContent(String id, String content) {
		try {
			String service = credentials.buildUrl(getContentUriBuilder(id)).toString();
			return execCreateContent(service, content);
		} catch (MalformedURLException | IllegalArgumentException | UriBuilderException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public ContentItem updateContent(String id, String content) {
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public boolean deleteContent(String id) {
		try {
			String service = credentials.buildUrl(getContentUriBuilder(id)).toString();
			return execDeleteContent(service);
		} catch (MalformedURLException | IllegalArgumentException | UriBuilderException e) {
			throw new RuntimeException(e);
		}
	}
	
	private final UriBuilder getContentUriBuilder() {
		return initiateUriBuilding().path(PATH);
	}

	private final UriBuilder getContentUriBuilder(String id) {
		return getContentUriBuilder().path(id);
	}
	
	private final String execGetContentStatus(String uri) {
		ClientRequest request = new ClientRequest(uri);
		request.accept("application/json");
		try {
			ClientResponse<String> response = request.get(String.class);
			if (response.getStatus() != 200) {
				throw new RuntimeException("Content status retrieval failed: HTTP error code " + response.getStatus());
			} else {
				return response.getEntity();
			}
		} catch (Exception e) {
			throw new RuntimeException("Content status retrieval failed: " + e.getMessage(), e);
		}
	}
	
	private final Model execGetContent(String uri) {
		RDFFormat format = RDFFormat.TURTLE;
		ClientRequest request = new ClientRequest(uri);
		request.accept(format.getDefaultMIMEType());
		try {
			ClientResponse<String> response = request.get(String.class);
			if (response.getStatus() != 200) {
				throw new RuntimeException("Content retrieval failed: HTTP error code " + response.getStatus());
			} else {
				BaseClientResponse<String> r = (BaseClientResponse<String>) response;
				InputStream stream = r.getStreamFactory().getInputStream();
				Model model = Rio.parse(stream, uri, format);
				response.releaseConnection();
				return model;
			}
		} catch (Exception e) {
			throw new RuntimeException("Content retrieval failed: " + e.getMessage(), e);
		}
	}
	
	private final ContentItem execCreateContent(String uri, String content) {
		ClientRequest request = new ClientRequest(uri);
		request.body("text/plain", content);
		request.accept("application/json");
		try {
			//TODO> use try-with-resources statement of java7 for properly releasing the connection
			//		which requires a RestEasy ClientResponse implementing AutoCloseable
			ClientResponse<ContentItem> response = request.post(ContentItem.class);
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
	
	private final boolean execDeleteContent(String uri) {
		ClientRequest request = new ClientRequest(uri);
		request.accept("application/json");
		try {
			ClientResponse<ContentItem> response = request.delete(ContentItem.class);
			if (response.getStatus() != 200) {
				return "deleted".equalsIgnoreCase(response.getEntity().getStatus());
			} else {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
	}
	
}
