/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.redlink.sdk;

import io.redlink.sdk.impl.Status;
import io.redlink.sdk.impl.data.model.LDPathResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.marmotta.client.model.sparql.SPARQLResult;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.Rio;

import com.jayway.restassured.RestAssured;

public class DataTest extends GenericTest {

    private static final String TEST_DATASET = "test";

    private static final String TEST_FILE = "/test.rdf";

    private static final String TEST_RESOURCE = "joao";

    public static final int TEST_FILE_TRIPLES = 15;

    public static final int TEST_RESOUCE_TRIPLES = 8;

    private static final String QUERY_CLEAN = "DELETE WHERE { ?s ?p ?o }";

    private static final String QUERY_SELECT = "SELECT * WHERE { ?s ?p ?o }";

    private static final String QUERY_CONSTRUCT = "CONSTRUCT { ?s ?p ?o } WHERE { ?s ?p ?o }";

    private static final String QUERY_UPDATE = "INSERT DATA { <http://example.org/test> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://example.org/Test> }";

    public static final RDFFormat TEST_FILE_FORMAT = RDFFormat.RDFXML;

    private static Credentials credentials;
    private static Status status;
    private static RedLink.Data redlink;

    @BeforeClass
    public static void beforeClass() throws IOException, URISyntaxException {
        credentials = buildCredentials(DataTest.class);
        Assume.assumeNotNull(credentials);
        Assume.assumeNotNull(credentials.getVersion());
        Assume.assumeTrue(credentials.verify());

        status = credentials.getStatus();
        redlink = RedLinkFactory.createDataClient(credentials);
    }

    @AfterClass
    public static void afterClass() {
        credentials = null;
        status = null;
        redlink = null;
    }

    @Before
    public void setUp() throws Exception {
        Assume.assumeTrue(redlink.cleanDataset(TEST_DATASET));
    }


    @Test
    public void testVerifyKey() throws IOException, URISyntaxException {
        Credentials credentials = buildCredentials();
        Assert.assertNotNull(credentials);
        Assert.assertNotNull(credentials.getVersion());
        Assert.assertTrue("Credentials cannot be verified", credentials.verify());
        status = credentials.getStatus();
        Assert.assertNotNull(status);
        Assert.assertTrue(TEST_DATASET + " not found", status.getDatasets().contains(TEST_DATASET));
    }

    @Test
    public void testImport() throws IOException, RDFParseException, RDFHandlerException, URISyntaxException {
        InputStream in = this.getClass().getResourceAsStream(TEST_FILE);
        Assume.assumeNotNull(in);
        String base = buildDatasetBaseUri(credentials, status.getOwner(), TEST_DATASET);
        final Model model = Rio.parse(in, base, TEST_FILE_FORMAT);
        Assert.assertTrue(redlink.importDataset(model, TEST_DATASET));
        final SPARQLResult triples = redlink.sparqlTupleQuery(QUERY_SELECT, TEST_DATASET);
        Assert.assertNotNull(triples);
        Assert.assertTrue(triples.size() >= TEST_FILE_TRIPLES);
        //TODO: more specific testing
    }

    @Test
    public void testImportCheckDataHub() throws IOException, RDFParseException, RDFHandlerException, URISyntaxException {
        InputStream in = this.getClass().getResourceAsStream(TEST_FILE);
        Assume.assumeNotNull(in);
        String base = buildDatasetBaseUri(credentials, status.getOwner(), TEST_DATASET);
        final Model model = Rio.parse(in, base, TEST_FILE_FORMAT);
        Assert.assertTrue(redlink.importDataset(model, TEST_DATASET));

        RestAssured
            .given()
                .header("Accept", "text/turtle")
            .expect()
                .statusCode(200)
                .contentType("text/turtle")
            .get(base + TEST_RESOURCE);
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
        final SPARQLResult triples = redlink.sparqlTupleQuery(QUERY_SELECT, TEST_DATASET);
        Assert.assertNotNull(triples);
        Assert.assertTrue("result size was smaller than expected", triples.size() >= TEST_FILE_TRIPLES);
        //TODO: more specific testing
    }

    //@Test
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
        final SPARQLResult triples = redlink.sparqlTupleQuery(QUERY_SELECT, TEST_DATASET);
        Assert.assertNotNull(triples);
        Assert.assertTrue(triples.size() > 0);
        //TODO: more specific testing
    }

    @Test
    public void testImportStream() {
        InputStream in = this.getClass().getResourceAsStream(TEST_FILE);
        Assume.assumeNotNull(in);
        Assert.assertTrue(redlink.importDataset(in, TEST_FILE_FORMAT, TEST_DATASET));
        final SPARQLResult triples = redlink.sparqlTupleQuery(QUERY_SELECT, TEST_DATASET);
        Assert.assertNotNull(triples);
        Assert.assertTrue(triples.size() > 0);
        Assert.assertTrue(triples.size() >= TEST_FILE_TRIPLES);
        //TODO: more specific testing
    }

    @Test
    public void testImportCleaningStream() {
        InputStream in = this.getClass().getResourceAsStream(TEST_FILE);
        Assume.assumeNotNull(in);
        Assert.assertTrue(redlink.importDataset(in, RDFFormat.RDFXML, TEST_DATASET, true));
        final SPARQLResult triples = redlink.sparqlTupleQuery(QUERY_SELECT, TEST_DATASET);
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
    public void testResourceImportedInDataset() throws IOException, RDFParseException, RDFHandlerException, URISyntaxException {
        //first import data
        InputStream in = this.getClass().getResourceAsStream(TEST_FILE);
        Assume.assumeNotNull(in);
        final Model model = Rio.parse(in, buildDatasetBaseUri(credentials, status.getOwner(), TEST_DATASET), TEST_FILE_FORMAT);
        Assert.assertTrue(redlink.importDataset(model, TEST_DATASET, true));
        final SPARQLResult triples = redlink.sparqlTupleQuery(QUERY_SELECT, TEST_DATASET);
        Assert.assertNotNull(triples);
        Assert.assertEquals(TEST_FILE_TRIPLES, triples.size());

        //and then the actual test
        String resource = buildDatasetBaseUri(credentials, status.getOwner(), TEST_DATASET) + TEST_RESOURCE;
        Model resourceModel = redlink.getResource(resource, TEST_DATASET);
        Assert.assertNotNull(resourceModel);
        Assert.assertTrue(resourceModel.size() < triples.size());
        Assert.assertEquals(TEST_RESOUCE_TRIPLES, resourceModel.size());
    }

    @Test
    public void testResourceImported() throws IOException, RDFParseException, RDFHandlerException, URISyntaxException {
        //first import data
        InputStream in = this.getClass().getResourceAsStream(TEST_FILE);
        Assume.assumeNotNull(in);
        final Model model = Rio.parse(in, buildDatasetBaseUri(credentials, status.getOwner(), TEST_DATASET), TEST_FILE_FORMAT);
        Assert.assertTrue(redlink.importDataset(model, TEST_DATASET, true));
        final SPARQLResult triples = redlink.sparqlTupleQuery(QUERY_SELECT, TEST_DATASET);
        Assert.assertNotNull(triples);
        Assert.assertEquals(TEST_FILE_TRIPLES, triples.size());

        //and then the actual test
        final String resource = buildDatasetBaseUri(credentials, status.getOwner(), TEST_DATASET) + TEST_RESOURCE;
        final ValueFactoryImpl vf = new ValueFactoryImpl();
        final Resource sesameResource = vf.createURI(resource);
        final Model resouceModel = new LinkedHashModel();
        resouceModel.add(sesameResource, vf.createURI("http://example.org/foo"), vf.createLiteral("foo"));
        Assert.assertTrue(redlink.importResource(resource, model, TEST_DATASET, true));
        final Model resourceModelFromApi = redlink.getResource(resource, TEST_DATASET);
        Assert.assertNotNull(resourceModelFromApi);
        Assert.assertEquals(2, resourceModelFromApi.size());
    }

    @Test
    public void testCleanGetResourceFail() throws URISyntaxException {
        Assert.assertTrue(redlink.cleanDataset(TEST_DATASET));
        String resource = buildDatasetBaseUri(credentials, status.getOwner(), TEST_DATASET) + TEST_RESOURCE;
        Assert.assertEquals(0, redlink.getResource(resource, TEST_DATASET).size());
    }

    @Test
    public void testSelect() {
        final SPARQLResult result = redlink.sparqlTupleQuery(QUERY_SELECT);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.size() >= 0);
    }

    @Test
    public void testDatasetSelect() {
        Assume.assumeTrue(redlink.sparqlUpdate(QUERY_CLEAN, TEST_DATASET));
        final SPARQLResult result = redlink.sparqlTupleQuery(QUERY_SELECT, TEST_DATASET);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void testDatasetCleanImportSelect() {
        Assume.assumeTrue(redlink.sparqlUpdate(QUERY_CLEAN, TEST_DATASET));
        Assert.assertEquals(0, getCurrentSize(TEST_DATASET));
        Assert.assertTrue(redlink.sparqlUpdate(QUERY_UPDATE, TEST_DATASET));
        Assert.assertEquals(1, getCurrentSize(TEST_DATASET));
        final SPARQLResult result = redlink.sparqlTupleQuery(QUERY_SELECT, TEST_DATASET);
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
    public void testDatasetCleanImportConstruct() {
        Assume.assumeTrue(redlink.sparqlUpdate(QUERY_CLEAN, TEST_DATASET));
        Assert.assertEquals(0, getCurrentSize(TEST_DATASET));
        Assert.assertTrue(redlink.sparqlUpdate(QUERY_UPDATE, TEST_DATASET));
        Assert.assertEquals(1, getCurrentSize(TEST_DATASET));
        final Model result = redlink.sparqlGraphQuery(QUERY_CONSTRUCT, TEST_DATASET);
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(1, result.size());
    }

    @Test
    public void testDatasetCleanImportDescribe() throws IOException, RDFParseException, RDFHandlerException, URISyntaxException {
        Assume.assumeTrue(redlink.sparqlUpdate(QUERY_CLEAN, TEST_DATASET));
        Assert.assertEquals(0, getCurrentSize(TEST_DATASET));
        InputStream in = this.getClass().getResourceAsStream(TEST_FILE);
        Assume.assumeNotNull(in);

        String dataset = buildDatasetBaseUri(credentials, status.getOwner(), TEST_DATASET);
        final Model model = Rio.parse(in, dataset, TEST_FILE_FORMAT);
        Assert.assertTrue(redlink.importDataset(model, TEST_DATASET));

        String resource = dataset + TEST_RESOURCE;
        final Model result = redlink.sparqlGraphQuery("DESCRIBE <" + resource + ">", TEST_DATASET);
        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(8, result.size());
    }

    @Test
    public void testLDPath() throws IOException, RDFParseException, RDFHandlerException, URISyntaxException {
        InputStream in = this.getClass().getResourceAsStream(TEST_FILE);
        Assume.assumeNotNull(in);
        final Model model = Rio.parse(in, buildDatasetBaseUri(credentials, status.getOwner(), TEST_DATASET), TEST_FILE_FORMAT);
        Assert.assertTrue(redlink.importDataset(model, TEST_DATASET, true));
        String resource = buildDatasetBaseUri(credentials, status.getOwner(), TEST_DATASET) + TEST_RESOURCE;
        final LDPathResult results = redlink.ldpath(resource, TEST_DATASET, "name = foaf:name[@en] :: xsd:string ;");
        Assert.assertNotNull(results);
        Assert.assertTrue(results.size() > 0);
        Assert.assertTrue(results.getFields().contains("name"));
        Assert.assertTrue(results.getResults("name").size() > 0);
        Assert.assertEquals("John Pereira", results.getResults("name").get(0).toString());
    }

    @Test
    public void testLDPathNoDataset() throws IOException, RDFParseException, RDFHandlerException, URISyntaxException {
        InputStream in = this.getClass().getResourceAsStream(TEST_FILE);
        Assume.assumeNotNull(in);
        final Model model = Rio.parse(in, buildDatasetBaseUri(credentials, status.getOwner(), TEST_DATASET), TEST_FILE_FORMAT);
        Assert.assertTrue(redlink.importDataset(model, TEST_DATASET, true));
        String resource = buildDatasetBaseUri(credentials, status.getOwner(), TEST_DATASET) + TEST_RESOURCE;
        final LDPathResult results = redlink.ldpath(resource, "name = foaf:name[@en] :: xsd:string ;");
        Assert.assertNotNull(results);
        Assert.assertTrue(results.size() > 0);
        Assert.assertTrue(results.getFields().contains("name"));
        Assert.assertTrue(results.getResults("name").size() > 0);
        Assert.assertEquals("John Pereira", results.getResults("name").get(0).toString());
    }

    @Test
    public void testRelease() {
        Assert.assertTrue(redlink.release(TEST_DATASET));
    }

    private int getCurrentSize(String dataset) {
        return redlink.sparqlTupleQuery(QUERY_SELECT, TEST_DATASET).size();
    }

}
