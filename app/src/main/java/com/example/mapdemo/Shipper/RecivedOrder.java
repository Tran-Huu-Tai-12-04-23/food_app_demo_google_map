package com.example.mapdemo.Shipper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.utils.widget.ImageFilterView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.mapdemo.Order;
import com.example.mapdemo.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RecivedOrder extends AppCompatActivity {

    ImageFilterView thumbnails;
    TextView tvNameFood;
    MaterialButton btnShip;
    Order order;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recived_order);

        btnShip = findViewById(R.id.btn_ship);
        thumbnails = findViewById(R.id.thumbnail_food);
        tvNameFood = findViewById(R.id.tv_name_food);

        FirebaseApp.initializeApp(this);


        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("order");

        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    order = snapshot.getValue(Order.class);
                    tvNameFood.setText(order.getFood().getName());

                    Glide.with(getApplicationContext())
                            .load(order.getFood().getThumbnails())
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(thumbnails);
                    return;
                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors or onCancelled events
                Log.e("Firebase", "Error: " + databaseError.getMessage());
            }
        });

        btnShip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addShip(order);


            }



        });
    }

    private void addShip(Order order) {
        DatabaseReference ship = FirebaseDatabase.getInstance().getReference("ship");

        String orderKey = ship.push().getKey();

        // Set the order under the generated key
        ship.child(orderKey).setValue(order)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firebase", "Order added successfully!");

                        Intent intent = new Intent(getApplicationContext(), DirectionActivity.class);
                        intent.putExtra("lat", order.getLocation().latitude);
                        intent.putExtra("lng", order.getLocation().longitude);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firebase", "Failed to add order: " + e.getMessage());
                    }
                });
    }


}