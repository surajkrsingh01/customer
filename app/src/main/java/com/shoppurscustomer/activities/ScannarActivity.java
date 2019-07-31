package com.shoppurscustomer.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.shoppurscustomer.R;
import com.shoppurscustomer.models.MyShop;
import com.shoppurscustomer.utilities.Constants;
import com.shoppurscustomer.utilities.DialogAndToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScannarActivity extends NetworkBaseActivity {

    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    private boolean isProduct;
    private String flag,type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scannar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        flag = intent.getStringExtra("flag");
        type = intent.getStringExtra("type");

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
                    // nevigate to product details page
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
