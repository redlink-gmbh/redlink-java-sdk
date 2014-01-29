package io.redlink.sdk;

import io.redlink.sdk.analysis.AnalysisRequest;
import io.redlink.sdk.analysis.AnalysisRequest.OutputFormat;
import io.redlink.sdk.impl.analysis.model.*;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;

public class AnalysisTest extends GenericTest {
	
	private static RedLink.Analysis redlink;

    private static final String TEST_ANALYSIS = "wordlift";

	private static final String STANBOL_TEXT_TO_ENHANCE = "The Open Source Project Apache Stanbol provides different "
                + "features that facilitate working with linked data, in the netlabs.org early adopter proposal VIE "
                + "I wanted to try features which were not much used before, like the Ontology Manager and the Rules component. "
                + "The News of the project can be found on the website! Rupert Westenthaler, living in Austria, "
                + "is the main developer of Stanbol. The System is well integrated with many CMS like Drupal and Alfresco.";
	
	private static String PARIS_TEXT_TO_ENHANCE = "Paris is the capital of France";
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Credentials credentials = buildCredentials(AnalysisTest.class);
		Assume.assumeNotNull(credentials);
        Assume.assumeNotNull(credentials.getVersion());
        Assume.assumeTrue(credentials.verify());
		redlink = RedLinkFactory.getInstance().createAnalysisClient(credentials);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		redlink = null;
	}
	
	/**
	 * <p>Tests the empty enhancements when an empty string is sent to the API</p>
	 */
	@Test
	public void testEmptyEnhancement() {
		AnalysisRequest request = AnalysisRequest.builder()
				.setAnalysis(TEST_ANALYSIS)
				.setContent("  ")
				.setOutputFormat(OutputFormat.TURTLE).build();
		Enhancements enhancements = redlink.enhance(request);
		Assert.assertNotNull(enhancements);
//		Assert.assertEquals(0, enhancements.getModel().size());
		Assert.assertEquals(0, enhancements.getEnhancements().size());
		Assert.assertEquals(0, enhancements.getTextAnnotations().size());
		Assert.assertEquals(0, enhancements.getEntityAnnotations().size());
	}
	
	/**
	 * <p>Tests the size of the obtained enhancements</p>
	 */
	@Test
	public void testDemoEnhancement() {
		AnalysisRequest request = AnalysisRequest.builder()
				.setAnalysis(TEST_ANALYSIS)
				.setContent(PARIS_TEXT_TO_ENHANCE)
				.setOutputFormat(OutputFormat.RDFXML).build();
		Enhancements enhancements = redlink.enhance(request);
		Assert.assertNotNull(enhancements);
//		Assert.assertNotEquals(0, enhancements.getModel().size());
		int sizeE = enhancements.getEnhancements().size();
		Assert.assertNotEquals(0, sizeE);
		int sizeTA = enhancements.getTextAnnotations().size();
		Assert.assertNotEquals(0, sizeTA);
		int sizeEA = enhancements.getEntityAnnotations().size();
		Assert.assertNotEquals(0, sizeEA);
		Assert.assertEquals(sizeE, sizeTA+sizeEA);
		
		//Best Annotation
		testEnhancementBestAnnotations(enhancements);
		
		// Filter By Confidence
		testGetEntityAnnotationByConfidenceValue(enhancements);
		
		// Entity Properties
		testEntityProperties(enhancements);
	}
	
	/**
	 * <p>Tests the properties of the enhancements</p>
	 */
	@Test
	public void testEnhancementProperties() {
		AnalysisRequest request = AnalysisRequest.builder()
				.setAnalysis(TEST_ANALYSIS)
				.setContent(STANBOL_TEXT_TO_ENHANCE)
				.setOutputFormat(OutputFormat.RDFXML).build();
	    Enhancements enhancements = redlink.enhance(request);
	    Assert.assertFalse(enhancements.getLanguages().isEmpty());
	    Assert.assertFalse(enhancements.getTextAnnotations().isEmpty());
	    Assert.assertFalse(enhancements.getEntityAnnotations().isEmpty());
	    Assert.assertFalse(enhancements.getEntities().isEmpty());

	    for(Enhancement en : enhancements.getEnhancements()) {
	        Assert.assertNotEquals(0, en.getConfidence());
	        Assert.assertNotNull(en.getConfidence());
	        
	        if(en instanceof TextAnnotation) {
	            testTextAnnotationProperties((TextAnnotation) en);
	        } else if(en instanceof EntityAnnotation) {
	            testEntityAnnotationProperties((EntityAnnotation) en);
	        }
	    }
	    
	}
	
	/**
	 * <p>Tests the {@code TextAnnotation} properties</p>
	 * @param ta the TextAnnotation object
	 */
	private void testTextAnnotationProperties(TextAnnotation ta) {
		Assert.assertEquals("en", ta.getLanguage());
		Assert.assertNotEquals("", ta.getSelectionContext());
		Assert.assertNotNull(ta.getSelectionContext());
		Assert.assertNotEquals("", ta.getSelectedText());
		Assert.assertNotNull(ta.getSelectedText());
		Assert.assertNotEquals(0, ta.getEnds());
		Assert.assertNotEquals(-1, ta.getStarts());
	}

    /**
     * <p>Tests the {@code EntityAnnotation} properties</p>
     * @param ea
     */
    private void testEntityAnnotationProperties(EntityAnnotation ea) {
        Assert.assertNotEquals(ea.getEntityLabel(), "");
        Assert.assertNotNull(ea.getEntityLabel());
        Assert.assertNotNull(ea.getEntityReference());
        Assert.assertNotNull(ea.getEntityTypes());
        Assert.assertNotEquals(ea.getDataset(), "");
        //Assert.assertNotNull(ea.getDataset());
    }
    
    /**
     * <p>Tests the best annotations method</p>
     */
	private void testEnhancementBestAnnotations(Enhancements enhancements) {
	   Map<TextAnnotation, EntityAnnotation> bestAnnotations = enhancements.getBestAnnotations();
	   Assert.assertNotEquals(0, bestAnnotations.keySet().size());
	   Assert.assertEquals(bestAnnotations.size(), enhancements.getTextAnnotations().size());
	   Entry<TextAnnotation, EntityAnnotation> entry = null;
	   Iterator<Entry<TextAnnotation, EntityAnnotation>> it = bestAnnotations.entrySet().iterator();
	   while(it.hasNext()) {
		   entry = it.next();
		   Collection<EntityAnnotation> eas = enhancements.getEntityAnnotations(entry.getKey());
		   for(EntityAnnotation ea:eas){
			   Assert.assertTrue((ea.equals(entry.getValue())) || entry.getValue().getConfidence() >= ea.getConfidence());
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
    public void testFilterEntitiesByConfidenceValue() {
    	AnalysisRequest request = AnalysisRequest.builder()
				.setAnalysis(TEST_ANALYSIS)
				.setContent(STANBOL_TEXT_TO_ENHANCE)
				.setOutputFormat(OutputFormat.RDFXML).build();
    	Enhancements enhancements = redlink.enhance(request);
        Collection<EntityAnnotation> eas = enhancements.getEntityAnnotationsByConfidenceValue((0.9));
        Assert.assertTrue(eas.size() > 0);
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
    	Assert.assertTrue(Float.parseFloat(paris.getFirstPropertyValue("http://stanbol.apache.org/ontology/entityhub/entityhub#entityRank")) > 0.5f);
    	Assert.assertTrue(paris.getValues(DCTERMS.SUBJECT.toString()).contains("http://dbpedia.org/resource/Category:Capitals_in_Europe"));

    	EntityAnnotation parisEa = enhancements.getEntityAnnotation(paris.getUri());
    	Assert.assertTrue(parisEa.getEntityTypes().contains("http://dbpedia.org/ontology/Place"));
    	Assert.assertEquals("Paris", parisEa.getEntityLabel());
    	Assert.assertEquals("dbpedia", parisEa.getDataset());
    	Assert.assertEquals("en", parisEa.getLanguage());


	}
    
}
