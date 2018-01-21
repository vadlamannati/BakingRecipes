package com.example.bharadwaj.bakingrecipes.network;

import com.example.bharadwaj.bakingrecipes.constants.Constants;
import com.example.bharadwaj.bakingrecipes.model.Recipe;
import com.example.bharadwaj.bakingrecipes.service.RequestBuilder;
import com.example.bharadwaj.bakingrecipes.service.ServiceCreator;

import java.util.List;

import retrofit2.Call;

/**
 * Created by Bharadwaj on 1/20/18.
 */

public class NetworkUtils {

    public static Call<List<Recipe>> getRecipes(){
        return ServiceCreator.createService(RequestBuilder.class, Constants.BASE_URL).getRecipes();
    }

}
