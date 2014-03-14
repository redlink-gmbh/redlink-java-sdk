package io.redlink.sdk.impl.analysis.model;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.*;
import com.google.common.primitives.Doubles;

import java.util.*;

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

    Enhancements() {
        this.enhancements = ArrayListMultimap.create();
        this.entities = Maps.newLinkedHashMap();
        languages = Sets.newHashSet();
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
        return (Collection<TextAnnotation>) result; // Should be safe. Need to
        // be tested
    }

    /**
     * Returns the {@link Collection} of extracted {@link EntityAnnotation}s
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public Collection<EntityAnnotation> getEntityAnnotations() {
        Collection<? extends Enhancement> result = enhancements.get(EntityAnnotation.class);
        return (Collection<EntityAnnotation>) result; // Should be safe. Needs to be tested
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

        for (EntityAnnotation ea : eas)
            if (ea.getRelations().contains(ta))
                result.add(ea);

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
                });
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
                }).toImmutableList();
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
                }).toImmutableList();
    }
    
    public Multimap<TextAnnotation, EntityAnnotation> getEntityAnnotationsByTextAnnotation(){
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
     * Returns the best {@link EntityAnnotation} (the one with higher confidence value) for each extracted {@link TextAnnotation}
     *
     * @return
     */
    public Map<TextAnnotation, EntityAnnotation> getBestAnnotations() {
        Multimap<TextAnnotation, EntityAnnotation> map = ArrayListMultimap.create();
        Map<TextAnnotation, EntityAnnotation> result = new HashMap<TextAnnotation, EntityAnnotation>();

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

        Ordering<EntityAnnotation> o = new Ordering<EntityAnnotation>() {
            @Override
            public int compare(EntityAnnotation left, EntityAnnotation right) {
                return Doubles.compare(left.confidence, right.confidence);
            }
        };

        for (TextAnnotation ta : map.keys()) {
            eas = map.get(ta);
            result.put(ta, o.max(eas));
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

                });
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

                });
    }

}
