package io.redlink.sdk;

import io.redlink.sdk.impl.analysis.model.Enhancements;
import io.redlink.sdk.impl.search.model.SearchResults;

/**
 * 
 * @author sergio.fernandez@redlink.co
 *
 */
public interface RedLink {

    public static interface Analysis {

        public static final String PATH = "analysis";

        public static final String ENHANCE = "enhance";

        public static final String FORMAT = "out";

        Enhancements enhance(String content, String analysis);

    }
	
	public static interface Data {
		
		public static final String PATH = "data";
		
	}
	
	public static interface Search {
		
		public static final String PATH = "search";
		
		public static final String QUERY = "search";
		
		public static final String START = "start";
		
		public static final String RESULTS = "results";
		
		public static final String FACET = "facet";
		
		SearchResults search(String query, String core);
		
		SearchResults search(String query, String core, int start, int results, boolean facet);
		
	}

}
