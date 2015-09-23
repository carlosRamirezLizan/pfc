package com.carlos.ramirez.android.service.pfc.model;

/**
 * Created by carlos on 22/9/15.
 */
public class PublishOptions {

    public int id;
    public String title;
    public String description;

    public PublishOptions(int id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

