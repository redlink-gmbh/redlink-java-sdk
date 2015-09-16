package io.redlink.sdk.impl.analysis.model;

/**
 * The Sentiment of a part of the Document (usually on Sentence level) <ul>
 * <li><code>-1</code> (very negative)
 * <li><code>0</code> (neutral)
 * <li><code>1</code> (very positive)
 * <ul>
 * 
 * @author Rupert Westenthaler
 *
 */
public class SentimentAnnotation extends Enhancement {

    private double sentiment;
    private int starts;
    private int ends;

    public SentimentAnnotation() {
        super();
    }
    
    void setSentiment(double sentiment) {
        this.sentiment = sentiment;
    }
    
    public double getSentiment() {
        return sentiment;
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

    
}
