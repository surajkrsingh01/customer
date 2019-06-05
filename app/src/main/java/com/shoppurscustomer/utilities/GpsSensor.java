package com.shoppurscustomer.utilities;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

/**
 * Created by Shweta on 13-10-2017.
 */

public class GpsSensor {

    // Declaring a Location Manager
    private LocationManager mLocationManager;
    private Context context;
    private boolean isGPSEnabled,isNetworkEnabled;

    private Location mLocation;

    private double mLatitude;

    private double mLongitude;

    public GpsSensor(Context context){
        this.context = context;
        mLocationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
    }

    public Location getLocation(){

        if(Utility.checkLocationPermission(context)){

            Log.i("GPS Sensor","In get Location ");

            // getting GPS status
            isGPSEnabled = mLocationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = mLocationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (isGPSEnabled) {

                Log.i("GPS Sensor","Gps enabled");

                if (mLocationManager != null) {
                    mLocation = mLocationManager
                            .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    Log.i("GPS Sensor","Location "+mLocation);
                }

            }

            if (isNetworkEnabled && mLocation == null) {

                Log.i("GPS Sensor","Network enabled");

                if (mLocationManager != null) {
                    mLocation = mLocationManager
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                    Log.i("GPS Sensor","Location "+mLocation);
                }

            }
        }

        return mLocation;
    }
}
