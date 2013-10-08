package io.redlink.sdk.impl.enhance.model;

import io.redlink.sdk.impl.vocabulary.model.Entity;
import io.redlink.sdk.util.ModelRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import org.openrdf.model.Literal;
import org.openrdf.model.Model;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.query.Binding;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * 
 * 
 * @author Rafa Haro
 * 
 */
public final class OpenRDFEnhancementsParser implements EnhancementsParser {
	/**
	 * 
	 */
	private Model model;

	/**
	 * 
	 */
	private ModelRepository repository;

	public OpenRDFEnhancementsParser(Model model)
			throws EnhancementParserException {
		this.model = model;
		try {
			this.repository = ModelRepository.create(model);
		} catch (RepositoryException e) {
			throw new EnhancementParserException(
					"There was an error initializing the Enhancement Parser", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.redlink.sdk.impl.enhance.model.EnhancementsParser#createEnhancements()
	 */
	public Enhancements createEnhancements() throws EnhancementParserException {
		Enhancements enhancements = new Enhancements(model);
		enhancements.setEnhancements(parseEnhancements());
		enhancements.setLanguages(parseLanguages());
		return enhancements;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.redlink.sdk.impl.enhance.model.EnhancementsParser#parseLanguages()
	 */
	public Collection<String> parseLanguages() throws EnhancementParserException {

		Collection<String> languages = Sets.newHashSet();

		String textAnnotationsQuery = "PREFIX fise: <http://fise.iks-project.eu/ontology/> \n"
				+ "PREFIX dct: <http://purl.org/dc/terms/> \n"
				+ "SELECT * { \n"
				+ "  ?annotation a fise:TextAnnotation . \n"
				+ "  ?annotation dct:type <"
				+ DCTERMS.LINGUISTIC_SYSTEM
				+ "> . \n"
				+ "  ?annotation dct:language ?language . \n"
				+ "} \n";

		try {
			RepositoryConnection conn = repository.getConnection();
			try {
				conn.begin();
	
				TupleQueryResult textAnnotationsResults = conn.prepareTupleQuery(QueryLanguage.SPARQL, textAnnotationsQuery).evaluate();
				while (textAnnotationsResults.hasNext()) {
					BindingSet result = textAnnotationsResults.next();
					if (result.hasBinding("language")) {
						languages.add(result.getBinding("language").getValue().stringValue());
					}
				}
				conn.commit();
			} finally {
				conn.close();
			}
		} catch (QueryEvaluationException | MalformedQueryException e) {
			throw new EnhancementParserException(
					"Error parsing text annotations", e);
		} catch (RepositoryException e) {
			throw new EnhancementParserException(
					"Error querying the RDF Model obtained as Service Response",
					e);
		}

		return languages;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.redlink.sdk.impl.enhance.model.EnhancementsParser#parseEnhancements()
	 */
	public Collection<Enhancement> parseEnhancements()
			throws EnhancementParserException {
		Multimap<Enhancement, String> relations = ArrayListMultimap.create();
		Map<String, Enhancement> enhancementsByUri = Maps.newHashMap();

		try {
			RepositoryConnection conn = repository.getConnection();
			try {
				conn.begin();
	
				Collection<TextAnnotation> tas = parseTextAnnotations(conn, relations);
				Collection<EntityAnnotation> eas = parseEntityAnnotations(conn, relations);
	
				for (Enhancement e : tas) {
					enhancementsByUri.put(e.uri, e);
				}
				
				for (Enhancement e : eas) {
					enhancementsByUri.put(e.uri, e);
				}
	
				for (Enhancement e : relations.keys()) {
					Collection<String> relationsUris = relations.get(e);
					Collection<Enhancement> relationsEnhans = Sets.newHashSet();
					for (String uri : relationsUris) {
						relationsEnhans.add(enhancementsByUri.get(uri));
					}
					e.setRelations(relationsEnhans);
				}
	
				conn.commit();
			} finally {
				conn.close();
			}
		} catch (RepositoryException e) {
			throw new EnhancementParserException(
					"Error querying the RDF Model obtained as Service Response",
					e);
		}

		return enhancementsByUri.values();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.redlink.sdk.impl.enhance.model.EnhancementsParser#parseTextAnnotations
	 * ()
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Collection<TextAnnotation> parseTextAnnotations()
			throws EnhancementParserException {
		Multimap<Enhancement, String> relations = ArrayListMultimap.create();

		try {
			RepositoryConnection conn = repository.getConnection();
			conn.begin();
			Collection<TextAnnotation> tas = parseTextAnnotations(conn,
					relations);
			Collection<TextAnnotation> result = (Collection) resolveRelations(
					relations, conn); // Safe Casting
			for (TextAnnotation ta : tas)
				if (!result.contains(ta))
					result.add(ta);
			conn.close();
			return result;
		} catch (RepositoryException e) {
			throw new EnhancementParserException(
					"Error querying the RDF Model obtained as Service Response",
					e);
		}
	}

	private Collection<TextAnnotation> parseTextAnnotations(
			RepositoryConnection conn, Multimap<Enhancement, String> relations)
			throws EnhancementParserException {

		Collection<TextAnnotation> tas = Sets.newHashSet();

		String textAnnotationsQuery = "PREFIX fise: <http://fise.iks-project.eu/ontology/> \n"
				+ "PREFIX dct: <http://purl.org/dc/terms/> \n"
				+ "SELECT * { \n"
				+ "  ?annotation a fise:TextAnnotation ; \n"
				+ "	 fise:confidence ?confidence ; \n"
				+ "    fise:extracted-from ?extractedFrom ; \n"
				+ "	   dct:created ?created ; \n"
				+ "    dct:creator ?creator . \n"
				+ "  OPTIONAL { ?annotation dct:type ?type} \n"
				+ "  OPTIONAL { ?annotation dct:language ?language} \n"
				+ "  OPTIONAL { ?annotation fise:start ?start ; fise:end ?end } \n"
				+ "  OPTIONAL { ?annotation dct:relation ?relation } \n"
				+ "  OPTIONAL { ?annotation fise:selection-context ?selectionContext } \n"
				+ "  OPTIONAL { ?annotation fise:selected-text ?selectedText } \n"
				+ "} \n";

		try {
			TupleQueryResult textAnnotationsResults = conn.prepareTupleQuery(
					QueryLanguage.SPARQL, textAnnotationsQuery).evaluate();
			while (textAnnotationsResults.hasNext()) {
				BindingSet result = textAnnotationsResults.next();
				if (!result.hasBinding("type")
						|| !result
								.getBinding("type")
								.getValue()
								.stringValue()
								.equalsIgnoreCase(
										DCTERMS.LINGUISTIC_SYSTEM.stringValue())) // Filter
																					// Language
																					// Annotations
				{
					final String uri = result.getBinding("annotation")
							.getValue().stringValue();
					Enhancement textAnnotation = Iterables.find(
							relations.keys(), new Predicate<Enhancement>() {
								@Override
								public boolean apply(Enhancement ta) {
									return ta.getUri().equals(uri);
								}
							}, new TextAnnotation(uri));

					setTextAnnotationData((TextAnnotation) textAnnotation,
							result, relations);
					if (!tas.contains(textAnnotation))
						tas.add((TextAnnotation) textAnnotation);
				}
			}
		} catch (QueryEvaluationException | MalformedQueryException e) {
			throw new EnhancementParserException(
					"Error parsing text annotations", e);
		} catch (RepositoryException e) {
			throw new EnhancementParserException(
					"Error querying the RDF Model obtained as Service Response",
					e);
		}

		return tas;
	}

	private void setTextAnnotationData(TextAnnotation textAnnotation,
			BindingSet result, Multimap<Enhancement, String> relations)
			throws RepositoryException {
		if (!relations.containsKey(textAnnotation)) {
			setEnhancementData(textAnnotation, result);
			if (result.hasBinding("start")) {
				textAnnotation.setStarts(Integer.parseInt(result
						.getBinding("start").getValue().stringValue()));
				textAnnotation.setEnds(Integer.parseInt(result
						.getBinding("end").getValue().stringValue()));
			}
			if (result.hasBinding("type")) {
				textAnnotation.setType(result.getBinding("type").getValue()
						.stringValue());
			}
			if (result.hasBinding("relation")) {
				String nextRelationUri = result.getBinding("relation")
						.getValue().stringValue();
				relations.put(textAnnotation, nextRelationUri);
			}
			if (result.hasBinding("selectionContext")) {
				textAnnotation.setSelectionContext(result
						.getBinding("selectionContext").getValue()
						.stringValue());
			}
			if (result.hasBinding("selectedText")) {
				Binding selectedText = result.getBinding("selectedText");
				textAnnotation.setSelectedText(selectedText.getValue()
						.stringValue());
				if (!result.hasBinding("language")
						&& (selectedText.getValue() instanceof Literal))
					textAnnotation.setLanguage(((Literal) selectedText
							.getValue()).getLanguage());
			}
		} else {
			if (result.hasBinding("relation")) {
				String nextRelationUri = result.getBinding("relation")
						.getValue().stringValue();
				relations.put(textAnnotation, nextRelationUri);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.redlink.sdk.impl.enhance.model.EnhancementsParser#parseEntityAnnotations
	 * ()
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Collection<EntityAnnotation> parseEntityAnnotations()
			throws EnhancementParserException {
		Multimap<Enhancement, String> relations = ArrayListMultimap.create();

		try {
			RepositoryConnection conn = repository.getConnection();
			conn.begin();
			Collection<EntityAnnotation> eas = parseEntityAnnotations(conn,
					relations);
			Collection<EntityAnnotation> result = (Collection) resolveRelations(
					relations, conn); // Safe Casting
			for (EntityAnnotation ea : eas)
				if (!result.contains(ea))
					result.add(ea);
			conn.close();
			return result;
		} catch (RepositoryException e) {
			throw new EnhancementParserException(
					"Error querying the RDF Model obtained as Service Response",
					e);
		}
	}

	private Collection<EntityAnnotation> parseEntityAnnotations(
			RepositoryConnection conn, Multimap<Enhancement, String> relations)
			throws EnhancementParserException {

		Collection<EntityAnnotation> eas = Sets.newHashSet();

		String entityAnnotationsQuery = "PREFIX fise: <http://fise.iks-project.eu/ontology/> \n"
				+ "PREFIX dct: <http://purl.org/dc/terms/> \n"
				+ "PREFIX entityhub: <http://stanbol.apache.org/ontology/entityhub/entityhub#> \n"
				+ "SELECT * { \n"
				+ "  ?annotation a fise:EntityAnnotation ; \n"
				+ "	  fise:confidence ?confidence ; \n"
				+ "	  dct:created ?created ; \n"
				+ "     dct:creator ?creator ; \n"
				+ "    fise:extracted-from ?extractedFrom ; \n"
				+ "  OPTIONAL { ?language a dct:language  } \n"
				+ "  OPTIONAL { ?annotation dct:relation ?relation } \n"
				+ "  OPTIONAL { ?annotation fise:entity-label ?entityLabel } \n"
				+ "  OPTIONAL { ?annotation fise:entity-reference ?entityReference } \n"
				+ "  OPTIONAL { ?annotation fise:entity-type ?entityType } \n"
				+ "  OPTIONAL { ?annotation entityhub:site ?site } \n" + "}";

		try {
			TupleQueryResult entityAnnotationsResults = conn.prepareTupleQuery(
					QueryLanguage.SPARQL, entityAnnotationsQuery).evaluate();

			while (entityAnnotationsResults.hasNext()) {
				BindingSet result = entityAnnotationsResults.next();
				final String uri = result.getBinding("annotation").getValue()
						.stringValue();
				Enhancement entityAnnotation = Iterables.find(relations.keys(),
						new Predicate<Enhancement>() {
							@Override
							public boolean apply(Enhancement ta) {
								return ta.getUri().equals(uri);
							}
						}, new EntityAnnotation(uri));

				setEntityAnnotationData((EntityAnnotation) entityAnnotation,
						result, conn, relations);
				if (!eas.contains(entityAnnotation))
					eas.add((EntityAnnotation) entityAnnotation);
			}
		} catch (QueryEvaluationException | MalformedQueryException e) {
			throw new EnhancementParserException(
					"Error parsing text annotations", e);
		} catch (RepositoryException e) {
			throw new EnhancementParserException(
					"Error querying the RDF Model obtained as Service Response",
					e);
		}

		return eas;
	}

	private void setEntityAnnotationData(EntityAnnotation entityAnnotation,
			BindingSet result, RepositoryConnection conn,
			Multimap<Enhancement, String> relations)
			throws RepositoryException, EnhancementParserException {

		if (!relations.containsKey(entityAnnotation)) {
			setEnhancementData(entityAnnotation, result);
			if (result.hasBinding("entityLabel")) {
				Binding entityLabel = result.getBinding("entityLabel");
				entityAnnotation.setEntityLabel(entityLabel.getValue()
						.stringValue());
				if (!result.hasBinding("language")
						&& (entityLabel.getValue() instanceof Literal))
					entityAnnotation.setLanguage(((Literal) entityLabel
							.getValue()).getLanguage());

			}

			if (result.hasBinding("entityReference")) {
				entityAnnotation
						.setEntityReference(parseEntity(conn, result
								.getBinding("entityReference").getValue()
								.stringValue()));
			}

			if (result.hasBinding("relation")) {
				String nextRelationUri = result.getBinding("relation")
						.getValue().stringValue();
				relations.put(entityAnnotation, nextRelationUri);
			}

			Collection<String> types = new HashSet<String>();
			if (result.hasBinding("entityType")) {
				types.add(result.getBinding("entityType").getValue()
						.stringValue());
			}
			entityAnnotation.setEntityTypes(types);

			if (result.hasBinding("site")) {
				entityAnnotation.setSite(result.getBinding("site").getValue()
						.stringValue());
			}
		} else {
			if (result.hasBinding("relation")) {
				final String nextRelationUri = result.getBinding("relation")
						.getValue().stringValue();
				if (!relations.containsEntry(entityAnnotation, nextRelationUri))
					relations.put(entityAnnotation, nextRelationUri);
			}

			if (result.hasBinding("entityType")) {
				final String entityType = result.getBinding("entityType")
						.getValue().stringValue();
				Collection<String> types = entityAnnotation.getEntityTypes();
				Optional<String> eType = Iterables.tryFind(types,
						new Predicate<String>() {

							@Override
							public boolean apply(String arg0) {
								return arg0.equals(entityType);
							}

						});
				if (!eType.isPresent())
					types.add(entityType);
			}
		}

	}

	private Collection<Enhancement> resolveRelations(
			Multimap<Enhancement, String> relations, RepositoryConnection conn)
			throws EnhancementParserException {

		Queue<String> toParse = new LinkedList<String>();
		toParse.addAll(Sets.newHashSet(relations.values()));
		Map<String, Enhancement> allRelations = new HashMap<String, Enhancement>();
		Collection<Enhancement> initialEnhancements = relations.keys();

		while (!toParse.isEmpty()) {
			String nextRelation = toParse.poll();
			Enhancement nextEnhancement = parseEnhancement(nextRelation, conn,
					toParse, relations);

			if (nextEnhancement != null)
				allRelations.put(nextRelation, nextEnhancement);
		}

		for (Enhancement e : relations.keys()) {
			Collection<String> relationsUris = relations.get(e);
			Collection<Enhancement> nextRelEnhancements = Sets.newHashSet();
			for (String uri : relationsUris)
				if (uri != null)
					nextRelEnhancements.add(allRelations.get(uri));
			e.setRelations(nextRelEnhancements);
		}

		return initialEnhancements;
	}

	private Enhancement parseEnhancement(String nextRelation,
			RepositoryConnection conn, Queue<String> toParse,
			Multimap<Enhancement, String> relations)
			throws EnhancementParserException {

		String enhancementQuery = "PREFIX fise: <http://fise.iks-project.eu/ontology/> \n"
				+ "SELECT * { \n"
				+ "  <"
				+ nextRelation
				+ "> a fise:EntityAnnotation ; \n" + "}";
		Enhancement enhancement = null;

		try {
			TupleQueryResult enhancementResults = conn.prepareTupleQuery(
					QueryLanguage.SPARQL, enhancementQuery).evaluate();
			if (enhancementResults.hasNext())
				enhancement = parseEntityAnnotation(nextRelation, conn,
						toParse, relations);
			else
				enhancement = parseTextAnnotation(nextRelation, conn, toParse,
						relations);
		} catch (QueryEvaluationException | MalformedQueryException e) {
			throw new EnhancementParserException("Error parsing enhancement "
					+ nextRelation, e);
		} catch (RepositoryException e) {
			throw new EnhancementParserException(
					"Error parsing Enhancement Type for URI" + nextRelation, e);
		}

		return enhancement;
	}

	private Enhancement parseTextAnnotation(String taUri,
			RepositoryConnection conn, Queue<String> toParse,
			Multimap<Enhancement, String> relations)
			throws RepositoryException, EnhancementParserException {

		TextAnnotation enhancement = new TextAnnotation(taUri);
		String textAnnotationQuery = "PREFIX fise: <http://fise.iks-project.eu/ontology/> \n"
				+ "PREFIX dct: <http://purl.org/dc/terms/> \n"
				+ "PREFIX entityhub: <http://stanbol.apache.org/ontology/entityhub/entityhub#> \n"
				+ "SELECT * { \n" + "	<"
				+ taUri
				+ ">  fise:confidence ?confidence . \n"
				+ "	<"
				+ taUri
				+ ">  dct:created ?created . \n"
				+ "   <"
				+ taUri
				+ ">  dct:creator ?creator . \n"
				+ taUri
				+ ">  fise:extracted-from ?extractedFrom . \n"
				+ "  OPTIONAL { <"
				+ taUri
				+ ">  dct:language ?language } \n"
				+ "  OPTIONAL { <"
				+ taUri
				+ "> fise:start ?start ; fise:end ?end } \n"
				+ "  OPTIONAL { <"
				+ taUri
				+ "> dct:type ?type } \n"
				+ "  OPTIONAL { <"
				+ taUri
				+ "> dct:relation ?relation } \n"
				+ "  OPTIONAL { <"
				+ taUri
				+ "> fise:selection-context ?selectionContext } \n"
				+ "  OPTIONAL { <"
				+ taUri
				+ "> fise:selected-text ?selectedText } \n" + "}";
		try {
			TupleQueryResult textAnnotationResults = conn.prepareTupleQuery(
					QueryLanguage.SPARQL, textAnnotationQuery).evaluate();

			while (textAnnotationResults.hasNext()) {
				BindingSet result = textAnnotationResults.next();
				int i = 0;
				if (i == 0) {
					setEnhancementData(enhancement, result);
					if (result.hasBinding("start")) {
						enhancement.setStarts(Integer.parseInt(result
								.getBinding("start").getValue().stringValue()));
						enhancement.setEnds(Integer.parseInt(result
								.getBinding("end").getValue().stringValue()));
					}
					if (result.hasBinding("type")) {
						enhancement.setType(result.getBinding("type")
								.getValue().stringValue());
					}
					if (result.hasBinding("relation")) {
						String nextRelationUri = result.getBinding("relation")
								.getValue().stringValue();
						if (!relations.values().contains(nextRelationUri))
							toParse.add(nextRelationUri);
						relations.put(enhancement, nextRelationUri);
					}
					if (result.hasBinding("selectionContext")) {
						enhancement.setSelectionContext(result
								.getBinding("selectionContext").getValue()
								.stringValue());
					}
					if (result.hasBinding("selectedText")) {
						Binding selectedText = result
								.getBinding("selectedText");
						enhancement.setSelectedText(selectedText.getValue()
								.stringValue());
						if (!result.hasBinding("language")
								&& (selectedText.getValue() instanceof Literal))
							enhancement.setLanguage(((Literal) selectedText
									.getValue()).getLanguage());
					}
				} else {
					if (result.hasBinding("relation")) {
						String nextRelationUri = result.getBinding("relation")
								.getValue().stringValue();
						if (!relations.values().contains(nextRelationUri))
							toParse.add(nextRelationUri);
						relations.put(enhancement, nextRelationUri);
					}
				}

				i++;
			}
		} catch (QueryEvaluationException | MalformedQueryException e) {
			throw new EnhancementParserException(
					"Error parsing text annotation with URI: " + taUri, e);
		}

		return enhancement;
	}

	private EntityAnnotation parseEntityAnnotation(String eaUri,
			RepositoryConnection conn, Queue<String> toParse,
			Multimap<Enhancement, String> relations)
			throws RepositoryException, EnhancementParserException {

		EntityAnnotation enhancement = new EntityAnnotation(eaUri);
		String entityAnnotationsQuery = "PREFIX fise: <http://fise.iks-project.eu/ontology/> \n"
				+ "PREFIX dct: <http://purl.org/dc/terms/> \n"
				+ "PREFIX entityhub: <http://stanbol.apache.org/ontology/entityhub/entityhub#> \n"
				+ "SELECT * { \n" + "	<"
				+ eaUri
				+ ">  fise:confidence ?confidence ; \n"
				+ "	<"
				+ eaUri
				+ ">  dct:created ?created ; \n"
				+ "   <"
				+ eaUri
				+ ">  dct:creator ?creator ; \n"
				+ "   <"
				+ eaUri
				+ ">  dct:language ?language . \n"
				+ eaUri
				+ ">  fise:extracted-from ?extractedFrom . \n"
				+ "  OPTIONAL { <"
				+ eaUri
				+ "> dct:relation ?relation } \n"
				+ "  OPTIONAL { <"
				+ eaUri
				+ "> fise:entity-label ?entityLabel } \n"
				+ "  OPTIONAL { <"
				+ eaUri
				+ "> fise:entity-reference ?entityReference } \n"
				+ "  OPTIONAL { <"
				+ eaUri
				+ "> fise:entity-type ?entityType } \n"
				+ "  OPTIONAL { <"
				+ eaUri + "> entityhub:site ?site } \n" + "}";
		try {
			TupleQueryResult entityAnnotationsResults = conn.prepareTupleQuery(
					QueryLanguage.SPARQL, entityAnnotationsQuery).evaluate();

			int i = 0;
			while (entityAnnotationsResults.hasNext()) {
				BindingSet result = entityAnnotationsResults.next();
				if (i == 0) {
					setEnhancementData(enhancement, result);
					if (result.hasBinding("entityLabel")) {
						Binding entityLabel = result.getBinding("entityLabel");
						enhancement.setEntityLabel(entityLabel.getValue()
								.stringValue());
						if (!result.hasBinding("language")
								&& (entityLabel.getValue() instanceof Literal))
							enhancement.setLanguage(((Literal) entityLabel
									.getValue()).getLanguage());

					}
					if (result.hasBinding("entityReference")) {
						enhancement.setEntityReference(parseEntity(conn, result
								.getBinding("entityReference").getValue()
								.stringValue()));
					}
					if (result.hasBinding("relation")) {
						String nextRelationUri = result.getBinding("relation")
								.getValue().stringValue();
						if (!relations.values().contains(nextRelationUri))
							toParse.add(nextRelationUri);
						relations.put(enhancement, nextRelationUri);
					}
					if (result.hasBinding("entityType")) {
						Collection<String> types = new HashSet<String>();
						types.add(result.getBinding("entityType").getValue()
								.stringValue());
						enhancement.setEntityTypes(types);
					}
					if (result.hasBinding("site")) {
						enhancement.setSite(result.getBinding("site")
								.getValue().stringValue());
					}
				} else {
					if (result.hasBinding("relation")) {
						String nextRelationUri = result.getBinding("relation")
								.getValue().stringValue();
						Collection<String> eRelations = relations
								.get(enhancement);
						if (!eRelations.contains(nextRelationUri)) {
							if (!relations.values().contains(nextRelationUri))
								toParse.add(nextRelationUri);
							relations.put(enhancement, nextRelationUri);
						}
					}

					if (result.hasBinding("entityType")) {
						String nextType = result.getBinding("entityType")
								.getValue().stringValue();
						if (!enhancement.getEntityTypes().contains(nextType))
							enhancement.getEntityTypes().add(nextType);
					}
				}

				i++;
			}

		} catch (QueryEvaluationException | MalformedQueryException e) {
			throw new EnhancementParserException(
					"Error parsing entity annotation with URI: " + eaUri, e);
		}

		return enhancement;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.redlink.sdk.impl.enhance.model.EnhancementsParser#parseEntity(java
	 * .lang.String)
	 */
	public Entity parseEntity(String entityUri)
			throws EnhancementParserException {
		try {
			RepositoryConnection conn = repository.getConnection();
			conn.begin();
			Entity result = parseEntity(conn, entityUri);
			conn.close();
			return result;
		} catch (RepositoryException e) {
			throw new EnhancementParserException(
					"Error querying the RDF Model obtained as Service Response",
					e);
		}
	}

	private Entity parseEntity(RepositoryConnection conn, String entityUri)
			throws EnhancementParserException {
		String entityQuery = "SELECT ?p ?o { \n" + "  <" + entityUri
				+ "> ?p ?o ; \n" + "}";
		Entity entity = new Entity(entityUri);

		try {
			TupleQueryResult entityResults = conn.prepareTupleQuery(
					QueryLanguage.SPARQL, entityQuery).evaluate();
			while (entityResults.hasNext()) {
				BindingSet result = entityResults.next();
				Value object = result.getBinding("o").getValue();
				if (object instanceof LiteralImpl) {
					String language = ((LiteralImpl) object).getLanguage();
					if (language == null) {
						entity.addPropertyValue(result.getBinding("p")
								.getValue().stringValue(), object.stringValue());
					} else {
						entity.addPropertyValue(result.getBinding("p")
								.getValue().stringValue(), language,
								object.stringValue());
					}
				} else {
					entity.addPropertyValue(result.getBinding("p").getValue()
							.stringValue(), object.stringValue());
				}
			}
		} catch (QueryEvaluationException | MalformedQueryException e) {
			throw new EnhancementParserException(
					"Error parsing text annotations", e);
		} catch (RepositoryException e) {
			throw new EnhancementParserException(
					"Error querying the RDF Model obtained as Service Response",
					e);
		}

		return entity;
	}

	private void setEnhancementData(Enhancement enhancement, BindingSet result) {
		enhancement.setConfidence(Double.parseDouble(result
				.getBinding("confidence").getValue().stringValue()));
		SimpleDateFormat format = new SimpleDateFormat(
				"yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");
		Date created;
		try {
			created = format.parse(result.getBinding("created").getValue()
					.stringValue());
		} catch (ParseException e) {
			created = new Date();
		}
		enhancement.setCreated(created);
		enhancement.setCreator(result.getBinding("creator").getValue()
				.stringValue());
		enhancement.setLanguage(result.getBinding("language") != null ? result
				.getBinding("language").getValue().stringValue() : null);
		enhancement.setExtractedFrom(result.getBinding("extractedFrom")
				.getValue().stringValue());
	}
}
