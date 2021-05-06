package com.example.bakingapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bakingapp.databinding.MainRecipeCardItemBinding;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class MainRecipeAdapter extends RecyclerView.Adapter<MainRecipeAdapter.ViewHolder> {

    final private RecipeItemClickListener mOnClickListener;
    private List<Recipe> recipes;
    private String tempImage;

    public MainRecipeAdapter(List<Recipe> recipes, RecipeItemClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
        this.recipes = recipes;
    }

    public Recipe getRecipeAtIndex(int index) {
        return recipes.get(index);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        MainRecipeCardItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.main_recipe_card_item, parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.tvRecipeName.setText(recipes.get(position).getName());
        holder.binding.tvServings.setText(String.format("Servings: %d", recipes.get(position).getServings()));
        switch ((recipes.get(position).getName())) {
            case "Nutella Pie":
                tempImage = "https://images-gmi-pmc.edge-generalmills.com/0d8ad1b2-0411-4b37-8cdf-9b7d0334d42a.jpg";
                break;
            case "Brownies":
                tempImage = "https://img.buzzfeed.com/thumbnailer-prod-us-east-1/video-api/assets/166813.jpg";
                break;
            case "Yellow Cake":
                tempImage = "https://i.ytimg.com/vi/gIpKJqpzTXY/maxresdefault.jpg";
                break;
            case "Cheesecake":
                tempImage = "https://www.simplyrecipes.com/wp-content/uploads/2014/12/perfect-cheesecake-horiz-a-1200.jpg";
                break;
            default:
                tempImage = "";
        }
        holder.setImage(tempImage);
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public interface RecipeItemClickListener {
        void onRecipeItemClick(int clickedItemIndex);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        MainRecipeCardItemBinding binding;
        private Context context;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            context = itemView.getContext();

            itemView.setOnClickListener(this);
        }

        void setImage(String image) {
            if (image.trim().equals("")) {
                binding.ivRecipeImage.setImageResource(R.drawable.error);
            } else {
                Picasso.get()
                        .load(image)
                        .error(R.drawable.error)
                        .placeholder(R.drawable.loading)
                        .into(binding.ivRecipeImage);
            }
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onRecipeItemClick(clickedPosition);
        }
    }

}
