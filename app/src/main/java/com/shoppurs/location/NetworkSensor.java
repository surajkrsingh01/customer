package com.shoppurs.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import android.util.Log;


import com.shoppurs.interfaces.OnLocationReceivedListener;

import static android.content.Context.LOCATION_SERVICE;

public class NetworkSensor implements LocationListener {

    private Context mContext;

    // flag for GPS status
    private boolean isGPSEnabled = false;

    // flag for network status
    private boolean isNetworkEnabled = false;

    private Location mLocation;

    private double mLatitude;

    private double mLongitude;

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 5;

    private LocationManager mLocationManager;

    private OnLocationReceivedListener onLocationReceived;

    public NetworkSensor setOnLocationReceived(OnLocationReceivedListener onLocationReceived) {
        this.onLocationReceived = onLocationReceived;
        return this;
    }

    public NetworkSensor() {

    }

    public NetworkSensor(Context context) {
        mContext = context;
    }

    public void getLocation() throws Exception {
        mLocationManager = (LocationManager) mContext
                .getSystemService(LOCATION_SERVICE);

        // getting GPS status
        isGPSEnabled = mLocationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        isNetworkEnabled = mLocationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (isNetworkEnabled && mLocation == null) {

            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

            if (mLocationManager != null) {
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mLocation = mLocationManager
                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (mLocation != null) {
                    mLatitude = mLocation.getLatitude();
                    mLongitude = mLocation.getLongitude();
                }
            }

        }
    }

    @Override
    public void onLocationChanged(Location location) {
        onLocationReceived.onLocationReceived(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.i("Network","Changed Status "+status+" "+provider);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.i("Network","Enabled "+" "+provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.i("Network","Disabled  "+provider);
    }
}
