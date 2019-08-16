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
import com.shoppurscustomer.models.MyProduct;
import com.shoppurscustomer.utilities.Constants;
import com.shoppurscustomer.utilities.DialogAndToast;
import com.shoppurscustomer.utilities.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionDetailsActivity extends NetworkBaseActivity {

    private TextView textViewStatusHeader,tvStatus,tvTransactionId,tvRrn,tvAmount,tvPaymentMethod;
    private ImageView imageStatusSuccess,imageViewStatusFailure;
    private RelativeLayout rlFooter;
    private TextView tvFooter;
    private boolean isDelivered;
    private List<MyProduct> myShopOrderList;

    private String custCode,orderNumber,paymentStatus;
    private float totalAmount;
    private RelativeLayout relative_footer;

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
        relative_footer = findViewById(R.id.relative_footer_action);
        relative_footer.setBackgroundColor(colorTheme);
        tvFooter.setText("Track Your Order");
        orderNumber = getIntent().getStringExtra("orderNumber");
        totalAmount = getIntent().getFloatExtra("totalAmount", 0);
       // myShopOrderList = (List<MyProduct>) getIntent().getSerializableExtra("shopOrderList");
        //Log.d("shopOrderList ", myShopOrderList.size() +"");
        getorderDetails();

        rlFooter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //openInvoiceActivity();
                Intent intent = new Intent(TransactionDetailsActivity.this, MyOrderActivity.class);
                intent.putExtra("callingActivity", "TransactionDetailsActivity");
                intent.putExtra("orderNumber", orderNumber);
                intent.putExtra("shopOrderList", (Serializable) myShopOrderList);
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
        String[] orderlist = orderNumber.split(",");
        params.put("number", orderlist[0]);
        params.put("dbName",sharedPreferences.getString(Constants.DB_NAME,""));
        params.put("dbUserName",sharedPreferences.getString(Constants.DB_USER_NAME,""));
        params.put("dbPassword",sharedPreferences.getString(Constants.DB_PASSWORD,""));
        String url=getResources().getString(R.string.root_url)+"order/get_order_status_details";
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"orderDetails");
    }


    @Override
    public void onJsonObjectResponse(JSONObject response, String apiName) {
        showProgress(false);
        try {
            // JSONObject jsonObject=response.getJSONObject("response");
            Log.d("response", response.toString());
            if (apiName.equals("orderDetails")) {
                if (response.getBoolean("status")) {
                    JSONArray jsonArray = response.getJSONArray("result");

                    for(int i=0;i<jsonArray.length();i++) {
                        tvTransactionId.setText(jsonArray.getJSONObject(i).getString("transactionId"));
                        tvRrn.setText(jsonArray.getJSONObject(i).getString("orderRefNo"));
                        tvPaymentMethod.setText(jsonArray.getJSONObject(i).getString("paymentMethod"));
                        tvAmount.setText(Utility.numberFormat(totalAmount));
                        paymentStatus = jsonArray.getJSONObject(i).getString("paymentStatus");
                    }

                    if(tvPaymentMethod!=null && tvPaymentMethod.equals("cash")){
                        findViewById(R.id.rlrrn).setVisibility(View.GONE);
                    }

                    if(paymentStatus!=null && paymentStatus.equals("Success") || paymentStatus!=null && paymentStatus.equals("SUCCESS")){
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
