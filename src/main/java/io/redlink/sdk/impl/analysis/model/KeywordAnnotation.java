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
 * A Keyword extracted form the analyzed content including its {@link #getCount() count},
 * and {@link #getMetric() metirc}.
 *
 */
public class KeywordAnnotation extends Enhancement {
    
    
    private String keyword;
    private String keywordLang;

    private int count = 1;
    private Double metric;
   
    public KeywordAnnotation() {
        super();
    }
    
    public void setKeyword(String keyword, String lang) {
        this.keyword = keyword;
        this.keywordLang = lang;
    }
    
    /**
     * The keyword as mentioned in the text
     * @return
     */
    public String getKeyword() {
        return keyword;
    }
    /**
     * The language of the keyword
     * @return
     */
    public String getKeywordLang() {
        return keywordLang;
    }
    
    void setMetric(Double metric) {
        this.metric = metric;
    }
    /**
     * The metric - importance - of the keyword in the range <code>[0..1]</code>
     * @return
     */
    public Double getMetric() {
        return metric;
    }
    
    void setCount(int count) {
        this.count = count;
    }
    /**
     * The number of mentions of the keyword within the text. For multi-word
     * phrases this might be a calculated average also considering sub-phrase
     * mentions.
     * @return
     */
    public int getCount() {
        return count;
    }
 
}
