package com.shoppurs.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.shoppurs.R;
import com.shoppurs.adapters.ShopContactsAdapter;
import com.shoppurs.models.MyShop;
import com.shoppurs.utilities.Constants;
import com.shoppurs.utilities.DialogAndToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopContactListActivity extends NetworkBaseActivity {

    private EditText editMobile;
    private ImageView iv_contacts, text_header;
    private RecyclerView recycler_viewShops;
    private ShopContactsAdapter shopContactsAdapter;
    private TextView text_error;
    private List<MyShop> myShopList;
    private ProgressBar progress_bar;
    private boolean searchByQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_contact_lits);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        myShopList  = new ArrayList<>();
        recycler_viewShops = findViewById(R.id.recycler_viewShops);
        progress_bar = findViewById(R.id.progress_bar);
        text_error = findViewById(R.id.tvNoData);
        editMobile = findViewById(R.id.edit_mobile);
        iv_contacts = findViewById(R.id.iv_contacts);
        text_header = findViewById(R.id.text_header);
        text_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        editMobile.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!TextUtils.isEmpty(s)){
                    searchByQuery = true;
                    recycler_viewShops.setVisibility(View.VISIBLE);
                    //showShopListRecycleView(true);
                    searchShopbyQuery(s.toString());
                }else {
                    searchByQuery = false;
                    showNoData(false);
                    showProgressBar(false);
                    //recycler_viewShops.setVisibility(View.GONE);
                    getshopContacts();
                    //showShopListRecycleView(false);
                }
            }
        });
        getshopContacts();

    }

    private void getshopContacts(){
        myShopList.clear();
        showNoData(false);
        recycler_viewShops.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this);
        recycler_viewShops.setLayoutManager(layoutManager);
        recycler_viewShops.setItemAnimator(new DefaultItemAnimator());
        shopContactsAdapter=new ShopContactsAdapter(this,myShopList);
        shopContactsAdapter.setType("contactList");
        recycler_viewShops.setAdapter(shopContactsAdapter);

        Map<String,String> params=new HashMap<>();
        params.put("limit", "10");
        params.put("offset", ""+myShopList.size());
        params.put("lattitude", sharedPreferences.getString(Constants.CUST_CURRENT_LAT,""));
        params.put("longitude", sharedPreferences.getString(Constants.CUST_CURRENT_LONG,""));
        params.put("dbName", sharedPreferences.getString(Constants.DB_NAME, ""));
        String url=getResources().getString(R.string.url_customer)+"/api/customers/allshoplist";
        //showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"contactList");
    }

    private void searchShopbyQuery(String query){
        myShopList.clear();
        showNoData(false);
        recycler_viewShops.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this);
        recycler_viewShops.setLayoutManager(layoutManager);
        recycler_viewShops.setItemAnimator(new DefaultItemAnimator());
        shopContactsAdapter=new ShopContactsAdapter(this,myShopList);
        shopContactsAdapter.setType("contactList");
        recycler_viewShops.setAdapter(shopContactsAdapter);

        Map<String, String> params = new HashMap<>();
        params.put("query",query);
        params.put("limit", "10");
        params.put("offset", ""+offset);
        params.put("lattitude", sharedPreferences.getString(Constants.CUST_CURRENT_LAT,""));
        params.put("longitude", sharedPreferences.getString(Constants.CUST_CURRENT_LONG,""));
        params.put("dbName",sharedPreferences.getString(Constants.DB_NAME,""));
        params.put("dbUserName",sharedPreferences.getString(Constants.DB_USER_NAME,""));
        params.put("dbPassword",sharedPreferences.getString(Constants.DB_PASSWORD,""));
        String url=getResources().getString(R.string.url_customer)+"/api/search/shops";
        //String url=getResources().getString(R.string.root_url)+"search/shops_by_mobile";
        showProgressBar(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"contactList");
    }

    private void showNoData(boolean show){
        if(show){
            recycler_viewShops.setVisibility(View.GONE);
            text_error.setVisibility(View.VISIBLE);
        }else{
            //recyclerView_shops.setVisibility(View.VISIBLE);
            text_error.setVisibility(View.GONE);
        }
    }

    private void showProgressBar(boolean show){
        if(show){
            //recyclerView_shops.setVisibility(View.GONE);
            progress_bar.setVisibility(View.VISIBLE);
        }else{
            //recyclerView_shops.setVisibility(View.VISIBLE);
            progress_bar.setVisibility(View.GONE);
        }
    }

    public void scanStoresToPay(String mobile) {
        Map<String, String> params = new HashMap<>();
        String url = getResources().getString(R.string.url_customer) + "/api/customers/shop/shop_details_by_qrcode";
        params.put("mobile", mobile);
        params.put("dbName", sharedPreferences.getString(Constants.DB_NAME, ""));
        params.put("dbUserName", sharedPreferences.getString(Constants.DB_USER_NAME, ""));
        params.put("dbPassword", sharedPreferences.getString(Constants.DB_PASSWORD, ""));
        Log.d(TAG, params.toString());
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST, url, new JSONObject(params), "InstantPay");
    }

    @Override
    public void onJsonObjectResponse(JSONObject response, String apiName) {
        showProgressBar(false);
        showProgress(false);
        try {
            Log.d("response", response.toString());
            if(apiName.equals("InstantPay")){
                if (response.getString("status").equals("true") || response.getString("status").equals(true)) {
                    //DialogAndToast.showDialog(response.getString("message"), ScannarActivity.this);

                    if (response.getString("result") != null) {
                        JSONObject jsonObject = response.getJSONObject("result");
                        MyShop myShop = new MyShop();
                        String shop_code = jsonObject.getString("retcode");
                        myShop.setId(shop_code);
                        Log.d("shop_id", myShop.getId());
                        myShop.setName(jsonObject.getString("retshopname"));
                        myShop.setMobile(jsonObject.getString("retmobile"));
                        myShop.setMerchantId(jsonObject.getString("merchantId"));
                        if(!myShop.getId().equals("null")){
                            Intent intent = new Intent(ShopContactListActivity.this, InstantPayActivity.class);
                            intent.putExtra("flag", "InstantPay");
                            intent.putExtra("object", myShop);
                            startActivity(intent);
                        }
                    }else {
                        DialogAndToast.showDialog(response.getString("message"), ShopContactListActivity.this);
                    }
                }else {
                    DialogAndToast.showDialog(response.getString("message"), ShopContactListActivity.this);
                }
            }else if(apiName.equals("contactList")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)){
                    JSONArray shopJArray = null;
                    if(searchByQuery) {
                        JSONObject jsonObject = response.getJSONObject("result");
                        shopJArray = jsonObject.getJSONArray("data");
                        offset = jsonObject.getInt("offset");
                    }
                    else {
                        JSONObject jsonObject = response.getJSONObject("result");
                        shopJArray = jsonObject.getJSONArray("shoplist");
                    }

                    myShopList.clear();
                    for(int i=0;i<shopJArray.length();i++){
                        MyShop myShop = new MyShop();
                        String shop_code = shopJArray.getJSONObject(i).getString("retcode");
                        myShop.setId(shop_code);
                        Log.d("shop_id", myShop.getId());
                        myShop.setName(shopJArray.getJSONObject(i).getString("retshopname"));
                        myShop.setMobile(shopJArray.getJSONObject(i).getString("retmobile"));
                        myShop.setAddress(shopJArray.getJSONObject(i).getString("retaddress"));
                        myShop.setState(shopJArray.getJSONObject(i).getString("retcountry"));
                        myShop.setCity(shopJArray.getJSONObject(i).getString("retcity"));
                        myShop.setShopimage(shopJArray.getJSONObject(i).getString("retphoto"));

                        myShop.setDbname(shopJArray.getJSONObject(i).getString("dbname"));
                        myShop.setDbusername(shopJArray.getJSONObject(i).getString("dbuser"));
                        myShop.setDbpassword(shopJArray.getJSONObject(i).getString("dbpassword"));
                      //  myShop.setImage(R.drawable.thumb_21);
                        Log.d(myShopList.toString(), "shop_code"+ shop_code);

                        myShopList.add(myShop);

                        //"retcode":"shop_8","retname":"Vipin Dhama","retshopname":"Dhama Test 1","retmobile":"9718181697","retlanguage":"English","retaddress":"Delhi","retpincode":"110091","retemail":"vipinsuper19@gmail.com","retphoto":"","retpassword":"1234","retcountry":"India","retstate":"Delhi","retcity":"Delhi","serverip":"49.50.77.154","dbname":"shop_8","dbuser":"shoppurs_master","dbpassword":"$hop@2018#"
                    }

                    if(myShopList.size()>0){
                        shopContactsAdapter.notifyDataSetChanged();
                    }else {
                        showNoData(true);
                    }

                }else {
                    DialogAndToast.showDialog(response.getString("message"),ShopContactListActivity.this);
                }
            }
        }catch (JSONException e) {
            e.printStackTrace();
            DialogAndToast.showToast(getResources().getString(R.string.json_parser_error)+e.toString(), ShopContactListActivity.this);
        }
    }
}
