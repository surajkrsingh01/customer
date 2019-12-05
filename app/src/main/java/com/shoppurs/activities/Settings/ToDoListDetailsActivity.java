package com.shoppurs.activities.Settings;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.shoppurs.R;
import com.shoppurs.activities.HandleCartActivity;
import com.shoppurs.activities.ManageToDoListProducts;
import com.shoppurs.activities.ProductDetailActivity;
import com.shoppurs.activities.ShopProductListActivity;
import com.shoppurs.adapters.ToDoProductListAdapter;
import com.shoppurs.database.DbHelper;
import com.shoppurs.fragments.BottomSearchProductsFragment;
import com.shoppurs.fragments.OfferDescriptionFragment;
import com.shoppurs.interfaces.MyItemClickListener;
import com.shoppurs.models.Barcode;
import com.shoppurs.models.MyProduct;
import com.shoppurs.models.MyToDo;
import com.shoppurs.models.ProductColor;
import com.shoppurs.models.ProductDiscountOffer;
import com.shoppurs.models.ProductFrequency;
import com.shoppurs.models.ProductPriceDetails;
import com.shoppurs.models.ProductPriceOffer;
import com.shoppurs.models.ProductSize;
import com.shoppurs.models.ProductUnit;
import com.shoppurs.models.ShopDeliveryModel;
import com.shoppurs.models.SubCategory;
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

public class ToDoListDetailsActivity extends ManageToDoListProducts implements MyItemClickListener{

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private TextView tvNoData;
    private ProgressBar progressBar;
    private ToDoProductListAdapter toDoProductListAdapter;
    private String shopCode, shopName,shopAddress, shopdbname,dbuser,dbpassword;
    private List<MyProduct> myProductList, myTempProductList;
    private ImageView searchView;
    private Typeface typeface;
    private int position, type;
    private ShopDeliveryModel shopDeliveryModel;
    private String custCode, custdbname;
    private RelativeLayout relative_footer_action;
    private MyToDo myToDo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_product_list);
        typeface = Utility.getSimpleLineIconsFont(this);
        dbHelper = new DbHelper(this);
        shopCode = getIntent().getStringExtra("shop_code");
        shopName = getIntent().getStringExtra("name");
        shopAddress = getIntent().getStringExtra("address");
        shopdbname = getIntent().getStringExtra("dbname");
        dbuser = getIntent().getStringExtra("dbuser");
        dbpassword = getIntent().getStringExtra("dbpassword");
        shopDeliveryModel = new ShopDeliveryModel();
        shopDeliveryModel = (ShopDeliveryModel) getIntent().getSerializableExtra("shopDeliveryModel");
        myToDo = (MyToDo) getIntent().getSerializableExtra("todo_item");
        custCode = sharedPreferences.getString(Constants.USER_ID,"");
        custdbname = sharedPreferences.getString(Constants.DB_NAME, "");
        initViews();
    }

    private void initViews(){
        searchView = findViewById(R.id.image_search);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSearchProductsFragment bottomSearchProductsFragment = new BottomSearchProductsFragment();
                bottomSearchProductsFragment.initParams(sharedPreferences, isDarkTheme, typeface, shopCode, "","", "ToDo List");
                bottomSearchProductsFragment.setMyItemClickListener(ToDoListDetailsActivity.this);
                bottomSearchProductsFragment.show(getSupportFragmentManager(), bottomSearchProductsFragment.getTag());
            }
        });
        TextView text_left_label = findViewById(R.id.text_left_label);
        TextView text_right_label = findViewById(R.id.text_right_label);
        text_right_label.setText(myToDo.getName());
        TextView text_shop_name = findViewById(R.id.text_shop_name);
        text_shop_name.setText(shopName);
        text_left_label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        swipeRefreshLayout =findViewById(R.id.swipe_refresh);
        recyclerView = findViewById(R.id.recycler_viewProduct);
        tvNoData = findViewById(R.id.tvNoData);
        progressBar=findViewById(R.id.progress_bar);
        relative_footer_action = findViewById(R.id.relative_footer_action);
        relative_footer_action.setBackgroundColor(colorTheme);
        TextView tvAction = findViewById(R.id.text_action);
        tvAction.setTextColor(Color.WHITE);
        tvAction.setText("Add ToDo List to Cart");
        relative_footer_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getProductDetails();
            }
        });

        myProductList = new ArrayList<>();
        myTempProductList = new ArrayList<>();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        toDoProductListAdapter = new ToDoProductListAdapter(this, myProductList, shopCode, isDarkTheme, typeface);
        toDoProductListAdapter.setItemClickListener(this);
        recyclerView.setAdapter(toDoProductListAdapter);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getToList();
            }
        });
        getToList();
    }

    private void getToList(){
        myProductList.clear();
        if(swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
        myProductList.addAll(dbHelper.getToDoListProducts(shopCode, myToDo.getId()));
        Log.d("List Size ", ""+myProductList.size());
        if(myProductList.size()==0){
            showNoData(true);
        }else {
            showNoData(false);
            toDoProductListAdapter.notifyDataSetChanged();
        }
    }

    private void showNoData(boolean show){
        if(show){
            recyclerView.setVisibility(View.GONE);
            tvNoData.setVisibility(View.VISIBLE);
            relative_footer_action.setVisibility(View.GONE);
        }else{
            recyclerView.setVisibility(View.VISIBLE);
            tvNoData.setVisibility(View.GONE);
            relative_footer_action.setVisibility(View.VISIBLE);
        }
    }

    private void showProgressBar(boolean show, String type){
        if(show){
            if(type.equals("RefreshList"))
                recyclerView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            tvNoData.setVisibility(View.GONE);
        }else{
            recyclerView.setVisibility(View.VISIBLE);
            tvNoData.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
    }


    public void showLargeImageDialog(MyProduct product, View view){
        showImageDialog(product.getProdImage1(),view);
    }

    public void showProductDetails(MyProduct product){
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra("MyProduct",product);
        startActivity(intent);
    }

    public void showOfferDescription(MyProduct item){
        OfferDescriptionFragment offerDescriptionFragment = new OfferDescriptionFragment();
        offerDescriptionFragment.setProduct(item);
        offerDescriptionFragment.setColorTheme(colorTheme);
        offerDescriptionFragment.show(getSupportFragmentManager(), "Offer Description Bottom Sheet");
    }

    public void updateCart(int type, int position){
        Log.d("clicked Position ", position+"");
        this.position = position;
        this.type = type;
        //updateCart(type, position, shopCode, myProductList.get(position), shopDeliveryModel, this);
    }

    @Override
    public void onItemClicked(int pos) {

    }

    @Override
    public void onProductSearch(MyProduct myProduct) {
       // DialogAndToast.showToast(myProduct.getName() + " Selected ", this);
        myProductList.add(myProduct);
        showNoData(false);
        toDoProductListAdapter.notifyItemInserted(myProductList.size()-1);
        dbHelper.addProductInTODOList(myProduct, myToDo.getId());
    }

    @Override
    public void onItemClicked(int pos, String type) {
        MyProduct product = myProductList.get(pos);
        //DialogAndToast.showDialog(product.getName() +" "+type, this);

        if(type.equals("showOffer"))
            showOfferDescription(product);
        else if(type.equals("showDetails"))
            showProductDetails(product);
        else if(type.equals("remove")) {
            dbHelper.removeProductFromTODoList(product.getId(), shopCode);
            myProductList.remove(pos);
            toDoProductListAdapter.notifyItemRemoved(pos);
        }
    }

    public void showProgress(boolean show){
        if(show){
            progressDialog.show();
        }else{
            progressDialog.dismiss();
        }
    }

    public void getProductDetails(){
        myTempProductList.clear();
        Map<String,String> params=new HashMap<>();
        String prodCodes ="";
        int counter=0;
        for(MyProduct product: myProductList){
            if(counter<myProductList.size()) {
                if(counter==0)
                    prodCodes = product.getCode();
                else
                prodCodes = prodCodes + "," + product.getCode();
            }
            counter++;
        }
        params.put("code", prodCodes);
        //params.put("shopCode", shopCode);
        params.put("dbName",shopdbname);
        String url=getResources().getString(R.string.url)+"/multiproducts/ret_products_details";
        Log.d(TAG, params.toString());
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST, url,new JSONObject(params),"getProductDetails");
    }

    @Override
    public void onJsonObjectResponse(JSONObject response, String apiName) {
        showProgress(false);
        try {
            // JSONObject jsonObject=response.getJSONObject("response");
            Log.d("response", response.toString());
            if(apiName.equals("getProductDetails")){
                loading = false;
                if(response.getString("status").equals("true")||response.getString("status").equals(true)) {
                    JSONObject dataObject;
                    if (!response.getString("result").equals("null")) {
                        JSONArray productJArray = response.getJSONArray("result");
                        Log.d("productJArray ", productJArray.length()+"");
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
                            myTempProductList.add(myProduct);
                        }
                        if (myTempProductList.size() > 0) {
                            DialogAndToast.showToast("please wait..", ToDoListDetailsActivity.this);
                            init(2, shopCode, myTempProductList, shopDeliveryModel, ToDoListDetailsActivity.this);
                        }
                    }else {
                        showNoData(true);
                    }

                }else {
                    DialogAndToast.showDialog(response.getString("message"), ToDoListDetailsActivity.this);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            DialogAndToast.showToast(getResources().getString(R.string.json_parser_error)+e.toString(),ToDoListDetailsActivity.this);
        }
    }

}
