package com.shoppurs.activities.Settings;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.shoppurs.R;
import com.shoppurs.activities.CartActivity;
import com.shoppurs.activities.HandleCartActivity;
import com.shoppurs.activities.ProductDetailActivity;
import com.shoppurs.activities.ScannarActivity;
import com.shoppurs.adapters.FrequencyProductListAdapter;
import com.shoppurs.database.DbHelper;
import com.shoppurs.fragments.BottomSearchFragment;
import com.shoppurs.fragments.FrequencyFragment;
import com.shoppurs.models.MyProduct;
import com.shoppurs.models.ProductFrequency;
import com.shoppurs.models.ShopDeliveryModel;
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

public class FrequencyProductsActivity extends HandleCartActivity {

    private Toolbar toolbar;
    private Menu menu;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerViewProduct;
    private TextView tvNoData, cartItemCount, cartItemPrice, viewCart;
    private TextView text_shop_name, tv_shortName, text_mobile, text_address, text_state_city, text_left_label, text_right_label;
    private ProgressBar progressBar;
    private RelativeLayout rlfooterviewcart;
    private FrequencyProductListAdapter shopProductAdapter;
    private List<MyProduct> myProductList;
    private DbHelper dbHelper;
    private ImageView image_view_shop, image_fav, image_search, image_scan;
    private String shopName, shopCode, shopdbname,dbuser, dbpassword, custdbname;

    private int position, type, productDetailsType;
    private int counter;
    private ShopDeliveryModel shopDeliveryModel;
    private Typeface typeface;
    private boolean isFrequencySelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frequency_products);
        dbHelper = new DbHelper(this);
        shopCode = getIntent().getStringExtra("shop_code");
        shopName = getIntent().getStringExtra("name");
        shopdbname = getIntent().getStringExtra("dbname");
        dbuser = getIntent().getStringExtra("dbuser");
        dbpassword = getIntent().getStringExtra("dbpassword");
        shopDeliveryModel = new ShopDeliveryModel();
        shopDeliveryModel = (ShopDeliveryModel) getIntent().getSerializableExtra("shopDeliveryModel");
        custdbname = sharedPreferences.getString(Constants.DB_NAME, "");
        initViews();
    }

    private void initViews(){
        myProductList = new ArrayList<>();
        text_shop_name = findViewById(R.id.text_shop_name);
        text_shop_name.setText(shopName);
        tv_shortName = findViewById(R.id.tv_shortName);
        image_view_shop = findViewById(R.id.image_view_shop);
        image_fav = findViewById(R.id.image_fav);
        Log.d("isShopFavourite ", ""+dbHelper.isShopFavorited(shopCode));
        if(dbHelper.isShopFavorited(shopCode))
            image_fav.setImageDrawable(ContextCompat.getDrawable(FrequencyProductsActivity.this,R.drawable.favroite_selected));
        else image_fav.setImageDrawable(ContextCompat.getDrawable(FrequencyProductsActivity.this, R.drawable.favrorite_select));

        image_search = findViewById(R.id.image_search);
        image_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dbHelper.isShopFavorited(shopCode))
                    shopFavorite("N");
                else shopFavorite("Y");
            }
        });

        image_search.setVisibility(View.GONE);
        image_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSearchFragment bottomSearchFragment = new BottomSearchFragment();
                Bundle bundle = new Bundle();
                bundle.putString("shopCode", shopCode);
                bundle.putString("shopName", shopName);
                bottomSearchFragment.setArguments(bundle);
                bottomSearchFragment.setTypeFace(Utility.getSimpleLineIconsFont(FrequencyProductsActivity.this));
                bottomSearchFragment.setCallingActivityName("FrequencyProductListActivity", sharedPreferences, isDarkTheme);
                bottomSearchFragment.show(getSupportFragmentManager(), bottomSearchFragment.getTag());
            }
        });

        image_scan = findViewById(R.id.image_scan);
        image_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FrequencyProductsActivity.this, ScannarActivity.class);
                intent.putExtra("flag","scan");
                intent.putExtra("type","scanProducts");
                intent.putExtra("shopCode",shopCode);
                // startActivity(intent);
                startActivityForResult(intent,112);
            }
        });

        text_left_label = findViewById(R.id.text_left_label);
        text_right_label = findViewById(R.id.text_right_label);
        text_left_label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(ShopProductListActivity.this, ShopListActivity.class));
                finish();
            }
        });

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        swipeRefreshLayout =findViewById(R.id.swipe_refresh);
        // tv_shopname = findViewById(R.id.tv_shopname);
        recyclerViewProduct = findViewById(R.id.recycler_viewProduct);
        tvNoData = findViewById(R.id.tvNoData);
        progressBar=findViewById(R.id.progress_bar);
        rlfooterviewcart = findViewById(R.id.rlfooterviewcart);
        cartItemCount= findViewById(R.id.itemCount);
        cartItemPrice = findViewById(R.id.itemPrice);
        viewCart = findViewById(R.id.viewCart);

        recyclerViewProduct.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewProduct.setLayoutManager(layoutManager);
        recyclerViewProduct.setItemAnimator(new DefaultItemAnimator());
        shopProductAdapter = new FrequencyProductListAdapter(this, myProductList);
        shopProductAdapter.setTypeFace(Utility.getSimpleLineIconsFont(this));
        shopProductAdapter.setShopCode(shopCode);
        shopProductAdapter.setDarkTheme(isDarkTheme);
        recyclerViewProduct.setAdapter(shopProductAdapter);

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

        Map<String,String> params=new HashMap<>();
        // params.put("subCatId", subCatId); // as per user selected category from top horizontal categories list
        params.put("code", shopCode);
        params.put("limit", "10");
        params.put("offset", myProductList.size()+"");
        params.put("dbName",sharedPreferences.getString(Constants.DB_NAME, ""));
        Log.d(TAG, params.toString());
        String url=getResources().getString(R.string.url_customer)+"/api/order/get_frequency_order";
        showProgressBar(true);
        jsonObjectApiRequest(Request.Method.POST, url,new JSONObject(params),"getProducts");
    }



    public void updateFrequencyCart(int type, int position){
        Log.d("clicked Position ", position+"");
        this.position = position;
        this.type = type;

        if(myProductList.get(position).getFrequency()!=null)
            addFrequencyProducttoCart(type, position, shopCode, myProductList.get(position),shopDeliveryModel, this);
    }

    public void shopFavorite(String status){
        Map<String,String> params=new HashMap<>();
        params.put("favStatus", status);
        params.put("code",shopCode);
        params.put("dbName", custdbname);
        params.put("dbUserName",sharedPreferences.getString(Constants.DB_USER_NAME, ""));
        params.put("dbPassword",sharedPreferences.getString(Constants.DB_PASSWORD, ""));
        Log.d(TAG, params.toString());
        String url=getResources().getString(R.string.url_customer)+"/api/customers/shop/favourite";
        //showProgressBar(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"shopFavorite");
    }

    @Override
    public void onJsonObjectResponse(JSONObject response, String apiName) {
        showProgressBar(false);
        try {
            // JSONObject jsonObject=response.getJSONObject("response");
            Log.d("response", response.toString());
            if(apiName.equals("getProducts")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)) {
                    JSONObject dataObject;
                    if(!response.getString("result").equals("null")){
                        JSONArray orderJArray = response.getJSONArray("result");
                        JSONObject jsonObject = null;
                        ProductFrequency frequency = null;
                        MyProduct productItem = null;
                        for (int i = 0; i < orderJArray.length(); i++) {
                            JSONArray productJArray = orderJArray.getJSONObject(i).getJSONArray("myProductList");
                            jsonObject = productJArray.getJSONObject(0);
                            productItem = new MyProduct();
                            productItem.setShopCode(shopCode);
                            productItem.setId(jsonObject.getString("prodId"));
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

                            frequency = new ProductFrequency();
                            frequency.setName(jsonObject.getString("frequency"));
                            frequency.setQyantity(jsonObject.getString("frequencyQty"));
                            frequency.setNoOfDays(jsonObject.getString("frequencyValue"));
                            frequency.setStartDate(jsonObject.getString("frequencyStartDate"));
                            frequency.setEndDate(jsonObject.getString("frequencyEndDate"));
                            frequency.setNextOrderDate(jsonObject.getString("nextOrderDate"));
                            frequency.setLastOrderDate(jsonObject.getString("lastOrderDate"));
                            frequency.setStatus(jsonObject.getString("status"));
                            productItem.setFrequency(frequency);
                            myProductList.add(productItem);
                        }
                    }

                    if(myProductList.size()>0){
                        Log.d("myProductList size ", myProductList.size()+"");
                        shopProductAdapter.notifyDataSetChanged();
                    }else {
                        showNoData(true);
                    }

                }else {
                    DialogAndToast.showDialog(response.getString("message"),FrequencyProductsActivity.this);
                }
            }else if(apiName.equals("shopFavorite")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)){
                    Log.d(TAG, "Added in Favorite Shop" );
                    if(dbHelper.isShopFavorited(shopCode)) {
                        dbHelper.remove_from_Favorite(shopCode);
                        image_fav.setImageDrawable(ContextCompat.getDrawable(FrequencyProductsActivity.this, R.drawable.favrorite_select));
                    }else {
                        dbHelper.add_to_Favorite(shopCode);
                        image_fav.setImageDrawable(ContextCompat.getDrawable(FrequencyProductsActivity.this, R.drawable.favroite_selected));
                    }
                }else {
                    DialogAndToast.showToast(response.getString("message"),FrequencyProductsActivity.this);
                }
            }else if(apiName.equals("shopRating")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)){
                    Log.d(TAG, "Rated Shop" );
                }else {
                    DialogAndToast.showToast(response.getString("message"),FrequencyProductsActivity.this);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            DialogAndToast.showToast(getResources().getString(R.string.json_parser_error)+e.toString(),FrequencyProductsActivity.this);
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

    private void showProgressBar(boolean show){
        if(show){
            recyclerViewProduct.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            tvNoData.setVisibility(View.GONE);
        }else{
            recyclerViewProduct.setVisibility(View.VISIBLE);
            tvNoData.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(shopProductAdapter!=null) {
            shopProductAdapter.notifyDataSetChanged();
            counter = dbHelper.getCartCount();
        }
    }

    public void setFrequencySelecte(boolean frequencySelecte, ProductFrequency frequency, int position) {
        isFrequencySelected = frequencySelecte;
        myProductList.get(position).setFrequency(frequency);
    }

    public void showLargeImageDialog(MyProduct product, View view){
        showImageDialog(product.getProdImage1(),view);
    }

    public void showProductDetails(MyProduct product){
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra("MyProduct",product);
        intent.putExtra("shopDeliveryModel",shopDeliveryModel);
        startActivity(intent);
    }

    public void showFrequencyBottomShet(MyProduct item, int position){
        FrequencyFragment frequencyFragment = new FrequencyFragment();
        if(item.getFrequency()!=null)
            frequencyFragment.setProduct(item, position, "edit");
        else frequencyFragment.setProduct(item, position, "add");
        frequencyFragment.setColorTheme(colorTheme);
        frequencyFragment.show(getSupportFragmentManager(), "Product ProductFrequency Bottom Sheet");
    }

    @Override
    public void updateUi(MyProduct product, int position) {
        Log.d("return position ", position+"");
        Log.d("return product ", product.getId()+"");
        myProductList.set(position, product);
        shopProductAdapter.notifyItemChanged(position);
        shopProductAdapter.notifyDataSetChanged();
        //updateCartCount();
    }

    @Override
    public void startFrequencyCartActivity() {
        Intent intent = new Intent( this, CartActivity.class);
        intent.putExtra("cart_type", "frequency");
        intent.putExtra("position", position);
        //startActivityForResult(intent, 102);
        startActivity(intent);
        finish();
    }
}
