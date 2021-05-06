package com.example.bakingapp;


import android.content.Context;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bakingapp.databinding.FragmentRecipeInfoBinding;

import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecipeInfoFragment extends Fragment {

    FragmentRecipeInfoBinding binding;
    OnStepClickListener mCallback;
    private Recipe recipe;

    public RecipeInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallback = (OnStepClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnStepClickListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipe_info, container, false);
        final View rootView = binding.getRoot();

        if (recipe == null) {
            recipe = ((RecipeInfoActivity) (Objects.requireNonNull(getActivity()))).getRecipe();
        }

        LinearLayoutManager ingredientsLayoutManager = new LinearLayoutManager(getContext());
        binding.rvIngredients.setLayoutManager(ingredientsLayoutManager);
        binding.rvIngredients.setHasFixedSize(true);
        binding.rvIngredients.setAdapter(new IngredientsAdapter(recipe.getIngredients()));


        LinearLayoutManager stepsLayoutManager = new LinearLayoutManager(getContext());
        binding.rvSteps.setLayoutManager(stepsLayoutManager);
        binding.rvSteps.setHasFixedSize(true);
        binding.rvSteps.setAdapter(new StepsAdapter(recipe.getSteps(), new StepsAdapter.StepItemClickListener() {
            @Override
            public void onStepItemClick(int clickedItemIndex) {
                mCallback.onStepSelected(clickedItemIndex);
            }
        }));

        return rootView;
    }

    public interface OnStepClickListener {
        void onStepSelected(int position);
    }

}
