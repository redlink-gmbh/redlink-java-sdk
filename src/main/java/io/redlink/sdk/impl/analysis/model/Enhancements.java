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
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Collections2;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.google.common.primitives.Doubles;

/**
 * Analysis Result API. This class eases the management of the RedLink analysis service results, providing
 * an API that hides the complexity of the Enhancement Structure returned by the service
 *
 * @author sergio.fernandez@redlink.co
 * @author rafa.haro@redlink.co
 */
public class Enhancements implements Iterable<Enhancement> {

    /**
     * Map <Enhancement Type, Enhancement Object>
     */
    private Multimap<Class<? extends Enhancement>, Enhancement> enhancements;

    /**
     * Map <URI, Entity>
     */
    private Map<String, Entity> entities;

    /**
     * Annotations' languages (LanguageAnnotation)
     */
    private Collection<String> languages;
    
    private Double documentSentiment;

    Enhancements() {
        this.enhancements = ArrayListMultimap.create();
        this.entities = Maps.newLinkedHashMap();
        this.languages = Sets.newHashSet();
    }

    @Override
    public Iterator<Enhancement> iterator() {
        return getEnhancements().iterator();
    }

    /**
     * Returns the {@link Collection} of all {@link Enhancement}s, including Text, Entity and Topic annotations
     *
     * @return
     */
    public Collection<Enhancement> getEnhancements() {
        return Collections.unmodifiableCollection(enhancements.values());
    }

    void setEnhancements(Collection<Enhancement> enhancements) {
        for (Enhancement e : enhancements) {
            addEnhancement(e);
        }
    }

    void addEnhancement(Enhancement enhancement) {
        enhancements.put(enhancement.getClass(), enhancement);

        if (enhancement instanceof EntityAnnotation) {
            EntityAnnotation ea = (EntityAnnotation) enhancement;
            entities.put(ea.getEntityReference().getUri(),
                    ea.getEntityReference());
        } else if(enhancement instanceof TopicAnnotation){
            TopicAnnotation ta = (TopicAnnotation)enhancement;
            entities.put(ta.getTopicReference().getUri(), ta.getTopicReference());
        }
    }

    /**
     * Returns the {@link Collection} of extracted {@link TextAnnotation}s
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public Collection<TextAnnotation> getTextAnnotations() {
        Collection<? extends Enhancement> result = enhancements
                .get(TextAnnotation.class);
        return result == null ? Collections.<TextAnnotation>emptySet() : 
            Collections.unmodifiableCollection((Collection<TextAnnotation>) result);
    }

    /**
     * Returns the {@link Collection} of extracted {@link EntityAnnotation}s
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public Collection<EntityAnnotation> getEntityAnnotations() {
        Collection<? extends Enhancement> result = enhancements.get(EntityAnnotation.class);
        return result == null ? Collections.<EntityAnnotation>emptySet() : 
            Collections.unmodifiableCollection((Collection<EntityAnnotation>) result);
    }

    /**
     * Returns the {@link Collection} of extracted {@link SentimentAnnotation}s. This
     * allows to process low level sentiment values extracted from sub-sections of the
     * document (e.g. on Sentence level). Use {@link #getDocumentSentiment()} to
     * get the overall sentiment of the document as a whole
     *
     * @return A collection of Sentiment annotations for sub-sections of the document
     * @see #getDocumentSentiment()
     */
    @SuppressWarnings("unchecked")
    public Collection<SentimentAnnotation> getSentimentAnnotations() {
        Collection<? extends Enhancement> result = enhancements.get(SentimentAnnotation.class);
        return result == null ? Collections.<SentimentAnnotation>emptySet() : 
            Collections.unmodifiableCollection((Collection<SentimentAnnotation>) result);
    }
    
    /**
     * Returns the {@link Collection} of extracted {@link EntityAnnotation}s
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public Collection<TopicAnnotation> getTopicAnnotations() {
        Collection<? extends Enhancement> result = enhancements.get(TopicAnnotation.class);
        return result == null ? Collections.<TopicAnnotation>emptySet() : 
            Collections.unmodifiableCollection((Collection<TopicAnnotation>) result);
    }

    /**
     * Returns the {@link Collection} of dereferenced {@link Entity}s
     *
     * @return
     */
    public Collection<Entity> getEntities() {
        return Collections.unmodifiableCollection(entities.values());
    }

    /**
     * Returns a dereferenced entity by its URI
     *
     * @param URI
     * @return
     */
    public Entity getEntity(String URI) {
        return entities.get(URI);
    }

    /**
     * Returns a {@link Collection} of {@link EntityAnnotation}s associated to the {@link TextAnnotation} passed by parameter
     *
     * @param ta {@link TextAnnotation}
     * @return
     */
    public Collection<EntityAnnotation> getEntityAnnotations(TextAnnotation ta) {
        Collection<EntityAnnotation> eas = getEntityAnnotations();
        Collection<EntityAnnotation> result = Sets.newHashSet();

        for (EntityAnnotation ea : eas) {
            if (ea.getRelations() != null && ea.getRelations().contains(ta)) {
                result.add(ea);
            }
        }

        return result;
    }

    /**
     * Returns a {@link Collection} of {@link Entity}s for which associated {@link EntityAnnotation}s has a confidence value
     * greater than or equal to the value passed by parameter
     *
     * @param confidenceValue Threshold confidence value
     * @return
     */
    public Collection<Entity> getEntitiesByConfidenceValue(final Double confidenceValue) {

        Collection<EntityAnnotation> sortedEas = getEntityAnnotationsByConfidenceValue(confidenceValue);

        return Collections2.transform(sortedEas,
                new Function<EntityAnnotation, Entity>() {
                    @Override
                    public Entity apply(final EntityAnnotation ea) {
                        return ea.getEntityReference();
                    }
                }
        );
    }

    /**
     * Returns a {@link Collection} of {@link TextAnnotation}s which confidences values are greater than or equal
     * to the value passed by parameter
     *
     * @param confidenceValue Threshold confidence value
     * @return
     */
    public Collection<TextAnnotation> getTextAnnotationsByConfidenceValue(final Double confidenceValue) {
        return FluentIterable.from(getTextAnnotations())
                .filter(new Predicate<TextAnnotation>() {
                    @Override
                    public boolean apply(TextAnnotation e) {
                        return e.confidence.doubleValue() >= confidenceValue
                                .doubleValue();
                    }
                }).toList();
    }

    /**
     * Returns a {@link Collection} of {@link EntityAnnotation}s which confidences values are greater than or equal
     * to the value passed by parameter
     *
     * @param confidenceValue Threshold confidence value
     * @return
     */
    public Collection<EntityAnnotation> getEntityAnnotationsByConfidenceValue(
            final Double confidenceValue) {
        return FluentIterable.from(getEntityAnnotations())
                .filter(new Predicate<EntityAnnotation>() {
                    @Override
                    public boolean apply(EntityAnnotation e) {
                        return e.confidence.doubleValue() >= confidenceValue
                                .doubleValue();
                    }
                }).toList();
    }

    /**
     * Returns a {@link Collection} of {@link EntityAnnotation}s which confidences values are greater than or equal
     * to the value passed by parameter
     *
     * @param minConfidenceValue Minimum threshold confidence value
     * @param maxConfidenceValue Maximum threshold confidence value
     * @return
     */
    public Collection<EntityAnnotation> getEntityAnnotationsByConfidenceValue(
            final Double minConfidenceValue, final Double maxConfidenceValue) {
        return FluentIterable.from(getEntityAnnotations())
                .filter(new Predicate<EntityAnnotation>() {
                    @Override
                    public boolean apply(EntityAnnotation e) {
                        final double value = e.confidence.doubleValue();
                        return minConfidenceValue.doubleValue() <= value
                                && value <= maxConfidenceValue.doubleValue();
                    }
                }).toList();
    }

    public Multimap<TextAnnotation, EntityAnnotation> getEntityAnnotationsByTextAnnotation() {
        Multimap<TextAnnotation, EntityAnnotation> map = ArrayListMultimap.create();

        Collection<EntityAnnotation> eas = getEntityAnnotations();
        for (EntityAnnotation ea : eas) {
            if (ea.relations != null) {
                for (Enhancement e : ea.relations) {
                    if (e instanceof TextAnnotation) {
                        map.put((TextAnnotation) e, ea);
                    }
                }
            }
        }
        return map;
    }

    /**
     * Returns the best {@link EntityAnnotation}s (those with the highest confidence value) for each extracted {@link TextAnnotation}
     *
     * @return best annotations
     */
    public Multimap<TextAnnotation, EntityAnnotation> getBestAnnotations() {

        Ordering<EntityAnnotation> o = new Ordering<EntityAnnotation>() {
            @Override
            public int compare(EntityAnnotation left, EntityAnnotation right) {
                return Doubles.compare(left.confidence, right.confidence);
            }
        }.reverse();

        Multimap<TextAnnotation, EntityAnnotation> result = ArrayListMultimap.create();
        for (TextAnnotation ta : getTextAnnotations()) {
            List<EntityAnnotation> eas = o.sortedCopy(getEntityAnnotations(ta));
            if (!eas.isEmpty()) {
                Collection<EntityAnnotation> highest = new HashSet<>();
                Double confidence = eas.get(0).getConfidence();
                for (EntityAnnotation ea : eas) {
                    if (ea.confidence < confidence) {
                        break;
                    } else {
                        highest.add(ea);
                    }
                }
                result.putAll(ta, highest);
            }
        }

        return result;
    }

    /**
     * Returns an {@link EntityAnnotation} by its associated dereferenced {@link Entity} URI
     *
     * @param entityUri
     * @return
     */
    public EntityAnnotation getEntityAnnotation(final String entityUri) {
        return Iterables.find(getEntityAnnotations(),
                new Predicate<EntityAnnotation>() {

                    @Override
                    public boolean apply(EntityAnnotation ea) {
                        return ea.getEntityReference().getUri()
                                .equals(entityUri);
                    }

                }
        );
    }

    /**
     * Returns a {@link Collection} of identified languages in the analyzed content
     *
     * @return
     */
    public Collection<String> getLanguages() {
        return languages;
    }

    /**
     * @param languages
     */
    void setLanguages(Collection<String> languages) {
        this.languages = languages;
    }

    /**
     * @param language
     */
    void addLanguage(String language) {
        this.languages.add(language);
    }

    void setDocumentSentiment(Double documentSentiment) {
        this.documentSentiment = documentSentiment;
    }
    /**
     * Getter for the overall sentiment of the Document
     * @return the document sentiment or <code>null</code> if no sentiment component 
     * is configured for the analysis.
     */
    public Double getDocumentSentiment() {
        return documentSentiment;
    }
    
    /**
     * Returns the {@link Collection} of extracted categories (topics) for the analyzed content
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public Collection<TopicAnnotation> getCategories() {
        Collection<? extends Enhancement> result = enhancements.get(TopicAnnotation.class);
        return (Collection<TopicAnnotation>) result; // Should be safe. Needs to be tested
    }

    /**
     * Returns true if the contents has been categorized with a category identified by the URI passed by parameter
     *
     * @param conceptURI URI of the category concept
     * @return
     */
    public boolean hasCategory(final String conceptURI) {
        return Iterables.any(getCategories(),
                new Predicate<TopicAnnotation>() {

                    @Override
                    public boolean apply(TopicAnnotation ta) {
                        return ta.getTopicReference().
                                equals(conceptURI);
                    }

                }
        );
    }
    
    /**
     * Returns the {@link Collection} of extracted {@link EntityAnnotation}s
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public Collection<KeywordAnnotation> getKeywordAnnotations() {
        Collection<? extends Enhancement> result = enhancements.get(KeywordAnnotation.class);
        return result == null ? Collections.<KeywordAnnotation>emptySet() : 
            Collections.unmodifiableCollection((Collection<KeywordAnnotation>) result);
    }

    /**
     * Returns a {@link Collection} of {@link KeywordAnnotation}s which 
     * a <ul>
     * <li> count greater than or equal the required count
     * <li> metric greater than or equals the required metric
     * </ul>y
     *
     * @param minCount Threshold count value or <code>null</code> for no threshold
     * @param minMetric Threshold metric value or <code>null</code> for no threshold
     * @return the {@link KeywordAnnotation}s fulfilling the parsed requirements.
     */
    public Collection<KeywordAnnotation> getKeywordAnnotationsByCountMetric(
            Integer minCount, Double minMetric) {
        final Integer count = minCount == null || minCount < 1 ? null : minCount;
        final Double metric = minMetric == null || minMetric <= 0 ? null : minMetric;
        if(count == null && metric == null){
            return getKeywordAnnotations();
        } else {
            return FluentIterable.from(getKeywordAnnotations())
                    .filter(new Predicate<KeywordAnnotation>() {
                        @Override
                        public boolean apply(KeywordAnnotation e) {
                            boolean apply = true;
                            if(metric != null && (e.getMetric() == null || metric > e.getMetric())){
                                apply = false; //filter because metric is to small
                            }
                            if(count != null && (count > e.getCount())){
                                apply = false; //filter because count is to low
                            }
                            return apply;
                        }
                    }).toList();
        }
    }


}
