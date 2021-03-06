package com.shoppurs.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.shoppurs.R;
import com.shoppurs.adapters.CategoryAdapter;
import com.shoppurs.models.HomeListItem;
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

public class SubCatListActivity extends NetworkBaseActivity {

    private RecyclerView recyclerView;
    private CategoryAdapter myItemAdapter;
    private List<Object> itemList;
    private TextView textViewError;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private String catName, catId;
    private TextView text_left_label, text_right_label, text_desc;

    private float MIN_WIDTH = 200,MIN_HEIGHT = 230,MAX_WIDTH = 200,MAX_HEIGHT = 290;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_cat_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        text_left_label = findViewById(R.id.text_left_label);
        text_right_label = findViewById(R.id.text_right_label);
        text_left_label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SubCatListActivity.this, CategoryListActivity.class));
                finish();
            }
        });

        text_desc = findViewById(R.id.text_desc);

        catName = getIntent().getStringExtra("catName");
        catId = getIntent().getStringExtra("catId");

        Log.i(TAG,"catName "+catName);
        Log.i(TAG,"catId "+catId);

        itemList = new ArrayList<>();
        HomeListItem myItem = new HomeListItem();
        //myItem.setTitle(catName);
        //myItem.setDesc(catName+" Products");
        itemList.add(myItem);

        text_right_label.setText(catName);
        text_desc.setText(catName+" Products");

        swipeRefreshLayout=findViewById(R.id.swipe_refresh);
        progressBar=findViewById(R.id.progress_bar);
        textViewError = findViewById(R.id.text_error);
        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
      /*  int resId = R.anim.layout_animation_slide_from_bottom;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this, resId);
        recyclerView.setLayoutAnimation(animation);*/
        myItemAdapter=new CategoryAdapter(this,itemList,"subCatList");
        recyclerView.setAdapter(myItemAdapter);

        if(itemList.size() == 0){
            showNoData(true);
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                getItemList();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
           // window.setStatusBarColor(getResources().getColor(R.color.blue700));
        }

        initFooter(this,1);
        setItemList();
    }

    private void getItemList(){
        swipeRefreshLayout.setRefreshing(false);
    }

    private void setItemList(){
        /*if(catName.equals("Grocery")){
            SubCategory subCategory = new SubCategory();
            subCategory.setName("Breakfast & Dairy");
            subCategory.setLocalImage(R.drawable.thumb_16);
            subCategory.setWidth(MIN_WIDTH);
            subCategory.setHeight(MIN_HEIGHT);
            itemList.add(subCategory);
            subCategory = new SubCategory();
            subCategory.setName("Masala & Spices");
            subCategory.setLocalImage(R.drawable.thumb_17);
            subCategory.setWidth(MAX_WIDTH);
            subCategory.setHeight(MAX_HEIGHT);
            itemList.add(subCategory);
            subCategory = new SubCategory();
            subCategory.setName("Personal Care");
            subCategory.setLocalImage(R.drawable.thumb_18);
            subCategory.setWidth(MAX_WIDTH);
            subCategory.setHeight(MAX_HEIGHT);
            itemList.add(subCategory);
            subCategory = new SubCategory();
            subCategory.setName("Beverages");
            subCategory.setLocalImage(R.drawable.thumb_19);
            subCategory.setWidth(MIN_WIDTH);
            subCategory.setHeight(MIN_HEIGHT);
            itemList.add(subCategory);
        }else{
            SubCategory subCategory = new SubCategory();
            subCategory.setName("Pen & Pen Sets");
            subCategory.setLocalImage(R.drawable.thumb_20);
            subCategory.setWidth(MIN_WIDTH);
            subCategory.setHeight(MIN_HEIGHT);
            itemList.add(subCategory);
            subCategory = new SubCategory();
            subCategory.setName("Notebooks");
            subCategory.setLocalImage(R.drawable.thumb_21);
            subCategory.setWidth(MAX_WIDTH);
            subCategory.setHeight(MAX_HEIGHT);
            itemList.add(subCategory);
            subCategory = new SubCategory();
            subCategory.setName("Papers");
            subCategory.setLocalImage(R.drawable.thumb_22);
            subCategory.setWidth(MAX_WIDTH);
            subCategory.setHeight(MAX_HEIGHT);
            itemList.add(subCategory);
            subCategory = new SubCategory();
            subCategory.setName("Color & Paints");
            subCategory.setLocalImage(R.drawable.thumb_23);
            subCategory.setWidth(MAX_WIDTH);
            subCategory.setHeight(MAX_HEIGHT);
            itemList.add(subCategory);
            subCategory = new SubCategory();
            subCategory.setName("Desk Organizer");
            subCategory.setLocalImage(R.drawable.thumb_24);
            subCategory.setWidth(MAX_WIDTH);
            subCategory.setHeight(MAX_HEIGHT);
            itemList.add(subCategory);
            subCategory = new SubCategory();
            subCategory.setName("Markers");
            subCategory.setLocalImage(R.drawable.thumb_25);
            subCategory.setWidth(MIN_WIDTH);
            subCategory.setHeight(MIN_HEIGHT);
            itemList.add(subCategory);
        }*/

        Map<String,String> params=new HashMap<>();
        params.put("id",catId);
        params.put("dbName", sharedPreferences.getString(Constants.DB_NAME, ""));

        String url=getResources().getString(R.string.url_customer)+"/api/customers/subcategories";
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"subcategories");
    }

    @Override
    public void onJsonObjectResponse(JSONObject response, String apiName) {
        showProgress(false);
        try {
            // JSONObject jsonObject=response.getJSONObject("response");
            Log.d("response", response.toString());
            if(apiName.equals("subcategories")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)){

                    JSONArray resultJArray = response.getJSONArray("result");
                        for (int j = 0; j < resultJArray.length(); j++) {
                            SubCategory subCategory = new SubCategory();
                            subCategory.setSubcatid(resultJArray.getJSONObject(j).getString("subCatId"));
                            subCategory.setId(resultJArray.getJSONObject(j).getString("catId"));
                            subCategory.setName(resultJArray.getJSONObject(j).getString("subCatName"));
                            subCategory.setImage(resultJArray.getJSONObject(j).getString("subCatImage"));

                            if (j == 0 || j == resultJArray.length() - 1) {
                                subCategory.setWidth(MIN_WIDTH);
                                subCategory.setHeight(MIN_HEIGHT);
                            } else {
                                subCategory.setWidth(MAX_WIDTH);
                                subCategory.setHeight(MAX_HEIGHT);
                            }
                            itemList.add(subCategory);
                        }

                    if(itemList.size()>0){
                        myItemAdapter.notifyDataSetChanged();
                    }else showNoData(true);

                }else {
                    DialogAndToast.showDialog(response.getString("message"),SubCatListActivity.this);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            showNoData(true);
            //DialogAndToast.showToast(getResources().getString(R.string.json_parser_error)+e.toString(),SubCatListActivity.this);
        }
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

}
