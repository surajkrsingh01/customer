package com.shoppurs.location;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.core.app.ActivityCompat;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.shoppurs.R;
import com.shoppurs.interfaces.OnLocationReceivedListener;
import com.shoppurs.utilities.AppController;
import com.shoppurs.utilities.Constants;


import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GpsLocationProvider {

    private final String TAG = "GpsLocationProvider";
    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private Handler mHandler;
    private boolean isReturnResult;
    private Location mLocation;
    private String mAddress;
    private SharedPreferences sharedPreferences;
    private OnLocationReceivedListener onLocationReceivedListener;

    public void setOnLocationReceivedListener(OnLocationReceivedListener onLocationReceivedListener) {
        this.onLocationReceivedListener = onLocationReceivedListener;
    }

    private Context context;

    public GpsLocationProvider(Context context, boolean isReturnResult){
        this.context = context;
        this.isReturnResult = isReturnResult;
        sharedPreferences = context.getSharedPreferences(Constants.MYPREFERENCEKEY,context.MODE_PRIVATE);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    public void buildLocationProviderInstance() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        createLocationRequest();
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Log.i(TAG,"Location is null");
                    if(isReturnResult){
                        onLocationReceivedListener.onLocationReceived(null);
                    }
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    Log.i(TAG,"Location is "+location.getLatitude()+" "+location.getLongitude());
                    mLocation = location;
                    if(isReturnResult){
                       onLocationReceivedListener.onLocationReceived(location);
                    }else{
                        startFetchAddressIntentService();
                    }
                    stopLocationUpdates();
                }
            };
        };

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                startLocationUpdates();
            }
        };
    }

    private void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null /* Looper */);
        Log.i(TAG,"Location updates started");
    }

    public void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
        Log.i(TAG,"Location updates stopped");
    }

    private void saveLocationUpdate(){
        Map<String,String> params=new HashMap<>();
        params.put("promotorId",""+sharedPreferences.getInt(Constants.USER_ID,0));
        params.put("lattitude",""+mLocation.getLatitude());
        params.put("longitude",""+mLocation.getLongitude());
        params.put("device","Android");
        params.put("address",mAddress);
        String url=context.getResources().getString(R.string.url_customer)+"/api/customers/user/location";
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST,url,new JSONObject(params),new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                AppController.getInstance().getRequestQueue().getCache().clear();
                Log.i(TAG,response.toString());
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                AppController.getInstance().getRequestQueue().getCache().clear();
                Log.i(TAG,"Json Error "+error.toString());
            }
        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Basic "+sharedPreferences.getString(Constants.TOKEN,""));
                return params;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    protected void startFetchAddressIntentService() {
      /*  // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(context, FetchAddressIntentService.class);
        // Pass the result receiver as an extra to the service.
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        // Pass the location data as an extra to the service.
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLocation);
        context.startService(intent);*/

        FetchAddress fetchAddress = new FetchAddress();
        // Start downloading json data from Google Directions API
        fetchAddress.execute("");
    }

    // Fetches data from url passed
    private class FetchAddress extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            try {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String errorMessage = "";

                List<Address> addresses = null;

                try {
                    addresses = geocoder.getFromLocation(
                            mLocation.getLatitude(),
                            mLocation.getLongitude(),
                            // In this sample, get just a single address.
                            1);
                    //showToast("LocationAddress is gotten..");
                } catch (IOException ioException) {
                    // Catch network or other I/O problems.
                    errorMessage = context.getString(R.string.service_not_available)+ioException.toString();
                    //  Log.e(TAG, errorMessage, ioException);
                } catch (IllegalArgumentException illegalArgumentException) {
                    // Catch invalid latitude or longitude values.
                    errorMessage = context.getString(R.string.invalid_lat_long_used);
                }

                // Handle case where no address was found.
                if (addresses == null || addresses.size() == 0) {
                    if (errorMessage.isEmpty()) {
                        errorMessage = context.getString(R.string.no_address_found);
                        Log.d(TAG, "Address no found "+errorMessage);
                    }
                    mAddress = "";
                } else {
                    Address address = addresses.get(0);
                    ArrayList<String> addressFragments = new ArrayList<String>();

                    // Fetch the address lines using getAddressLine,
                    // join them, and send them to the thread.
                    for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                        addressFragments.add(address.getAddressLine(i));
                    }

                    // addressFragments have no values but address has
                    Log.d(TAG , "Address found "+address.getAddressLine(0));
                    mAddress = address.getAddressLine(0);

                }

            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            saveLocationUpdate();
        }
    }


    public void getlastLocation(){
        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                onLocationReceivedListener.onLocationReceived(location);
            }
        });
    }

}
