package com.shoppurs.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.shoppurs.R;
import com.shoppurs.adapters.SearchShopAdapter;
import com.shoppurs.models.MyProduct;
import com.shoppurs.models.MyShop;
import com.shoppurs.utilities.AppController;
import com.shoppurs.utilities.ConnectionDetector;
import com.shoppurs.utilities.Constants;
import com.shoppurs.utilities.DialogAndToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartShopListActivity extends NetworkBaseActivity {

    private List<MyShop> itemList;
    private RecyclerView recyclerView;
    private SearchShopAdapter myItemAdapter;
    private TextView textViewError, text_left_label, text_right_label;
    private String flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_shops);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        flag = getIntent().getStringExtra("flag");

        init();

    }

    private void init(){
        text_left_label = findViewById(R.id.text_left_label);
        text_right_label = findViewById(R.id.text_right_label);
        if(flag!=null && flag.equals("mainOffers"))
            text_left_label.setText("Offers");
        else text_left_label.setText("Cart");

            text_left_label.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent;
                    if(flag!=null && flag.equals("mainOffers"))
                        intent = new Intent(CartShopListActivity.this, MainActivity.class);
                        else intent = new Intent(CartShopListActivity.this, CartActivity.class);
                    startActivity(intent);
                    finish();
                }
            });


        textViewError = findViewById(R.id.text_error);
        recyclerView=findViewById(R.id.recycler_view);

        itemList = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        myItemAdapter=new SearchShopAdapter(this,itemList);
        myItemAdapter.setColorTheme(colorTheme);
        if(flag!=null && flag.equals("offers"))
        myItemAdapter.setFlag("CartShopOffers");
        else if(flag!=null && flag.equals("mainOffers"))
            myItemAdapter.setFlag("mainOffers");
        else myItemAdapter.setFlag("CartShopCoupons");
        recyclerView.setAdapter(myItemAdapter);

        if(ConnectionDetector.isNetworkAvailable(this)){
            if(flag!=null && flag.equals("mainOffers"))
            getShopListbyCategory();
            else
            getShopList();
        }
    }

    private void getShopListbyCategory(){
        Map<String,String> params=new HashMap<>();
        params.put("id",getIntent().getStringExtra("catId"));
        params.put("dbName",sharedPreferences.getString(Constants.DB_NAME,""));
        params.put("dbUserName",sharedPreferences.getString(Constants.DB_USER_NAME,""));
        params.put("dbPassword",sharedPreferences.getString(Constants.DB_PASSWORD,""));
        String url=getResources().getString(R.string.root_url)+"offers/get_active_offer_shops_by_cat";
        Log.i(TAG, params.toString());
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url, new JSONObject(params),"shopList");
    }

    private void getShopList(){
        List<MyProduct> cartItemList = dbHelper.getCartProducts();
        String shopCodes ="";
        for(MyProduct myProduct: cartItemList) {

            Log.d("left "+cartItemList.indexOf(myProduct), "right "+(cartItemList.size()-1));
            if(cartItemList.size()==1 || cartItemList.indexOf(myProduct) == cartItemList.size()-1)
                shopCodes  = shopCodes.concat(myProduct.getShopCode());
            else
                 shopCodes  = shopCodes.concat(myProduct.getShopCode()+",");
        }

        Map<String,String> params=new HashMap<>();
        params.put("code", shopCodes);
        params.put("dbName",sharedPreferences.getString(Constants.DB_NAME,""));
        params.put("dbUserName",sharedPreferences.getString(Constants.DB_USER_NAME,""));
        params.put("dbPassword",sharedPreferences.getString(Constants.DB_PASSWORD,""));
        String url=getResources().getString(R.string.root_url)+"customers/shop/shop_details_by_code";
        Log.i(TAG, params.toString());
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url, new JSONObject(params),"shopList");
    }

    public void jsonObjectApiRequest(int method,String url, JSONObject jsonObject, final String apiName){
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(method,url,jsonObject,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                AppController.getInstance().getRequestQueue().getCache().clear();
                Log.i(TAG,response.toString());
                showProgress(false);
                onJsonObjectResponse(response,apiName);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                AppController.getInstance().getRequestQueue().getCache().clear();
                showProgress(false);
                onServerErrorResponse(error,apiName);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + sharedPreferences.getString(Constants.JWT_TOKEN, ""));
                //params.put("VndUserDetail", appVersion+"#"+deviceName+"#"+osVersionName);
                return params;
            }
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public void onJsonObjectResponse(JSONObject response, String apiName) {
        try {
            if (apiName.equals("shopList")) {
                if (response.getString("status").equals("true") || response.getString("status").equals(true)) {
                    JSONArray shopJArray = response.getJSONArray("result");
                    for (int i = 0; i < shopJArray.length(); i++) {
                        MyShop myShop = new MyShop();
                        String shop_code = shopJArray.getJSONObject(i).getString("retcode");
                        myShop.setCode(shop_code);
                        Log.d("shop_id", myShop.getCode());
                        myShop.setName(shopJArray.getJSONObject(i).getString("retshopname"));
                        myShop.setMobile(shopJArray.getJSONObject(i).getString("retmobile"));
                        myShop.setAddress(shopJArray.getJSONObject(i).getString("retaddress"));
                        myShop.setState(shopJArray.getJSONObject(i).getString("retcountry"));
                        myShop.setCity(shopJArray.getJSONObject(i).getString("retcity"));
                        myShop.setShopimage(shopJArray.getJSONObject(i).getString("retphoto"));
                        myShop.setDeliveryAvailable(shopJArray.getJSONObject(i).getString("isDeliveryAvailable"));
                        myShop.setMinDeliveryAmount(shopJArray.getJSONObject(i).getDouble("minDeliveryAmount"));
                        myShop.setDbname(shopJArray.getJSONObject(i).getString("dbname"));
                        myShop.setDbusername(shopJArray.getJSONObject(i).getString("dbuser"));
                        myShop.setDbpassword(shopJArray.getJSONObject(i).getString("dbpassword"));
                        myShop.setImage(R.drawable.thumb_21);
                        itemList.add(myShop);
                    }
                    if (itemList.size() > 0)
                        myItemAdapter.notifyDataSetChanged();
                    else {
                        itemList.clear();
                        myItemAdapter.notifyDataSetChanged();
                    }

                } else {
                    DialogAndToast.showDialog(response.getString("message"), this);
                }
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
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
    public void onResume() {
        super.onResume();
        if(myItemAdapter!=null)
            myItemAdapter.notifyDataSetChanged();
    }

    public void showLargeImageDialog(MyShop shop,  View view){
        showImageDialog(shop.getShopimage(), view);
    }
}
