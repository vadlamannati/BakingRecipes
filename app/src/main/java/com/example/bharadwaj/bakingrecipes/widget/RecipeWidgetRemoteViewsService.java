package com.example.bharadwaj.bakingrecipes.widget;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

/**
 * Created by Bharadwaj on 4/8/18.
 */

public class RecipeWidgetRemoteViewsService extends RemoteViewsService {
    private static final String LOG_TAG = RecipeWidgetRemoteViewsService.class.getSimpleName();

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.v(LOG_TAG, "Returning a new RecipeWidgetRemoteViewsFactory instance");
        return new RecipeWidgetRemoteViewsFactory(getApplicationContext());
    }
}
