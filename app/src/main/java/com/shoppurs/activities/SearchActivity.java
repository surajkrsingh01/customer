package com.shoppurs.activities;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;

import com.android.volley.Request;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.material.appbar.AppBarLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shoppurs.R;
import com.shoppurs.activities.Settings.AddressActivity;
import com.shoppurs.activities.Settings.SettingActivity;
import com.shoppurs.adapters.PopulatCategoriesAdapter;
import com.shoppurs.adapters.ShopAdapter;
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

public class SearchActivity extends NetworkBaseActivity{

    private Toolbar toolbar;
    private AppBarLayout appBarLayout;
    private RecyclerView recycler_popular_category, recycler_popular_tags, recyclerView_shops;
    private List<Category> categoryList, tagList;
    private PopulatCategoriesAdapter populatCategoriesAdapter;
    private ShopAdapter shopAdapter;
    private List<Object> shopList;
    private TextView cancel;
    private EditText et_search;
    private TextView text_customer_address, text_shops, text_popular_category, text_popular_tags, text_error;
    private CircleImageView customer_profile;
    private ProgressBar progress_bar;
    String searchCatId;
    private boolean isCategorySearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        initFooter(this,3);

        init();
    }

    private void init(){
        shopList = new ArrayList<>();
        et_search = findViewById(R.id.et_search);
        ((GradientDrawable)et_search.getBackground()).setColor(colorTheme);
        cancel = findViewById(R.id.cancel);
        text_shops = findViewById(R.id.text_shops);
        text_popular_category = findViewById(R.id.text_popular_category);
        text_popular_tags = findViewById(R.id.text_popular_tags);
        recyclerView_shops = findViewById(R.id.recycler_shop_list);
        recycler_popular_category = findViewById(R.id.recycler_popular_category);
        recycler_popular_tags =  findViewById(R.id.recycler_popular_tags);

        text_error = findViewById(R.id.text_error);
        progress_bar = findViewById(R.id.progress_bar);

        getTopCategories();
        getTopTags();

        recycler_popular_category.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        recycler_popular_category.setLayoutManager(layoutManager);
        recycler_popular_category.setItemAnimator(new DefaultItemAnimator());
        populatCategoriesAdapter = new PopulatCategoriesAdapter(this, categoryList);
        populatCategoriesAdapter.setTheme(isDarkTheme, colorTheme);
        recycler_popular_category.setAdapter(populatCategoriesAdapter);
        recycler_popular_category.setNestedScrollingEnabled(false);

        recycler_popular_tags.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(this, RecyclerView.VERTICAL,false);
        recycler_popular_tags.setLayoutManager(layoutManager1);
        recycler_popular_tags.setItemAnimator(new DefaultItemAnimator());
        populatCategoriesAdapter = new PopulatCategoriesAdapter(this, tagList);
        populatCategoriesAdapter.setTheme(isDarkTheme, colorTheme);
        recycler_popular_tags.setAdapter(populatCategoriesAdapter);
        recycler_popular_tags.setNestedScrollingEnabled(false);



        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_search.setText("");
                cancel.setVisibility(View.GONE);
                showShopListRecycleView(false);
            }
        });

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!TextUtils.isEmpty(s)){
                    cancel.setVisibility(View.VISIBLE);
                    showShopListRecycleView(true);
                    if(isCategorySearch) {
                        isCategorySearch = false;
                        searchShopsbyCategory(searchCatId);
                    }
                    else{
                        searchShopbyQuery(s.toString());
                    }
                }else {
                    cancel.setVisibility(View.GONE);
                    showShopListRecycleView(false);
                }
            }
        });

        text_customer_address = findViewById(R.id.text_customer_address);
        if(TextUtils.isEmpty(sharedPreferences.getString(Constants.CUST_ADDRESS, "")))
            text_customer_address.setText("Update Your Location");
        else
            text_customer_address.setText(sharedPreferences.getString(Constants.CUST_ADDRESS, ""));
        text_customer_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchActivity.this, AddressActivity.class);
                startActivity(intent);
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
                Intent intent = new Intent(SearchActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });
    }

    private void searchShopsbyCategory(String catId){
        String subCatId = "1";
        String subCatName = "";
        shopList.clear();
        recyclerView_shops.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView_shops.setLayoutManager(layoutManager);
        recyclerView_shops.setItemAnimator(new DefaultItemAnimator());
        shopAdapter=new ShopAdapter(this,shopList,"shopList", catId, subCatId, subCatName);
        shopAdapter.setFlag("SearchActivity");
        recyclerView_shops.setAdapter(shopAdapter);

        Map<String,String> params=new HashMap<>();
        params.put("id",catId);
        params.put("limit","10");
        params.put("offset","0");
        params.put("dbName", sharedPreferences.getString(Constants.DB_NAME, ""));
        String url=getResources().getString(R.string.root_url)+"search/cat/shops";
        showProgressBar(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"NormalShops");
    }

    private void searchShopbyQuery(String query){
        shopList.clear();
        showNoData(false);
        recyclerView_shops.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView_shops.setLayoutManager(layoutManager);
        recyclerView_shops.setItemAnimator(new DefaultItemAnimator());
        shopAdapter=new ShopAdapter(this,shopList,"shopList", "", "", "");
        shopAdapter.setFlag("SearchActivity");
        recyclerView_shops.setAdapter(shopAdapter);

        Map<String, String> params = new HashMap<>();
        params.put("query",query);
        params.put("limit", "10");
        params.put("offset", ""+shopList.size());
        params.put("dbName",sharedPreferences.getString(Constants.DB_NAME,""));
        params.put("dbUserName",sharedPreferences.getString(Constants.DB_USER_NAME,""));
        params.put("dbPassword",sharedPreferences.getString(Constants.DB_PASSWORD,""));
        String url=getResources().getString(R.string.root_url)+"search/shops";
        showProgressBar(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"NormalShops");
    }

    private void showShopListRecycleView(boolean visible){
        if(visible){
            text_shops.setVisibility(View.VISIBLE);
            recyclerView_shops.setVisibility(View.VISIBLE);

            text_popular_tags.setVisibility(View.GONE);
            text_popular_category.setVisibility(View.GONE);
            recycler_popular_category.setVisibility(View.GONE);
            recycler_popular_tags.setVisibility(View.GONE);
            showProgressBar(true);
        }else {
            text_shops.setVisibility(View.GONE);
            text_error.setVisibility(View.GONE);
            recyclerView_shops.setVisibility(View.GONE);

            text_popular_tags.setVisibility(View.VISIBLE);
            text_popular_category.setVisibility(View.VISIBLE);
            recycler_popular_category.setVisibility(View.VISIBLE);
            recycler_popular_tags.setVisibility(View.VISIBLE);
            showProgressBar(false);
        }
    }

    private void showNoData(boolean show){
        if(show){
            recyclerView_shops.setVisibility(View.GONE);
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

    private void getTopCategories(){
        categoryList = new ArrayList<>();
        Category category;
        category = new Category();
        category.setName("Grocery");
        category.setId("1");
        categoryList.add(category);

        category = new Category();
        category.setName("Stationary");
        category.setId("2");
        categoryList.add(category);

        category = new Category();
        category.setName("Fashion");
        category.setId("5");
        categoryList.add(category);

        category = new Category();
        category.setName("Electronics");
        category.setId("100");
        categoryList.add(category);

        category = new Category();
        category.setName("Jewellery");
        category.setId("6");
        categoryList.add(category);

        category = new Category();
        category.setName("Restaurants");
        category.setId("100");
        categoryList.add(category);
    }

    private void getTopTags(){
        tagList = new ArrayList<>();
        Category category;
        category = new Category();
        category.setName("Restaurants Nearby");
        category.setId("100");
        tagList.add(category);

        category = new Category();
        category.setName("Grocery Stores Nearby");
        category.setId("1");
        tagList.add(category);

        category = new Category();
        category.setName("Fashoion Stores");
        category.setId("100");
        tagList.add(category);

        category = new Category();
        category.setName("Toy Stores");
        category.setId("100");
        tagList.add(category);

        category = new Category();
        category.setName("Stationary Stores");
        category.setId("2");
        tagList.add(category);

        category = new Category();
        category.setName("Home Appliances Stores");
        category.setId("100");
        tagList.add(category);
    }


    public void setEt_search(String search_value, String catID){
         isCategorySearch = true;
        searchCatId = catID;
        et_search.setText(search_value);
    }

    @Override
    public void onJsonObjectResponse(JSONObject response, String apiName) {
        showProgressBar(false);
        try {
            // JSONObject jsonObject=response.getJSONObject("response");
            Log.d("response", response.toString());
            if(apiName.equals("NormalShops")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)){

                    //JSONObject jsonObject = response.getJSONObject("result");
                    JSONArray shopJArray = response.getJSONArray("result");
                    shopList.clear();
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
                        myShop.setLatitude(shopJArray.getJSONObject(i).getDouble("retLat"));
                        myShop.setLongitude(shopJArray.getJSONObject(i).getDouble("retLong"));
                        myShop.setDeliveryAvailable(shopJArray.getJSONObject(i).getString("isDeliveryAvailable"));
                        myShop.setMinDeliveryAmount(shopJArray.getJSONObject(i).getDouble("minDeliveryAmount"));

                        myShop.setDbname(shopJArray.getJSONObject(i).getString("dbname"));
                        myShop.setDbusername(shopJArray.getJSONObject(i).getString("dbuser"));
                        myShop.setDbpassword(shopJArray.getJSONObject(i).getString("dbpassword"));
                        myShop.setImage(R.drawable.thumb_21);
                        Log.d(shopList.toString(), "shop_code"+ shop_code);

                        shopList.add(myShop);

                        //"retcode":"shop_8","retname":"Vipin Dhama","retshopname":"Dhama Test 1","retmobile":"9718181697","retlanguage":"English","retaddress":"Delhi","retpincode":"110091","retemail":"vipinsuper19@gmail.com","retphoto":"","retpassword":"1234","retcountry":"India","retstate":"Delhi","retcity":"Delhi","serverip":"49.50.77.154","dbname":"shop_8","dbuser":"shoppurs_master","dbpassword":"$hop@2018#"
                    }

                    if(shopList.size()>0){
                        shopAdapter.notifyDataSetChanged();
                    }else {
                        showNoData(true);
                    }

                }else {
                    DialogAndToast.showDialog(response.getString("message"),SearchActivity.this);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            DialogAndToast.showToast(getResources().getString(R.string.json_parser_error)+e.toString(),SearchActivity.this);
        }
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
