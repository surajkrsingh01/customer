package com.shoppurscustomer.activities.Settings;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.shoppurscustomer.R;
import com.shoppurscustomer.activities.NetworkBaseActivity;
import com.shoppurscustomer.adapters.SettingsAdapter;
import com.shoppurscustomer.interfaces.MyItemClickListener;
import com.shoppurscustomer.utilities.Constants;

import java.util.ArrayList;
import java.util.List;

public class PersonalProfileActivity extends NetworkBaseActivity implements MyItemClickListener {

    private TextView tvName,tvAddress,tvMobile;
    private ImageView imageQrCode;

    private RecyclerView recyclerView;
    private List<String> itemList;
    private SettingsAdapter itemAdapter;
    private TextView tv_top_parent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalinfo);
        initViews();
        initFooter(this,2);
        initViews();
    }

    private void initViews(){
        tvName = findViewById(R.id.text_shop_name);
        tvAddress = findViewById(R.id.text_shop_address);
        tvMobile = findViewById(R.id.text_shop_mobile);
        imageQrCode = findViewById(R.id.image_qr_code);

        tvName.setText(sharedPreferences.getString(Constants.FULL_NAME,""));
        String addr = sharedPreferences.getString(Constants.CUST_ADDRESS,"");
        Log.d("addr ", addr);
        if(TextUtils.isEmpty(addr)){
            tvAddress.setVisibility(View.GONE);
        }else {
            tvAddress.setVisibility(View.VISIBLE);
            tvAddress.setText(addr);
        }

        tvMobile.setText(sharedPreferences.getString(Constants.MOBILE_NO,""));

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        // requestOptions.override(Utility.dpToPx(150, context), Utility.dpToPx(150, context));
        requestOptions.centerCrop();
        requestOptions.skipMemoryCache(false);

        Log.d("CustCode ", sharedPreferences.getString(Constants.USER_ID,""));

        Glide.with(this)
                .load(getResources().getString(R.string.base_image_url)+"/customers/"+
                        sharedPreferences.getString(Constants.USER_ID,"")+"/qrcode.PNG")
                .apply(requestOptions)
                .error(R.drawable.qr_code_default)
                .into(imageQrCode);

        itemList = new ArrayList<>();
        itemList.add("Customer Details");
        itemList.add("Address");

        //itemList.add("Invite on Shoppurs");
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        itemAdapter=new SettingsAdapter(this,itemList);
        itemAdapter.setMyItemClickListener(this);
        recyclerView.setAdapter(itemAdapter);

        tv_top_parent = findViewById(R.id.text_right_label);
        tv_top_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PersonalProfileActivity.this, SettingActivity.class));
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            super.onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClicked(int position) {
        String name = itemList.get(position);
        if(name.equals("Customer Details")){
            Intent intent = new Intent(this, BasicProfileActivity.class);
            startActivity(intent);
        }else if(name.equals("Address")) {
            Intent intent = new Intent(this, AddressActivity.class);
            startActivity(intent);
        }
    }
}