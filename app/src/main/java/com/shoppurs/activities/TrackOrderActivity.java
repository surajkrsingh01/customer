package com.shoppurs.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.shoppurs.R;
import com.shoppurs.models.DeliveryAddress;
import com.shoppurs.models.ShoppursPartner;
import com.shoppurs.utilities.Constants;
import com.shoppurs.utilities.DialogAndToast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TrackOrderActivity extends NetworkBaseActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private final float ZOOM = 15f;

    private LatLng shopLatLng,custLatLng,orderLatLng;
    private String shopName,shopAddress,shopMobile,custName,
            custAddress,custMobile,orderAddress;

    private TextView tvPartnerName,tvPartnerMobile;
    private ShoppursPartner partner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_order);

        init();
    }

    private void init(){
        partner = (ShoppursPartner)getIntent().getSerializableExtra("partner");
        tvPartnerName = findViewById(R.id.tvPartnerName);
        tvPartnerMobile = findViewById(R.id.tvPartnerMobile);

        tvPartnerName.setText(partner.getName());
        tvPartnerMobile.setText(partner.getMobile());

        double latitude =  Double.parseDouble(sharedPreferences.getString(Constants.USER_LAT,""));
        double longitude = Double.parseDouble(sharedPreferences.getString(Constants.USER_LONG,""));
        if(latitude != 0d){
            custLatLng = new LatLng(latitude,longitude);
        }

        latitude = Double.parseDouble(partner.getLatitude());
        longitude = Double.parseDouble(partner.getLongitude());
        if(latitude != 0d){
            orderLatLng = new LatLng(latitude,longitude);
        }

        latitude = Double.parseDouble(getIntent().getStringExtra("shopLat"));
        longitude = Double.parseDouble(getIntent().getStringExtra("shopLong"));
        if(latitude != 0d){
            shopLatLng = new LatLng(latitude,longitude);
        }

        shopName = getIntent().getStringExtra("shopName");
        shopMobile = getIntent().getStringExtra("shopMobile");
        shopAddress = getIntent().getStringExtra("shopAddress");

        DeliveryAddress deliveryAddress = dbHelper.getdefaultDeliveryAddress(sharedPreferences.getString(Constants.USER_ID,""));
        custName = sharedPreferences.getString(Constants.FULL_NAME,"");
        custMobile = sharedPreferences.getString(Constants.MOBILE_NO,"");
        custAddress = deliveryAddress.getAddress();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        TextView text_header_1 = findViewById(R.id.text_header_1);
        TextView text_header_1_2 = findViewById(R.id.text_header_1_2);

        text_header_1_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        text_header_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrackOrderActivity.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (googleMap == null) {
            DialogAndToast.showToast("Map is not available...", TrackOrderActivity.this);
        }
        mMap.getUiSettings().setZoomControlsEnabled(false); // true to enable
        mMap.setTrafficEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                LinearLayout info = new LinearLayout(TrackOrderActivity.this);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(TrackOrderActivity.this);
                title.setTextColor(getResources().getColor(R.color.primary_text_color));
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());
                info.addView(title);

                if(!TextUtils.isEmpty(marker.getSnippet())){
                    TextView snippet = new TextView(TrackOrderActivity.this);
                    snippet.setTextColor(getResources().getColor(R.color.secondary_text_color));
                    snippet.setGravity(Gravity.CENTER);
                    snippet.setText(marker.getSnippet());
                    info.addView(snippet);
                }
                return info;
            }
        });

        showProgress(true);
        FetchAddress fetchAddress = new FetchAddress();
        // Start downloading json data from Google Directions API
        fetchAddress.execute("");
    }


    private void trackOrder(){
        MarkerOptions options = new MarkerOptions();
        ArrayList<MarkerOptions> latlngs = new ArrayList<>();

        options.position(shopLatLng);
        options.title(shopName);
        options.snippet(shopAddress+"/n"+shopMobile);
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mMap.addMarker(options);

        options.position(orderLatLng);
        options.title("Order Location");
        options.snippet(orderAddress);
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mMap.addMarker(options);

        options.position(custLatLng);
        options.title(custName);
        options.snippet(custAddress+"/n"+custMobile);
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mMap.addMarker(options);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(shopLatLng);
        builder.include(custLatLng);

        LatLngBounds bounds = builder.build();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.30); // offset from edges of the map 10% of screen
        //int padding = 40; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height,padding);
        // mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 70));
        mMap.moveCamera(cu);
    }


    // Fetches data from url passed
    private class FetchAddress extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            try {
                Geocoder geocoder = new Geocoder(TrackOrderActivity.this, Locale.getDefault());
                String errorMessage = "";

                List<Address> addresses = null;

                try {
                    addresses = geocoder.getFromLocation(
                            orderLatLng.latitude,
                            orderLatLng.longitude,
                            // In this sample, get just a single address.
                            1);
                    //showToast("LocationAddress is gotten..");
                } catch (IOException ioException) {
                    // Catch network or other I/O problems.
                    errorMessage = getResources().getString(R.string.service_not_available)+ioException.toString();
                    //  Log.e(TAG, errorMessage, ioException);
                } catch (IllegalArgumentException illegalArgumentException) {
                    // Catch invalid latitude or longitude values.
                    errorMessage = getResources().getString(R.string.invalid_lat_long_used);
                }

                // Handle case where no address was found.
                if (addresses == null || addresses.size() == 0) {
                    if (errorMessage.isEmpty()) {
                        errorMessage = getResources().getString(R.string.no_address_found);
                        Log.d(TAG, "Address no found "+errorMessage);
                    }
                    orderAddress = "";
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
                    orderAddress = address.getAddressLine(0);

                }

            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            showProgress(false);
            trackOrder();
        }
    }
}
