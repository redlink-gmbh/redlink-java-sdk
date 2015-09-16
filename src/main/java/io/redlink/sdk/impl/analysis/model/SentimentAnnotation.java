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
 * The Sentiment of a part of the Document (usually on Sentence level) <ul>
 * <li><code>-1</code> (very negative)
 * <li><code>0</code> (neutral)
 * <li><code>1</code> (very positive)
 * <ul>
 * 
 * @author Rupert Westenthaler
 *
 */
public class SentimentAnnotation extends Enhancement{

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
