package com.shoppurs.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.shoppurs.R;
import com.shoppurs.activities.Settings.AddressActivity;
import com.shoppurs.activities.Settings.SettingActivity;
import com.shoppurs.adapters.CategoryAdapter;
import com.shoppurs.models.CatListItem;
import com.shoppurs.models.Category;
import com.shoppurs.models.SubCategory;
import com.shoppurs.utilities.Constants;
import com.shoppurs.utilities.DialogAndToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CategoryListActivity extends NetworkBaseActivity {

    private RecyclerView recyclerView;
    private CategoryAdapter myItemAdapter;
    private List<Object> itemList;
    private TextView textViewError;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private boolean isCategoriesLoaded, isSubcategoriesLoaded;
    private String dbname;
    private float MIN_WIDTH = 200,MIN_HEIGHT = 230,MAX_WIDTH = 200,MAX_HEIGHT = 290;
    private TextView text_customer_address;
    private CircleImageView customer_profile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        isCategoriesLoaded =false;
        isSubcategoriesLoaded =false;
        itemList = new ArrayList<>();
        dbname = sharedPreferences.getString(Constants.DB_NAME,"");


        swipeRefreshLayout=findViewById(R.id.swipe_refresh);
        progressBar=findViewById(R.id.progress_bar);
        textViewError = findViewById(R.id.text_error);
        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
      /*  int resId = R.anim.layout_animation_slide_from_bottom;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this, resId);
        recyclerView.setLayoutAnimation(animation);*/
            myItemAdapter = new CategoryAdapter(this, itemList, "catList");
             myItemAdapter.setColorTheme(colorTheme);
            recyclerView.setAdapter(myItemAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                getItemList();
            }
        });

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.blue700));
        }*/

        initFooter(this,1);

        text_customer_address = findViewById(R.id.text_customer_address);
        if(TextUtils.isEmpty(sharedPreferences.getString(Constants.CUST_ADDRESS, "")))
            text_customer_address.setText("Update Your Location");
        else
        text_customer_address.setText(sharedPreferences.getString(Constants.CUST_ADDRESS, ""));
        text_customer_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CategoryListActivity.this, AddressActivity.class);
                startActivity(intent);
            }
        });
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
                Intent intent = new Intent(CategoryListActivity.this, SettingActivity.class);
                startActivity(intent);
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
        params.put("dbName", dbname);
        String url=getResources().getString(R.string.url)+"/cat_subcat";
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"categories");
    }


    @Override
    public void onJsonObjectResponse(JSONObject response, String apiName) {
        showProgress(false);
        try {
            Log.d("response", response.toString());
            if(apiName.equals("categories")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)){
                    isCategoriesLoaded = true;
                    JSONObject jsonObject = response.getJSONObject("result");
                    JSONArray catJArray = jsonObject.getJSONArray("categories");

                    CatListItem myItem = new CatListItem();
                    myItem.setTitle("Products");
                    myItem.setDesc("Categories");
                    myItem.setType(0);
                    List<Object> catList = new ArrayList<>();

                    for(int i=0;i<catJArray.length();i++){
                        Category category = new Category();
                        category.setId(catJArray.getJSONObject(i).getString("catId"));
                        category.setName(catJArray.getJSONObject(i).getString("catName"));
                        category.setImage(catJArray.getJSONObject(i).getString("catImage"));

                        catList.add(category);
                        myItem.setItemList(catList);
                    }
                    itemList.add(myItem);
                    for(int i=0;i<catJArray.length();i++) {
                        JSONArray subcatJArray = catJArray.getJSONObject(i).getJSONArray("subCatList");
                        if(subcatJArray.length()>0){
                        myItem = new CatListItem();
                        myItem.setTitle(catJArray.getJSONObject(i).getString("catName"));
                        String mcatid =catJArray.getJSONObject(i).getString("catId");
                            Log.d("mCATID",mcatid);
                        myItem.setType(1);
                        List<Object> subcatList = new ArrayList<>();
                        for (int j=0; j<subcatJArray.length();j++) {
                            if (subcatJArray.getJSONObject(j).getString("catId").equals(mcatid)) {
                                if (subcatList.size() < 4) {
                                    SubCategory subCategory = new SubCategory();
                                    subCategory.setSubcatid(subcatJArray.getJSONObject(j).getString("subCatId"));
                                    subCategory.setId(subcatJArray.getJSONObject(j).getString("catId"));
                                    Log.d("mCATID", subCategory.getId());
                                    subCategory.setName(subcatJArray.getJSONObject(j).getString("subCatName"));
                                    subCategory.setImage(subcatJArray.getJSONObject(j).getString("subCatImage"));

                                    if(subcatJArray.length()==2){
                                        subCategory.setWidth(MIN_WIDTH);
                                        subCategory.setHeight(MIN_WIDTH);
                                    } else if (subcatJArray.length()>3 && j==0 || subcatJArray.length()>3 && j == 3) {
                                        subCategory.setWidth(MIN_WIDTH);
                                        subCategory.setHeight(MIN_WIDTH);
                                    }else {
                                        subCategory.setWidth(MAX_WIDTH);
                                        subCategory.setHeight(MAX_HEIGHT);
                                    }
                                    subcatList.add(subCategory);
                                    myItem.setItemList(subcatList);
                                    Log.d(TAG, subcatList.size() + "");


                                }
                            }
                        }
                        itemList.add(myItem);
                       }
                    }

                    if(itemList.size()>0){
                        myItemAdapter.notifyDataSetChanged();
                    }

                }else {
                    DialogAndToast.showDialog(response.getString("message"),CategoryListActivity.this);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            DialogAndToast.showToast(getResources().getString(R.string.json_parser_error)+e.toString(),CategoryListActivity.this);
        }
    }



}
