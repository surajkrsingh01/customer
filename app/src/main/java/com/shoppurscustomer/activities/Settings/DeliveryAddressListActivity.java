package com.shoppurscustomer.activities.Settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.shoppurscustomer.R;
import com.shoppurscustomer.activities.NetworkBaseActivity;
import com.shoppurscustomer.adapters.CouponOfferAdapter;
import com.shoppurscustomer.adapters.DeliveryAddressListAdapter;
import com.shoppurscustomer.interfaces.MyItemClickListener;
import com.shoppurscustomer.models.Coupon;
import com.shoppurscustomer.models.DeliveryAddress;
import com.shoppurscustomer.utilities.ConnectionDetector;
import com.shoppurscustomer.utilities.Constants;
import com.shoppurscustomer.utilities.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeliveryAddressListActivity extends NetworkBaseActivity implements MyItemClickListener {

    private List<DeliveryAddress> itemList;
    private RecyclerView recyclerView;
    private DeliveryAddressListAdapter myItemAdapter;
    private TextView textViewError, text_header;
    private LinearLayout rl_new_DeliveryAddress;

    private String flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_address);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();

    }

    private void init(){
        text_header = findViewById(R.id.text_header);
        text_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DeliveryAddressListActivity.this, SettingActivity.class));
            }
        });
        textViewError = findViewById(R.id.text_error);
        recyclerView=findViewById(R.id.recycler_view);
        rl_new_DeliveryAddress = findViewById(R.id.linear_eidt);
        rl_new_DeliveryAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DeliveryAddressListActivity.this, AddDeliveryAddressActivity.class));
            }
        });

        flag = getIntent().getStringExtra("flag");
        itemList = new ArrayList<>();
        itemList = dbHelper.getallDeliveryAddress(sharedPreferences.getString(Constants.USER_ID,""));
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        myItemAdapter=new DeliveryAddressListAdapter(this,itemList);
        myItemAdapter.setMyItemClickListener(this);
        myItemAdapter.setColorTheme(colorTheme);
        recyclerView.setAdapter(myItemAdapter);

        if(ConnectionDetector.isNetworkAvailable(this)){
            //getCouponOffers();
        }
    }


    private void getCouponOffers(){
        Map<String,String> params=new HashMap<>();
        params.put("dbName",sharedPreferences.getString(Constants.DB_NAME,""));
        params.put("dbUserName",sharedPreferences.getString(Constants.DB_USER_NAME,""));
        params.put("dbPassword",sharedPreferences.getString(Constants.DB_PASSWORD,""));
        String url=getResources().getString(R.string.root_url)+Constants.GET_COUPON_OFFER;
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"offerList");
    }

    @Override
    public void onJsonObjectResponse(JSONObject response, String apiName) {
/*
        try {
            if (apiName.equals("offerList")) {
                if (response.getBoolean("status")) {
                    itemList.clear();
                    JSONArray couponArray = response.getJSONArray("result");
                    JSONObject dataObject = null;
                    Coupon coupon = null;
                    int len = couponArray.length();
                    len = couponArray.length();
                    for (int i = 0; i < len; i++) {
                        dataObject = couponArray.getJSONObject(i);
                        coupon = new Coupon();
                        coupon.setId(dataObject.getInt("id"));
                        coupon.setPercentage(dataObject.getInt("percentage"));
                        coupon.setAmount((float)dataObject.getDouble("amount"));
                        coupon.setName(dataObject.getString("name"));
                        coupon.setStatus(dataObject.getString("status"));
                        coupon.setStartDate(dataObject.getString("startDate"));
                        coupon.setEndDate(dataObject.getString("endDate"));
                        dbHelper.addCouponOffer(coupon, Utility.getTimeStamp(),Utility.getTimeStamp());
                        itemList.add(coupon);
                    }

                    if(couponArray.length() == 0){
                        showNoData(true);
                    }else{
                        showNoData(false);
                        myItemAdapter.notifyDataSetChanged();
                    }

                }
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }*/
    }

    private void showNoData(boolean show){
        if(show){
            recyclerView.setVisibility(View.GONE);
            textViewError.setVisibility(View.VISIBLE);
        }else{
            recyclerView.setVisibility(View.VISIBLE);
            textViewError.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemClicked(int position) {
        DeliveryAddress deliveryAddress = (DeliveryAddress) itemList.get(position);
        Intent intent  = new Intent();
        /*intent.putExtra("name",coupon.getName());
        intent.putExtra("id",coupon.getId());
        intent.putExtra("per",coupon.getPercentage());
        intent.putExtra("amount",coupon.getAmount());
        setResult(-1,intent);
        finish();*/
    }


    public void showAlert(String title, String msg, final String action, final DeliveryAddress deliveryAddress){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(action.equals("delete")){
                            dbHelper.remove_delivery_address(deliveryAddress.getId());
                            updateList();

                        }else if(action.equals("set_default")){
                            deliveryAddress.setIsDefaultAddress("Yes");
                            dbHelper.updateDeliveryAddress(deliveryAddress, sharedPreferences.getString(Constants.USER_ID,""));
                            setDefaultAddressChecked(deliveryAddress.getId());
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        updateList();
                    }
                });
        AlertDialog alert = builder.create();
        alert.setTitle(title);
        alert.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateList();
    }

    public void setDefaultAddressChecked(String id){
        for(DeliveryAddress address: itemList){
            if(!address.getId().equals(id))
                address.setIsDefaultAddress("No");
               dbHelper.updateDeliveryAddress(address, sharedPreferences.getString(Constants.USER_ID,""));
        }
        updateList();
    }

    public void updateList(){
        List<DeliveryAddress> addressList = dbHelper.getallDeliveryAddress(sharedPreferences.getString(Constants.USER_ID,""));
        itemList.clear();
        itemList.addAll(addressList);
        Log.d("itemList size "+itemList.size(), "addressList size "+addressList.size());
        Log.d("myItemAdapter ", ""+myItemAdapter);
        if(myItemAdapter!=null)
            myItemAdapter.notifyDataSetChanged();
    }
}
