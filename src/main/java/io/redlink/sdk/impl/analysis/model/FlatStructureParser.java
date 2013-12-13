package io.redlink.sdk.impl.analysis.model;

import io.redlink.sdk.impl.analysis.model.server.Annotation;
import io.redlink.sdk.impl.analysis.model.server.Annotation.DerefencedEntity;
import io.redlink.sdk.impl.analysis.model.server.Annotation.PlainLiteral;
import io.redlink.sdk.impl.analysis.model.server.Annotation.Position;
import io.redlink.sdk.impl.analysis.model.server.FlatEnhancementStructure;

import java.util.Collection;
import java.util.LinkedList;
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
public class FlatStructureParser extends EnhancementsParser {
	
	/**
	 * 
	 */
	private final FlatEnhancementStructure structure;
	
	public FlatStructureParser(FlatEnhancementStructure structure){
		this.structure = structure;
	}

	@Override
	public Collection<Enhancement> parseEnhancements()
			throws EnhancementParserException {
		Collection<Enhancement> eas = new LinkedList<Enhancement>();
		Map<String, Enhancement> tasByPosition = Maps.newHashMap();
		
		for(Annotation annotation:structure.getAnnotations()){
			EntityAnnotation ea = new EntityAnnotation();
			ea.setConfidence(annotation.getConfidence());
			ea.setEntityLabel(annotation.getLabel());
			ea.setEntityTypes(annotation.getTypes());
			ea.setLanguage(annotation.getLanguage());
			ea.setDataset(annotation.getDataset());
			ea.setEntityReference(createEntity(annotation.getReference(), annotation.getEntity()));
			Collection<Enhancement> relations = new LinkedList<Enhancement>();
			for(Position position:annotation.getPositions()){
				String key = new String("" + position.getStart()).
						concat(new String("" + position.getEnd())); 
				if(tasByPosition.containsKey(key))
					relations.add(tasByPosition.get(key));
				else{
					//TODO Parse TextAnnotation
				}
			}
			ea.setRelations(relations);
			eas.add(ea);
		}
		Collection<Enhancement> result = tasByPosition.values();
		result.addAll(eas);
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

	@Override
	public Collection<String> parseLanguages()
			throws EnhancementParserException {
		Collection<String> languages = Sets.newHashSet();
		for(String language:structure.getLanguages())
			languages.add(language);
		return languages;
	}

	@Override
	public Collection<TextAnnotation> parseTextAnnotations()
			throws EnhancementParserException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<EntityAnnotation> parseEntityAnnotations()
			throws EnhancementParserException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Entity parseEntity(String entityUri)
			throws EnhancementParserException {
		// TODO Auto-generated method stub
		return null;
	}

}
