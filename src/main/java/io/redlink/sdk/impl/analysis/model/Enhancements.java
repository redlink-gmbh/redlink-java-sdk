package io.redlink.sdk.impl.analysis.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
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
 * 
 * @author sergio.fernandez@redlink.co
 * @author rafa.haro@redlink.co
 * 
 */
public class Enhancements implements Iterable<Enhancement>{

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

	@SuppressWarnings("unchecked")
	public Collection<TextAnnotation> getTextAnnotations() {
		Collection<? extends Enhancement> result = enhancements
				.get(TextAnnotation.class);
		return (Collection<TextAnnotation>) result; // Should be safe. Need to
													// be tested
	}

	@SuppressWarnings("unchecked")
	public Collection<EntityAnnotation> getEntityAnnotations() {
		Collection<? extends Enhancement> result = enhancements .get(EntityAnnotation.class);
		return (Collection<EntityAnnotation>) result; // Should be safe. Needs to be tested
	}

	public Collection<Entity> getEntities() {
		return Collections.unmodifiableCollection(entities.values());
	}

	public Entity getEntity(String URI) {
		return entities.get(URI);
	}

	/**
	 * 
	 * @param ta
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
	 * 
	 * @return
	 */
	public Collection<String> getLanguages() {
		return languages;
	}

	/**
	 * 
	 * @param languages
	 */
	void setLanguages(Collection<String> languages) {
		this.languages = languages;
	}

	/**
	 * 
	 * @param language
	 */
	void addLanguage(String language) {
		this.languages.add(language);
	}
	
}
