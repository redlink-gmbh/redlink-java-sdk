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
 * RedLink's simplified Analysis' Annotations common schema. For more information, please visit https://stanbol.apache.org/docs/trunk/components/enhancer/enhancementstructure
 *
 * @author sergio.fernandez@redlink.co
 * @author rafa.haro@redlink.co
 * @see https://stanbol.apache.org/docs/trunk/components/enhancer/enhancementstructure
 */
public abstract class Enhancement implements Comparable<Enhancement> {
    // properties
    protected Double confidence = null; // http://fise.iks-project.eu/ontology/confidence
    protected Collection<Enhancement> relations = null; // http://purl.org/dc/terms/relation

    public Enhancement() {

    }

    /**
     * Get Annotation's confidence value. A confidence value will be always in the range [0,1]. For Text annotations, the confidence value
     * provides an estimation about how likely the matched text correspond to some entity in one of the configured datasets for the user RedLink
     * application and will depend of the configured analysis engines. Entity annotations' confidence values provide an estimation about how likely
     * a Text Annotation actually refers to the entity
     *
     * @return Annotation's confidence value
     */
    public Double getConfidence() {
        return confidence;
    }

    void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    /**
     * Returns a {@link Collection} of Annotations related to the current one
     *
     * @return
     */
    public Collection<Enhancement> getRelations() {
        return relations;
    }

    void setRelations(Collection<Enhancement> relations) {
        this.relations = relations;
    }

    /**
     * Enhancements do not have a language. 
     *
     * @return <code>null</code>
     * @deprecated use {label}Lanugage methods of subclasses. This will allways
     * return <code>null</code> (if not overridden by subclasses).
     */
    public String getLanguage() {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Enhancement o) {
        if (this.equals(o))
            return 0;

        if (this.confidence > o.getConfidence())
            return -1;
        else if (this.confidence < o.getConfidence())
            return 1;
        else
            return 0;
    }
}
