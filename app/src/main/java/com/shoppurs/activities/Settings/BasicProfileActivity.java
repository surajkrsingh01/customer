package com.shoppurs.activities.Settings;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.shoppurs.R;
import com.shoppurs.activities.BaseImageActivity;
import com.shoppurs.interfaces.FirebaseImageUploadListener;
import com.shoppurs.services.FirebaseImageUploadService;
import com.shoppurs.utilities.Constants;
import com.shoppurs.utilities.DialogAndToast;
import com.shoppurs.utilities.Utility;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class BasicProfileActivity extends BaseImageActivity implements FirebaseImageUploadListener {

    private EditText etUsername,etGstNo,etEmail,etMobile;
    private CircleImageView profileImage;
    private String imageBase64;
    private TextView tv_top_parent, tv_parent;
    private ImageView btn_camera;
    FirebaseImageUploadService firebaseImageUploadService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseImageUploadService = FirebaseImageUploadService.getInstance();
       // initFooterAction(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
    }

    private void init(){
        imageBase64 = "no";
        etUsername = findViewById(R.id.edit_user_name);
        etGstNo = findViewById(R.id.edit_gst_no);
        etEmail = findViewById(R.id.edit_email);
        etMobile = findViewById(R.id.edit_mobile);
        profileImage = findViewById(R.id.profile_image);
        RelativeLayout btnUpdate = findViewById(R.id.relative_footer_action);
        btnUpdate.setBackgroundColor(colorTheme);
        btn_camera = findViewById(R.id.btn_camera);
        ((GradientDrawable)btn_camera.getBackground()).setColor(colorTheme);

        etUsername.setText(sharedPreferences.getString(Constants.FULL_NAME,""));
        //etShopName.setText(sharedPreferences.getString(Constants.SHOP_NAME,""));
        //etGstNo.setText(sharedPreferences.getString(Constants.GST_NO,""));
        etMobile.setText(sharedPreferences.getString(Constants.MOBILE_NO,""));
        etEmail.setText(sharedPreferences.getString(Constants.EMAIL,""));

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        // requestOptions.override(Utility.dpToPx(150, context), Utility.dpToPx(150, context));
        requestOptions.centerCrop();
        requestOptions.skipMemoryCache(false);
        requestOptions.signature(new ObjectKey(sharedPreferences.getString("profile_image_signature","")));
        Glide.with(this)
                .load(sharedPreferences.getString(Constants.PROFILE_PIC, ""))
                .apply(requestOptions)
                .error(R.drawable.ic_photo_black_192dp)
                .into(profileImage);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
            }
        });

        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        tv_top_parent = findViewById(R.id.text_left_label);
        tv_parent = findViewById(R.id.text_right_label);

        tv_top_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BasicProfileActivity.this, SettingActivity.class));
                finish();
            }
        });
        tv_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BasicProfileActivity.this, PersonalProfileActivity.class));
                finish();
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageDialog(sharedPreferences.getString(Constants.PROFILE_PIC, ""), profileImage);
            }
        });
    }

    private void updateProfile(){
        String username = etUsername.getText().toString();
        String gstNo = etGstNo.getText().toString();
       String mobile = etMobile.getText().toString();
       String email = etEmail.getText().toString();

        if(TextUtils.isEmpty(username)){
            DialogAndToast.showDialog("Please enter name",this);
            return;
        }


        /*if(TextUtils.isEmpty(gstNo)){
            DialogAndToast.showDialog("Please enter GST No",this);
            return;
        }*/
        /*if(TextUtils.isEmpty(mobile)){
            DialogAndToast.showDialog("Please enter mobile number",this);
            return;
        }else  if(mobile.length() != 10){
            DialogAndToast.showDialog("Please enter valid mobile number",this);
            return;
        }*/

        if(TextUtils.isEmpty(email)){
            DialogAndToast.showDialog("Please enter email",this);
            return;
        }

        Map<String,String> params=new HashMap<>();
        params.put("name",username);
        params.put("email",email);
        params.put("mobile",sharedPreferences.getString(Constants.MOBILE_NO,""));
        params.put("profileImage",imageBase64);
        params.put("id","0");
        params.put("code",sharedPreferences.getString(Constants.USER_ID,""));
        params.put("dbName",sharedPreferences.getString(Constants.DB_NAME,""));
        params.put("dbUserName",sharedPreferences.getString(Constants.DB_USER_NAME,""));
        params.put("dbPassword",sharedPreferences.getString(Constants.DB_PASSWORD,""));
        Log.d(TAG, params.toString());
        JSONArray dataArray = new JSONArray();
        JSONObject dataObject = new JSONObject(params);
        dataArray.put(dataObject);
        String url=getResources().getString(R.string.url)+Constants.UPDATE_BASIC_PROFILE;
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"updateBasicProfile");

    }

    @Override
    public void onJsonObjectResponse(JSONObject response, String apiName) {
        if (apiName.equals("updateBasicProfile")) {
           try{
               if(response.getBoolean("status")){
                   editor.putString(Constants.FULL_NAME,etUsername.getText().toString());
                   editor.putString(Constants.MOBILE_NO,etMobile.getText().toString());
                   editor.putString(Constants.EMAIL,etEmail.getText().toString());
                   uploadImagesToFirebase();
                   if(!imageBase64.equals("no")) {
                       String timestamp = Utility.getTimeStamp();
                       requestOptions.signature(new ObjectKey(timestamp));
                       editor.putString(Constants.PROFILE_PIC, getResources().getString(R.string.base_image_url) +
                               "/customers/" + sharedPreferences.getString(Constants.USER_ID, "") + "/photo.jpg");
                       editor.putString("profile_image_signature",timestamp);

                   }
                   editor.commit();
                   DialogAndToast.showToast(response.getString("message"),this);
                  // finish();
               }else{
                   DialogAndToast.showDialog(response.getString("message"),this);
               }
           }catch (JSONException e){
               e.printStackTrace();
           }
        }
    }

    @Override
    protected void imageAdded(){
        Glide.with(this)
                .load(imagePath)
                .apply(requestOptions)
                .into(profileImage);
       imageBase64 = convertToBase64(new File(imagePath));

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

    private void uploadImagesToFirebase(){
        Log.i(TAG,"uploading images to firebase..");
            showProgress(true);
            firebaseImageUploadService.setFirebaseImageUploadListener(this);
            imageBase64 = convertToBase64(new File(imagePath));
            firebaseImageUploadService.uploadImage("Customer ", imageBase64);
    }

    @Override
    public void onImageUploaded(String position, String url) {
        String timestamp = Utility.getTimeStamp();
        requestOptions.signature(new ObjectKey(timestamp));
        editor.putString(Constants.PROFILE_PIC, url);
        editor.putString("profile_image_signature",timestamp);
    }

    @Override
    public void onImageFailed(String position) {

    }
}
