package com.example.mapdemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.mapdemo.Service.GetDataAsync;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public class OrderFoodActivity extends AppCompatActivity {
    public ImageFilterView thumbnails;
    public TextView tvDistance, tvMinutes, tvNameFood, tvCost;
    MaterialButton btnOrder;
    ConstraintLayout loading;
    LatLng orderLocation = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_food);
        FirebaseApp.initializeApp(this);

        Food selectedFood = (Food) getIntent().getSerializableExtra("selectedFood");
        tvDistance = findViewById(R.id.tv_distance);
        tvNameFood = findViewById(R.id.tv_name_food);
        tvMinutes = findViewById(R.id.tv_minutes);
        thumbnails = findViewById(R.id.thumbnail_food);
        tvCost = findViewById(R.id.tv_cost);
        btnOrder = findViewById(R.id.btn_order);
        loading = findViewById(R.id.loading);


        Order order = new Order();
        // Now you have the selected Food object to work with
        if (selectedFood != null) {
            // Do something with the selectedFood object
            Toast.makeText(this, selectedFood.getName(), Toast.LENGTH_SHORT).show();
            Glide.with(this)
                    .load(selectedFood.getThumbnails())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(thumbnails);

            tvDistance.setText(selectedFood.getDistance());
            tvCost.setText(selectedFood.getCost());
            tvMinutes.setText(selectedFood.getDuration());
            tvNameFood.setText(selectedFood.getName());


            order.setFood(selectedFood);
            order.setAddress(selectedFood.getLocationShop());
            order.setTotal(selectedFood.getCost());
        }

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading.setVisibility(View.VISIBLE);
                addOrder(order);
            }
        });


    }



    public void addOrder(Order order) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("order");
        String key = myRef.push().getKey();
        assert key != null;
        getLocation(order.getAddress());

        if( orderLocation == null ) {
            Toast.makeText(this, "Không tìm thấy địa chỉ", Toast.LENGTH_SHORT).show();
            return;

        }

        order.setLocation(orderLocation);

        myRef.child(key).setValue(order)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        loading.setVisibility(View.GONE);
                        Log.d("Firebase", "Thêm dữ liệu thành công!");
                        Toast.makeText(getApplicationContext(), "Đặt hàng thành công!", Toast.LENGTH_LONG).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firebase", "Thêm dữ liệu thất bại!", e);
                        loading.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Đặt hàng thất bại!", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void getLocation(String locationStr) {
        // Build the API link
        String apiLink = "https://maps.googleapis.com/maps/api/geocode/json"
                + "?address=" + locationStr
                + "&key=" + getResources().getString(R.string.api_key);


        // Initialize the Loader
        LoaderManager.getInstance(this).destroyLoader(100);
        LoaderManager.getInstance(this).initLoader(100, null, new LoaderManager.LoaderCallbacks<String>() {
            @NonNull
            @Override
            public Loader<String> onCreateLoader(int loaderId, @Nullable Bundle args) {
                GetDataAsync getDataAsync = new GetDataAsync(OrderFoodActivity.this);
                getDataAsync.setLinkAPI(apiLink);
                return getDataAsync;
            }

            public void onLoadFinished(@NonNull Loader<String> loader, String data) {
                if (data != null) {
                    try {
                        Gson gson = new Gson();
                        JsonObject jsonObject = gson.fromJson(data, JsonObject.class);

                        // Check if the response contains "results" array
                        if (jsonObject.has("results")) {
                            JsonArray resultsArray = jsonObject.getAsJsonArray("results");

                            // Check if the results array is not empty
                            if (resultsArray.size() > 0) {
                                JsonObject firstResult = resultsArray.get(0).getAsJsonObject();

                                // Extract location details
                                String formattedAddress = firstResult.get("formatted_address").getAsString();
                                double latitude = firstResult.getAsJsonObject("geometry")
                                        .getAsJsonObject("location")
                                        .get("lat")
                                        .getAsDouble();
                                double longitude = firstResult.getAsJsonObject("geometry")
                                        .getAsJsonObject("location")
                                        .get("lng")
                                        .getAsDouble();

                                // Print or use the extracted information as needed
                                System.out.println("Formatted Address: " + formattedAddress);
                                System.out.println("Latitude: " + latitude);
                                System.out.println("Longitude: " + longitude);
                                orderLocation = new LatLng(latitude, longitude);

                                Toast.makeText(getApplicationContext(), String.valueOf(latitude), Toast.LENGTH_LONG).show();
                                // You may want to update UI or do other actions here
                            } else {
                                Log.e("GeocodingTask", "No results found in the 'results' array");
                            }
                        } else {
                            Log.e("GeocodingTask", "Response does not contain 'results' array");
                        }
                    } catch (JsonSyntaxException e) {
                        Log.e("GeocodingTask", "Error parsing JSON: " + e.getMessage());
                    }
                }
            }


            @Override
            public void onLoaderReset(@NonNull Loader<String> loader) {
                // Handle loader reset if needed
            }
        }).forceLoad();
    }

}