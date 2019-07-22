package com.shoppurscustomer.activities;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.shoppurscustomer.R;
import com.shoppurscustomer.adapters.MyOrderDetailsAdapter;
import com.shoppurscustomer.models.MyOrderDetail;
import com.shoppurscustomer.models.MyProduct;
import com.shoppurscustomer.utilities.Constants;
import com.shoppurscustomer.utilities.DialogAndToast;

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
    private int orderId;
    private String orderDate, orderStatus;
    private double orderAmount;
    private TextView tv_order_id, tv_date, tv_amount, textView2, textView4;
    private ImageView imageView2,imageView4;
    private View view1, view2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        orderStatus = getIntent().getStringExtra("orderStatus");
        orderId = getIntent().getIntExtra("orderId",0);
        orderDate = getIntent().getStringExtra("orderDate");
        orderAmount = getIntent().getDoubleExtra("orderAmount", 0.0d);
        dbName = sharedPreferences.getString(Constants.DB_NAME,"");
        dbUserName = sharedPreferences.getString(Constants.DB_USER_NAME,"");
        dbPassword = sharedPreferences.getString(Constants.DB_PASSWORD,"");
        custId = sharedPreferences.getString(Constants.USER_ID,"");

         tv_order_id = findViewById(R.id.tv_order_id);
         tv_date = findViewById(R.id.tv_date);
         tv_amount = findViewById(R.id.tv_amount);
         imageView2 = findViewById(R.id.image_2);
         imageView4 = findViewById(R.id.image_4);
         textView2  = findViewById(R.id.text_2);
         textView4 = findViewById(R.id.text_4);
         view1 = findViewById(R.id.view_status_1);
         view2 = findViewById(R.id.view_status_2);

        tv_order_id.setText("Order Id - " +String.valueOf(orderId));
        tv_date.setText(orderDate);
        tv_amount.setText("Total Amount "+String.valueOf(orderAmount));

        myOrderDetailslist = new ArrayList<>();
        recycler_order = (RecyclerView) findViewById(R.id.recycler_order);
        recycler_order.setHasFixedSize(true);
        recycler_order.setRecycledViewPool(new RecyclerView.RecycledViewPool());
        recycler_order.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recycler_order.getRecycledViewPool().setMaxRecycledViews(0, 0);
        myOrderDetailsAdapter = new MyOrderDetailsAdapter(MyOrderDetailsActivity.this,myOrderDetailslist );
        recycler_order.setAdapter(myOrderDetailsAdapter);
        setTrackStatus(orderStatus);
        getOrderDetails();
        initFooter(this, 4);
    }

    public void getOrderDetails(){
        Map<String,String> params=new HashMap<>();
        //params.put("action","4");
        params.put("id",String.valueOf(orderId));
        params.put("dbName",dbName);
        params.put("dbUserName",dbUserName);
        params.put("dbPassword",dbPassword);
        String url=getResources().getString(R.string.root_url)+"order/get_order_details";
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
                        MyProduct myProduct = new MyProduct();
                        myOrderDetail.setShopName(jsonArray.getJSONObject(i).getString("shopName"));
                        myOrderDetail.setStatus(jsonArray.getJSONObject(i).getString("status"));
                        //myOrderDetail.setStatus("Pending");
                        myProduct.setName(jsonArray.getJSONObject(i).getString("prodName"));
                        myProduct.setQuantity(jsonArray.getJSONObject(i).getInt("qty"));
                        myOrderDetail.setMyProduct(myProduct);
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
        }else if(mode.toLowerCase().equals("delivered")){
            view1.setBackgroundColor(getResources().getColor(R.color.green700));
            textView2.setTextColor(getResources().getColor(R.color.green700));
            imageView2.setBackgroundResource(R.drawable.accent_color_4_circle_background);

            view2.setBackgroundColor(getResources().getColor(R.color.green700));
            textView4.setTextColor(getResources().getColor(R.color.green700));
            imageView4.setBackgroundResource(R.drawable.accent_color_4_circle_background);
        }else if(mode.toLowerCase().equals("cancelled")){
            view1.setBackgroundColor(getResources().getColor(R.color.green700));
            textView2.setTextColor(getResources().getColor(R.color.green700));
            imageView2.setBackgroundResource(R.drawable.accent_color_4_circle_background);
            textView2.setText("Cancelled");
        }
    }
}