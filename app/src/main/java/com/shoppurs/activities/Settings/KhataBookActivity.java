package com.shoppurs.activities.Settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.shoppurs.R;
import com.shoppurs.activities.InvoiceActivity;
import com.shoppurs.activities.NetworkBaseActivity;
import com.shoppurs.adapters.KhataTransactionAdapter;
import com.shoppurs.interfaces.MyItemClickListener;
import com.shoppurs.models.KhataTransaction;
import com.shoppurs.models.MyProduct;
import com.shoppurs.utilities.ConnectionDetector;
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

public class KhataBookActivity extends NetworkBaseActivity implements MyItemClickListener {

    private TextView text_action, text_left_label, text_second_label;
    private RelativeLayout relative_footer_action;
    private TextView textKhataNo,textTotalDueAmount,tvKhataNo,textKhataOpenDate,textCustomerName,textCustMobile;
    private String khataNo, khataOpenDate;
    private RecyclerView recyclerView;
    private List<KhataTransaction> itemList;
    private KhataTransactionAdapter itemAdapter;
    private float totalDueAmount;
    private RelativeLayout rlFooter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_khata_book);
            init();
    }

    private void init(){
        initHeaderFooter();
        khataNo = getIntent().getStringExtra("khataNo");
        khataOpenDate = getIntent().getStringExtra("khataOpenDate");
        if(ConnectionDetector.isNetworkAvailable(this)){
            getTransactions();
        }else{
            DialogAndToast.showDialog(getResources().getString(R.string.no_internet),this);
        }

        tvKhataNo = findViewById(R.id.text_sub_header);
        textKhataNo = findViewById(R.id.textKhataNo);
        textTotalDueAmount = findViewById(R.id.textTotalDueAmount);
        textKhataOpenDate = findViewById(R.id.textKhataOpenDate);
        textCustomerName = findViewById(R.id.textCustomerName);
        textCustMobile = findViewById(R.id.textCustMobile);
        rlFooter = findViewById(R.id.relative_footer_action);
        textKhataNo.setText("Khata No - "+khataNo);
        textKhataOpenDate.setText("Open Date - "+Utility.parseDate(khataOpenDate,"yyyy-MM-dd HH:mm:ss","dd MMM yyyy"));
        textCustomerName.setText(getIntent().getStringExtra("name")
                +" "+getIntent().getStringExtra("mobile"));

        itemList = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);
        // recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        itemAdapter=new KhataTransactionAdapter(this,itemList);
        itemAdapter.setMyItemClickListener(this);
        recyclerView.setAdapter(itemAdapter);

        rlFooter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                khataPayment();
            }
        });
    }


    private void getTransactions(){
        Map<String,String> params=new HashMap<>();
        params.put("kbNo",khataNo);
        params.put("dbName",sharedPreferences.getString(Constants.DB_NAME,""));
        params.put("dbUserName",sharedPreferences.getString(Constants.DB_USER_NAME,""));
        params.put("dbPassword",sharedPreferences.getString(Constants.DB_PASSWORD,""));
        String url=getResources().getString(R.string.url_customer)+Constants.KHATA_TRANSACTIONS;
        //String url=getResources().getString(R.string.url_shop)+Constants.KHATA_TRANSACTIONS;
        //showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"khataTransaction");
    }

    private void khataPayment(){
        Intent intent = new Intent(KhataBookActivity.this, PayKhataActivity.class);
        intent.putExtra("khataNo",khataNo);
        intent.putExtra("totalDue",(int)totalDueAmount);
        intent.putExtra("shop_code", getIntent().getStringExtra("shop_code"));
        //intent.putExtra("custId", getIntent().getIntExtra("custId",0));
        intent.putExtra("name", getIntent().getStringExtra("name"));
        intent.putExtra("mobile", getIntent().getStringExtra("mobile"));
        intent.putExtra("photo", getIntent().getStringExtra("photo"));
        intent.putExtra("address", getIntent().getStringExtra("address"));
        //intent.putExtra("custUserCreateStatus", getIntent().getStringExtra("custUserCreateStatus"));
        startActivityForResult(intent,123);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(itemList.size() > 0){
            totalDueAmount = 0f;
            itemList.clear();
            itemAdapter.notifyDataSetChanged();
            getTransactions();
        }
    }

    @Override
    public void onJsonObjectResponse(JSONObject jsonObject, String apiName) {
        try {
            if(apiName.equals("khataTransaction")) {
                if (jsonObject.getBoolean("status")) {
                    JSONArray jsonArray = jsonObject.getJSONArray("result");
                    int len = jsonArray.length();
                    KhataTransaction item = null;
                    Gson gson = new Gson();
                    for(int i=0;i<len;i++){
                        item = gson.fromJson(jsonArray.getJSONObject(i).toString(),KhataTransaction.class);
                        if(item.getPaymentStatus()==null || item.getPaymentStatus().equals("null"))
                            item.setPaymentStatus("Pending");
                        if(item.getPaymentTransactionType().contains("Credit")){
                            totalDueAmount = totalDueAmount + item.getPaymentAmount();
                        }else{
                            if(item.getPaymentStatus().equals("Approved"))
                            totalDueAmount = totalDueAmount - item.getPaymentAmount();
                        }
                        itemList.add(item);
                    }

                    if(totalDueAmount > 0){
                        rlFooter.setVisibility(View.VISIBLE);
                        textTotalDueAmount.setText(String.format("%.02f",totalDueAmount));
                    }else{
                        rlFooter.setVisibility(View.GONE);
                        textTotalDueAmount.setText("0.00");
                    }

                    itemAdapter.notifyDataSetChanged();
                }
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void initHeaderFooter(){
        text_left_label = findViewById(R.id.text_left_label);
        text_left_label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        text_second_label = findViewById(R.id.text_second_label);
        text_second_label.setText("Khatabook");
        text_action = findViewById(R.id.text_action);
        text_action.setText("Pay");
        relative_footer_action = findViewById(R.id.relative_footer_action);
        relative_footer_action.setBackgroundColor(colorTheme);
        relative_footer_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogAndToast.showDialog("Payment to "+getIntent().getStringExtra("name"), KhataBookActivity.this);
            }
        });
    }

    @Override
    public void onItemClicked(int position) {
        KhataTransaction item = itemList.get(position);
        Intent intent = new Intent(KhataBookActivity.this, InvoiceActivity.class);
        intent.putExtra("orderNumber",item.getCoNo());
        startActivity(intent);
    }

    @Override
    public void onProductSearch(MyProduct myProduct) {

    }

    @Override
    public void onItemClicked(int pos, String type) {

    }
}
