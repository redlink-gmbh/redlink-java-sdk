package io.redlink.sdk;

import org.apache.marmotta.client.model.sparql.SPARQLResult;
import org.junit.*;

public class DataTest extends GenericTest {

    private static final String TEST_DATASET = "foaf";
	
	private static final String QUERY_SELECT_ALL = "SELECT * WHERE { ?s ?p ?o }";

    private RedLink.Data redlink;

	@Before
	public void setUp() throws Exception {
		Credentials credentials = buildCredentials(DataTest.class);
        Assume.assumeNotNull(credentials);
		Assume.assumeTrue(credentials.verify());
		redlink = RedLinkFactory.createDataClient(credentials);
	}

	@After
	public void tearDown() throws Exception {
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
