package io.redlink.sdk.impl.analysis.model;

import java.util.Collection;

/**
 * 
 * @author rafa.haro@redlink.co
 *
 */
public class TopicAnnotation extends Enhancement {

		// properties
		private String topicLabel = null; // http://fise.iks-project.eu/ontology/entity-label
		private Entity topicReference = null; // http://fise.iks-project.eu/ontology/entity-reference
		private Collection<String> types = null; // http://fise.iks-project.eu/ontology/entity-type
		private String site = null; // http://stanbol.apache.org/ontology/entityhub/entityhub#"
		
		public String getTopicLabel() {
			return topicLabel;
		}
		
		void setTopicLabel(String topicLabel) {
			this.topicLabel = topicLabel;
		}
		public Entity getTopicReference() {
			return topicReference;
		}
		
		void setTopicReference(Entity topicReference) {
			this.topicReference = topicReference;
		}
		
		public Collection<String> getTypes() {
			return types;
		}
		
		void setTypes(Collection<String> types) {
			this.types = types;
		}
		
		public String getSite() {
			return site;
		}
		
		void setSite(String site) {
			this.site = site;
		}	
}
