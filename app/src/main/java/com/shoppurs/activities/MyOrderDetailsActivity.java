package com.shoppurs.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.shoppurs.R;
import com.shoppurs.activities.Settings.SettingActivity;
import com.shoppurs.adapters.MyOrderDetailsAdapter;
import com.shoppurs.models.AssignedOrder;
import com.shoppurs.models.MyOrderDetail;
import com.shoppurs.models.MyProduct;
import com.shoppurs.models.ShoppursPartner;
import com.shoppurs.utilities.Constants;
import com.shoppurs.utilities.DialogAndToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by suraj kumar singh on 05-04-2019.
 */

public class MyOrderDetailsActivity extends NetworkBaseActivity{


    private RecyclerView recycler_order;
    private MyOrderDetailsAdapter myOrderDetailsAdapter;
    private List<MyOrderDetail> myOrderDetailslist;
    private String dbName, dbUserName, dbPassword,custId;
    private final String TAG = "MyOrderDetailsActivity";
    private String orderNumber,orderId;
    private String orderDate, orderStatus, shopName;
    private double orderAmount;
    private TextView tv_shopName, tv_footer, tv_order_id, tv_date, tv_amount, textView2, textView4;
    private RelativeLayout rl_footer_action;
    private ImageView imageView2,imageView4;
    private View view1, view2;
    private TextView text_left_label, text_right_label;
    private LinearLayout linearTrackOrder;
    private String partnerOrderId;
    private ShoppursPartner partner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        partnerOrderId = getIntent().getStringExtra("partnerOrderId");
        Log.d("partnerOrderId ", partnerOrderId);
        orderStatus = getIntent().getStringExtra("orderStatus");
        shopName = getIntent().getStringExtra("shopName");
        orderNumber = getIntent().getStringExtra("orderNumber");
        orderId = getIntent().getStringExtra("orderId" );
        orderDate = getIntent().getStringExtra("orderDate");
        orderAmount = getIntent().getDoubleExtra("orderAmount", 0.0d);
        dbName = sharedPreferences.getString(Constants.DB_NAME,"");
        dbUserName = sharedPreferences.getString(Constants.DB_USER_NAME,"");
        dbPassword = sharedPreferences.getString(Constants.DB_PASSWORD,"");
        custId = sharedPreferences.getString(Constants.USER_ID,"");

        tv_shopName = findViewById(R.id.tv_shopName);
         tv_order_id = findViewById(R.id.tv_order_id);
         tv_date = findViewById(R.id.tv_date);
         tv_amount = findViewById(R.id.text_total_amount);
         imageView2 = findViewById(R.id.image_2);
         imageView4 = findViewById(R.id.image_4);
         textView2  = findViewById(R.id.text_2);
         textView4 = findViewById(R.id.text_4);
         view1 = findViewById(R.id.view_status_1);
         view2 = findViewById(R.id.view_status_2);

        tv_shopName.setText(shopName);
        tv_order_id.setText("Order Number - " +String.valueOf(orderNumber));
        tv_date.setText(orderDate);
        tv_amount.setText(""+String.valueOf(orderAmount));

        myOrderDetailslist = new ArrayList<>();
        recycler_order = (RecyclerView) findViewById(R.id.recycler_order);
        recycler_order.setHasFixedSize(true);
        recycler_order.setRecycledViewPool(new RecyclerView.RecycledViewPool());
        recycler_order.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recycler_order.getRecycledViewPool().setMaxRecycledViews(0, 0);
        myOrderDetailsAdapter = new MyOrderDetailsAdapter(MyOrderDetailsActivity.this,myOrderDetailslist );
        recycler_order.setAdapter(myOrderDetailsAdapter);
        getOrderDetails();

        if(!partnerOrderId.equals("0"))
            assignStatus();

        tv_footer = findViewById(R.id.text_action);
        tv_footer.setText("View Invoice");
        rl_footer_action = findViewById(R.id.relative_footer_action);
        rl_footer_action.setBackgroundColor(colorTheme);
        rl_footer_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(tv_footer.getText().toString()) && tv_footer.getText().toString().equals("Back"))
                    finish();
                else {
                    Intent intent = new Intent(MyOrderDetailsActivity.this, InvoiceActivity.class);
                    intent.putExtra("orderNumber", orderNumber);
                    startActivity(intent);
                }
            }
        });
        setTrackStatus(orderStatus);
        text_left_label = findViewById(R.id.text_left_label);
        text_right_label = findViewById(R.id.text_right_label);
        text_left_label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyOrderDetailsActivity.this, SettingActivity.class));
                finish();
            }
        });
        text_right_label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyOrderDetailsActivity.this, MyOrderActivity.class));
                finish();
            }
        });

        linearTrackOrder = findViewById(R.id.linear_bottom);
        linearTrackOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyOrderDetailsActivity.this,TrackOrderActivity.class);
                intent.putExtra("partner",partner);
                intent.putExtra("shopLat",getIntent().getStringExtra("shopLat"));
                intent.putExtra("shopLong",getIntent().getStringExtra("shopLong"));
                intent.putExtra("shopName",getIntent().getStringExtra("shopName"));
                intent.putExtra("shopAddress",getIntent().getStringExtra("shopAddress"));
                intent.putExtra("shopMobile",getIntent().getStringExtra("shopMobile"));
                startActivity(intent);
            }
        });
    }

    private void assignStatus(){
        Map<String,String> params=new HashMap<>();
        params.put("orderId", partnerOrderId);
        params.put("shopCode","");
        String url=getResources().getString(R.string.url_partner)+Constants.ASSIGN_STATUS;
        Log.d("params ", params.toString());
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"assignStatus");
    }

    public void getOrderDetails(){
        Map<String,String> params=new HashMap<>();
        //params.put("action","4");
        params.put("id",String.valueOf(orderId));
        params.put("dbName",dbName);
        params.put("dbUserName",dbUserName);
        params.put("dbPassword",dbPassword);
        String url=getResources().getString(R.string.url_customer)+"/api/order/get_order_details";
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"getOrders");
    }

    @Override
    public void onJsonObjectResponse(JSONObject response, String apiName) {
        showProgress(false);
        try {
            Log.d(TAG, response.toString());
            if(apiName.equals("getOrders")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)){
                    JSONArray jsonArray = response.getJSONArray("result");
                    for(int i=0;i<jsonArray.length();i++){
                        MyOrderDetail myOrderDetail = new MyOrderDetail();
                        MyProduct productItem = new MyProduct();
                       // myOrderDetail.setShopName(jsonArray.getJSONObject(i).getString("shopName"));
                        myOrderDetail.setShopName("My Shop");
                        myOrderDetail.setStatus(jsonArray.getJSONObject(i).getString("status"));
                        //myOrderDetail.setStatus("Pending");
                        productItem.setName(jsonArray.getJSONObject(i).getString("prodName"));
                        productItem.setQuantity(jsonArray.getJSONObject(i).getInt("qty"));

                        productItem.setProdImage1(jsonArray.getJSONObject(i).getString("prodImage1"));
                        productItem.setProdImage2(jsonArray.getJSONObject(i).getString("prodImage2"));
                        productItem.setProdImage3(jsonArray.getJSONObject(i).getString("prodImage3"));
                        productItem.setMrp(Float.parseFloat(jsonArray.getJSONObject(i).getString("prodMrp")));
                        productItem.setSellingPrice(Float.parseFloat(jsonArray.getJSONObject(i).getString("prodSp")));

                        myOrderDetail.setMyProduct(productItem);
                        myOrderDetailslist.add(myOrderDetail);
                    }
                    if(myOrderDetailslist.size()>0){
                        myOrderDetailsAdapter.notifyDataSetChanged();
                    }else {
                        //showNoData(true);
                    }

                }else {
                    DialogAndToast.showDialog(response.getString("message"),MyOrderDetailsActivity.this);
                }
            }else if (apiName.equals("assignStatus")) {
                if (response.getBoolean("status")) {
                    int responseCode = response.getInt("statusCode");
                    if(responseCode == 1){
                        Gson gson = new Gson();
                        partner = gson.fromJson(response.getString("result"), ShoppursPartner.class);
                        AssignedOrder order  = gson.fromJson(response.getJSONObject("result").getString("deliveryOrder"), AssignedOrder.class);
                        partner.setAssignedOrder(order);
                        linearTrackOrder.setVisibility(View.VISIBLE);
                       // buttonOrderDelivered.setVisibility(View.GONE);
                        //rlPartnerDetails.setVisibility(View.VISIBLE);
                        //tvPartnerName.setText(partner.getName());
                        //tvPartnerMobile.setText(partner.getMobile());
                    }else if(responseCode == 3){
                        //progressBar.setVisibility(View.GONE);
                        //btnAssign.setVisibility(View.GONE);
                        linearTrackOrder.setVisibility(View.GONE);
                        // buttonOrderDelivered.setVisibility(View.VISIBLE);
                        //tvLabel1.setText("Partner has delivered the order");
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            DialogAndToast.showToast(getResources().getString(R.string.json_parser_error)+e.toString(),MyOrderDetailsActivity.this);
        }
    }

    private void setTrackStatus(String mode){
        if(mode.toLowerCase().equals("accepted")){
            view1.setBackgroundColor(getResources().getColor(R.color.green700));
            textView2.setTextColor(getResources().getColor(R.color.green700));
            imageView2.setBackgroundResource(R.drawable.accent_color_4_circle_background);
            tv_footer.setText("Back");
        }else if(mode.toLowerCase().equals("delivered")){
            view1.setBackgroundColor(getResources().getColor(R.color.green700));
            textView2.setTextColor(getResources().getColor(R.color.green700));
            imageView2.setBackgroundResource(R.drawable.accent_color_4_circle_background);

            view2.setBackgroundColor(getResources().getColor(R.color.green700));
            textView4.setTextColor(getResources().getColor(R.color.green700));
            imageView4.setBackgroundResource(R.drawable.accent_color_4_circle_background);
            rl_footer_action.setVisibility(View.VISIBLE);
        }else if(mode.toLowerCase().equals("cancelled")){
            view1.setBackgroundColor(getResources().getColor(R.color.green700));
            textView2.setTextColor(getResources().getColor(R.color.green700));
            imageView2.setBackgroundResource(R.drawable.accent_color_4_circle_background);
            textView2.setText("Cancelled");
            tv_footer.setText("Back");
        }else if(mode.toLowerCase().equals("pending")){
            tv_footer.setText("Back");
        }
    }
}