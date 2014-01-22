package io.redlink.sdk.impl;


/**
 * RedLink's user Application Status data 
 * 
 * @author rafa.haro@redlink.co
 *
 */
public class Status {

    private boolean accessible;
    private int bytes;
    private int requests;
    private int limit;
    private int seconds;

    public Status() {

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
//
//    public void setLimit(JsonNode node) {
//        final String str = node.toString();
//        try {
//            limit = Integer.parseInt(str);
//        } catch (Exception e) {
//            if ("unlimited".equals(str)) {
//                limit = -1;
//            } else {
//                limit = 0;
//            }
//        }
//    }

    /**
     * Returns the number of seconds that the application has been active
     * 
     * @return
     */
    public int getSeconds() {
        return seconds;
    }

}
