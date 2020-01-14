package com.shoppurs.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.shoppurs.R;
import com.shoppurs.activities.Settings.SettingActivity;
import com.shoppurs.adapters.StoreAdapter;
import com.shoppurs.fragments.BottomLocationFragment;
import com.shoppurs.interfaces.LocationActionListener;
import com.shoppurs.models.CatListItem;
import com.shoppurs.models.Category;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class StoresListActivity extends BaseLocation implements LocationActionListener {

    private RecyclerView recyclerView;
    private StoreAdapter myItemAdapter;
    private List<Object> itemList;
    private TextView textViewError;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    List<Object> favoriteShopList = new ArrayList<>();
    List<Object> normalShopList = new ArrayList<>();
    List<String> myfavoriteShopIds = new ArrayList<>();
    private CircleImageView customer_profile;
    private ImageView iv_cart;
    private TextView text_customer_address;
    private boolean isVisible,loadingComplete, isUpdateAvailable;
    protected ProgressDialog progressDialog;



    private float MIN_WIDTH = 200,MIN_HEIGHT = 230,MAX_WIDTH = 200,MAX_HEIGHT = 290;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stores);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        itemList = new ArrayList<>();
        //getDemoLists();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading..");

        swipeRefreshLayout=findViewById(R.id.swipe_refresh);
        progressBar=findViewById(R.id.progress_bar);
        textViewError = findViewById(R.id.text_error);
        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        myItemAdapter=new StoreAdapter(this,itemList,"catList");
        myItemAdapter.setColorTheme(colorTheme);
        recyclerView.setAdapter(myItemAdapter);

        text_customer_address = findViewById(R.id.text_customer_address);
        if(TextUtils.isEmpty(sharedPreferences.getString(Constants.CUST_CURRENT_ADDRESS, "")))
            text_customer_address.setText("Update Your Location");
        else
            text_customer_address.setText(sharedPreferences.getString(Constants.CUST_CURRENT_ADDRESS, ""));
        text_customer_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLocationBottomShet();
            }
        });

        initFooter(this,2);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                getItemList();
            }
        });
        iv_cart = findViewById(R.id.iv_cart);
        iv_cart.setColorFilter(colorTheme,
                android.graphics.PorterDuff.Mode.SRC_IN);
        iv_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StoresListActivity.this, CartActivity.class));
            }
        });
        customer_profile= findViewById(R.id.profile_image);
        customer_profile.setCircleBackgroundColor(colorTheme);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        // requestOptions.override(Utility.dpToPx(150, context), Utility.dpToPx(150, context));
        requestOptions.centerCrop();
        requestOptions.skipMemoryCache(false);
        requestOptions.signature(new ObjectKey(sharedPreferences.getString("profile_image_signature","")));
        Glide.with(this)
                .load(sharedPreferences.getString(Constants.PROFILE_PIC, ""))
                .apply(requestOptions)
                .error(R.drawable.ic_photo_black_192dp)
                .into(customer_profile);
        customer_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StoresListActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        getCurrentLocation();
        showProgressBar(true);

        if(!sharedPreferences.getBoolean(Constants.IS_TOKEN_SAVED, false)){
            saveToken();
        }
    }

    public void saveToken(){
        Map<String,String> params=new HashMap<>();
        params.put("mobile", sharedPreferences.getString(Constants.MOBILE_NO, ""));
        params.put("userType", "customer");
        params.put("token",sharedPreferences.getString(Constants.FCM_TOKEN, ""));
        params.put("dbName", sharedPreferences.getString(Constants.DB_NAME,""));
        String url=getResources().getString(R.string.url_customer)+"/api/customers/save_fcm_token";
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"saveToken");
    }

    public void getAllCategories(){
        loadingComplete = false;
        Map<String,String> params=new HashMap<>();
        if(!TextUtils.isEmpty(sharedPreferences.getString(Constants.CUST_CURRENT_ADDRESS,""))){
            params.put("lattitude", sharedPreferences.getString(Constants.CUST_CURRENT_LAT, ""));
            params.put("longitude", sharedPreferences.getString(Constants.CUST_CURRENT_LONG, ""));
        }else {
            params.put("lattitude", sharedPreferences.getString(Constants.CUST_LAT, ""));
            params.put("longitude", sharedPreferences.getString(Constants.CUST_LONG, ""));
        }
        params.put("dbName", sharedPreferences.getString(Constants.DB_NAME,""));
        String url=getResources().getString(R.string.url_customer)+"/api/customers/cat_subcat";
        showProgressBar(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"categories");
    }

    private void getFavoriteStores(){
        Map<String,String> params=new HashMap<>();
        params.put("dbName", sharedPreferences.getString(Constants.DB_NAME, ""));
        String url=getResources().getString(R.string.url_customer)+"/api/customers/shop/favourite_shops";
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"FavoriteShops");
    }

    private void getNormalStores(){
        Map<String,String> params=new HashMap<>();
        if(!TextUtils.isEmpty(sharedPreferences.getString(Constants.CUST_CURRENT_ADDRESS,""))){
            params.put("lattitude", sharedPreferences.getString(Constants.CUST_CURRENT_LAT, ""));
            params.put("longitude", sharedPreferences.getString(Constants.CUST_CURRENT_LONG, ""));
        }else {
            params.put("lattitude", sharedPreferences.getString(Constants.CUST_LAT, ""));
            params.put("longitude", sharedPreferences.getString(Constants.CUST_LONG, ""));
        }
        params.put("dbName", sharedPreferences.getString(Constants.DB_NAME, ""));
        String url=getResources().getString(R.string.url_customer)+"/api/customers/allshoplist";
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"NormalShops");
    }


    @Override
    public void onJsonObjectResponse(JSONObject response, String apiName) {
        try {
            // JSONObject jsonObject=response.getJSONObject("response");
            Log.d("response", response.toString());

            if(apiName.equals("categories")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)){
                    JSONObject jsonObject = response.getJSONObject("result");
                    JSONArray catJArray = jsonObject.getJSONArray("categories");
                    List<Object> catList = new ArrayList<>();
                    Category category = new Category();
                    category.setName("Scan Stores");
                    catList.add(category);
                    for(int i=0;i<catJArray.length();i++){
                        category= new Category();
                        category.setId(catJArray.getJSONObject(i).getString("catId"));
                        category.setName(catJArray.getJSONObject(i).getString("catName"));
                        category.setImage(catJArray.getJSONObject(i).getString("catImage"));
                        catList.add(category);
                    }
                    if(catList.size()>0) {
                        Log.d("catList size ", catList.size() + "");
                        CatListItem myItem = new CatListItem();
                        String customerAddress = sharedPreferences.getString(Constants.CUST_ADDRESS,"");
                       // myItem.setTitle("AB 7 Safdarjan Enclave Behind Kamal Cinema ");
                       // myItem.setDesc("Stores to Shop");
                        myItem.setType(0);
                        myItem.setItemList(catList);
                        itemList.add(myItem);
                        getFavoriteStores();
                    }else{
                        showProgressBar(false);
                    }

                }else {
                    DialogAndToast.showDialog(response.getString("message"),StoresListActivity.this);
                    showProgressBar(false);
                }
            } else if(apiName.equals("NormalShops")){
                loadingComplete = true;
                if(response.getString("status").equals("true")||response.getString("status").equals(true)){
                    showProgressBar(false);
                    if(!response.getString("result").equals("null")) {
                        JSONObject jsonObject = response.getJSONObject("result");
                        JSONArray shopJArray = jsonObject.getJSONArray("shoplist");

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
                            myShop.setImage(R.drawable.thumb_21);
                            //Log.d(favoriteShopList.toString(), "shop_code"+ shop_code);
                            if (myfavoriteShopIds.contains(shop_code)) {
                                favoriteShopList.add(myShop);
                            } else
                                normalShopList.add(myShop);

                            //"retcode":"shop_8","retname":"Vipin Dhama","retshopname":"Dhama Test 1","retmobile":"9718181697","retlanguage":"English","retaddress":"Delhi","retpincode":"110091","retemail":"vipinsuper19@gmail.com","retphoto":"","retpassword":"1234","retcountry":"India","retstate":"Delhi","retcity":"Delhi","serverip":"49.50.77.154","dbname":"shop_8","dbuser":"shoppurs_master","dbpassword":"$hop@2018#"
                        }

                        Log.d("itemlist size  ", itemList.size() + "");
                        Log.d("favoriteShopList size  ", favoriteShopList.size() + "");
                        Log.d("normalShopList size  ", normalShopList.size() + "");
                        if (favoriteShopList.size() > 0) {
                            CatListItem myItem1 = new CatListItem();
                            myItem1.setTitle("My Favourite Stores");
                            myItem1.setType(1);
                            myItem1.setItemList(favoriteShopList);
                            itemList.add(myItem1);
                            myItemAdapter.notifyDataSetChanged();
                        }//else tv_myfav.setVisibility(View.GONE);

                        if (normalShopList.size() > 0) {
                            CatListItem myItem1 = new CatListItem();
                            myItem1.setTitle("My Stores");
                            myItem1.setType(1);
                            myItem1.setItemList(normalShopList);
                            itemList.add(myItem1);
                            myItemAdapter.notifyDataSetChanged();
                        }
                        if (itemList.size() == 0) {
                            showNoData(true);
                        }else  myItemAdapter.notifyDataSetChanged();
                    }else {
                        //DialogAndToast.showDialog(response.getString("message"),StoresListActivity.this);
                        showProgressBar(false);
                        myItemAdapter.notifyDataSetChanged();
                    }

                }else {
                    DialogAndToast.showDialog(response.getString("message"),StoresListActivity.this);
                    showProgressBar(false);
                }
            }else if(apiName.equals("FavoriteShops")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)) {
                    JSONArray jsonArray = response.getJSONArray("result");
                    for(int i=0;i<jsonArray.length();i++) {
                        Log.d(TAG, jsonArray.getJSONObject(i).getString(""+i));
                        myfavoriteShopIds.add(jsonArray.getJSONObject(i).getString(""+i));
                    }
                    getNormalStores();
                }else {
                    showProgressBar(false);
                }
            }else if(apiName.equals("saveToken")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)) {
                    editor.putBoolean(Constants.IS_TOKEN_SAVED, true);
                    editor.commit();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            DialogAndToast.showToast(getResources().getString(R.string.json_parser_error)+e.toString(),StoresListActivity.this);
        }
    }


    private void getItemList(){
        favoriteShopList.clear();
        normalShopList.clear();
        itemList.clear();
        swipeRefreshLayout.setRefreshing(false);
        getAllCategories();
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
            progressBar.setVisibility(View.VISIBLE);
        }else{
            progressBar.setVisibility(View.GONE);
        }
    }

    public void scanBarCode(){
        Intent intent = new Intent(this,ScannarActivity.class);
        intent.putExtra("flag","scan");
        intent.putExtra("type","scanStores");
        // startActivity(intent);
        startActivityForResult(intent,111);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                Log.d(TAG, contents);
                Log.d(TAG, format);
                // Handle successful scan
            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
            }
        }
    }

    private void getDemoLists(){
        itemList = new ArrayList<>();
        CatListItem myItem = new CatListItem();
        myItem.setTitle("NEAR BY");
        myItem.setDesc("Stores to Shop");
        myItem.setType(0);
        List<Object> catList = new ArrayList<>();

        Category category = new Category();
        category.setName("Scan");
        catList.add(category);

        category = new Category();
        category.setName("Yor grocery store just around the corner");
        category.setLocalImage(R.drawable.thumb_14);
        catList.add(category);

        category = new Category();
        category.setName("Your stationary store here");
        category.setLocalImage(R.drawable.thumb_15);
        catList.add(category);
        myItem.setItemList(catList);
        itemList.add(myItem);

        CatListItem myItem1 = new CatListItem();
        myItem1.setTitle("My Favorite Stores");
        myItem1.setType(1);
        List<Object> shopList = new ArrayList<>();

        MyShop myshop = new MyShop();
        myshop.setAddress("The Best Diet for a Flatter Belly");
        myshop.setName("BIRYANI BLUES");
        myshop.setImage(R.drawable.thumb_11);
        myshop.setType(8);
        shopList.add(myshop);

        myshop = new MyShop();
        myshop.setAddress("Were Vegetables Better Before?");
        myshop.setName("AROGYA MEALS");
        myshop.setImage(R.drawable.thumb_12);
        myshop.setType(8);
        shopList.add(myshop);

        myshop = new MyShop();
        myshop.setAddress("Here Are the Five Best Fruits of the");
        myshop.setName("DANA CHOGA");
        myshop.setImage(R.drawable.thumb_13);
        myshop.setType(8);
        shopList.add(myshop);

        myItem1.setItemList(shopList);
        itemList.add(myItem1);
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

    public void showLocationBottomShet(){
        BottomLocationFragment locationFragment = new BottomLocationFragment();
        locationFragment.setListener(this);
        locationFragment.initFragment(colorTheme, isDarkTheme, "Choose Location", sharedPreferences.getString(Constants.CUST_CURRENT_ADDRESS, ""));
        locationFragment.show(getSupportFragmentManager(), "Location Action Bottom Sheet");
    }

    @Override
    public void onResume() {
        super.onResume();
        isVisible = true;
        if(TextUtils.isEmpty(sharedPreferences.getString(Constants.CUST_CURRENT_ADDRESS, "")))
            text_customer_address.setText("Update Your Location");
        else
            text_customer_address.setText(sharedPreferences.getString(Constants.CUST_CURRENT_ADDRESS, ""));

        if(isUpdateAvailable){
            getItemList();
            isUpdateAvailable = false;
        }
    }

    @Override
    public void updateUi() {
        super.updateUi();
        isUpdateAvailable = true;
        if(isVisible) {
            text_customer_address.setText(sharedPreferences.getString(Constants.CUST_CURRENT_ADDRESS, ""));
            //if(loadingComplete) {
            // showProgress(false);
            //swipeRefreshLayout.setRefreshing(true);
            getItemList();
            //}
        }
    }

    @Override
    public void onLocationAction(String type, String address) {
        if(type.equals("Search Location")){
            searchPlaces();
        }else if(type.equals("Get Current Location")){
            getCurrentLocation();
            showProgressBar(true);
        }else if(type.equals("Update Location")){
            updateCurrentLocation(address);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isVisible = false;
    }

}