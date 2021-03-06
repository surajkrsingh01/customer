package com.shoppurs.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.shoppurs.R;
import com.shoppurs.adapters.ShopAdapter;
import com.shoppurs.fragments.BottomSearchFragment;
import com.shoppurs.models.MyShop;
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

public class ScrollShopListActivity extends NetworkBaseActivity {

    private List<Object> itemList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private TextView textViewError, tv_mynormal, text_left_label, text_right_label;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ShopAdapter shopAdapter ;
    private String subCatName, subCatId, catId;
    private String dbName, dbUserName, dbPassword, shopListType;
    private ImageView imamge_search;
    private boolean isScrolling;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll_shop_list);
        init();
    }

    private void init(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        subCatName = getIntent().getStringExtra("subCatName");
        subCatId = getIntent().getStringExtra("subCatId");
        catId = getIntent().getStringExtra("CatId");
        shopListType = getIntent().getStringExtra("flag");
        dbName = sharedPreferences.getString(Constants.DB_NAME,"");
        dbUserName = sharedPreferences.getString(Constants.DB_USER_NAME,"");
        dbPassword = sharedPreferences.getString(Constants.DB_PASSWORD,"");

        swipeRefreshLayout=findViewById(R.id.swipe_refresh);
        imamge_search = findViewById(R.id.image_search);
        progressBar=findViewById(R.id.progress_bar);
        textViewError = findViewById(R.id.text_error);
        recyclerView=findViewById(R.id.recycler_view_normal_shop);
        tv_mynormal = findViewById(R.id.tv_mynormal);
        itemList = new ArrayList<>();

        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        shopAdapter=new ShopAdapter(this,itemList,"shopList", catId, subCatId, subCatName);
        shopAdapter.setFlag("ScrollShopList");
        shopAdapter.setShopListType(shopListType);
        recyclerView.setAdapter(shopAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                getItemList();
            }
        });

        imamge_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSearchFragment bottomSearchFragment = new BottomSearchFragment();
                bottomSearchFragment.setCallingActivityName("ShopListActivity", sharedPreferences, isDarkTheme);
                bottomSearchFragment.setSubCatName(subCatName);
                bottomSearchFragment.setSubcatId(subCatId);
                bottomSearchFragment.setCatId(catId);
                bottomSearchFragment.show(getSupportFragmentManager(), bottomSearchFragment.getTag());
            }
        });

        text_left_label = findViewById(R.id.text_left_label);
        if(!TextUtils.isEmpty(shopListType)){
            if(shopListType.equals("Frequency")  || shopListType.equals("Return Product") ||
                shopListType.equals("ToDo List") || shopListType.equals("MarketStore") ||
                shopListType.contains("Stores")  || shopListType.contains("Favourite")) {
                text_left_label.setText("Nearby");
            }else if(shopListType.equals("KhataBook"))
                text_left_label.setText("Settings");
        }

        initFooter(this,2);

        text_right_label = findViewById(R.id.text_right_label);
        text_left_label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(ShopListActivity.this, SubCatListActivity.class));
                finish();
            }
        });

        if(!TextUtils.isEmpty(shopListType)) {
            if (shopListType.equals("Frequency") || shopListType.equals("Return Product") ||
                    shopListType.equals("ToDo List") || shopListType.equals("MarketStore") ||
                    shopListType.contains("Stores")  || shopListType.contains("Favourite") ||
                    shopListType.equals("KhataBook")) {
                getNormalShops("initialLoading");
            }
        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(isScroll ){
                    int  visibleItemCount, totalItemCount, pastVisibleItems;
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    Log.i(TAG,"visible "+visibleItemCount+" totalItem "+totalItemCount);
                    pastVisibleItems = ((LinearLayoutManager)layoutManager).findLastVisibleItemPosition();
                    Log.i(TAG,"visible + pastItem "+(visibleItemCount+pastVisibleItems));

                    if (!isScrolling) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            isScrolling = true;
                            //offset = limit + offset;
                            getNormalShops("loadOnScroll");
                            showProgressBar(true);
                        }
                    }
                }
            }
        });
    }

    private void getItemList(){
        offset =0;
        swipeRefreshLayout.setRefreshing(false);
        tv_mynormal.setVisibility(View.GONE);
        itemList.clear();
        if(!TextUtils.isEmpty(shopListType)) {
            if (shopListType.equals("Frequency") || shopListType.equals("Return Product") ||
                    shopListType.equals("ToDo List") || shopListType.equals("MarketStore") ||
                    shopListType.contains("Stores")  || shopListType.contains("Favourite") ||
                    shopListType.equals("KhataBook")) {
                getNormalShops("initialLoading");
            }
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

    private void showProgressBar(boolean show){
        if(show){
           // recyclerView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            //textViewError.setVisibility(View.GONE);
        }else{
            //recyclerView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void getNormalShops(String type){
        Map<String,String> params=new HashMap<>();
        params.put("dbName", sharedPreferences.getString(Constants.DB_NAME, ""));
        String url=null;
        if(!TextUtils.isEmpty(shopListType) && shopListType.equals("Frequency"))
            url=getResources().getString(R.string.url_customer)+"/api/order/get_frequency_order_shop_list";
        else if(!TextUtils.isEmpty(shopListType) && shopListType.equals("Return Product"))
            url = getResources().getString(R.string.url_customer) + "/api/customers/shop/sales_return_shops";
        else if(!TextUtils.isEmpty(shopListType) && shopListType.equals("ToDo List")) {
            params.put("lattitude", sharedPreferences.getString(Constants.CUST_CURRENT_LAT,""));
            params.put("longitude", sharedPreferences.getString(Constants.CUST_CURRENT_LONG,""));
            params.put("dbName", sharedPreferences.getString(Constants.DB_NAME, ""));
            params.put("limit", limit+"");
            params.put("offset", offset+"");
            url=getResources().getString(R.string.url_customer)+"/api/customers/allshoplist";
        }else if(!TextUtils.isEmpty(shopListType) && shopListType.equals("MarketStore")){
            params.put("lattitude", sharedPreferences.getString(Constants.CUST_CURRENT_LAT,""));
            params.put("longitude", sharedPreferences.getString(Constants.CUST_CURRENT_LONG,""));
            params.put("dbName", sharedPreferences.getString(Constants.DB_NAME, ""));
            params.put("id", getIntent().getStringExtra("marketId"));
            params.put("limit", limit+"");
            params.put("offset", offset+"");
            url=getResources().getString(R.string.url_customer)+"/api/market/searchShop";
        }else if(!TextUtils.isEmpty(shopListType) && shopListType.equals("KhataBook")){
            params.put("lattitude", sharedPreferences.getString(Constants.CUST_CURRENT_LAT, ""));
            params.put("longitude", sharedPreferences.getString(Constants.CUST_CURRENT_LONG, ""));
            params.put("limit", limit+"");
            params.put("offset", offset+"");
            params.put("dbName", sharedPreferences.getString(Constants.DB_NAME, ""));
            //url=getResources().getString(R.string.url_customer)+"/api/customers/allshoplist";
            url=getResources().getString(R.string.url_customer)+"/api/khata/shops";
        }else if(!TextUtils.isEmpty(shopListType)){
            if(!TextUtils.isEmpty(sharedPreferences.getString(Constants.CUST_CURRENT_ADDRESS,""))){
                params.put("lattitude", sharedPreferences.getString(Constants.CUST_CURRENT_LAT, ""));
                params.put("longitude", sharedPreferences.getString(Constants.CUST_CURRENT_LONG, ""));
            }else {
                params.put("lattitude", sharedPreferences.getString(Constants.CUST_LAT, ""));
                params.put("longitude", sharedPreferences.getString(Constants.CUST_LONG, ""));
            }
            params.put("limit", limit+"");
            params.put("offset", offset+"");
            params.put("dbName", sharedPreferences.getString(Constants.DB_NAME, ""));
            if(shopListType.contains("Stores"))
            url=getResources().getString(R.string.url_customer)+"/api/customers/allshoplist";
            else if(shopListType.contains("Favorite"))
                url=getResources().getString(R.string.url_customer)+"/api/customers/allshoplist";
        }
        //showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),type);
    }

    @Override
    public void onJsonObjectResponse(JSONObject response, String apiName) {
        // showProgress(false);
        try {

            // JSONObject jsonObject=response.getJSONObject("response");
            Log.d("response", response.toString());
            if(apiName.equals("initialLoading")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)) {
                    showProgress(false);
                    if(!response.getString("result").equals("null")){
                        JSONArray shopJArray = null;
                        JSONArray khataJArray = null;

                        if(!TextUtils.isEmpty(shopListType) && shopListType.equals("Frequency") ||
                                !TextUtils.isEmpty(shopListType) && shopListType.equals("Return Product")){
                            shopJArray = response.getJSONArray("result");
                        }else if(!TextUtils.isEmpty(shopListType) && shopListType.equals("MarketStore")){
                            shopJArray = response.getJSONArray("result");
                            //shopJArray = jsonObject.getJSONArray("data");
                        }else if(!TextUtils.isEmpty(shopListType) && shopListType.equals("ToDo List")){
                            JSONObject jsonObject = response.getJSONObject("result").getJSONObject("shoplist");
                            shopJArray = jsonObject.getJSONArray("data");
                            offset = jsonObject.getInt("offset");
                        }else if(!TextUtils.isEmpty(shopListType)){
                            if(shopListType.contains("Stores") || shopListType.contains("Favorite")) {
                                JSONObject jsonObject = response.getJSONObject("result").getJSONObject("shoplist");
                                shopJArray = jsonObject.getJSONArray("data");
                                offset = jsonObject.getInt("offset");
                            }else if(shopListType.contains("KhataBook")){
                                JSONObject jsonObject = response.getJSONObject("result");
                                shopJArray = jsonObject.getJSONArray("shopList");
                                khataJArray = jsonObject.getJSONArray("khataList");
                                offset =  shopJArray.length();
                            }
                        }else{
                            JSONObject jsonObject = response.getJSONObject("result");
                            shopJArray = jsonObject.getJSONArray("shoplist");
                        }
                        for (int i = 0; i < shopJArray.length(); i++) {
                            MyShop myShop = new MyShop();
                            String shop_code = shopJArray.getJSONObject(i).getString("retcode");
                            myShop.setId(shop_code);
                            Log.d("shop_id", myShop.getId());
                            if(khataJArray!=null && khataJArray.length()>=i) {
                                myShop.setKhataNumber(khataJArray.getJSONObject(i).getString("kbNo"));
                                myShop.setKhataOpenDate(khataJArray.getJSONObject(i).getString("createdDate"));
                            }
                            myShop.setName(shopJArray.getJSONObject(i).getString("retshopname"));
                            myShop.setMobile(shopJArray.getJSONObject(i).getString("retmobile"));
                            myShop.setAddress(shopJArray.getJSONObject(i).getString("retaddress"));
                            myShop.setState(shopJArray.getJSONObject(i).getString("retcountry"));
                            myShop.setCity(shopJArray.getJSONObject(i).getString("retcity"));
                            myShop.setShopimage(shopJArray.getJSONObject(i).getString("retphoto"));

                            myShop.setLatitude(shopJArray.getJSONObject(i).getDouble("retLat"));
                            myShop.setLongitude(shopJArray.getJSONObject(i).getDouble("retLong"));
                            myShop.setDeliveryAvailable(shopJArray.getJSONObject(i).getString("isDeliveryAvailable"));
                            myShop.setMinDeliveryAmount(shopJArray.getJSONObject(i).getDouble("minDeliveryAmount")); //charge per km
                            myShop.setMinDeliverytime(shopJArray.getJSONObject(i).getString("minDeliverytime"));
                            myShop.setMinDeliverydistance(shopJArray.getJSONObject(i).getInt("minDeliverydistance"));
                            myShop.setChargesAfterMinDistance(shopJArray.getJSONObject(i).getDouble("chargesAfterMinDistance"));

                            myShop.setDbname(shopJArray.getJSONObject(i).getString("dbname"));
                            myShop.setDbusername(shopJArray.getJSONObject(i).getString("dbuser"));
                            myShop.setDbpassword(shopJArray.getJSONObject(i).getString("dbpassword"));

                            itemList.add(myShop);
                        }

                        if (itemList.size() > 0) {
                            if(shopJArray.length() < limit){
                                isScroll = false;
                            }
                            tv_mynormal.setVisibility(View.VISIBLE);
                            shopAdapter.notifyDataSetChanged();
                        } else{
                            tv_mynormal.setVisibility(View.GONE);
                            showNoData(true);
                        }

                    }else {
                        tv_mynormal.setVisibility(View.GONE);
                        showNoData(true);
                        showProgress(false);
                    }
                }else {
                    DialogAndToast.showDialog(response.getString("message"),ScrollShopListActivity.this);
                    showProgress(false);
                }
            } else if(apiName.equals("loadOnScroll")){
                isScrolling = false;
                if(response.getString("status").equals("true")||response.getString("status").equals(true)){
                    showProgressBar(false);
                    if(!response.getString("result").equals("null")) {
                        JSONArray shopJArray = null;
                         if(!TextUtils.isEmpty(shopListType) && shopListType.equals("MarketStore")){
                            shopJArray = response.getJSONArray("result");
                        }else {
                             JSONObject jsonObject = response.getJSONObject("result").getJSONObject("shoplist");
                             shopJArray = jsonObject.getJSONArray("data");
                             offset = jsonObject.getInt("offset");
                         }

                        for (int i = 0; i < shopJArray.length(); i++) {
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

                            myShop.setLatitude(shopJArray.getJSONObject(i).getDouble("retLat"));
                            myShop.setLongitude(shopJArray.getJSONObject(i).getDouble("retLong"));
                            myShop.setDeliveryAvailable(shopJArray.getJSONObject(i).getString("isDeliveryAvailable"));
                            myShop.setMinDeliveryAmount(shopJArray.getJSONObject(i).getDouble("minDeliveryAmount"));
                            myShop.setMinDeliverytime(shopJArray.getJSONObject(i).getString("minDeliverytime"));
                            myShop.setMinDeliverydistance(shopJArray.getJSONObject(i).getInt("minDeliverydistance"));
                            myShop.setChargesAfterMinDistance(shopJArray.getJSONObject(i).getDouble("chargesAfterMinDistance"));

                            myShop.setDbname(shopJArray.getJSONObject(i).getString("dbname"));
                            myShop.setDbusername(shopJArray.getJSONObject(i).getString("dbuser"));
                            myShop.setDbpassword(shopJArray.getJSONObject(i).getString("dbpassword"));

                            itemList.add(myShop);
                        }
                        if (itemList.size() > 0) {
                            if(shopJArray.length() < limit){
                                isScroll = false;
                            }
                            if(shopJArray.length() > 0){
                                recyclerView.post(new Runnable() {
                                    public void run() {
                                        shopAdapter.notifyDataSetChanged();
                                    }
                                });
                                Log.d(TAG, "NEXT ITEMS LOADED");
                            }else{
                                Log.d(TAG, "NO ITEMS FOUND");
                            }

                        }
                    }else {
                        showProgressBar(false);
                        shopAdapter.notifyDataSetChanged();
                    }

                }else {
                    DialogAndToast.showDialog(response.getString("message"),ScrollShopListActivity.this);
                    showProgressBar(false);
                }
            }else if(apiName.equals("FavoriteShops")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)) {
                    JSONArray jsonArray = response.getJSONArray("result");
                    for(int i=0;i<jsonArray.length();i++) {
                        Log.d(TAG, jsonArray.getJSONObject(i).getString(""+i));
                      //  myfavoriteLists.add(jsonArray.getJSONObject(i).getString(""+i));
                    }
                    getNormalShops("initialLoading");
                }else {
                    showProgress(false);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            DialogAndToast.showToast(getResources().getString(R.string.json_parser_error)+e.toString(),ScrollShopListActivity.this);
            showProgress(false);
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
        } else if(item.getItemId()== R.id.action_search){
            //DialogAndToast.showToast("Search Clicked..", this);
            BottomSearchFragment bottomSearchFragment = new BottomSearchFragment();
            bottomSearchFragment.setCallingActivityName("ShopListActivity", sharedPreferences, isDarkTheme);
            bottomSearchFragment.setSubCatName(subCatName);
            bottomSearchFragment.setSubcatId(subCatId);
            bottomSearchFragment.setCatId(catId);
            bottomSearchFragment.show(getSupportFragmentManager(), bottomSearchFragment.getTag());
        }
        return super.onOptionsItemSelected(item);
    }

    public void makeCall(String mobile){
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:"+mobile));
        if(Utility.verifyCallPhonePermissions(this))
            startActivity(callIntent);
    }

    public void openWhatsApp(String mobile){
        try {
            mobile = "91"+mobile;
            String text = "This is a test";// Replace with your message.
            //   String toNumber = "xxxxxxxxxx"; // Replace with mobile phone number without +Sign or leading zeros, but with country code
            //Suppose your country is India and your phone number is “xxxxxxxxxx”, then you need to send “91xxxxxxxxxx”.
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://api.whatsapp.com/send?phone="+mobile +"&text="));
            startActivity(intent);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void showLargeImageDialog(MyShop shop,  View view){
        showImageDialog(shop.getShopimage(), view);
    }

}
