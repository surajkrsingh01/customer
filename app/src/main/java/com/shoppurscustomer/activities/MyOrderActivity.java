package com.shoppurscustomer.activities;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.Request;
import com.shoppurscustomer.R;
import com.shoppurscustomer.adapters.CartAdapter;
import com.shoppurscustomer.adapters.MyOrderAdapter;
import com.shoppurscustomer.models.CartItem;
import com.shoppurscustomer.models.MyOrder;
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
 * Created by suraj kumar singh on 03-04-2019.
 */

public class MyOrderActivity extends NetworkBaseActivity{

    private RecyclerView recycler_order;
    private MyOrderAdapter myOrderAdapter;
    private List<MyOrder> myOrderList;
    private String dbName, dbUserName, dbPassword,custId;
    private final String TAG = "MyOrderActivity";
    private SwipeRefreshLayout swipe_refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);

        dbName = sharedPreferences.getString(Constants.DB_NAME,"");
        dbUserName = sharedPreferences.getString(Constants.DB_USER_NAME,"");
        dbPassword = sharedPreferences.getString(Constants.DB_PASSWORD,"");
        custId = sharedPreferences.getString(Constants.USER_ID,"");

        swipe_refresh = findViewById(R.id.swipe_refresh);
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe_refresh.setRefreshing(true);
                getItemList();
            }
        });

        myOrderList = new ArrayList<>();
        recycler_order = (RecyclerView) findViewById(R.id.recycler_order);
        recycler_order.setHasFixedSize(true);
        recycler_order.setRecycledViewPool(new RecyclerView.RecycledViewPool());
        recycler_order.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recycler_order.getRecycledViewPool().setMaxRecycledViews(0, 0);
        myOrderAdapter = new MyOrderAdapter(MyOrderActivity.this, myOrderList);
        recycler_order.setAdapter(myOrderAdapter);
        getOrders();
        initFooter(this,4);
    }

    private void getItemList(){
        swipe_refresh.setRefreshing(false);
    }


    public void getOrders(){
        Map<String,String> params=new HashMap<>();
        //params.put("action","4");
        //params.put("custId",custId);
        params.put("dbName",dbName);
        params.put("dbUserName",dbUserName);
        params.put("dbPassword",dbPassword);
        String url=getResources().getString(R.string.root_url)+"order/get_order";
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
                        MyOrder myOrder = new MyOrder();
                        myOrder.setOrderNumber(jsonArray.getJSONObject(i).getString("orderNumber"));
                        myOrder.setOrderDate(jsonArray.getJSONObject(i).getString("orderDate"));
                        myOrder.setOrderDeliveryNote(jsonArray.getJSONObject(i).getString("orderDeliveryNote"));
                        myOrder.setOrderDeliveryMode(jsonArray.getJSONObject(i).getString("orderDeliveryMode"));
                        myOrder.setPaymentMode(jsonArray.getJSONObject(i).getString("paymentMode"));
                        myOrder.setCustName(jsonArray.getJSONObject(i).getString("custName"));
                        myOrder.setDeliveryAddress(jsonArray.getJSONObject(i).getString("deliveryAddress"));
                        myOrder.setOrderStatus(jsonArray.getJSONObject(i).getString("orderStatus"));
                        myOrder.setOrderImage(jsonArray.getJSONObject(i).getString("orderImage"));
                        myOrder.setPinCode(jsonArray.getJSONObject(i).getInt("pinCode"));
                        myOrder.setMobileNo(jsonArray.getJSONObject(i).getInt("mobileNo"));
                        myOrder.setOrderId(jsonArray.getJSONObject(i).getInt("orderId"));
                        myOrder.setTotalQuantity(jsonArray.getJSONObject(i).getInt("totalQuantity"));
                        myOrder.setToalAmount(jsonArray.getJSONObject(i).getDouble("toalAmount"));
                        myOrderList.add(myOrder);
                    }
                    if(myOrderList.size()>0){
                        myOrderAdapter.notifyDataSetChanged();
                    }else {
                        //showNoData(true);
                    }

                }else {
                    DialogAndToast.showDialog(response.getString("message"),MyOrderActivity.this);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            DialogAndToast.showToast(getResources().getString(R.string.json_parser_error)+e.toString(),MyOrderActivity.this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_shop_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
            return true;
        }else if(item.getItemId()== R.id.action_search){
            DialogAndToast.showToast("Search Clicked..", this);
        }
        return super.onOptionsItemSelected(item);
    }
}
