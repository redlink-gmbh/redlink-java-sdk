package io.redlink.sdk.impl;

import org.codehaus.jackson.JsonNode;

/**
 * 
 * TODO: documentation
 */

public class Status {

    private boolean accessible;
    private int bytes;
    private int requests;
    private int limit;
    private int seconds;

    public Status() {

    }

    public boolean isAccessible() {
        return accessible;
    }

    public void setAccessible(boolean accessible) {
        this.accessible = accessible;
    }

    public int getBytes() {
        return bytes;
    }

    public int getRequests() {
        return requests;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(JsonNode node) {
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

    public int getSeconds() {
        return seconds;
    }

}
