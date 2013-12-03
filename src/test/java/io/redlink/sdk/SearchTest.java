package io.redlink.sdk;

import io.redlink.sdk.impl.CustomCredentials;
import io.redlink.sdk.impl.search.model.SearchResults;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

public class SearchTest {
	
	private static RedLink.Search redlink;

    private static final String TEST_CORE = "test";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Credentials credentials = new CustomCredentials();
		Assume.assumeTrue(credentials.verify());
		redlink = RedLinkFactory.createSearchClient(credentials);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		redlink = null;
	}
	
	@Test
	public void search() {
		SearchResults results = redlink.search("example", TEST_CORE);
		Assert.assertNotNull(results);
		//Assert.assertTrue(results.hasNext());
		int n = 0;
		while (results.hasNext()) {
			results.remove();
			n++;
		}
		Assert.assertEquals(results.getTotalResults(), n);
		Assert.assertTrue(results.getTotalResults() <= results.getItemsPerPage());
	}
	
}
