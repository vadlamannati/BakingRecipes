package com.example.bharadwaj.bakingrecipes.widget;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.bharadwaj.bakingrecipes.R;
import com.example.bharadwaj.bakingrecipes.RecipeStepsActivity;
import com.example.bharadwaj.bakingrecipes.constants.Constants;
import com.example.bharadwaj.bakingrecipes.model.Ingredient;

import org.parceler.Parcels;

import java.util.List;

/**
 * Created by Bharadwaj on 4/8/18.
 */

public class RecipeWidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private List<Ingredient> mIngredients;
    private static final String LOG_TAG = RecipeWidgetRemoteViewsFactory.class.getSimpleName();

    public RecipeWidgetRemoteViewsFactory(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        Log.v(LOG_TAG, "Replacing with new Data Set");
        mIngredients = RecipeIngredientsWidgetProvider.recipe.getIngredients();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return mIngredients != null ? mIngredients.size() : 0;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        Log.v(LOG_TAG, "Entering getViewAt");

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_ingredient_list_item);
        views.setTextViewText(R.id.widget_list_item_text_view, mIngredients.get(position).toString());

        Intent recipeDetailsIntent = new Intent(mContext,RecipeStepsActivity.class);
        recipeDetailsIntent.putExtra(Constants.RECIPE, Parcels.wrap(RecipeIngredientsWidgetProvider.recipe));
        views.setOnClickFillInIntent(R.id.widget_list_item_text_view, recipeDetailsIntent);

        Log.v(LOG_TAG, "Leaving getViewAt");
        Log.v(LOG_TAG, "Returning a new RemoteViews instance");
        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
