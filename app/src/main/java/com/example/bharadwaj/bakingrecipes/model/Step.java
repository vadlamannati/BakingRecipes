
package com.example.bharadwaj.bakingrecipes.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class Step {

    @SerializedName("id")
    @Expose
    int id;
    @SerializedName("shortDescription")
    @Expose
    String shortDescription;
    @SerializedName("description")
    @Expose
    String description;
    @SerializedName("videoURL")
    @Expose
    String videoURL;
    @SerializedName("thumbnailURL")
    @Expose
    String thumbnailURL;

    public Step() {
    }

    public Step(int id, String shortDescription, String description, String videoURL, String thumbnailURL) {
        this.id = id;
        this.shortDescription = shortDescription;
        this.description = description;
        this.videoURL = videoURL;
        this.thumbnailURL = thumbnailURL;
    }

    public int getId() {
        return id;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

}
