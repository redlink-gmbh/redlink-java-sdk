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
 * Text annotation, selects pieces of parsed textual content by using the following properties
 *
 * @author sergio.fernandez@redlink.co
 * @author rafa.haro@redlink.co
 * @see <a href="https://stanbol.apache.org/docs/trunk/components/enhancer/enhancementstructure#fisetextannotation">FISE Text Annotation</a>
 */
public final class TextAnnotation extends Enhancement {

    // properties
    private int starts = 0; // http://fise.iks-project.eu/ontology/start
    private int ends = 0; // http://fise.iks-project.eu/ontology/end
    private String selectedText = null; // http://fise.iks-project.eu/ontology/selected-text
    private String selectedTextLang = null;
    private String selectionContext = null; // http://fise.iks-project.eu/ontology/selection-context
    private String selectionPrefix = null;
    private String selectionSuffix = null;
    private String type = null; // http://purl.org/dc/terms/type

    public TextAnnotation() {
        super();
    }

    /**
     * Returns the position of the text where the annotation starts
     *
     * @return
     */
    public int getStarts() {
        return starts;
    }

    void setStarts(int starts) {
        this.starts = starts;
    }

    /**
     * Returns the position of the text where the annotation ends
     *
     * @return
     */
    public int getEnds() {
        return ends;
    }

    void setEnds(int ends) {
        this.ends = ends;
    }

    /**
     * Returns the matched/extracted piece of text from the analyzed content
     *
     * @return
     */
    public String getSelectedText() {
        return selectedText;
    }

    public String getSelectedTextLang() {
        return selectedTextLang;
    }
    
    /**
     * @deprecated use {@link #getSelectedTextLang()}
     */
    @Override
    @Deprecated
    public String getLanguage() {
        return getSelectedTextLang();
    }
    
    void setSelectedText(String selectedText, String lang) {
        this.selectedText = selectedText;
        this.selectedTextLang = lang;
    }

    /**
     * Returns a surrounding context of the matched text
     *
     * @return
     */
    public String getSelectionContext() {
        return selectionContext;
    }

    void setSelectionContext(String selectionContext) {
        this.selectionContext = selectionContext;
    }

    void setSelectionPrefixSuffix(String prefix, String suffix){
        this.selectionPrefix = prefix;
        this.selectionSuffix = suffix;
    }
    /**
     * Some chars before the {@link #getSelectedText()} aimed to allow the location
     * of the selection in case {@link #getStarts()} and {@link #getEnds()} can 
     * not be used (e.g. for rich text documents)
     * @return the selection prefix
     */
    public String getSelectionPrefix() {
        return selectionPrefix;
    }
    
    /**
     * Some chars after the {@link #getSelectedText()} aimed to allow the location
     * of the selection in case {@link #getStarts()} and {@link #getEnds()} can 
     * not be used (e.g. for rich text documents)
     * @return the selection prefix
     */
    public String getSelectionSuffix() {
        return selectionSuffix;
    }
    
    /**
     * Returns the type of the entity annotation
     *
     * @return
     */
    public String getType() {
        return type;
    }

    void setType(String type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ends;
        result = prime * result + starts;
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
        TextAnnotation other = (TextAnnotation) obj;
        if (ends != other.ends)
            return false;
        if (starts != other.starts)
            return false;
        return true;
    }


}
