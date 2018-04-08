package com.example.bharadwaj.bakingrecipes.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.bharadwaj.bakingrecipes.R;
import com.example.bharadwaj.bakingrecipes.RecipeStepsActivity;
import com.example.bharadwaj.bakingrecipes.constants.Constants;
import com.example.bharadwaj.bakingrecipes.model.Recipe;

import org.parceler.Parcels;

/**
 * Implementation of App Widget functionality.
 */

public class RecipeIngredientsWidgetProvider extends AppWidgetProvider {

    private static final String LOG_TAG = RecipeIngredientsWidgetProvider.class.getSimpleName();
    protected static Recipe recipe;
    protected static Recipe initialRecipe;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        Log.v(LOG_TAG, "Entering updateAppWidget");
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_ingredients_widget);

        Intent recipestepsIntent = new Intent(context, RecipeStepsActivity.class);
        recipestepsIntent.addCategory(Intent.ACTION_MAIN);
        recipestepsIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        recipestepsIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, recipestepsIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.recipe_widget_list_view, pendingIntent);

        // Set the RecipeWidgetRemoteViewsService intent to act as the adapter for the GridView
        Intent intent = new Intent(context, RecipeWidgetRemoteViewsService.class);
        views.setRemoteAdapter(R.id.recipe_widget_list_view, intent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
        Log.v(LOG_TAG, "Leaving updateAppWidget");

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.v(LOG_TAG, "Entering onUpdate");

        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        Log.v(LOG_TAG, "Leaving onUpdate");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(LOG_TAG, "Entering onReceive");

        //Receiving the intent Broadcast Sent through IntentService
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, RecipeIngredientsWidgetProvider.class));
        final String action = intent.getAction();
        Log.v(LOG_TAG, "Action : " + action);

        if (action != null && action.equals(Constants.UPDATE_ACTION)) {
            recipe = Parcels.unwrap(intent.getExtras().getParcelable(Constants.RECIPE));
            if(recipe == null)  recipe = initialRecipe;
            Log.v(LOG_TAG, "Recipe in onReceive : " + recipe.getName());

            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.recipe_widget_list_view);

            //Now Updating all widgets
            onUpdate(context, appWidgetManager, appWidgetIds);
            super.onReceive(context, intent);
        }
        Log.v(LOG_TAG, "Leaving onReceive");

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    public static void setInitialRecipe(Recipe recipe) {
        initialRecipe = recipe;
    }
}

