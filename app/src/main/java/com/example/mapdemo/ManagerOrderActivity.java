package com.example.mapdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ManagerOrderActivity extends AppCompatActivity {

    RecyclerView recyclerViewContentOrder;
    ArrayList<Order> listOrder;
    OrderAdapter orderAdapter;
    LinearLayout loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_order);


        loading = findViewById(R.id.loading);
        FirebaseApp.initializeApp(this);


        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("order");

        recyclerViewContentOrder = findViewById(R.id.content_order_list);
        recyclerViewContentOrder.setLayoutManager(new LinearLayoutManager(this));

        listOrder = new ArrayList<>();
        orderAdapter = new OrderAdapter(this, listOrder);
        recyclerViewContentOrder.setAdapter(orderAdapter);

        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Order order = snapshot.getValue(Order.class);
                    orderAdapter.add(order);
                }

                loading.setVisibility(View.GONE);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors or onCancelled events
                Log.e("Firebase", "Error: " + databaseError.getMessage());
            }
        });


    }
}