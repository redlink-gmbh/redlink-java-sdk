package io.redlink.sdk;

import io.redlink.sdk.impl.analysis.model.Enhancements;
import io.redlink.sdk.impl.content.model.ContentItem;
import io.redlink.sdk.impl.search.model.SearchResults;
import io.redlink.sdk.impl.vocabulary.model.Profile;

import org.openrdf.model.Model;
import org.openrdf.rio.RDFFormat;

/**
 * 
 * @author sergio.fernandez@redlink.co
 *
 */
public interface RedLink {
	
	public static interface Content {
		
		public static final String PATH = "content";
		
		String getContentStatus(String id);
		
		Model getContent(String id);
		
		ContentItem createContent(String content);
		
		//ContentItem createContent(String content, Map<Resource,Value> metadata);
		
		ContentItem createContent(String id, String content);
		
		//ContentItem createContent(String id, String content, Map<Resource,Value> metadata);
		
		ContentItem updateContent(String id, String content);
		
		//ContentItem updateContent(String id, String content, Map<Resource,Value> metadata);
		
		//ContentItem updateContentMetadata(String id, Map<Resource,Value> metadata);
		
		//List<ContentItem> uploadMetadata(Model metadata);
		
		//List<ContentItem> dryRunUploadMetadata(Model metadata); //rollback?
		
		//List<ContentItem> uploadMetadata(Model metadata, List<ContentItem> items);
		
		boolean deleteContent(String id);
		
	}
	
	public static interface Search {
		
		public static final String PATH = "search";
		
		public static final String QUERY = "search";
		
		public static final String START = "start";
		
		public static final String RESULTS = "results";
		
		public static final String FACET = "facet";
		
		SearchResults search(String query);
		
		SearchResults search(String query, int start, int results, boolean facet);
		
	}
	
	public static interface Analysis {
	
		public static final String PATH = "analysis";
		
		public static final String FORMAT = "out";
		
		Enhancements enhance(String content);
		
	}
	
	public static interface Vocabulary {
		
		public static final String PATH = "vocabulary";

		public static final String FORMAT = "format";

		public static final String PROFILE = "profile";

		Vocabulary createVocabulary(String vocabulary, RDFFormat format, Profile profile);
		
		Vocabulary createVocabulary(String id, String vocabulary, RDFFormat format, Profile profile);
		
		boolean deleteVocabulary(String id);
		
	}

}
