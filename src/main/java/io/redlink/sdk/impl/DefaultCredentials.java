package io.redlink.sdk.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

/**
 * Default credentials against the public api
 * 
 * @author sergiofernandez@redlink.co
 * @author jakob.frank@redlink.co
 *
 */
public final class DefaultCredentials extends AbstractCredentials {
	
	private static final String ENDPOINT = "https://beta.redlink.io";
	
	private static final String VERSION = "1.0-ALPHA"; //TODO: versions align between api and sdk
	
	private static final String KEY_PARAM = "key";
	
	public DefaultCredentials(String apiKey){
		super(ENDPOINT, VERSION, apiKey);
	}
	
	public boolean verify() {
		throw new RuntimeException("unimplemented");
	}

	@Override
	public WebTarget buildUrl(UriBuilder builder) throws MalformedURLException, IllegalArgumentException, UriBuilderException {
		// enable the ssl/tls stuff
		SSLContext ctx = null;
		try {
			// load the certificate
	        InputStream fis = this.getClass().getResourceAsStream("/redlink-CA.crt");
	        CertificateFactory cf = CertificateFactory.getInstance("X.509");
	        Certificate cert = cf.generateCertificate(fis);
	
	        // Load the keyStore that includes self-signed cert as a "trusted" entry
	        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType()); 
	        keyStore.load(null, null);
	        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
	        keyStore.setCertificateEntry("redlink-CA", cert);
	        tmf.init(keyStore);
	        ctx = SSLContext.getInstance("TLS");
	        ctx.init(null, tmf.getTrustManagers(), null);
	        //SSLSocketFactory sslFactory = ctx.getSocketFactory();
		} catch (CertificateException | KeyStoreException | NoSuchAlgorithmException | KeyManagementException | IOException e) {
			e.printStackTrace();
		}
		
		// build the client
		ResteasyClientBuilder clientBuilder = new ResteasyClientBuilder();
		if (ctx == null) {
			clientBuilder.disableTrustManager();
		} else {
			clientBuilder.sslContext(ctx);
		}
		URI uri = builder.queryParam(KEY_PARAM, apiKey).build();
		return clientBuilder.build().target(uri.toString());
	}
	
}
