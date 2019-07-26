package com.shoppurscustomer.activities.Settings;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;


import com.android.volley.Request;
import com.shoppurscustomer.R;
import com.shoppurscustomer.activities.NetworkBaseActivity;
import com.shoppurscustomer.models.DeliveryAddress;
import com.shoppurscustomer.utilities.ConnectionDetector;
import com.shoppurscustomer.utilities.Constants;
import com.shoppurscustomer.utilities.DialogAndToast;

import org.json.JSONException;
import org.json.JSONObject;


public class AddDeliveryAddressActivity extends NetworkBaseActivity {

    private EditText etName,etMobile,etHouseNo,etAddress,etLandMark, etCity,etState, etPinCode;

    private RelativeLayout rlFooter;
    private TextView text_action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_delivery_address);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();

    }

    private void init(){
        text_action = findViewById(R.id.text_action);
        text_action.setText("Add Address");
        etName = findViewById(R.id.edit_customer_name);
        etMobile = findViewById(R.id.edit_customer_mobile);
        etHouseNo = findViewById(R.id.edit_customer_house_no);
        etAddress = findViewById(R.id.edit_customer_address);
        etLandMark = findViewById(R.id.edit_customer_landmark);
        etCity = findViewById(R.id.edit_customer_city);
        etState = findViewById(R.id.edit_customer_state);
        etPinCode = findViewById(R.id.edit_customer_pin);
        rlFooter = findViewById(R.id.relative_footer_action);
        rlFooter.setBackgroundColor(colorTheme);

        rlFooter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ConnectionDetector.isNetworkAvailable(AddDeliveryAddressActivity.this)){
                    registerCustomer();
                }
            }
        });
    }

    private void registerCustomer(){
        String mobile = etMobile.getText().toString();
        String name =   etName.getText().toString();
        String houseNo = etHouseNo.getText().toString();
        String address =   etAddress.getText().toString();
        String landMark = etLandMark.getText().toString();
        String city = etCity.getText().toString();
        String state = etState.getText().toString();
        String pin =   etPinCode.getText().toString();

        if(TextUtils.isEmpty(name)){
            DialogAndToast.showDialog("Please enter customer name.",this);
            return;
        }
        if(TextUtils.isEmpty(mobile)){
            DialogAndToast.showDialog("Please enter mobile number.",this);
            return;
        }else if(mobile.length() != 10){
            DialogAndToast.showDialog("Please enter valid mobile number.",this);
            return;
        }
        if(TextUtils.isEmpty(houseNo)){
            DialogAndToast.showDialog("Please enter House No.",this);
            return;
        }
        if(TextUtils.isEmpty(address)){
            DialogAndToast.showDialog("Please enter Address.",this);
            return;
        }
        if(TextUtils.isEmpty(city)){
            DialogAndToast.showDialog("Please enter City.",this);
            return;
        }
        if(TextUtils.isEmpty(state)){
            DialogAndToast.showDialog("Please enter State.",this);
            return;
        }
        if(TextUtils.isEmpty(pin)){
            DialogAndToast.showDialog("Please enter Pincode.",this);
            return;
        }

        DeliveryAddress deliveryAddress = new DeliveryAddress();
        deliveryAddress.setName(name);
        deliveryAddress.setMobile(mobile);
        deliveryAddress.setHouseNo(houseNo);
        deliveryAddress.setAddress(address);
        deliveryAddress.setLandmark(landMark);
        deliveryAddress.setCity(city);
        deliveryAddress.setState(state);
        deliveryAddress.setPinCode(pin);
        dbHelper.addDeliveryAddress(deliveryAddress, sharedPreferences.getString(Constants.USER_ID,""));
        DialogAndToast.showToast("Address Added ", this);
        finish();

        /*Map<String,String> params=new HashMap<>();
        params.put("mobileNo",mobile);
        params.put("name",name);
        params.put("address",address);
        params.put("email",email);
        params.put("pin",pin);
        params.put("dbName",sharedPreferences.getString(Constants.DB_NAME,""));
        params.put("dbUserName",sharedPreferences.getString(Constants.DB_USER_NAME,""));
        params.put("dbPassword",sharedPreferences.getString(Constants.DB_PASSWORD,""));
        String url=getResources().getString(R.string.url)+Constants.REGISTER_CUSTOMER;
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"registerShopCustomer");*/
    }

    @Override
    public void onJsonObjectResponse(JSONObject response, String apiName) {

        try {
            if (apiName.equals("registerShopCustomer")) {
                if (response.getBoolean("status")) {
                    showMyDialog(response.getString("message"));
                }else{
                    if(response.getInt("result") == 1){
                        showMyDialog(response.getString("message"));
                    }else {
                        DialogAndToast.showDialog(response.getString("message"), this);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDialogPositiveClicked(){
        etName.setText("");
        etMobile.setText("");
        etName.setError(null);
        etMobile.setError(null);
        etAddress.setText("");
        etPinCode.setText("");
    }

}
