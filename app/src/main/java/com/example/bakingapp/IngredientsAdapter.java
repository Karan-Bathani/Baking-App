package com.example.bakingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.ViewHolder> {
    private String tempIngredients;
    private List<Ingredient> ingredients;

    public IngredientsAdapter(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.ingredients_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ingredient ingredient = ingredients.get(position);
        tempIngredients = "=> " + ingredient.getIngredient() + "(" +ingredient.getQuantity()+" "+ingredient.getMeasure()+")";
        holder.tvIngredients.setText(tempIngredients);

    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvIngredients;

        public ViewHolder(View itemView) {
            super(itemView);

            tvIngredients = itemView.findViewById(R.id.tvIngredients);
        }
    }
}
