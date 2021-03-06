package com.shoppurs.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.shoppurs.R;
import com.shoppurs.adapters.CategoryAdapter;
import com.shoppurs.fragments.BottomSearchFragment;
import com.shoppurs.models.HomeListItem;
import com.shoppurs.models.MyHeader;
import com.shoppurs.models.MyProduct;
import com.shoppurs.utilities.Constants;
import com.shoppurs.utilities.DialogAndToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductListActivity extends NetworkBaseActivity {

    private RecyclerView recyclerView;
    private CategoryAdapter myItemAdapter;
    private List<Object> itemList;
    private TextView textViewError;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private String callingClass, shopname, address,mobile, stateCity, subcatid,subcatname, dbname, dbuser, dbpassword;
    private String shopCode, custName, custCode;
   // private CartItem cartItem;
    private MyProduct myProduct;
    private RelativeLayout rlfooterviewcart;
    private TextView cartItemCount, cartItemPrice, viewCart;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);

        shopCode = sharedPreferences.getString(Constants.SHOP_INSIDE_CODE,"");
        dbname = sharedPreferences.getString(Constants.DB_NAME,"");
        dbuser = sharedPreferences.getString(Constants.DB_USER_NAME,"");
        dbpassword = sharedPreferences.getString(Constants.DB_PASSWORD,"");
        custName = sharedPreferences.getString(Constants.FULL_NAME, "");
        custCode = sharedPreferences.getString(Constants.USER_ID,"");
        callingClass = getIntent().getStringExtra("callingClass");

        if(callingClass!=null && callingClass.equals("ShopListActivity")){
            shopname  = getIntent().getStringExtra("name");
            address = getIntent().getStringExtra("address");
            mobile = getIntent().getStringExtra("mobile");
            stateCity  = getIntent().getStringExtra("stateCity");
            subcatid = getIntent().getStringExtra("subcatid");
            subcatname  = getIntent().getStringExtra("subcatname");
            //dbname = getIntent().getStringExtra("dbname");
            //dbuser  = getIntent().getStringExtra("dbuser");
            //dbpassword = getIntent().getStringExtra("dbpassword");
            shopCode = getIntent().getStringExtra("shop_code");
            editor.putString(Constants.SHOP_INSIDE_CODE,shopCode);
            editor.putString(Constants.SHOP_INSIDE_NAME, shopname);
            editor.commit();

            itemList = new ArrayList<>();
            HomeListItem myItem = new HomeListItem();
            myItem.setTitle(shopname);
            myItem.setDesc(address);
            myItem.setMobile(mobile);
            myItem.setType(0);
            itemList.add(myItem);

            MyHeader myHeader = new MyHeader();
            myHeader.setTitle(subcatname);
            myHeader.setType(1);
            itemList.add(myHeader);
        }

        swipeRefreshLayout=findViewById(R.id.swipe_refresh);
        progressBar=findViewById(R.id.progress_bar);
        textViewError = findViewById(R.id.text_error);
        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
      /*  int resId = R.anim.layout_animation_slide_from_bottom;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this, resId);
        recyclerView.setLayoutAnimation(animation);*/
        myItemAdapter=new CategoryAdapter(this,itemList,"productList");
        myItemAdapter.setShopCode(shopCode);
        recyclerView.setAdapter(myItemAdapter);

        if(itemList.size() == 0){
            showNoData(true);
        }


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                getItemList();
            }
        });

        rlfooterviewcart = findViewById(R.id.rlfooterviewcart);
        cartItemCount = findViewById(R.id.itemCount);
        cartItemPrice = findViewById(R.id.itemPrice);
        viewCart = findViewById(R.id.viewCart);
        getProducts();
    }

    private void getItemList(){
        swipeRefreshLayout.setRefreshing(false);
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
            recyclerView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            textViewError.setVisibility(View.GONE);
        }else{
            recyclerView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }


    public void add_toCart(MyProduct product){
        this.myProduct = myProduct;
        myProduct.setShopCode(shopCode);
        myProduct.setQuantity(1);
        myProduct.setSellingPrice(myProduct.getSellingPrice());
        myProduct.setCustCode(custCode);
        float subtotal = myProduct.getSellingPrice() * myProduct.getQuantity();
        myProduct.setTotalAmount(subtotal);

        Map<String,String> params=new HashMap<>();
        params.put("shopCode", shopCode);
        params.put("prodCode", myProduct.getCode());
        params.put("prodQty", String.valueOf(1));
        params.put("dbName", dbname);
        params.put("dbUserName", dbuser);
        params.put("dbPassword", dbpassword);
        Log.d(TAG, params.toString());

        String url=getResources().getString(R.string.url_customer)+"cart/add";
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"addtocart");
    }

    public void updateCart(MyProduct myProduct, int quantity){
        this.myProduct = myProduct;
        myProduct.setShopCode(shopCode);
        myProduct.setQuantity(quantity);
        myProduct.setTotalAmount(myProduct.getSellingPrice() * myProduct.getQuantity());

        Map<String,String> params=new HashMap<>();
        params.put("shopCode", shopCode);
        params.put("prodCode", myProduct.getCode());
        params.put("prodQty", String.valueOf(quantity));
        params.put("dbName", dbname);
        params.put("dbUserName", dbuser);
        params.put("dbPassword", dbpassword);
        Log.d(TAG, params.toString());

        String url=getResources().getString(R.string.url_customer)+"cart/update";
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"updatCart");
    }

    public void removeCart(MyProduct product){
        this.myProduct = product;
        myProduct.setShopCode(shopCode);
        Map<String,String> params=new HashMap<>();
        params.put("shopCode",shopCode);
        params.put("prodCode", myProduct.getCode());
        params.put("dbName", dbname);
        params.put("dbUserName", dbuser);
        params.put("dbPassword", dbpassword);
        Log.d(TAG, params.toString());

        String url=getResources().getString(R.string.url_customer)+"cart/remove_product";
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"removeCart");
    }

    public void getProducts(){
        Map<String,String> params=new HashMap<>();
        params.put("subCatId", subcatid);
        params.put("shopCode", shopCode);
        params.put("dbName",shopCode);
        params.put("dbUserName",dbuser);
        params.put("dbPassword",dbpassword);
        Log.d(TAG, params.toString());
        String url=getResources().getString(R.string.url_customer)+"/api/customers/products/ret_productslist";
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"productfromshop");
    }

    public void shopFavorite(String status){
        Map<String,String> params=new HashMap<>();
        params.put("favStatus", status);
        params.put("code",shopCode);
        params.put("dbName", dbname);
        params.put("dbUserName",dbuser);
        params.put("dbPassword",dbpassword);
        Log.d(TAG, params.toString());
        String url=getResources().getString(R.string.url_customer)+"/api/customers/shop/favourite";
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"shopFavorite");
    }
    public void shopRating(float rating){
        Map<String,String> params=new HashMap<>();
        params.put("ratings", String.valueOf(rating));
        params.put("custCode", custCode);
        params.put("shopCode",shopCode);
        params.put("dbName", dbname);
        params.put("dbUserName",dbuser);
        params.put("dbPassword",dbpassword);
        Log.d(TAG, params.toString());
        String url=getResources().getString(R.string.url_customer)+"/api/customers/shop/ratings";
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"shopRating");
    }


    @Override
    public void onJsonObjectResponse(JSONObject response, String apiName) {
        showProgress(false);
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
                        myProduct.setMrp(Float.parseFloat(productJArray.getJSONObject(i).getString("prodMrp")));
                        myProduct.setSellingPrice(Float.parseFloat(productJArray.getJSONObject(i).getString("prodSp")));
                        myProduct.setCode(productJArray.getJSONObject(i).getString("prodCode"));
                        myProduct.setBarCode(productJArray.getJSONObject(i).getString("prodBarCode"));
                        myProduct.setDesc(productJArray.getJSONObject(i).getString("prodDesc"));
                        //myProduct.setLocalImage(R.drawable.thumb_16);
                        myProduct.setProdImage1(productJArray.getJSONObject(i).getString("prodImage1"));
                        myProduct.setProdImage2(productJArray.getJSONObject(i).getString("prodImage2"));
                        myProduct.setProdImage3(productJArray.getJSONObject(i).getString("prodImage3"));
                        myProduct.setProdHsnCode(productJArray.getJSONObject(i).getString("prodHsnCode"));
                        myProduct.setProdMfgDate(productJArray.getJSONObject(i).getString("prodMfgDate"));
                        myProduct.setProdExpiryDate(productJArray.getJSONObject(i).getString("prodExpiryDate"));
                        myProduct.setProdMfgBy(productJArray.getJSONObject(i).getString("prodMfgBy"));
                        myProduct.setProdExpiryDate(productJArray.getJSONObject(i).getString("prodExpiryDate"));
                        myProduct.setOfferId(productJArray.getJSONObject(i).getString("offerId"));
                        myProduct.setProdCgst(Float.parseFloat(productJArray.getJSONObject(i).getString("prodCgst")));
                        myProduct.setProdIgst(Float.parseFloat(productJArray.getJSONObject(i).getString("prodIgst")));
                        myProduct.setProdSgst(Float.parseFloat(productJArray.getJSONObject(i).getString("prodSgst")));
                        myProduct.setProdWarranty(productJArray.getJSONObject(i).getString("prodWarranty"));
                        myProduct.setSubCatName(subcatname);
                        itemList.add(myProduct);
                    }
                    if(itemList.size()>0){
                        myItemAdapter.notifyDataSetChanged();
                    }else {
                        showNoData(true);
                    }

                }else {
                    DialogAndToast.showDialog(response.getString("message"),ProductListActivity.this);
                }
            }if(apiName.equals("addtocart")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)){
                    dbHelper.addProductToCart(myProduct, "normal");
                    updateCartCount();
                    Log.d(TAG, "added o cart" );
                }else {
                    DialogAndToast.showToast(response.getString("message"),ProductListActivity.this);
                }
            }else if(apiName.equals("updatCart")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)){
                    dbHelper.updateCartData(myProduct, "normal");
                    updateCartCount();
                    Log.d(TAG, "updated cart" );
                }else {
                    DialogAndToast.showToast(response.getString("message"),ProductListActivity.this);
                }
            }else if(apiName.equals("removeCart")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)){
                    dbHelper.removeProductFromCart(myProduct.getId(),myProduct.getShopCode());
                    updateCartCount();
                    Log.d(TAG, "Deleted cart" );
                }else {
                    DialogAndToast.showToast(response.getString("message"),ProductListActivity.this);
                }
            }else if(apiName.equals("shopFavorite")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)){
                    Log.d(TAG, "Added in Favorite Shop" );
                    if(dbHelper.isShopFavorited(shopCode)) {
                        dbHelper.remove_from_Favorite(shopCode);
                        menu.getItem(0).setIcon(ContextCompat.getDrawable(ProductListActivity.this, R.drawable.favrorite_select));
                    }else {
                        dbHelper.add_to_Favorite(shopCode);
                        menu.getItem(0).setIcon(ContextCompat.getDrawable(ProductListActivity.this, R.drawable.favroite_selected));
                    }
                }else {
                    DialogAndToast.showToast(response.getString("message"),ProductListActivity.this);
                }
            }else if(apiName.equals("shopRating")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)){
                    Log.d(TAG, "Rated Shop" );
                }else {
                    DialogAndToast.showToast(response.getString("message"),ProductListActivity.this);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            DialogAndToast.showToast(getResources().getString(R.string.json_parser_error)+e.toString(),ProductListActivity.this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.my_product_menu, menu);
        if(dbHelper.isShopFavorited(shopCode)){
            menu.getItem(0).setIcon(ContextCompat.getDrawable(ProductListActivity.this, R.drawable.favroite_selected));
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
           onBackPressed();
           return true;
        }
        else if(item.getItemId()==R.id.action_favrouite){
            if(dbHelper.isShopFavorited(shopCode))
            shopFavorite("N");
            else shopFavorite("Y");
        }else if(item.getItemId() == R.id.action_rating){
            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.rating_dialog_layout);
            dialog.show();
            final RatingBar ratingBar = dialog.findViewById(R.id.ratingBar);
            Button submit = dialog.findViewById(R.id.btn_submit);
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    float rating = ratingBar.getRating();
                    shopRating(rating);
                }
            });
        }else if(item.getItemId() == R.id.action_search){
            //DialogAndToast.showToast("Search Clicked..", this);
            BottomSearchFragment bottomSearchFragment = new BottomSearchFragment();
            Bundle bundle = new Bundle();
            bundle.putString("shopCode", shopCode);
            bundle.putString("shopName", shopname);
            bundle.putString("shopAddress", address);
            bundle.putString("shopMobile", mobile);
            bottomSearchFragment.setArguments(bundle);
            bottomSearchFragment.setCallingActivityName("ProductListActivity", sharedPreferences, isDarkTheme);
            bottomSearchFragment.setSubCatName(subcatname);
            bottomSearchFragment.setSubcatId(subcatid);
            bottomSearchFragment.show(getSupportFragmentManager(), bottomSearchFragment.getTag());
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        myItemAdapter.notifyDataSetChanged();
        updateCartCount();
    }

    public void updateCartCount(){
        if(dbHelper.getCartCount()>0){
            rlfooterviewcart.setVisibility(View.VISIBLE);
            viewCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ProductListActivity.this, CartActivity.class));
                }
            });
            List<MyProduct> cartItemList = new ArrayList<>();
            cartItemList = dbHelper.getCartProducts("normal");
            float total_amount =0;
            for (MyProduct cartItem: cartItemList) {
                total_amount = total_amount + cartItem.getTotalAmount();
            }
            cartItemPrice.setText("Amount "+String.valueOf(total_amount));
            cartItemCount.setText("Item "+String.valueOf(dbHelper.getCartCount()));
        }else rlfooterviewcart.setVisibility(View.GONE);
    }
}
