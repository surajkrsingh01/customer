package com.shoppurscustomer.activities.payment.ccavenue.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.shoppurscustomer.R;
import com.shoppurscustomer.activities.NetworkBaseActivity;
import com.shoppurscustomer.activities.TransactionDetailsActivity;
import com.shoppurscustomer.activities.payment.ccavenue.utility.AvenuesParams;
import com.shoppurscustomer.activities.payment.ccavenue.utility.Constants;
import com.shoppurscustomer.activities.payment.ccavenue.utility.LoadingDialog;
import com.shoppurscustomer.activities.payment.ccavenue.utility.RSAUtility;
import com.shoppurscustomer.activities.payment.ccavenue.utility.ServiceUtility;
import com.shoppurscustomer.utilities.DialogAndToast;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class CCAvenueWebViewActivity extends NetworkBaseActivity {

    public static final String TAG = "CCAvenueWeb";
   // public static final String ACCESS_CODE = "4YRUXLSRO20O8NIH";
    public String orderId = "0";
   /* Test
    public static final String ACCESS_CODE = "AVYS02GA48AW12SYWA";
    public static final String MERCHANT_ID = "191918";
    public static final String WORKING_KEY = "42A76E35C3C23567D3E1E3284C1A12AF";*/

   /**/
    public static final String ACCESS_CODE = "AVQH72EH28AD03HQDA";
    public static final String MERCHANT_ID = "143051";
    public static final String WORKING_KEY = "534D4882D2F074761151788FCE5EE352";

    public static final String RSA_KEY_URL = "https://secure.ccavenue.com/transaction/jsp/GetRSA.jsp";
    public String REDIRECT_URL = "";
    public String CANCEL_URL = "";

    Intent mainIntent;
    String encVal;
    String vResponse;
    private String flag;
    private String name,address,mobileNo,email,zip,city, state,country,responseData;
    private JSONObject dataObject;
    private WebView webview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ccavenue_web_view);

        webview = (WebView) findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);

        name= sharedPreferences.getString(com.shoppurscustomer.utilities.Constants.FULL_NAME,"");
        address= sharedPreferences.getString(com.shoppurscustomer.utilities.Constants.CUST_ADDRESS,"");
        email= sharedPreferences.getString(com.shoppurscustomer.utilities.Constants.EMAIL,"");
        mobileNo= sharedPreferences.getString(com.shoppurscustomer.utilities.Constants.MOBILE_NO,"");
        zip= sharedPreferences.getString(com.shoppurscustomer.utilities.Constants.CUST_PINCODE,"");
        city = sharedPreferences.getString(com.shoppurscustomer.utilities.Constants.CUST_CITY,"");
        state = sharedPreferences.getString(com.shoppurscustomer.utilities.Constants.CUST_STATE,"");
        country = sharedPreferences.getString(com.shoppurscustomer.utilities.Constants.CUST_COUNTRY,"");

        if(name.equals("null") || name.equals("Not Available")){
            name = "";
        }

        if(address.equals("null") || address.equals("Not Available")){
              address = "";
        }

        if(mobileNo.equals("null") || mobileNo.equals("Not Available")){
            mobileNo = "";
        }

        if(email.equals("null") || email.equals("Not Available")){
           email = "";
        }

        if(zip.equals("null") || zip.equals("Not Available")){
            zip = "";
        }

        mainIntent = getIntent();
        flag = mainIntent.getStringExtra("flag");

        REDIRECT_URL = getResources().getString(R.string.url_web)+"/web/payment/paymentResponseHandler";
        CANCEL_URL = getResources().getString(R.string.url_web)+"/web/payment/paymentResponseHandler";

      //  REDIRECT_URL = getResources().getString(R.string.url)+"/api/paymentResponseHandler";
     //   CANCEL_URL = getResources().getString(R.string.url)+"/api/paymentResponseHandler";

        Integer randomNum = ServiceUtility.randInt(0, 9999999);
       // orderId = mainIntent.getStringExtra(AvenuesParams.ORDER_ID);
        orderId = randomNum.toString();
        Log.i(TAG,"orderID "+orderId);
        get_RSA();


        Log.i(TAG,"amount "+mainIntent.getStringExtra(AvenuesParams.AMOUNT));

      //  get_RSA_key(ACCESS_CODE,orderId);

    }

    private class RenderView extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
           // LoadingDialog.showLoadingDialog(CCAvenueWebViewActivity.this, "Loading...");

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            if (!ServiceUtility.chkNull(vResponse).equals("")
                    && ServiceUtility.chkNull(vResponse).toString().indexOf("ERROR") == -1) {
                StringBuffer vEncVal = new StringBuffer("");
                vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.AMOUNT, mainIntent.getStringExtra(AvenuesParams.AMOUNT)));
                vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.CURRENCY, mainIntent.getStringExtra(AvenuesParams.CURRENCY)));
                /*if(flag.equals("wallet") || flag.equals("credit") || flag.equals("service")){
                    vEncVal.append(ServiceUtility.addToPostParams("merchant_param3",sharedPreferences.getString(com.gsttm.utilities.Constants.USER_ID,"")));
                }*/
                encVal = RSAUtility.encrypt(vEncVal.substring(0, vEncVal.length() - 1), vResponse);  //encrypt amount and currency
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            //LoadingDialog.cancelLoading();
            showProgress(false);

            @SuppressWarnings("unused")
            class MyJavaScriptInterface {
                @JavascriptInterface
                public void processHTML(final String html) {
                    // process the html source code to get final status of transaction
                    Log.i(TAG,"response "+html);
                    final Intent intent = new Intent();
                    try {
                        responseData = html.substring(html.indexOf("{"),(html.lastIndexOf("}")+1));
                        Log.i(TAG,"response "+responseData);
                      //  JSONObject dataObject = new JSONObject(response);
                        intent.putExtra("response", responseData);
                       // intent.putExtra("message", dataObject.getString("status_message"));
                    } catch (Exception e){
                        e.printStackTrace();
                      //  intent.putExtra("transStatus", "true");
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            saveResponse();
                        }
                    });

                //    setResult(-1,intent);
               //     finish();
               //     CCAvenueWebViewActivity.this.finish();
                }

                @JavascriptInterface
                public void showToast(final String webMessage){
                    final String msgeToast = webMessage;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CCAvenueWebViewActivity.this, webMessage, Toast.LENGTH_SHORT).show();
                        }
                    });


                }
            }
            webview.getSettings().setJavaScriptEnabled(true);
            webview.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
           // webview.addJavascriptInterface(new MyJavaScriptInterface(), "AndroidFunction");
            webview.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(webview, url);
                    LoadingDialog.cancelLoading();
                    Log.i(TAG,"redirecting "+url);
                    if (url.contains("paymentResponseHandler")) {
                        Log.i(TAG,"Loading javascript");
                        webview.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
                    }else if(url.contains("processNbkReq")){
                       // webview.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
                    }
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    LoadingDialog.showLoadingDialog(CCAvenueWebViewActivity.this, "Loading...");
                }
            });


            try {
                String postData = "";
                postData = AvenuesParams.ACCESS_CODE + "=" + URLEncoder.encode(ACCESS_CODE, "UTF-8") + "&" + AvenuesParams.MERCHANT_ID + "=" + URLEncoder.encode(MERCHANT_ID, "UTF-8") + "&" + AvenuesParams.ORDER_ID + "=" + URLEncoder.encode(orderId, "UTF-8")
                        + "&" + AvenuesParams.REDIRECT_URL + "=" + URLEncoder.encode(REDIRECT_URL, "UTF-8")
                        + "&" + AvenuesParams.CANCEL_URL + "=" + URLEncoder.encode(CANCEL_URL, "UTF-8")
                        + "&" + AvenuesParams.ENC_VAL + "=" + URLEncoder.encode(encVal, "UTF-8")
                        + "&" + "billing_name" + "=" + URLEncoder.encode(name, "UTF-8")
                        + "&" + "billing_address" + "=" + URLEncoder.encode(address, "UTF-8")
                        + "&" + "billing_address" + "=" + URLEncoder.encode(address, "UTF-8")
                        + "&" + "billing_address" + "=" + URLEncoder.encode(address, "UTF-8")
                        + "&" + "billing_zip" + "=" + URLEncoder.encode(zip, "UTF-8")
                        + "&" + "billing_city" + "=" + URLEncoder.encode(city, "UTF-8")
                        + "&" + "billing_state" + "=" + URLEncoder.encode(state, "UTF-8")
                        + "&" + "billing_country" + "=" + URLEncoder.encode(country, "UTF-8")
                        + "&" + "billing_tel" + "=" + URLEncoder.encode(mobileNo, "UTF-8")
                        + "&" + "billing_email" + "=" + URLEncoder.encode(email, "UTF-8")
                        + "&" + "delivery_name" + "=" + URLEncoder.encode(name, "UTF-8")
                        + "&" + "delivery_address" + "=" + URLEncoder.encode(address, "UTF-8")
                         //+ "&" + "billing_zip" + "=" + URLEncoder.encode("Vipin Dhama", "UTF-8")
                        + "&" + "delivery_tel" + "=" + URLEncoder.encode(mobileNo, "UTF-8")
                        + "&" + "delivery_email" + "=" + URLEncoder.encode(email, "UTF-8")
                        + "&" + "merchant_param3" + "=" + URLEncoder.encode(sharedPreferences.getString(com.shoppurscustomer.utilities.Constants.USER_ID,""), "UTF-8");
                Log.i(TAG,"params "+postData);
                webview.postUrl(Constants.TRANS_URL, postData.getBytes());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
    }

    public void get_RSA() {

        Log.i(TAG,"Getting RSA...");
        String url=getResources().getString(R.string.url_web)+"/api/getRSAKey?orderId="+orderId+"&accessCode="+ACCESS_CODE;
        progressDialog.setMessage("Loading...");
        showProgress(true);
        // Log.i(TAG,"params "+params.toString());
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(),"getRSAKey");

    }

    public void get_RSA_key(final String ac, final String od) {
        LoadingDialog.showLoadingDialog(CCAvenueWebViewActivity.this, "Loading...");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, RSA_KEY_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(WebViewActivity.this,response,Toast.LENGTH_LONG).show();
                        LoadingDialog.cancelLoading();

                        if (response != null && !response.equals("")) {
                            vResponse = response;     ///save retrived rsa key
                            Log.i(TAG,"key "+vResponse);
                            if (vResponse.contains("!ERROR!")) {
                                show_alert(vResponse);
                            } else {
                                new RenderView().execute();   // Calling async task to get display content
                            }


                        }
                        else
                        {
                            show_alert("No response");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        LoadingDialog.cancelLoading();
                        //Toast.makeText(WebViewActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(AvenuesParams.ACCESS_CODE, ac);
                params.put(AvenuesParams.ORDER_ID, od);
                return params;
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void show_alert(String msg) {
        android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(
                CCAvenueWebViewActivity.this).create();

        alertDialog.setTitle("Error!!!");
        if (msg.contains("\n"))
            msg = msg.replaceAll("\\\n", "");

        alertDialog.setMessage(msg);



        alertDialog.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });


        alertDialog.show();
    }

    @Override
    public void onJsonObjectResponse(JSONObject response, String apiName) {
        try {
             if(apiName.equals("getRSAKey")){
                if(response.getBoolean("status")){
                    String jsonObject = response.getString("result");
                    if (jsonObject != null && !jsonObject.equals("")) {
                        vResponse = jsonObject;
                        ///save retrived rsa key
                        if (vResponse.contains("!ERROR!")) {
                            DialogAndToast.showDialog(vResponse,CCAvenueWebViewActivity.this);
                        } else {
                            showProgress(true);
                            new RenderView().execute();
                        }
                    }

                }else{
                    DialogAndToast.showDialog(response.getString("message"),this);
                }
            }else if (apiName.equals("updatePaymentStatus")) {
                 if (response.getBoolean("status")) {

                     if(flag.equals("buyUserLicense")){
                         Intent intent = new Intent();
                         intent.putExtra("response",dataObject.toString());
                         setResult(-1,intent);
                         finish();
                         CCAvenueWebViewActivity.this.finish();
                     }else{
                         Intent intent = new Intent(CCAvenueWebViewActivity.this, TransactionDetailsActivity.class);
                         intent.putExtra("responseData",dataObject.toString());
                         intent.putExtra("shopArray",getIntent().getStringExtra("shopArray"));
                         intent.putExtra("response", dataObject.toString());
                         startActivity(intent);
                         CCAvenueWebViewActivity.this.finish();
                     }

                 }else{
                     DialogAndToast.showDialog(response.getString("message"),this);
                 }
             }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onStringResponse(String response, String apiName) {
        if (response != null && !response.equals("")) {
            vResponse = response;
            ///save retrived rsa key


            if (vResponse.contains("!ERROR!")) {
                DialogAndToast.showDialog(vResponse,CCAvenueWebViewActivity.this);
            } else {
                showProgress(true);
                new RenderView().execute();

            }
        }
    }

    private void saveResponse(){
        try{
            dataObject = new JSONObject(responseData);
            Log.i(TAG,"Save Response "+dataObject.toString());
            try{
                dataObject.put("orderNumber",orderId);
                if(dataObject.getString("response_code").equals("0")){
                    dataObject.put("status", "Done");
                    dataObject.put("approved", true);
                }else{
                    dataObject.put("status", "Failed");
                    dataObject.put("approved", false);
                }
                dataObject.put("transactionType", "NA");
                dataObject.put("merchantId", MERCHANT_ID);
                dataObject.put("paymentMethod", dataObject.getString("payment_mode"));
                dataObject.put("paymentMode", "ePay");
                dataObject.put("transactionId", dataObject.getString("tracking_id"));
                dataObject.put("cardBrand", dataObject.getString("card_name"));
                dataObject.put("responseCode", dataObject.getString("response_code"));
                dataObject.put("responseMessage", dataObject.getString("status_message"));
                dataObject.put("currencyCode", dataObject.getString("currency"));
                dataObject.put("date", dataObject.getString("trans_date"));
                //dataObject.put("status", dataObject.getString("order_status"));
                dataObject.put("custCode",getIntent().getStringExtra("custCode"));
                //dataObject.put("cardHolderName",dataObject.getString("Card Hodler Name"));
                dataObject.put("userName",sharedPreferences.getString(com.shoppurscustomer.utilities.Constants.FULL_NAME,""));
                dataObject.put("dbName",sharedPreferences.getString(com.shoppurscustomer.utilities.Constants.DB_NAME,""));
                dataObject.put("dbUserName",sharedPreferences.getString(com.shoppurscustomer.utilities.Constants.DB_USER_NAME,""));
                dataObject.put("dbPassword",sharedPreferences.getString(com.shoppurscustomer.utilities.Constants.DB_PASSWORD,""));
            }catch (JSONException e){
                e.printStackTrace();
            }
            String url=getResources().getString(R.string.url_web)+ com.shoppurscustomer.utilities.Constants.ADD_TRANS_DATA;
            showProgress(true);
            jsonObjectApiRequest(Request.Method.POST,url,dataObject,"updatePaymentStatus");

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

}
