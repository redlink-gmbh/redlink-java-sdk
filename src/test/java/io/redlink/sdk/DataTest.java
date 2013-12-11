package io.redlink.sdk;

import org.apache.marmotta.client.model.sparql.SPARQLResult;
import org.junit.*;

public class DataTest extends GenericTest {

    private static final String TEST_DATASET = "foaf";

    private static final String ASSUME_QUERY = "DELETE WHERE { ?s ?p ?o }";
	
	private static final String QUERY_SELECT = "SELECT * WHERE { ?s ?p ?o }";

    private static final String QUERY_UPDATE = "INSERT DATA { <http://example.org/test> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://example.org/Test> }";

    private RedLink.Data redlink;

	@Before
	public void setUp() throws Exception {
		Credentials credentials = buildCredentials(DataTest.class);
        Assume.assumeNotNull(credentials);
		Assume.assumeTrue(credentials.verify());
		redlink = RedLinkFactory.createDataClient(credentials);
        Assume.assumeTrue(redlink.sparqlUpdate(ASSUME_QUERY, TEST_DATASET));
	}

	@After
	public void tearDown() throws Exception {
		redlink = null;
	}

    @Test
    public void testSelect() {
        final SPARQLResult result = redlink.sparqlSelect(QUERY_SELECT);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void testDatasetSelect() {
        final SPARQLResult result = redlink.sparqlSelect(QUERY_SELECT, TEST_DATASET);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void testDatasetUpdateSelect() {
        Assert.assertTrue(redlink.sparqlUpdate(QUERY_UPDATE, TEST_DATASET));
        final SPARQLResult result = redlink.sparqlSelect(QUERY_SELECT, TEST_DATASET);
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertTrue(result.getFieldNames().contains("s"));
        Assert.assertTrue(result.getFieldNames().contains("p"));
        Assert.assertTrue(result.getFieldNames().contains("o"));
        Assert.assertEquals("http://example.org/test", result.get(0).get("s").toString());
        Assert.assertEquals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", result.get(0).get("p").toString());
        Assert.assertEquals("http://example.org/Test", result.get(0).get("o").toString());
    }

}
