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
package io.redlink.sdk.impl.analysis.model;

import java.util.Collection;

/**
 * Enhancement Structure (RedLink's analysis service response) Parser API. The Enhancement Structure (https://stanbol.apache.org/docs/trunk/components/enhancer/enhancementstructure)
 * can be serialized in different formats, being RDF the default one. Each implementation of the parser has the mission to read the structure in the incoming format and transform it
 * in a {@link Enhancements} object, allowing the user to use the results in an easier way.
 *
 * @author rafa.haro@redlink.co
 */
public abstract class EnhancementsParser {

    /**
     * Returns an {@link Enhancements} object from a serialized Enhancement Structure
     *
     * @return
     * @throws EnhancementParserException
     */
    public final Enhancements createEnhancements() throws EnhancementParserException {
        Enhancements enhancements = new Enhancements();
        enhancements.setEnhancements(parseEnhancements());
        enhancements.setLanguages(parseLanguages());
        enhancements.setDocumentSentiment(parseDocumentSentiment());
        return enhancements;
    }

    /**
     * Returns a {@link Collection} of {@link Enhancement} from a serialized Enhancement Structure
     *
     * @return
     * @throws EnhancementParserException
     */
    abstract Collection<Enhancement> parseEnhancements() throws EnhancementParserException;

    /**
     * Returns a {@link Collection} of languages from a serialized Enhancement Structure
     *
     * @return
     * @throws EnhancementParserException
     */
    public abstract Collection<String> parseLanguages() throws EnhancementParserException;

    /**
     * Returns a {@link Collection} of {@link TextAnnotation}s from a serialized Enhancement Structure
     *
     * @return
     * @throws EnhancementParserException
     */
    public abstract Collection<TextAnnotation> parseTextAnnotations() throws EnhancementParserException;


    /**
     * Returns a {@link Collection} of {@link EntityAnnotation}s from a serialized Enhancement Structure
     *
     * @return
     * @throws EnhancementParserException
     */
    public abstract Collection<EntityAnnotation> parseEntityAnnotations() throws EnhancementParserException;

    /**
     * Returns a {@link Collection} of {@link TopicAnnotation}s from a serialized Enhancement Structure
     *
     * @return
     * @throws EnhancementParserException
     */
    public abstract Collection<TopicAnnotation> parseTopicAnnotation() throws EnhancementParserException;

    /**
     * Returns a {@link Collection} of {@link SentimentAnnotation}s from a serialized Enhancement Structure
     * @return
     * @throws EnhancementParserException
     */
    public abstract Collection<SentimentAnnotation> parseSentimentAnnotation() throws EnhancementParserException;
    
    /**
     * Returns the Sentiment for the processed document or <code>null</code> if
     * no Sentiment analysis component is configured for the analysis.
     * @return the Document Sentiment or <code>null</code> if not available
     * @throws EnhancementParserException
     */
    public abstract Double parseDocumentSentiment() throws EnhancementParserException;

    /**
     * Returns a dereferenced {@link Entity} identified by its URI from a serialized Enhancement Structure
     *
     * @param entityUri
     * @return
     * @throws EnhancementParserException
     */
    public abstract Entity parseEntity(String entityUri, String dataset) throws EnhancementParserException;
}
