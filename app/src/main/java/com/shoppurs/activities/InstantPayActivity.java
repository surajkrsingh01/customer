package com.shoppurs.activities;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import com.shoppurs.R;
import com.shoppurs.activities.payment.ccavenue.activities.CCAvenueWebViewActivity;
import com.shoppurs.activities.payment.ccavenue.activities.PaymentActivity;
import com.shoppurs.activities.payment.ccavenue.utility.AvenuesParams;
import com.shoppurs.models.MyShop;
import com.shoppurs.utilities.DialogAndToast;

public class InstantPayActivity  extends NetworkBaseActivity {

    private EditText editTextAmount,editTextRemark;
    private TextView tvBack, tvMobile, tvName, tvAction;
    private RelativeLayout btnContinue;
    private MyShop myShop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instantpay);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        myShop = (MyShop) getIntent().getSerializableExtra("object");
        init();
    }

    private void init(){
        editTextAmount = findViewById(R.id.edit_amount);
        editTextRemark = findViewById(R.id.edit_remark);
        btnContinue = findViewById(R.id.relative_footer_action);
        btnContinue.setBackgroundColor(colorTheme);
        tvAction = findViewById(R.id.text_action);
        tvMobile = findViewById(R.id.text_mobile);
        tvName = findViewById(R.id.text_name);
        tvBack = findViewById(R.id.text_header);
        tvAction.setText("Pay Now");
        if(myShop!=null) {
            tvMobile.setText(myShop.getMobile());
            tvName.setText(myShop.getName());
        }

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               checkCustomerInfo();
            }
        });

        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InstantPayActivity.this, StoresListActivity.class));
                finish();
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    private void checkCustomerInfo(){
        String amount = editTextAmount.getText().toString();
        String remark = editTextRemark.getText().toString();

        if(TextUtils.isEmpty(amount)){
            DialogAndToast.showDialog("Please enter Amount.",this);
            return;
        }else if(TextUtils.isEmpty(remark)){
            DialogAndToast.showDialog("Please enter Remark.",this);
            return;
        }else {
            Intent intent = new Intent(InstantPayActivity.this, PaymentActivity.class);
          //  String ta = editTextAmount.getText().toString().split(" ")[1];
          //  ta = ta.replaceAll(",", "");
            intent.putExtra(AvenuesParams.AMOUNT, String.format("%.02f", Float.parseFloat(editTextAmount.getText().toString())));
            intent.putExtra(AvenuesParams.CURRENCY, "INR");
            intent.putExtra("flag", "instantPay");
            intent.putExtra("remarks", editTextRemark.getText().toString());
            intent.putExtra("object", myShop);
            //intent.putExtra("shopArray",shopArray.toString());
            startActivity(intent);
            finish();
        }
    }
}
