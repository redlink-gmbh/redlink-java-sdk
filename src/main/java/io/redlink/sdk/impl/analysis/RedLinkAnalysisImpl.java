package io.redlink.sdk.impl.analysis;

import io.redlink.sdk.Credentials;
import io.redlink.sdk.RedLink;
import io.redlink.sdk.impl.RedLinkAbstractImpl;
import io.redlink.sdk.impl.analysis.model.Enhancements;
import io.redlink.sdk.impl.analysis.model.EnhancementsParser;
import io.redlink.sdk.impl.analysis.model.EnhancementsParserFactory;
import io.redlink.sdk.util.FormatHelper;

import java.io.StringReader;
import java.net.MalformedURLException;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;

import org.openrdf.model.Model;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.rio.ParserConfig;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.BasicParserSettings;
import org.openrdf.rio.helpers.ParseErrorLogger;

public class RedLinkAnalysisImpl extends RedLinkAbstractImpl implements RedLink.Analysis {

	/**
	 * Managed Response Format
	 */
	private static final RDFFormat format = RDFFormat.TURTLE;

	public RedLinkAnalysisImpl(Credentials credentials) {
		super(credentials);
	}

	@Override
	public Enhancements enhance(String content) {
		try {
			WebTarget target = credentials.buildUrl(getEnhanceUriBuilder());
			return execEnhance(target, content);
		} catch (MalformedURLException | IllegalArgumentException | UriBuilderException e) {
			throw new RuntimeException(e);
		}
	}

	private final UriBuilder getEnhanceUriBuilder() {
		return initiateUriBuilding().path(PATH).queryParam(FORMAT, FormatHelper.getLabel(format));
	}

	private final Enhancements execEnhance(WebTarget target, String content) {
		Builder request = target.request();
		request.accept(format.getDefaultMIMEType());
		try {
			Response response = request.post(Entity.text(content));
			if (response.getStatus() != 200) {
				// TODO: improve this feedback from the sdk (400, 500, etc)
				throw new RuntimeException("Enhancement failed: HTTP error code " + response.getStatus());
			} else {
				String entity = response.readEntity(String.class);
				ParserConfig config = new ParserConfig();
				// Prevent malformed datetime values
				// TODO review - added to prevent errors when parsing invalid dates
				config.set(BasicParserSettings.VERIFY_DATATYPE_VALUES, false);
				// long pre = System.currentTimeMillis();
				Model model = Rio.parse(new StringReader(entity), target.getUri().toString(), format, config, ValueFactoryImpl.getInstance(), new ParseErrorLogger());
				// long time = System.currentTimeMillis() - pre;
				// System.out.println("RIO PARSE TIME: " + time + " ms");
				// pre = System.currentTimeMillis();

				// TODO Initialize Parser using strategy criteria -> Decoupled
				// model representation (could be Jena Model or even JSON-LD)
				EnhancementsParser parser = EnhancementsParserFactory.createDefaultParser(model);
				Enhancements enhancements = parser.createEnhancements();
				// time = System.currentTimeMillis() - pre;
				// System.out.println("ENHANCEMENTS PARSE TIME: " + time + " ms");
				// TODO Add a Parsing Time Manager (allow users to identify
				// problems in the Enhancer service)
				return enhancements;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Enhancement failed: " + e.getMessage(), e);
		}
	}

}
