package com.example.bharadwaj.bakingrecipes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.example.bharadwaj.bakingrecipes.constants.Constants;
import com.example.bharadwaj.bakingrecipes.model.Recipe;
import com.example.bharadwaj.bakingrecipes.widget.UpdateRecipeWidgetService;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeStepsActivity extends AppCompatActivity implements RecipeStepsFragment.OnRecipeStepsFragmentInteractionListener {

    private static final String LOG_TAG = RecipeStepsActivity.class.getSimpleName();
    Fragment retainedRecipeStepsFragment;
    Fragment retainedStepDetailsFragment;
    @BindView(R.id.steps_activity_toolbar)
    Toolbar toolbar;
    private RecipeStepsFragment mRecipeStepsFragment;
    private StepDetailsFragment mStepDetailsFragment;
    private boolean reconfigureAppBarBackButton = false;
    private Recipe mCurrentRecipe;
    private int fragmentIdentifier = RecipeStepsFragment.fragmentIdentifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, "Entering RecipeStepsActivity");

        setContentView(R.layout.activity_recipe_steps);
        ButterKnife.bind(this);

        Log.v(LOG_TAG, "In layout mode : " + getResources().getString(R.string.configurationDetector));

        if(savedInstanceState != null){
            Log.v(LOG_TAG, " savedInstanceState object : " + savedInstanceState.getInt(Constants.FRAGMENT_IDENTIFIER));
            fragmentIdentifier = savedInstanceState.getInt(Constants.FRAGMENT_IDENTIFIER);
            mCurrentRecipe = Parcels.unwrap(savedInstanceState.getParcelable(Constants.RECIPE));
        }
        else {
            Log.v(LOG_TAG, " savedInstanceState object : " + savedInstanceState);
            Intent recipeStepsIntent = getIntent();
            if(recipeStepsIntent != null){
                mCurrentRecipe = Parcels.unwrap(recipeStepsIntent.getParcelableExtra(Constants.RECIPE));
            }
        }
        if (mCurrentRecipe != null)
            toolbar.setTitle(Constants.RECIPE_STEPS_TITLE + mCurrentRecipe.getName());

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        retainedRecipeStepsFragment = getSupportFragmentManager().findFragmentByTag(String.valueOf(RecipeStepsFragment.fragmentIdentifier));
        retainedStepDetailsFragment = getSupportFragmentManager().findFragmentByTag(String.valueOf(StepDetailsFragment.fragmentIdentifier));

        if(getResources().getString(R.string.configurationDetector).equals(Constants.IN_PORTRAIT_MODE)
                || getResources().getString(R.string.configurationDetector).equals(Constants.IN_LANDSCAPE_MODE) ){

            switch (fragmentIdentifier) {
                case RecipeStepsFragment.fragmentIdentifier:
                    fragmentIdentifier = RecipeStepsFragment.fragmentIdentifier;

                    if(retainedRecipeStepsFragment != null){
                        Log.v(LOG_TAG, "Replacing fragment holder with retained RecipeStepsFragment");
                        getSupportFragmentManager().beginTransaction().replace(R.id.recipe_steps_placeholder_fragment, retainedRecipeStepsFragment, String.valueOf(RecipeStepsFragment.fragmentIdentifier)).commit();
                    }else{
                        Log.v(LOG_TAG, "Replacing fragment holder with new RecipeStepsFragment");
                        mRecipeStepsFragment = new RecipeStepsFragment();
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(Constants.RECIPE, Parcels.wrap(mCurrentRecipe));
                        mRecipeStepsFragment.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction().add(R.id.recipe_steps_placeholder_fragment, mRecipeStepsFragment, String.valueOf(RecipeStepsFragment.fragmentIdentifier)).commit();
                    }
                    break;

                case StepDetailsFragment.fragmentIdentifier:
                    fragmentIdentifier = StepDetailsFragment.fragmentIdentifier;
                    reconfigureAppBarBackButton = true;

                    if(retainedStepDetailsFragment != null){
                        Log.v(LOG_TAG, "Replacing fragment holder with retained StepDetailsFragment");
                        getSupportFragmentManager().beginTransaction().replace(R.id.recipe_steps_placeholder_fragment, retainedStepDetailsFragment, String.valueOf(StepDetailsFragment.fragmentIdentifier)).commit();
                    }else{
                        Log.v(LOG_TAG, "Replacing fragment holder with new StepDetailsFragment");
                        mStepDetailsFragment = new StepDetailsFragment();
                        getSupportFragmentManager().beginTransaction().add(R.id.recipe_steps_placeholder_fragment, mStepDetailsFragment, String.valueOf(StepDetailsFragment.fragmentIdentifier)).commit();
                    }
                    break;
                default:
                    Log.v(LOG_TAG,"Incorrect Fragment Identifier : " + fragmentIdentifier);
            }
        }else if(getResources().getString(R.string.configurationDetector).equals(Constants.IN_TABLET_MODE)){

            Log.v(LOG_TAG, "In Tablet mode ");
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            if(retainedStepDetailsFragment !=null && retainedStepDetailsFragment != null){
                fragmentTransaction.replace(R.id.recipe_steps_placeholder_fragment, retainedRecipeStepsFragment, String.valueOf(RecipeStepsFragment.fragmentIdentifier));
                fragmentTransaction.replace(R.id.steps_details_placeholder_fragment, retainedStepDetailsFragment, String.valueOf(StepDetailsFragment.fragmentIdentifier));
                Log.v(LOG_TAG, "Replacing fragment holders with retained RecipeStepsFragment and StepDetailsFragment");
            }else {
                mRecipeStepsFragment = new RecipeStepsFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable(Constants.RECIPE, Parcels.wrap(mCurrentRecipe));
                mRecipeStepsFragment.setArguments(bundle);
                fragmentTransaction.add(R.id.recipe_steps_placeholder_fragment, mRecipeStepsFragment, String.valueOf(RecipeStepsFragment.fragmentIdentifier));

                bundle.putInt(Constants.STEP_ID, 0);
                mStepDetailsFragment = new StepDetailsFragment();
                mStepDetailsFragment.setArguments(bundle);
                fragmentTransaction.add(R.id.steps_details_placeholder_fragment, mStepDetailsFragment, String.valueOf(StepDetailsFragment.fragmentIdentifier));

            }
            fragmentTransaction.commit();

        }

        UpdateRecipeWidgetService.startRecipeIntentService(this, mCurrentRecipe);

        Log.v(LOG_TAG, "Leaving RecipeStepsActivity");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.v(LOG_TAG, "Entering onOptionsItemSelected");
        switch (item.getItemId()) {
            case android.R.id.home:
                if (reconfigureAppBarBackButton && !getResources().getString(R.string.configurationDetector).equals(Constants.IN_TABLET_MODE)) {
                    reconfigureAppBarBackButton = false;
                    Intent intentToGoBackToRecipesSteps = new Intent(getApplicationContext(), RecipeStepsActivity.class);
                    intentToGoBackToRecipesSteps.putExtra(Constants.RECIPE, Parcels.wrap(mCurrentRecipe));
                    startActivity(intentToGoBackToRecipesSteps);
                    return true;
                } else {
                    Intent intentToGoBackToRecipesHome = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intentToGoBackToRecipesHome);
                    return true;
                }
        }
        Log.v(LOG_TAG, "Leaving onOptionsItemSelected");
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRecipeStepsFragmentInteraction(Bundle bundle) {
        Log.v(LOG_TAG, "Entering onRecipeStepsFragmentInteraction");

        if (bundle != null) {

            mStepDetailsFragment = new StepDetailsFragment();
            mStepDetailsFragment.setArguments(bundle);

            fragmentIdentifier = StepDetailsFragment.fragmentIdentifier;
            if(getResources().getString(R.string.configurationDetector).equals(Constants.IN_TABLET_MODE)){
                getSupportFragmentManager().beginTransaction().replace(R.id.steps_details_placeholder_fragment, mStepDetailsFragment, String.valueOf(StepDetailsFragment.fragmentIdentifier)).commit();
            }else{
                getSupportFragmentManager().beginTransaction().replace(R.id.recipe_steps_placeholder_fragment, mStepDetailsFragment, String.valueOf(StepDetailsFragment.fragmentIdentifier)).commit();
            }

            //Reconfiguring back button on app bar to return to Recipe Steps menu on single pane mode
            reconfigureAppBarBackButton = true;
        }
        Log.v(LOG_TAG, "Leaving onRecipeStepsFragmentInteraction");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.v(LOG_TAG, "Entering onSaveInstanceState");
        outState.putParcelable(Constants.RECIPE, Parcels.wrap(mCurrentRecipe));
        outState.putInt(Constants.FRAGMENT_IDENTIFIER, fragmentIdentifier);
        Log.v(LOG_TAG, "Leaving onSaveInstanceState");
    }
}
