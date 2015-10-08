package com.example.mathias.helloworld;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LocationManager mLocationManager; // Provides information on user location
    private LocationListener mLocationListener;
    private boolean mRequestingUpdates = false;
    private double mLatitude;
    private double mLongitude;
    private int minUpdateTime = 1000;
    private int minUpdateDist = 0;
    private ArrayList<Marker> markerList = new ArrayList<Marker>();
    private int activateBluetoothRange = 50;
    private NotificationManager mNotificationManager;
    private int notificationId = 1;
    private int TOAST_OPTION = 0;
    private int NOTIFICATION_OPTION = 1;
    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        settings = getSharedPreferences(UserStatic.getEmail(), 0);
        setUpMapIfNeeded();
    }

    protected void displayNotification(){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("Treasure within sigth!");
        mBuilder.setContentText("Or maybe i'm wrong arrgh");
        mBuilder.setTicker("yoho, yoho. A pirate life for me!");
        mBuilder.setSmallIcon(R.drawable.ic_launcher_web);

        Intent resultIntent = new Intent(this,HomeActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(HomeActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_ONE_SHOT);

        mBuilder.setContentIntent(pendingIntent);

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(notificationId, mBuilder.build());
    }

    @Override
    protected void onStart() {
        Log.d("on resume", "resuming");
        if (!mRequestingUpdates) {
            mLocationManager.removeUpdates(mLocationListener);
        }
        setUpMapIfNeeded();
        super.onStart();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera.
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        Log.d("setup", "Doing setup");
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker").snippet("Snippet"));
        //Dummy marker?

        // Enable MyLocation Layer of Google Map
        mMap.setMyLocationEnabled(true);

        // Create a criteria object to retrieve provider
        //Criteria criteria = new Criteria();

        // Get the name of the best provider
        //String provider = getLocationManager().getBestProvider(criteria, true);

        // Get Current Location from the best provider
        Location myLocation = getLocationManager().getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        //Fejlen ovenover angiver om vi har faaet adgang til last know location.
        //Vi kan godt indfoere tjekket eller droppe det
        //Den beholder vi simpelthen bare og dropper at checke efter noget.

        //tjekker om myLocation = null
        if (myLocation == null) {
            Log.d("location", "null");
            return;
        }

        // set map type
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Get latitude and longitude of the current location
        mLatitude = myLocation.getLatitude();
        mLongitude = myLocation.getLongitude();

        // Create a LatLng object for the current location
        LatLng myCoordinates = new LatLng(mLatitude, mLongitude);

        // Show the current location in Google Map
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myCoordinates));

        // Zoom in the Google Map
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
        
        // Add marker showing your location
        //addMapMarker(mLatitude, mLongitude, "Dig");
        // Function for zooming to current location
        //CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(myCoordinates, 12);
        //mMap.animateCamera(yourLocation);

        //Request continous updates
        mRequestingUpdates = false;
        requestLocalUpdatesIfNeeded(LocationManager.NETWORK_PROVIDER);

        //Update our position on server
        updateServerPosition();

        //Get other users position from server
        updateOtherUsers(TOAST_OPTION);

    }


    private LocationManager getLocationManager() {
        if (mLocationManager == null)
            // Get our LocationManager object from System Service LOCATION_SERVICE
            mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return mLocationManager;
    }

    //Request continous local position updates
    private void requestLocalUpdatesIfNeeded(String provider){

        if (!mRequestingUpdates) {
            // create locationListener, will receive location updates
            mLocationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Log.d("Location", "changed");
                    mLatitude = location.getLatitude();
                    mLongitude = location.getLongitude();
                    updateServerPosition();
                    updateOtherUsers(TOAST_OPTION);
                }
                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {}
                @Override
                public void onProviderEnabled(String s) {}
                @Override
                public void onProviderDisabled(String s) {}
            };

            //start receiving continous position updates
            mLocationManager.requestLocationUpdates(provider, minUpdateTime, minUpdateDist, mLocationListener);

            mRequestingUpdates = true;
        }

    }

    private boolean updateServerPosition() {
        String req_tag = "req_update_position";

        StringRequest req = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN,
                new Response.Listener<String>() {
                    @Override  //If succesfull, should move to next screen
                    public void onResponse(String response) {
                        Log.d("position update", "position update response: " + response);

                        try {
                            //Create JSONObject, easier to work with
                            JSONObject JResponse = new JSONObject(response);
                            boolean error = JResponse.getBoolean("error");
                            if (!error) {}
                                //what to use data for, if anything?
                            else{
                                Log.e("position update", "position update error: " + response);
                                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT)
                                        .show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override //If not succesfull, show user error message
            //only does it, if there's a network error, not login error
            public void onErrorResponse(VolleyError error) {
                Log.e("login", "Login error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT)
                        .show();
            }
        })  {
            @Override // Set all parameters for for server
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "updatePosition");
                params.put("email", UserStatic.getEmail());
                params.put("latitude", String.valueOf(mLatitude));
                params.put("longitude", String.valueOf(mLongitude));
                return params;
            }
        };

        req.addMarker(req_tag);
        //Adding request to request queue
        NetworkSingleton.getInstance(getApplicationContext()).addToRequestQueue(req);
        return true;
    }

    private boolean updateOtherUsers(final int popupOption) {
        String req_tag = "req_get_position";

        StringRequest req = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN,
                new Response.Listener<String>() {
                    @Override  //If succesfull, should move to next screen
                    public void onResponse(String response) {
                        Log.d("others position update", " others position update response: " + response);

                        try {
                            //Create JSONObject, easier to work with
                            JSONObject JResponse = new JSONObject(response);
                            boolean error = JResponse.getBoolean("error");
                            if (!error) {
                                //Delete current markers
                                deleteAllMarkers();
                                //Get array of positions
                                JSONObject positions = JResponse.getJSONObject("positions");
                                //add markers to map via loop
                                for (int i = 0; i < positions.length(); i++) {
                                    JSONObject position = positions.getJSONObject("position" + i);
                                    String email = position.getString("email");
                                    double latitude = position.getDouble("latitude");
                                    double longitude = position.getDouble("longitude");
                                    Log.d("latitude", "" + latitude);
                                    Log.d("longitude", "" + longitude);
                                    addMapMarker(latitude, longitude, email);
                                }

                                if(existsMarkersWithinRange()){
                                    Context context = getApplicationContext();
                                    CharSequence text = "Someone is in range!! Maybe you can steal treasure!";
                                    int duration = Toast.LENGTH_SHORT;
                                    Log.d("notifications:", String.valueOf(settings.getBoolean("notifications", false)));
                                    if (settings.getBoolean("notifications", false))
                                    {
                                        if(popupOption == TOAST_OPTION){
                                            Toast toast = Toast.makeText(context, text, duration);
                                            toast.show();
                                        } else if(popupOption == NOTIFICATION_OPTION){
                                            displayNotification();
                                        }
                                    }

                                }


                            }
                            else{
                                Log.e("position update", "position update error: " + response);
                                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG)
                                        .show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override //If not succesfull, show user error message
            //only does it, if there's a network error, not login error
            public void onErrorResponse(VolleyError error) {
                Log.e("login", "Login error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG)
                        .show();
            }
        })  {
            @Override // Set all parameters for server
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "getPosition");
                params.put("email", UserStatic.getEmail());
                return params;
            }
        };

        req.addMarker(req_tag);
        //Adding request to request queue
        NetworkSingleton.getInstance(getApplicationContext()).addToRequestQueue(req);
        return true;
    }

    private void addMapMarker(double latitude, double longitude, String title) {
        Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(latitude, longitude))
                            .title(title).snippet("Position of: " + title));
        markerList.add(marker);
    }

    private void deleteAllMarkers() {
        for (Marker m : markerList) {
            m.remove();
        }
    }

    public Boolean existsMarkersWithinRange(){
        for(Marker m: markerList){
            float[] result = new float[1];
            double latitude = m.getPosition().latitude;
            double longitude = m.getPosition().longitude;
            Location.distanceBetween(mLatitude, mLongitude, latitude, longitude, result);
            if(activateBluetoothRange > Math.abs(result[0])) {
                return true;
            }
        }
        return false;
    }

    protected void onStop(){

        Log.d("onPause", "Paused ");
        mLocationManager.removeUpdates(mLocationListener);
        mRequestingUpdates = false;
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mLatitude = location.getLatitude();
                mLongitude = location.getLongitude();
                updateServerPosition();
                updateOtherUsers(NOTIFICATION_OPTION);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minUpdateTime, minUpdateDist, mLocationListener);
        super.onStop();
    }

}
