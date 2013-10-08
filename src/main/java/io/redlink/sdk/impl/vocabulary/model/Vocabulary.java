package io.redlink.sdk.impl.vocabulary.model;


public class Vocabulary {

    private String id;

    private String name;

    private String description;

    private String profile;

    private Boolean enabled = true;

    private Boolean editable = true;

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean active) {
        enabled = active;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public Boolean isEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }

}
