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
package io.redlink.sdk.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.codehaus.jackson.JsonNode;


/**
 * RedLink's user Application Status data
 *
 * @author Rafa Haro
 * @author Sergio Fernández
 */
public class Status {

    private int owner;
    private boolean accessible;
    private int bytes;
    private int requests;
    private int limit;
    private int seconds;
    private List<String> datasets;
    private List<String> analyses;

    public Status() {
        datasets = new ArrayList<>();
        analyses = new ArrayList<>();
    }

    /**
     * Returns the owner id
     *
     * @return owner id
     */
    public int getOwner() {
        return owner;
    }

    /**
     * Set the owner id
     *
     * @param owner id
     */
    public void setOwner(int owner) {
        this.owner = owner;
    }

    /**
     * Returns true if the Application is accessible
     *
     * @return
     */
    public boolean isAccessible() {
        return accessible;
    }

    /**
     * Returns the number of bytes consumed by the Application
     *
     * @return
     */
    public int getBytes() {
        return bytes;
    }

    /**
     * Returns the number of request attended by the Application
     *
     * @return
     */
    public int getRequests() {
        return requests;
    }

    /**
     * Returns the limit of the Application
     *
     * @return
     */
    public int getLimit() {
        return limit;
    }

    void setLimit(JsonNode node) {
        final String str = node.toString();
        try {
            limit = Integer.parseInt(str);
        } catch (Exception e) {
            if ("unlimited".equals(str)) {
                limit = -1;
            } else {
                limit = 0;
            }
        }
    }

    /**
     * Returns the number of seconds that the application has been active
     *
     * @return
     */
    public int getSeconds() {
        return seconds;
    }

    /**
     * Datasets bound
     *
     * @return
     */
    public List<String> getDatasets() {
        return Collections.unmodifiableList(datasets);
    }

    /**
     * Set datasets
     *
     * @param datasets
     */
    public void setDatasets(List<String> datasets) {
        this.datasets = datasets;
    }

    /**
     * Analyses bound
     *
     * @return analyses
     */
    public List<String> getAnalyses() {
        return Collections.unmodifiableList(analyses);
    }

    /**
     * Set analyses
     *
     * @param analyses
     */
    public void setAnalyses(List<String> analyses) {
        this.analyses = analyses;
    }

}
