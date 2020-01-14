package com.shoppurs.activities;

import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AddressComponent;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.shoppurs.R;
import com.shoppurs.activities.Settings.AddressActivity;
import com.shoppurs.interfaces.OnLocationReceivedListener;
import com.shoppurs.location.GeocodingLocation;
import com.shoppurs.location.GpsLocationProvider;
import com.shoppurs.location.NetworkSensor;
import com.shoppurs.utilities.Constants;
import com.shoppurs.utilities.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class BaseLocation extends NetworkBaseActivity implements OnLocationReceivedListener {

    private GpsLocationProvider locationProvider;
    public LatLng mLatLong;
    public String mAddress;
    private boolean isUiUpdated;
    private final int SEARCH_LOCATION = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void getCurrentLocation(){
        if(Utility.checkLocationPermission(this)){
            locationProvider = new GpsLocationProvider(this, true);
            locationProvider.setOnLocationReceivedListener(this);
            //locationProvider.getlastLocation();
            createLocationRequest();
        }
    }

    public void searchPlaces(){
        Places.initialize(getApplicationContext(), sharedPreferences.getString(Constants.GOOGLE_MAP_API_KEY,""));
        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);
// Set the fields to specify which types of place data to return.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.ADDRESS,
                Place.Field.LAT_LNG,Place.Field.ADDRESS_COMPONENTS);
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN,fields).build(this);
        startActivityForResult(intent, SEARCH_LOCATION);
    }

    public void updateCurrentLocation(String mAddress){
        this.mAddress = mAddress;
        GeocodingLocation locationAddress = new GeocodingLocation();
        locationAddress.getAddressFromLocation(mAddress,
                getApplicationContext(), new BaseLocation.GeocoderHandler());
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    //mAddress = bundle.getString("address");
                    mLatLong = new LatLng(bundle.getDouble("lat"), bundle.getDouble("long"));
                    editor.putString(Constants.CUST_CURRENT_ADDRESS, mAddress);
                    editor.putString(Constants.CUST_CURRENT_LAT, mLatLong.latitude+"");
                    editor.putString(Constants.CUST_CURRENT_LONG, mLatLong.longitude+"");
                    editor.commit();
                    break;
                default:
            }
            Log.d("LatLong from Address ", " "+mLatLong);
            updateUi();
        }
    }

    protected void createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getLocation(true);
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(BaseLocation.this, 1);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }

    private void getLocation(boolean isPermissionGranted){
        if(isPermissionGranted){
            GpsLocationProvider gpsLocationProvider = new GpsLocationProvider(this,true);
            gpsLocationProvider.setOnLocationReceivedListener(this);
            gpsLocationProvider.buildLocationProviderInstance();
            gpsLocationProvider.startLocationUpdates();
        }else{
            NetworkSensor networkSensor = new NetworkSensor(this);
            networkSensor.setOnLocationReceived(this);
            try {
                if (Utility.checkLocationPermission(this)) {
                    networkSensor.getLocation();
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.i("BaseLocation", "Error " + e.getMessage());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    createLocationRequest();
                } else {
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 1 ) {
            if(resultCode == RESULT_OK)
            getLocation(true);
            else updateUi();
        }else if (requestCode == SEARCH_LOCATION) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(intent);
                Log.i(TAG, "Place: " + place.getName()
                        + ", " + place.getId()+ ", " + place.getAddress());
                List<AddressComponent> componentList = place.getAddressComponents().asList();

                mAddress = place.getAddress();
                mLatLong = place.getLatLng();
                editor.putString(Constants.CUST_CURRENT_ADDRESS, mAddress);
                editor.putString(Constants.CUST_CURRENT_LAT, mLatLong.latitude+"");
                editor.putString(Constants.CUST_CURRENT_LONG, mLatLong.longitude+"");
                editor.commit();

                updateUi();

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(intent);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
                updateUi();
            }
        }
    }

    @Override
    public void onLocationReceived(Location location) {
        Log.i(TAG,"Location is received. "+location);
        if(location!=null) {
            mLatLong = new LatLng(location.getLatitude(), location.getLongitude());
            setLocationComponents();
        }
    }

    private void setLocationComponents(){
        Log.i(TAG,"Getting address components...");
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(mLatLong.latitude, mLatLong.longitude, 1);
        } catch (IOException ioException) {
            Log.e(TAG, ioException.getMessage());
        } catch (IllegalArgumentException illegalArgumentException) {
            Log.e(TAG, illegalArgumentException.getMessage());
        }
        if(addresses!=null && addresses.size() > 0){
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();
            for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
            mAddress = address.getAddressLine(0);
            editor.putString(Constants.CUST_CURRENT_ADDRESS, mAddress);
            editor.putString(Constants.CUST_CURRENT_LAT, mLatLong.latitude+"");
            editor.putString(Constants.CUST_CURRENT_LONG, mLatLong.longitude+"");

            editor.commit();
           /*if(!isUiUpdated){
               isUiUpdated = true;*/
                updateUi();
            //}

        }
        else{
            Log.e(TAG, "There is some problem in fetching address.");
            updateUi();
        }
    }

    public void updateUi(){

    }
}
