package io.redlink.sdk;

import io.redlink.sdk.impl.data.model.LDPathResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.marmotta.client.model.sparql.SPARQLResult;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openrdf.model.Model;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.Rio;

public class DataTest extends GenericTest {

    private static final String TEST_DATASET = "test";

    private static final String TEST_FILE = "/test.rdf";

    private static final String TEST_BASE_URI = "http://data.redlink.io/355/foaf";

    private static final String TEST_RESOURCE = "http://data.redlink.io/355/foaf/joao";

    public static final int TEST_FILE_TRIPLES = 15;

    public static final int TEST_RESOUCE_TRIPLES = 8;

    private static final String QUERY_CLEAN = "DELETE WHERE { ?s ?p ?o }";

    private static final String QUERY_SELECT = "SELECT * WHERE { ?s ?p ?o }";

    private static final String QUERY_UPDATE = "INSERT DATA { <http://example.org/test> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://example.org/Test> }";
    public static final RDFFormat TEST_FILE_FORMAT = RDFFormat.RDFXML;

    private static RedLink.Data redlink;

    @BeforeClass
    public static void verifyConnection() throws MalformedURLException {
        Credentials credentials = buildCredentials(DataTest.class);
        Assume.assumeNotNull(credentials);
        Assume.assumeNotNull(credentials.getVersion());
        Assume.assumeTrue(credentials.verify());
        redlink = RedLinkFactory.createDataClient(credentials);
    }

    @Before
    public void setUp() throws Exception {
        Assume.assumeTrue(redlink.cleanDataset(TEST_DATASET));
    }

    @Test
    public void testImport() throws IOException, RDFParseException, RDFHandlerException {
        InputStream in = this.getClass().getResourceAsStream(TEST_FILE);
        final Model model = Rio.parse(in, TEST_BASE_URI, TEST_FILE_FORMAT);
        Assert.assertTrue(redlink.importDataset(model, TEST_DATASET));
        final SPARQLResult triples = redlink.sparqlSelect(QUERY_SELECT, TEST_DATASET);
        Assert.assertNotNull(triples);
        Assert.assertTrue(triples.size() >= TEST_FILE_TRIPLES);
        //TODO: more specific testing
    }

    @Test
    public void testImportFile() throws FileNotFoundException {
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
        Assert.assertTrue(redlink.importDataset(file, TEST_DATASET));
        final SPARQLResult triples = redlink.sparqlSelect(QUERY_SELECT, TEST_DATASET);
        Assert.assertNotNull(triples);
        Assert.assertTrue(triples.size() >= TEST_FILE_TRIPLES);
        //TODO: more specific testing
    }

    @Test
    public void testImportCleaningFile() throws FileNotFoundException {
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
        Assert.assertTrue(redlink.importDataset(file, TEST_DATASET, true));
        final SPARQLResult triples = redlink.sparqlSelect(QUERY_SELECT, TEST_DATASET);
        Assert.assertNotNull(triples);
        Assert.assertEquals(TEST_FILE_TRIPLES, triples.size());
        //TODO: more specific testing
    }

    @Test
    public void testImportStream() {
        InputStream in = this.getClass().getResourceAsStream(TEST_FILE);
        Assume.assumeNotNull(in);
        Assert.assertTrue(redlink.importDataset(in, TEST_FILE_FORMAT, TEST_DATASET));
        final SPARQLResult triples = redlink.sparqlSelect(QUERY_SELECT, TEST_DATASET);
        Assert.assertNotNull(triples);
        Assert.assertTrue(triples.size() >= TEST_FILE_TRIPLES);
        //TODO: more specific testing
    }

    @Test
    public void testImportCleaningStream() {
        InputStream in = this.getClass().getResourceAsStream(TEST_FILE);
        Assume.assumeNotNull(in);
        Assert.assertTrue(redlink.importDataset(in, RDFFormat.RDFXML, TEST_DATASET, true));
        final SPARQLResult triples = redlink.sparqlSelect(QUERY_SELECT, TEST_DATASET);
        Assert.assertNotNull(triples);
        Assert.assertEquals(TEST_FILE_TRIPLES, triples.size());
        //TODO: more specific testing
    }

    @Test
    public void testExport() {
        final int size = getCurrentSize(TEST_DATASET);
        Model model = redlink.exportDataset(TEST_DATASET);
        Assert.assertNotNull(model);
        Assert.assertEquals(size, model.size());
    }

    @Test
    public void testImportExport() {
        final int size = getCurrentSize(TEST_DATASET);
        InputStream in = this.getClass().getResourceAsStream(TEST_FILE);
        Assume.assumeNotNull(in);
        Assert.assertTrue(redlink.importDataset(in, RDFFormat.RDFXML, TEST_DATASET));
        Model model = redlink.exportDataset(TEST_DATASET);
        Assert.assertNotNull(model);
        Assert.assertEquals(size + TEST_FILE_TRIPLES, model.size());
    }

    @Test
    public void testImportCleaningExport() {
        InputStream in = this.getClass().getResourceAsStream(TEST_FILE);
        Assume.assumeNotNull(in);
        Assert.assertTrue(redlink.importDataset(in, RDFFormat.RDFXML, TEST_DATASET, true));
        Model model = redlink.exportDataset(TEST_DATASET);
        Assert.assertNotNull(model);
        Assert.assertEquals(TEST_FILE_TRIPLES, model.size());
    }

    @Test
    public void testDatasetClean() {
        Assert.assertTrue(redlink.sparqlUpdate(QUERY_CLEAN, TEST_DATASET));
        Assert.assertEquals(0, getCurrentSize(TEST_DATASET));
    }

    @Test
    public void testResourceImported() {
        //first import data
        InputStream in = this.getClass().getResourceAsStream(TEST_FILE);
        Assume.assumeNotNull(in);
        Assert.assertTrue(redlink.importDataset(in, RDFFormat.RDFXML, TEST_DATASET, true));
        final SPARQLResult triples = redlink.sparqlSelect(QUERY_SELECT, TEST_DATASET);
        Assert.assertNotNull(triples);
        Assert.assertEquals(TEST_FILE_TRIPLES, triples.size());

        //and then the actual test
        Model model = redlink.getResource(TEST_RESOURCE, TEST_DATASET);
        Assert.assertNotNull(model);
        Assert.assertTrue(model.size() < triples.size());
        Assert.assertEquals(TEST_RESOUCE_TRIPLES, model.size());
    }

    @Test
    public void testCleanGetResourceFail() {
        Assert.assertTrue(redlink.cleanDataset(TEST_DATASET));
        Assert.assertEquals(0, redlink.getResource(TEST_RESOURCE, TEST_DATASET).size());
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
        Assert.assertEquals(0, getCurrentSize(TEST_DATASET));
        Assert.assertTrue(redlink.sparqlUpdate(QUERY_UPDATE, TEST_DATASET));
        Assert.assertEquals(1, getCurrentSize(TEST_DATASET));
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

    @Test
    public void testLDPath() {
        InputStream in = this.getClass().getResourceAsStream(TEST_FILE);
        Assume.assumeNotNull(in);
        Assert.assertTrue(redlink.importDataset(in, RDFFormat.RDFXML, TEST_DATASET, true));
        final LDPathResult results = redlink.ldpath(TEST_RESOURCE, TEST_DATASET, "name = foaf:name[@en] :: xsd:string ;");
        Assert.assertNotNull(results);
        Assert.assertTrue(results.size() > 0);
        Assert.assertTrue(results.getFields().contains("name"));
        Assert.assertTrue(results.getResults("name").size() > 0);
        Assert.assertEquals("John Pereira", results.getResults("name").get(0).toString());
    }

    @Test
    public void testLDPathNoDataset() {
        InputStream in = this.getClass().getResourceAsStream(TEST_FILE);
        Assume.assumeNotNull(in);
        Assert.assertTrue(redlink.importDataset(in, RDFFormat.RDFXML, TEST_DATASET, true));
        final LDPathResult results = redlink.ldpath(TEST_RESOURCE, "name = foaf:name[@en] :: xsd:string ;");
        Assert.assertNotNull(results);
        Assert.assertTrue(results.size() > 0);
        Assert.assertTrue(results.getFields().contains("name"));
        Assert.assertTrue(results.getResults("name").size() > 0);
        Assert.assertEquals("John Pereira", results.getResults("name").get(0).toString());
    }

    private int getCurrentSize(String dataset) {
        return redlink.sparqlSelect(QUERY_SELECT, TEST_DATASET).size();
    }

    @Test
    public void testVerifyCredentials() throws MalformedURLException {
        Credentials credentials = buildCredentials();
        Assume.assumeNotNull(credentials);
        Assume.assumeNotNull(credentials.getVersion());
        Assert.assertTrue(credentials.verify());
    }

}
