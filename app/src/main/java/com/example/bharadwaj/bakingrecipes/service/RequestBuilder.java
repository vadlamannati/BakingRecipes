package com.example.bharadwaj.bakingrecipes.service;

import com.example.bharadwaj.bakingrecipes.model.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Bharadwaj on 1/20/18.
 */

public interface RequestBuilder {

    @GET("/topher/2017/May/59121517_baking/baking.json")
    Call<List<Recipe>> getRecipes();

}
