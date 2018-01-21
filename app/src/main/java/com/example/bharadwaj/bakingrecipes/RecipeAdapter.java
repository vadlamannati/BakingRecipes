package com.example.bharadwaj.bakingrecipes;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bharadwaj.bakingrecipes.model.Recipe;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Bharadwaj on 1/18/18.
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private static final String LOG_TAG = RecipeAdapter.class.getSimpleName();
    List<Recipe> recipes;

    public RecipeAdapter() {

    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.recipe_list_item, parent, false);

        Log.v(LOG_TAG, "Returning new ViewHolder object");
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder recipeViewHolder, int position) {
        Recipe recipe = recipes.get(position);

        //Log.v(LOG_TAG, "Recipe : " + recipe.toString());
        recipeViewHolder.recipeNameView.setText(recipe.getName());
        recipeViewHolder.recipeServingsValueView.setText(String.valueOf(recipe.getServings()));

        if( recipe.getImage() != null && recipe.getImage() != "" ){
            Picasso.with(recipeViewHolder.recipePreviewView.getContext())
                    .load(recipe.getImage())
                    .placeholder(R.mipmap.recipe_preview_view_holder)
                    .error(R.mipmap.recipe_preview_view_error)
                    .fit()
                    .into(recipeViewHolder.recipePreviewView);
        }else {
            //recipeViewHolder.recipePreviewView.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        if (recipes == null) return 0;
        return recipes.size();
    }

    public void fillRecipeData(List<Recipe> recipes) {
        Log.v(LOG_TAG, "Filling Recipes into Adapter...");
        this.recipes = recipes;
        notifyDataSetChanged();
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.recipe_name_view)
        TextView recipeNameView;

        @BindView(R.id.recipe_preview_view)
        ImageView recipePreviewView;

        @BindView(R.id.recipe_servings_value_view)
        TextView recipeServingsValueView;

        public RecipeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
