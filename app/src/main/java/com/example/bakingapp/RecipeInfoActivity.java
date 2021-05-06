package com.example.bakingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;


public class RecipeInfoActivity extends AppCompatActivity implements RecipeInfoFragment.OnStepClickListener, RecipeInfoDetailFragment.OnButtonClickListener {

    private boolean mTwoPane;
    private SharedPreferences sharedPreferences;
    private Recipe recipe;
    private Step tempStep;
    private Fragment detailFragment;
    private FragmentManager fragmentManager;
    FrameLayout recipeDetailContainer;
    private List<Step> steps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey(AppUtils.EXTRAS_RECIPE)) {
                recipe = getIntent().getParcelableExtra(AppUtils.EXTRAS_RECIPE);
                steps = recipe.getSteps();
            }
        }

        setTitle(recipe.getName());

        setContentView(R.layout.activity_recipe_info);
        recipeDetailContainer = (FrameLayout) findViewById(R.id.recipeDetailContainer);
        mTwoPane = (findViewById(R.id.fragment_info_detail_container) != null);

    }

    public Recipe getRecipe() {
        return recipe;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }

        if (id == R.id.action_widget){
            boolean isRecipeInWidget = (sharedPreferences.getInt(AppUtils.PREFERENCES_ID, -1) == recipe.getId());

            // If recipe already in widget, remove it
            if (isRecipeInWidget){
                sharedPreferences.edit()
                        .remove(AppUtils.PREFERENCES_ID)
                        .remove(AppUtils.PREFERENCES_WIDGET_TITLE)
                        .remove(AppUtils.PREFERENCES_WIDGET_CONTENT)
                        .apply();

                item.setIcon(R.drawable.ic_star_border);
                Snackbar.make(recipeDetailContainer, R.string.widget_recipe_removed, Snackbar.LENGTH_SHORT).show();
            }
            // if recipe not in widget, then add it
            else{
                sharedPreferences
                        .edit()
                        .putInt(AppUtils.PREFERENCES_ID, recipe.getId())
                        .putString(AppUtils.PREFERENCES_WIDGET_TITLE, recipe.getName())
                        .putString(AppUtils.PREFERENCES_WIDGET_CONTENT, ingredientsString())
                        .apply();

                item.setIcon(R.drawable.ic_star_white);
                Snackbar.make(recipeDetailContainer, R.string.widget_recipe_added, Snackbar.LENGTH_SHORT).show();
            }

            // Change the Widget
            ComponentName provider = new ComponentName(this, BakingWidgetProvider.class);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            int[] ids = appWidgetManager.getAppWidgetIds(provider);
            BakingWidgetProvider bakingWidgetProvider = new BakingWidgetProvider();
            bakingWidgetProvider.onUpdate(this, appWidgetManager, ids);
        }

        return super.onOptionsItemSelected(item);
    }

    private String ingredientsString(){
        StringBuilder result = new StringBuilder();
        for (Ingredient ingredient :  recipe.getIngredients()){
            result.append(ingredient.getDoseStr()).append(" ").append(ingredient.getIngredient()).append("\n");
        }
        return result.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.recipe_menu, menu);

        sharedPreferences = getSharedPreferences(BuildConfig.APPLICATION_ID, MODE_PRIVATE);
        if ((sharedPreferences.getInt("ID", -1) == recipe.getId())){
            menu.findItem(R.id.action_widget).setIcon(R.drawable.ic_star_white);
        }
        return true;
    }

    @Override
    public void onStepSelected(int position) {
        Step step = recipe.getSteps().get(position);

        if (!step.getThumbnailURL().isEmpty()) {
            String mimeType = AppUtils.getMimeType(this, Uri.parse(step.getThumbnailURL()));
            if (mimeType.startsWith(AppUtils.MIME_VIDEO)) {
                step.swapVideoWithThumb();
            }
        }
        if (!step.getVideoURL().isEmpty()) {
            String mimeType = AppUtils.getMimeType(this, Uri.parse(step.getVideoURL()));
            if (mimeType.startsWith(AppUtils.MIME_IMAGE)) {
                step.swapVideoWithThumb();
            }
        }

        if (mTwoPane) {
            RecipeInfoDetailFragment detailFragment = new RecipeInfoDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable("step", step);
            bundle.putInt("list_size", recipe.getSteps().size());
            bundle.putString("activity", "RecipeInfoActivity");
            detailFragment.setArguments(bundle);

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_info_detail_container, detailFragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, RecipeInfoDetailActivity.class);
            intent.putExtra(AppUtils.EXTRAS_STEP, step);
            intent.putExtra(AppUtils.EXTRAS_RECIPE, recipe);
            intent.putExtra(AppUtils.EXTRAS_RECIPE_NAME, recipe.getName());
            startActivity(intent);
        }
    }

    @Override
    public void onButtonClicked(int stepId) {
        tempStep = steps.get(stepId);
        Bundle bundle = new Bundle();
        bundle.putParcelable(AppUtils.EXTRAS_STEP, tempStep);
        bundle.putInt("list_size", recipe.getSteps().size());
        detailFragment = new RecipeInfoDetailFragment();
        detailFragment.setArguments(bundle);

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.recipeInfoDetailContainer, detailFragment)
                .commit();
    }
}
