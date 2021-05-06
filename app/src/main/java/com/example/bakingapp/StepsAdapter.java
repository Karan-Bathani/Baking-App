package com.example.bakingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bakingapp.databinding.StepItemBinding;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.ViewHolder> {

    final private StepItemClickListener mOnClickListener;
    public int selectedPosition = -1;
    private List<Step> steps;

    public StepsAdapter(List<Step> steps, StepItemClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
        this.steps = steps;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        StepItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.step_item, parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Step step = steps.get(position);
        holder.binding.tvStep.setText(step.getShortDescription());

    }

    @Override
    public int getItemCount() {
        return steps.size();
    }

    public interface StepItemClickListener {
        void onStepItemClick(int clickedItemIndex);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        StepItemBinding binding;
        private Context context;

        public ViewHolder(View itemView) {
            super(itemView);

            itemView.setTag(3);
            binding = DataBindingUtil.bind(itemView);
            context = itemView.getContext();

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            selectedPosition = getAdapterPosition();
            mOnClickListener.onStepItemClick(selectedPosition);
            notifyDataSetChanged();
        }
    }

}
