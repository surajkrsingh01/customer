package com.shoppurscustomer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.google.gson.JsonObject;
import com.shoppurscustomer.R;
import com.shoppurscustomer.utilities.Constants;
import com.shoppurscustomer.utilities.DialogAndToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TransactionDetailsActivity extends NetworkBaseActivity {

    private TextView textViewStatusHeader,tvStatus,tvTransactionId,tvRrn,tvAmount,tvPaymentMethod;
    private ImageView imageStatusSuccess,imageViewStatusFailure;
    private RelativeLayout rlFooter;
    private TextView tvFooter;
    private boolean isDelivered;

    private String custCode,orderNumber,paymentStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();
        setStatusLayout(false);
    }

    private void init(){
        textViewStatusHeader = findViewById(R.id.tv_status_header);
        tvStatus = findViewById(R.id.text_status_message);
        tvPaymentMethod = findViewById(R.id.tv_payment_method);
        tvTransactionId = findViewById(R.id.transaction_id);
        tvRrn = findViewById(R.id.tv_rrn);
        tvAmount = findViewById(R.id.tv_amount);
        imageStatusSuccess = findViewById(R.id.image_status_success);
        imageViewStatusFailure = findViewById(R.id.image_status_failure);
        rlFooter = findViewById(R.id.relative_footer_action);
        tvFooter = findViewById(R.id.text_action);
        tvFooter.setText("Track Your Order");
        orderNumber = getIntent().getStringExtra("orderNumber");
        getorderDetails();

        rlFooter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //openInvoiceActivity();
                Intent intent = new Intent(TransactionDetailsActivity.this, MyOrderActivity.class);
                intent.putExtra("callingActivity", "TransactionDetailsActivity");
                intent.putExtra("orderNumber", orderNumber);
                startActivity(intent);
            }
        });
    }

    private void setStatusLayout(boolean status){
        if(status){
            imageStatusSuccess.setVisibility(View.VISIBLE);
            imageViewStatusFailure.setVisibility(View.GONE);
            textViewStatusHeader.setText("Congrats");
            tvStatus.setText("Order has been successfully placed.");
            //placeOrder();

        }else{
            imageStatusSuccess.setVisibility(View.GONE);
            imageViewStatusFailure.setVisibility(View.VISIBLE);
            textViewStatusHeader.setText("Sorry");
            tvStatus.setText("There is some problem occurred.");

        }
    }


    private void getorderDetails(){
        Map<String,String> params=new HashMap<>();
        params.put("number", orderNumber);
        params.put("dbName",sharedPreferences.getString(Constants.DB_NAME,""));
        params.put("dbUserName",sharedPreferences.getString(Constants.DB_USER_NAME,""));
        params.put("dbPassword",sharedPreferences.getString(Constants.DB_PASSWORD,""));
        String url=getResources().getString(R.string.root_url)+"order/get_order_status_details";
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"orderDetails");
    }

    private void openInvoiceActivity(){
        Intent intent = new Intent(TransactionDetailsActivity.this,InvoiceActivity.class);
        intent.putExtra("orderNumber",getIntent().getStringExtra("orderNumber"));
        startActivity(intent);
        finish();
    }

    @Override
    public void onJsonObjectResponse(JSONObject response, String apiName) {
        showProgress(false);
        try {
            // JSONObject jsonObject=response.getJSONObject("response");
            Log.d("response", response.toString());
            if (apiName.equals("orderDetails")) {
                if (response.getBoolean("status")) {
                    JSONObject dataObject = response.getJSONObject("result");
                    tvTransactionId.setText(dataObject.getString("transactionId"));
                    tvRrn.setText(dataObject.getString("orderRefNo"));
                    tvPaymentMethod.setText(dataObject.getString("paymentMethod"));
                    tvAmount.setText(dataObject.getString("amount"));
                    paymentStatus = dataObject.getString("paymentStatus");

                    if(dataObject.getString("paymentMethod").equals("cash")){
                        findViewById(R.id.rlrrn).setVisibility(View.GONE);
                    }

                    if(paymentStatus.equals("Success") || paymentStatus.equals("SUCCESS")){
                        // placeOrder();
                        setStatusLayout(true);
                    }else{
                        setStatusLayout(false);
                    }
                }else{
                    DialogAndToast.showDialog(response.getString("message"),this);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            DialogAndToast.showToast(getResources().getString(R.string.json_parser_error)+e.toString(),TransactionDetailsActivity.this);
        }
    }

}
