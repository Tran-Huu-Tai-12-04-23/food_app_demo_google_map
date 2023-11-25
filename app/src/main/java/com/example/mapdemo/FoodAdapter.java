package com.example.mapdemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {
    Context context;
    private ArrayList<Food> itemList;

    public FoodAdapter(Context context, ArrayList<Food> data) {
        this.itemList = data;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.food_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Food food = itemList.get(position);
        Glide.with(context)
                .load(food.getThumbnails())
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache the image
                .into(holder.thumbnails);

        holder.tvNameFood.setText(food.getName());
        holder.tvCost.setText(food.getCost());
        holder.tvDistance.setText(food.getDistance());
        holder.tvMinutes.setText(food.getDuration());


        holder.btnBuyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OrderFoodActivity.class);
                Food selectedFood = itemList.get(holder.getAdapterPosition());
                // Put the Food object into the Intent
                intent.putExtra("selectedFood",  selectedFood);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageFilterView thumbnails;
        public TextView tvDistance, tvMinutes, tvNameFood, tvCost;
        MaterialButton btnBuyNow;

        public ViewHolder(View view) {
            super(view);
            tvDistance = view.findViewById(R.id.tv_distance);
            tvNameFood = view.findViewById(R.id.tv_name_food);
            tvMinutes = view.findViewById(R.id.tv_minutes);
            thumbnails = view.findViewById(R.id.thumbnail_food);
            tvCost = view.findViewById(R.id.tv_cost);
            btnBuyNow = view.findViewById(R.id.btn_buy_now);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void editFood(Food food, int index) {

        itemList.get(index).setDuration(food.getDuration());
        itemList.get(index).setDistance(food.getDistance());
        notifyDataSetChanged();

    }

}
