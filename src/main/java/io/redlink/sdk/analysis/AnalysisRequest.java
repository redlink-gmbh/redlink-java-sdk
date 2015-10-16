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
package io.redlink.sdk.analysis;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.google.common.net.MediaType;
import io.redlink.sdk.RedLink.Analysis;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.Collection;

/**
 * Represent a Request Object necessary to perform {@link Analysis} services' requests. An instance of this class contains
 * all the parameters that can be used for profiling a request to the RedLink {@link Analysis} service. In order to build
 * such instances, {@link AnalysisRequestBuilder} must be used
 *
 * @author Rafa Haro
 * @author Sergio Fernández
 */
public class AnalysisRequest {

    /**
     * Accepted Input Formats
     */
    public static enum InputFormat {
        TEXT(MediaType.create("text", "plain")),
        HTML(MediaType.create("text", "html")),
        PDF(MediaType.create("application", "pdf")),
        OFFICE(MediaType.create("application", "doc")),
        OCTETSTREAM(MediaType.create("application", "octet-stream"));

        private final MediaType type;

        private InputFormat(MediaType type) {
            this.type = type;
        }

        public MediaType value() {
            return type;
        }

    }

    /**
     * Accepted Output Formats
     */
    public static enum OutputFormat {

        XML(MediaType.create("application", "xml")),
        JSON(MediaType.create("application", "json")),
        JSONLD(MediaType.create("application", "ld+json")),
        RDFXML(MediaType.create("application", "rdf+xml")),
        RDFJSON(MediaType.create("application", "rdf+json")),
        TURTLE(MediaType.create("text", "turtle")),
        NT(MediaType.create("text", "rdf+n3"));

        private final MediaType type;

        OutputFormat(MediaType type) {
            this.type = type;
        }

        public MediaType value() {
            return type;
        }

        public static OutputFormat get(String type) {
            for (OutputFormat of : OutputFormat.values())
                if (of.type.toString().equals(type))
                    return of;
            return null;
        }

    }
    
    /**
     * Supported Content Formats in the Builder API
     */
    private enum ContentType {
        STRING, FILE, INPUTSTREAM, EMPTY
    }
    
    private boolean consumed = false;

    /**
     * Default Input Format
     */
    private InputFormat inputFormat = InputFormat.TEXT;

    /**
     * Default Output Format
     */
    private OutputFormat outputFormat = OutputFormat.TURTLE;

    /**
     * Stream to Analyze
     */
    private Optional<InputStream> contentStream = Optional.absent();
    
    /**
     * File to Analyze
     */
    private Optional<File> contentFile = Optional.absent();
    
    /**
     * String to Analyze
     */
    private Optional<String> contentString = Optional.absent();
    private Optional<String> contentEncoding = Optional.absent(); 

    /**
     * Analysis Service name
     */
    private Optional<String> analysis = Optional.absent();

    /**
     * Request Content Summary
     */
    private boolean summary = true;

    /**
     * Request Entities' thumbnails/depiction
     */
    private boolean thumbnail = true;

    /**
     * Type of the Content to be analyzed
     */
    private ContentType contentType = ContentType.EMPTY;
    
    /**
     * List of Fields to be dereferenced
     */
    private Collection<String> dereferencedFields = Sets.newHashSet();
    
    /**
     * LDPath to be executed for dereferencing
     */
    private Optional<String> ldpath = Optional.absent();
    
    /**
     * Return current request {@link InputFormat} name. The input format should be used to specify the format
     * of the content that is sent to analyze
     *
     * @return Current Request {@link InputFormat}'s name
     */
    public String getInputFormat() {
        return inputFormat.name();
    }

    /**
     * Return current request {@link InputFormat} {@link MediaType}. The input format should be used to specify the format
     * of the content that is sent to analyze
     *
     * @return Current Request {@link InputFormat}'s {@link MediaType}
     */
    public MediaType getInputMediaType() {
        return inputFormat.type;
    }

    /**
     * Return current request {@link OutputFormat} name. The output format should be used to specify the format
     * of the analysis service response
     *
     * @return Current Request {@link OutputFormat}'s name
     */
    public String getOutputFormat() {
        return outputFormat.name();
    }

    /**
     * Return current request {@link OutputFormat}'s {@link MediaType} . The output format should be used to specify the format
     * of the analysis service response
     *
     * @return Current Request {@link OutputFormat}'s {@link MediaType}
     */
    public MediaType getOutputMediaType() {
        return outputFormat.type;
    }

    /**
     * Get current request content as {@link InputStream}
     *
     * @return {@link InputStream} containing the content that is going to be analyzed
     */
    public InputStream getContent() {
        switch(contentType){
            case EMPTY:
                throw new RuntimeException("There is not Content available to analyze");
            case FILE:
                if(consumed)
                    try {
                        contentStream = Optional.of((InputStream) new FileInputStream(contentFile.get()));
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                break;
                case STRING:
                    if (consumed) {
                        if (contentEncoding.isPresent()) {
                            try {
                                contentStream = Optional.of(IOUtils.toInputStream(contentString.get(), contentEncoding.get()));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            contentStream = Optional.of(IOUtils.toInputStream(contentString.get()));
                        }
                    }
                    break;
                case INPUTSTREAM:
                    if (consumed) {
                        throw new RuntimeException("The Content Stream to be analyzed has been already consumed");
                    }
                    break;
                default:
                    break;
        }
        
        consumed = true;
        return contentStream.get();
    }

    /**
     * Get current request analysis name
     *
     * @return User's RedLink Application Analysis name for the current request
     */
    public String getAnalysis() {
        return analysis.orNull();
    }

    /**
     * Get current request summary parameter
     *
     * @return Flag indicating if current request will ask analysis service to return entities's summaries
     */
    public boolean getSummary() {
        return summary;
    }

    /**
     * Get current request thumbnail parameter
     *
     * @return Flag indicating if current request will ask analysis service to return entities's thumbnails
     */
    public boolean getThumbnail() {
        return thumbnail;
    }
    
    public Collection<String> getFieldsToDereference(){
        return dereferencedFields;
    }
    
    public String getLDPathProgram(){
        return ldpath.orNull();
    }

    /**
     * Analysis Request Builder. This class allows the user to easily generate {@link AnalysisRequest} objects to
     * be used at {@link Analysis} services
     *
     * @author rafa.haro@redlink.co
     */
    public static class AnalysisRequestBuilder {

        private final AnalysisRequest request = new AnalysisRequest();

        /**
         * Set Request {@link InputFormat}
         *
         * @param inputFormat Request's {@link InputFormat}
         * @return Current Request Builder
         */
        public AnalysisRequestBuilder setInputFormat(InputFormat inputFormat) {
            this.request.inputFormat = inputFormat;
            return this;
        }

        /**
         * Set Request {@link OutputFormat}
         *
         * @param outputFormat Request's {@link OutputFormat}
         * @return Current Request Builder
         */
        public AnalysisRequestBuilder setOutputFormat(OutputFormat outputFormat) {
            this.request.outputFormat = outputFormat;
            return this;
        }

        /**
         * Set Request Analysis name
         *
         * @param analysis Analysis name
         * @return Current Request Builder
         */
        public AnalysisRequestBuilder setAnalysis(String analysis) {
            this.request.analysis = Optional.of(analysis);
            return this;
        }

        /**
         * Set Request retrieve entities' summaries parameter
         *
         * @param summary Request Summary Parameter
         * @return Current Request Builder
         */
        public AnalysisRequestBuilder setSummaries(boolean summary) {
            this.request.summary = summary;
            return this;
        }

        /**
         * Set Request retrieve entities' thumbnails parameter
         *
         * @param thumbnail Request Thumbnail Parameter
         * @return Current Request Builder
         */
        public AnalysisRequestBuilder setThumbnails(boolean thumbnail) {
            this.request.thumbnail = thumbnail;
            return this;
        }

        /**
         * Set Request Content
         *
         * @param content {@link String} content to be analyzed
         * @return Current Request Builder
         */
        public AnalysisRequestBuilder setContent(String content) {
            // Assuming UTF-8
            this.request.contentStream = Optional.of(IOUtils.toInputStream(content));
            this.request.contentString = Optional.of(content);
            this.request.contentType = ContentType.STRING;
            return this;
        }

        /**
         * Set request content
         *
         * @param content  {@link String} content to be analyzed
         * @param encoding Content's encoding
         * @return Current Request Builder
         * @throws IOException
         */
        public AnalysisRequestBuilder setContent(String content, String encoding) throws IOException {
            this.request.contentStream = Optional.of(IOUtils.toInputStream(content, encoding));
            this.request.contentString = Optional.of(content);
            this.request.contentEncoding = Optional.of(encoding);
            this.request.contentType = ContentType.STRING;
            return this;
        }

        /**
         * Set Request Content
         *
         * @param file {@link File} containing the content to be analyzed
         * @return Current Request Builder
         * @throws FileNotFoundException
         */
        public AnalysisRequestBuilder setContent(File file) throws FileNotFoundException {
            this.request.contentStream = Optional.of((InputStream) new FileInputStream(file));
            this.request.contentFile = Optional.of(file);
            this.request.contentType = ContentType.FILE;
            return this;
        }

        /**
         * Set Request Content
         *
         * @param stream {@link InputStream} containing the content to be analyzed
         * @return Current Request Builder
         */
        public AnalysisRequestBuilder setContent(InputStream stream) {
            this.request.contentStream = Optional.of(stream);
            this.request.contentType = ContentType.INPUTSTREAM;
            return this;
        }
        
        public AnalysisRequestBuilder setLDpathProgram(String ldpathProgram){
            this.request.ldpath = Optional.of(ldpathProgram);
            return this;
        }
        
        public AnalysisRequestBuilder addDereferencingField(String field){
            this.request.dereferencedFields.add(field);
            return this;
        }

        /**
         * Returns {@link Analysis}'s service request object
         *
         * @return built {@link AnalysisRequest} object
         */
        public AnalysisRequest build() {
            return request;
        }

    }

    /**
     * Create a new Request Builder
     *
     * @return Created {@link AnalysisRequestBuilder}
     */
    public static AnalysisRequestBuilder builder() {
        return new AnalysisRequestBuilder();
    }

}
