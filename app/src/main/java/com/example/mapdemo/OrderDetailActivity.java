package com.example.mapdemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.example.mapdemo.Service.GetDataAsync;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.TravelMode;

import java.util.ArrayList;
import java.util.List;

/** @noinspection ALL*/
public class OrderDetailActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener, RoutingListener {
    private GoogleMap googleMap;
    MapView mapView;
    Order order;
    LatLng locationOrder;
    //current and destination location objects
    Location myLocation=null;
    Location destinationLocation=null;
    protected LatLng start=null;
    protected LatLng end=null;

    //polyline object
    private List<Polyline> polylines=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        FirebaseApp.initializeApp(this);
        order = (Order) getIntent().getSerializableExtra("order");


        if(order == null ) {
            Toast.makeText(this, "Finish", Toast.LENGTH_LONG).show();
            finish();

        };
        getLocation(order.getFood().getLocationShop());

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                // Add map customization or markers here
                googleMap = map;
                LatLng ori = new LatLng(10.775843, 106.699139);

                LatLng des = new LatLng(10.729934, 106.721797);

                end = des;
                start = ori;

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ori, 100));
                googleMap.addMarker(new MarkerOptions().position(ori).title("Origin"));
        /*        getWalkingRoute(ori, des);*/
                Findroutes(ori, des);


            }
        });

    }
    @SuppressLint("StaticFieldLeak")
    private void getWalkingRoute(LatLng ori, LatLng des) {
        // Sử dụng AsyncTask hoặc Thread để không chặn giao diện người dùng
        new AsyncTask<Void, Void, List<LatLng>>() {
            @Override
            protected List<LatLng> doInBackground(Void... voids) {
                try {
                    // Gửi yêu cầu đến Directions API để lấy thông tin về tuyến đường đi bộ
                    DirectionsResult result = DirectionsApi.newRequest(getGeoContext())
                            .mode(TravelMode.WALKING)
                            .origin(new com.google.maps.model.LatLng(ori.latitude, ori.longitude))
                            .destination(new com.google.maps.model.LatLng(des.latitude, des.longitude))
                            .await();

                    // Trích xuất các điểm từ dữ liệu kết quả
                    List<LatLng> path = new ArrayList<>();
                    for (DirectionsStep step : result.routes[0].legs[0].steps) {
                        path.add(new LatLng(step.startLocation.lat, step.startLocation.lng));
                    }
                    path.add(new LatLng(result.routes[0].legs[0].endLocation.lat, result.routes[0].legs[0].endLocation.lng));

                    return path;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<LatLng> path) {
                // Vẽ tuyến đường đi bộ trên bản đồ
                if (path != null && path.size() > 0) {
                    PolylineOptions polylineOptions = new PolylineOptions()
                            .addAll(path)
                            .width(5)
                            .color(Color.RED);
                    googleMap.addPolyline(polylineOptions);
                }
            }
        }.execute();
    }

    private GeoApiContext getGeoContext() {
        // Tạo và cấu hình đối tượng GeoApiContext với khóa API
        return new GeoApiContext.Builder()
                .apiKey(getResources().getString(R.string.api_key))
                .build();
    }
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
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
                GetDataAsync getDataAsync = new GetDataAsync(OrderDetailActivity.this);
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

                                Toast.makeText(getApplicationContext(), String.valueOf(latitude), Toast.LENGTH_LONG).show();
                                locationOrder = new LatLng(latitude, longitude);

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

    ////draw route

    public void Findroutes(LatLng Start, LatLng End)
    {
        if(Start==null || End==null) {
            Toast.makeText(OrderDetailActivity.this,"Unable to get location",Toast.LENGTH_LONG).show();
        }
        else
        {

            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener((RoutingListener) this)
                    .alternativeRoutes(true)
                    .waypoints(Start, End)
                    .key(getResources().getString(R.string.api_key))  //also define your api key here.
                    .build();
            routing.execute();
        }
    }

    //Routing call back functions.


    //Routing call back functions.
    @Override
    public void onRoutingFailure(RouteException e) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar snackbar= Snackbar.make(parentLayout, e.toString(), Snackbar.LENGTH_LONG);
        snackbar.show();
//    Findroutes(start,end);
    }

    @Override
    public void onRoutingStart() {
        Toast.makeText(OrderDetailActivity.this,"Finding Route...",Toast.LENGTH_LONG).show();
    }


    //If Route finding success..
    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {

        CameraUpdate center = CameraUpdateFactory.newLatLng(start);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
        if(polylines!=null) {
            polylines.clear();
        }
        PolylineOptions polyOptions = new PolylineOptions();
        LatLng polylineStartLatLng=null;
        LatLng polylineEndLatLng=null;


        polylines = new ArrayList<>();
        //add route(s) to the map using polyline
        for (int i = 0; i <route.size(); i++) {

            if(i==shortestRouteIndex)
            {
                polyOptions.color(getResources().getColor(R.color.colorPrimary));
                polyOptions.width(20);
                polyOptions.addAll(route.get(shortestRouteIndex).getPoints());
                Polyline polyline = googleMap.addPolyline(polyOptions);
                polylineStartLatLng=polyline.getPoints().get(0);
                int k=polyline.getPoints().size();
                polylineEndLatLng=polyline.getPoints().get(k-1);
                polylines.add(polyline);

            }
            else {

            }

        }

        //Add Marker on route starting position
        MarkerOptions startMarker = new MarkerOptions();
        startMarker.position(polylineStartLatLng);
        startMarker.title("My Location");
        googleMap.addMarker(startMarker);

        //Add Marker on route ending position
        MarkerOptions endMarker = new MarkerOptions();
        endMarker.position(polylineEndLatLng);
        endMarker.title("Destination");
        googleMap.addMarker(endMarker);
    }

    @Override
    public void onRoutingCancelled() {
        Findroutes(start,end);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Findroutes(start,end);

    }



}
