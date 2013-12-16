package io.redlink.sdk.impl.analysis.model;

import io.redlink.sdk.impl.analysis.model.server.Annotation;
import io.redlink.sdk.impl.analysis.model.server.Annotation.DerefencedEntity;
import io.redlink.sdk.impl.analysis.model.server.Annotation.PlainLiteral;
import io.redlink.sdk.impl.analysis.model.server.Annotation.Position;
import io.redlink.sdk.impl.analysis.model.server.Category;
import io.redlink.sdk.impl.analysis.model.server.FlatEnhancementStructure;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * 
 * @author rafa.haro@redlink.co
 *
 */

final class FlatStructureParser extends EnhancementsParser {
	
	/**
	 * 
	 */
	private final FlatEnhancementStructure structure;
	
	public FlatStructureParser(FlatEnhancementStructure structure){
		this.structure = structure;
	}

	/*
	 * (non-Javadoc)
	 * @see io.redlink.sdk.impl.analysis.model.EnhancementsParser#parseEnhancements()
	 */
	@Override
	public Collection<Enhancement> parseEnhancements()
			throws EnhancementParserException {
		Map<String, Enhancement> tasByPosition = Maps.newHashMap();
		Collection<EntityAnnotation> eas = parseEntityAnnotations(tasByPosition);
		Collection<Enhancement> result = tasByPosition.values();
		result.addAll(eas);
		return result;
	}

	private TextAnnotation createTextAnnotation(Position position) {
		TextAnnotation result = new TextAnnotation();
		result.setConfidence(position.getConfidence());
		result.setStarts(position.getStart());
		result.setEnds(position.getEnd());
		result.setLanguage(position.getLanguage());
		result.setRelations(Collections.<Enhancement> emptyList());
		result.setSelectedText(position.getText());
		result.setSelectionContext(position.getContext());
		return result;
	}

	private Entity createEntity(String reference, DerefencedEntity entity) {
		Entity result = new Entity(reference);
		Map<String, List<PlainLiteral>> properties = entity.getProperties();
		for(Entry<String, List<PlainLiteral>> entry:properties.entrySet()){
			String property = entry.getKey();
			List<PlainLiteral> values = entry.getValue();
			for(PlainLiteral value:values)
				if(value.getLanguage() == null || value.getLanguage().isEmpty())
					result.addPropertyValue(property, value.getText());
				else
					result.addPropertyValue(property, value.getLanguage(), value.getText());
			
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see io.redlink.sdk.impl.analysis.model.EnhancementsParser#parseLanguages()
	 */
	@Override
	public Collection<String> parseLanguages()
			throws EnhancementParserException {
		Collection<String> languages = Sets.newHashSet();
		for(String language:structure.getLanguages())
			languages.add(language);
		return languages;
	}

	/*
	 * (non-Javadoc)
	 * @see io.redlink.sdk.impl.analysis.model.EnhancementsParser#parseTextAnnotations()
	 */
	@Override
	public Collection<TextAnnotation> parseTextAnnotations()
			throws EnhancementParserException {
		
		Map<String, TextAnnotation> tasByPosition = Maps.newHashMap();
		
		for(Annotation annotation:structure.getAnnotations())
			for(Position position:annotation.getPositions()){
				String key = new String("" + position.getStart()).
						concat(new String("" + position.getEnd()));
				if(!tasByPosition.containsKey(key))
					tasByPosition.put(key, createTextAnnotation(position));
			}
		
		return tasByPosition.values();
	}

	/*
	 * (non-Javadoc)
	 * @see io.redlink.sdk.impl.analysis.model.EnhancementsParser#parseEntityAnnotations()
	 */
	@Override
	public Collection<EntityAnnotation> parseEntityAnnotations()
			throws EnhancementParserException {
		
		Map<String, Enhancement> tasByPosition = Maps.newHashMap();
		return parseEntityAnnotations(tasByPosition);
	}
	
	private Collection<EntityAnnotation> parseEntityAnnotations(
			Map<String, Enhancement> tasByPosition){
		Collection<EntityAnnotation> eas = Sets.newHashSet();

		for(Annotation annotation:structure.getAnnotations()){
			EntityAnnotation ea = new EntityAnnotation();
			ea.setConfidence(annotation.getConfidence());
			ea.setEntityLabel(annotation.getLabel());
			ea.setEntityTypes(annotation.getTypes());
			ea.setLanguage(annotation.getLanguage());
			ea.setDataset(annotation.getDataset());
			ea.setEntityReference(createEntity(annotation.getReference(), annotation.getEntity()));
			Collection<Enhancement> relations = Sets.newHashSet();
			for(Position position:annotation.getPositions()){
				String key = new String("" + position.getStart()).
						concat(new String("" + position.getEnd()));
				Enhancement ta = tasByPosition.get(key);
				if(ta == null)
					tasByPosition.put(key,
							ta = createTextAnnotation(position));
				relations.add(ta);
			}
			ea.setRelations(relations);
			eas.add(ea);
		}
		return eas;
	}

	/*
	 * (non-Javadoc)
	 * @see io.redlink.sdk.impl.analysis.model.EnhancementsParser#parseEntity(java.lang.String)
	 */
	@Override
	public Entity parseEntity(String entityUri)
			throws EnhancementParserException {
		
		for(Annotation annotation:structure.getAnnotations())
			if(annotation.getReference().equals(entityUri))
				return createEntity(entityUri, 
						annotation.getEntity());
		
		return new Entity(entityUri); // Empty Entity
	}

	/*
	 * (non-Javadoc)
	 * @see io.redlink.sdk.impl.analysis.model.EnhancementsParser#parseTopicAnnotation()
	 */
	@Override
	public Collection<TopicAnnotation> parseTopicAnnotation()
			throws EnhancementParserException {
		Collection<TopicAnnotation> tas = Sets.newHashSet();
		for(Category category:structure.getCategories()){
			TopicAnnotation next = new TopicAnnotation();
			next.setConfidence(category.getConfidence());
			next.setLanguage(category.getLanguage());
			next.setRelations(Collections.<Enhancement> emptyList());
			next.setDataset(category.getDataset());
			next.setSummary(category.getSummary());
			next.setTopicLabel(category.getLabel());
			next.setTopicReference(category.getReference());
			tas.add(next);
		}
		return tas;
	}

}
