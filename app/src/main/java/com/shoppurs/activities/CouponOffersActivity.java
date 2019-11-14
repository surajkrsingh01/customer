package com.shoppurs.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.shoppurs.R;
import com.shoppurs.adapters.CouponOfferAdapter;
import com.shoppurs.interfaces.MyItemClickListener;
import com.shoppurs.models.Coupon;
import com.shoppurs.models.MyProduct;
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

public class CouponOffersActivity extends NetworkBaseActivity implements MyItemClickListener {

    private List<Object> itemList;
    private RecyclerView recyclerView;
    private CouponOfferAdapter myItemAdapter;
    private TextView textApply,textViewError;
    private EditText edit_coupon_code;

    private String flag, shopDbName, shopCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_offers);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();

    }

    private void init(){
        flag = getIntent().getStringExtra("flag");
        shopDbName = getIntent().getStringExtra("dbname");
        shopCode = getIntent().getStringExtra("shopCode");
        itemList = new ArrayList<>();
        textViewError = findViewById(R.id.text_error);
        textApply = findViewById(R.id.btn_apply);
        edit_coupon_code = findViewById(R.id.edit_coupon_code);
        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        myItemAdapter=new CouponOfferAdapter(this,itemList);
        myItemAdapter.setMyItemClickListener(this);
        myItemAdapter.setColorTheme(colorTheme);
        recyclerView.setAdapter(myItemAdapter);

        textApply.setTextColor(colorTheme);
        textApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String couponCode = edit_coupon_code.getText().toString();
                if(!TextUtils.isEmpty(couponCode))
                    applyCoupon(couponCode);
                else
                DialogAndToast.showDialog("Please Enter Coupon Code ",CouponOffersActivity.this);
            }
        });

        if(ConnectionDetector.isNetworkAvailable(this)){
            getCouponOffers();
        }
    }


    private void getCouponOffers(){
        Map<String,String> params=new HashMap<>();
        params.put("dbName",shopDbName);
        params.put("dbUserName",sharedPreferences.getString(Constants.DB_USER_NAME,""));
        params.put("dbPassword",sharedPreferences.getString(Constants.DB_PASSWORD,""));
        String url=getResources().getString(R.string.root_url)+Constants.GET_COUPON_OFFER;
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"offerList");
    }

    private void applyCoupon(String couponCode){
        Map<String,String> params=new HashMap<>();
        params.put("shopCode",shopCode);
        params.put("couponCode",couponCode);
        params.put("dbName",shopDbName);
        params.put("dbUserName",sharedPreferences.getString(Constants.DB_USER_NAME,""));
        params.put("dbPassword",sharedPreferences.getString(Constants.DB_PASSWORD,""));
        String url=getResources().getString(R.string.root_url)+"offers/validate_coupon_offer";
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"applyCoupon");
    }

    @Override
    public void onJsonObjectResponse(JSONObject response, String apiName) {

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
                        coupon.setShopCode(shopCode);
                        coupon.setPercentage(dataObject.getInt("percentage"));
                        coupon.setMaxDiscount((float)dataObject.getDouble("amount"));
                        coupon.setAmount((float)dataObject.getDouble("amount"));
                        coupon.setName(dataObject.getString("name"));
                        coupon.setStatus(dataObject.getString("status"));
                        coupon.setStartDate(dataObject.getString("startDate"));
                        coupon.setEndDate(dataObject.getString("endDate"));
                        //dbHelper.manageCouponOffer(coupon, Utility.getTimeStamp(),Utility.getTimeStamp());
                        itemList.add(coupon);
                    }

                    if(itemList.size() == 0){
                        showNoData(true);
                    }else{
                        showNoData(false);
                        myItemAdapter.notifyDataSetChanged();
                    }

                }
            }else if(apiName.equals("applyCoupon")){
                Log.d("response ", response.toString());
                if (response.getBoolean("status")) {
                    JSONObject dataObject = response.getJSONObject("result");
                    Coupon coupon = new Coupon();
                    coupon.setId(dataObject.getInt("id"));
                    coupon.setShopCode(dataObject.getString("shopCode"));
                    coupon.setPercentage(dataObject.getInt("percentage"));
                    coupon.setAmount((float)dataObject.getDouble("amount"));
                    coupon.setMaxDiscount((float)dataObject.getDouble("amount"));
                    coupon.setName(dataObject.getString("name"));
                    coupon.setStatus(dataObject.getString("status"));
                    coupon.setStartDate(dataObject.getString("startDate"));
                    coupon.setEndDate(dataObject.getString("endDate"));

                    if(coupon.getPercentage()==0){
                        showMyDialog("Coupon is not Valid");
                        return;
                    }else {
                        if(coupon.getPercentage()>0) {
                            Float couponDiscount;
                            if(coupon.getShopCode().equals("SHP1"))
                             couponDiscount = dbHelper.getTotalPriceCart("normal") * coupon.getPercentage() / 100;
                            else
                                couponDiscount = dbHelper.getTotalAmount(coupon.getShopCode()) * coupon.getPercentage() / 100;
                            if(couponDiscount<coupon.getMaxDiscount()  || coupon.getMaxDiscount()==0)
                            coupon.setAmount(couponDiscount);
                            else coupon.setAmount(coupon.getMaxDiscount());
                            Log.d(coupon.getPercentage()+"" , "Percentage");
                            Log.d(coupon.getAmount()+"" , "Discount");
                             dbHelper.manageCouponDiscount(coupon, "remove");
                             dbHelper.manageCouponDiscount(coupon, "update");
                             dbHelper.removeCouponFromCart(coupon.getShopCode());
                            dbHelper.manageCouponOffer(coupon, "add");
                            showMyDialog("Coupon Applied Successfully Amount "+coupon.getAmount());
                        }
                    }
                }else {
                    int result = response.getInt("result");
                    if(result==0){
                        showMyDialog("Coupon is not Valid");
                    }else if(result==1)
                        showMyDialog("Something went wrong, please try letter");
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
    public void onItemClicked(int position) {
        Coupon coupon = (Coupon) itemList.get(position);
        applyCoupon(coupon.getName());
    }

    @Override
    public void onProductSearch(MyProduct myProduct) {

    }

    @Override
    public void onItemClicked(int pos, String type) {

    }
}
