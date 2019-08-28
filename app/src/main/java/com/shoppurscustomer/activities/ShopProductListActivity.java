package com.shoppurscustomer.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.shoppurscustomer.R;
import com.shoppurscustomer.adapters.ShopProductListAdapter;
import com.shoppurscustomer.adapters.TopCategoriesAdapter;
import com.shoppurscustomer.database.DbHelper;
import com.shoppurscustomer.fragments.BottomSearchFragment;
import com.shoppurscustomer.fragments.DescBottomFragment;
import com.shoppurscustomer.fragments.OfferDescriptionFragment;
import com.shoppurscustomer.interfaces.MyItemTypeClickListener;
import com.shoppurscustomer.models.Barcode;
import com.shoppurscustomer.models.CartItem;
import com.shoppurscustomer.models.Category;
import com.shoppurscustomer.models.Coupon;
import com.shoppurscustomer.models.MyProduct;
import com.shoppurscustomer.models.ProductColor;
import com.shoppurscustomer.models.ProductComboOffer;
import com.shoppurscustomer.models.ProductDiscountOffer;
import com.shoppurscustomer.models.ProductPriceDetails;
import com.shoppurscustomer.models.ProductPriceOffer;
import com.shoppurscustomer.models.ProductSize;
import com.shoppurscustomer.models.ProductUnit;
import com.shoppurscustomer.models.SubCategory;
import com.shoppurscustomer.utilities.Constants;
import com.shoppurscustomer.utilities.DialogAndToast;
import com.shoppurscustomer.utilities.Utility;

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

public class ShopProductListActivity extends NetworkBaseActivity {

    private Toolbar toolbar;
    private Menu menu;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerViewProduct, recyclerViewCategory;
    private TextView tvNoData, cartItemCount, cartItemPrice, viewCart;
    private TextView text_shop_name, tv_shortName, text_mobile, text_address, text_state_city, text_left_label, text_right_label;
    private ProgressBar progressBar;
    private RelativeLayout rlfooterviewcart;
    private ShopProductListAdapter shopProductAdapter;
    private TopCategoriesAdapter categoriesAdapter;
    private List<SubCategory> subCategoryList;
    private List<MyProduct> myProductList;
   // private CartItem cartItem;
    private MyProduct myProduct, freeProdut;
    private DbHelper dbHelper;
    private ImageView image_view_shop, image_fav, image_search, image_scan;
    private String shopCode, shopName, shopImage, shopMobile, address, statecity, catId, selectdSubCatId, selectedSubCatName, custCode, shopdbname, custdbname, dbuser, dbpassword;

    private int position, type, productDetailsType, selectdSubCatPosition;
    private int counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_product_list);
        dbHelper = new DbHelper(this);
        shopCode = sharedPreferences.getString(Constants.SHOP_INSIDE_CODE,"");
        shopName = sharedPreferences.getString(Constants.SHOP_INSIDE_NAME,"");
        shopdbname = sharedPreferences.getString(Constants.SHOP_DBNAME,"");
        shopImage = getIntent().getStringExtra("photo");
        Log.d("shopImage", shopImage);
        shopMobile = getIntent().getStringExtra("mobile");
        address = getIntent().getStringExtra("address");
        statecity = getIntent().getStringExtra("stateCity");
        catId = getIntent().getStringExtra("catId");
        selectdSubCatId = getIntent().getStringExtra("subcatid");
        selectedSubCatName = getIntent().getStringExtra("subcatname");
        custCode = sharedPreferences.getString(Constants.USER_ID,"");
        custdbname = sharedPreferences.getString(Constants.DB_NAME, "");

        initViews();
    }

    private void initViews(){
        text_shop_name = findViewById(R.id.text_shop_name);
        tv_shortName = findViewById(R.id.tv_shortName);
        image_view_shop = findViewById(R.id.image_view_shop);
        text_mobile = findViewById(R.id.text_mobile);
        text_address = findViewById(R.id.text_address);
        text_state_city = findViewById(R.id.text_state_city);
        image_fav = findViewById(R.id.image_fav);
        Log.d("isShopFavourite ", ""+dbHelper.isShopFavorited(shopCode));
        if(dbHelper.isShopFavorited(shopCode))
            image_fav.setImageDrawable(ContextCompat.getDrawable(ShopProductListActivity.this,R.drawable.favroite_selected));
        else image_fav.setImageDrawable(ContextCompat.getDrawable(ShopProductListActivity.this, R.drawable.favrorite_select));

        image_search = findViewById(R.id.image_search);
        image_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dbHelper.isShopFavorited(shopCode))
                    shopFavorite("N");
                else shopFavorite("Y");
            }
        });

        image_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSearchFragment bottomSearchFragment = new BottomSearchFragment();
                Bundle bundle = new Bundle();
                bundle.putString("shopCode", shopCode);
                bundle.putString("shopName", shopName);
                bundle.putString("shopAddress", address);
                bundle.putString("shopMobile", shopMobile);
                bottomSearchFragment.setArguments(bundle);
                bottomSearchFragment.setCallingActivityName("ShopProductListActivity", sharedPreferences, isDarkTheme);
                bottomSearchFragment.setSubCatName(selectedSubCatName);
                bottomSearchFragment.setSubcatId(selectdSubCatId);
                bottomSearchFragment.show(getSupportFragmentManager(), bottomSearchFragment.getTag());
            }
        });

        image_scan = findViewById(R.id.image_scan);
        image_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShopProductListActivity.this,ScannarActivity.class);
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

        setShopDetails();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        swipeRefreshLayout =findViewById(R.id.swipe_refresh);
       // tv_shopname = findViewById(R.id.tv_shopname);
        recyclerViewProduct = findViewById(R.id.recycler_viewProduct);
        recyclerViewCategory = findViewById(R.id.recycler_viewCategory);
        tvNoData = findViewById(R.id.tvNoData);
        progressBar=findViewById(R.id.progress_bar);
        rlfooterviewcart = findViewById(R.id.rlfooterviewcart);
        cartItemCount= findViewById(R.id.itemCount);
        cartItemPrice = findViewById(R.id.itemPrice);
        viewCart = findViewById(R.id.viewCart);

//        tv_shopname.setText(shopName);
       /* setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);*/
        getsubCategories();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(subCategoryList.size()>0)
                getProducts(selectdSubCatId, "");
            }
        });
    }

    private void setShopDetails(){
        text_shop_name.setText(shopName);

        if(shopName.length()>1) {
            tv_shortName.setText(shopName.substring(0, 1));
            //image_view_shop .setText(shopName);
            text_mobile.setText(shopMobile);
            text_address.setText(address);
            text_state_city.setText(statecity);

            String initials = "";
            if (shopName.contains(" ")) {
                String[] nameArray = shopName.split(" ");
                initials = nameArray[0].substring(0, 1) + nameArray[1].substring(0, 1);
            } else {
                initials = shopName.substring(0, 2);
            }

            tv_shortName.setText(initials);
        }

        if(shopImage !=null && shopImage.contains("http")){
            tv_shortName.setVisibility(View.GONE);
            image_view_shop.setVisibility(View.VISIBLE);
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
            requestOptions.dontTransform();
            // requestOptions.override(Utility.dpToPx(150, context), Utility.dpToPx(150, context));
            // requestOptions.centerCrop();
            requestOptions.skipMemoryCache(false);

            Glide.with(this)
                    .load(shopImage)
                    .apply(requestOptions)
                    .into(image_view_shop);

        }else{
            tv_shortName.setVisibility(View.VISIBLE);
            image_view_shop.setVisibility(View.GONE);
        }
    }

    private void getsubCategories(){
        subCategoryList = new ArrayList<>();
        recyclerViewCategory.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false);
        recyclerViewCategory.setLayoutManager(layoutManager);
        recyclerViewCategory.setItemAnimator(new DefaultItemAnimator());
        categoriesAdapter = new TopCategoriesAdapter(this, subCategoryList);
        recyclerViewCategory.setAdapter(categoriesAdapter);


        Map<String,String> params=new HashMap<>();
        params.put("code", shopCode);
        params.put("dbName", sharedPreferences.getString(Constants.DB_NAME, ""));
        String url=getResources().getString(R.string.root_url)+"categories/retailer_sub_categories";
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url, new JSONObject(params),"get_subcategories");
    }


    public void getProducts(String subCatId, String action){
        selectdSubCatId = subCatId;
        myProductList = new ArrayList<>();
        recyclerViewProduct.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewProduct.setLayoutManager(layoutManager);
        recyclerViewProduct.setItemAnimator(new DefaultItemAnimator());
        shopProductAdapter = new ShopProductListAdapter(this, myProductList);
        shopProductAdapter.setShopCode(shopCode);
        shopProductAdapter.setDarkTheme(isDarkTheme);
        recyclerViewProduct.setAdapter(shopProductAdapter);

        myProductList.clear();
        if(swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);

        Map<String,String> params=new HashMap<>();
        params.put("subCatId", subCatId); // as per user selected category from top horizontal categories list
        params.put("shopCode", shopCode);
        params.put("dbName",shopdbname);
        Log.d(TAG, params.toString());
        String url=getResources().getString(R.string.url)+"/products/ret_productslist";
        showProgressBar(true);
        jsonObjectApiRequest(Request.Method.POST, url,new JSONObject(params),"productfromshop");
    }


    public void updateCart(int type, int position){
        Log.d("clicked Position ", position+"");
        this.position = position;
        this.type = type;
        if(type==2){
            productDetailsType = 1;
            getProductDetails(myProductList.get(position).getId());
        }else onProductClicked(type, position);
    }

    private void onProductClicked(int type, int position){
        this.position = position;
        this.myProduct = myProductList.get(position);
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
                    shopProductAdapter.notifyItemChanged(position);
                    updateCartCount();
                    Log.d("onRemove Qyantity ", myProduct.getQuantity()+"");
                }else{
                    int qty = myProduct.getQuantity() - 1;
                    float netSellingPrice = getOfferAmount(myProduct,type);
                    myProduct.setQuantity(myProduct.getQuantity());
                    qty = myProduct.getQuantity();
                    Log.i(TAG,"netSellingPrice "+netSellingPrice);
                    float amount = 0;
                    amount = myProduct.getTotalAmount() - netSellingPrice;
                    Log.i(TAG,"tot amount "+amount);
                    myProduct.setTotalAmount(amount);
                    if(myProduct.getProductPriceOffer()!=null){
                        myProduct.setSellingPrice(amount/qty);
                    }
                    dbHelper.updateCartData(myProduct);
                    shopProductAdapter.notifyItemChanged(position);
                    updateCartCount();
                }
            }

        }else if(type == 2){
            /*if(myProduct.getIsBarcodeAvailable().equals("Y")){
                if(myProduct.getQuantity() == myProduct.getBarcodeList().size()){
                    shopProductAdapter.notifyDataSetChanged();
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
                    shopProductAdapter.notifyItemChanged(position);
                    updateCartCount();
                }

           }else{*/
                if(myProduct.getQuantity() >= myProduct.getQoh()){
                    shopProductAdapter.notifyDataSetChanged();
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
                    float amount = 0;
                    Log.i(TAG,"netSellingPrice "+netSellingPrice);
                    amount = myProduct.getTotalAmount() + netSellingPrice;
                    Log.i(TAG,"tot amount "+amount);
                    myProduct.setTotalAmount(amount);
                    if(myProduct.getProductPriceOffer()!=null){
                        myProduct.setSellingPrice(amount/qty);
                    }
                    qty = myProduct.getQuantity();
                    Log.i(TAG,"qty "+qty);
                    myProduct.setQuantity(myProduct.getQuantity());
                    dbHelper.updateCartData(myProduct);
                    shopProductAdapter.notifyItemChanged(position);
                    shopProductAdapter.notifyDataSetChanged();
                    updateCartCount();
                }
          //  }
        }
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
        if(type ==2 && myProductList.get(position).getProductDiscountOffer()!=null){
            ProductDiscountOffer productDiscountOffer = myProductList.get(position).getProductDiscountOffer();
            if(productDiscountOffer.getProdBuyId()!= productDiscountOffer.getProdFreeId()){
                productDetailsType = 2;
                getProductDetails(String.valueOf(productDiscountOffer.getProdFreeId()));
            }else onProductClicked(type, position);
        }else {
            onProductClicked(type,position);
        }
    }



    public void removeCart(MyProduct myProduct){
        this.myProduct = myProduct;
       /* cartItem = new CartItem();
        cartItem.setShopCode(shopCode);
        cartItem.setProdCode(myProduct.getCode());*/

        Map<String,String> params=new HashMap<>();
        params.put("shopCode",shopCode);
        params.put("prodCode", myProduct.getCode());
        params.put("dbName", custdbname);
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
        params.put("dbName", custdbname);
        params.put("dbUserName",dbuser);
        params.put("dbPassword",dbpassword);
        Log.d(TAG, params.toString());
        String url=getResources().getString(R.string.url)+"/shop/favourite";
        //showProgressBar(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"shopFavorite");
    }

    @Override
    public void onJsonObjectResponse(JSONObject response, String apiName) {
        showProgressBar(false);
        try {
            // JSONObject jsonObject=response.getJSONObject("response");
            Log.d("response", response.toString());
            if(apiName.equals("productfromshop")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)) {
                    JSONObject dataObject;
                     if(!response.getString("result").equals("null")){
                    JSONArray productJArray = response.getJSONArray("result");

                    for (int i = 0; i < productJArray.length(); i++) {
                        MyProduct myProduct = new MyProduct();
                        myProduct.setShopCode(shopCode);
                        myProduct.setId(productJArray.getJSONObject(i).getString("prodId"));
                        myProduct.setCatId(productJArray.getJSONObject(i).getString("prodCatId"));
                        myProduct.setSubCatId(productJArray.getJSONObject(i).getString("prodSubCatId"));
                        myProduct.setName(productJArray.getJSONObject(i).getString("prodName"));
                        myProduct.setQoh(productJArray.getJSONObject(i).getInt("prodQoh"));
                        Log.d("Qoh ", myProduct.getQoh()+"");
                        myProduct.setMrp(Float.parseFloat(productJArray.getJSONObject(i).getString("prodMrp")));
                        myProduct.setSellingPrice(Float.parseFloat(productJArray.getJSONObject(i).getString("prodSp")));
                        myProduct.setCode(productJArray.getJSONObject(i).getString("prodCode"));
                        myProduct.setIsBarcodeAvailable(productJArray.getJSONObject(i).getString("isBarcodeAvailable"));
                        //myProduct.setBarCode(productJArray.getJSONObject(i).getString("prodBarCode"));
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
                        myProduct.setProdCgst(Float.parseFloat(productJArray.getJSONObject(i).getString("prodCgst")));
                        myProduct.setProdIgst(Float.parseFloat(productJArray.getJSONObject(i).getString("prodIgst")));
                        myProduct.setProdSgst(Float.parseFloat(productJArray.getJSONObject(i).getString("prodSgst")));
                        myProduct.setProdWarranty(productJArray.getJSONObject(i).getString("prodWarranty"));
                        //myProduct.setSubCatName(subcatname);


                        int innerLen = 0;
                        int len = 0;

                        JSONArray tempArray = null;
                        JSONObject jsonObject = null, tempObject = null;
                        ProductUnit productUnit = null;
                        ProductSize productSize = null;
                        ProductColor productColor = null;

                        if (!productJArray.getJSONObject(i).getString("productUnitList").equals("null")) {
                            tempArray = productJArray.getJSONObject(i).getJSONArray("productUnitList");
                            int tempLen = tempArray.length();
                            List<ProductUnit> productUnitList = new ArrayList<>();

                            for (int unitCounter = 0; unitCounter < tempLen; unitCounter++) {
                                tempObject = tempArray.getJSONObject(unitCounter);
                                productUnit = new ProductUnit();
                                productUnit.setId(tempObject.getInt("id"));
                                productUnit.setUnitName(tempObject.getString("unitName"));
                                productUnit.setUnitValue(tempObject.getString("unitValue"));
                                productUnit.setStatus(tempObject.getString("status"));
                                productUnitList.add(productUnit);
                            }
                            myProduct.setProductUnitList(productUnitList);
                        }

                        if (!productJArray.getJSONObject(i).getString("productSizeList").equals("null")) {
                            tempArray = productJArray.getJSONObject(i).getJSONArray("productSizeList");
                            int tempLen = tempArray.length();
                            List<ProductSize> productSizeList = new ArrayList<>();

                            for (int unitCounter = 0; unitCounter < tempLen; unitCounter++) {
                                List<ProductColor> productColorList = new ArrayList<>();
                                tempObject = tempArray.getJSONObject(unitCounter);
                                productSize = new ProductSize();
                                productSize.setId(tempObject.getInt("id"));
                                productSize.setSize(tempObject.getString("size"));
                                productSize.setStatus(tempObject.getString("status"));
                                productSize.setProductColorList(productColorList);

                                if (!productJArray.getJSONObject(i).getString("productSizeList").equals("null")) {
                                    JSONArray colorArray = tempObject.getJSONArray("productColorList");
                                    for (int colorCounter = 0; colorCounter < colorArray.length(); colorCounter++) {
                                        tempObject = colorArray.getJSONObject(colorCounter);
                                        productColor = new ProductColor();
                                        productColor.setId(tempObject.getInt("id"));
                                        productColor.setSizeId(tempObject.getInt("sizeId"));
                                        productColor.setColorName(tempObject.getString("colorName"));
                                        productColor.setColorValue(tempObject.getString("colorValue"));
                                        productColor.setStatus(tempObject.getString("status"));
                                        productColorList.add(productColor);
                                    }

                                }
                                productSizeList.add(productSize);
                            }
                            myProduct.setProductSizeList(productSizeList);
                        }


                        if (!productJArray.getJSONObject(i).getString("barcodeList").equals("null")) {
                            JSONArray productBarCodeArray = productJArray.getJSONObject(i).getJSONArray("barcodeList");
                            len = productBarCodeArray.length();
                            JSONObject barcodeJsonObject = null;
                            List<Barcode> barcodeList = new ArrayList<>();
                            for (int j = 0; j < len; j++) {
                                barcodeJsonObject = productBarCodeArray.getJSONObject(j);
                                barcodeList.add(new Barcode(barcodeJsonObject.getString("barcode")));
                                // dbHelper.addProductBarcode(jsonObject.getInt("prodId"),jsonObject.getString("prodBarCode"));
                            }
                            myProduct.setBarcodeList(barcodeList);
                        }

                        if (!productJArray.getJSONObject(i).getString("productDiscountOfferList").equals("null")) {
                            JSONArray freeArray = productJArray.getJSONObject(i).getJSONArray("productDiscountOfferList");
                            len = freeArray.length();
                            ProductDiscountOffer productDiscountOffer = null;
                            for (int k = 0; k < len; k++) {
                                dataObject = freeArray.getJSONObject(k);
                                Log.d("index ", "" + len);
                                productDiscountOffer = new ProductDiscountOffer();
                                productDiscountOffer.setId(dataObject.getInt("id"));
                                productDiscountOffer.setOfferName(dataObject.getString("offerName"));
                                productDiscountOffer.setProdBuyId(dataObject.getInt("prodBuyId"));
                                productDiscountOffer.setProdFreeId(dataObject.getInt("prodFreeId"));
                                productDiscountOffer.setProdBuyQty(dataObject.getInt("prodBuyQty"));
                                productDiscountOffer.setProdFreeQty(dataObject.getInt("prodFreeQty"));
                                productDiscountOffer.setStatus(dataObject.getString("status"));
                                productDiscountOffer.setStartDate(dataObject.getString("startDate"));
                                productDiscountOffer.setEndDate(dataObject.getString("endDate"));

                                //myProduct.setproductoffer
                                myProduct.setOfferId(String.valueOf(productDiscountOffer.getId()));
                                myProduct.setOfferType("free");
                                myProduct.setProductOffer(productDiscountOffer);

                                //  dbHelper.addProductFreeOffer(productDiscountOffer, Utility.getTimeStamp(),Utility.getTimeStamp());
                            }
                            myProduct.setProductDiscountOffer(productDiscountOffer);
                        }


                        if (!productJArray.getJSONObject(i).getString("productPriceOffer").equals("null")) {
                            JSONArray priceArray = productJArray.getJSONObject(i).getJSONArray("productPriceOffer");
                            len = priceArray.length();
                            JSONArray productPriceArray = null;
                            ProductPriceOffer productPriceOffer = null;
                            ProductPriceDetails productPriceDetails;
                            List<ProductPriceDetails> productPriceOfferDetails = null;
                            for (int l = 0; l < len; l++) {
                                dataObject = priceArray.getJSONObject(l);
                                productPriceOffer = new ProductPriceOffer();
                                productPriceOffer.setId(dataObject.getInt("id"));
                                productPriceOffer.setProdId(dataObject.getInt("prodId"));
                                productPriceOffer.setOfferName(dataObject.getString("offerName"));
                                productPriceOffer.setStatus(dataObject.getString("status"));
                                productPriceOffer.setStartDate(dataObject.getString("startDate"));
                                productPriceOffer.setEndDate(dataObject.getString("endDate"));
                                productPriceArray = dataObject.getJSONArray("productComboOfferDetails");

                                myProduct.setOfferId(String.valueOf(productPriceOffer.getId()));
                                myProduct.setOfferType("price");
                                myProduct.setProductOffer(productPriceOffer);


                                productPriceOfferDetails = new ArrayList<>();
                                innerLen = productPriceArray.length();
                                for (int k = 0; k < innerLen; k++) {
                                    dataObject = productPriceArray.getJSONObject(k);
                                    productPriceDetails = new ProductPriceDetails();
                                    productPriceDetails.setId(dataObject.getInt("id"));
                                    productPriceDetails.setPcodPcoId(dataObject.getInt("pcodPcoId"));
                                    productPriceDetails.setPcodProdQty(dataObject.getInt("pcodProdQty"));
                                    productPriceDetails.setPcodPrice((float) dataObject.getDouble("pcodPrice"));
                                    if(k==0)
                                        myProduct.setSellingPrice(productPriceDetails.getPcodPrice());
                                    productPriceDetails.setStatus(dataObject.getString("status"));
                                    productPriceOfferDetails.add(productPriceDetails);
                                }
                                productPriceOffer.setProductPriceDetails(productPriceOfferDetails);
                            }
                            myProduct.setProductPriceOffer(productPriceOffer);
                        }



                        myProductList.add(myProduct);

                    }
                }
                    if(myProductList.size()>0){
                        shopProductAdapter.notifyDataSetChanged();
                    }else {
                        showNoData(true);
                    }

                }else {
                    DialogAndToast.showDialog(response.getString("message"),ShopProductListActivity.this);
                }
            }else if(apiName.equals("get_subcategories")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)){
                    JSONArray subcatJArray = response.getJSONArray("result");

                    for (int j = 0; j < subcatJArray.length(); j++) {
                            SubCategory subCategory = new SubCategory();
                            subCategory.setSubcatid(subcatJArray.getJSONObject(j).getString("subCatId"));
                            subCategory.setId(subcatJArray.getJSONObject(j).getString("catId"));
                            subCategory.setName(subcatJArray.getJSONObject(j).getString("subCatName"));
                            subCategoryList.add(subCategory);
                    }

                    if(subCategoryList.size()>0) {

                        if(!TextUtils.isEmpty(selectdSubCatId)) {
                            selectdSubCatPosition = 0;
                            for (SubCategory subCategory : subCategoryList) {
                                if (subCategory.getSubcatid().equals(selectdSubCatId)) {
                                    subCategory.setSelected(true);
                                    selectdSubCatPosition = subCategoryList.indexOf(subCategory);
                                    break;
                                }
                            }
                        }else {
                            subCategoryList.get(0).setSelected(true);
                            selectdSubCatId = subCategoryList.get(0).getSubcatid();
                        }

                        categoriesAdapter.notifyDataSetChanged();
                        Log.d("selectdSubCatPosition", ""+selectdSubCatPosition);
                        recyclerViewCategory.scrollToPosition(selectdSubCatPosition);
                        getProducts(selectdSubCatId, "");
                    }
                }else {
                    DialogAndToast.showToast(response.getString("message"),ShopProductListActivity.this);
                }
            } else if(apiName.equals("addtocart")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)){
                    dbHelper.addProductToCart(myProduct);
                    updateCartCount();
                    Log.d(TAG, "added o cart" );
                }else {
                    DialogAndToast.showToast(response.getString("message"),ShopProductListActivity.this);
                }
            }else if(apiName.equals("updatCart")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)){
                  dbHelper.updateCartData(myProduct);
                    updateCartCount();
                    Log.d(TAG, "updated cart" );
                }else {
                    DialogAndToast.showToast(response.getString("message"),ShopProductListActivity.this);
                }
            }else if(apiName.equals("removeCart")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)){
                //    dbHelper.deleteCart(cartItem);
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
                        image_fav.setImageDrawable(ContextCompat.getDrawable(ShopProductListActivity.this, R.drawable.favrorite_select));
                    }else {
                        dbHelper.add_to_Favorite(shopCode);
                        image_fav.setImageDrawable(ContextCompat.getDrawable(ShopProductListActivity.this, R.drawable.favroite_selected));
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
            }else if(apiName.equals("productDetails")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)){
                    JSONObject jsonObject = response.getJSONObject("result");
                    if(productDetailsType == 1){
                        myProductList.get(position).setQoh(jsonObject.getInt("prodQoh"));
                        Log.d("Qoh ", myProductList.get(position).getQoh()+"");
                        checkFreeProductOffer();
                    }else {
                        freeProdut = new MyProduct();
                        freeProdut.setId(jsonObject.getString("prodId"));
                        freeProdut.setCatId(jsonObject.getString("prodCatId"));
                        freeProdut.setSubCatId(jsonObject.getString("prodSubCatId"));
                        freeProdut.setName(jsonObject.getString("prodName"));
                        freeProdut.setQoh(jsonObject.getInt("prodQoh"));
                        freeProdut.setQuantity(1);
                        freeProdut.setFreeProductPosition(position+1);
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
                        onProductClicked(type, position);
                    }

                }else {
                    DialogAndToast.showToast("Something went wrong, Please try again", ShopProductListActivity.this);
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
        if(shopProductAdapter!=null) {
            shopProductAdapter.notifyDataSetChanged();
            counter = dbHelper.getCartCount();
        }
        updateCartCount();
    }

    float totalPrice;
    public void updateCartCount(){
        totalPrice = 0;
        if(dbHelper.getCartCount()>0){
            rlfooterviewcart.setVisibility(View.VISIBLE);
            viewCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ShopProductListActivity.this, CartActivity.class));
                }
            });
            totalPrice = dbHelper.getTotalPriceCart();
            Log.d("onRemove totalPrice ",totalPrice+"" );

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

            Coupon coupon = dbHelper.getCouponOffer("SHP1");
            if(coupon!=null && coupon.getAmount()>0) {
                Float couponDiscount = coupon.getAmount();
                totalPrice = totalPrice - couponDiscount;
            }

            cartItemPrice.setText("Amount "+ Utility.numberFormat(totalPrice));
            cartItemCount.setText("Item "+String.valueOf(dbHelper.getCartCount()));
        }else rlfooterviewcart.setVisibility(View.GONE);

    }


    /*@Override
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
    }*/


    public void showOfferDescription(MyProduct item){
        OfferDescriptionFragment offerDescriptionFragment = new OfferDescriptionFragment();
        offerDescriptionFragment.setProduct(item);
        offerDescriptionFragment.setColorTheme(colorTheme);
        offerDescriptionFragment.show(getSupportFragmentManager(), "Offer Description Bottom Sheet");
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
           // }else{
           //     amount = item.getSellingPrice();
            //}

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
                            dbHelper.updateFreeCartData(productDiscountOffer.getProdFreeId(),item.getOfferItemCounter(),0f, item.getShopCode());
                            dbHelper.updateOfferCounterCartData(item.getOfferItemCounter(),Integer.parseInt(item.getId()), item.getShopCode());
                        }
                    }

                }
            }else if(type == 2){
                if(productDiscountOffer.getProdBuyId() == productDiscountOffer.getProdFreeId()){
                    Log.i(TAG,"Same product");
                    Log.i(TAG,"item qty "+item.getQuantity()+" offer buy qty"+productDiscountOffer.getProdBuyQty());
                    Log.i(TAG,"plus mode "+(item.getQuantity() - item.getOfferItemCounter())% productDiscountOffer.getProdBuyQty());
                    Log.d(TAG, " offerCounter "+item.getOfferItemCounter() +" shopCode "+item.getShopCode() +" Qyantity "+item.getQuantity() +" prodId "+item.getId() +" offer Amount "+amount);
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

    public void showLargeImageDialog(MyProduct product, View view){
        showImageDialog(product.getProdImage1(),view);
    }
}
