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
