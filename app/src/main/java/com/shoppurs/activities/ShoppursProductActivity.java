package com.shoppurs.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.shoppurs.R;
import com.shoppurs.activities.Settings.SettingActivity;
import com.shoppurs.adapters.ShopProductListAdapter;
import com.shoppurs.adapters.StoreAdapter;
import com.shoppurs.adapters.TopCategoriesAdapter;
import com.shoppurs.database.DbHelper;
import com.shoppurs.fragments.BottomLocationFragment;
import com.shoppurs.fragments.BottomSearchFragment;
import com.shoppurs.fragments.FrequencyFragment;
import com.shoppurs.fragments.OfferDescriptionFragment;
import com.shoppurs.interfaces.LocationActionListener;
import com.shoppurs.models.Barcode;
import com.shoppurs.models.CatListItem;
import com.shoppurs.models.Category;
import com.shoppurs.models.MyProduct;
import com.shoppurs.models.MyShop;
import com.shoppurs.models.ProductColor;
import com.shoppurs.models.ProductDiscountOffer;
import com.shoppurs.models.ProductFrequency;
import com.shoppurs.models.ProductPriceDetails;
import com.shoppurs.models.ProductPriceOffer;
import com.shoppurs.models.ProductSize;
import com.shoppurs.models.ProductUnit;
import com.shoppurs.models.ShopDeliveryModel;
import com.shoppurs.models.SubCategory;
import com.shoppurs.utilities.AppController;
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

public class ShoppursProductActivity extends HandleCartActivity {

    private List<Object> categoryList;
    private StoreAdapter myItemAdapter;
    private RecyclerView recyclerView;
    private TextView textViewError;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private CircleImageView customer_profile;
    private ImageView iv_cart, iv_search;
    private TextView text_left_label, text_right_label;
    private boolean isVisible;
    protected ProgressDialog progressDialog;

    private RecyclerView recyclerViewProduct, recyclerViewCategory;
    private TextView tvNoData, cartItemCount, cartItemPrice, viewCart;
    private RelativeLayout rlfooterviewcart;
    private ShopProductListAdapter shopProductAdapter;
    private TopCategoriesAdapter categoriesAdapter;
    private List<SubCategory> subCategoryList;
    private List<MyProduct> myProductList;
    private DbHelper dbHelper;
    private String shopCode, shopName, shopImage, shopMobile, address, statecity, catId, selectdSubCatId, selectedSubCatName, shopdbname;
    private int position, type, productDetailsType, selectdSubCatPosition;
    private int counter;
    private ShopDeliveryModel shopDeliveryModel;
    private Typeface typeface;
    private boolean isFrequencySelected;
    private View view_top_sub_cat, seperator_1;

    public boolean isFrequencySelected() {
        return isFrequencySelected;
    }
    public void setFrequencySelected(boolean frequencySelected, ProductFrequency frequency, int position) {
        isFrequencySelected = frequencySelected;
        myProductList.get(position).setFrequency(frequency);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoppurs_product);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();
        getShopDetails();
    }

    private void init(){
        dbHelper = new DbHelper(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading..");

        swipeRefreshLayout=findViewById(R.id.swipe_refresh);
        progressBar=findViewById(R.id.progress_bar);
        textViewError = findViewById(R.id.tvNoData);
        categoryList = new ArrayList<>();
        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        myItemAdapter=new StoreAdapter(this,categoryList,"catList");
        myItemAdapter.setColorTheme(colorTheme);
        recyclerView.setAdapter(myItemAdapter);

        text_left_label = findViewById(R.id.text_left_label);
        text_right_label = findViewById(R.id.text_right_label);

        //initFooter(this,3);

        iv_search = findViewById(R.id.iv_search);
        iv_search.setColorFilter(colorTheme,
                android.graphics.PorterDuff.Mode.SRC_IN);
        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSearchFragment bottomSearchFragment = new BottomSearchFragment();
                Bundle bundle = new Bundle();
                bundle.putString("shopCode", shopCode);
                bundle.putString("shopName", shopName);
                bundle.putString("shopAddress", address);
                bundle.putString("shopMobile", shopMobile);
                bottomSearchFragment.setArguments(bundle);
                bottomSearchFragment.setCallingActivityName("ShoppursProductActivity", sharedPreferences, isDarkTheme);
                bottomSearchFragment.setTypeFace(Utility.getSimpleLineIconsFont(ShoppursProductActivity.this));
                bottomSearchFragment.setSubCatName(selectedSubCatName);
                bottomSearchFragment.setSubcatId(selectdSubCatId);
                bottomSearchFragment.show(getSupportFragmentManager(), bottomSearchFragment.getTag());
            }
        });
        iv_cart = findViewById(R.id.iv_cart);
        iv_cart.setColorFilter(colorTheme,
                android.graphics.PorterDuff.Mode.SRC_IN);
        iv_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ShoppursProductActivity.this, CartActivity.class));
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
                Intent intent = new Intent(ShoppursProductActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        shopCode = "SHP1";
        shopDeliveryModel = new ShopDeliveryModel();

        recyclerViewProduct = findViewById(R.id.recycler_viewProduct);
        recyclerViewCategory = findViewById(R.id.recycler_viewCategory);
        tvNoData = findViewById(R.id.tvNoData);
        progressBar=findViewById(R.id.progress_bar);
        rlfooterviewcart = findViewById(R.id.rlfooterviewcart);
        cartItemCount= findViewById(R.id.itemCount);
        cartItemPrice = findViewById(R.id.itemPrice);
        viewCart = findViewById(R.id.viewCart);

        myProductList = new ArrayList<>();
        // recyclerViewProduct.setHasFixedSize(true);
        final RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewProduct.setLayoutManager(linearLayoutManager);
        recyclerViewProduct.setItemAnimator(new DefaultItemAnimator());
        shopProductAdapter = new ShopProductListAdapter(this, myProductList);
        shopProductAdapter.setTypeFace(Utility.getSimpleLineIconsFont(this));
        shopProductAdapter.setShopCode(shopCode);
        shopProductAdapter.setDarkTheme(isDarkTheme);
        recyclerViewProduct.setAdapter(shopProductAdapter);

//        tv_shopname.setText(shopName);
       /* setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);*/
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                offset = 0;
                if(subCategoryList.size()>0)
                    getProducts(selectdSubCatId, "onRefreshProduct");
            }
        });

        recyclerViewProduct.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(isScroll){
                    int  visibleItemCount, totalItemCount, pastVisibleItems;
                    visibleItemCount = linearLayoutManager.getChildCount();
                    totalItemCount = linearLayoutManager.getItemCount();
                    Log.i(TAG,"visible "+visibleItemCount+" total "+totalItemCount);
                    pastVisibleItems = ((LinearLayoutManager)linearLayoutManager).findLastVisibleItemPosition();
                    Log.i(TAG,"total visible "+(visibleItemCount+pastVisibleItems));

                    if (!loading) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            loading = true;
                            offset = limit + offset;
                            getProducts(selectdSubCatId, "");
                        }
                    }
                }
            }
        });

        view_top_sub_cat = findViewById(R.id.view_top_sub_cat);
        seperator_1 = findViewById(R.id.seperator_1);

    }

    private void getShopDetails(){
        Map<String,String> params=new HashMap<>();
        String url=getResources().getString(R.string.url_customer)+"/api/customers/shop/shop_details_by_code";
        params.put("code", shopCode);
        params.put("dbName", sharedPreferences.getString(Constants.DB_NAME, ""));
        params.put("dbUserName", sharedPreferences.getString(Constants.DB_USER_NAME, ""));
        params.put("dbPassword", sharedPreferences.getString(Constants.DB_PASSWORD, ""));
        Log.d(TAG, params.toString());
        showProgressBar(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"shopDetails");
    }

    public void getAllCategories(){
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
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"categories");
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
        params.put("id",catId);
        params.put("dbName", sharedPreferences.getString(Constants.DB_NAME, ""));
        String url=getResources().getString(R.string.url_customer)+"/api/customers/subcategories";
        //showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url, new JSONObject(params),"get_subcategories");
    }


    public void getProducts(String subCatId, String action){
        if(action.equals("onSubCategorySelected") || action.equals("onRefreshProduct")){
            AppController.getInstance().getRequestQueue().cancelAll("MyRequstTag");
            offset=0;
            myProductList.clear();
            shopProductAdapter.notifyDataSetChanged();
        }
        selectdSubCatId = subCatId;
        if(swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);

        Map<String,String> params=new HashMap<>();
        params.put("subCatId", subCatId); // as per user selected category from top horizontal categories list
        params.put("shopCode", shopCode);
        params.put("dbName",shopdbname);
        params.put("limit", ""+limit);
        params.put("offset", ""+offset);
        Log.d(TAG, params.toString());
        String url=getResources().getString(R.string.url_customer)+"/api/customers/products/ret_productslist";
        showProgressBar(true);
        jsonObjectApiRequest(Request.Method.POST, url,new JSONObject(params),"productfromshop");
    }


    public void updateCart(int type, int position){
        Log.d("clicked Position ", position+"");
        this.position = position;
        this.type = type;
        updateCart(type, position, shopCode, myProductList.get(position),shopDeliveryModel, this);
    }

    public void updateFrequencyCart(int type, int position){
        Log.d("clicked Position ", position+"");
        this.position = position;
        this.type = type;

        if(myProductList.get(position).getFrequency()!=null)
            addFrequencyProducttoCart(type, position, shopCode, myProductList.get(position),shopDeliveryModel, this);
    }

    @Override
    public void onJsonObjectResponse(JSONObject response, String apiName) {
        try {
            // JSONObject jsonObject=response.getJSONObject("response");
            Log.d("response", response.toString());

            if(swipeRefreshLayout.isRefreshing())
                swipeRefreshLayout.setRefreshing(false);
            if(apiName.equals("productfromshop"))
            showProgressBar(false);
            if (apiName.equals("shopDetails")) {
                if (response.getString("status").equals("true") || response.getString("status").equals(true)) {
                    //DialogAndToast.showDialog(response.getString("message"), ScannarActivity.this);
                    if(response.getString("result")!=null) {
                        JSONObject jsonObject = response.getJSONArray("result").getJSONObject(0);

                        shopName = jsonObject.getString("retshopname");
                        shopdbname = jsonObject.getString("dbname");
                        shopMobile = jsonObject.getString("retmobile");
                        address =  jsonObject.getString("retaddress");
                        shopImage = jsonObject.getString("retphoto");
                        shopDeliveryModel.setShopCode(shopCode);
                        shopDeliveryModel.setRetLat(jsonObject.getDouble("retLat"));
                        shopDeliveryModel.setRetLong(jsonObject.getDouble("retLong"));
                        shopDeliveryModel.setIsDeliveryAvailable(jsonObject.getString("isDeliveryAvailable"));
                        shopDeliveryModel.setMinDeliveryAmount(jsonObject.getDouble("minDeliveryAmount"));
                        shopDeliveryModel.setMinDeliverytime(jsonObject.getString("minDeliverytime"));
                        shopDeliveryModel.setMinDeliverydistance(jsonObject.getInt("minDeliverydistance"));
                        shopDeliveryModel.setChargesAfterMinDistance(jsonObject.getDouble("chargesAfterMinDistance"));
                        editor.putString(Constants.SHOP_INSIDE_CODE,shopCode);
                        editor.putString(Constants.SHOP_INSIDE_NAME, shopName);
                        editor.putString(Constants.SHOP_DBNAME,shopdbname);
                        editor.commit();
                        getAllCategories();
                    }else {
                        showMyDialog(response.getString("message"));
                    }
                } else {
                    showMyDialog(response.getString("message"));
                }
            }else if(apiName.equals("categories")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)){
                    JSONObject jsonObject = response.getJSONObject("result");
                    JSONArray catJArray = jsonObject.getJSONArray("categories");
                    List<Object> catList = new ArrayList<>();
                    Category category = new Category();
                    category.setName("Scan Products");
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
                        myItem.setType(0);
                        myItem.setItemList(catList);
                        categoryList.add(myItem);
                        myItemAdapter.notifyDataSetChanged();
                        catId = catJArray.getJSONObject(0).getString("catId");
                        text_left_label.setText(catJArray.getJSONObject(0).getString("catName"));
                        getsubCategories();
                    }else{
                        showProgressBar(false);
                    }

                }else {
                    DialogAndToast.showDialog(response.getString("message"),ShoppursProductActivity.this);
                    showProgressBar(false);
                }
            }if(apiName.equals("productfromshop")){
                loading = false;
                if(response.getString("status").equals("true")||response.getString("status").equals(true)) {
                    JSONObject dataObject;
                    if (!response.getString("result").equals("null")) {
                        myItemAdapter.notifyDataSetChanged();
                        categoriesAdapter.notifyDataSetChanged();
                        final JSONArray productJArray = response.getJSONArray("result");
                        if (productJArray.length() > 0 && selectdSubCatId.equals(response.getJSONArray("result")
                                .getJSONObject(0).getString("prodSubCatId"))){
                            for (int i = 0; i < productJArray.length(); i++) {
                                MyProduct myProduct = new MyProduct();
                                myProduct.setShopCode(shopCode);
                                myProduct.setId(productJArray.getJSONObject(i).getString("prodCode"));
                                myProduct.setProdId(productJArray.getJSONObject(i).getInt("prodId"));
                                myProduct.setCatId(productJArray.getJSONObject(i).getString("prodCatId"));
                                myProduct.setSubCatId(productJArray.getJSONObject(i).getString("prodSubCatId"));
                                myProduct.setName(productJArray.getJSONObject(i).getString("prodName"));
                                myProduct.setQoh(productJArray.getJSONObject(i).getInt("prodQoh"));
                                Log.d("Qoh ", myProduct.getQoh() + "");
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

                               /* HttpURLConnection connection = null;
                                try {
                                    connection = (HttpURLConnection) new URL(myProduct.getProdImage1()).openConnection();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                Long dateTime = connection.getLastModified();
                                connection.disconnect();*/

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

                                if (!TextUtils.isEmpty(productJArray.getJSONObject(i).getString("frequency")) && !productJArray.getJSONObject(i).getString("frequency").equals("null")) {
                                    Log.d("Frequency  ", "Available");
                                    ProductFrequency frequency = new ProductFrequency();
                                    frequency.setName(productJArray.getJSONObject(i).getString("frequency"));
                                    frequency.setQyantity(productJArray.getJSONObject(i).getString("frequencyQty"));
                                    frequency.setNoOfDays(productJArray.getJSONObject(i).getString("frequencyValue"));
                                    frequency.setNextOrderDate(productJArray.getJSONObject(i).getString("nextOrderDate"));
                                    frequency.setEndDate(productJArray.getJSONObject(i).getString("frequencyEndDate"));
                                    frequency.setStartDate(productJArray.getJSONObject(i).getString("frequencyStartDate"));
                                    frequency.setStatus(productJArray.getJSONObject(i).getString("freqStatus"));
                                    myProduct.setFrequency(frequency);
                                }

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
                                        productSize.setId(tempObject.getString("id"));
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
                                            if (k == 0)
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
                            if (myProductList.size() > 0) {
                                if (productJArray.length() > 0) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            shopProductAdapter.notifyItemRangeInserted(offset, myProductList.size());
                                        }
                                    });
                                } else {
                                    Log.d(TAG, "NO ITEMS FOUND");
                                }

                                if (productJArray.length() != limit) {
                                    isScroll = false;
                                }
                            }
                        }
                    }else {
                        showNoData(true);
                    }

                }else {
                    DialogAndToast.showDialog(response.getString("message"),ShoppursProductActivity.this);
                }
            }else if(apiName.equals("get_subcategories")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)) {
                    if (response.getString("result").equals("null")) {
                        view_top_sub_cat.setVisibility(View.GONE);
                        seperator_1.setVisibility(View.GONE);
                        showProgressBar(false);
                        showNoData(true);
                    }else {
                    JSONArray subcatJArray = response.getJSONArray("result");
                    for (int j = 0; j < subcatJArray.length(); j++) {
                        SubCategory subCategory = new SubCategory();
                        subCategory.setSubcatid(subcatJArray.getJSONObject(j).getString("subCatId"));
                        subCategory.setId(subcatJArray.getJSONObject(j).getString("catId"));
                        subCategory.setName(subcatJArray.getJSONObject(j).getString("subCatName"));
                        subCategoryList.add(subCategory);
                    }

                    if (subCategoryList.size() > 0) {
                        view_top_sub_cat.setVisibility(View.VISIBLE);
                        seperator_1.setVisibility(View.VISIBLE);
                        if (!TextUtils.isEmpty(selectdSubCatId)) {
                            selectdSubCatPosition = 0;
                            for (SubCategory subCategory : subCategoryList) {
                                if (subCategory.getSubcatid().equals(selectdSubCatId)) {
                                    subCategory.setSelected(true);
                                    selectdSubCatPosition = subCategoryList.indexOf(subCategory);
                                    break;
                                }
                            }
                        } else {
                            subCategoryList.get(0).setSelected(true);
                            selectdSubCatId = subCategoryList.get(0).getSubcatid();
                        }

                        // categoriesAdapter.notifyDataSetChanged();
                        Log.d("selectdSubCatPosition", "" + selectdSubCatPosition);
                        recyclerViewCategory.scrollToPosition(selectdSubCatPosition);
                        getProducts(selectdSubCatId, "onSubCategorySelected");
                    }
                }
                }else {
                    DialogAndToast.showToast(response.getString("message"),ShoppursProductActivity.this);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            DialogAndToast.showToast(getResources().getString(R.string.json_parser_error)+e.toString(),ShoppursProductActivity.this);
        }
    }

    private void showNoData(boolean show){
        if(show){
           // recyclerView.setVisibility(View.GONE);
            textViewError.setVisibility(View.VISIBLE);
        }else{
            //recyclerView.setVisibility(View.VISIBLE);
            textViewError.setVisibility(View.GONE);
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
        isVisible = true;
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
                    startActivity(new Intent(ShoppursProductActivity.this, CartActivity.class));
                }
            });

            totalPrice = dbHelper.getTotalPriceCart("normal") - (dbHelper.getTotalShopCouponDiscount("normal")+dbHelper.getTotalShoppursCouponDiscount("normal"));

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

            cartItemPrice.setText("Amount "+ Utility.numberFormat(totalPrice));
            cartItemCount.setText("Item "+String.valueOf(dbHelper.getCartCount()));
        }else rlfooterviewcart.setVisibility(View.GONE);

    }


    public void showOfferDescription(MyProduct item){
        OfferDescriptionFragment offerDescriptionFragment = new OfferDescriptionFragment();
        offerDescriptionFragment.setProduct(item);
        offerDescriptionFragment.setColorTheme(colorTheme);
        offerDescriptionFragment.show(getSupportFragmentManager(), "Offer Description Bottom Sheet");
    }

    public void showFrequencyBottomShet(MyProduct item, int position){
        FrequencyFragment frequencyFragment = new FrequencyFragment();
        if(item.getFrequency()!=null)
            frequencyFragment.setProduct(item, position, "edit");
        else frequencyFragment.setProduct(item, position, "add");
        frequencyFragment.setColorTheme(colorTheme);
        frequencyFragment.show(getSupportFragmentManager(), "Product ProductFrequency Bottom Sheet");
    }

    public void showLargeImageDialog(MyProduct product, View view){
        showImageDialog(product.getProdImage1(),view);
    }

    public void showProductDetails(MyProduct product){
        Intent intent = new Intent(this,ProductDetailActivity.class);
        intent.putExtra("MyProduct",product);
        intent.putExtra("shopDeliveryModel",shopDeliveryModel);
        startActivity(intent);
    }

    @Override
    public void updateUi(MyProduct product, int position) {
        Log.d("return position ", position+"");
        Log.d("return product ", product.getId()+"");
        myProductList.set(position, product);
        shopProductAdapter.notifyItemChanged(position);
        shopProductAdapter.notifyDataSetChanged();
        updateCartCount();
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

    @Override
    protected void onPause() {
        super.onPause();
        isVisible = false;
    }

    public void scanBarCode(){
        Intent intent = new Intent(this,ScannarActivity.class);
        intent.putExtra("flag","scan");
        intent.putExtra("type","scanProducts");
        intent.putExtra("shopCode",shopCode);
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
        } else if (requestCode == 102) {
            myProductList.get(position).setFrequency(null);
            //myProductList.get(getIntent().getIntExtra("position", 0)).setFrequency(null);
        }
    }

    public void selectCategory(Category category){
       // DialogAndToast.showDialog("Category Selected "+category.getName(), this);
        text_left_label.setText(category.getName());
        selectdSubCatId = "";
        catId = category.getId();
        showProgressBar(true);
        getsubCategories();
    }

}
