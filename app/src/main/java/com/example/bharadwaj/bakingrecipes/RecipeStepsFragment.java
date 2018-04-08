package com.example.bharadwaj.bakingrecipes;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.bharadwaj.bakingrecipes.adapters.RecipeStepAdapter;
import com.example.bharadwaj.bakingrecipes.constants.Constants;
import com.example.bharadwaj.bakingrecipes.model.Recipe;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnRecipeStepsFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RecipeStepsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipeStepsFragment extends Fragment {

    protected static final String LOG_TAG = RecipeStepsFragment.class.getSimpleName();

    static final int fragmentIdentifier = 100;

    private GridLayoutManager mGridLayoutManager;
    private Unbinder mUnbinder;
    private RecipeStepAdapter mRecipeStepAdapter;
    private OnRecipeStepsFragmentInteractionListener mListener;
    private Recipe mCurrentRecipe;

    @BindView(R.id.recipe_ingredients_view)
    EditText recipeIngredientsView;
    @BindView(R.id.recipe_steps_list)
    RecyclerView recipe_steps_recycler_view;

    public RecipeStepsFragment() {
        // Required empty public constructor
        Log.v(LOG_TAG, "RecipeStepsFragment constructor called");
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecipeStepsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecipeStepsFragment newInstance(String param1, String param2) {
        RecipeStepsFragment fragment = new RecipeStepsFragment();
        /*Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);*/
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, "Entering onCreate");

        setRetainInstance(true);

        if (getArguments() != null) {
            Log.v(LOG_TAG, " savedInstanceState object : " + savedInstanceState);
            mCurrentRecipe = Parcels.unwrap(getArguments().getParcelable(Constants.RECIPE));
        }
        Log.v(LOG_TAG, "Leaving onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(LOG_TAG, "Entering onCreateView");
        Log.v(LOG_TAG, "Viewgroup is : " + container.toString());

        View rootView = inflater.inflate(R.layout.fragment_recipe_steps, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);

        setRecipeDetails(mCurrentRecipe);

        Log.v(LOG_TAG, "Leaving onCreateView");

        // Inflate the layout for this fragment
        return rootView;
    }

    private void setRecipeDetails(Recipe currentRecipe) {

        Log.v(LOG_TAG, "Current Recipe is : " + currentRecipe.getName());

        recipeIngredientsView.setText(currentRecipe.getIngredientsAsList());

        mRecipeStepAdapter = new RecipeStepAdapter(getContext(), mListener, currentRecipe);
        mRecipeStepAdapter.fillStepData(currentRecipe.getSteps());
        mGridLayoutManager = new GridLayoutManager(getContext(), 1);

        recipe_steps_recycler_view.setAdapter(mRecipeStepAdapter);
        recipe_steps_recycler_view.setLayoutManager(mGridLayoutManager);

        Log.v(LOG_TAG, "RecyclerView Adapter check : " + recipe_steps_recycler_view.getAdapter());

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.v(LOG_TAG, "Entering onAttach");

        if (context instanceof OnRecipeStepsFragmentInteractionListener) {
            mListener = (OnRecipeStepsFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRecipeStepsFragmentInteractionListener");
        }
        Log.v(LOG_TAG, "Leaving onAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.v(LOG_TAG, "Entering onDetach");
        mListener = null;
        mUnbinder.unbind();
        Log.v(LOG_TAG, "Leaving onDetach");
    }

    public interface OnRecipeStepsFragmentInteractionListener {
        void onRecipeStepsFragmentInteraction(Bundle bundle);
    }
}
