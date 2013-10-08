package io.redlink.sdk.impl.content.model;

/**
 * Content Item
 *
 * @author sergio.fernandez@redlink.co
 */
public class ContentItem {

    private String id;

    private String uri;
    
    private String status;

    private String message;
    
    public ContentItem() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
    
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
}
