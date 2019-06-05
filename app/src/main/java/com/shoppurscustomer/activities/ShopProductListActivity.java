package com.shoppurscustomer.activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.shoppurscustomer.R;
import com.shoppurscustomer.adapters.ShopProductListAdapter;
import com.shoppurscustomer.adapters.TopCategoriesAdapter;
import com.shoppurscustomer.database.DbHelper;
import com.shoppurscustomer.models.CartItem;
import com.shoppurscustomer.models.Category;
import com.shoppurscustomer.models.MyProduct;
import com.shoppurscustomer.models.SubCategory;
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
 * Created by suraj kumar singh on 20-04-2019.
 */

public class ShopProductListActivity extends NetworkBaseActivity{

    private Toolbar toolbar;
    private Menu menu;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerViewProduct, recyclerViewCategory;
    private TextView tv_shopname, tvNoData, cartItemCount, cartItemPrice, viewCart;
    private ProgressBar progressBar;
    private RelativeLayout rlfooterviewcart;
    private Spinner spinnerSubcategory;
    private ShopProductListAdapter shopProductAdapter;
    private TopCategoriesAdapter categoriesAdapter;
    private List<Category> categoryList;
    private List<SubCategory> subCategoryList;
    private List<String> mFilteredSubCategoriesName;
    private List<String> mFilteredSubCategoriesId;
    private List<MyProduct> myProductList;
    private CartItem cartItem;
    private DbHelper dbHelper;
    private String shopCode, shopName, catId, subCatId, subCatName, custCode, dbname, dbuser, dbpassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_product_list);
        dbHelper = new DbHelper(this);
        shopCode = getIntent().getStringExtra("shopCode");
        shopName = getIntent().getStringExtra("shopName");
        catId = getIntent().getStringExtra("catId");
        subCatId = getIntent().getStringExtra("subcatid");
        subCatName = getIntent().getStringExtra("subCatName");
        custCode = sharedPreferences.getString(Constants.USER_ID,"");
        dbname = sharedPreferences.getString(Constants.DB_NAME,"");
        dbuser = sharedPreferences.getString(Constants.DB_USER_NAME,"");
        dbpassword = sharedPreferences.getString(Constants.DB_PASSWORD,"");
        initViews();
    }

    private void initViews(){
        toolbar = findViewById(R.id.toolbar);
        swipeRefreshLayout =findViewById(R.id.swipe_refresh);
        tv_shopname = findViewById(R.id.tv_shopname);
        spinnerSubcategory = findViewById(R.id.spinner);
        recyclerViewProduct = findViewById(R.id.recycler_viewProduct);
        recyclerViewCategory = findViewById(R.id.recycler_viewCategory);
        tvNoData = findViewById(R.id.tvNoData);
        progressBar=findViewById(R.id.progress_bar);
        rlfooterviewcart = findViewById(R.id.rlfooterviewcart);
        cartItemCount= findViewById(R.id.itemCount);
        cartItemPrice = findViewById(R.id.itemPrice);
        viewCart = findViewById(R.id.viewCart);

        tv_shopname.setText(shopName);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        getCategories();
        getProducts(catId, "");
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getProducts(catId, "");
            }
        });
    }

    private void getCategories(){
        categoryList = new ArrayList<>();
        subCategoryList = new ArrayList<>();
        recyclerViewCategory.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false);
        recyclerViewCategory.setLayoutManager(layoutManager);
        recyclerViewCategory.setItemAnimator(new DefaultItemAnimator());
        categoriesAdapter = new TopCategoriesAdapter(this, categoryList);
        recyclerViewCategory.setAdapter(categoriesAdapter);

        String url=getResources().getString(R.string.url)+"/cat_subcat";
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url, new JSONObject(),"get_categories");
    }


    public void getProducts(String catId, String action){
        if(action!=null && action.equals("onCategorySelected")){
            mFilteredSubCategoriesName = new ArrayList<>();
            mFilteredSubCategoriesId = new ArrayList<>();
            for(SubCategory subCategory: subCategoryList) {
                if(subCategory.getId().equals(catId))
                    mFilteredSubCategoriesName.add(subCategory.getName());
                    mFilteredSubCategoriesId.add(subCategory.getId());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(ShopProductListActivity.this, android.R.layout.simple_spinner_item, mFilteredSubCategoriesName);
            spinnerSubcategory.setAdapter(adapter);
        }
        myProductList = new ArrayList<>();
        recyclerViewProduct.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewProduct.setLayoutManager(layoutManager);
        recyclerViewProduct.setItemAnimator(new DefaultItemAnimator());
        shopProductAdapter = new ShopProductListAdapter(this, myProductList);
        shopProductAdapter.setShopCode(shopCode);
        recyclerViewProduct.setAdapter(shopProductAdapter);

        myProductList.clear();
        if(swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);

        Map<String,String> params=new HashMap<>();
        params.put("subCatId", catId); // as per user selected category from top horizontal categories list
        params.put("shopCode", shopCode);
        params.put("dbName",dbname);
        params.put("dbUserName",dbuser);
        params.put("dbPassword",dbpassword);
        Log.d(TAG, params.toString());
        String url=getResources().getString(R.string.url)+"/products/ret_productslist";
        showProgressBar(true);
        jsonObjectApiRequest(Request.Method.POST, url,new JSONObject(params),"productfromshop");
    }

    public void add_toCart(MyProduct myProduct){
        cartItem = new CartItem();
        cartItem.setShopCode(shopCode);
        cartItem.setShopName(shopName);
        cartItem.setQuantity(1);
        cartItem.setProdCode(myProduct.getCode());
        cartItem.setBarcode(myProduct.getBarCode());
        cartItem.setItemName(myProduct.getName());
        cartItem.setPrice(Float.parseFloat(myProduct.getSellingPrice()));
        cartItem.setCustCode(custCode);
        float subtotal = cartItem.getPrice() * cartItem.getQuantity();
        cartItem.setTotalAmout(subtotal);

        Map<String,String> params=new HashMap<>();
        params.put("shopCode", shopCode);
        params.put("prodCode", myProduct.getBarCode());
        params.put("prodQty", String.valueOf(1));
        params.put("dbName", dbname);
        params.put("dbUserName", dbuser);
        params.put("dbPassword", dbpassword);
        Log.d(TAG, params.toString());

        String url=getResources().getString(R.string.root_url)+"cart/add";
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"addtocart");
    }

    public void updateCart(MyProduct myProduct, int quantity){
        cartItem = new CartItem();
        cartItem.setShopCode(shopCode);
        cartItem.setProdCode(myProduct.getCode());
        cartItem.setQuantity(quantity);
        cartItem.setPrice(Float.parseFloat(myProduct.getSellingPrice()));
        cartItem.setTotalAmout(cartItem.getPrice() * cartItem.getQuantity());

        Map<String,String> params=new HashMap<>();
        params.put("shopCode", shopCode);
        params.put("prodCode", myProduct.getCode());
        params.put("prodQty", String.valueOf(quantity));
        params.put("dbName", dbname);
        params.put("dbUserName", dbuser);
        params.put("dbPassword", dbpassword);
        Log.d(TAG, params.toString());

        String url=getResources().getString(R.string.root_url)+"cart/update";
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"updatCart");
    }

    public void removeCart(MyProduct myProduct){
        cartItem = new CartItem();
        cartItem.setShopCode(shopCode);
        cartItem.setProdCode(myProduct.getCode());

        Map<String,String> params=new HashMap<>();
        params.put("shopCode",shopCode);
        params.put("prodCode", myProduct.getCode());
        params.put("dbName", dbname);
        params.put("dbUserName", dbuser);
        params.put("dbPassword", dbpassword);
        Log.d(TAG, params.toString());

        String url=getResources().getString(R.string.root_url)+"cart/remove_product";
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"removeCart");
    }



    public void shopFavorite(String status){
        Map<String,String> params=new HashMap<>();
        params.put("favStatus", status);
        params.put("code",shopCode);
        params.put("dbName", dbname);
        params.put("dbUserName",dbuser);
        params.put("dbPassword",dbpassword);
        Log.d(TAG, params.toString());
        String url=getResources().getString(R.string.url)+"/shop/favourite";
        showProgressBar(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"shopFavorite");
    }

    @Override
    public void onJsonObjectResponse(JSONObject response, String apiName) {
        showProgressBar(false);
        try {
            // JSONObject jsonObject=response.getJSONObject("response");
            Log.d("response", response.toString());
            if(apiName.equals("productfromshop")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)){
                    JSONArray productJArray = response.getJSONArray("result");
                    for(int i=0;i<productJArray.length();i++){
                        MyProduct myProduct = new MyProduct();
                        myProduct.setId(productJArray.getJSONObject(i).getString("prodId"));
                        myProduct.setName(productJArray.getJSONObject(i).getString("prodName"));
                        myProduct.setQuantity(productJArray.getJSONObject(i).getInt("prodQoh"));
                        myProduct.setMrp(productJArray.getJSONObject(i).getString("prodMrp"));
                        myProduct.setSellingPrice(productJArray.getJSONObject(i).getString("prodSp"));
                        myProduct.setCode(productJArray.getJSONObject(i).getString("prodCode"));
                        myProduct.setBarCode(productJArray.getJSONObject(i).getString("prodBarCode"));
                        myProduct.setDesc(productJArray.getJSONObject(i).getString("prodDesc"));
                        myProduct.setLocalImage(R.drawable.thumb_16);
                        myProduct.setProdImage1(productJArray.getJSONObject(i).getString("prodImage1"));
                        myProduct.setProdImage2(productJArray.getJSONObject(i).getString("prodImage2"));
                        myProduct.setProdImage3(productJArray.getJSONObject(i).getString("prodImage3"));
                        myProduct.setProdHsnCode(productJArray.getJSONObject(i).getString("prodHsnCode"));
                        myProduct.setProdMfgDate(productJArray.getJSONObject(i).getString("prodMfgDate"));
                        myProduct.setProdExpiryDate(productJArray.getJSONObject(i).getString("prodExpiryDate"));
                        myProduct.setProdMfgBy(productJArray.getJSONObject(i).getString("prodMfgBy"));
                        myProduct.setProdExpiryDate(productJArray.getJSONObject(i).getString("prodExpiryDate"));
                        myProduct.setOfferId(productJArray.getJSONObject(i).getString("offerId"));
                        myProduct.setProdCgst(productJArray.getJSONObject(i).getString("prodCgst"));
                        myProduct.setProdIgst(productJArray.getJSONObject(i).getString("prodIgst"));
                        myProduct.setProdSgst(productJArray.getJSONObject(i).getString("prodSgst"));
                        myProduct.setProdWarranty(productJArray.getJSONObject(i).getString("prodWarranty"));
                        //myProduct.setSubCatName(subcatname);
                        myProductList.add(myProduct);
                    }
                    if(myProductList.size()>0){
                        shopProductAdapter.notifyDataSetChanged();
                    }else {
                        showNoData(true);
                    }

                }else {
                    DialogAndToast.showDialog(response.getString("message"),ShopProductListActivity.this);
                }
            }else if(apiName.equals("get_categories")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)){
                    JSONObject jsonObject = response.getJSONObject("result");
                    JSONArray catJArray = jsonObject.getJSONArray("categories");
                    for(int i=0;i<catJArray.length();i++) {
                        JSONArray subcatJArray = catJArray.getJSONObject(i).getJSONArray("subCatList");
                        if(subcatJArray.length()>0) {
                            Category category = new Category();
                            String mcatid = catJArray.getJSONObject(i).getString("catId");
                            category.setId(catJArray.getJSONObject(i).getString("catId"));
                            category.setName(catJArray.getJSONObject(i).getString("catName"));
                            category.setImage(getResources().getString(R.string.image_root) + "" + catJArray.getJSONObject(i).getString("catImage"));
                            category.setSelected(false);
                            categoryList.add(category);

                            for (int j = 0; j < subcatJArray.length(); j++) {
                                if (subcatJArray.getJSONObject(j).getString("catId").equals(mcatid)) {
                                    SubCategory subCategory = new SubCategory();
                                    subCategory.setSubcatid(subcatJArray.getJSONObject(j).getString("subCatId"));
                                    subCategory.setId(subcatJArray.getJSONObject(j).getString("catId"));
                                    subCategory.setName(subcatJArray.getJSONObject(j).getString("subCatName"));
                                    subCategoryList.add(subCategory);
                                }
                            }
                        }
                    }
                    if(categoryList.size()>0) {
                        categoriesAdapter.notifyDataSetChanged();
                    }
                }else {
                    DialogAndToast.showToast(response.getString("message"),ShopProductListActivity.this);
                }
            } else if(apiName.equals("addtocart")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)){
                    dbHelper.add_to_Cart(cartItem);
                    updateCartCount();
                    Log.d(TAG, "added o cart" );
                }else {
                    DialogAndToast.showToast(response.getString("message"),ShopProductListActivity.this);
                }
            }else if(apiName.equals("updatCart")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)){
                   dbHelper.updateCart(cartItem);
                    updateCartCount();
                    Log.d(TAG, "updated cart" );
                }else {
                    DialogAndToast.showToast(response.getString("message"),ShopProductListActivity.this);
                }
            }else if(apiName.equals("removeCart")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)){
                    dbHelper.deleteCart(cartItem);
                    updateCartCount();
                    Log.d(TAG, "Deleted cart" );
                }else {
                    DialogAndToast.showToast(response.getString("message"),ShopProductListActivity.this);
                }
            }else if(apiName.equals("shopFavorite")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)){
                    Log.d(TAG, "Added in Favorite Shop" );
                    if(dbHelper.isShopFavorited(shopCode)) {
                        dbHelper.remove_from_Favorite(shopCode);
                        menu.getItem(0).setIcon(ContextCompat.getDrawable(ShopProductListActivity.this, R.drawable.favrorite_select));
                    }else {
                        dbHelper.add_to_Favorite(shopCode);
                        menu.getItem(0).setIcon(ContextCompat.getDrawable(ShopProductListActivity.this, R.drawable.favroite_selected));
                    }
                }else {
                    DialogAndToast.showToast(response.getString("message"),ShopProductListActivity.this);
                }
            }else if(apiName.equals("shopRating")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)){
                    Log.d(TAG, "Rated Shop" );
                }else {
                    DialogAndToast.showToast(response.getString("message"),ShopProductListActivity.this);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            DialogAndToast.showToast(getResources().getString(R.string.json_parser_error)+e.toString(),ShopProductListActivity.this);
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
        shopProductAdapter.notifyDataSetChanged();
        updateCartCount();
    }

    public void updateCartCount(){
        if(dbHelper.getCartCount()>0){
            rlfooterviewcart.setVisibility(View.VISIBLE);
            viewCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ShopProductListActivity.this, CartActivity.class));
                }
            });
            List<CartItem> cartItemList = new ArrayList<>();
            cartItemList = dbHelper.getAllCartItems();
            float total_amount =0;
            for (CartItem cartItem: cartItemList) {
                total_amount = total_amount + cartItem.getTotalAmout();
            }
            cartItemPrice.setText("Amount "+String.valueOf(total_amount));
            cartItemCount.setText("Item "+String.valueOf(dbHelper.getCartCount()));
        }else rlfooterviewcart.setVisibility(View.GONE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.my_product_menu, menu);
        if(dbHelper.isShopFavorited(shopCode))
            menu.getItem(0).setIcon(ContextCompat.getDrawable(this,R.drawable.favroite_selected));
        else menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.favrorite_select));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()== android.R.id.home){
            onBackPressed();
            return true;
        }else if(item.getItemId() == R.id.action_favrouite){
            if(dbHelper.isShopFavorited(shopCode))
                shopFavorite("N");
            else shopFavorite("Y");
        }
        return super.onOptionsItemSelected(item);
    }
}
