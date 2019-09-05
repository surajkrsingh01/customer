package com.shoppurs.activities.payment.ccavenue.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.shoppurs.R;
import com.shoppurs.activities.NetworkBaseActivity;
import com.shoppurs.activities.payment.ccavenue.utility.AvenuesParams;
import com.shoppurs.activities.payment.ccavenue.utility.CardTypeDTO;
import com.shoppurs.activities.payment.ccavenue.utility.Constants;
import com.shoppurs.adapters.PaymentAdapter;
import com.shoppurs.interfaces.MyItemTypeClickListener;
import com.shoppurs.models.PaymentOption;
import com.shoppurs.utilities.AppController;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PaymentActivity extends NetworkBaseActivity implements MyItemTypeClickListener {

    private List<PaymentOption> itemList;
    Map<String,ArrayList<CardTypeDTO>> cardsList = new LinkedHashMap<String,ArrayList<CardTypeDTO>>();
    private RecyclerView recyclerView;
    private PaymentAdapter itemAdapter;

    private String vJsonStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       // initFooterAction(this);
        TextView tvTotalAmt = findViewById(R.id.text_action);
        tvTotalAmt.setText(getIntent().getStringExtra("amount"));

        init();
    }

    private void init(){
        itemList = new ArrayList<>();
       /* PaymentOption paymentOption = new PaymentOption();
        paymentOption.setTitle("Credit Card");
        itemList.add(paymentOption);
        paymentOption = new PaymentOption();
        paymentOption.setTitle("Debit Card");
        itemList.add(paymentOption);
        paymentOption = new PaymentOption();
        paymentOption.setTitle("Net Banking");
        itemList.add(paymentOption);
        paymentOption = new PaymentOption();
        paymentOption.setTitle("Wallet");
        itemList.add(paymentOption);
        paymentOption = new PaymentOption();
        paymentOption.setTitle("UPI");
        itemList.add(paymentOption);*/
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        itemAdapter=new PaymentAdapter(this,itemList,isDarkTheme);
        itemAdapter.setMyItemTypeClickListener(this);
        recyclerView.setAdapter(itemAdapter);

        getPaymentOptions();
    }

    private void getPaymentOptions(){
        showProgress(true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(WebViewActivity.this,response,Toast.LENGTH_LONG).show();
                        showProgress(false);
                        // vResponse = response;
                        // new RenderView().execute();

                        if (response != null && !response.equals("")) {
                            vJsonStr= response;
                            Log.i(TAG,"response "+vJsonStr);
                            try {
                                JSONObject responseObject = new JSONObject(response);
                                JSONArray jsonArray = responseObject.getJSONArray("payOptions");
                                JSONObject jsonObject = null;
                                PaymentOption paymentOption = null;
                                for(int i=0 ;i<jsonArray.length(); i++){
                                    jsonObject = jsonArray.getJSONObject(i);
                                    paymentOption = new PaymentOption();
                                    paymentOption.setId(jsonObject.getString("payOpt"));
                                    paymentOption.setTitle(jsonObject.getString("payOptDesc"));
                                    if(!paymentOption.getTitle().contains("IVRS") && !paymentOption.getTitle().contains("NEFT")
                                     && !paymentOption.getTitle().contains("Mobile")){
                                        itemList.add(paymentOption);

                                        JSONArray vCardArr = new JSONArray(jsonObject.getString("cardsList"));
                                        if(vCardArr.length()>0){
                                            cardsList.put(paymentOption.getId(), new ArrayList<CardTypeDTO>()); //Add a new Arraylist
                                            for(int j=0;j<vCardArr.length();j++){
                                                JSONObject card = vCardArr.getJSONObject(j);
                                                try{
                                                    CardTypeDTO cardTypeDTO = new CardTypeDTO();
                                                    cardTypeDTO.setCardName(card.getString("cardName"));
                                                    cardTypeDTO.setCardType(card.getString("cardType"));
                                                    cardTypeDTO.setPayOptType(card.getString("payOptType"));
                                                    cardTypeDTO.setDataAcceptedAt(card.getString("dataAcceptedAt"));
                                                    cardTypeDTO.setStatus(card.getString("status"));

                                                    cardsList.get(paymentOption.getId()).add(cardTypeDTO);
                                                }catch (Exception e) { Log.e("ServiceHandler", "Error parsing cardType",e); }
                                            }
                                        }
                                    }
                                }

                                itemAdapter.setCardsList(cardsList);
                                itemAdapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            // new GetData().execute();

                        } else {
                            Log.e("ServiceHandler", "Couldn't get any data from the url");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showProgress(false);
                        //Toast.makeText(WebViewActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(AvenuesParams.COMMAND, "getJsonDataVault");
                params.put(AvenuesParams.ACCESS_CODE, "4YRUXLSRO20O8NIH");
                params.put(AvenuesParams.CURRENCY,"INR");
                params.put(AvenuesParams.AMOUNT,"500.00");
                params.put(AvenuesParams.CUSTOMER_IDENTIFIER, "abc");
                return params;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(stringRequest);
    }


    @Override
    public void onItemClicked(int position, int type) {

    }
}
