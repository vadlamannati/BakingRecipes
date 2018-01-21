package com.example.bharadwaj.bakingrecipes.service;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Bharadwaj on 1/20/18.
 */

public class ServiceCreator {

    private static final OkHttpClient.Builder sOkHttpClient = new OkHttpClient.Builder();
    public static final int CONNECTION_TIMEOUT = 30000;
    public static final int READ_TIMEOUT = 30000;

    //Preventing instantiation.
    private ServiceCreator() {
    }

    public static <C> C createService(Class<C> service, String baseUrl){

        sOkHttpClient.connectTimeout(CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(sOkHttpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        //Log.v(LOG_TAG, "");
        return retrofit.create(service);

    }

}
