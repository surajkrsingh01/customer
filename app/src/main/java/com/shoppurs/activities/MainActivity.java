package com.shoppurs.activities;

import android.content.Intent;
import android.net.Uri;
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
import com.shoppurs.R;
import com.shoppurs.activities.Settings.SettingActivity;
import com.shoppurs.adapters.MyItemAdapter;
import com.shoppurs.models.HomeListItem;
import com.shoppurs.models.MyShop;
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
    private boolean bannerLoaded, categoryLoaded, shopLoaded;

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
        /*HomeListItem myItem = new HomeListItem();

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
        itemList.add(myItem);*/

       /* myItem = new HomeListItem();
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
        itemList.add(myItem);*/

       /* myItem = new HomeListItem();
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
        itemList.add(myItem);*/

        swipeRefreshLayout=findViewById(R.id.swipe_refresh);
        progressBar=findViewById(R.id.progress_bar);
        textViewError = findViewById(R.id.text_error);
        recyclerView=findViewById(R.id.recycler_view);
       // RecyclerView.LayoutManager layoutManagerHomeMenu=new LinearLayoutManager(this);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
      /*  int resId = R.anim.layout_animation_slide_from_bottom;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this, resId);
        recyclerView.setLayoutAnimation(animation);*/
        myItemAdapter=new MyItemAdapter(this,itemList,"homeList");
        recyclerView.setAdapter(myItemAdapter);

        initFooter(this,0);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                getItemList();
            }
        });
        getBanners();
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

    public void getBanners(){
        Map<String,String> params=new HashMap<>();
        params.put("lattitude", sharedPreferences.getString(Constants.CUST_CURRENT_LAT,""));
        params.put("longitude", sharedPreferences.getString(Constants.CUST_CURRENT_LONG,""));
        params.put("dbName",sharedPreferences.getString(Constants.DB_NAME, ""));
        String url=getResources().getString(R.string.url_offer)+"get_banner_offers";
       // showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"offer_banners");
    }

    public void getCategories(){
        Map<String,String> params=new HashMap<>();
        params.put("lattitude", sharedPreferences.getString(Constants.CUST_CURRENT_LAT,""));
        params.put("longitude", sharedPreferences.getString(Constants.CUST_CURRENT_LONG,""));
        params.put("dbName",sharedPreferences.getString(Constants.DB_NAME, ""));
        String url=getResources().getString(R.string.url_offer)+"get_active_offer_categories";
        //showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"offer_categories");
    }

    public void getShops(){
        Map<String,String> params=new HashMap<>();
        params.put("lattitude", sharedPreferences.getString(Constants.CUST_CURRENT_LAT,""));
        params.put("longitude", sharedPreferences.getString(Constants.CUST_CURRENT_LONG,""));
        params.put("dbName",sharedPreferences.getString(Constants.DB_NAME, ""));
        String url=getResources().getString(R.string.url_offer)+"get_active_offer_shops";
        //showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"offer_shops");
    }


    @Override
    public void onJsonObjectResponse(JSONObject response, String apiName) {
        if(apiName.equals("offer_shops"))
        showProgress(false);
        try {
            // JSONObject jsonObject=response.getJSONObject("response");
            Log.d("response", response.toString());
            if (apiName.equals("offer_banners")) {
                if (response.getBoolean("status")) {
                    JSONArray dataArray = response.getJSONArray("result");
                    JSONObject jsonObject = null;
                    int len = dataArray.length();
                    HomeListItem myItem = null,homeListItem = null;
                    List<Object> homeListItems;
                    int position = 0,prePosition = 0;
                    for (int i = 0; i < len; i++) {
                        jsonObject = dataArray.getJSONObject(i);
                        position = getBannerPosition(jsonObject.getString("name"));
                        if(position >itemList.size()){
                            homeListItem = new HomeListItem();
                            homeListItem.setType(5);
                            homeListItems = new ArrayList<>();
                            myItem = new HomeListItem();
                            myItem.setId(jsonObject.getString("id"));
                            myItem.setName(jsonObject.getString("name"));
                            myItem.setImage(jsonObject.getString("image"));
                            myItem.setDesc(jsonObject.getString("desc"));
                            myItem.setType(1);
                            homeListItems.add(myItem);
                            homeListItem.setItemList(homeListItems);
                            prePosition = position;
                            itemList.add(homeListItem);
                        }else{
                            homeListItem = (HomeListItem) itemList.get(position-1);
                            myItem = new HomeListItem();
                            myItem.setId(jsonObject.getString("id"));
                            myItem.setName(jsonObject.getString("name"));
                            myItem.setImage(jsonObject.getString("image"));
                            myItem.setDesc(jsonObject.getString("desc"));
                            myItem.setType(1);
                            homeListItem.getItemList().add(myItem);
                        }
                    }

                    //myItemAdapter.notifyDataSetChanged();
                }

                getCategories();

            }
            else if(apiName.equals("offer_categories")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)){
                    JSONArray jsonArray = response.getJSONArray("result");
                    HomeListItem myItem = new HomeListItem();
                    myItem.setTitle("Offers");
                    myItem.setDesc("Category Offers");
                    myItem.setType(0);
                    itemList.add(myItem);
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        if (i < 4) {
                            myItem = new HomeListItem();
                            myItem.setId(jsonObject.getString("catId"));
                            myItem.setTitle(jsonObject.getString("catName"));
                            myItem.setImage(jsonObject.getString("imageUrl"));
                            myItem.setType(3);
                            if (jsonArray.length() == 2) {
                                myItem.setWidth(MIN_WIDTH);
                                myItem.setHeight(MIN_WIDTH);
                            } else if (jsonArray.length() > 3 && i == 0 || jsonArray.length() > 3 && i == 3) {
                                myItem.setWidth(MIN_WIDTH);
                                myItem.setHeight(MIN_WIDTH);
                            } else {
                                myItem.setWidth(MAX_WIDTH);
                                myItem.setHeight(MAX_HEIGHT);
                            }
                            itemList.add(myItem);
                        }
                    }
                    Log.d("catList size ", itemList.size() + "");
                    if(itemList.size()>0) {
                        //myItemAdapter.notifyDataSetChanged();

                    }else{
                        //showProgress(false);
                    }

                    getShops();
                }else {
                    DialogAndToast.showDialog(response.getString("message"),MainActivity.this);
                    showProgress(false);
                }
            }else if(apiName.equals("offer_shops")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)){
                    showProgress(false);
                    if(!response.getString("result").equals("null")) {
                        JSONArray shopJArray = response.getJSONArray("result");
                        if(shopJArray.length()>0){
                        HomeListItem myItem = new HomeListItem();
                        myItem.setTitle("Offers");
                        myItem.setDesc("Store Offers");
                        myItem.setType(0);
                        itemList.add(myItem);
                        for (int i = 0; i < shopJArray.length(); i++) {
                            MyShop myShop = new MyShop();
                            String shop_code = shopJArray.getJSONObject(i).getString("retcode");
                            myShop.setCode(shop_code);
                            Log.d("shop_code", myShop.getCode());
                            myShop.setName(shopJArray.getJSONObject(i).getString("retshopname"));
                            myShop.setMobile(shopJArray.getJSONObject(i).getString("retmobile"));
                            myShop.setAddress(shopJArray.getJSONObject(i).getString("retaddress"));
                            myShop.setState(shopJArray.getJSONObject(i).getString("retcountry"));
                            myShop.setCity(shopJArray.getJSONObject(i).getString("retcity"));
                            myShop.setShopimage(shopJArray.getJSONObject(i).getString("retphoto"));
                            myShop.setLatitude(shopJArray.getJSONObject(i).getDouble("retLat"));
                            myShop.setLongitude(shopJArray.getJSONObject(i).getDouble("retLong"));
                            myShop.setDeliveryAvailable(shopJArray.getJSONObject(i).getString("isDeliveryAvailable"));
                            myShop.setMinDeliveryAmount(shopJArray.getJSONObject(i).getDouble("minDeliveryAmount"));

                            myShop.setMinDeliverytime(shopJArray.getJSONObject(i).getString("minDeliverytime"));
                            myShop.setMinDeliverydistance(shopJArray.getJSONObject(i).getInt("minDeliverydistance"));
                            myShop.setChargesAfterMinDistance(shopJArray.getJSONObject(i).getDouble("chargesAfterMinDistance"));

                            myShop.setDbname(shopJArray.getJSONObject(i).getString("dbname"));
                            myShop.setDbusername(shopJArray.getJSONObject(i).getString("dbuser"));
                            myShop.setDbpassword(shopJArray.getJSONObject(i).getString("dbpassword"));
                            myShop.setImage(R.drawable.thumb_21);
                            itemList.add(myShop);
                        }
                        //myItemAdapter.notifyDataSetChanged();
                        Log.d("itemList Size ", itemList.size() + " ");

                        if (itemList.size() > 0) {
                            if (shopJArray.length() == 0)
                                itemList.remove(itemList.size() - 1);
                            myItemAdapter.notifyDataSetChanged();
                        }
                    }
                        if(itemList.size()>0)
                            myItemAdapter.notifyDataSetChanged();
                    }else {
                        //DialogAndToast.showDialog(response.getString("message"),StoresListActivity.this);
                        showProgress(false);
                        myItemAdapter.notifyDataSetChanged();
                    }

                }else {
                    DialogAndToast.showDialog(response.getString("message"),MainActivity.this);
                    showProgress(false);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            DialogAndToast.showToast(getResources().getString(R.string.json_parser_error)+e.toString(),MainActivity.this);
        }
    }

    private int getBannerPosition(String name){
        int position = Integer.parseInt(name.split("_")[0]);
        return position;
    }

    public void makeCall(String mobile){
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:"+mobile));
        if(Utility.verifyCallPhonePermissions(this))
            startActivity(callIntent);
    }

    public void openWhatsApp(String mobile){
        try {
            mobile = "91"+mobile;
            String text = "This is a test";// Replace with your message.
            //   String toNumber = "xxxxxxxxxx"; // Replace with mobile phone number without +Sign or leading zeros, but with country code
            //Suppose your country is India and your phone number is “xxxxxxxxxx”, then you need to send “91xxxxxxxxxx”.
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://api.whatsapp.com/send?phone="+mobile +"&text="));
            startActivity(intent);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public void showLargeImageDialog(MyShop shop,  View view){
        showImageDialog(shop.getShopimage(), view);
    }

}
