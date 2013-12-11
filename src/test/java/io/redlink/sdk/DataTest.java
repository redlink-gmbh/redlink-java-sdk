package io.redlink.sdk;

import org.apache.marmotta.client.model.sparql.SPARQLResult;
import org.junit.*;
import org.openrdf.rio.RDFFormat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

public class DataTest extends GenericTest {

    private static final String TEST_DATASET = "foaf";

    private static final String TEST_FILE = "/test.rdf";

    public static final int TEST_FILE_TRIPLES = 15;

    private static final String QUERY_CLEAN = "DELETE WHERE { ?s ?p ?o }";

    private static final String QUERY_SELECT = "SELECT * WHERE { ?s ?p ?o }";

    private static final String QUERY_UPDATE = "INSERT DATA { <http://example.org/test> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://example.org/Test> }";

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
    public void testImportFile() throws FileNotFoundException {
        testImportFile(false);
    }

    @Test
    public void testCleanImportFile() throws FileNotFoundException {
        testImportFile(true);
    }

    private void testImportFile(boolean cleanBefore) throws FileNotFoundException {
        final int initialSize = (cleanBefore ? 0 : getCurrentSize(TEST_DATASET));
        URL url = this.getClass().getResource(TEST_FILE);
        Assume.assumeNotNull(url);
        File file;
        try {
            file = new File(url.toURI());
        } catch (URISyntaxException e) {
            file = new File(url.getPath());
        }
        Assume.assumeNotNull(file);
        Assume.assumeTrue(file.exists());
        testImport(redlink.importDataset(file, TEST_DATASET, cleanBefore), initialSize);
    }

    @Test
    public void testImportStream() {
        testImportStream(false);
    }

    @Test
    public void testCleanImportStream() {
        testImportStream(true);
    }

    private void testImportStream(boolean cleanBefore) {
        final int initialSize = (cleanBefore ? 0 : getCurrentSize(TEST_DATASET));
        InputStream in = this.getClass().getResourceAsStream(TEST_FILE);
        Assume.assumeNotNull(in);
        testImport(redlink.importDataset(in, RDFFormat.RDFXML, TEST_DATASET, cleanBefore), initialSize);
    }

    private void testImport(boolean imported) {
        testImport(imported, 0);
    }

    private void testImport(boolean imported, int previousSize) {
        Assert.assertTrue(imported);
        final SPARQLResult triples = redlink.sparqlSelect(QUERY_SELECT, TEST_DATASET);
        Assert.assertNotNull(triples);
        Assert.assertEquals(previousSize + TEST_FILE_TRIPLES, triples.size());
        //TODO: more specific testing
    }

    private int getCurrentSize(String dataset) {
        return redlink.sparqlSelect(QUERY_SELECT, TEST_DATASET).size();
    }

    @Test
    public void testSelect() {
        final SPARQLResult result = redlink.sparqlSelect(QUERY_SELECT);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.size() >= 0);
    }

    @Test
    public void testDatasetSelect() {
        Assume.assumeTrue(redlink.sparqlUpdate(QUERY_CLEAN, TEST_DATASET));
        final SPARQLResult result = redlink.sparqlSelect(QUERY_SELECT, TEST_DATASET);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void testDatasetCleanImportSelect() {
        Assume.assumeTrue(redlink.sparqlUpdate(QUERY_CLEAN, TEST_DATASET));
        final int initialSize = getCurrentSize(TEST_DATASET);
        Assert.assertEquals(0, initialSize);
        Assert.assertTrue(redlink.sparqlUpdate(QUERY_UPDATE, TEST_DATASET));
        final SPARQLResult result = redlink.sparqlSelect(QUERY_SELECT, TEST_DATASET);
        Assert.assertNotNull(result);
        Assert.assertTrue(getCurrentSize(TEST_DATASET) > initialSize);
        Assert.assertFalse(result.isEmpty());
        Assert.assertTrue(result.getFieldNames().contains("s"));
        Assert.assertTrue(result.getFieldNames().contains("p"));
        Assert.assertTrue(result.getFieldNames().contains("o"));
        Assert.assertEquals("http://example.org/test", result.get(0).get("s").toString());
        Assert.assertEquals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", result.get(0).get("p").toString());
        Assert.assertEquals("http://example.org/Test", result.get(0).get("o").toString());
    }

}
