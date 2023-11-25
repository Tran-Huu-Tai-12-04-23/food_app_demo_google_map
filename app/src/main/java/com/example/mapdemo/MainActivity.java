package com.example.mapdemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mapdemo.Service.GetDataAsync;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends FragmentActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 123;
    private String apiLink = "https://maps.googleapis.com/maps/api/distancematrix/json?key=AIzaSyCwODDdAAlVuYb27NKf_vp0Vow4wRANZ6o";
    private static final int LOADER_ID = 1;
    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private RecyclerView recyclerViewContentFood;
    private FoodAdapter foodAdapter;

    private TextView tvLocation;
    ArrayList<Food> itemList;
    ArrayList<Food> dataFood;
    ProgressBar loading;
    LinearLayout btnManOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvLocation = findViewById(R.id.tv_location);
        loading = findViewById(R.id.loading);
        btnManOrder = findViewById(R.id.btn_manager_order);

        btnManOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ManagerOrderActivity.class);

                startActivity(intent);
            }
        });

        //get your current location
        checkLocationPermission();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();

        recyclerViewContentFood = findViewById(R.id.container_view_food);
        recyclerViewContentFood.setLayoutManager(new LinearLayoutManager(this));

        itemList = fakeDataFood();
        foodAdapter = new FoodAdapter(this, itemList);
        recyclerViewContentFood.setAdapter(foodAdapter);

        recyclerViewContentFood.setVisibility(View.VISIBLE);
        loading.setVisibility(View.GONE);


//        int i = 0;
//        for( Food food : itemList ) {
//            if( currentLocation != null ) {
//                String originAddress = getNameAddress(currentLocation);
//                String destinationAddress = food.getLocationShop();
//                makeAPICall(originAddress, destinationAddress,  i );
//            }else {
//                String originAddress ="140 Hoàng Diệu 2, P. Linh Chiểu, Thành Phố Thủ Đức, TP. HCM";
//                String destinationAddress = food.getLocationShop();
//                makeAPICall(originAddress, destinationAddress,  i );
//            }
//            i++;
//        }
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if( location != null ) {
                    currentLocation = location;
                    tvLocation.setText(getNameAddress(currentLocation));
                }else {
                    Toast.makeText(getApplicationContext(), "Location not found!", Toast.LENGTH_LONG).show();

                }
            }
        });
    }
    public String getNameAddress( Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if (addresses != null && addresses.size() > 0) {
                String nameAddress = addresses.get(0).getAddressLine(0);
                return nameAddress;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{ android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permission is already granted, get the current location
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, get the current location
                getLastLocation();

            } else {
                Toast.makeText(getApplicationContext(), "Acess denined!", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private ArrayList<Food> fakeDataFood() {
        ArrayList<Food> listFood = new ArrayList<>();

        Food food = new Food();
        food.setThumbnails("https://images.foody.vn/res/g116/1155652/prof/s640x400/foody-upload-api-foody-mobile-im-6522b18f-221112225428.jpeg");
        food.setCost("40.000 vnd");
        food.setName("Cơm Ba Ghiên - Nguyễn Văn Trỗi");
        food.setDescription("Cơm gà ngonn , ");
        food.setLocationShop("146/3 Nguyễn Văn Trỗi, P. 8, Phú Nhuận, TP. HCM");

        listFood.add(food);


        Food food1 = new Food();
        food1.setThumbnails("https://images.foody.vn/res/g5/40315/prof/s640x400/foody-upload-api-foody-mobile-z4-b2f2645d-230826132121.jpeg");
        food1.setCost("55,000 vnd");
        food1.setName("Trà Sữa Hoa Hướng Dương - Hoàng Diệu 2");
        food1.setDescription("Trà sửa ngon lắm , ");
        food1.setLocationShop("140 Hoàng Diệu 2, P. Linh Chiểu, Thành Phố Thủ Đức, TP. HCM");

        listFood.add(food1);


        Food food2 = new Food();
        food2.setThumbnails("https://images.foody.vn/res/g112/1116116/prof/s640x400/foody-upload-api-foody-mobile-an-025f67f7-211112160025.jpg");
        food2.setCost("60.000 vnd");
        food2.setName("Gà Ta Tường Vy - Cơm Gà, Cháo & Gỏi Gà - Nguyễn Văn Quá");
        food2.setDescription("Cơm gà ngonn , ");
        food2.setLocationShop("661 Nguyễn Văn Quá, P. Đông Hưng Thuận, Quận 12, TP. HCM");

        listFood.add(food2);


        Food food3 = new Food();
        food3.setThumbnails("https://images.foody.vn/res/g117/1163333/prof/s640x400/foody-upload-api-foody-mobile-ta-f8e92a47-230304194500.jpeg");
        food3.setCost("100.000 vnd");
        food3.setName("Cơm Chiên & Nui Xào - Đường 50");
        food3.setDescription("Cơm chiên ngonn , ");
        food3.setLocationShop("10 Đường số 50, P. 5, Quận 4, Quận 4, TP. HCM");

        listFood.add(food3);
        listFood.add(food3);
        listFood.add(food3);
        listFood.add(food3);
        listFood.add(food3);
        listFood.add(food3);
        listFood.add(food3);

        return listFood;
    }
    private String encodeAddress(String address) {
        try {
            return URLEncoder.encode(address, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Error encoding address", e);
        }
    }
    private void makeAPICall(String origin, String destination, final int id) {
        // Build the API link
        String apiLink = "https://maps.googleapis.com/maps/api/distancematrix/json"
                + "?origins=" + encodeAddress(origin)
                + "&destinations=" + encodeAddress(destination)
                + "&key=" + getResources().getString(R.string.api_key) ; // Replace with your actual API key

        // Initialize the Loader
        LoaderManager.getInstance(this).destroyLoader(id);
        LoaderManager.getInstance(this).initLoader(id, null, new LoaderManager.LoaderCallbacks<String>() {
            @NonNull
            @Override
            public Loader<String> onCreateLoader(int loaderId, @Nullable Bundle args) {
                GetDataAsync getDataAsync = new GetDataAsync(MainActivity.this);
                getDataAsync.setLinkAPI(apiLink);
                return getDataAsync;
            }

            @Override
            public void onLoadFinished(@NonNull Loader<String> loader, String data) {
                Log.i("data", data);
                if (data != null) {
                    // Process the data and update the Food object
                    try {
                        JSONObject jsonData = new JSONObject(data);
                        JSONArray rows = jsonData.getJSONArray("rows");
                        JSONObject elements = rows.getJSONObject(0).getJSONArray("elements").getJSONObject(0);
                        String distanceText = elements.getJSONObject("distance").getString("text");
                        String duration = elements.getJSONObject("duration").getString("text");
                        if( itemList != null ) {
                            Food food = itemList.get(id);
                            food.setDistance(distanceText);
                            food.setDuration(duration);
                            foodAdapter.editFood(food, id);

                            if( id == itemList.size() - 1) {

                                loading.setVisibility(View.GONE);
                                recyclerViewContentFood.setVisibility(View.VISIBLE);
                            }
                        }


                        // You may want to update UI or do other actions here
                    } catch (JSONException e) {
                        e.printStackTrace();
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