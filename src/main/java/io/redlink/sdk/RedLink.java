package io.redlink.sdk;

import io.redlink.sdk.analysis.AnalysisRequest;
import io.redlink.sdk.impl.analysis.model.Enhancements;
import io.redlink.sdk.impl.data.model.LDPathResult;
import io.redlink.sdk.impl.search.model.SearchResults;

import org.apache.marmotta.client.model.sparql.SPARQLResult;
import org.openrdf.model.Model;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * @author sergio.fernandez@redlink.co
 *
 */
public interface RedLink {

    static final String URI = "uri";

    public static interface Analysis {

        static final String PATH = "analysis";

        static final String ENHANCE = "enhance";
        
        Enhancements enhance(AnalysisRequest request);

    }
	
	public static interface Data {
		
		static final String PATH = "data";

        static final String RESOURCE = "resource";

        static final String SPARQL = "sparql";

        static final String SELECT = "select";

        static final String QUERY = "query";

        static final String UPDATE = "update";

        static final String LDPATH = "ldpath";

        boolean importDataset(Model data, String dataset) throws IOException, RDFHandlerException;

        boolean importDataset(Model data, String dataset, boolean cleanBefore) throws RDFHandlerException, IOException;

        boolean importDataset(File file, String dataset) throws FileNotFoundException;

        boolean importDataset(File file, String dataset, boolean cleanBefore) throws FileNotFoundException;

        boolean importDataset(InputStream in, RDFFormat format, String Dataset);

        boolean importDataset(InputStream in, RDFFormat format, String Dataset, boolean cleanBefore);

        Model exportDataset(String dataset);

        boolean cleanDataset(String dataset);

        Model getResource(String resource);

        Model getResource(String resource, String dataset);

        boolean importResource(String resource, Model data, String dataset);

        boolean importResource(String resource, Model data, String dataset, boolean cleanBefore);

        boolean deleteResource(String resource, String dataset);

        SPARQLResult sparqlSelect(String query, String dataset);

        SPARQLResult sparqlSelect(String query);

        boolean sparqlUpdate(String query, String dataset);

        LDPathResult ldpath(String uri, String dataset, String program);

        LDPathResult ldpath(String uri, String program);

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
