package com.shoppurscustomer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.shoppurscustomer.R;
import com.shoppurscustomer.activities.Settings.SettingActivity;
import com.shoppurscustomer.adapters.MyItemAdapter;
import com.shoppurscustomer.models.HomeListItem;
import com.shoppurscustomer.utilities.Constants;
import com.shoppurscustomer.utilities.DialogAndToast;
import com.shoppurscustomer.utilities.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends NetworkBaseActivity {

    private RecyclerView recyclerView;

    private MyItemAdapter myItemAdapter;
    private List<Object> itemList;
    private TextView textViewError;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;

    private float MIN_WIDTH = 200,MIN_HEIGHT = 230,MAX_WIDTH = 200,MAX_HEIGHT = 290;
    private TextView text_customer_address;
    private CircleImageView customer_profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        text_customer_address = findViewById(R.id.text_customer_address);
        text_customer_address.setText(Utility.getTimeStamp("EEE dd MMM, yyyy"));

        customer_profile= findViewById(R.id.profile_image);
        customer_profile.setCircleBackgroundColor(colorTheme);
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
                .into(customer_profile);
        customer_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });


        //if(String.valueOf(tv_location.getText()).length()>100)

        itemList = new ArrayList<>();
        HomeListItem myItem = new HomeListItem();
        //smyItem.setTitle("Sunday 16 December, 2018");
        myItem.setDesc("Today");
        myItem.setType(0);
        itemList.add(myItem);

        myItem = new HomeListItem();
        myItem.setName("Samsung Stores");
        myItem.setLocalIcon(R.drawable.icon_1);
        myItem.setLocalImage(R.drawable.thumb_1);
        myItem.setType(1);
        itemList.add(myItem);

        myItem = new HomeListItem();
        myItem.setTitle("The Fashion Trends");
        myItem.setDesc("Handmade in Italy, Now in India");
        myItem.setCategory("Fashion");
        myItem.setLocalImage(R.drawable.thumb_2);
        myItem.setType(2);
        itemList.add(myItem);

        myItem = new HomeListItem();
        myItem.setName("Grocery");
        myItem.setTitle("Big Grocery Sale !!");
        myItem.setLocalIcon(R.drawable.icon_1);
        myItem.setLocalImage(R.drawable.thumb_3);
        myItem.setType(1);
        itemList.add(myItem);

        myItem = new HomeListItem();
        myItem.setTitle("15 December - 16 December");
        myItem.setDesc("Festive Sales");
        myItem.setType(0);
        itemList.add(myItem);

        myItem = new HomeListItem();
        myItem.setTitle("Sports Store");
        myItem.setLocalImage(R.drawable.thumb_4);
        myItem.setType(3);
        myItem.setWidth(MIN_WIDTH);
        myItem.setHeight(MIN_HEIGHT);
        itemList.add(myItem);

        myItem = new HomeListItem();
        myItem.setTitle("Books and Toys");
        myItem.setLocalImage(R.drawable.thumb_5);
        myItem.setType(3);
        myItem.setWidth(MAX_WIDTH);
        myItem.setHeight(MAX_HEIGHT);
        itemList.add(myItem);

        myItem = new HomeListItem();
        myItem.setTitle("Top Sunglasses");
        myItem.setLocalImage(R.drawable.thumb_6);
        myItem.setType(3);
        myItem.setWidth(MAX_WIDTH);
        myItem.setHeight(MAX_HEIGHT);
        itemList.add(myItem);

        myItem = new HomeListItem();
        myItem.setTitle("Fashion Makeups");
        myItem.setLocalImage(R.drawable.thumb_7);
        myItem.setType(3);
        myItem.setWidth(MAX_WIDTH);
        myItem.setHeight(MAX_HEIGHT);
        itemList.add(myItem);

        myItem = new HomeListItem();
        myItem.setTitle("MotoBikes");
        myItem.setLocalImage(R.drawable.thumb_8);
        myItem.setType(3);
        myItem.setWidth(MAX_WIDTH);
        myItem.setHeight(MAX_HEIGHT);
        itemList.add(myItem);

        myItem = new HomeListItem();
        myItem.setTitle("Swimwear and Lingerie");
        myItem.setLocalImage(R.drawable.thumb_9);
        myItem.setType(3);
        myItem.setWidth(MIN_WIDTH);
        myItem.setHeight(MIN_HEIGHT);
        itemList.add(myItem);

        myItem = new HomeListItem();
        myItem.setTitle("Big Discount in Titan Watches");
        myItem.setDesc("Titan Stores");
        myItem.setCategory("Watches");
        myItem.setLocalImage(R.drawable.thumb_11);
        myItem.setType(4);
        itemList.add(myItem);

        myItem = new HomeListItem();
        myItem.setTitle("10% Discount on Camera And HandyCams");
        myItem.setDesc("Sony Stores");
        myItem.setCategory("Camera And HandyCams");
        myItem.setLocalImage(R.drawable.thumb_12);
        myItem.setType(4);
        itemList.add(myItem);

        myItem = new HomeListItem();
        myItem.setTitle("Upto 30% discount in furnitures and other products");
        myItem.setDesc("Home Town Stores");
        myItem.setCategory("Home Furnishing");
        myItem.setLocalImage(R.drawable.thumb_13);
        myItem.setType(4);
        itemList.add(myItem);

        swipeRefreshLayout=findViewById(R.id.swipe_refresh);
        progressBar=findViewById(R.id.progress_bar);
        textViewError = findViewById(R.id.text_error);
        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
       // RecyclerView.LayoutManager layoutManagerHomeMenu=new LinearLayoutManager(this);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
      /*  int resId = R.anim.layout_animation_slide_from_bottom;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this, resId);
        recyclerView.setLayoutAnimation(animation);*/
        myItemAdapter=new MyItemAdapter(this,itemList,"homeList");
        recyclerView.setAdapter(myItemAdapter);

        if(itemList.size() == 0){
            showNoData(true);
        }

        initFooter(this,0);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                getItemList();
            }
        });
        volleyRequest();
    }

    private void getItemList(){
        swipeRefreshLayout.setRefreshing(false);
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

    private void showProgressBar(boolean show){
        if(show){
            recyclerView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            textViewError.setVisibility(View.GONE);
        }else{
            recyclerView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }


    public void volleyRequest(){
        Map<String,String> params=new HashMap<>();
        params.put("action","4");

        String url=getResources().getString(R.string.url_offer)+"manageShoppursOffer";
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"manageShoppursOffer");
    }


    @Override
    public void onJsonObjectResponse(JSONObject response, String apiName) {
        showProgress(false);
        try {
            // JSONObject jsonObject=response.getJSONObject("response");
            Log.d("response", response.toString());
            if(apiName.equals("manageShoppursOffer")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)){

                    JSONArray offerArray = response.getJSONArray("result");
                    Log.d("offerArray size ", offerArray.length() +"");
                    for(int i=0;i<offerArray.length();i++){
                        HomeListItem myItem = new HomeListItem();
                        myItem.setTitle(offerArray.getJSONObject(i).getString("offerName"));
                        //myItem.setTitle("The Fashion Trends");
                        myItem.setDesc("Handmade in Italy, Now in India");
                        myItem.setCategory("Fashion");
                        myItem.setLocalImage(R.drawable.thumb_2);
                        myItem.setType(2);
                        itemList.add(myItem);
                        //{"status":true,"message":"Offer Fetched successfully.","result":[{"offerId":1,"action":0,"productId":0,"offerNumber":"Offer 1","offerName":"Test Offer","startDate":"2019-03-12 00:00:00","endDate":"2019-03-20 00:00:00","status":"Y","type":"shoppurs","createBy":"suraj","updatedBy":"suraj","createdDate":"2019-03-12 12:20:43","updatedDate":"2019-03-12 12:20:43","dbname":null,"percentage":1.5,"amount":100},{"offerId":2,"action":0,"productId":0,"offerNumber":"Offer 1","offerName":"Test Offer","startDate":"2019-03-12 00:00:00","endDate":"2019-03-20 00:00:00","status":"Y","type":"shoppurs","createBy":"suraj","updatedBy":"suraj","createdDate":"2019-03-12 14:35:01","updatedDate":"2019-03-12 14:35:01","dbname":null,"percentage":1.5,"amount":100},{"offerId":3,"action":0,"productId":0,"offerNumber":"Offer 1","offerName":"Test Offer","startDate":"2019-03-12 00:00:00","endDate":"2019-03-20 00:00:00","status":"Y","type":"shoppurs","createBy":"suraj","updatedBy":"suraj","createdDate":"2019-03-12 14:35:09","updatedDate":"2019-03-12 14:35:09","dbname":null,"percentage":1.5,"amount":100},{"offerId":4,"action":0,"productId":0,"offerNumber":"Offer 1","offerName":"Test Offer","startDate":"2019-03-12 00:00:00","endDate":"2019-03-20 00:00:00","status":"Y","type":"shoppurs","createBy":"suraj","updatedBy":"suraj","createdDate":"2019-03-12 14:36:07","updatedDate":"2019-03-12 14:36:07","dbname":null,"percentage":1.5,"amount":100}]}
                    }

                    if(itemList.size()>0){
                        myItemAdapter.notifyDataSetChanged();
                    }else {
                        showNoData(true);
                    }

                }else {
                  //  DialogAndToast.showToast(response.getString("message"),MainActivity.this);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            DialogAndToast.showToast(getResources().getString(R.string.json_parser_error)+e.toString(),MainActivity.this);
        }
    }

}
