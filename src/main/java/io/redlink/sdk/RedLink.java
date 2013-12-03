package io.redlink.sdk;

import io.redlink.sdk.impl.analysis.model.Enhancements;
import io.redlink.sdk.impl.search.model.SearchResults;
import org.apache.marmotta.client.model.sparql.SPARQLResult;

/**
 * 
 * @author sergio.fernandez@redlink.co
 *
 */
public interface RedLink {

    public static interface Analysis {

        static final String PATH = "analysis";

        static final String ENHANCE = "enhance";

        static final String FORMAT = "out";

        Enhancements enhance(String content, String analysis);

    }
	
	public static interface Data {
		
		static final String PATH = "data";

        static final String SPARQL = "sparql";

        static final String SELECT = "select";

        static final String QUERY = "query";

        static final String UPDATE = "update";

        static final String RESOURCE = "resource";

        static final String IMPORT = "import";

        static final String EXPORT = "export";

        static final String LDPATH = "ldpath";

        SPARQLResult sparqlSelect(String query, String dataset);

	}
	
	public static interface Search {
		
		static final String PATH = "search";
		
		static final String QUERY = "search";
		
		static final String START = "start";
		
		static final String RESULTS = "results";
		
		static final String FACET = "facet";
		
		SearchResults search(String query, String core);
		
		SearchResults search(String query, String core, int start, int results, boolean facet);
		
	}

}
