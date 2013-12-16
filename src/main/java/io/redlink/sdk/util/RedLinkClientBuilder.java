package io.redlink.sdk.util;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.concurrent.TimeUnit;

/**
 * Extended implementation of the ResteasyClientBuilder
 *
 * @author sergio.fernandez@redlink.co
 */
public class RedLinkClientBuilder extends ResteasyClientBuilder {

    private static Logger log = LoggerFactory.getLogger(RedLinkClientBuilder.class);

    public static final int DEFAULT_TIMEOUT = 10;

    /**
     * Build a RedLink Rest Client with the default timeout for requests
     *
     */
    public RedLinkClientBuilder() {
        this(DEFAULT_TIMEOUT);
    }


    /**
     * Build a RedLink Rest Client with a custom timeout for requests
     *
     * @param timeout requests' timeout in seconds
     */
    public RedLinkClientBuilder(long timeout) {
        super();
        this.establishConnectionTimeout(timeout, TimeUnit.SECONDS);

        //ssl/tls stuff
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
        if (ctx == null) {
            disableTrustManager();
        } else {
            sslContext(ctx);
        }

    }
}
