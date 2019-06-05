package com.shoppurscustomer.activities;

import android.graphics.PorterDuff;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.shoppurscustomer.R;
import com.shoppurscustomer.adapters.ShopAdapter;
import com.shoppurscustomer.fragments.BottomSearchFragment;
import com.shoppurscustomer.models.MyShop;
import com.shoppurscustomer.utilities.Constants;
import com.shoppurscustomer.utilities.DialogAndToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopListActivity extends NetworkBaseActivity {

    private List<Object> myNormalitemList;
    private List<Object> myFavoriteitemList;
    private List<String> myfavoriteLists;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private TextView textViewError, tv_myfav, tv_mynormal;
    private RecyclerView recyclerViewNormalshop, recyclerViewFavoriteshop;
    private ShopAdapter myNormalshopAdapter,myFavoriteshopAdapter ;
    private String subCatName, subCatId, catId;
    private float MIN_WIDTH = 200,MIN_HEIGHT = 230,MAX_WIDTH = 200,MAX_HEIGHT = 290;
    private String dbName, dbUserName, dbPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        subCatName = getIntent().getStringExtra("subCatName");
        subCatId = getIntent().getStringExtra("subCatId");
        catId = getIntent().getStringExtra("CatId");
        dbName = sharedPreferences.getString(Constants.DB_NAME,"");
        dbUserName = sharedPreferences.getString(Constants.DB_USER_NAME,"");
        dbPassword = sharedPreferences.getString(Constants.DB_PASSWORD,"");

        swipeRefreshLayout=findViewById(R.id.swipe_refresh);
        progressBar=findViewById(R.id.progress_bar);
        textViewError = findViewById(R.id.text_error);
        recyclerViewNormalshop=findViewById(R.id.recycler_view_normal_shop);
        recyclerViewFavoriteshop =findViewById(R.id.recycler_view_favorite_shop);
        tv_myfav = findViewById(R.id.tv_myfav);
        tv_mynormal = findViewById(R.id.tv_mynormal);
        myFavoriteitemList = new ArrayList<>();
        myNormalitemList = new ArrayList<>();
        myfavoriteLists = new ArrayList<>();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                getItemList();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.yellow500));
        }

        initFooter(this,1);
        getFavoriteShops();
       // getNormalShops();
    }

    private void getItemList(){
        swipeRefreshLayout.setRefreshing(false);
    }

    private void showNoData(boolean show){
        if(show){
            recyclerViewFavoriteshop.setVisibility(View.GONE);
            recyclerViewNormalshop.setVisibility(View.GONE);
            textViewError.setVisibility(View.VISIBLE);
        }else{
            recyclerViewNormalshop.setVisibility(View.VISIBLE);
            recyclerViewFavoriteshop.setVisibility(View.VISIBLE);
            textViewError.setVisibility(View.GONE);
        }
    }

    private void showProgressBar(boolean show){
        if(show){
            recyclerViewNormalshop.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            textViewError.setVisibility(View.GONE);
        }else{
            recyclerViewNormalshop.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void getFavoriteShops(){
        /*MyShop myShop = new MyShop();
        myShop.setId("12");
        myShop.setName("Rajender Da Dhaba");
        myShop.setMobile("9470564235");
        myShop.setAddress("Behind Kaml Cinema, Green Park New Delhi");
        myShop.setState("New Delhi");
        myShop.setCity("New Delhi");
        myShop.setShopimage("photo.jpg");
        myShop.setImage(R.drawable.thumb_21);
        myFavoriteitemList.add(myShop);
*/
        recyclerViewFavoriteshop.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerViewFavoriteshop.setLayoutManager(layoutManager);
        recyclerViewFavoriteshop.setItemAnimator(new DefaultItemAnimator());
        myFavoriteshopAdapter=new ShopAdapter(this,myFavoriteitemList,"shopList", catId, subCatId, subCatName);
        recyclerViewFavoriteshop.setAdapter(myFavoriteshopAdapter);

        Map<String,String> params=new HashMap<>();
        params.put("dbName",dbName);
        params.put("dbUserName",dbUserName);
        params.put("dbPassword",dbPassword);
        String url=getResources().getString(R.string.url)+"/shop/favourite_shops";
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"FavoriteShops");
    }

    private void getNormalShops(){
        recyclerViewNormalshop.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerViewNormalshop.setLayoutManager(layoutManager);
        recyclerViewNormalshop.setItemAnimator(new DefaultItemAnimator());
        myNormalshopAdapter=new ShopAdapter(this,myNormalitemList,"shopList", catId, subCatId, subCatName);
        recyclerViewNormalshop.setAdapter(myNormalshopAdapter);

        Map<String,String> params=new HashMap<>();
        params.put("subcatid",subCatId);
        String url=getResources().getString(R.string.url)+"/shoplist";
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"NormalShops");
    }



    @Override
    public void onJsonObjectResponse(JSONObject response, String apiName) {
        showProgress(false);
        try {
            // JSONObject jsonObject=response.getJSONObject("response");
            Log.d("response", response.toString());
            if(apiName.equals("NormalShops")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)){

                    JSONObject jsonObject = response.getJSONObject("result");
                    JSONArray shopJArray = jsonObject.getJSONArray("shoplist");

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
                        myShop.setImage(R.drawable.thumb_21);
                        Log.d(myfavoriteLists.toString(), "shop_code"+ shop_code);
                        if(myfavoriteLists.contains(shop_code)){
                            myFavoriteitemList.add(myShop);
                        }else
                        myNormalitemList.add(myShop);

                        //"retcode":"shop_8","retname":"Vipin Dhama","retshopname":"Dhama Test 1","retmobile":"9718181697","retlanguage":"English","retaddress":"Delhi","retpincode":"110091","retemail":"vipinsuper19@gmail.com","retphoto":"","retpassword":"1234","retcountry":"India","retstate":"Delhi","retcity":"Delhi","serverip":"49.50.77.154","dbname":"shop_8","dbuser":"shoppurs_master","dbpassword":"$hop@2018#"
                    }

                    if(myNormalitemList.size()>0){
                        myNormalshopAdapter.notifyDataSetChanged();
                    }else tv_mynormal.setVisibility(View.GONE);
                    if(myFavoriteitemList.size()>0){
                        myFavoriteshopAdapter.notifyDataSetChanged();
                    }else tv_myfav.setVisibility(View.GONE);
                    if(myNormalitemList.size() ==0 && myFavoriteitemList.size() ==0){
                        showNoData(true);
                    }

                }else {
                    DialogAndToast.showDialog(response.getString("message"),ShopListActivity.this);
                }
            }else if(apiName.equals("FavoriteShops")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)) {
                    JSONArray jsonArray = response.getJSONArray("result");
                    for(int i=0;i<jsonArray.length();i++) {
                        Log.d(TAG, jsonArray.getJSONObject(i).getString(""+i));
                        myfavoriteLists.add(jsonArray.getJSONObject(i).getString(""+i));
                    }
                    getNormalShops();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            DialogAndToast.showToast(getResources().getString(R.string.json_parser_error)+e.toString(),ShopListActivity.this);
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
            bottomSearchFragment.setCallingActivityName("ShopList");
            bottomSearchFragment.setSubCatName(subCatName);
            bottomSearchFragment.setSubcatId(subCatId);
            bottomSearchFragment.setCatId(catId);
            bottomSearchFragment.show(getSupportFragmentManager(), bottomSearchFragment.getTag());
        }
        return super.onOptionsItemSelected(item);
    }
}
