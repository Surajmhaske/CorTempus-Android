package com.example.android.message_sqlite; /**
 * Created by Snehal on 07-05-2017.
 */
import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.google.android.gms.wearable.DataMap.TAG;

public class GPS_cordinates extends Service implements ConnectionCallbacks,
        OnConnectionFailedListener, LocationListener  {

   // Context act = null;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    // Google client to interact with Google API
    private LocationManager locationManager;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    // private static GoogleApiClient mGoogleApiClient;
    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 1000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 5; // 10 meters

    //private static final String REGISTER_URL = "http://snehalspatil1991.000webhostapp.com/index1.php";
    //private static final String REGISTER_URL = "http://spatil20.000webhostapp.com/index1.php";
    private static final String REGISTER_URL = GlobalVariables.serverBaseURL + "/index1.php";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_USER_CONTACT = "user_contact";
    public static final String KEY_EMERGENCY_CONTACT = "emergency_contact";

    private SharedPreferences sharedPreferences;
    private static final String MyPrefs = "TestPreferences";

    public GPS_cordinates(){

    }

//    public GPS_cordinates(Activity act){
//
//        this.act = act;
//        buildGoogleApiClient();
//        createLocationRequest();
///*
//        // First we need to check availability of play services
//        if (checkPlayServices()) {
//            System.out.println("ok");
//            // Building the GoogleApi client
//             buildGoogleApiClient();
//             //displayLocation();
//            createLocationRequest();
//        } else {
//            System.out.println("NOT ok");
//        }
////        if (mGoogleApiClient != null) {
////            mGoogleApiClient.connect();
////        }
//*/
//
//    }


    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        Toast.makeText(this, "Service Created", Toast.LENGTH_LONG).show();
       // this = (Service)this;

//        super.onCreate();
//        buildGoogleApiClient();
//        createLocationRequest();
        /*
        // First we need to check availability of play services
        if (checkPlayServices()) {
            System.out.println("ok");
            // Building the GoogleApi client
            buildGoogleApiClient();
            //displayLocation();
            createLocationRequest();
        } else {
            System.out.println("NOT ok");
        }
        */
    }


@Override
public int onStartCommand(Intent intent,int flags, int startid) {
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
    buildGoogleApiClient();
    createLocationRequest();
    return super.onStartCommand(intent, flags, startid);


}

    public void onDestroy() {
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_LONG).show();

    }




    /**
     * Method to verify google play services on the device
     * */
    /*
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this.act);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this.act,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(act.getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                act.finish();
            }
            return false;
        }
        return true;
    }*/



    /**
     * Creating google api client object
     * */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        mGoogleApiClient.connect();

    }



    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {

        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    private boolean isconnected = false;

    public boolean isConnected() {
        return isconnected;
    }
    @Override
    public void onConnected(Bundle arg0) {

        // Once connected with google api, get the location
        this.isconnected = true;

        displayLocation();
        startLocationUpdates();


    }

    @Override
    public void onLocationChanged(Location location) {
        // Assign the new location
        mLastLocation = location;

//        Toast.makeText(getApplicationContext(), "Location changed!",
//                Toast.LENGTH_SHORT).show();

        // Displaying the new location on UI
        displayLocation();
        //return mLastLocation;

        //1.get user name from shared preferneces
        //2.use volley for this user name , lat, log

        // final String username = "Shivaji";
        // final String password = "Shivaji";
        // final String email = "shivaji@gmail.com";
        // final String user_contact = "+157149971499";
        //final String emergency_contact = "+16619036222";

        getResponse();




    }


    /**
     * Creating location request object
     * */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT); // 10 meters
    }
    /**
     * Starting the location updates
     * */
    protected void startLocationUpdates() {
        System.out.println(" location uodate entered");
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
            System.out.println(mLastLocation);
            if (mLastLocation != null) {
                System.out.println("inside insidek");
                double latitude = mLastLocation.getLatitude();
                double longitude = mLastLocation.getLongitude();
                System.out.println(latitude + ", " + longitude);
//            lblLocation.setText(latitude + ", " + longitude);
             //  onLocationChanged();
            } else {
                System.out.println("condition fail");
//            lblLocation
//                    .setText("(Couldn't get the location. Make sure location is enabled on the device)");
            }
        } catch (SecurityException sec){
            // TODO handle allow access to location
            System.out.println("No access");
        }

        //getResponse();
 //onLocationChanged(latitude,longitude);
    }






    public void displayLocation() {
        System.out.println("entered");
        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            // Toast.makeText(context,latitude+" "+longitude,Toast.LENGTH_LONG).show();
            System.out.println(mLastLocation);
            if (mLastLocation != null) {
                System.out.println("inside insidek");
                double latitude = mLastLocation.getLatitude();
                double longitude = mLastLocation.getLongitude();
                System.out.println(latitude + ", " + longitude);
//            lblLocation.setText(latitude + ", " + longitude);
//
            } else {
                System.out.println("condition fail");
//            lblLocation
//                    .setText("(Couldn't get the location. Make sure location is enabled on the device)");
            }
        } catch (SecurityException sec){
            // TODO handle allow access to location
            System.out.println("No access");
        }

    }


    public void getResponse(){
        sharedPreferences = this.getSharedPreferences(MyPrefs, MODE_PRIVATE);
        final String user_result = sharedPreferences.getString("Susername", "default string");
        System.out.println("Resulted Value in shared pref in the classs : " + user_result);
        final String username = user_result.trim();
        final String latitude = String.valueOf(mLastLocation.getLatitude());
        final String longitude = String.valueOf(mLastLocation.getLongitude());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Toast.makeText(this.act, response, Toast.LENGTH_LONG).show();
                        System.out.println("Resulted Value in response og gps coordinates : " + response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("Resulted Value in error gps coordinates : " + error);
                        //  Toast.makeText(RegisterActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                        // Toast.makeText(RegisterActivity.this,response,Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(KEY_USERNAME, username);
                params.put(KEY_LATITUDE, latitude);
                params.put(KEY_LONGITUDE, longitude);
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


    }


}
