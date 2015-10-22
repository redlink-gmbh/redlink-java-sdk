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

import com.google.code.tempusfugit.concurrency.ConcurrentRule;
import com.google.code.tempusfugit.concurrency.RepeatingRule;
import com.google.code.tempusfugit.concurrency.annotations.Concurrent;
import com.google.code.tempusfugit.concurrency.annotations.Repeating;
import io.redlink.sdk.analysis.AnalysisRequest;
import io.redlink.sdk.impl.analysis.model.Enhancements;
import org.junit.*;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openrdf.rio.RDFHandlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Concurrency test on Analysis
 *
 * @author sergio.fernandez@redlink.co
 */
public class AnalysisConcurrencyTest extends GenericTest {

    private static Logger log = LoggerFactory.getLogger(AnalysisConcurrencyTest.class);

    private static final String TEST_ANALYSIS = "test";

    private static String TEXT_TO_ENHANCE = "Paris is the capital of France";

    private RedLink.Analysis redlink;

    @Rule
    public ConcurrentRule crule = new ConcurrentRule();

    @Rule
    public RepeatingRule rrule = new RepeatingRule();

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
            log.info("finished {}", description.getMethodName());
        }
    };

    @Before
    public void setup() throws MalformedURLException {
        Credentials credentials = buildCredentials(AnalysisConcurrencyTest.class);
        redlink = RedLinkFactory.createAnalysisClient(credentials);
    }

    @After
    public void destroy() {
        redlink = null;
    }

    @BeforeClass
    public static void cleanUp() throws Exception {
        Credentials credentials = buildCredentials(AnalysisConcurrencyTest.class);
        Assume.assumeNotNull(credentials);
        Assume.assumeNotNull(credentials.getVersion());
        Assume.assumeTrue(credentials.verify());
    }

    @Test
    @Concurrent(count = 8)
    @Repeating(repetition = 32)
    public void testConcurrently() throws IOException, RDFHandlerException, InterruptedException {
        try {
            AnalysisRequest request = AnalysisRequest.builder()
                    .setAnalysis(TEST_ANALYSIS)
                    .setContent(TEXT_TO_ENHANCE)
                    .setOutputFormat(AnalysisRequest.OutputFormat.TURTLE).build();
            Enhancements enhancements = redlink.enhance(request);
            Assert.assertNotNull(enhancements);
        } catch (RuntimeException ex) {
            log.error("exception: ",ex);
            Assert.fail(ex.getMessage());
        }
    }

}
