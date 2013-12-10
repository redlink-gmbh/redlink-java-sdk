package io.redlink.sdk;

import org.apache.marmotta.client.model.sparql.SPARQLResult;
import org.junit.*;

public class DataTest extends GenericTest {
	
	private static RedLink.Data redlink;

    private static final String TEST_DATASET = "foaf";
	
	private static String QUERY_SELECT_ALL = "SELECT * WHERE { ?s ?p ?o }";
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Credentials credentials = buildCredentials(DataTest.class);
		Assume.assumeTrue(credentials.verify());
		redlink = RedLinkFactory.createDataClient(credentials);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		redlink = null;
	}

	@Test
	public void testDatasetSelect() {
        final SPARQLResult result = redlink.sparqlSelect(QUERY_SELECT_ALL, TEST_DATASET);
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
    }

    @Test
    public void testSelect() {
        final SPARQLResult result = redlink.sparqlSelect(QUERY_SELECT_ALL);
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
    }
    
}
