package com.shoppurs.activities.Settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.shoppurs.R;
import com.shoppurs.activities.InstantPayActivity;
import com.shoppurs.activities.NetworkBaseActivity;
import com.shoppurs.activities.TransactionDetailsActivity;
import com.shoppurs.activities.payment.ccavenue.activities.CCAvenueWebViewActivity;
import com.shoppurs.activities.payment.ccavenue.utility.AvenuesParams;
import com.shoppurs.utilities.Constants;
import com.shoppurs.utilities.DialogAndToast;
import com.shoppurs.utilities.Utility;

import org.json.JSONException;
import org.json.JSONObject;

public class PayKhataActivity extends NetworkBaseActivity {

    private TextView tvPayCash,tvOnline;
    private EditText etAmount;
    private String khataNo;
    private int totalDueAmount;
    private int amount;
    private RelativeLayout rlFooter;
    private LinearLayout linearPayLayout;
    private String paymentMethod;
    private JSONObject dataObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_khata_payment);

        init();
        initFooterAction();
    }

    private void init(){
        etAmount = findViewById(R.id.edit_amount);
        tvPayCash = findViewById(R.id.tvPayCash);
        tvOnline = findViewById(R.id.tvOnline);
        linearPayLayout = findViewById(R.id.linearPayLayout);
        rlFooter = findViewById(R.id.relative_footer_action);

        khataNo = getIntent().getStringExtra("khataNo");
        totalDueAmount = getIntent().getIntExtra("totalDue",0);

        rlFooter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearPayLayout.setVisibility(View.VISIBLE);
            }
        });

        tvPayCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentMethod = "Cash";
                khataPayment();
            }
        });

        tvOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentMethod = "Online";
                khataPayment();
            }
        });
    }

    private void khataPayment(){
        try{
            amount = Integer.parseInt(etAmount.getText().toString());
        }catch (NumberFormatException nfe){
            nfe.printStackTrace();
        }

        if(amount == 0){
            DialogAndToast.showDialog("Please enter amount",this);
            return;
        }else if(amount > totalDueAmount){
            DialogAndToast.showDialog("Please enter valid amount. Entered amount is greater than total due amount - Rs "+totalDueAmount,this);
            return;
        }

        if(paymentMethod.equals("Cash")){
            addTransactions();
        }else{
            Intent intent = new Intent(PayKhataActivity.this, CCAvenueWebViewActivity.class);
            //  String ta = editTextAmount.getText().toString().split(" ")[1];
            //  ta = ta.replaceAll(",", "");
            intent.putExtra(AvenuesParams.AMOUNT, String.valueOf(amount)+".00");
            //intent.putExtra(AvenuesParams.AMOUNT, amount);//String.format("%.02f",amount));
            intent.putExtra(AvenuesParams.CURRENCY, "INR");
            intent.putExtra("remarks", "Khata Payment");
            intent.putExtra("orderNumber",Utility.getTimeStamp());
            intent.putExtra("flag","KhataTransaction");
            intent.putExtra("custCode", sharedPreferences.getString(Constants.USER_ID,""));
            intent.putExtra("custName", sharedPreferences.getString(Constants.USER_ID,""));
            intent.putExtra("custMobile", sharedPreferences.getString(Constants.MOBILE_NO,""));
            intent.putExtra("custImage", sharedPreferences.getString(Constants.PROFILE_PIC,""));
            startActivity(intent);
            finish();
        }

    }

    private void addTransactions(){
        dataObject = new JSONObject();
        try{
            dataObject.put("responseCode","00");
            dataObject.put("responseMessage","Approved");
            dataObject.put("dateTime", Utility.getTimeStamp());
           // dataObject.put("referenceNumber",mCardSaleResponseData.getRRNO());
            dataObject.put("amount",String.valueOf(amount));
          //  dataObject.put("rrn",mCardSaleResponseData.getRRNO());
            dataObject.put("transactionStatus",true);
            dataObject.put("merchantId",getIntent().getStringExtra("shop_code"));
            //dataObject.put("merchantRefNo",sharedPreferences.getString(Constants.MERCHANT_REF_NO,""));
            //dataObject.put("merchantRefInvoiceNo",sharedPreferences.getString(Constants.MERCHANT_REF_NO,""));
            dataObject.put("cardType","Cash");
            dataObject.put("cardBrand","Cash");
            dataObject.put("paymentMethod","Cash");
            // String tranStatus = dataObject.getString("transactionStatus");
            boolean approved = dataObject.getBoolean("transactionStatus");;
            dataObject.put("approved",approved);
            dataObject.put("transactionId","0");
            dataObject.put("transactionType","Khata/"+khataNo+"/Debit");
            dataObject.put("date",Utility.getTimeStamp());
            dataObject.put("orderNumber","Khata Debit");
            dataObject.put("payStatus", "Done");
            dataObject.put("status_message", "SUCCESS");
            dataObject.put("paymentMode", "Cash");
            dataObject.put("merchantName",getIntent().getStringExtra("name"));
            dataObject.put("merchantAddress", getIntent().getStringExtra("address"));
            dataObject.put("shopCode",getIntent().getStringExtra("shop_code"));
            dataObject.put("userCreateStatus","C");
            dataObject.put("paymentMethod","Cash");
            dataObject.put("paymentBrand","Cash");
            dataObject.put("userName",sharedPreferences.getString(Constants.FULL_NAME,""));
            dataObject.put("custCode",sharedPreferences.getString(Constants.USER_ID,""));
            dataObject.put("dbName",sharedPreferences.getString(Constants.DB_NAME,""));
            dataObject.put("dbUserName",sharedPreferences.getString(Constants.DB_USER_NAME,""));
            dataObject.put("dbPassword",sharedPreferences.getString(Constants.DB_PASSWORD,""));
        }catch (JSONException e){
            e.printStackTrace();
        }
        String url=getResources().getString(R.string.url_shop)+Constants.ADD_TRANS_DATA;
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url,dataObject,"updatePaymentStatus");
    }

    @Override
    public void onJsonObjectResponse(JSONObject jsonObject, String apiName) {
        try {
            if (apiName.equals("updatePaymentStatus")) {
                if (jsonObject.getBoolean("status")) {
                    dataObject.put("transactionId",jsonObject.getJSONObject("result").getString("transactionId"));
                    dataObject.put("rrn",dataObject.getString("transactionId"));
                    Intent i = new Intent(PayKhataActivity.this, TransactionDetailsActivity.class);
                    i.putExtra("response", dataObject.toString());
                    i.putExtra("amount", String.valueOf(amount)+".00");
                    i.putExtra("flag", "Khatabook");
                    startActivity(i);
                    finish();
                }else{
                    DialogAndToast.showDialog(jsonObject.getString("message"),this);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            DialogAndToast.showToast(getResources().getString(R.string.json_parser_error)+e.toString(),
                    PayKhataActivity.this);
        }
    }

    public void initFooterAction() {
        findViewById(R.id.relative_footer_action).setBackgroundColor(colorTheme);
        TextView tv = findViewById(R.id.text_action);
        if (colorTheme == getResources().getColor(R.color.white)) {
            tv.setTextColor(getResources().getColor(R.color.primary_text_color));
        } else {
            tv.setTextColor(getResources().getColor(R.color.white));
        }
        tv.setText("Pay");
    }
}
