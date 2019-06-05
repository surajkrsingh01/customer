package com.shoppurscustomer.activities.Settings;

import android.os.Bundle;

import com.shoppurscustomer.R;
import com.shoppurscustomer.activities.NetworkBaseActivity;

public class PersonalProfileActivity extends NetworkBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalinfo);
        initViews();
        initFooter(this,4);
    }

    private void initViews(){
    }
}