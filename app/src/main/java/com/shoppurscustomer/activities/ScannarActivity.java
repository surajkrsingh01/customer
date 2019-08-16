package com.shoppurscustomer.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.google.gson.JsonObject;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.shoppurscustomer.R;
import com.shoppurscustomer.models.Barcode;
import com.shoppurscustomer.models.MyProduct;
import com.shoppurscustomer.models.MyShop;
import com.shoppurscustomer.models.ProductColor;
import com.shoppurscustomer.models.ProductDiscountOffer;
import com.shoppurscustomer.models.ProductPriceDetails;
import com.shoppurscustomer.models.ProductPriceOffer;
import com.shoppurscustomer.models.ProductSize;
import com.shoppurscustomer.models.ProductUnit;
import com.shoppurscustomer.utilities.Constants;
import com.shoppurscustomer.utilities.DialogAndToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScannarActivity extends NetworkBaseActivity {

    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    private boolean isProduct;
    private String flag,type, shopCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scannar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        flag = intent.getStringExtra("flag");
        type = intent.getStringExtra("type");
        shopCode =intent.getStringExtra("shopCode");

        //Initialize barcode scanner view
        barcodeScannerView = findViewById(R.id.zxing_barcode_scanner);

        //start capture
        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();


    }

    @Override
    public void onResume() {
        super.onResume();
        if(capture != null){
            barcodeScannerView.decodeSingle(new BarcodeCallback() {
                @Override
                public void barcodeResult(BarcodeResult result) {
                    Log.i(TAG,"result "+result.toString());
                    processResult(result.toString());
                }

                @Override
                public void possibleResultPoints(List<ResultPoint> resultPoints) {

                }
            });

            capture.onResume();
        }
    }

    private void processResult(String rawValue){
        try {
            if(type.equals("addProduct") || type.equals("addProductBarcode")){
                DialogAndToast.showDialog("Barcode "+rawValue, this );
                DialogAndToast.showToast("Product is not exist in database.",this);
                finish();
               /* Intent intent = new Intent();
                intent.putExtra("barCode",rawValue);
                setResult(-1,intent);
                finish();*/
            }else if(type.equals("scanProducts")){
                scanProducts(rawValue);
            }else if(type.equals("scanStores")){
                scanStores(rawValue);
            }
            else{
               /*String code = dbHelper.checkBarCodeExist(rawValue);
               if(code.equals("no")){
                   DialogAndToast.showToast("Product is not exist in database.",this);
                   finish();
               }else{
                   Intent intent = new Intent(this,ProductDetailActivity.class);
                   intent.putExtra("id",code);
                   intent.putExtra("subCatName","");
                   intent.putExtra("flag","scan");
                   startActivity(intent);
                   finish();
               }*/
            }
        }catch (Exception e){
            Log.e(TAG,"error "+e.toString());
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(capture != null)
            capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(capture != null)
            capture.onDestroy();
    }

    private void scanStores(String mobile){
        Map<String,String> params=new HashMap<>();
        String url=getResources().getString(R.string.url)+"/shop/shop_details_by_mobile";
        params.put("mobile", mobile);
        params.put("dbName", sharedPreferences.getString(Constants.DB_NAME, ""));
        params.put("dbUserName", sharedPreferences.getString(Constants.DB_USER_NAME, ""));
        params.put("dbPassword", sharedPreferences.getString(Constants.DB_PASSWORD, ""));
        Log.d(TAG, params.toString());
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"scanStores");
    }

    private void scanProducts(String barCode){
        Map<String,String> params=new HashMap<>();
        String url=getResources().getString(R.string.url)+"/products/barcode/ret_products_details";
        params.put("code", barCode);
        params.put("dbName", sharedPreferences.getString(Constants.SHOP_INSIDE_CODE, ""));
        params.put("dbUserName", sharedPreferences.getString(Constants.DB_USER_NAME, ""));
        params.put("dbPassword", sharedPreferences.getString(Constants.DB_PASSWORD, ""));
        Log.d(TAG, params.toString());
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"scanProducts");
    }

    @Override
    public void onJsonObjectResponse(JSONObject response, String apiName) {
        showProgress(false);
        try {
            Log.d("response", response.toString());
            if (apiName.equals("scanStores")) {
                if (response.getString("status").equals("true") || response.getString("status").equals(true)) {
                    //DialogAndToast.showDialog(response.getString("message"), ScannarActivity.this);

                    if(response.getString("result")!=null) {
                        JSONObject jsonObject = response.getJSONObject("result");
                        MyShop myShop = new MyShop();
                        String shop_code = jsonObject.getString("retcode");
                        myShop.setId(shop_code);
                        Log.d("shop_id", myShop.getId());
                        myShop.setName(jsonObject.getString("retshopname"));
                        myShop.setMobile(jsonObject.getString("retmobile"));
                        myShop.setAddress(jsonObject.getString("retaddress"));
                        myShop.setState(jsonObject.getString("retcountry"));
                        myShop.setCity(jsonObject.getString("retcity"));
                        myShop.setShopimage(jsonObject.getString("retphoto"));
                        myShop.setDbname(jsonObject.getString("dbname"));
                        myShop.setDbusername(jsonObject.getString("dbuser"));
                        myShop.setDbpassword(jsonObject.getString("dbpassword"));
                        myShop.setImage(R.drawable.thumb_21);

                        if(!myShop.getId().equals("null")){
                            Intent intent = new Intent(this, ShopProductListActivity.class);
                            intent.putExtra("callingClass","ShopListActivity");
                            intent.putExtra("name",myShop.getName());
                            intent.putExtra("photo",myShop.getShopimage());
                            intent.putExtra("address",myShop.getAddress());
                            intent.putExtra("mobile", myShop.getMobile());
                            intent.putExtra("stateCity",myShop.getState()+", "+myShop.getCity());
                            /*intent.putExtra("catId", catId);
                            intent.putExtra("subcatid",subcatid);
                            intent.putExtra("subcatname",subcatname);*/
                            intent.putExtra("dbname",myShop.getDbname());
                            intent.putExtra("dbuser",myShop.getDbusername());
                            intent.putExtra("dbpassword",myShop.getDbpassword());
                            intent.putExtra("shop_code",myShop.getId());
                            editor.putString(Constants.SHOP_DBNAME,myShop.getDbname());
                            editor.putString(Constants.SHOP_DB_USER_NAME,myShop.getDbusername());
                            editor.putString(Constants.SHOP_DB_PASSWORD,myShop.getDbpassword());
                            editor.putString(Constants.SHOP_INSIDE_CODE,myShop.getId());
                            editor.putString(Constants.SHOP_INSIDE_NAME, myShop.getName());
                            editor.commit();
                            startActivity(intent);
                        }
                    }else {
                        DialogAndToast.showDialog(response.getString("message"), ScannarActivity.this);
                    }
                } else {
                    DialogAndToast.showDialog(response.getString("message"), ScannarActivity.this);
                }
            }else if(apiName.equals("scanProducts")){
                if (response.getString("status").equals("true") || response.getString("status").equals(true)) {
                    DialogAndToast.showDialog(response.getString("message"), ScannarActivity.this);

                    JSONObject dataObject;
                    if(!response.getString("result").equals("null")){
                        JSONObject productJObject = response.getJSONObject("result");

                            MyProduct myProduct = new MyProduct();
                            myProduct.setShopCode(shopCode);
                            myProduct.setId(productJObject.getString("prodId"));
                            myProduct.setCatId(productJObject.getString("prodCatId"));
                            myProduct.setSubCatId(productJObject.getString("prodSubCatId"));
                            myProduct.setName(productJObject.getString("prodName"));
                            myProduct.setQoh(productJObject.getInt("prodQoh"));
                            myProduct.setMrp(Float.parseFloat(productJObject.getString("prodMrp")));
                            myProduct.setSellingPrice(Float.parseFloat(productJObject.getString("prodSp")));
                            myProduct.setCode(productJObject.getString("prodCode"));
                            myProduct.setIsBarcodeAvailable(productJObject.getString("isBarcodeAvailable"));
                            //myProduct.setBarCode(productJObject.getString("prodBarCode"));
                            myProduct.setDesc(productJObject.getString("prodDesc"));
                            myProduct.setLocalImage(R.drawable.thumb_16);
                            myProduct.setProdImage1(productJObject.getString("prodImage1"));
                            myProduct.setProdImage2(productJObject.getString("prodImage2"));
                            myProduct.setProdImage3(productJObject.getString("prodImage3"));
                            myProduct.setProdHsnCode(productJObject.getString("prodHsnCode"));
                            myProduct.setProdMfgDate(productJObject.getString("prodMfgDate"));
                            myProduct.setProdExpiryDate(productJObject.getString("prodExpiryDate"));
                            myProduct.setProdMfgBy(productJObject.getString("prodMfgBy"));
                            myProduct.setProdExpiryDate(productJObject.getString("prodExpiryDate"));
                            myProduct.setOfferId(productJObject.getString("offerId"));
                            myProduct.setProdCgst(Float.parseFloat(productJObject.getString("prodCgst")));
                            myProduct.setProdIgst(Float.parseFloat(productJObject.getString("prodIgst")));
                            myProduct.setProdSgst(Float.parseFloat(productJObject.getString("prodSgst")));
                            myProduct.setProdWarranty(productJObject.getString("prodWarranty"));
                            //myProduct.setSubCatName(subcatname);


                            int innerLen = 0;
                            int len = 0;

                            JSONArray tempArray = null;
                            JSONObject jsonObject = null, tempObject = null;
                            ProductUnit productUnit = null;
                            ProductSize productSize = null;
                            ProductColor productColor = null;

                            if (!productJObject.getString("productUnitList").equals("null")) {
                                tempArray = productJObject.getJSONArray("productUnitList");
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

                            if (!productJObject.getString("productSizeList").equals("null")) {
                                tempArray = productJObject.getJSONArray("productSizeList");
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

                                    if (!productJObject.getString("productSizeList").equals("null")) {
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


                            if (!productJObject.getString("barcodeList").equals("null")) {
                                JSONArray productBarCodeArray = productJObject.getJSONArray("barcodeList");
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

                            if (!productJObject.getString("productDiscountOfferList").equals("null")) {
                                JSONArray freeArray = productJObject.getJSONArray("productDiscountOfferList");
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


                            if (!productJObject.getString("productPriceOffer").equals("null")) {
                                JSONArray priceArray = productJObject.getJSONArray("productPriceOffer");
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
                                        productPriceDetails.setStatus(dataObject.getString("status"));
                                        productPriceOfferDetails.add(productPriceDetails);
                                    }
                                    productPriceOffer.setProductPriceDetails(productPriceOfferDetails);
                                }
                                myProduct.setProductPriceOffer(productPriceOffer);
                            }
                        Intent intent = new Intent(this,ProductDetailActivity.class);
                        intent.putExtra("MyProduct",myProduct);
                        startActivity(intent);
                        finish();
                    }else {
                        DialogAndToast.showDialog(response.getString("message"), ScannarActivity.this);
                    }
                }else{
                    DialogAndToast.showDialog(response.getString("message"), ScannarActivity.this);
                }
                }
        }catch (JSONException e) {
            e.printStackTrace();
            DialogAndToast.showToast(getResources().getString(R.string.json_parser_error)+e.toString(),ScannarActivity.this);
        }
    }

}
