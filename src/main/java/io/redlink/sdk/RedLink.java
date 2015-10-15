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

import io.redlink.sdk.analysis.AnalysisRequest;
import io.redlink.sdk.impl.analysis.model.Enhancements;
import io.redlink.sdk.impl.data.model.LDPathResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import org.apache.marmotta.client.model.sparql.SPARQLResult;
import org.openrdf.model.Model;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;

/**
 * RedLink Client API
 *
 * @author sergio.fernandez@redlink.co
 */
public interface RedLink {

    String URI = "uri";

     String IN = "in";

    String OUT = "out";

    /**
     * RedLink Analysis API. Any implementation of this interface must have a constructor that receives a {@link Credentials}
     * object which will be used for each service request
     */
    interface Analysis {

        String PATH = "analysis";

        String ENHANCE = "enhance";

        String SUMMARY = "summary";

        String THUMBNAIL = "thumbnail";
        
        String DEREF_FIELDS = "enhancer.engines.dereference.fields";
        
        String LDPATH = "enhancer.engines.dereference.ldpath";

        /**
         * Performs an analysis of the content included in the request, getting a {@link Enhancements} object as result
         * The analysis result will depend on the configured in the configured application within the used {@link Credentials}
         *
         * @param request {@link AnalysisRequest} containing the request parameters and the content to be enhanced
         * @return Simplified RedLink Enhancement Structure
         */
        Enhancements enhance(AnalysisRequest request) throws IOException;
        
        /**
         * Performs an analysis of the content included in the request getting as response an instance of the {@link Class}
         * passed by parameter. Current implementation only support {@link String} and {@link Enhancements} as responseType. 
         * If {@link Enhancements} is passed, the request will assume RDF+XML as response format, and will parse the response
         * to create the {@link Enhancements} object. If {@link String} is passed as response type, the method will return
         * the RAW response in the format specified at the {@link AnalysisRequest} request parameter
         * 
         * @param request {@link AnalysisRequest} containing the request parameters and the content to be enhanced
         * @param responseType {@link Class} of the response. Only {@link Enhancements} and {@link String} are supported
         * @return An instance of the class passed by parameter wrapping the Analysis Service response
         */
        <T> T enhance(AnalysisRequest request, Class<T> responseType) throws IOException;

    }

    /**
     * RedLink LinkedData API. Any implementation of this interface must have a constructor that receives a {@link Credentials}
     * object which will be used for each service request
     */
    interface Data {

        String PATH = "data";

        String RESOURCE = "resource";

        String SPARQL = "sparql";

        String SELECT = "select";

        String QUERY = "query";

        String UPDATE = "update";

        String LDPATH = "ldpath";

        String RELEASE = "release";

        /**
         * Import an RDF {@link Model} into the selected Dataset. The Dataset must exist at the user RedLink account and
         * must be configured for the user's RedLink application used in the request.
         *
         * @param data    RDF {@link Model} to be imported
         * @param dataset Name of the dataset where the data will be imported
         * @return Flag indicating if the importation has been performed successfully
         * @throws IOException
         * @throws RDFHandlerException
         */
        boolean importDataset(Model data, String dataset) throws IOException, RDFHandlerException;

        /**
         * Import an RDF {@link Model} into the selected Dataset. The Dataset must exist at the user RedLink account and
         * must be configured for the user's RedLink application used in the request. If cleanBefore value is true, current dataset at
         * user's RedLink application will be cleaned first
         *
         * @param data        RDF {@link Model} to be imported
         * @param dataset     Name of the dataset where the data will be imported
         * @param cleanBefore Flag indicating if the dataset must be cleaned before
         * @return Flag indicating if the importation has been performed successfully
         * @throws RDFHandlerException
         * @throws IOException
         */
        boolean importDataset(Model data, String dataset, boolean cleanBefore) throws RDFHandlerException, IOException;

        /**
         * Import the Model contained in the passed {@link File} into the selected Dataset. The Dataset must exist at the user RedLink account and
         * must be configured for the user's RedLink application used in the request
         *
         * @param file    {@link File} containing the RDF Model to be imported
         * @param dataset Name of the dataset where the data will be imported
         * @return Flag indicating if the importation has been performed successfully
         * @throws FileNotFoundException
         */
        boolean importDataset(File file, String dataset) throws FileNotFoundException;

        /**
         * Import the Model contained in the passed {@link File} into the selected Dataset. The Dataset must exist at the user RedLink account and
         * must be configured for the user's RedLink application used in the request. If cleanBefore value is true, current dataset at
         * user's RedLink application will be cleaned first
         *
         * @param file        {@link File} containing the RDF Model to be imported
         * @param dataset     Name of the dataset where the data will be imported
         * @param cleanBefore Flag indicating if the dataset must be cleaned before
         * @return Flag indicating if the importation has been performed successfully
         * @throws FileNotFoundException
         */
        boolean importDataset(File file, String dataset, boolean cleanBefore) throws FileNotFoundException;

        /**
         * Import the Model contained in the passed {@link InputStream} into the selected Dataset. The Dataset must exist at the user RedLink account and
         * must be configured for the user's RedLink application used in the request
         *
         * @param in      {@link InputStream} containing the RDF Model to be imported
         * @param format  {@link RDFFormat} indicating the format of the model contained in the InputStream
         * @param Dataset Name of the dataset where the data will be imported
         * @return Flag indicating if the importation has been performed successfully
         */
        boolean importDataset(InputStream in, RDFFormat format, String Dataset);

        /**
         * Import the Model contained in the passed {@link InputStream} into the selected Dataset. The Dataset must exist at the user RedLink account and
         * must be configured for the user's RedLink application used in the request. If cleanBefore value is true, current dataset at
         * user's RedLink application will be cleaned first
         *
         * @param in          {@link InputStream} containing the RDF Model to be imported
         * @param format      {@link RDFFormat} indicating the format of the model contained in the InputStream
         * @param Dataset     Name of the dataset where the data will be imported
         * @param cleanBefore Flag indicating if the dataset must be cleaned before
         * @return Flag indicating if the importation has been performed successfully
         */
        boolean importDataset(InputStream in, RDFFormat format, String Dataset, boolean cleanBefore);

        /**
         * Export the user dataset at his RedLink application to a local RDF {@link Model}. The Dataset must exist at the user RedLink account and
         * must be configured for the user's RedLink application used in the request
         *
         * @param dataset Name of the dataset at user's RedLink application to be exported
         * @return RDF {@link Model} representing the dataset
         */
        Model exportDataset(String dataset);

        /**
         * Clean (delete all the data) user's dataset. The Dataset must exist at the user RedLink account and
         * must be configured for the user's RedLink application used in the request
         *
         * @param dataset Name of the dataset at user's RedLink application to be cleaned
         * @return Flag indicating if the dataset has been cleaned correctly
         */
        boolean cleanDataset(String dataset);

        /**
         * Get resource data by its URI as RDF {@link Model}
         *
         * @param resource URI (identifier) of the resource
         * @return {@link Model} representing the resource and all its properties or null if the resource is not found
         */
        Model getResource(String resource) throws URISyntaxException;

        /**
         * Get resource data by its URI as RDF {@link Model} from the user dataset passed by parameter. The Dataset must exist at the user RedLink account and
         * must be configured for the user's RedLink application used in the request
         *
         * @param resource URI (identifier) of the resource
         * @param dataset  Name of the dataset at user's RedLink application where the resource will be lookup
         * @return
         */
        Model getResource(String resource, String dataset) throws URISyntaxException;

        /**
         * Import a resource represented by an RDF {@link Model} into the selected Dataset. The Dataset must exist at the user RedLink account and
         * must be configured for the user's RedLink application used in the request.
         *
         * @param resource URI (identifier) of the resource
         * @param data     Resource data as RDF {@link Model}
         * @param dataset  Name of the dataset at user's RedLink application where the resource will be imported
         * @return Flag indicating if the importation has been performed successfully
         */
        boolean importResource(String resource, Model data, String dataset);

        /**
         * Import a resource represented by an RDF {@link Model} into the selected Dataset. The Dataset must exist at the user RedLink account and
         * must be configured for the user's RedLink application used in the request. If cleanBefore value is true, current resource at
         * user's dataset will be deleted first
         *
         * @param resource    URI (identifier) of the resource
         * @param data        Resource data as RDF {@link Model}
         * @param dataset     Name of the dataset at user's RedLink application where the resource will be imported
         * @param cleanBefore Flag indicating if the resource must be deleted before
         * @return Flag indicating if the importation has been performed successfully
         */
        boolean importResource(String resource, Model data, String dataset, boolean cleanBefore);

        /**
         * Delete a Resource identified by its URI in the user dataset passed by parameter. The Dataset must exist at the user RedLink account and
         * must be configured for the user's RedLink application used in the request
         *
         * @param resource URI (identifier) of the resource
         * @param dataset  Name of the dataset at user's RedLink application where the resource will be deleted
         * @return Flag indicating if the deletion has been performed successfully
         */
        boolean deleteResource(String resource, String dataset);

        /**
         * Execute a SPARQL tuple query using the dataset passed by paramater as context. The Dataset must exist at the user RedLink account and
         * must be configured for the user's RedLink application used in the request
         *
         * @param query   SPARQL tuple query to be executed
         * @param dataset Name of the dataset at user's RedLink application where the query will be executed
         * @return Result of the query as {@link SPARQLResult} object
         */
        SPARQLResult sparqlTupleQuery(String query, String dataset);

        /**
         * use sparqlTupleQuery() instead
         *
         */
        @Deprecated
        SPARQLResult sparqlSelect(String query, String dataset);

        /**
         * Execute a SPARQL tuple query using as context all the configured datasets at user's RedLink application
         *
         * @param query SPARQL tuple query to be executed
         * @return Result of the query as {@link SPARQLResult} object
         */
        SPARQLResult sparqlTupleQuery(String query);

        /**
         * use sparqlTupleQuery() instead
         *
         */
        @Deprecated
        SPARQLResult sparqlSelect(String query);

        /**
         * Execute a SPARQL graph query using the dataset passed by paramater as context. The Dataset must exist at the user RedLink account and
         * must be configured for the user's RedLink application used in the request
         *
         * @param query SPARQL graph query to be executed
         * @param dataset ame of the dataset at user's RedLink application where the query will be executed
         * @return Result of the query as {@link org.openrdf.model.Model} object
         */
        Model sparqlGraphQuery(String query, String dataset);

        /**
         * Execute a SPARQL graph query using as context all the configured datasets at user's RedLink application
         *
         * @param query SPARQL graph query to be executed
         * @return Result of the query as {@link org.openrdf.model.Model} object
         */
        Model sparqlGraphQuery(String query);

        /**
         * Update dataset's resources using an SPARQL update query. The Dataset must exist at the user RedLink account and
         * must be configured for the user's RedLink application used in the request
         *
         * @param query   SPARQL query to be executed
         * @param dataset Name of the dataset at user's RedLink application where the query will be executed
         * @return Flag indicating whether the update has performed successfully
         */
        boolean sparqlUpdate(String query, String dataset);

        /**
         * Execute a LDPath program using the dataset passed by paramater as context. The Dataset must exist at the user RedLink account and
         * must be configured for the user's RedLink application used in the request
         *
         * @param uri
         * @param dataset Name of the dataset at user's RedLink application where the query will be executed
         * @param program LDPath program to be executed
         * @return Result of the program execution as {@link LDPathResult} object
         */
        LDPathResult ldpath(String uri, String dataset, String program);

        /**
         * Execute a LDPath program using as context all the configured datasets at user's RedLink application
         *
         * @param uri
         * @param program LDPath program to be executed
         * @return Result of the program execution as {@link LDPathResult} object
         */
        LDPathResult ldpath(String uri, String program);

        /**
         * Releases the data currently store in the dataset to be used
         * later on for analysis purposes.
         *
         * @param dataset Name of the dataset to release
         * @return Result o the operation
         */
        boolean release(String dataset);

    }

}
