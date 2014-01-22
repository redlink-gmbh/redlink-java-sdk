package io.redlink.sdk.impl.data.model;

import org.apache.marmotta.client.model.rdf.RDFNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author sergio.fernandez@redlink.co
 *
 */
public class LDPathResult {

    private Map<String,List<RDFNode>> result;

    public LDPathResult() {
        this.result = new HashMap<String, List<RDFNode>>();
    }

    public LDPathResult(Map<String,List<RDFNode>> result) {
        this.result = new HashMap<String,List<RDFNode>>(result);
    }

    public boolean add(String field, List<RDFNode> result) {
        this.result.put(field, result);
        return true;
    }

    public Set<String> getFields() {
        return result.keySet();
    }

    public List<RDFNode> getResults(String field) {
        return result.get(field);
    }

    public int size() {
        return result.keySet().size();
    }

}
