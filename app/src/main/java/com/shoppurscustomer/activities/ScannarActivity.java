package com.shoppurscustomer.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.shoppurscustomer.R;
import com.shoppurscustomer.utilities.DialogAndToast;

import java.util.List;

public class ScannarActivity extends BaseActivity {

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
            }else{
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

}
