package com.example.mapdemo;

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
import java.util.Date;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    Context context;
    private ArrayList<Order> itemList;

    public OrderAdapter(Context context, ArrayList<Order> data) {
        this.itemList = data;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = itemList.get(position);
        Food food = order.getFood();
        Glide.with(context)
                .load(food.getThumbnails())
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache the image
                .into(holder.thumbnails);

        holder.tvNameFood.setText(food.getName());
        holder.tvCost.setText(food.getCost());
        holder.tvOrderDate.setText(new Date().toString());


        holder.btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),  OrderDetailActivity.class);
                Order selecteOrder = itemList.get(holder.getAdapterPosition());
                // Put the Food object into the Intent
                intent.putExtra("order",  selecteOrder);
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
        public TextView tvNameFood, tvCost, tvOrderDate;
        MaterialButton btnDetail;

        public ViewHolder(View view) {
            super(view);
            tvNameFood = view.findViewById(R.id.tv_name_food);
            thumbnails = view.findViewById(R.id.thumbnail_food);
            tvCost = view.findViewById(R.id.tv_cost);
            btnDetail = view.findViewById(R.id.btn_detail);
            tvOrderDate = view.findViewById(R.id.tv_date_order);
        }
    }

    public void add(Order order) {
        itemList.add(order);
        if( itemList != null ) {
            notifyItemInserted(itemList.size());
        }else {
            notifyItemInserted(0);
        }
    }
}

