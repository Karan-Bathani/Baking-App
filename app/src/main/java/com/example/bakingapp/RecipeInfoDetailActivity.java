package com.example.bakingapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.View;


import java.util.List;

public class RecipeInfoDetailActivity extends AppCompatActivity implements RecipeInfoDetailFragment.OnButtonClickListener {

    // Fragment key to restore onSaveInstanceState
    private static final String DETAIL_FRAGMENT_KEY = "detail_fragment";
    private Fragment detailFragment;
    private Step currentStep, tempStep;
    private Recipe recipe;
    private List<Step> steps;
    private FragmentManager fragmentManager;
    private String recipeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_info_detail);

        if (savedInstanceState == null) {
            detailFragment = new RecipeInfoDetailFragment();
        } else {
            detailFragment = getSupportFragmentManager().getFragment(savedInstanceState, DETAIL_FRAGMENT_KEY);
        }

        currentStep = getIntent().getParcelableExtra(AppUtils.EXTRAS_STEP);
        recipe = getIntent().getParcelableExtra(AppUtils.EXTRAS_RECIPE);
        steps = recipe.getSteps();
        recipeName = getIntent().getStringExtra(AppUtils.EXTRAS_RECIPE_NAME);

        Bundle bundle = new Bundle();
        bundle.putParcelable(AppUtils.EXTRAS_STEP, currentStep);
        bundle.putInt("list_size", recipe.getSteps().size());
        bundle.putString("activity", "RecipeInfoDetailActivity");
        detailFragment.setArguments(bundle);

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.recipeInfoDetailContainer, detailFragment)
                .commit();

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            if (currentStep.getId() > 0)
                ab.setTitle(getString(R.string.step) + " " + (currentStep.getId()));
            else
                ab.setTitle(getString(R.string.recipe_introduction));
            ab.setSubtitle(recipeName);
        }


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, DETAIL_FRAGMENT_KEY, detailFragment);
    }

    @Override
    public void onButtonClicked(int stepId) {
        tempStep = steps.get(stepId);
        Bundle bundle = new Bundle();
        bundle.putParcelable(AppUtils.EXTRAS_STEP, tempStep);
        bundle.putInt("list_size", recipe.getSteps().size());
        bundle.putString("activity", "RecipeInfoDetailActivity");
        detailFragment = new RecipeInfoDetailFragment();
        detailFragment.setArguments(bundle);

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.recipeInfoDetailContainer, detailFragment)
                .commit();

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            if (tempStep.getId() > 0)
                ab.setTitle(getString(R.string.step) + " " + (tempStep.getId()));
            else
                ab.setTitle(getString(R.string.recipe_introduction));
            ab.setSubtitle(recipeName);
        }
    }
}
