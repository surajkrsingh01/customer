package com.shoppurs.activities.Settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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
import com.shoppurs.activities.NetworkBaseActivity;
import com.shoppurs.interfaces.OnLocationReceivedListener;
import com.shoppurs.location.GeocodingLocation;
import com.shoppurs.location.GpsLocationProvider;
import com.shoppurs.location.NetworkSensor;
import com.shoppurs.models.SpinnerItem;
import com.shoppurs.utilities.Constants;
import com.shoppurs.utilities.DialogAndToast;
import com.shoppurs.utilities.Utility;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddressActivity extends NetworkBaseActivity implements OnMapReadyCallback, OnLocationReceivedListener {
    private final float ZOOM = 15f;
    private final int SEARCH_LOCATION = 3;

    private GoogleMap mMap;
    private Marker marker;
    private LatLng shopLatLng;

    private EditText editAddress, editLocality, editCountry,editState,editCity,editPincode;
    private String country,state,city,pin;
    private TextView tv_parent, tv_top_parent;
    private ImageView ivSearch;

    private Button btnGetLocation;
    private boolean isFirstTime = true;
    private String flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
    }

    private void init(){

        flag = getIntent().getStringExtra("flag");

        // Initialize Places.
        Places.initialize(getApplicationContext(), sharedPreferences.getString(Constants.GOOGLE_MAP_API_KEY,""));

        double latitude = 0, longitude = 0;
        String cl = sharedPreferences.getString(Constants.CUST_LAT,"0.0");
        Log.d("c1 ", cl);
        if(TextUtils.isEmpty(cl) || cl.equals("null")){

        }else {
            latitude = Double.parseDouble(sharedPreferences.getString(Constants.CUST_LAT,"0.0"));
            longitude = Double.parseDouble(sharedPreferences.getString(Constants.CUST_LONG,"0.0"));
        }


        Log.i(TAG,"lat "+latitude+" long "+longitude);

        if(latitude != 0d){
            shopLatLng = new LatLng(latitude,longitude);
        }

        btnGetLocation = findViewById(R.id.btn_get_location);
        //int colorTheme = sharedPreferences.getInt(Constants.COLOR_THEME,0);
        RelativeLayout btnUpdate = findViewById(R.id.relative_footer_action);
        btnUpdate.setBackgroundColor(colorTheme);

        if(colorTheme == getResources().getColor(R.color.white)){
            btnGetLocation.setTextColor(getResources().getColor(R.color.primary_text_color));
        }
        btnGetLocation.setBackgroundColor(colorTheme);

        ivSearch = findViewById(R.id.image_search);
        editAddress = findViewById(R.id.edit_address);
        editLocality = findViewById(R.id.edit_locality);
        editCountry = findViewById(R.id.edit_country);
        editState = findViewById(R.id.edit_state);
        editCity = findViewById(R.id.edit_city);
        editPincode = findViewById(R.id.edit_pincode);
        editAddress.setText(sharedPreferences.getString(Constants.CUST_ADDRESS,""));
        editLocality.setText(sharedPreferences.getString(Constants.CUST_LOCALITY, ""));
        editCountry.setText(sharedPreferences.getString(Constants.CUST_COUNTRY,""));
        editState.setText(sharedPreferences.getString(Constants.CUST_STATE,""));
        editCity.setText(sharedPreferences.getString(Constants.CUST_CITY,""));
        editPincode.setText(sharedPreferences.getString(Constants.CUST_PINCODE,""));
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Utility.checkLocationPermission(AddressActivity.this)){
                    createLocationRequest();
                }
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               showAlert("Customer Address", "Do you want to update Address?");
            }
        });

        tv_top_parent = findViewById(R.id.text_left_label);
        tv_parent = findViewById(R.id.text_right_label);

        if(flag != null){
            tv_top_parent.setText("Back");
            TextView tv = findViewById(R.id.text_action);
            tv.setText("Continue");
            tv_parent.setVisibility(View.GONE);
        }

        tv_top_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag != null){

                }else{
                    startActivity(new Intent(AddressActivity.this, SettingActivity.class));
                }
                finish();
            }
        });
        tv_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddressActivity.this, PersonalProfileActivity.class));
                finish();
            }
        });

        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchPlaces();
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (googleMap == null) {
            DialogAndToast.showToast("Map is not available...",AddressActivity.this);
        }
        // Add a marker in Sydney and move the camera
        // LatLng sydney = new LatLng(-34, 151);
        // mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //  mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        //   googleMap.setMyLocationEnabled(true); // false to disable
        mMap.getUiSettings().setZoomControlsEnabled(false); // true to enable
        mMap.setTrafficEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if(shopLatLng != null){
            updateMarker(shopLatLng);
        }else{
            if(Utility.checkLocationPermission(AddressActivity.this)){
                createLocationRequest();
            }
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // openDialog();
            }
        });
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
                        resolvable.startResolutionForResult(AddressActivity.this, 1);
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
                Log.i("MainActivity", "Error " + e.getMessage());
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    getLocation(true);
                case Activity.RESULT_CANCELED:
                    break;
            }
        } else if (requestCode == SEARCH_LOCATION) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getName()
                        + ", " + place.getId() + ", " + place.getAddress());
                List<AddressComponent> componentList = place.getAddressComponents().asList();
                for (AddressComponent component : componentList) {
                    Log.i(TAG, "component " + component.getName());
                    List<String> typeList = component.getTypes();
                    for (String type : typeList) {
                        Log.i(TAG, "type " + type);
                        if (type.equals("country")) {
                            country = component.getName();
                            editCountry.setText(country);
                        } else if (type.equals("administrative_area_level_1")) {
                            state = component.getName();
                            editState.setText(state);
                        } else if (type.equals("locality") || type.equals("neighborhood")) {
                            city = component.getName();
                            editCity.setText(city);
                        } else if (type.equals("postal_code")) {
                            pin = component.getName();
                            editPincode.setText(pin);
                        }
                    }
                }
                editAddress.setText(place.getAddress());
                shopLatLng = place.getLatLng();
                updateMarker(shopLatLng);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

    }

    @Override
    public void onLocationReceived(Location location) {
        Log.i(TAG,"Location is received.");
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        shopLatLng = latLng;
        setLocationComponents();
        updateMarker(latLng);
    }

    private void setLocationComponents(){
        Log.i(TAG,"Getting address components...");
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(shopLatLng.latitude, shopLatLng.longitude, 1);
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
            editAddress.setText(address.getAddressLine(0));
            if(address.getCountryName() != null)
                editCountry.setText(address.getCountryName());
            if(address.getAdminArea() != null)
                editState.setText(address.getAdminArea());
            if(address.getLocality() != null)
                editCity.setText(address.getLocality());
            if(address.getPostalCode() != null)
                editPincode.setText(address.getPostalCode());
        }else{
            Log.e(TAG, "There is some problem in fetching address.");
        }
    }

    private void updateMarker(LatLng latLng){
        if(marker == null){
            marker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    .title("My Location"));

            mMap.getUiSettings().setZoomControlsEnabled(false); // true to enable
            mMap.setTrafficEnabled(true);
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        }else{
            marker.setPosition(latLng);
        }

        if(mMap.getCameraPosition().zoom < ZOOM){
            // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)      // Sets the center of the map to Mountain View
                    .zoom(ZOOM)                   // Sets the zoom
                    // .bearing(90)                // Sets the orientation of the camera to east
                    // .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }else {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }
    }

    public void searchPlaces(){

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);
// Set the fields to specify which types of place data to return.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.ADDRESS,
                Place.Field.LAT_LNG,Place.Field.ADDRESS_COMPONENTS);


        Intent intent =
                new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN,fields)
                        .build(this);
        startActivityForResult(intent, SEARCH_LOCATION);

    }

    private void getCountries() {
        String url = getResources().getString(R.string.url_customer) + Constants.GET_COUNTRIES;
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST, url, new JSONObject(), "countries");
    }

    private void getStates(String countryId) {
        String url = getResources().getString(R.string.url_customer) + Constants.GET_STATES + countryId;
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST, url, new JSONObject(), "states");
    }

    private void getCities(String stateId) {
        String url = getResources().getString(R.string.url_customer) + Constants.GET_CITIES + stateId;
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST, url, new JSONObject(), "cities");
    }

    String address,locality;
    private void validateFields(){
        address = editAddress.getText().toString();
        locality = editLocality.getText().toString();
        pin = editPincode.getText().toString();
        country = editCountry.getText().toString();
        state = editState.getText().toString();
        city = editCity.getText().toString();


        if (TextUtils.isEmpty(address)) {
            DialogAndToast.showDialog(getResources().getString(R.string.address_required),this);
            return;
        }
        if(TextUtils.isEmpty(locality)){
            DialogAndToast.showDialog("Please Enter Locality",this);
            return;
        }

        if (TextUtils.isEmpty(pin)) {
            DialogAndToast.showDialog(getResources().getString(R.string.pincode_required),this);
            return;
        }

        if (TextUtils.isEmpty(state)) {
            DialogAndToast.showDialog("Please enter your state", this);
            return;
        }
        if (TextUtils.isEmpty(city)) {
            DialogAndToast.showDialog("Please enter your city", this);
            return;
        }

        GeocodingLocation locationAddress = new GeocodingLocation();
        locationAddress.getAddressFromLocation(address,
                getApplicationContext(), new GeocoderHandler());

    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    shopLatLng = new LatLng(bundle.getDouble("lat"), bundle.getDouble("long"));

                    break;
                default:
                    locationAddress = null;
            }
            //Log.d("LatLong from Address ", locationAddress);
            Log.d("LatLong from Address ", " "+shopLatLng);
            updateAddressAfterGettingLatLong();
            //latLongTV.setText(locationAddress);
        }
    }

    private void updateAddressAfterGettingLatLong(){
        if(flag != null){

            Intent intent = new Intent();
            intent.putExtra("address",address);
            intent.putExtra("country",country);
            intent.putExtra("state",state);
            intent.putExtra("city",city);
            intent.putExtra("pin",pin);
            intent.putExtra("latitude",shopLatLng.latitude);
            intent.putExtra("longitude",shopLatLng.longitude);
            setResult(-1,intent);
            finish();

        }else{
            Map<String,String> params=new HashMap<>();
            params.put("address",address);
            params.put("locality", locality);
            params.put("country",country);
            params.put("state",state);
            params.put("city",city);
            params.put("pinCode",pin);
            if(shopLatLng == null){
                params.put("latitude","0.0");
                params.put("longitude","0.0");
            }else{
                params.put("latitude",""+shopLatLng.latitude);
                params.put("longitude",""+shopLatLng.longitude);
            }

            params.put("id","0");
            params.put("mobile",sharedPreferences.getString(Constants.MOBILE_NO,""));
            params.put("dbName",sharedPreferences.getString(Constants.DB_NAME,""));
            params.put("dbUserName",sharedPreferences.getString(Constants.DB_USER_NAME,""));
            params.put("dbPassword",sharedPreferences.getString(Constants.DB_PASSWORD,""));
            JSONArray dataArray = new JSONArray();
            JSONObject dataObject = new JSONObject(params);
            dataArray.put(dataObject);
            String url=getResources().getString(R.string.url_customer)+Constants.UPDATE_ADDRESS;
            showProgress(true);
            jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"updateAddress");
        }
    }

    @Override
    public void onJsonObjectResponse(JSONObject response, String apiName) {

        try {
            if (apiName.equals("countries")) {

                if (response.getBoolean("status")) {
                    JSONArray dataArray = response.getJSONArray("result");
                    JSONObject jsonObject = null;
                    SpinnerItem item = null;
                    int len = dataArray.length();
                   /* countryListObject.clear();
                    countryList.clear();

                    for (int i = 0; i < len; i++) {
                        jsonObject = dataArray.getJSONObject(i);
                        item = new SpinnerItem();
                        item.setId("" + jsonObject.getInt("id"));
                        item.setName(jsonObject.getString("name"));
                        countryListObject.add(item);
                        countryList.add(item.getName());
                    }
                    countryList.add(0, "Select Country");
                    countryListObject.add(0, new SpinnerItem());
                    countryAdapter.notifyDataSetChanged();

                    if (countryList.size() == 2) {
                        spinnerCountry.setSelection(1);
                    }*/

                } else {
                    DialogAndToast.showDialog(response.getString("message"), this);
                }
            } else if (apiName.equals("states")) {

                if (response.getBoolean("status")) {
                    JSONArray dataArray = response.getJSONArray("result");
                    JSONObject jsonObject = null;
                    SpinnerItem item = null;
                    int len = dataArray.length();
                   /* stateListObject.clear();
                    stateList.clear();

                    for (int i = 0; i < len; i++) {
                        jsonObject = dataArray.getJSONObject(i);
                        item = new SpinnerItem();
                        item.setId("" + jsonObject.getInt("id"));
                        item.setName(jsonObject.getString("name"));
                        stateListObject.add(item);
                        stateList.add(item.getName());
                    }
                    stateList.add(0, "Select State");
                    stateListObject.add(0, new SpinnerItem());
                    stateAdapter.notifyDataSetChanged();

                    if(isFirstTime){
                        setStateValue();
                    }*/

                } else {
                    DialogAndToast.showDialog(response.getString("message"), this);
                }
            }else if (apiName.equals("cities")) {

                if (response.getBoolean("status")) {
                    JSONArray dataArray = response.getJSONArray("result");
                    JSONObject jsonObject = null;
                    SpinnerItem item = null;
                    int len = dataArray.length();
                  /*  cityListObject.clear();
                    cityList.clear();

                    for (int i = 0; i < len; i++) {
                        jsonObject = dataArray.getJSONObject(i);
                        item = new SpinnerItem();
                        item.setId("" + jsonObject.getInt("id"));
                        item.setName(jsonObject.getString("name"));
                        cityListObject.add(item);
                        cityList.add(item.getName());
                    }
                    cityList.add(0, "Select City");
                    cityListObject.add(0, new SpinnerItem());
                    cityAdapter.notifyDataSetChanged();

                    if(isFirstTime){
                        setCityValue();
                    }*/

                } else {
                    DialogAndToast.showDialog(response.getString("message"), this);
                }
            }else {
                if(response.getBoolean("status")){
                    editor.putString(Constants.CUST_ADDRESS,editAddress.getText().toString());
                    editor.putString(Constants.CUST_PINCODE,editPincode.getText().toString());
                    editor.putString(Constants.CUST_COUNTRY,editCountry.getText().toString());
                    editor.putString(Constants.CUST_STATE,editState.getText().toString());
                    editor.putString(Constants.CUST_CITY,editCity.getText().toString());
                    editor.putString(Constants.CUST_LOCALITY,editLocality.getText().toString());
                    if(shopLatLng != null){
                        editor.putString(Constants.CUST_LAT,""+shopLatLng.latitude);
                        editor.putString(Constants.CUST_LONG,""+shopLatLng.longitude);
                    }
                    editor.commit();
                    DialogAndToast.showToast(response.getString("message"),this);
                    finish();
                }else{
                    DialogAndToast.showDialog(response.getString("message"),this);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void openDialog(){
        DialogAndToast.showDialog("Get location",this);
    }

    public void showAlert(String title, String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        validateFields();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.setTitle(title);
        alert.show();
    }

}
