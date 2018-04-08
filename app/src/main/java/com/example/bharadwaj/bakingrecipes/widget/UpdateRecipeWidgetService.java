package com.example.bharadwaj.bakingrecipes.widget;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.bharadwaj.bakingrecipes.constants.Constants;
import com.example.bharadwaj.bakingrecipes.model.Recipe;

import org.parceler.Parcels;

/**
 * Created by Bharadwaj on 4/8/18.
 */

public class UpdateRecipeWidgetService extends IntentService {

    private static final String LOG_TAG = UpdateRecipeWidgetService.class.getSimpleName();

    public UpdateRecipeWidgetService() {
        super("UpdateRecipeWidgetService");
    }

    public static void startRecipeIntentService(Context context, Recipe recipe) {
        Log.v(LOG_TAG, "Entering startRecipeIntentService");
        Intent intent = new Intent(context, UpdateRecipeWidgetService.class);

        Log.v(LOG_TAG, "Recipe in startRecipeIntentService : " + recipe.getName());

        intent.putExtra(Constants.RECIPE, Parcels.wrap(recipe));
        context.startService(intent);
        Log.v(LOG_TAG, "Leaving startRecipeIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.v(LOG_TAG, "Entering onHandleIntent");
        if (intent != null) {
            Recipe recipe = Parcels.unwrap(intent.getExtras().getParcelable(Constants.RECIPE));
            Intent newIntent = new Intent(Constants.UPDATE_ACTION);
            Log.v(LOG_TAG, "Recipe in onHandleIntent : " + recipe.getName());
            newIntent.setAction(Constants.UPDATE_ACTION);
            newIntent.putExtra(Constants.RECIPE, Parcels.wrap(recipe));

            //Broadcasting the given intent to interested BroadcastReceivers (AppWidgetProvider in this project).
            sendBroadcast(newIntent);
        }
        Log.v(LOG_TAG, "Leaving onHandleIntent");
    }
}
