package com.shoppurs.activities.Settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.shoppurs.R;
import com.shoppurs.activities.NetworkBaseActivity;
import com.shoppurs.adapters.DeliveryAddressListAdapter;
import com.shoppurs.interfaces.MyItemClickListener;
import com.shoppurs.models.DeliveryAddress;
import com.shoppurs.models.MyProduct;
import com.shoppurs.utilities.ConnectionDetector;
import com.shoppurs.utilities.Constants;
import com.shoppurs.utilities.DialogAndToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeliveryAddressListActivity extends NetworkBaseActivity implements MyItemClickListener {

    private List<DeliveryAddress> itemList;
    private RecyclerView recyclerView;
    private DeliveryAddressListAdapter myItemAdapter;
    private TextView textViewError, text_header;
    private LinearLayout rl_new_DeliveryAddress;

    private String flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_address);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();

    }

    private void init(){
        text_header = findViewById(R.id.text_header);
        text_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DeliveryAddressListActivity.this, SettingActivity.class));
            }
        });
        textViewError = findViewById(R.id.text_error);
        recyclerView=findViewById(R.id.recycler_view);
        rl_new_DeliveryAddress = findViewById(R.id.linear_eidt);
        rl_new_DeliveryAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DeliveryAddressListActivity.this, AddDeliveryAddressActivity.class));
            }
        });

        flag = getIntent().getStringExtra("flag");
        itemList = new ArrayList<>();
        itemList = dbHelper.getallDeliveryAddress(sharedPreferences.getString(Constants.USER_ID,""));
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        myItemAdapter=new DeliveryAddressListAdapter(this,itemList);
        myItemAdapter.setFlag(flag);
        myItemAdapter.setMyItemClickListener(this);
        myItemAdapter.setColorTheme(colorTheme);
        recyclerView.setAdapter(myItemAdapter);

    }
    private void showNoData(boolean show){
        if(show){
            recyclerView.setVisibility(View.GONE);
            textViewError.setVisibility(View.VISIBLE);
        }else{
            recyclerView.setVisibility(View.VISIBLE);
            textViewError.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemClicked(int position) {
        DeliveryAddress deliveryAddress = (DeliveryAddress) itemList.get(position);
        Intent intent  = new Intent();
        /*intent.putExtra("name",coupon.getName());
        intent.putExtra("id",coupon.getId());
        intent.putExtra("per",coupon.getPercentage());
        intent.putExtra("amount",coupon.getAmount());
        setResult(-1,intent);
        finish();*/
    }

    @Override
    public void onProductSearch(MyProduct myProduct) {

    }

    @Override
    public void onItemClicked(int pos, String type) {

    }


    public void showAlert(String title, String msg, final String action, final DeliveryAddress deliveryAddress){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(action.equals("delete")){
                            deleteDeliveryAddress(deliveryAddress);
                        }else if(action.equals("set_default")){
                            deliveryAddress.setIsDefaultAddress("Yes");
                            updateDeliveryAddress(deliveryAddress);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        updateList();
                    }
                });
        AlertDialog alert = builder.create();
        alert.setTitle(title);
        alert.show();
    }

    DeliveryAddress deliveryAddress;
    public void updateDeliveryAddress(DeliveryAddress address){
        this.deliveryAddress = address;
        Map<String,String> params=new HashMap<>();
        params.put("dbName",sharedPreferences.getString(Constants.DB_NAME,""));
        params.put("dbUserName",sharedPreferences.getString(Constants.DB_USER_NAME,""));
        params.put("dbPassword",sharedPreferences.getString(Constants.DB_PASSWORD,""));

        params.put("userCode",sharedPreferences.getString(Constants.USER_ID,""));
        params.put("id", address.getId());
        params.put("name",address.getName());
        params.put("mobile",address.getMobile());
        params.put("city",address.getCity());
        params.put("province",address.getState());
        params.put("country",address.getCountry());
        params.put("locality",address.getLandmark());
        params.put("zip",address.getPinCode());
        params.put("address",address.getAddress());
        params.put("userName",sharedPreferences.getString(Constants.FULL_NAME,""));
        params.put("userLat",address.getDelivery_lat());
        params.put("userLong",address.getDelivery_long());
        if(!TextUtils.isEmpty(address.getIsDefaultAddress()) && address.getIsDefaultAddress().equals("Yes"))
            params.put("isDefault","1");
        else params.put("isDefault","0");
        String url=getResources().getString(R.string.root_url)+"customers/update_delivery_address";
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"updateAddress");
    }

    private void deleteDeliveryAddress(DeliveryAddress address){
        this.deliveryAddress = address;
        Map<String,String> params=new HashMap<>();
        params.put("dbName",sharedPreferences.getString(Constants.DB_NAME,""));
        params.put("dbUserName",sharedPreferences.getString(Constants.DB_USER_NAME,""));
        params.put("dbPassword",sharedPreferences.getString(Constants.DB_PASSWORD,""));
        params.put("id", address.getId());
        String url=getResources().getString(R.string.root_url)+"customers/delete_delivery_address";
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"deleteAddress");
    }

    @Override
    public void onJsonObjectResponse(JSONObject response, String apiName) {
        try {
            if(response.getString("status").equals("true")||response.getString("status").equals(true)) {
                if(apiName.equals("updateAddress")){
                    dbHelper.updateDeliveryAddress(deliveryAddress, sharedPreferences.getString(Constants.USER_ID,""));
                    setDefaultAddressChecked(deliveryAddress.getId());
                    if(!TextUtils.isEmpty(flag) && flag.equals("CartActivity")){
                        Intent intent = new Intent();
                        intent.putExtra("object", deliveryAddress);
                        setResult(-1, intent);
                        finish();
                    }
                }else if(apiName.equals("deleteAddress")){
                    dbHelper.remove_delivery_address(deliveryAddress.getId());
                    updateList();
                }
            }else {
                DialogAndToast.showToast(response.getString("message"), DeliveryAddressListActivity.this);
            }
        }catch (JSONException e) {
            e.printStackTrace();
            DialogAndToast.showToast("Something went wrong, please try again", DeliveryAddressListActivity.this);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        updateList();
    }

    public void setDefaultAddressChecked(String id){
        for(DeliveryAddress address: itemList){
            if(!address.getId().equals(id))
                address.setIsDefaultAddress("No");
               dbHelper.updateDeliveryAddress(address, sharedPreferences.getString(Constants.USER_ID,""));
        }
        updateList();
    }

    public void updateList(){
        List<DeliveryAddress> addressList = dbHelper.getallDeliveryAddress(sharedPreferences.getString(Constants.USER_ID,""));
        itemList.clear();
        itemList.addAll(addressList);
        Log.d("itemList size "+itemList.size(), "addressList size "+addressList.size());
        Log.d("myItemAdapter ", ""+myItemAdapter);
        if(myItemAdapter!=null)
            myItemAdapter.notifyDataSetChanged();
    }
}
