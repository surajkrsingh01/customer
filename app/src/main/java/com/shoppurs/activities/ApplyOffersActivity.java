package com.shoppurs.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.android.volley.Request;
import com.google.gson.JsonArray;
import com.shoppurs.R;
import com.shoppurs.adapters.ApplyOfferAdapter;
import com.shoppurs.fragments.OfferDescriptionFragment;
import com.shoppurs.models.Barcode;
import com.shoppurs.models.MyProduct;
import com.shoppurs.models.ProductColor;
import com.shoppurs.models.ProductComboDetails;
import com.shoppurs.models.ProductComboOffer;
import com.shoppurs.models.ProductDiscountOffer;
import com.shoppurs.models.ProductPriceDetails;
import com.shoppurs.models.ProductPriceOffer;
import com.shoppurs.models.ProductSize;
import com.shoppurs.models.ProductUnit;
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

public class ApplyOffersActivity extends NetworkBaseActivity {

    private List<Object> itemList;
    List<ProductComboOffer> comboOffer;
    private Map<Integer, MyProduct> comboProductMap;
    private RecyclerView recyclerView;
    private ApplyOfferAdapter myItemAdapter;
    private TextView textViewError, text_left_label, text_right_label;

    private RelativeLayout rlOfferDesc;
    private String flag;
    private int position;
    private int counter;
    private String shopCode;
    private ProgressBar progressBar;
    private RelativeLayout rlfooterviewcart;
    private TextView cartItemCount,cartItemPrice, viewCart;
    private ShopDeliveryModel shopDeliveryModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_offers);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();
    }

    private void init() {
        rlfooterviewcart = findViewById(R.id.rlfooterviewcart);
        cartItemCount = findViewById(R.id.itemCount);
        cartItemPrice = findViewById(R.id.itemPrice);
        viewCart = findViewById(R.id.viewCart);
        text_left_label = findViewById(R.id.text_left_label);
        text_right_label = findViewById(R.id.text_right_label);
        text_left_label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ApplyOffersActivity.this, CartShopListActivity.class));
                finish();
            }
        });

        flag = getIntent().getStringExtra("flag");
        shopCode = getIntent().getStringExtra("shopCode");
        shopDeliveryModel = new ShopDeliveryModel();
        shopDeliveryModel = (ShopDeliveryModel) getIntent().getSerializableExtra("shopDeliveryModel");
        rlOfferDesc = findViewById(R.id.rl_offer_desc);
        itemList = new ArrayList<>();
        progressBar = findViewById(R.id.progress_bar);
        textViewError = findViewById(R.id.text_error);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        myItemAdapter = new ApplyOfferAdapter(this, itemList);
        myItemAdapter.setShopCode(shopCode);
        myItemAdapter.setColorTheme(colorTheme);
        recyclerView.setAdapter(myItemAdapter);
        getActiveOfferList();
    }

    private void getActiveOfferList(){
        Map<String,String> params=new HashMap<>();
        params.put("limit", "10");
        params.put("offset", "0");
        params.put("dbName",sharedPreferences.getString(Constants.SHOP_DBNAME,""));
        Log.d(TAG, params.toString());
        String url=getResources().getString(R.string.url)+"/products/ret_productslist_with_offers";
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST, url,new JSONObject(params),"activeOfferList");
    }

    private void setComboOfferList(){
        Log.i(TAG,"combo offer  "+comboOffer.size());
        MyProduct myProductItem = null;

        String comboName = "";
        String comboIds = "";

        float totAmt = 0f;
        float totMrp = 0f;
        float totSgst =0f;
        float totCgst = 0f;
        MyProduct comboProductItem;
        int comboId = 1;
        for(ProductComboOffer productComboOffer : comboOffer){
            myProductItem = new MyProduct();
            for(ProductComboDetails productComboDetails : productComboOffer.getProductComboOfferDetails()){
                Log.d("comboProductMap Size ", ""+comboProductMap);
                          comboProductItem = comboProductMap.get(productComboDetails.getPcodProdId());
                totAmt = totAmt + (productComboDetails.getPcodPrice() * productComboDetails.getPcodProdQty());
                totMrp = totMrp + (comboProductItem.getMrp() * productComboDetails.getPcodProdQty());

                float rate = ((productComboDetails.getPcodPrice() * (comboProductItem.getProdCgst()+comboProductItem.getProdSgst()))/(100 +
                        (comboProductItem.getProdCgst()+comboProductItem.getProdSgst())));

                totCgst = totCgst + ((rate/2) * productComboDetails.getPcodProdQty());
                totSgst = totSgst + ((rate/2) * productComboDetails.getPcodProdQty());

                if(TextUtils.isEmpty(comboName)){
                    comboName = comboProductItem.getName();
                    comboIds = ""+comboProductItem.getId();
                }else{
                    comboName = comboName +"+"+comboProductItem.getName();
                    comboIds = comboIds +"-"+comboProductItem.getId();
                }
                Log.i(TAG,"product Id "+comboIds);
            }
            Log.i(TAG,"cgst "+totCgst);
            Log.i(TAG,"sgst "+totSgst);
            Log.i(TAG,"totAmt "+totAmt);
            Log.i(TAG,"totMrp "+totMrp);
            myProductItem.setId("-"+comboId);
            myProductItem.setOfferType("ComboOffer");
            myProductItem.setName(comboName);
            myProductItem.setComboProductIds(comboIds);
            myProductItem.setSellingPrice(totAmt);
            // myProductItem.setTotalAmount(totAmt);
            myProductItem.setMrp(totMrp);
            myProductItem.setProdSgst(totSgst);
            myProductItem.setProdCgst(totCgst);
            myProductItem.setIsBarcodeAvailable("N");
            myProductItem.setShopCode(shopCode);
            itemList.add(myProductItem);
            comboId++;
        }
    }

    @Override
    public void onJsonObjectResponse(JSONObject response, String apiName) {
        showProgress(false);
        try {

            if(apiName.equals("activeOfferList")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)) {
                    JSONObject dataObject;
                    if(!response.getString("result").equals("null")){
                        JSONArray productJArray = response.getJSONObject("result").getJSONArray("productList");
                        for (int i = 0; i < productJArray.length(); i++) {
                            MyProduct myProduct = new MyProduct();
                            myProduct.setShopCode(shopCode);
                            myProduct.setId(productJArray.getJSONObject(i).getString("prodId"));
                            myProduct.setCatId(productJArray.getJSONObject(i).getString("prodCatId"));
                            myProduct.setSubCatId(productJArray.getJSONObject(i).getString("prodSubCatId"));
                            myProduct.setName(productJArray.getJSONObject(i).getString("prodName"));
                            myProduct.setQoh(productJArray.getJSONObject(i).getInt("prodQoh"));
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
                                    myProduct.setProductDiscountOffer(productDiscountOffer);

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
                                    //myProduct.setProductPriceOffer(productPriceOffer);


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
                            itemList.add(myProduct);
                        }

                        JSONArray comboJArray = response.getJSONObject("result").getJSONArray("comboOfferList");
                        comboOffer = new ArrayList<>();
                        comboProductMap = new HashMap<>();
                        for(int m =0;m<comboJArray.length();m++){
                            ProductComboOffer productComboOffer = new ProductComboOffer();
                            ProductComboDetails productComboDetails;
                            List<ProductComboDetails> productComboDetailsList = new ArrayList<>();
                            productComboOffer.setId(comboJArray.getJSONObject(m).getInt("id"));
                            productComboOffer.setOfferName(comboJArray.getJSONObject(m).getString("offerName"));
                            productComboOffer.setStatus(comboJArray.getJSONObject(m).getString("status"));
                            JSONArray comboProductDetailsJArray = comboJArray.getJSONObject(m).getJSONArray("productComboOfferDetails");
                            for(int p=0;p<comboProductDetailsJArray.length();p++){
                                productComboDetails = new ProductComboDetails();
                                productComboDetails.setId(comboProductDetailsJArray.getJSONObject(p).getInt("id"));
                                productComboDetails.setPcodPcoId(comboProductDetailsJArray.getJSONObject(p).getInt("pcodPcoId"));
                                productComboDetails.setPcodProdId(comboProductDetailsJArray.getJSONObject(p).getInt("pcodProdId"));
                                productComboDetails.setPcodProdQty(comboProductDetailsJArray.getJSONObject(p).getInt("pcodProdQty"));
                                productComboDetails.setPcodPrice((float) comboProductDetailsJArray.getJSONObject(p).getDouble("pcodPrice"));
                                productComboDetails.setStatus(comboProductDetailsJArray.getJSONObject(p).getString("status"));
                                productComboDetailsList.add(productComboDetails);
                            }
                            productComboOffer.setProductComboOfferDetails(productComboDetailsList);
                            comboOffer.add(productComboOffer);

                            JSONArray comboProductJArray = comboJArray.getJSONObject(m).getJSONArray("productList");
                            for(int n =0;n<comboProductJArray.length();n++){
                                MyProduct myProduct = new MyProduct();
                                myProduct.setId(comboProductJArray.getJSONObject(n).getString("prodId"));
                                myProduct.setCatId(comboProductJArray.getJSONObject(n).getString("prodCatId"));
                                myProduct.setSubCatId(comboProductJArray.getJSONObject(n).getString("prodSubCatId"));
                                myProduct.setName(comboProductJArray.getJSONObject(n).getString("prodName"));
                                myProduct.setQoh(comboProductJArray.getJSONObject(n).getInt("prodQoh"));
                                myProduct.setMrp(Float.parseFloat(comboProductJArray.getJSONObject(n).getString("prodMrp")));
                                myProduct.setSellingPrice(Float.parseFloat(comboProductJArray.getJSONObject(n).getString("prodSp")));
                                myProduct.setCode(comboProductJArray.getJSONObject(n).getString("prodCode"));
                                myProduct.setIsBarcodeAvailable(comboProductJArray.getJSONObject(n).getString("isBarcodeAvailable"));
                                //myProduct.setBarCode(comboProductJArray.getJSONObject(n).getString("prodBarCode"));
                                myProduct.setDesc(comboProductJArray.getJSONObject(n).getString("prodDesc"));
                                myProduct.setLocalImage(R.drawable.thumb_16);
                                myProduct.setProdImage1(comboProductJArray.getJSONObject(n).getString("prodImage1"));
                                myProduct.setProdImage2(comboProductJArray.getJSONObject(n).getString("prodImage2"));
                                myProduct.setProdImage3(comboProductJArray.getJSONObject(n).getString("prodImage3"));
                                myProduct.setProdHsnCode(comboProductJArray.getJSONObject(n).getString("prodHsnCode"));
                                myProduct.setProdMfgDate(comboProductJArray.getJSONObject(n).getString("prodMfgDate"));
                                myProduct.setProdExpiryDate(comboProductJArray.getJSONObject(n).getString("prodExpiryDate"));
                                myProduct.setProdMfgBy(comboProductJArray.getJSONObject(n).getString("prodMfgBy"));
                                myProduct.setProdExpiryDate(comboProductJArray.getJSONObject(n).getString("prodExpiryDate"));
                                myProduct.setOfferId(comboProductJArray.getJSONObject(n).getString("offerId"));
                                myProduct.setProdCgst(Float.parseFloat(comboProductJArray.getJSONObject(n).getString("prodCgst")));
                                myProduct.setProdIgst(Float.parseFloat(comboProductJArray.getJSONObject(n).getString("prodIgst")));
                                myProduct.setProdSgst(Float.parseFloat(comboProductJArray.getJSONObject(n).getString("prodSgst")));
                                myProduct.setProdWarranty(comboProductJArray.getJSONObject(n).getString("prodWarranty"));
                                comboProductMap.put(Integer.parseInt(myProduct.getId()), myProduct);
                            }
                        }
                        if(comboOffer.size()>0)
                            setComboOfferList();
                    }
                    if(itemList.size()>0){
                        myItemAdapter.notifyDataSetChanged();
                    }else {
                         showNoData(true);
                    }

                }else {
                    DialogAndToast.showDialog(response.getString("message"), this);
                }

        }else if(apiName.equals("productDetails")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)){
                    JSONObject jsonObject = response.getJSONObject("result");
                    if(productDetailsType == 1){
                        ((MyProduct)(itemList.get(position))).setQoh(jsonObject.getInt("prodQoh"));
                       // product.setQoh(jsonObject.getInt("prodQoh"));
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
                    DialogAndToast.showToast("Something went wrong, Please try again", ApplyOffersActivity.this);
                }
            }

        }catch (JSONException a){
            DialogAndToast.showToast("Something went wrong, Please try again", ApplyOffersActivity.this);
        }
    }

    private void showProgressBar(boolean show){
        if(show){
            recyclerView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            textViewError.setVisibility(View.GONE);
        }else{
            recyclerView.setVisibility(View.VISIBLE);
            textViewError.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void showNoData(boolean show){
        if(show){
            recyclerView.setVisibility(View.GONE);
            textViewError.setVisibility(View.VISIBLE);
        }

        else {
            textViewError.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private int productDetailsType =1, type;
    private MyProduct myProduct, freeProdut;
    boolean isProductCombo;
    public void updateCart(int type, int position){
        MyProduct myProduct = (MyProduct) itemList.get(position);
        Log.d("clicked Position ", position+"");
        this.position = position;
        this.type = type;

        if(!TextUtils.isEmpty(myProduct.getOfferType()) && myProduct.getOfferType().equals("ComboOffer"))
            isProductCombo = true;
        else isProductCombo =false;
        if(type==2 && !isProductCombo){
            productDetailsType = 1;
            getProductDetails(myProduct.getId());
        }else onProductClicked(type, position);
    }

    private void onProductClicked(int type, int position){
        this.position = position;
        myProduct = (MyProduct) itemList.get(position);
        myProduct.setShopCode(shopCode);

        if(type == 1){
            if(myProduct.getQuantity() > 0){
                if(myProduct.getQuantity() == 1){
                    counter--;
                    //dbHelper.removeProductFromCart(myProduct.getProdBarCode());
                    dbHelper.removeProductFromCart(myProduct.getId(),  myProduct.getShopCode());
                    dbHelper.removePriceProductFromCart(myProduct.getId(),  myProduct.getShopCode());
                    if(myProduct.getProductPriceOffer()!=null){
                        ProductPriceOffer productPriceOffer = myProduct.getProductPriceOffer();
                        for(ProductPriceDetails productPriceDetails : productPriceOffer.getProductPriceDetails()){
                            dbHelper.removePriceProductDetailsFromCart(String.valueOf(productPriceDetails.getId()),  myProduct.getShopCode());
                        }
                    }
                    if(myProduct.getProductDiscountOffer()!=null){
                        ProductDiscountOffer productDiscountOffer = (ProductDiscountOffer)myProduct.getProductDiscountOffer();

                        if(productDiscountOffer.getProdBuyId() != productDiscountOffer.getProdFreeId())
                            dbHelper.removeFreeProductFromCart(productDiscountOffer.getProdFreeId(),  myProduct.getShopCode());
                    }
                    myProduct.setQuantity(0);
                    myProduct.setTotalAmount(0);
                    myItemAdapter.notifyItemChanged(position);
                    myItemAdapter.notifyDataSetChanged();
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
                    dbHelper.updateCartData(myProduct, "normal");
                    myItemAdapter.notifyItemChanged(position);
                    myItemAdapter.notifyDataSetChanged();
                    updateCartCount();
                }
            }

        }else if(type == 2){
            /*if(myProduct.getIsBarcodeAvailable().equals("Y")){
                *//*if(myProduct.getQuantity() == myProduct.getBarcodeList().size()){
                    DialogAndToast.showDialog("There are no more stocks",this);
                }else{*//*
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
                myItemAdapter.notifyItemChanged(position);
                //updateCartCount();
                // }

            }else{*/
                if(myProduct.getQuantity() == myProduct.getQoh() && !isProductCombo){
                    myItemAdapter.notifyDataSetChanged();
                    DialogAndToast.showDialog("There are no more stocks",this);
                }else{
                    int qty = myProduct.getQuantity() + 1;
                    myProduct.setQuantity(qty);
                    if(qty == 1){
                        counter++;
                        myProduct.setFreeProductPosition(counter);
                        dbHelper.addProductToCart(myProduct, "normal");
                        dbHelper.addShopDeliveryDetails(shopDeliveryModel);
                    }
                    float netSellingPrice = getOfferAmount(myProduct,type);
                    Log.i(TAG,"netSellingPrice "+netSellingPrice);
                    float amount = myProduct.getTotalAmount() + netSellingPrice;
                    Log.i(TAG,"productTotal "+myProduct.getTotalAmount());
                    Log.i(TAG,"tot amount "+amount);
                    myProduct.setTotalAmount(amount);
                    if(myProduct.getProductPriceOffer()!=null){
                        myProduct.setSellingPrice(amount/qty);
                    }
                    qty = myProduct.getQuantity();
                    Log.i(TAG,"qty "+qty);

                    dbHelper.updateCartData(myProduct, "normal");
                    myItemAdapter.notifyItemChanged(position);
                    myItemAdapter.notifyDataSetChanged();
                    updateCartCount();
                }
            //}
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
                            dbHelper.removeFreeProductFromCart(productDiscountOffer.getProdFreeId(),  item.getShopCode());
                        }else{
                            dbHelper.updateFreeCartData(productDiscountOffer.getProdFreeId(),item.getOfferItemCounter(),0f,  item.getShopCode());
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
                            dbHelper.addProductToCart(item1, "normal");
                            Log.d("FreeProductPosition ", ""+item.getFreeProductPosition());
                            dbHelper.updateFreePositionCartData(item.getFreeProductPosition(),Integer.parseInt(item.getId()),  item.getShopCode());
                            dbHelper.updateOfferCounterCartData(item.getOfferItemCounter(),Integer.parseInt(item.getId()), item.getShopCode());
                            Log.i(TAG,"Different product added to cart");
                        }else{

                            dbHelper.updateFreeCartData(productDiscountOffer.getProdFreeId(),item.getOfferItemCounter(),0f,  item.getShopCode());
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
        MyProduct myProduct = (MyProduct) itemList.get(position);
        if(type ==2 && myProduct.getProductDiscountOffer()!=null){
            ProductDiscountOffer productDiscountOffer = myProduct.getProductDiscountOffer();
            if(productDiscountOffer.getProdBuyId()!= productDiscountOffer.getProdFreeId()){
                productDetailsType = 2;
                getProductDetails(String.valueOf(productDiscountOffer.getProdFreeId()));
            }else onProductClicked(type, position);
        }else {
            onProductClicked(type,position);
        }
    }

    public void showOfferDescription(MyProduct item){
        OfferDescriptionFragment offerDescriptionFragment = new OfferDescriptionFragment();
        offerDescriptionFragment.setProduct(item);
        offerDescriptionFragment.setColorTheme(colorTheme);
        offerDescriptionFragment.show(getSupportFragmentManager(), "Offer Description Bottom Sheet");
    }

    public void showLargeImageDialog(MyProduct product, View view){
        showImageDialog(product.getProdImage1(), view);
    }

    public void updateCartCount(){
        if(dbHelper.getCartCount()>0){
            rlfooterviewcart.setVisibility(View.VISIBLE);
            viewCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ApplyOffersActivity.this, CartActivity.class));
                }
            });
            float totalPrice = dbHelper.getTotalPriceCart("normal") - (dbHelper.getTotalShopCouponDiscount("normal")+dbHelper.getTotalShoppursCouponDiscount("normal"));
            float deliveryDistance = 0;

            cartItemPrice.setText("Amount "+ Utility.numberFormat(totalPrice));
            cartItemCount.setText("Item "+String.valueOf(dbHelper.getCartCount()));
            //cartItemCount.setText(String.valueOf(dbHelper.getProductQuantity(myProduct.getId(), shopCode)));
        }else rlfooterviewcart.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateCartCount();
    }
}
