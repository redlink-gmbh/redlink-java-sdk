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

import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import io.redlink.sdk.analysis.AnalysisRequest;
import io.redlink.sdk.analysis.AnalysisRequest.AnalysisRequestBuilder;
import io.redlink.sdk.analysis.AnalysisRequest.OutputFormat;
import io.redlink.sdk.impl.analysis.model.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.*;
import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map.Entry;

public class AnalysisTest extends GenericTest {

    private static Logger log = LoggerFactory.getLogger(AnalysisTest.class);

    private static RedLink.Analysis redlink;
    private static final String TEST_FILE = "/willsmith.txt";
    private static final String TEST_ANALYSIS = "test";

    private static final String STANBOL_TEXT_TO_ENHANCE = "The Open Source Project Apache Stanbol provides different "
            + "features that facilitate working with linked data, in the netlabs.org early adopter proposal VIE "
            + "I wanted to try features which were not much used before, like the Ontology Manager and the Rules component. "
            + "The News of the project can be found on the website! Rupert Westenthaler, living in Austria, "
            + "is the main developer of Stanbol. The System is well integrated with many CMS like Drupal and Alfresco.";

    private static String PARIS_TEXT_TO_ENHANCE = "Paris is the capital of France";

    private static final String DEREFERENCING_TEXT = "Roberto Baggio is a retired Italian football forward and attacking midfielder/playmaker"
            + " who was the former President of the Technical Sector of the FIGC. Widely regarded as one of the greatest footballers of all time, "
            + "he came fourth in the FIFA Player of the Century Internet poll, and was chosen as a member of the FIFA World Cup Dream Team";

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        Credentials credentials = buildCredentials(AnalysisTest.class);
        Assume.assumeNotNull(credentials);
        Assume.assumeNotNull(credentials.getVersion());
        Assume.assumeTrue(credentials.verify());

        redlink = RedLinkFactory.createAnalysisClient(credentials);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        redlink = null;
    }

    /**
     * <p>Tests the empty enhancements when an empty string is sent to the API</p>
     */
    //@Test
    //FIXME: server-side issue (to be solved by Rupert)
    public void testEmptyEnhancement() throws IOException {
        AnalysisRequest request = AnalysisRequest.builder()
                .setAnalysis(TEST_ANALYSIS)
                .setContent("  ")
                .setOutputFormat(OutputFormat.TURTLE).build();
        Enhancements enhancements = redlink.enhance(request);
        Assert.assertNotNull(enhancements);
        //Assert.assertEquals(0, enhancements.getModel().size());
        Assert.assertEquals(0, enhancements.getEnhancements().size());
        Assert.assertEquals(0, enhancements.getTextAnnotations().size());
        Assert.assertEquals(0, enhancements.getEntityAnnotations().size());
    }

    @Test
    public void testFile() throws IOException, URISyntaxException {
        File file = new File(this.getClass().getResource(TEST_FILE).toURI());
        Assume.assumeTrue(file.exists());
        Assume.assumeTrue(file.canRead());

        AnalysisRequest request = AnalysisRequest.builder()
                .setAnalysis(TEST_ANALYSIS)
                .setContent(file)
                .setOutputFormat(OutputFormat.TURTLE).build();
        Enhancements enhancements = redlink.enhance(request);

        Assert.assertNotNull(enhancements);
        Assert.assertFalse(enhancements.getEnhancements().isEmpty());
    }

    /**
     * <p>Tests the size of the obtained enhancements</p>
     */
    @Test
    public void testDemoEnhancement() throws IOException {
        log.debug("> test annotations: ");
        log.debug(" - app: {}", TEST_ANALYSIS);
        String content = PARIS_TEXT_TO_ENHANCE + ". "+STANBOL_TEXT_TO_ENHANCE;
        log.debug(" - content: \n{}", content);
        AnalysisRequest request = AnalysisRequest.builder()
                .setAnalysis(TEST_ANALYSIS)
                .setContent(content)
                .setOutputFormat(OutputFormat.RDFXML).build();
        Enhancements enhancements = redlink.enhance(request);
        Assert.assertNotNull(enhancements);
        //Assert.assertNotEquals(0, enhancements.getModel().size());
        int sizeEnh = enhancements.getEnhancements().size();
        Assert.assertNotEquals(0, sizeEnh);
        int sizeTextAnno = enhancements.getTextAnnotations().size();
        Assert.assertNotEquals(0, sizeTextAnno);
        log.debug("> {} Text Annotations: ", sizeTextAnno);
        for(TextAnnotation ta : enhancements.getTextAnnotations()){
            testTextAnnotationProperties(ta);
        }

        int sizeEntityAnno = enhancements.getEntityAnnotations().size();
        Assert.assertNotEquals(0, sizeEntityAnno);
        log.debug("> {} Entity Annotations: ", sizeEntityAnno);
        for(EntityAnnotation ea : enhancements.getEntityAnnotations()){
            testEntityAnnotationProperties(ea);
        }
        
        int sizeTopicAnno = enhancements.getTopicAnnotations().size();
        Assert.assertNotEquals(0, sizeTopicAnno);
        log.debug("> {} Topic Annotations: ", sizeTopicAnno);
        for(TopicAnnotation ta : enhancements.getTopicAnnotations()){
            testTopicAnnotationProperties(ta);
        }
        
        int sizeSentiAnno = enhancements.getSentimentAnnotations().size();
        Assert.assertNotEquals(0, sizeSentiAnno);
        log.debug("> {} Sentiment Annotations: ", sizeSentiAnno);
        for(SentimentAnnotation sa : enhancements.getSentimentAnnotations()){
            testSentimentAnnotationProperties(sa);
        }
        
        int sizeKeywordAnno = enhancements.getKeywordAnnotations().size();
        Assert.assertNotEquals(0, sizeKeywordAnno);
        log.debug("> {} Entity Annotations: ", sizeKeywordAnno);
        for(KeywordAnnotation ka : enhancements.getKeywordAnnotations()){
            testKeywordAnnotationProperties(ka);
        }
        
        Assert.assertEquals(sizeEnh, sizeTextAnno + sizeEntityAnno + sizeKeywordAnno + sizeTopicAnno + sizeSentiAnno);

        //Best Annotation
        testEnhancementBestAnnotations(enhancements);

        // Filter By Confidence
        testGetEntityAnnotationByConfidenceValue(enhancements);

        //filterKeywords by Metric and count
        testGetKeywordsByCountMetric(enhancements);
        
        // Entity Properties
        testEntityProperties(enhancements);
        
        //Test language
        log.debug("Language: {}", enhancements.getLanguages());
        Assert.assertEquals(Collections.singleton("en"), enhancements.getLanguages());
        
        //Document Sentiment
        log.debug("Document Sentiment: {}", enhancements.getDocumentSentiment());
        Assert.assertNotNull(enhancements.getDocumentSentiment());
    }

    private void testGetKeywordsByCountMetric(Enhancements enhancements) {
        Collection<KeywordAnnotation> kas = enhancements.getKeywordAnnotationsByCountMetric(null, null);
        Assert.assertEquals(enhancements.getKeywordAnnotations().size(), kas.size());
        kas = enhancements.getKeywordAnnotationsByCountMetric(3, null);
        Assert.assertEquals(0, kas.size());
        kas = enhancements.getKeywordAnnotationsByCountMetric(null, 0.5d);
        Assert.assertEquals(1, kas.size());
    }

    /**
     * <p>Tests the properties of the enhancements</p>
     */
    @Test
    public void testEnhancementProperties() throws IOException {
        AnalysisRequest request = AnalysisRequest.builder()
                .setAnalysis(TEST_ANALYSIS)
                .setContent(STANBOL_TEXT_TO_ENHANCE)
                .setOutputFormat(OutputFormat.RDFXML).build();
        Enhancements enhancements = redlink.enhance(request);
        Assert.assertFalse(enhancements.getLanguages().isEmpty());
        Assert.assertFalse(enhancements.getTextAnnotations().isEmpty());
        Assert.assertFalse(enhancements.getEntityAnnotations().isEmpty());
        Assert.assertFalse(enhancements.getEntities().isEmpty());

        for (Enhancement en : enhancements.getEnhancements()) {
            Assert.assertNotEquals(0, en.getConfidence());
            Assert.assertNotNull(en.getConfidence());

            if (en instanceof TextAnnotation) {
                testTextAnnotationProperties((TextAnnotation) en);
            } else if (en instanceof EntityAnnotation) {
                testEntityAnnotationProperties((EntityAnnotation) en);
            }
        }
    }

    /**
     * <p>Tests the {@code TextAnnotation} properties</p>
     *
     * @param ta the TextAnnotation object
     */
    private void testTextAnnotationProperties(TextAnnotation ta) {
        log.debug("  - {}@{} [{}..{} | type: {}]", ta.getSelectedText(), ta.getSelectedTextLang(), 
            ta.getStarts(), ta.getEnds(), ta.getType());
        Assert.assertNotNull(ta.getSelectedText());
        Assert.assertTrue(StringUtils.isNotBlank(ta.getSelectedText()));
        Assert.assertEquals("en", ta.getSelectedTextLang());
        Assert.assertNotNull(ta.getSelectionContext());
        Assert.assertTrue(StringUtils.isNotBlank(ta.getSelectionContext()));
        Assert.assertTrue(ta.getStarts() >= 0);
        Assert.assertTrue(ta.getEnds() > ta.getStarts());
        Assert.assertNotNull(ta.getSelectionPrefix());
        Assert.assertNotNull(ta.getSelectionSuffix());
    }

    /**
     * <p>Tests the {@code EntityAnnotation} properties</p>
     *
     * @param ea
     */
    private void testEntityAnnotationProperties(EntityAnnotation ea) {
        log.debug("  - {}@{} [ref: {}]", ea.getEntityLabel(), ea.getEntityLabelLang(), 
            ea.getEntityReference() != null ? ea.getEntityReference().getUri() : null);
        Assert.assertNotNull(ea.getEntityLabel());
        Assert.assertTrue(StringUtils.isNotBlank(ea.getEntityLabel()));
        Assert.assertNotNull(ea.getEntityReference());
        Assert.assertNotNull(ea.getEntityTypes());
        if(ea.getDataset() != null) {
            Assert.assertTrue(StringUtils.isNotBlank(ea.getDataset()));
        } //else not all EntityAnnotation have a dataset
    }
    /**
     * <p>Tests the {@code EntityAnnotation} properties</p>
     *
     * @param ka
     */
    private void testKeywordAnnotationProperties(KeywordAnnotation ka) {
        log.debug("  - {} [count: {}, metric: {}]", ka.getKeyword(), ka.getCount(), ka.getMetric());
        Assert.assertNotNull(ka.getKeyword());
        Assert.assertTrue(StringUtils.isNotBlank(ka.getKeyword()));
        Assert.assertNotNull(ka.getMetric());
        Assert.assertTrue(ka.getMetric() > 0 && ka.getMetric() <= 1.0);
        Assert.assertTrue(ka.getCount() >= 1);
    }

    /**
     * <p>Tests the {@code TopicAnnotation} properties</p>
     *
     * @param ta
     */
    private void testTopicAnnotationProperties(TopicAnnotation ta) {
        log.debug("  - {}@{} [ref: {}]", ta.getTopicLabel(), ta.getTopicLabelLang(), 
            ta.getTopicReference() != null ? ta.getTopicReference().getUri() : null);
        Assert.assertNotNull(ta.getTopicLabel());
        Assert.assertTrue(StringUtils.isNotBlank(ta.getTopicLabel()));
        Assert.assertNotNull(ta.getTopicReference());
        Assert.assertNotNull(ta.getTopicReference());
        if(ta.getOrigin() != null){
            Assert.assertTrue(StringUtils.isNotBlank(ta.getOrigin()));
        }
    }
    /**
     * <p>Tests the {@code EntityAnnotation} properties</p>
     *
     * @param sa
     */
    private void testSentimentAnnotationProperties(SentimentAnnotation sa) {
        log.debug("  - {} [{}..{}]", sa.getSentiment(), sa.getStarts(), sa.getEnds());
        Assert.assertTrue(sa.getSentiment() >= -1 && sa.getSentiment() <= 1);
        Assert.assertNull(sa.getLanguage()); //sentiment Annotations do not have a language
        Assert.assertTrue(sa.getStarts() >= 0);
        Assert.assertTrue(sa.getEnds() > sa.getStarts());
    }

    /**
     * <p>Tests the best annotations method</p>
     */
    private void testEnhancementBestAnnotations(Enhancements enhancements) {
        Multimap<TextAnnotation, EntityAnnotation> bestAnnotations = enhancements.getBestAnnotations();
        Assert.assertNotEquals(0, bestAnnotations.keySet().size());
        Assert.assertTrue(bestAnnotations.keySet().size() <= enhancements.getTextAnnotations().size());

        for (TextAnnotation ta : bestAnnotations.keySet()) {
            //check all best have the same
            Collection<EntityAnnotation> eas = bestAnnotations.get(ta);
            Double confidence = Iterables.get(eas, 0).getConfidence();
            for (EntityAnnotation ea : eas) {
                Assert.assertEquals(confidence, ea.getConfidence());
            }

            //check the confidence is actually the highest
            for (EntityAnnotation ea : enhancements.getEntityAnnotations(ta)) {
                Assert.assertTrue(confidence >= ea.getConfidence());
            }
        }
    }

    /**
     * <p>Tests the getTextAnnotationByConfidenceValue method</p>
     */
    private void testGetEntityAnnotationByConfidenceValue(Enhancements enhancements) {
        Collection<EntityAnnotation> eas = enhancements.getEntityAnnotationsByConfidenceValue((0.5));
        Assert.assertTrue(eas.size() > 0);
    }

    /**
     * <p>Tests the getTextAnnotationByConfidenceValue method</p>
     */
    @Test
    public void testFilterEntitiesByConfidenceValue() throws IOException {
        AnalysisRequest request = AnalysisRequest.builder()
                .setAnalysis(TEST_ANALYSIS)
                .setContent(STANBOL_TEXT_TO_ENHANCE)
                .setOutputFormat(OutputFormat.RDFXML).build();
        Enhancements enhancements = redlink.enhance(request);
        Collection<EntityAnnotation> eas = enhancements.getEntityAnnotationsByConfidenceValue((0.9));
        Assert.assertTrue(eas.size() > 0);
    }

    @Test
    public void testRdfFormatResponse() throws IOException {
        AnalysisRequestBuilder builder = AnalysisRequest.builder()
                .setAnalysis(TEST_ANALYSIS)
                .setContent(PARIS_TEXT_TO_ENHANCE);
        Enhancements rdfResponse = redlink.enhance(builder.build(), Enhancements.class);
        Assert.assertFalse(rdfResponse.getEnhancements().isEmpty());
        testEntityProperties(rdfResponse);
    }

    @Test
    public void testJsonFormatResponse() throws IOException {
        AnalysisRequestBuilder builder = AnalysisRequest.builder()
                .setAnalysis(TEST_ANALYSIS)
                .setContent(PARIS_TEXT_TO_ENHANCE);
        AnalysisRequest request = builder.setOutputFormat(OutputFormat.JSON).build();
        String jsonResponse = redlink.enhance(request, String.class);
        try {
            new JSONObject(jsonResponse);
        } catch (JSONException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testXmlFormatResponse() throws IOException {
        AnalysisRequestBuilder builder = AnalysisRequest.builder()
                .setAnalysis(TEST_ANALYSIS)
                .setContent(PARIS_TEXT_TO_ENHANCE);
        AnalysisRequest request = builder.setOutputFormat(OutputFormat.XML).build();
        String xmlResponse = redlink.enhance(request, String.class);
        DocumentBuilderFactory domParserFac = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = domParserFac.newDocumentBuilder();
            db.parse(new InputSource(new StringReader(xmlResponse)));
        } catch (Exception e) {
            log.debug("Raw response: \n{}", xmlResponse);
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testDereferencing() throws IOException {

        // Dereferencing Fields
        AnalysisRequest request = AnalysisRequest.builder()
                .setAnalysis(TEST_ANALYSIS)
                .setContent(DEREFERENCING_TEXT)
                .addDereferencingField("fb:people.person.height_meters")
                .addDereferencingField("fb:people.person.date_of_birth")
                .addDereferencingField("dct:subject")
                .addDereferencingField("dbp:totalgoals")
                .setOutputFormat(OutputFormat.RDFXML).build();
        Enhancements enhancements = redlink.enhance(request);

        Entity baggio = enhancements.getEntity("http://rdf.freebase.com/ns/m.06d6f");
        Assert.assertNotNull(baggio);
        Assert.assertEquals(baggio.getFirstPropertyValue(
                        "http://rdf.freebase.com/ns/people.person.height_meters"),
                "1.74"
        );
        Assert.assertEquals(baggio.getFirstPropertyValue(
                        "http://rdf.freebase.com/ns/people.person.date_of_birth"),
                "1967-02-18"
        );

        Entity baggioDBP = enhancements.getEntity("http://dbpedia.org/resource/Roberto_Baggio");
        Assert.assertNotNull(baggioDBP);
        //Assert.assertEquals("http://dbpedia.org/resource/Category:Brescia_Calcio_players",
        //        baggioDBP.getFirstPropertyValue("http://purl.org/dc/terms/subject"));
        Assert.assertEquals("221", baggioDBP.getFirstPropertyValue("http://dbpedia.org/property/totalgoals"));

        //LdPath
        String date = "@prefix fb: <http://rdf.freebase.com/ns/>;"
                + "@prefix custom :<http://io.redlink/custom/freebase/>"
                + "custom:date = fb:people.person.date_of_birth :: xsd:string;"
                + "custom:nationality = fb:people.person.nationality/fb:location.location.adjectival_form :: xsd:string;";
        request = AnalysisRequest.builder()
                .setAnalysis(TEST_ANALYSIS)
                .setContent(DEREFERENCING_TEXT)
                .setLDpathProgram(date)
                .setOutputFormat(OutputFormat.RDFXML).build();
        enhancements = redlink.enhance(request);
        baggio = enhancements.getEntity("http://rdf.freebase.com/ns/m.06d6f");
        Assert.assertNotNull(baggio);
        String dateV = baggio.getFirstPropertyValue(
                "http://rdf.freebase.com/ns/people.person.date_of_birth");
        String dateCustomV = baggio.getFirstPropertyValue(
                "http://io.redlink/custom/freebase/date");
        Assert.assertEquals(dateV, dateCustomV);
        Assert.assertTrue(baggio.getValues(
                "http://io.redlink/custom/freebase/nationality")
                .contains("Italian"));

    }

    /**
     * Test for checking a non-deterministic behaviour (SDK-4)
     *
     */
    @Test
    public void testSDK4() throws IOException {
        String content = IOUtils.toString(AnalysisTest.class.getResourceAsStream("/SDK-4.txt"), "utf8");
        AnalysisRequest request = AnalysisRequest.builder()
                .setAnalysis(TEST_ANALYSIS)
                .setContent(content)
                .setOutputFormat(OutputFormat.TURTLE).build();
        Enhancements enhancements = redlink.enhance(request);
        Multimap<TextAnnotation, EntityAnnotation> bestAnnotations = enhancements.getBestAnnotations();
        for (TextAnnotation ta : bestAnnotations.keySet()) {
            Collection<EntityAnnotation> eas = bestAnnotations.get(ta);
            if (ta.getSelectedText().contains("Lopez")) {
                log.debug("Found target text annotation \"{}\" with {} entity annotation:", ta.getSelectedText(), eas.size());
                for (EntityAnnotation ea : eas) {
                    log.debug(" - {}", ea.getEntityReference().getUri());
                }
                log.trace("selection content: {}", ta.getSelectionContext());
            }
        }
    }

    /**
     * Test for checking an issue with some of the NLP engines
     *
     */
    @Test
    public void testSDK5() throws IOException {
        String content = IOUtils.toString(AnalysisTest.class.getResourceAsStream("/SDK-5.txt"), "utf8");
        AnalysisRequest request = AnalysisRequest.builder()
                .setAnalysis(TEST_ANALYSIS)
                .setContent(content)
                .setOutputFormat(OutputFormat.TURTLE).build();
        Enhancements enhancements = redlink.enhance(request);
        Collection<TextAnnotation> tas = enhancements.getTextAnnotations();
        logTextAnnotations(tas);
        Assert.assertEquals(8, tas.size());
        Multimap<TextAnnotation,EntityAnnotation> beas = enhancements.getBestAnnotations();
        logBestAnnotations(beas);
        Assert.assertEquals(12, beas.size());
    }

    /**
     * Test Entities Parsing and Properties
     */
    private void testEntityProperties(Enhancements enhancements) {
        Assert.assertFalse(enhancements.getEntities().isEmpty());
        Entity paris = enhancements.getEntity("http://dbpedia.org/resource/Paris");
        Assert.assertNotNull(paris);
        Assert.assertFalse(paris.getProperties().isEmpty());

        //entity has been added to the analysis result
        Assert.assertFalse(paris.getProperties().isEmpty());
        Assert.assertFalse(paris.getValues(RDFS.LABEL.toString()).isEmpty());
        Assert.assertEquals("Paris", paris.getValue(RDFS.LABEL.toString(), "en"));
        Assert.assertTrue(paris.getValues(RDF.TYPE.toString()).contains("http://dbpedia.org/ontology/Place"));
        // Assert.assertTrue(Float.parseFloat(paris.getFirstPropertyValue("http://stanbol.apache.org/ontology/entityhub/entityhub#entityRank")) > 0.5f);
        Assert.assertTrue(paris.getValues(DCTERMS.SUBJECT.toString()).contains("http://dbpedia.org/resource/Category:Capitals_in_Europe"));

        EntityAnnotation parisEa = enhancements.getEntityAnnotation(paris.getUri());
        Assert.assertEquals(parisEa.getEntityTypes(), Collections.singleton("http://dbpedia.org/ontology/Municipality"));
        Assert.assertEquals("Paris", parisEa.getEntityLabel());
        //       Assert.assertEquals("dbpedia", parisEa.getDataset());
        Assert.assertEquals("en", parisEa.getEntityLabelLang());
    }
    
    
    /**
     * @param tas
     */
    private void logTextAnnotations(Collection<TextAnnotation> tas) {
        log.debug("> TextAnnotations:");
        for(TextAnnotation ta : tas){
            log.debug(" - {}@[{},{}] - type: {}", ta.getSelectedText(), ta.getStarts(), ta.getEnds(), ta.getType());
        }
    }

    /**
     * @param beas
     */
    private void logBestAnnotations(Multimap<TextAnnotation,EntityAnnotation> beas) {
        log.debug("> best EntityAnnotations:");
        for(Entry<TextAnnotation, EntityAnnotation> entry : beas.entries()){
            log.debug(" - {}@[{},{}] -> {} | conf: {}", entry.getKey().getSelectedText(), 
                entry.getKey().getStarts(), entry.getKey().getEnds(), 
                entry.getValue().getEntityReference().getUri(), entry.getValue().getConfidence());
        }
    }

    @Test
    public void testEnhancementsAsInputstream() throws IOException {
        AnalysisRequestBuilder builder = AnalysisRequest.builder()
                .setAnalysis(TEST_ANALYSIS)
                .setContent(PARIS_TEXT_TO_ENHANCE);
        AnalysisRequest request = builder.setOutputFormat(OutputFormat.XML).build();
        InputStream response = redlink.enhance(request, InputStream.class);
        Assert.assertNotNull(response);
        DocumentBuilderFactory domParserFac = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = domParserFac.newDocumentBuilder();
            db.parse(response);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

}
