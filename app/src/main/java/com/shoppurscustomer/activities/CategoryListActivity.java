package com.shoppurscustomer.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.shoppurscustomer.R;
import com.shoppurscustomer.adapters.CategoryAdapter;
import com.shoppurscustomer.models.CatListItem;
import com.shoppurscustomer.models.Category;
import com.shoppurscustomer.models.SubCategory;
import com.shoppurscustomer.utilities.DialogAndToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryListActivity extends NetworkBaseActivity {

    private RecyclerView recyclerView;
    private CategoryAdapter myItemAdapter;
    private List<Object> itemList;
    private TextView textViewError;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private boolean isCategoriesLoaded, isSubcategoriesLoaded;

    private float MIN_WIDTH = 200,MIN_HEIGHT = 230,MAX_WIDTH = 200,MAX_HEIGHT = 290;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        isCategoriesLoaded =false;
        isSubcategoriesLoaded =false;
        itemList = new ArrayList<>();
        /*
        CatListItem myItem = new CatListItem();
        myItem.setTitle("Products");
        myItem.setDesc("Categories");
        myItem.setType(0);
        List<Object> catList = new ArrayList<>();

        Category category = new Category();
        category.setLocalImage(R.drawable.thumb_17);
        category.setName("Grocery");
        catList.add(category);

        category = new Category();
        category.setLocalImage(R.drawable.thumb_13);
        category.setName("Jewellery");
        catList.add(category);

        category = new Category();
        category.setLocalImage(R.drawable.thumb_16);
        category.setName("Restaurants");
        catList.add(category);

        category = new Category();
        category.setName("Electronics");
        category.setLocalImage(R.drawable.thumb_11);
        catList.add(category);

        category = new Category();
        category.setName("Fashion");
        category.setLocalImage(R.drawable.thumb_14);
        catList.add(category);

        category = new Category();
        category.setName("Stationary");
        category.setLocalImage(R.drawable.thumb_15);
        catList.add(category);
        myItem.setItemList(catList);
        itemList.add(myItem);

        myItem = new CatListItem();
        myItem.setTitle("Grocery");
        myItem.setType(1);
        List<Object> prodList = new ArrayList<>();
        SubCategory subCategory = new SubCategory();
        subCategory.setName("Breakfast & Dairy");
        subCategory.setLocalImage(R.drawable.thumb_16);
        subCategory.setWidth(MIN_WIDTH);
        subCategory.setHeight(MIN_HEIGHT);
        prodList.add(subCategory);
        subCategory = new SubCategory();
        subCategory.setName("Masala & Spices");
        subCategory.setLocalImage(R.drawable.thumb_17);
        subCategory.setWidth(MAX_WIDTH);
        subCategory.setHeight(MAX_HEIGHT);
        prodList.add(subCategory);
        subCategory = new SubCategory();
        subCategory.setName("Personal Care");
        subCategory.setLocalImage(R.drawable.thumb_18);
        subCategory.setWidth(MAX_WIDTH);
        subCategory.setHeight(MAX_HEIGHT);
        prodList.add(subCategory);
        subCategory = new SubCategory();
        subCategory.setName("Beverages");
        subCategory.setLocalImage(R.drawable.thumb_19);
        subCategory.setWidth(MIN_WIDTH);
        subCategory.setHeight(MIN_HEIGHT);
        prodList.add(subCategory);
        myItem.setItemList(prodList);
        itemList.add(myItem);

        myItem = new CatListItem();
        myItem.setTitle("Stationary");
        myItem.setType(1);
        prodList = new ArrayList<>();
        subCategory = new SubCategory();
        subCategory.setName("Pen & Pen Sets");
        subCategory.setLocalImage(R.drawable.thumb_20);
        subCategory.setWidth(MIN_WIDTH);
        subCategory.setHeight(MIN_HEIGHT);
        prodList.add(subCategory);
        subCategory = new SubCategory();
        subCategory.setName("Notebooks");
        subCategory.setLocalImage(R.drawable.thumb_21);
        subCategory.setWidth(MAX_WIDTH);
        subCategory.setHeight(MAX_HEIGHT);
        prodList.add(subCategory);
        subCategory = new SubCategory();
        subCategory.setName("Papers");
        subCategory.setLocalImage(R.drawable.thumb_22);
        subCategory.setWidth(MAX_WIDTH);
        subCategory.setHeight(MAX_HEIGHT);
        prodList.add(subCategory);
        subCategory = new SubCategory();
        subCategory.setName("Color & Paints");
        subCategory.setLocalImage(R.drawable.thumb_23);
        subCategory.setWidth(MAX_WIDTH);
        subCategory.setHeight(MAX_HEIGHT);
        prodList.add(subCategory);
        subCategory = new SubCategory();
        subCategory.setName("Desk Organizer");
        subCategory.setLocalImage(R.drawable.thumb_24);
        subCategory.setWidth(MAX_WIDTH);
        subCategory.setHeight(MAX_HEIGHT);
        prodList.add(subCategory);
        subCategory = new SubCategory();
        subCategory.setName("Markers");
        subCategory.setLocalImage(R.drawable.thumb_25);
        subCategory.setWidth(MIN_WIDTH);
        subCategory.setHeight(MIN_HEIGHT);
        prodList.add(subCategory);
        myItem.setItemList(prodList);
        itemList.add(myItem);*/

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
            recyclerView.setAdapter(myItemAdapter);

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
            window.setStatusBarColor(getResources().getColor(R.color.blue700));
        }

        initFooter(this,1);

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
        String url=getResources().getString(R.string.url)+"/cat_subcat";
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(),"categories");
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
                                SubCategory subCategory = new SubCategory();
                                subCategory.setSubcatid(subcatJArray.getJSONObject(j).getString("subCatId"));
                                subCategory.setId(subcatJArray.getJSONObject(j).getString("catId"));
                                Log.d("mCATID",subCategory.getId());
                                subCategory.setName(subcatJArray.getJSONObject(j).getString("subCatName"));
                                subCategory.setImage(subcatJArray.getJSONObject(j).getString("subCatImage"));

                                subCategory.setWidth(MAX_WIDTH);
                                subCategory.setHeight(MAX_HEIGHT);
                                subcatList.add(subCategory);
                                myItem.setItemList(subcatList);
                                Log.d(TAG, subcatList.size() + "");

                                if (j == subcatJArray.length() - 1) {
                                    ((SubCategory) subcatList.get(0)).setWidth(MIN_WIDTH);
                                    ((SubCategory) subcatList.get(0)).setHeight(MIN_HEIGHT);
                                    ((SubCategory) subcatList.get(subcatList.size() - 1)).setWidth(MIN_WIDTH);
                                    ((SubCategory) subcatList.get(subcatList.size() - 1)).setHeight(MIN_HEIGHT);
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
