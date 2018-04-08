package com.example.bharadwaj.bakingrecipes;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.bharadwaj.bakingrecipes.adapters.RecipeAdapter;
import com.example.bharadwaj.bakingrecipes.constants.Constants;
import com.example.bharadwaj.bakingrecipes.model.Recipe;
import com.example.bharadwaj.bakingrecipes.network.NetworkUtils;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private RecipeAdapter mRecipeAdapter;
    private GridLayoutManager mGridLayoutManager;

    @BindView(R.id.main_activity_toolbar) Toolbar toolbar;
    @BindView(R.id.recipe_list) RecyclerView recipeRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, "Entering MainActivity");

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        try {
            loadRecipes();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mRecipeAdapter = new RecipeAdapter(this);
        if(getResources().getString(R.string.configurationDetector).equals(Constants.IN_PORTRAIT_MODE)){
            Log.v(LOG_TAG, "Testing portrait detector");
            mGridLayoutManager = new GridLayoutManager(MainActivity.this, 1);
        }else if(getResources().getString(R.string.configurationDetector).equals(Constants.IN_LANDSCAPE_MODE)){
            Log.v(LOG_TAG, "Testing landscape detector");
            mGridLayoutManager = new GridLayoutManager(MainActivity.this, 2);
        }else if (getResources().getString(R.string.configurationDetector).equals(Constants.IN_TABLET_MODE)){
            Log.v(LOG_TAG, "Testing Tablet detector");
            mGridLayoutManager = new GridLayoutManager(MainActivity.this, 3);
        }

        recipeRecyclerView.setAdapter(mRecipeAdapter);
        recipeRecyclerView.setLayoutManager(mGridLayoutManager);

        Log.v(LOG_TAG, "RecyclerView Adapter check : " + recipeRecyclerView.getAdapter());

        Log.v(LOG_TAG, "Leaving MainActivity");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void loadRecipes() throws IOException {
        Call<List<Recipe>> recipesList = NetworkUtils.getRecipes();

        //Using enqueue because data is supposed to be loaded on another thread (asynchronously)
        recipesList.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                List<Recipe> recipes = response.body();
                Log.v(LOG_TAG, "Number of Recipes : " + recipes.size());
                if (recipes != null) {
                    mRecipeAdapter.fillRecipeData(recipes);
                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable throwable) {
                Log.v(LOG_TAG, "In onFailure : " + throwable.getMessage(), throwable );
            }
        });
    }

}
