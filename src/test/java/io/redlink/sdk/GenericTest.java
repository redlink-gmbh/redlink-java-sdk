package io.redlink.sdk;

import io.redlink.sdk.impl.DefaultCredentials;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;

/**
 * Generic tests and utilities shared by other tests in the suite
 * 
 * @author sergio.fernandez@redlink.co
 *
 */
public class GenericTest {
	
	protected static final String API_KEY_FILE = "/api.key";
	
	/**
	 * Build the credentials for testing the SDK. It dynamically
	 * check for a file 'api.key' in the same path where the test
	 * is, creating a DefaultCredentials with the API key,
	 * CustomCredentials against the demo otherwise.
	 * 
	 * @return credentials
	 */
	protected static final Credentials buildCredentials() {
		return buildCredentials(GenericTest.class);
	}	
	
	/**
	 * Build the credentials for testing the SDK. It dynamically
	 * check for a file 'api.key' in the same path where the test
	 * is, creating a DefaultCredentials with the API key,
	 * CustomCredentials against the demo otherwise.
	 * 
	 * @param klass klass to use for classpath-based file lookup
	 * 
	 * @return credentials
	 */
	protected static final Credentials buildCredentials(Class<?> klass) {
        //return new DefaultCredentials("********");
		InputStream is = klass.getResourceAsStream(API_KEY_FILE);
		if (is != null) {
		    try {
				InputStreamReader isr = new InputStreamReader(is);
			    BufferedReader br = new BufferedReader(isr);
				String apiKey = br.readLine();
				return new DefaultCredentials(apiKey);
			} catch (IOException e) {
                throw new RuntimeException("error reading api key: " + e.getMessage());
			}
		} else {
			throw new RuntimeException("api key not found");
		}
	}

    @Test
    public void testVerifyCredentials() throws MalformedURLException {
        Credentials credentials = buildCredentials();
        Assert.assertTrue(credentials.verify());
    }

}
