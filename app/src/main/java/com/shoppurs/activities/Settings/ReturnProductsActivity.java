package com.shoppurs.activities.Settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.shoppurs.R;
import com.shoppurs.activities.NetworkBaseActivity;
import com.shoppurs.activities.ProductDetailActivity;
import com.shoppurs.adapters.FrequencyProductListAdapter;
import com.shoppurs.adapters.ReturnProductListAdapter;
import com.shoppurs.database.DbHelper;
import com.shoppurs.models.MyProduct;
import com.shoppurs.models.ProductFrequency;
import com.shoppurs.models.ShopDeliveryModel;
import com.shoppurs.services.NotificationService;
import com.shoppurs.utilities.Constants;
import com.shoppurs.utilities.DialogAndToast;
import com.shoppurs.utilities.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReturnProductsActivity extends NetworkBaseActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerViewProduct;
    private TextView tvNoData;
    private ProgressBar progressBar;
    private ReturnProductListAdapter returnProductListAdapter;
    private String shopCode, shopName,shopAddress, shopdbname,dbuser,dbpassword;
    private List<MyProduct> myProductList;
    private MyProduct myProduct;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_products);
        dbHelper = new DbHelper(this);
        shopCode = getIntent().getStringExtra("shop_code");
        shopName = getIntent().getStringExtra("name");
        shopAddress = getIntent().getStringExtra("address");
        shopdbname = getIntent().getStringExtra("dbname");
        dbuser = getIntent().getStringExtra("dbuser");
        dbpassword = getIntent().getStringExtra("dbpassword");
        initViews();
    }

    private void initViews(){
        TextView text_left_label = findViewById(R.id.text_left_label);
        TextView text_shop_name = findViewById(R.id.text_shop_name);
        text_shop_name.setText(shopName);
        text_left_label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        swipeRefreshLayout =findViewById(R.id.swipe_refresh);
        recyclerViewProduct = findViewById(R.id.recycler_viewProduct);
        tvNoData = findViewById(R.id.tvNoData);
        progressBar=findViewById(R.id.progress_bar);

        myProductList = new ArrayList<>();
        recyclerViewProduct.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewProduct.setLayoutManager(layoutManager);
        recyclerViewProduct.setItemAnimator(new DefaultItemAnimator());
        returnProductListAdapter = new ReturnProductListAdapter(this, myProductList);
        returnProductListAdapter.setTypeFace(Utility.getSimpleLineIconsFont(this));
        returnProductListAdapter.setShopCode(shopCode);
        returnProductListAdapter.setDarkTheme(isDarkTheme);
        recyclerViewProduct.setAdapter(returnProductListAdapter);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getProductList();
            }
        });
        getProductList();
    }

    public void getProductList(){
        if(swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);

        myProductList.clear();
        Map<String,String> params=new HashMap<>();
        params.put("code", shopCode);
        params.put("limit", "10");
        params.put("offset", "0");
        params.put("dbName",sharedPreferences.getString(Constants.DB_NAME, ""));
        Log.d(TAG, params.toString());
        String url=getResources().getString(R.string.root_url)+"return/sales_return_list";
        showProgressBar(true, "RefreshList");
        jsonObjectApiRequest(Request.Method.POST, url,new JSONObject(params),"getProducts");
    }

    @Override
    public void onJsonObjectResponse(JSONObject response, String apiName) {
        showProgressBar(false, "");
        try {

            Log.d("response", response.toString());
            if(apiName.equals("getProducts")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)) {
                    JSONObject dataObject;
                    if(!response.getString("result").equals("null")){
                        JSONArray productJArray = response.getJSONArray("result");
                        JSONObject jsonObject = null;
                        ProductFrequency frequency = null;
                        MyProduct productItem = null;
                        for (int i = 0; i < productJArray.length(); i++) {
                            //JSONArray productJArray = orderJArray.getJSONObject(i).getJSONArray("myProductList");
                            jsonObject = productJArray.getJSONObject(i);
                            productItem = new MyProduct();
                            productItem.setShopCode(shopCode);
                            productItem.setSrId(jsonObject.getString("id"));
                            productItem.setId(jsonObject.getString("prodId"));
                            productItem.setQuantity(jsonObject.getInt("qty"));
                            productItem.setInvNo(jsonObject.getString("invNo"));
                            productItem.setTransId(jsonObject.getString("transId"));
                            productItem.setReturnStatus(jsonObject.getInt("status"));
                            productItem.setName(jsonObject.getString("prodName"));
                            productItem.setCode(jsonObject.getString("prodCode"));
                            productItem.setDesc(jsonObject.getString("prodDesc"));
                            productItem.setProdCgst(Float.parseFloat(jsonObject.getString("prodCgst")));
                            productItem.setProdIgst(Float.parseFloat(jsonObject.getString("prodIgst")));
                            productItem.setProdSgst(Float.parseFloat(jsonObject.getString("prodSgst")));
                            productItem.setProdImage1(jsonObject.getString("prodImage1"));
                            productItem.setProdImage2(jsonObject.getString("prodImage2"));
                            productItem.setProdImage3(jsonObject.getString("prodImage3"));
                            productItem.setMrp(Float.parseFloat(jsonObject.getString("prodMrp")));
                            productItem.setSellingPrice(Float.parseFloat(jsonObject.getString("prodSp")));
                            myProductList.add(productItem);
                        }
                    }

                    if(myProductList.size()>0){
                        Log.d("myProductList size ", myProductList.size()+"");
                        returnProductListAdapter.notifyDataSetChanged();
                    }else {
                        showNoData(true);
                    }

                }else {
                    DialogAndToast.showDialog(response.getString("message"),ReturnProductsActivity.this);
                }
            }else if(apiName.equals("AcceptReturn")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)) {
                    myProduct.setReturnStatus(3);
                    NotificationService.displayNotification(this, "Your Product has been Returned", null);
                    returnProductListAdapter.notifyItemChanged(position);
                }
            }else if(apiName.equals("RejectReturn")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)) {
                    myProduct.setReturnStatus(0);
                    returnProductListAdapter.notifyItemChanged(position);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            DialogAndToast.showToast(getResources().getString(R.string.json_parser_error)+e.toString(),ReturnProductsActivity.this);
        }
    }

    private void showNoData(boolean show){
        if(show){
            recyclerViewProduct.setVisibility(View.GONE);
            tvNoData.setVisibility(View.VISIBLE);
        }else{
            recyclerViewProduct.setVisibility(View.VISIBLE);
            tvNoData.setVisibility(View.GONE);
        }
    }

    private void showProgressBar(boolean show, String type){
        if(show){
            if(type.equals("RefreshList"))
            recyclerViewProduct.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            tvNoData.setVisibility(View.GONE);
        }else{
            recyclerViewProduct.setVisibility(View.VISIBLE);
            tvNoData.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
    }


    public void showLargeImageDialog(MyProduct product, View view){
        showImageDialog(product.getProdImage1(),view);
    }

    public void showProductDetails(MyProduct product){
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra("MyProduct",product);
        startActivity(intent);
    }
    public void acceptRequest(MyProduct product, int position){
        this.myProduct = product;
        this.position = position;
        Map<String,String> params=new HashMap<>();
        params.put("id", product.getSrId());
        params.put("custCode", sharedPreferences.getString(Constants.USER_ID, ""));
        params.put("shopCode", shopCode);
        params.put("shopName", shopName);
        params.put("shopAddress",shopAddress);
        params.put("invNo",product.getInvNo());
        params.put("prodCode",product.getCode());
        params.put("userName", sharedPreferences.getString(Constants.FULL_NAME,""));
        params.put("amount", String.valueOf(product.getSellingPrice()));
        params.put("dbName",sharedPreferences.getString(Constants.DB_NAME, ""));
        Log.d(TAG, params.toString());
        String url=getResources().getString(R.string.root_url)+"return/accept_return_request";
        showProgressBar(true,"");
        jsonObjectApiRequest(Request.Method.POST, url,new JSONObject(params),"AcceptReturn");
    }
    public void rejectRequest(MyProduct product, int position){
        this.position = position;
        this.myProduct = product;
        Map<String,String> params=new HashMap<>();
        params.put("id", product.getSrId());
        params.put("custCode", sharedPreferences.getString(Constants.USER_ID, ""));
        params.put("shopCode", shopCode);
        params.put("shopName", shopName);
        params.put("shopAddress",shopAddress);
        params.put("invNo",product.getInvNo());
        params.put("prodCode",product.getCode());
        params.put("userName", sharedPreferences.getString(Constants.FULL_NAME,""));
        params.put("amount", String.valueOf(product.getSellingPrice()));
        params.put("dbName",sharedPreferences.getString(Constants.DB_NAME, ""));
        Log.d(TAG, params.toString());
        String url=getResources().getString(R.string.root_url)+"return/cancel_return_request";
        showProgressBar(true,"");
        jsonObjectApiRequest(Request.Method.POST, url,new JSONObject(params),"RejectReturn");
    }
}
