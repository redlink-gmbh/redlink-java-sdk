package io.redlink.sdk;

import io.redlink.sdk.impl.CustomCredentials;
import io.redlink.sdk.impl.DefaultCredentials;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Abstract tests and utilities shared by other tests in the suite
 * 
 * @author sergio.fernandez@redlink.co
 *
 */
public class AbstractTests {
	
	protected static final String API_KEY_FILE = "/api.key";
	
	protected static final String DEMO_ENDPOINT = "http://demo.api.redlink.io/api";
	
	/**
	 * Build the credentials for testing the SDK. It dynamically
	 * check for a file 'api.key' in the same path where the test
	 * is, creating a DefaultCredentials with the API key,
	 * CustomCredentials against the demo otherwise.
	 * 
	 * @return credentials
	 */
	protected static final Credentials buildCredentials() {
		return buildCredentials(AbstractTests.class);
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
        //return new DefaultCredentials("hitsh2Ob6");
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

}
