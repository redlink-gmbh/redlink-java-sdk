package io.redlink.sdk.impl;

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

    public int getSeconds() {
        return seconds;
    }

}
