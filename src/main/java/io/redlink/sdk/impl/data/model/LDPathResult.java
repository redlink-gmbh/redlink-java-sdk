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
package io.redlink.sdk.impl.data.model;

import org.apache.marmotta.client.model.rdf.RDFNode;

import java.util.*;

/**
 * LDPath Result Set implementation, internally similar to a
 * {@link com.google.common.collect.Multimap}
 *
 * @author sergio.fernandez@redlink.co
 */
public class LDPathResult {

    private Map<String, List<RDFNode>> result;

    /**
     * Constructs an empty result set
     *
     */
    public LDPathResult() {
        this.result = new HashMap<String, List<RDFNode>>();
    }

    /**
     * Constructs a result set
     *
     * @param result results
     */
    public LDPathResult(Map<String, List<RDFNode>> result) {
        this.result = new HashMap<String, List<RDFNode>>(result);
    }

    /**
     * Add a new result
     *
     * @param field
     * @param result
     * @return
     */
    public boolean add(String field, List<RDFNode> result) {
        this.result.put(field, result);
        return true;
    }

    /**
     * Add a single result
     *
     * @param field
     * @param result
     * @return
     */
    public boolean add(String field, RDFNode result) {
        if (!this.result.containsKey(field)) {
            this.result.put(field, new ArrayList<RDFNode>());
        }
        this.result.get(field).add(result);
        return true;
    }

    /**
     * Get field names
     *
     * @return
     */
    public Set<String> getFields() {
        return result.keySet();
    }

    /**
     * Get results for a field name
     * @param field
     * @return
     */
    public List<RDFNode> getResults(String field) {
        return result.get(field);
    }

    /**
     * Size of the result set
     *
     * @return
     */
    public int size() {
        return result.keySet().size();
    }

}
