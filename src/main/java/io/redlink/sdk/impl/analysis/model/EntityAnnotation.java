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
 * Entity annotation, suggested/linked entities recognized within the text
 *
 * @author sergio.fernandez@redlink.co
 * @author rharo@zaizi.com
 * @author rupert.westenthaler@redlink.co
 *
 * @see <a href="https://stanbol.apache.org/docs/trunk/components/enhancer/enhancementstructure#fiseentityannotation">fise:EntityAnnotation</a>
 */
public final class EntityAnnotation extends Enhancement {

    // properties
    private String entityLabel = null; // http://fise.iks-project.eu/ontology/entity-label
    private String entityLabelLang;
    private Entity entityReference = null; // http://fise.iks-project.eu/ontology/entity-reference
    private Collection<String> entityTypes = null; // http://fise.iks-project.eu/ontology/entity-type
    private String dataset = null; // http://stanbol.apache.org/ontology/entityhub/entityhub#"

    public EntityAnnotation() {
        super();
    }

    /**
     * Returns the preferred entity label
     *
     * @return
     */
    public String getEntityLabel() {
        return entityLabel;
    }

    public String getEntityLabelLang() {
        return entityLabelLang;
    }
    
    /**
     * @deprecated use {@link #getEntityLabelLang()} instead
     */
    @Override
    @Deprecated
    public String getLanguage() {
        return getEntityLabelLang();
    }
    
    void setEntityLabel(String entityLabel, String lang) {
        this.entityLabel = entityLabel;
        this.entityLabelLang = lang;
    }

    /**
     * Returns the dereferenced {@link Entity} associated to the {@link EntityAnnotation}
     *
     * @return
     */
    public Entity getEntityReference() {
        return entityReference;
    }

    void setEntityReference(Entity entityReference) {
        this.entityReference = entityReference;
    }

    /**
     * Returns the {@link Collection} of types associated to the entity
     *
     * @return
     */
    public Collection<String> getEntityTypes() {
        return entityTypes;
    }

    void setEntityTypes(Collection<String> entityTypes) {
        this.entityTypes = entityTypes;
    }
    
    /**
     * The dataset of the entity (e.g. an own dataset or a public dataset of an other user)
     *
     * @return the dataset of the referenced entity or <code>null</code> if not applicable.
     */
    public String getDataset() {
        return dataset;
    }

    void setDataset(String dataset) {
        this.dataset = dataset;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((entityReference == null) ? 0 : entityReference.hashCode());
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
        EntityAnnotation other = (EntityAnnotation) obj;
        if (entityReference == null) {
            if (other.entityReference != null)
                return false;
        } else if (!entityReference.equals(other.entityReference))
            return false;
        return true;
    }

}
