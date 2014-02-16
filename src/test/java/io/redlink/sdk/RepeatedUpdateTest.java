package io.redlink.sdk;

import com.google.code.tempusfugit.concurrency.ConcurrentRule;
import com.google.code.tempusfugit.concurrency.RepeatingRule;
import com.google.code.tempusfugit.concurrency.annotations.Concurrent;
import com.google.code.tempusfugit.concurrency.annotations.Repeating;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.*;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openrdf.model.*;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.TreeModel;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.rio.RDFHandlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;

/**
 * Add file description here!
 *
 * @author Sebastian Schaffert (sschaffert@apache.org)
 */
public class RepeatedUpdateTest extends GenericTest {

    private static final String TEST_DATASET = "test";

    private RedLink.Data redlink;

    @Rule
    public ConcurrentRule crule = new ConcurrentRule();

    @Rule
    public RepeatingRule rrule = new RepeatingRule();


    protected static Random rnd;

    private static long runs = 0;

    private static Logger log = LoggerFactory.getLogger(DataConcurrencyTest.class);

    private List<URI> resources = new ArrayList<>();

    private List<Value> objects = new ArrayList<>();

    private Set<Statement> allAddedTriples = new HashSet<>();

    @Rule
    public TestWatcher watchman = new TestWatcher() {
        /**
         * Invoked when a test is about to start
         */
        @Override
        protected void starting(Description description) {
            log.info("{} being run...", description.getMethodName());
        }

        /**
         * Invoked when a test method finishes (whether passing or failing)
         */
        @Override
        protected void finished(Description description) {
            log.info("{}: {} added triples, {} removed triples, {} resources reused, {} objects reused", new Object[] { description.getMethodName(), tripleAddCount, tripleRemoveCount, resourcesReused, objectsReused});
        }
    };

    long tripleAddCount = 0;
    long tripleRemoveCount = 0;

    long resourcesReused = 0;
    long objectsReused = 0;

    private ValueFactory valueFactory;

    @Before
    public void setupTest() throws MalformedURLException {
        Credentials credentials = buildCredentials(DataTest.class);
        redlink = RedLinkFactory.createDataClient(credentials);

        valueFactory = new ValueFactoryImpl();

        rnd = new Random();
    }

    @BeforeClass
    @AfterClass
    public static void cleanUp() throws Exception {
        Credentials credentials = buildCredentials(DataTest.class);
        Assume.assumeNotNull(credentials);
        Assume.assumeNotNull(credentials.getVersion());
        Assume.assumeTrue(credentials.verify());

        RedLink.Data redlink = RedLinkFactory.createDataClient(credentials);
        Assume.assumeTrue(redlink.cleanDataset(TEST_DATASET));
    }


    /**
     * Insert a triple into the triplestore. Then run a SPARQL delete/insert/where update to replace the triple
     * with a new triple and check if the update was successful.
     *
     * @throws IOException
     * @throws RDFHandlerException
     * @throws InterruptedException
     */
    @Test
    @Concurrent(count = 5)
    @Repeating(repetition = 20)
    public void testInsertDelete() throws IOException, RDFHandlerException, InterruptedException {
        try {
            // subject and predicate remain the same over updates
            URI subject   = randomURI();
            URI predicate = randomURI();

            // create a statement in the triple store
            Model model = new TreeModel();
            model.add(new StatementImpl(subject, predicate, randomObject()));
            redlink.importDataset(model, TEST_DATASET);

            log.debug("inserted random triple");


            Model exported1 = redlink.exportDataset(TEST_DATASET);

            Assert.assertFalse(exported1.isEmpty());

            for(Statement stmt : model) {
                Assert.assertTrue("triple " + stmt + " not contained in exported data", exported1.contains(stmt));
            }


            // run an DELETE/INSERT update query, replacing the old value with a new value
            String updateTmpl = "DELETE { ?s <%s> ?o } INSERT { ?s <%s> \"Test\" } WHERE { ?s <%s> ?o . FILTER(?s = <%s>) }";
            String update = String.format(updateTmpl, predicate.stringValue(), predicate.stringValue(), predicate.stringValue(), subject.stringValue());

            redlink.sparqlUpdate(update, TEST_DATASET);

            // check current state of dataset
            Model exported2 = redlink.exportDataset(TEST_DATASET);
            Assert.assertFalse(exported2.isEmpty());

            Model filtered = exported2.filter(subject, predicate, null);
            Assert.assertFalse(filtered.isEmpty());
            Assert.assertEquals("Test", filtered.iterator().next().getObject().stringValue());

            redlink.deleteResource(subject.stringValue(), TEST_DATASET);

        } catch (RuntimeException ex) {
            log.error("exception: ",ex);

            Assert.fail(ex.getMessage());
        }
    }


    /**
     * Return a random URI, with a 10% chance of returning a URI that has already been used.
     * @return
     */
    protected URI randomURI() {
        return getValueFactory().createURI("http://localhost/"+ RandomStringUtils.randomAlphanumeric(16));
    }

    /**
     * Return a random RDF value, either a reused object (10% chance) or of any other kind.
     * @return
     */
    protected Value randomObject() {
        Value object;
        switch(rnd.nextInt(6)) {
            case 0: object = getValueFactory().createURI("http://data.redlink.io/"+ RandomStringUtils.randomAlphanumeric(8));
                break;
            case 2: object = getValueFactory().createLiteral(RandomStringUtils.randomAscii(40));
                break;
            case 3: object = getValueFactory().createLiteral(rnd.nextInt());
                break;
            case 4: object = getValueFactory().createLiteral(rnd.nextDouble());
                break;
            case 5: object = getValueFactory().createLiteral(rnd.nextBoolean());
                break;
            default: object = getValueFactory().createURI("http://data.redlink.io/"+ RandomStringUtils.randomAlphanumeric(8));
                break;

        }
        return object;

    }

    protected ValueFactory getValueFactory() {
        return valueFactory;
    }


}
