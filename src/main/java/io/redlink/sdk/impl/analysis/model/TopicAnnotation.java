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


/**
 * TopicAnnotation are used to categorize/classify the parsed content along some categorization system
 *
 * @author rafa.haro@redlink.co
 * @see https://stanbol.apache.org/docs/trunk/components/enhancer/enhancementstructure#fisetopicannotation
 */
public class TopicAnnotation extends Enhancement {

    // properties
    private String topicLabel = null; // http://fise.iks-project.eu/ontology/entity-label
    private String topicLabelLang = null;
    private Entity topicReference = null; // http://fise.iks-project.eu/ontology/entity-reference
    private String origin = null; // http://stanbol.apache.org/ontology/entityhub/entityhub#"
    private String summary = null; // Entity (Concept) Description

    /**
     * Returns the category preferred label
     *
     * @return
     */
    public String getTopicLabel() {
        return topicLabel;
    }

    public String getTopicLabelLang() {
        return topicLabelLang;
    }

    /**
     * @deprecated use {@link #getTopicLabelLang()} instead
     */
    @Override
    @Deprecated
    public String getLanguage() {
        return getTopicLabelLang();
    }
    
    void setTopicLabel(String topicLabel, String lang) {
        this.topicLabel = topicLabel;
        this.topicLabelLang = lang;
    }

    /**
     * Returns the URI of the category
     *
     * @return
     */
    public Entity getTopicReference() {
        return topicReference;
    }

    void setTopicReference(Entity topicReference) {
        this.topicReference = topicReference;
    }

    /**
     * @see #getOrign()
     * @deprecated
     */
    @Deprecated
    public String getDataset() {
        return getOrigin();
    }
    
    /**
     * The origin of the entity (e.g. an own dataset or a public dataset of an other user)
     * @return the origin of the referenced entity or <code>null</code> if not applicable.
     */
    public String getOrigin() {
        return origin;
    }

    void setOrigin(String origin) {
        this.origin = origin;
    }
    /**
     * Returns a description of the category
     *
     * @return
     */
    public String getSummary() {
        return summary;
    }

    void setSummary(String summary) {
        this.summary = summary;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((topicReference == null) ? 0 : topicReference.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TopicAnnotation other = (TopicAnnotation) obj;
        if (topicReference == null) {
            if (other.topicReference != null)
                return false;
        } else if (!topicReference.equals(other.topicReference))
            return false;
        return true;
    }
}
