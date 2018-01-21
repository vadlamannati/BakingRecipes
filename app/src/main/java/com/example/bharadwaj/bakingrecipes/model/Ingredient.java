
package com.example.bharadwaj.bakingrecipes.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class Ingredient {

    @SerializedName("quantity")
    @Expose
    float quantity;
    @SerializedName("measure")
    @Expose
    String measure;
    @SerializedName("ingredient")
    @Expose
    String ingredient;

    public float getQuantity() {
        return quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public String getIngredient() {
        return ingredient;
    }

}
