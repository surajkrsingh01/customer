package com.shoppurs.activities;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
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
import com.shoppurs.R;
import com.shoppurs.adapters.MyReviewAdapter;
import com.shoppurs.fragments.DescBottomFragment;
import com.shoppurs.models.MyProduct;
import com.shoppurs.models.MyReview;
import com.shoppurs.models.ProductDiscountOffer;
import com.shoppurs.models.ProductPriceDetails;
import com.shoppurs.models.ProductPriceOffer;
import com.shoppurs.utilities.Constants;
import com.shoppurs.utilities.DialogAndToast;
import com.shoppurs.utilities.Utility;

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
    private TextView textViewSubCatName,textViewProductName, text_product_name_top,
            tvStarRatings,tvNumRatings, text_product_selling_price,text_product_mrp,textViewDesc,tv_cartCount, tvReadMore, tvDiscount ;
    private ImageView imageView2,imageView3,imageView4, image_plus, image_minus;
    private Button btnAddCart;
    private LinearLayout linear_plus_minus, linear_qty;
    private String shopName, shopCode, shopdbName, custdbName, dbUserName, dbPassword ,custId,custName;
    private RelativeLayout rlfooterviewcart;
    private TextView cartItemCount,cartItemPrice, viewCart, text_left_label, text_right_label;
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

        shopCode = sharedPreferences.getString(Constants.SHOP_INSIDE_CODE,"");
        shopName = sharedPreferences.getString(Constants.SHOP_INSIDE_NAME,"");
        shopdbName = sharedPreferences.getString(Constants.SHOP_DBNAME,"");

        custdbName = sharedPreferences.getString(Constants.DB_NAME,"");
        dbUserName = sharedPreferences.getString(Constants.DB_USER_NAME,"");
        dbPassword = sharedPreferences.getString(Constants.DB_PASSWORD,"");
        custName = sharedPreferences.getString(Constants.FULL_NAME, "");
        custId = sharedPreferences.getString(Constants.USER_ID,"");
        myProduct = (MyProduct) getIntent().getSerializableExtra("MyProduct");
        Log.d("myProduct ", myProduct.getName()+" name");
        Log.d("myProduct ", myProduct.getShopCode()+" shopCode");
        Log.d("myProduct ", myProduct.getId()+"  id");
    }

    private void initViews(){
        text_left_label = findViewById(R.id.text_left_label);
        text_right_label = findViewById(R.id.text_right_label);
        text_left_label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProductDetailActivity.this, ShopProductListActivity.class));
                finish();
            }
        });

        imageView2 = findViewById(R.id.image_view_2);
        imageView3 = findViewById(R.id.image_view_3);
        imageView4 = findViewById(R.id.image_view_4);
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               showImageDialog(myProduct.getProdImage1(), imageView2);
            }
        });
        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageDialog(myProduct.getProdImage2(), imageView3);
            }
        });
        imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageDialog(myProduct.getProdImage3(), imageView4);
            }
        });

        textViewSubCatName = findViewById(R.id.text_sub_cat);
        textViewProductName = findViewById(R.id.text_product_name);
        text_product_name_top = findViewById(R.id.text_product_name_top);
        text_product_selling_price = findViewById(R.id.text_product_selling_price);
        text_product_mrp = findViewById(R.id.text_product_mrp);
        text_product_mrp.setPaintFlags(text_product_mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        textViewDesc = findViewById(R.id.text_desc);
        tvReadMore = findViewById(R.id.text_more);
        tvDiscount = findViewById(R.id.text_percentage_off);
        tvStarRatings = findViewById(R.id.text_product_ratings);
        tvNumRatings =  findViewById(R.id.text_numberof_ratings);

        btnAddCart = findViewById(R.id.btn_addCart);
        linear_plus_minus = findViewById(R.id.linear_plus_minus);
        image_plus = findViewById(R.id.image_plus);
        image_minus = findViewById(R.id.image_minus);
        linear_qty = findViewById(R.id.linear_qty);
        tv_cartCount = findViewById(R.id.tv_cartCount);

        textViewSubCatName.setText(myProduct.getSubCatName());
        textViewProductName.setText(myProduct.getName());
        text_product_name_top.setText(myProduct.getName());
        text_product_selling_price.setText(Utility.numberFormat(myProduct.getSellingPrice()));
        text_product_mrp.setText(Utility.numberFormat(myProduct.getMrp()));
        //textViewCode.setText(myProduct.getBarCode());

        if(!TextUtils.isEmpty(myProduct.getDesc()) && myProduct.getDesc().length() > 200){
            textViewDesc.setText(myProduct.getDesc().substring(0,200)+"...");
            tvReadMore.setVisibility(View.VISIBLE);
        }else{
            textViewDesc.setText(myProduct.getDesc());
        }

        tvReadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DescBottomFragment descBottomFragment = new DescBottomFragment();
                descBottomFragment.setDesc(myProduct.getDesc());
                descBottomFragment.show(getSupportFragmentManager(), "Description Bottom Sheet");
            }
        });

        /*textReorderLevel.setText(""+myProductItem.getProdReorderLevel());
        textViewQoh.setText(""+myProductItem.getProdQoh());*/

        /*if(myProduct.getIsBarCodeAvailable()!=null && myProduct.getIsBarCodeAvailable().equals("N")){
            buttonAddMultipleBarcode.setVisibility(View.GONE);
        }*/

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        // requestOptions.override(Utility.dpToPx(150, context), Utility.dpToPx(150, context));
        requestOptions.centerCrop();
        requestOptions.skipMemoryCache(false);

        Glide.with(this)
                .load(myProduct.getProdImage1())
                .apply(requestOptions)
                .error(R.drawable.ic_photo_black_192dp)
                .into(imageView2);

        Glide.with(this)
                .load(myProduct.getProdImage2())
                .apply(requestOptions)
                .error(R.drawable.ic_photo_black_192dp)
                .into(imageView3);

        Glide.with(this)
                .load(myProduct.getProdImage3())
                .apply(requestOptions)
                .error(R.drawable.ic_photo_black_192dp)
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

        if(dbHelper.checkProdExistInCart(myProduct.getId(), shopCode)){
            btnAddCart.setVisibility(View.GONE);
            linear_plus_minus.setVisibility(View.VISIBLE);
            Log.d("Id "+myProduct.getId(), "shopCode " +shopCode);
            Log.d("Count ", String.valueOf(dbHelper.getProductQuantity(myProduct.getId(), shopCode)));
            tv_cartCount.setText(String.valueOf(dbHelper.getProductQuantity(myProduct.getId(), shopCode)));
            myProduct.setFreeProductPosition(dbHelper.getFreeProductPosition(myProduct.getId(), shopCode));
            myProduct.setOfferItemCounter(dbHelper.getOfferCounter(myProduct.getId(), shopCode));
            myProduct.setQuantity(Integer.parseInt(tv_cartCount.getText().toString()));
            myProduct.setSellingPrice(dbHelper.getProductSellingPrice(myProduct.getId(), shopCode));

        }else {
            tv_cartCount.setText(String.valueOf(0));
            myProduct.setQuantity(0);
            linear_plus_minus.setVisibility(View.GONE);
            btnAddCart.setVisibility(View.VISIBLE);
        }

        btnAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  linear_plus_minus.setVisibility(View.VISIBLE);
               // btnAddCart.setVisibility(View.GONE);
                int count = Integer.parseInt(tv_cartCount.getText().toString());
                updateCart(2);
            }
        });
        image_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = Integer.parseInt(tv_cartCount.getText().toString());
                updateCart(1);
            }
        });
        image_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = Integer.parseInt(tv_cartCount.getText().toString());
                updateCart(2);
            }
        });

        float diff = myProduct.getMrp() - myProduct.getSellingPrice();
        if(diff > 0f){
            float percentage = diff * 100 / myProduct.getMrp();
            tvDiscount.setText(String.format("%.02f",percentage)+"% off");
        }else{
            tvDiscount.setVisibility(View.GONE);
            text_product_mrp.setVisibility(View.GONE);
        }

        getProductRatings();
    }

    private void getProductRatings(){
        Map<String,String> params=new HashMap<>();
        params.put("code",""+myProduct.getCode());
        params.put("dbName",sharedPreferences.getString(Constants.SHOP_DBNAME,""));
        params.put("dbUserName",sharedPreferences.getString(Constants.SHOP_DB_USER_NAME,""));
        params.put("dbPassword",sharedPreferences.getString(Constants.SHOP_DB_PASSWORD,""));
        String url=getResources().getString(R.string.root_url)+"products/product_ratings";
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"productRatingsData");
    }


    private void add_toCart(MyProduct cartItem){
        Map<String,String> params=new HashMap<>();
        params.put("shopCode",cartItem.getShopCode());
        params.put("prodCode", cartItem.getCode());
        params.put("prodQty", String.valueOf(cartItem.getQuantity()));
        params.put("custdbName", custdbName);
        params.put("dbUserName", dbUserName);
        params.put("dbPassword", dbPassword);
        Log.d(TAG, params.toString());

        String url=getResources().getString(R.string.root_url)+"cart/add";
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"addtocart");
    }

    int type, productDetailsType, counter;
    MyProduct freeProdut;
    boolean isProductCombo;
    public void updateCart(int type){
       this.type = type;
        if(!TextUtils.isEmpty(myProduct.getOfferType()) && myProduct.getOfferType().equals("ComboOffer"))
            isProductCombo = true;
        else isProductCombo =false;
        if(type==2 && !isProductCombo){
            productDetailsType = 1;
            getProductDetails(myProduct.getId());
        }else onProductClicked(type);
    }

    private void onProductClicked(int type){
        myProduct.setShopCode(shopCode);

        if(type == 1){
            if(myProduct.getQuantity() > 0){
                if(myProduct.getQuantity() == 1){
                    counter--;
                    //dbHelper.removeProductFromCart(myProduct.getProdBarCode());
                    dbHelper.removeProductFromCart(myProduct.getId(), myProduct.getShopCode());
                    dbHelper.removePriceProductFromCart(myProduct.getId(), myProduct.getShopCode());
                    if(myProduct.getProductPriceOffer()!=null){
                        ProductPriceOffer productPriceOffer = myProduct.getProductPriceOffer();
                        for(ProductPriceDetails productPriceDetails : productPriceOffer.getProductPriceDetails()){
                            dbHelper.removePriceProductDetailsFromCart(String.valueOf(productPriceDetails.getId()), myProduct.getShopCode());
                        }
                    }
                    if(myProduct.getProductDiscountOffer()!=null){
                        ProductDiscountOffer productDiscountOffer = (ProductDiscountOffer)myProduct.getProductOffer();

                        if(productDiscountOffer.getProdBuyId() != productDiscountOffer.getProdFreeId())
                            dbHelper.removeFreeProductFromCart(productDiscountOffer.getProdFreeId(), myProduct.getShopCode());
                    }
                    myProduct.setQuantity(0);
                    myProduct.setTotalAmount(0);
                    updateAddButtons();
                    updateCartCount();
                }else{
                    int qty = myProduct.getQuantity() - 1;
                    float netSellingPrice = getOfferAmount(myProduct,type);
                    //myProduct.setQuantity(qty);
                    qty = myProduct.getQuantity();
                    Log.i(TAG,"netSellingPrice "+netSellingPrice);
                    float amount = myProduct.getTotalAmount() - netSellingPrice;
                    Log.i(TAG,"tot amount "+amount);
                    myProduct.setTotalAmount(amount);
                    if(myProduct.getProductPriceOffer()!=null){
                        myProduct.setSellingPrice(amount/qty);
                    }
                    myProduct.setQuantity(myProduct.getQuantity());
                    dbHelper.updateCartData(myProduct);
                    updateCartCount();
                    updateAddButtons();
                }
            }

        }else if(type == 2){
            /*if(myProduct.getIsBarcodeAvailable().equals("Y")){
                if(myProduct.getQuantity() == myProduct.getBarcodeList().size()){
                    updateAddButtons();
                    DialogAndToast.showDialog("There are no more stocks",this);
                }else{
                    int qty = myProduct.getQuantity() + 1;
                    if(qty == 1){
                        counter++;
                        myProduct.setFreeProductPosition(counter);
                        dbHelper.addProductToCart(myProduct);
                    }
                    myProduct.setQuantity(qty);
                    Log.i(TAG,"qty "+qty);
                    float netSellingPrice = getOfferAmount(myProduct,type);
                    qty = myProduct.getQuantity();
                    Log.i(TAG,"netSellingPrice "+netSellingPrice);
                    float amount = myProduct.getTotalAmount() + netSellingPrice;
                    Log.i(TAG,"tot amount "+amount);
                    myProduct.setTotalAmount(amount);
                    dbHelper.updateCartData(myProduct);
                    updateCartCount();
                }

            }else{*/
                if(myProduct.getQuantity() >= myProduct.getQoh() && !isProductCombo){
                    updateAddButtons();
                    DialogAndToast.showDialog("There are no more stocks",this);
                }else{
                    int qty = myProduct.getQuantity() + 1;
                    myProduct.setQuantity(qty);
                    if(qty == 1){
                        counter++;
                        myProduct.setFreeProductPosition(counter);
                        dbHelper.addProductToCart(myProduct);
                    }
                    float netSellingPrice = getOfferAmount(myProduct,type);
                    Log.i(TAG,"netSellingPrice "+netSellingPrice);
                    float amount = myProduct.getTotalAmount() + netSellingPrice;
                    Log.i(TAG,"tot amount "+amount);
                    myProduct.setTotalAmount(amount);
                    if(myProduct.getProductPriceOffer()!=null){
                        myProduct.setSellingPrice(amount/qty);
                    }
                    qty = myProduct.getQuantity();
                    myProduct.setQuantity(myProduct.getQuantity());
                    Log.i(TAG,"qty "+qty);

                    dbHelper.updateCartData(myProduct);
                    updateAddButtons();
                    updateCartCount();
                }
            }
        //}
    }

    private void getProductDetails(String prodId){
        if(productDetailsType==1)
            showProgress(true);
        Map<String,String> params=new HashMap<>();
        params.put("id", prodId); // as per user selected category from top horizontal categories list
        params.put("code", shopCode);
        params.put("dbName",shopCode);
        Log.d(TAG, params.toString());
        String url=getResources().getString(R.string.url)+"/products/ret_products_details";
        jsonObjectApiRequest(Request.Method.POST, url,new JSONObject(params),"productDetails");
    }

    private void checkFreeProductOffer(){
        if(type ==2 && myProduct.getProductDiscountOffer()!=null){
            ProductDiscountOffer productDiscountOffer = myProduct.getProductDiscountOffer();
            if(productDiscountOffer.getProdBuyId()!= productDiscountOffer.getProdFreeId()){
                productDetailsType = 2;
                getProductDetails(String.valueOf(productDiscountOffer.getProdFreeId()));
            }else onProductClicked(type);
        }else {
            onProductClicked(type);
        }
    }


    private float getOfferAmount(MyProduct item,int type){
        float amount = 0f;
        int qty = item.getQuantity();
        if(item.getProductPriceOffer() != null){
            ProductPriceOffer productPriceOffer = (ProductPriceOffer)item.getProductPriceOffer();
            //if(qty > 1){
                int maxSize = productPriceOffer.getProductPriceDetails().size();
                int mod = qty % maxSize;
                Log.i(TAG,"mod "+mod);
                if(mod == 0){
                    mod = maxSize;
                }
                amount = getOfferPrice(mod,item.getSellingPrice(),productPriceOffer.getProductPriceDetails());
            /*}else{
                amount = item.getSellingPrice();
            }*/

            if(type == 1)
                item.setQuantity(item.getQuantity() - 1);

        }else if(item.getProductDiscountOffer() != null){

            ProductDiscountOffer productDiscountOffer = (ProductDiscountOffer)item.getProductDiscountOffer();
            amount = item.getSellingPrice();
            if(type == 1){
                if(productDiscountOffer.getProdBuyId() == productDiscountOffer.getProdFreeId()){
                    Log.i(TAG,"item qty "+item.getQuantity()+" offer buy qty"+productDiscountOffer.getProdBuyQty());
                    Log.i(TAG,"minus mode "+(item.getQuantity() - item.getOfferItemCounter()-1)% productDiscountOffer.getProdBuyQty());
                    if((item.getQuantity() - item.getOfferItemCounter() -1)% productDiscountOffer.getProdBuyQty() ==
                            (productDiscountOffer.getProdBuyQty()-1)){
                        item.setQuantity(item.getQuantity() - 2);
                        item.setOfferItemCounter(item.getOfferItemCounter() - 1);
                        dbHelper.updateOfferCounterCartData(item.getOfferItemCounter(),Integer.parseInt(item.getId()), item.getShopCode());

                    }else{
                        item.setQuantity(item.getQuantity() - 1);
                    }
                }else{
                    item.setQuantity(item.getQuantity() - 1);
                    Log.i(TAG,"minus mode "+item.getQuantity() % productDiscountOffer.getProdBuyQty());
                    if(item.getQuantity() % productDiscountOffer.getProdBuyQty() == (productDiscountOffer.getProdBuyQty()-1)){
                        item.setOfferItemCounter(item.getOfferItemCounter() - 1);
                        if(item.getOfferItemCounter() == 0){
                            dbHelper.removeFreeProductFromCart(productDiscountOffer.getProdFreeId(), item.getShopCode());
                        }else{
                            dbHelper.updateFreeCartData(productDiscountOffer.getProdFreeId(),item.getOfferItemCounter(),0f,item.getShopCode());
                            dbHelper.updateOfferCounterCartData(item.getOfferItemCounter(),Integer.parseInt(item.getId()), item.getShopCode());
                        }
                    }

                }
            }else if(type == 2){
                if(productDiscountOffer.getProdBuyId() == productDiscountOffer.getProdFreeId()){
                    Log.i(TAG,"Same product");
                    Log.i(TAG,"item qty "+item.getQuantity()+" offer buy qty"+productDiscountOffer.getProdBuyQty());
                    Log.i(TAG,"plus mode "+(item.getQuantity() - item.getOfferItemCounter())% productDiscountOffer.getProdBuyQty());
                    if((item.getQuantity() - item.getOfferItemCounter())% productDiscountOffer.getProdBuyQty() == 0){
                        item.setQuantity(item.getQuantity() + 1);
                        item.setOfferItemCounter(item.getOfferItemCounter() + 1);
                        dbHelper.updateOfferCounterCartData(item.getOfferItemCounter(),Integer.parseInt(item.getId()), item.getShopCode());
                    }else{

                    }
                }else{
                    Log.i(TAG,"Different product");
                    if(item.getQuantity() % productDiscountOffer.getProdBuyQty() == 0){
                        item.setOfferItemCounter(item.getOfferItemCounter() + 1);
                        MyProduct item1 = null;
                        if(item.getOfferItemCounter() == 1){
                            //            item1 = dbHelper.getProductDetails(productDiscountOffer.getProdFreeId());
                            item1 = freeProdut;
                            item1.setShopCode(shopCode);
                            item1.setSellingPrice(0f);
                            item1.setQuantity(1);
                            item1.setFreeProductPosition(item.getFreeProductPosition());
                            dbHelper.addProductToCart(item1);
                            Log.d("FreeProductPosition ", ""+item.getFreeProductPosition());
                            dbHelper.updateFreePositionCartData(item.getFreeProductPosition(),Integer.parseInt(item.getId()), item.getShopCode());
                            dbHelper.updateOfferCounterCartData(item.getOfferItemCounter(),Integer.parseInt(item.getId()), item.getShopCode());
                            Log.i(TAG,"Different product added to cart");
                        }else{

                            dbHelper.updateFreeCartData(productDiscountOffer.getProdFreeId(),item.getOfferItemCounter(),0f, item.getShopCode());
                            dbHelper.updateOfferCounterCartData(item.getOfferItemCounter(),Integer.parseInt(item.getId()), item.getShopCode());
                            Log.i(TAG,"Different product updated in cart");
                        }
                        //  myItemAdapter.notifyDataSetChanged();
                    }

                }

            }else{
                amount = item.getSellingPrice();
            }

        }else{
            if(type == 1)
                item.setQuantity(item.getQuantity() - 1);
            amount = item.getSellingPrice();
        }

        return amount;
    }

    private float getOfferPrice(int qty,float sp,List<ProductPriceDetails> productPriceDetailsList){
        float amount = 0f;
        int i=-1;
        for(ProductPriceDetails productPriceDetails:productPriceDetailsList){
            if(productPriceDetails.getPcodProdQty() == qty){
                amount = productPriceDetails.getPcodPrice();
                if(qty!=1){
                    amount = amount - productPriceDetailsList.get(i).getPcodPrice();
                }
                Log.i(TAG,"offer price "+amount);
                break;
            }else{
                amount = sp;
            }
            i++;
        }
        Log.i(TAG,"final selling price "+amount);
        return amount;
    }



    public void removeCart(MyProduct cartItem){
        Map<String,String> params=new HashMap<>();
        params.put("shopCode",cartItem.getShopCode());
        params.put("prodCode", cartItem.getCode());
        params.put("custdbName", custdbName);
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
                    dbHelper.addProductToCart(myProduct);
                    updateCartCount();
                    Log.d(TAG, "added o cart" );
                }else {
                      DialogAndToast.showToast(response.getString("message"),ProductDetailActivity.this);
                }
            }else if(apiName.equals("updatCart")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)){
                    dbHelper.updateCartData(myProduct);
                    updateCartCount();
                    Log.d(TAG, "updated cart" );
                }else {
                    DialogAndToast.showToast(response.getString("message"),ProductDetailActivity.this);
                }
            }else if(apiName.equals("removeCart")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)){
                    dbHelper.removeProductFromCart(myProduct.getId(), myProduct.getShopCode());
                    updateCartCount();
                    Log.d(TAG, "Deleted cart" );
                }else {
                    DialogAndToast.showToast(response.getString("message"),ProductDetailActivity.this);
                }
            }else if (apiName.equals("productRatingsData")) {
                if (response.getBoolean("status")) {
                    if(!response.getString("result").equals("null")) {
                        JSONObject jsonObject = response.getJSONObject("result");
                        tvStarRatings.setText(String.format("%.01f", (float) jsonObject.getDouble("ratings")));
                        tvNumRatings.setText(jsonObject.getInt("ratingHits") + " Ratings");
                    }else {
                        tvStarRatings.setText(String.format("%.01f", 0.0f));
                        tvNumRatings.setText(0 + " Ratings");
                    }
                }
            }else if(apiName.equals("productDetails")) {
                if (response.getString("status").equals("true") || response.getString("status").equals(true)) {
                    JSONObject jsonObject = response.getJSONObject("result");
                    if (productDetailsType == 1) {
                        myProduct.setQoh(jsonObject.getInt("prodQoh"));
                        checkFreeProductOffer();
                    } else {
                        freeProdut = new MyProduct();
                        freeProdut.setId(jsonObject.getString("prodId"));
                        freeProdut.setCatId(jsonObject.getString("prodCatId"));
                        freeProdut.setSubCatId(jsonObject.getString("prodSubCatId"));
                        freeProdut.setName(jsonObject.getString("prodName"));
                        freeProdut.setQoh(jsonObject.getInt("prodQoh"));
                        freeProdut.setQuantity(1);
                        freeProdut.setFreeProductPosition(myProduct.getFreeProductPosition() + 1);
                        freeProdut.setMrp(Float.parseFloat(jsonObject.getString("prodMrp")));
                        freeProdut.setSellingPrice(0);
                        freeProdut.setCode(jsonObject.getString("prodCode"));
                        freeProdut.setIsBarcodeAvailable(jsonObject.getString("isBarcodeAvailable"));
                        //myProduct.setBarCode(productJArray.getJSONObject(i).getString("prodBarCode"));
                        freeProdut.setDesc(jsonObject.getString("prodDesc"));
                        freeProdut.setLocalImage(R.drawable.thumb_16);
                        freeProdut.setProdImage1(jsonObject.getString("prodImage1"));
                        freeProdut.setProdImage2(jsonObject.getString("prodImage2"));
                        freeProdut.setProdImage3(jsonObject.getString("prodImage3"));
                        freeProdut.setProdHsnCode(jsonObject.getString("prodHsnCode"));
                        freeProdut.setProdMfgDate(jsonObject.getString("prodMfgDate"));
                        freeProdut.setProdExpiryDate(jsonObject.getString("prodExpiryDate"));
                        freeProdut.setProdMfgBy(jsonObject.getString("prodMfgBy"));
                        freeProdut.setProdExpiryDate(jsonObject.getString("prodExpiryDate"));
                        freeProdut.setOfferId(jsonObject.getString("offerId"));
                        freeProdut.setProdCgst(Float.parseFloat(jsonObject.getString("prodCgst")));
                        freeProdut.setProdIgst(Float.parseFloat(jsonObject.getString("prodIgst")));
                        freeProdut.setProdSgst(Float.parseFloat(jsonObject.getString("prodSgst")));
                        freeProdut.setProdWarranty(jsonObject.getString("prodWarranty"));
                        //myProduct.setSubCatName(subcatname);
                        onProductClicked(type);
                    }

                }else {
                    DialogAndToast.showToast("Something went wrong, Please try again", ProductDetailActivity.this);
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
            float totalPrice = dbHelper.getTotalPriceCart() - (dbHelper.getTotalShopCouponDiscount()+dbHelper.getTotalShoppursCouponDiscount());


            float deliveryDistance = 0;
            /*if(deliveryDistance > ){
                float diffKms = deliveryDistance -  sharedPreferences.getInt(Constants.MIN_DELIVERY_DISTANCE,0);
                int intKms = (int)diffKms;
                float decimalKms = diffKms - intKms;

                int chargesPerKm = sharedPreferences.getInt(Constants.CHARGE_AFTER_MIN_DISTANCE,0);
                deliveryCharges = intKms * chargesPerKm + decimalKms * chargesPerKm;
                tvDeliveryCharges.setText(Utility.numberFormat(deliveryCharges));
            }else{
                tvDeliveryCharges.setText("0.00");
            }*/
            //totalPrice = totalPrice + deliveryCharges;

            cartItemPrice.setText("Amount "+ Utility.numberFormat(totalPrice));
            cartItemCount.setText("Item "+String.valueOf(dbHelper.getCartCount()));
            tv_cartCount.setText(String.valueOf(dbHelper.getProductQuantity(myProduct.getId(), shopCode)));
        }else{
            myProduct.setTotalAmount(0);
            myProduct.setQuantity(0);
            rlfooterviewcart.setVisibility(View.GONE);
            tv_cartCount.setText(String.valueOf(0));
            linear_plus_minus.setVisibility(View.GONE);
            btnAddCart.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateCartCount();
    }

    public void updateAddButtons(){
        if(dbHelper.checkProdExistInCart(myProduct.getId(), shopCode)){
            btnAddCart.setVisibility(View.GONE);
            linear_plus_minus.setVisibility(View.VISIBLE);
            Log.d("Id "+myProduct.getId(), "shopCode " +shopCode);
            Log.d("Count ", String.valueOf(dbHelper.getProductQuantity(myProduct.getId(), shopCode)));
            tv_cartCount.setText(String.valueOf(dbHelper.getProductQuantity(myProduct.getId(), shopCode)));
            myProduct.setFreeProductPosition(dbHelper.getFreeProductPosition(myProduct.getId(), shopCode));
            myProduct.setOfferItemCounter(dbHelper.getOfferCounter(myProduct.getId(), shopCode));
            myProduct.setQuantity(Integer.parseInt(tv_cartCount.getText().toString()));
            myProduct.setTotalAmount(dbHelper.getTotalAmount(myProduct.getId(), shopCode));
            myProduct.setSellingPrice(dbHelper.getProductSellingPrice(myProduct.getId(), shopCode));
            text_product_selling_price.setText(Utility.numberFormat(dbHelper.getProductSellingPrice(myProduct.getId(), shopCode)));

            float diff = myProduct.getMrp() - myProduct.getSellingPrice();
            if(diff > 0f){
                float percentage = diff * 100 / myProduct.getMrp();
                tvDiscount.setText(String.format("%.02f",percentage)+"% off");
            }else{
                tvDiscount.setVisibility(View.GONE);
                text_product_mrp.setVisibility(View.GONE);
            }
        }else {
            tv_cartCount.setText(String.valueOf(0));
            linear_plus_minus.setVisibility(View.GONE);
            btnAddCart.setVisibility(View.VISIBLE);
        }
    }
}
