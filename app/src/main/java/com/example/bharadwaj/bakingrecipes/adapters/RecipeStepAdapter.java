package com.example.bharadwaj.bakingrecipes.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.bharadwaj.bakingrecipes.R;
import com.example.bharadwaj.bakingrecipes.RecipeStepsFragment.OnRecipeStepsFragmentInteractionListener;
import com.example.bharadwaj.bakingrecipes.constants.Constants;
import com.example.bharadwaj.bakingrecipes.model.Recipe;
import com.example.bharadwaj.bakingrecipes.model.Step;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Bharadwaj on 1/18/18.
 */

public class RecipeStepAdapter extends RecyclerView.Adapter<RecipeStepAdapter.StepViewHolder>  {

    private static final String LOG_TAG = RecipeStepAdapter.class.getSimpleName();
    private Context mContext;
    private OnRecipeStepsFragmentInteractionListener mListener;

    Recipe currentRecipe;
    List<Step> mSteps;

    public RecipeStepAdapter(Context context, OnRecipeStepsFragmentInteractionListener listener,  Recipe currentRecipe) {
        mContext = context;
        mListener = listener;
        this.currentRecipe = currentRecipe;
    }

    @Override
    public StepViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.step_list_item, parent, false);

        //Log.v(LOG_TAG, "Returning new ViewHolder object for step");
        return new StepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StepViewHolder stepViewHolder, int position) {
        Step step = mSteps.get(position);
        //Log.v(LOG_TAG, "Step : " + step.toString());
        stepViewHolder.recipeStepShortDescriptionView.setText(step.getShortDescription());

    }

    @Override
    public int getItemCount() {
        if (mSteps == null) return 0;
        return mSteps.size();
    }

    public void fillStepData(List<Step> steps) {
        Log.v(LOG_TAG, "Filling Steps into Adapter...");
        this.mSteps = steps;
        notifyDataSetChanged();
    }

    public class StepViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.recipe_step)
        Button recipeStepShortDescriptionView;

        public StepViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.recipe_step)
        public void startRecipeDetailsIntent(){
            Log.v(LOG_TAG, "Starting Intent to show the step details");

            Bundle bundle = new Bundle();
            bundle.putParcelable(Constants.RECIPE, Parcels.wrap(currentRecipe));
            bundle.putInt(Constants.STEP_ID, mSteps.get(getAdapterPosition()).getId());
            mListener.onRecipeStepsFragmentInteraction(bundle);
        }
    }
}
