package com.shoppurscustomer.activities;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.shoppurscustomer.R;
import com.shoppurscustomer.adapters.MyReviewAdapter;
import com.shoppurscustomer.models.CartItem;
import com.shoppurscustomer.models.MyProduct;
import com.shoppurscustomer.models.MyReview;
import com.shoppurscustomer.utilities.Constants;
import com.shoppurscustomer.utilities.DialogAndToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDetailActivity extends NetworkBaseActivity {

    private final String TAG ="ProductDetailActivity";
    private RecyclerView recyclerViewReview;
    private MyReviewAdapter myReviewAdapter;
    private List<MyReview> myReviewList;
    private TextView textViewSubCatName,textViewProductName,
                     text_product_selling_price,text_product_mrp,textViewDesc,tv_cartCount;
    private ImageView imageView2,imageView3,imageView4;
    private Button btnAddCart, btn_plus, btn_minus;
    private LinearLayout linear_plus_minus, linear_qty;
    private String shopName, shopCode, dbName, dbUserName, dbPassword ,custId,custName;
    private CartItem cartItem;
    private RelativeLayout rlfooterviewcart;
    private TextView cartItemCount,cartItemPrice, viewCart;
    private MyProduct myProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        init();
        initViews();
    }

    private void init(){
        myProduct = new MyProduct();
        shopCode = sharedPreferences.getString(Constants.SHOP_INSIDE,"");
        shopName = sharedPreferences.getString(Constants.SHOP_INSIDE_NAME,"");
        dbName = sharedPreferences.getString(Constants.DB_NAME,"");
        dbUserName = sharedPreferences.getString(Constants.DB_USER_NAME,"");
        dbPassword = sharedPreferences.getString(Constants.DB_PASSWORD,"");
        custName = sharedPreferences.getString(Constants.FULL_NAME, "");
        custId = sharedPreferences.getString(Constants.USER_ID,"");
        myProduct = (MyProduct) getIntent().getSerializableExtra("MyProduct");
        Log.d("myProduct ", myProduct+" myproduct");
        cartItem = new CartItem();
    }

    private void initViews(){
        imageView2 = findViewById(R.id.image_view_2);
        imageView3 = findViewById(R.id.image_view_3);
        imageView4 = findViewById(R.id.image_view_4);
        textViewSubCatName = findViewById(R.id.text_sub_cat);
        textViewProductName = findViewById(R.id.text_product_name);
        text_product_selling_price = findViewById(R.id.text_product_selling_price);
        text_product_mrp = findViewById(R.id.text_product_mrp);
        text_product_mrp.setPaintFlags(text_product_mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        textViewDesc = findViewById(R.id.text_desc);

        btnAddCart = findViewById(R.id.btn_addCart);
        linear_plus_minus = findViewById(R.id.linear_plus_minus);
        btn_plus = findViewById(R.id.btn_plus);
        btn_minus = findViewById(R.id.btn_minus);
        linear_qty = findViewById(R.id.linear_qty);
        tv_cartCount = findViewById(R.id.tv_cartCount);

        textViewSubCatName.setText(myProduct.getSubCatName());
        textViewProductName.setText(myProduct.getName());
        text_product_selling_price.setText(myProduct.getSellingPrice());
        text_product_mrp.setText(myProduct.getMrp());
        textViewDesc.setText(myProduct.getDesc());
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        // requestOptions.override(Utility.dpToPx(150, context), Utility.dpToPx(150, context));
        requestOptions.centerCrop();
        requestOptions.skipMemoryCache(true);
        Glide.with(this)
                .load(myProduct.getProdImage1())
                .apply(requestOptions)
                .into(imageView2);
        Glide.with(this)
                .load(myProduct.getProdImage1())
                .apply(requestOptions)
                .into(imageView3);

        Glide.with(this)
                .load(myProduct.getProdImage1())
                .apply(requestOptions)
                .into(imageView4);
        /*imageView2.setImageResource(myProduct.getProdImage1());
        imageView3.setImageResource(myProduct.getProdImage2());
        imageView4.setImageResource(myProduct.getProdImage3());
*/
        myReviewList = new ArrayList<>();
        recyclerViewReview= findViewById(R.id.recycler_view_review);
        recyclerViewReview.setItemAnimator(new DefaultItemAnimator());
        recyclerViewReview.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManagerReview=new LinearLayoutManager(this);
        recyclerViewReview.setLayoutManager(layoutManagerReview);
        myReviewAdapter=new MyReviewAdapter(this,myReviewList,"productReview");
        recyclerViewReview.setAdapter(myReviewAdapter);
        recyclerViewReview.setNestedScrollingEnabled(false);
        setReviews();

        rlfooterviewcart = findViewById(R.id.rlfooterviewcart);
        cartItemCount = findViewById(R.id.itemCount);
        cartItemPrice = findViewById(R.id.itemPrice);
        viewCart = findViewById(R.id.viewCart);

        btnAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linear_plus_minus.setVisibility(View.VISIBLE);
                btnAddCart.setVisibility(View.GONE);
                DialogAndToast.showToast("Add to Cart ", ProductDetailActivity.this);
                cartItem = new CartItem();
                cartItem.setProdCode(myProduct.getCode());
                cartItem.setBarcode(myProduct.getBarCode());
                cartItem.setItemName(myProduct.getName());
                cartItem.setShopCode(shopCode);
                cartItem.setShopName(shopName);
                cartItem.setPrice(Float.parseFloat(myProduct.getMrp()));
                cartItem.setQuantity(Integer.parseInt(tv_cartCount.getText().toString()));
                cartItem.setSize(2);
                cartItem.setCustCode(custId);
                cartItem.setCustName(custName);
                float subtotal = cartItem.getPrice() * cartItem.getQuantity();
                cartItem.setTotalAmout(subtotal);
                cartItem.setCreatedDate("25-03-2019");
                cartItem.setUpdatedDate("25-03-2019");
                DialogAndToast.showToast("Added Sucessfully " + tv_cartCount.getText().toString(), ProductDetailActivity.this);
                //startActivity(new Intent(ProductDetailActivity.this, CartActivity.class));
                add_toCart(cartItem);
            }
        });
        btn_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = Integer.parseInt(tv_cartCount.getText().toString());
                if(count==1){
                    linear_plus_minus.setVisibility(View.GONE);
                    btnAddCart.setVisibility(View.VISIBLE);
                    cartItem.setProdCode(myProduct.getCode());
                    cartItem.setShopCode(shopCode);
                    removeCart(cartItem);
                }else {
                    tv_cartCount.setText(String.valueOf(count-1));
                    cartItem.setShopCode(shopCode);
                    cartItem.setProdCode(myProduct.getCode());
                    cartItem.setQuantity(count-1);
                    cartItem.setPrice(Float.parseFloat(myProduct.getMrp()));
                    cartItem.setTotalAmout(cartItem.getPrice() * cartItem.getQuantity());
                    updateCart(cartItem);
                }
            }
        });
        btn_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = Integer.parseInt(tv_cartCount.getText().toString());
                tv_cartCount.setText(String.valueOf(count+1));
                cartItem.setQuantity(count+1);
                cartItem.setShopCode(shopCode);
                cartItem.setProdCode(myProduct.getCode());
                cartItem.setPrice(Float.parseFloat(myProduct.getMrp()));
                cartItem.setTotalAmout(cartItem.getPrice() * cartItem.getQuantity());
                updateCart(cartItem);
            }
        });

        if(dbHelper.isProductInCart(myProduct.getCode(), shopCode)){
            btnAddCart.setVisibility(View.GONE);
            linear_plus_minus.setVisibility(View.VISIBLE);
            tv_cartCount.setText(String.valueOf(dbHelper.getProductQuantity(myProduct.getCode(), shopCode)));
        }
    }


    private void add_toCart(CartItem cartItem){
        Map<String,String> params=new HashMap<>();
        params.put("shopCode",cartItem.getShopCode());
        params.put("prodCode", cartItem.getProdCode());
        params.put("prodQty", String.valueOf(cartItem.getQuantity()));
        params.put("dbName", dbName);
        params.put("dbUserName", dbUserName);
        params.put("dbPassword", dbPassword);
        Log.d(TAG, params.toString());

        String url=getResources().getString(R.string.root_url)+"cart/add";
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"addtocart");
    }

    public void updateCart(CartItem cartItem){
        this.cartItem = cartItem;
        Map<String,String> params=new HashMap<>();
        params.put("shopCode",cartItem.getShopCode());
        params.put("prodCode", cartItem.getProdCode());
        params.put("prodQty", String.valueOf(cartItem.getQuantity()));
        params.put("dbName", dbName);
        params.put("dbUserName", dbUserName);
        params.put("dbPassword", dbPassword);
        Log.d(TAG, params.toString());

        String url=getResources().getString(R.string.root_url)+"cart/update";
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"updatCart");
    }

    public void removeCart(CartItem cartItem){
        this.cartItem = cartItem;
        Map<String,String> params=new HashMap<>();
        params.put("shopCode",cartItem.getShopCode());
        params.put("prodCode", cartItem.getProdCode());
        params.put("dbName", dbName);
        params.put("dbUserName", dbUserName);
        params.put("dbPassword", dbPassword);
        Log.d(TAG, params.toString());

        String url=getResources().getString(R.string.root_url)+"cart/remove_product";
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"removeCart");
    }




    @Override
    public void onJsonObjectResponse(JSONObject response, String apiName) {
        showProgress(false);
        try {
            // JSONObject jsonObject=response.getJSONObject("response");
            Log.d("response", response.toString());
            if(apiName.equals("addtocart")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)){
                    dbHelper.add_to_Cart(cartItem);
                    updateCartCount();
                    Log.d(TAG, "added o cart" );
                }else {
                      DialogAndToast.showToast(response.getString("message"),ProductDetailActivity.this);
                }
            }else if(apiName.equals("updatCart")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)){
                    dbHelper.updateCart(cartItem);
                    updateCartCount();
                    Log.d(TAG, "updated cart" );
                }else {
                    DialogAndToast.showToast(response.getString("message"),ProductDetailActivity.this);
                }
            }else if(apiName.equals("removeCart")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)){
                    dbHelper.deleteCart(cartItem);
                    updateCartCount();
                    Log.d(TAG, "Deleted cart" );
                }else {
                    DialogAndToast.showToast(response.getString("message"),ProductDetailActivity.this);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            DialogAndToast.showToast(getResources().getString(R.string.json_parser_error)+e.toString(),ProductDetailActivity.this);
        }
    }

    private void setReviews(){
        MyReview myReview = new MyReview();
        myReview.setUserName("Vipin Dhama");
        myReview.setDateTime("4 hours ago");
        myReview.setRating(4);
        myReview.setReview("Review about product.");

        myReviewList.add(myReview);
        myReview = new MyReview();
        myReview.setUserName("Amit kumar");
        myReview.setDateTime("8 hours ago");
        myReview.setRating(4);
        myReview.setReview("Review about product.");
        myReviewList.add(myReview);

        myReview = new MyReview();
        myReview.setUserName("Sunil Kumar");
        myReview.setDateTime("6 days ago");
        myReview.setRating(4);
        myReview.setReview("Review about product.");
        myReviewList.add(myReview);

        myReviewAdapter.notifyDataSetChanged();
    }

    public void updateCartCount(){
        if(dbHelper.getCartCount()>0){
            rlfooterviewcart.setVisibility(View.VISIBLE);
            viewCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ProductDetailActivity.this, CartActivity.class));
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

        if(dbHelper.getProductQuantity(myProduct.getCode(), shopCode)>0)
            tv_cartCount.setText(String.valueOf(dbHelper.getProductQuantity(myProduct.getCode(), shopCode)));
        else {
            tv_cartCount.setText(String.valueOf(1));
            linear_plus_minus.setVisibility(View.GONE);
            btnAddCart.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateCartCount();
    }
}
