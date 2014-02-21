package io.redlink.sdk.analysis;

import com.google.common.base.Optional;
import io.redlink.sdk.RedLink.Analysis;
import org.apache.commons.io.IOUtils;

import javax.ws.rs.core.MediaType;
import java.io.*;

/**
 * Represent a Request Object necessary to perform {@link Analysis} services' requests. An instance of this class contains
 * all the parameters that can be used for profiling a request to the RedLink {@link Analysis} service. In order to build
 * such instances, {@link AnalysisRequestBuilder} must be used
 *
 * @author rafa.haro@redlink.co
 */
public class AnalysisRequest {

    /**
     * Accepted Input Formats
     */
    public static enum InputFormat {
        TEXT(MediaType.TEXT_PLAIN_TYPE),
        HTML(MediaType.TEXT_HTML_TYPE),
        PDF(new MediaType("application", "pdf")),
        OFFICE(new MediaType("application", "doc"));

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
        XML(MediaType.APPLICATION_ATOM_XML_TYPE),
        JSON(MediaType.APPLICATION_JSON_TYPE),
        JSONLD(new MediaType("application", "ld+json")),
        RDFXML(new MediaType("application", "rdf+xml")),
        RDFJSON(new MediaType("application", "rdf+json")),
        TURTLE(new MediaType("text", "turtle")),
        NT(new MediaType("text", "rdf+n3"));


        private final MediaType type;

        private OutputFormat(MediaType type) {
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
    private Optional<InputStream> content = Optional.absent();

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
     * true -> Content is a String
     * false -> Content is a File
     */
    private boolean isContentString = true;

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
        return content.get();
    }

    /**
     * Get current request analysis name
     *
     * @return User's RedLink Application Analysis name for the current request
     */
    public String getAnalysis() {
        return analysis.get();
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

    /**
     * Return true if current request content is a {@link String} instance
     *
     * @return Flag indicating if the content that will be analyzed is a {@link String}
     */
    public boolean isContentString() {
        return isContentString;
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
            this.request.content = Optional.of(IOUtils.toInputStream(content));
            this.request.isContentString = true;
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
            this.request.content = Optional.of(IOUtils.toInputStream(content, encoding));
            this.request.isContentString = true;
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
            this.request.content = Optional.of((InputStream) new FileInputStream(file));
            this.request.isContentString = false;
            return this;
        }

        /**
         * Set Request Content
         *
         * @param stream {@link InputStream} containing the content to be analyzed
         * @return Current Request Builder
         */
        public AnalysisRequestBuilder setContent(InputStream stream) {
            this.request.content = Optional.of(stream);
            this.request.isContentString = false;
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
