package com.shoppurs.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.scandit.datacapture.barcode.capture.BarcodeCapture;
import com.scandit.datacapture.barcode.capture.BarcodeCaptureListener;
import com.scandit.datacapture.barcode.capture.BarcodeCaptureSession;
import com.scandit.datacapture.barcode.capture.BarcodeCaptureSettings;
import com.scandit.datacapture.barcode.capture.SymbologySettings;
import com.scandit.datacapture.barcode.data.Symbology;
import com.scandit.datacapture.barcode.data.SymbologyDescription;
import com.scandit.datacapture.core.capture.DataCaptureContext;
import com.scandit.datacapture.core.common.feedback.Feedback;
import com.scandit.datacapture.core.data.FrameData;
import com.scandit.datacapture.core.source.Camera;
import com.scandit.datacapture.core.source.CameraSettings;
import com.scandit.datacapture.core.source.FrameSourceState;
import com.scandit.datacapture.core.ui.DataCaptureView;
import com.shoppurs.R;
import com.shoppurs.models.Barcode;
import com.shoppurs.models.MyProduct;
import com.shoppurs.models.MyShop;
import com.shoppurs.models.ProductColor;
import com.shoppurs.models.ProductDiscountOffer;
import com.shoppurs.models.ProductPriceDetails;
import com.shoppurs.models.ProductPriceOffer;
import com.shoppurs.models.ProductSize;
import com.shoppurs.models.ProductUnit;
import com.shoppurs.utilities.Constants;
import com.shoppurs.utilities.DialogAndToast;
import com.shoppurs.utilities.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class ScannarActivity extends NetworkBaseActivity implements BarcodeCaptureListener {

    //private CaptureManager capture;
    //private DecoratedBarcodeView barcodeScannerView;
    private BarcodeCapture barcodeCapture;
    private DataCaptureContext dataCaptureContext;
    private Camera camera;
    private boolean isProduct;
    private String flag,type, shopCode, qrCode;
    private TextView text_header;
    private ImageView imgBack;

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
        text_header = findViewById(R.id.text_desc);
        imgBack = findViewById(R.id.text_header);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if(type.equals("scanStores")){
            text_header.setText("Scan QR code of store to access their products");
        }else if(type.equals("scanProducts")){
            text_header.setText("Scan QR code of products");
        }



        dataCaptureContext = DataCaptureContext.forLicenseKey(getResources().getString(R.string.scandit_license_key));
        CameraSettings cameraSettings = BarcodeCapture.createRecommendedCameraSettings();

// Depending on the use case further camera settings adjustments can be made here.

        camera = Camera.getDefaultCamera();

        if (camera != null) {
            camera.applySettings(cameraSettings);
        }

        if (camera != null) {
            camera.switchToDesiredState(FrameSourceState.ON);
        }

        dataCaptureContext.setFrameSource(camera);

        // The barcode capturing process is configured through barcode capture settings
        // which are then applied to the barcode capture instance that manages barcode recognition.
        BarcodeCaptureSettings barcodeCaptureSettings = new BarcodeCaptureSettings();

        // The settings instance initially has all types of barcodes (symbologies) disabled.
        // For the purpose of this sample we enable a very generous set of symbologies.
        // In your own app ensure that you only enable the symbologies that your app requires as
        // every additional enabled symbology has an impact on processing times.
        HashSet<Symbology> symbologies = new HashSet<>();
        symbologies.add(Symbology.EAN13_UPCA);
        symbologies.add(Symbology.EAN8);
        symbologies.add(Symbology.UPCE);
        symbologies.add(Symbology.QR);
        symbologies.add(Symbology.MICRO_QR);
        symbologies.add(Symbology.DATA_MATRIX);
        symbologies.add(Symbology.CODE39);
        symbologies.add(Symbology.CODE128);
        barcodeCaptureSettings.enableSymbologies(symbologies);

        // Some linear/1d barcode symbologies allow you to encode variable-length data.
        // By default, the Scandit Data Capture SDK only scans barcodes in a certain length range.
        // If your application requires scanning of one of these symbologies, and the length is
        // falling outside the default range, you may need to adjust the "active symbol counts"
        // for this symbology. This is shown in the following few lines of code for one of the
        // variable-length symbologies.
        SymbologySettings symbologySettings =
                barcodeCaptureSettings.getSymbologySettings(Symbology.CODE39);
        SymbologySettings symbologySettingsqR =
                barcodeCaptureSettings.getSymbologySettings(Symbology.QR);


        HashSet<Short> activeSymbolCounts = new HashSet<>(
                Arrays.asList(new Short[] { 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20 }));

        symbologySettings.setActiveSymbolCounts(activeSymbolCounts);
        symbologySettingsqR.setActiveSymbolCounts(activeSymbolCounts);

        symbologySettings.setColorInvertedEnabled(true);
        symbologySettingsqR.setColorInvertedEnabled(true);

        barcodeCapture = BarcodeCapture.forDataCaptureContext(dataCaptureContext, barcodeCaptureSettings);
        barcodeCapture.addListener(this);

        barcodeCapture.getFeedback().setSuccess(new Feedback(null,null));

        DataCaptureView dataCaptureView = DataCaptureView.newInstance(this, dataCaptureContext);
        // Add a barcode capture overlay to the data capture view to render the location of captured
        // barcodes on top of the video preview.
        // This is optional, but recommended for better visual feedback.
        //BarcodeCaptureOverlay overlay = BarcodeCaptureOverlay.newInstance(barcodeCapture, dataCaptureView);
        //  overlay.setViewfinder(new RectangularViewfinder());

        ((ViewGroup) findViewById(R.id.scanner_container)).addView(dataCaptureView);


    }

    @Override
    public void onResume() {
        super.onResume();
        if(Utility.verifyCameraPermissions(this)){
            barcodeCapture.setEnabled(true);
            camera.switchToDesiredState(FrameSourceState.ON, null);
        }

        // Check for camera permission and request it, if it hasn't yet been granted.
        // Once we have the permission the onCameraPermissionGranted() method will be called.
        // requestCameraPermission();
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
        pauseFrameSource();
    }

    @Override
    protected void onDestroy() {
        barcodeCapture.removeListener(this);
        dataCaptureContext.removeMode(barcodeCapture);
        super.onDestroy();
    }

    private void pauseFrameSource() {
        // Switch camera off to stop streaming frames.
        // The camera is stopped asynchronously and will take some time to completely turn off.
        // Until it is completely stopped, it is still possible to receive further results, hence
        // it's a good idea to first disable barcode capture as well.
        barcodeCapture.setEnabled(false);
        camera.switchToDesiredState(FrameSourceState.OFF, null);
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
                        showMyDialog(response.getString("message"));
                    }
                } else {
                    showMyDialog(response.getString("message"));
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
                                productSize.setId(tempObject.getString("id"));
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
                        showMyDialog(response.getString("message"));
                    }
                }else{
                    showMyDialog(response.getString("message"));
                }
            }
        }catch (JSONException e) {
            e.printStackTrace();
            DialogAndToast.showToast(getResources().getString(R.string.json_parser_error)+e.toString(),ScannarActivity.this);
        }
    }

    public void showMyDialog(String msg) {
        //  errorNoInternet.setText("Oops... No internet");
        //  errorNoInternet.setVisibility(View.VISIBLE);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set title
        // alertDialogBuilder.setTitle("Oops...No internet");
        // set dialog message
        alertDialogBuilder
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        onDialogPositiveClicked();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    @Override
    public void onDialogPositiveClicked(){
        if(Utility.verifyCameraPermissions(this)){
            barcodeCapture.setEnabled(true);
            camera.switchToDesiredState(FrameSourceState.ON, null);
        }
    }


    @Override
    public void onBarcodeScanned(BarcodeCapture barcodeCapture, BarcodeCaptureSession barcodeCaptureSession, FrameData frameData) {
        if (barcodeCaptureSession.getNewlyRecognizedBarcodes().isEmpty()) return;

        // List<Barcode> recognizedBarcodes = barcodeCaptureSession.getNewlyRecognizedBarcodes();
        final com.scandit.datacapture.barcode.data.Barcode barcode = barcodeCaptureSession.getNewlyRecognizedBarcodes().get(0);
        final String symbology = SymbologyDescription.create(barcode.getSymbology()).getReadableName();
        Log.i(TAG,"barcode "+barcode.getData()+ " (" + symbology + ")");
        qrCode = barcode.getData();
        barcodeCapture.setEnabled(false);



        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                processResult(qrCode);
                //showMyDialog("barcode "+barcode.getData()+ " (" + symbology + ")");
            }
        });
    }

    @Override
    public void onObservationStarted(BarcodeCapture barcodeCapture) {

    }

    @Override
    public void onObservationStopped(BarcodeCapture barcodeCapture) {

    }

    @Override
    public void onSessionUpdated(BarcodeCapture barcodeCapture, BarcodeCaptureSession barcodeCaptureSession, FrameData frameData) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case Utility.MY_PERMISSIONS_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onDialogPositiveClicked();
                } else {

                }
                break;
        }

    }
}
