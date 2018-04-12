package com.example.bharadwaj.bakingrecipes;

import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.intent.matcher.IntentMatchers;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.bharadwaj.bakingrecipes.constants.Constants;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;

/**
 * Created by Bharadwaj on 4/11/18.
 */

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mainActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Test
    public void clickOnRecipeOpensRecipeStepsActivityTest() {
        onView(ViewMatchers.withId(R.id.recipe_list_item)).perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));
        Intents.intended(IntentMatchers.hasExtraWithKey(Constants.RECIPE));
    }

}
