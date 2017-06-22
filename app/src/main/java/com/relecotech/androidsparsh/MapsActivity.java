package com.relecotech.androidsparsh;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private double lat1, lat2, lon1, lon2;
    private JsonObject trackingDataItem;
    private JsonArray getJsonTrackResponse;
    private String busId;
    private String date_Time;
    private String bus_lati;
    private String bus_longi;
    private ArrayList<LatLng> points;
    private Polyline line;
    private LatLng latLng;
    HashMap<String, String> userDetails;
    private SessionManager sessionManager;
    private String direction;
    private LatLng pickPointsLatLon;
    private List<LatLng> latLngList;
    private ArrayList<String> stringPickPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        sessionManager = new SessionManager(this);
        userDetails = sessionManager.getUserDetails();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Bus Tracker");

        Intent intent = getIntent();
        stringPickPoints = intent.getStringArrayListExtra("pickPoint");
        latLngList = new ArrayList<>();
        points = new ArrayList<LatLng>();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        int id = item.getItemId();
        if (id == R.id.refresh_dashboard) {
//            line.remove();
            if (!points.isEmpty()) {
                points.clear();
            }
            new GetDataFromServer().execute();
        }

        return super.onOptionsItemSelected(item);
    }

    private class GetDataFromServer extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                getTrackingData();
            } catch (Exception e) {
                System.out.println("error" + e.getMessage());
            }
            return null;
        }
    }

    private void getTrackingData() {

        trackingDataItem = new JsonObject();
        trackingDataItem.addProperty("BusId", userDetails.get(SessionManager.KEY_BUS_ID));

        final SettableFuture<JsonElement> resultFuture = SettableFuture.create();
        ListenableFuture<JsonElement> serviceFilterFuture = MainActivity.mClient.invokeApi("getTrackingData", trackingDataItem);

        Futures.addCallback(serviceFilterFuture, new FutureCallback<JsonElement>() {
            @Override
            public void onFailure(Throwable exception) {
                resultFuture.setException(exception);
                System.out.println(" addTrackingData exception    " + exception);

            }

            @Override
            public void onSuccess(JsonElement response) {
                resultFuture.set(response);
                System.out.println(" addTrackingData  response    " + response);
                parseJSONAndPopulate(response);
            }
        });
    }

    private void parseJSONAndPopulate(JsonElement busTrackerResponse) {

        getJsonTrackResponse = busTrackerResponse.getAsJsonArray();

        if (getJsonTrackResponse.size() != 0) {
            System.out.println("getJsonListResponse.size()" + getJsonTrackResponse.size());
            for (int loop = 0; loop < getJsonTrackResponse.size(); loop++) {

                JsonObject jsonObjectForIteration = getJsonTrackResponse.get(loop).getAsJsonObject();

                date_Time = jsonObjectForIteration.get("dateTime").toString().replace("\"", "");
                bus_lati = jsonObjectForIteration.get("latitude").toString().replace("\"", "");
                bus_longi = jsonObjectForIteration.get("longitude").toString().replace("\"", "");
                busId = jsonObjectForIteration.get("bus_id").toString().replace("\"", "");
                latLng = new LatLng(Double.parseDouble(bus_lati), Double.parseDouble(bus_longi));
                direction = jsonObjectForIteration.get("direction").toString().replace("\"", "");

                points.add(loop, latLng);
                addMarker(latLng, 1);
                redrawLine();
            }
        }
    }

    private void addMarker(LatLng latLng, int i) {
//        mMap.clear();
        drawMarker();
        mMap.addMarker(new MarkerOptions().position(latLng).title(busId).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_bus)));


    }


    private void redrawLine() {

        mMap.clear();  //clears all Markers and Polyline

        PolylineOptions polylineOptions = new PolylineOptions().width(5).color(Color.GRAY).geodesic(true);
        for (int i = 0; i < points.size(); i++) {

            System.out.println(" point " + points.get(i));
            LatLng point = points.get(i);
            polylineOptions.add(point);
        }
//        polylineOptions.addAll(points);

        System.out.println("polylineOptions.getPoints() " + polylineOptions.getPoints());

        mMap.addPolyline(polylineOptions); //add Polyline
        addMarker(latLng, 1); //add Marker in current position
    }


    // Adding marker on the GoogleMaps
    private void drawMarker() {

        LatLng home = new LatLng(21.131806, 79.098861);
        mMap.addMarker(new MarkerOptions().position(home).title("Home").icon(BitmapDescriptorFactory.fromResource(R.drawable.home_pin1)));

        try {
            for (int loop = 0; loop < stringPickPoints.size(); loop++) {

                String latLongString = String.valueOf(stringPickPoints.get(loop));
                String latString = latLongString.substring(0, latLongString.indexOf(","));
                String lonString = latLongString.substring(latLongString.indexOf(",") + 1, latLongString.length());

//                drawMarker(new LatLng(Double.parseDouble(latString), Double.parseDouble(lonString)));
                mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(latString), Double.parseDouble(lonString))).title("STOP").icon(BitmapDescriptorFactory.fromResource(R.drawable.stop_sign)));
            }
        } catch (Exception e) {
            System.out.println("INSIDE onMapReady Exception " + e.getMessage());
        }


        mMap.addMarker(new MarkerOptions().position(new LatLng(21.108202, 79.103225)).title("Shahu Garden High School").icon(BitmapDescriptorFactory.fromResource(R.drawable.school1)));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(21.108202, 79.103225), 14.0f));

    }


    @Override
    public void onLocationChanged(Location location) {

        // Getting latitude of the current location
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        // Creating a LatLng object for the current location
        LatLng latLng = new LatLng(latitude, longitude);

        // Showing the current location in Google Map
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        // Zoom in the Google Map
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(17));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        System.out.println("INSIDE onMapReady ");

//        try {
//            // Customise the styling of the base map using a JSON object defined
//            // in a raw resource file.
//            boolean success = mMap.setMapStyle(
//                    MapStyleOptions.loadRawResourceStyle(
//                            this, R.raw.style_map));
//
//            if (!success) {
//                System.out.println("Style parsing failed.");
//            }
//        } catch (Resources.NotFoundException e) {
//            System.out.println("Can't find style. Error: " + e);
//        }


//        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);


        drawMarker();
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        mMap.getCameraPosition();
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(17));

        new GetDataFromServer().execute();

    }

    private double getDistance() {
        String unit = "K";
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit.equals("K")) {
            dist = dist * 1.609344;
        }
        return (dist);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::	This function converts decimal degrees to radians						 :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::	This function converts radians to decimal degrees						 :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }


}
