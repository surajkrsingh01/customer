package com.shoppurscustomer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shoppurscustomer.R;
import com.shoppurscustomer.adapters.MyItemAdapter;
import com.shoppurscustomer.adapters.StoreAdapter;
import com.shoppurscustomer.models.CatListItem;
import com.shoppurscustomer.models.Category;
import com.shoppurscustomer.models.HomeListItem;
import com.shoppurscustomer.models.SubCategory;

import java.util.ArrayList;
import java.util.List;

public class StoresListActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private StoreAdapter myItemAdapter;
    private List<Object> itemList;
    private TextView textViewError;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;

    private float MIN_WIDTH = 200,MIN_HEIGHT = 230,MAX_WIDTH = 200,MAX_HEIGHT = 290;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stores);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        itemList = new ArrayList<>();
        CatListItem myItem = new CatListItem();
        myItem.setTitle("NEAR BY");
        myItem.setDesc("Stores to Shop");
        myItem.setType(0);
        List<Object> catList = new ArrayList<>();

        Category category = new Category();
        category.setName("Scan");
        catList.add(category);

        category = new Category();
        category.setName("Yor grocery store just around the corner");
        category.setLocalImage(R.drawable.thumb_14);
        catList.add(category);

        category = new Category();
        category.setName("Your stationary store here");
        category.setLocalImage(R.drawable.thumb_15);
        catList.add(category);
        myItem.setItemList(catList);
        itemList.add(myItem);


        myItem = new CatListItem();
        List<Object> prodList = new ArrayList<>();
        myItem.setTitle("My Favourite Stores");
        myItem.setType(1);
        prodList = new ArrayList<>();

        HomeListItem myItem1 = new HomeListItem();
        myItem1.setTitle("Grocery Store, Kendriya Vihar, Sector 56 Gurgaon, Haryana");
        //myItem1.setDesc("Titan Stores");
        myItem1.setCategory("Raju Shop");
        myItem1.setLocalImage(R.drawable.thumb_11);
        myItem1.setType(8);
        prodList.add(myItem1);

        myItem1 = new HomeListItem();
        myItem1.setTitle("Grocery Store, Sector 57 Gurgaon, Haryana");
       // myItem1.setDesc("Sony Stores");
        myItem1.setCategory("24 Seven Store");
        myItem1.setLocalImage(R.drawable.thumb_12);
        myItem1.setType(8);
        prodList.add(myItem1);

        myItem1 = new HomeListItem();
        myItem1.setTitle("Grocery Store, Sector 57 Gurgaon, Haryana");
        //myItem1.setDesc("Home Town Stores");
        myItem1.setCategory("Spencer");
        myItem1.setLocalImage(R.drawable.thumb_13);
        myItem1.setType(8);
        prodList.add(myItem1);

        myItem.setItemList(prodList);
        itemList.add(myItem);

        myItem = new CatListItem();
        myItem.setTitle("My Favourite Restaurants");
        myItem.setType(1);
        prodList = new ArrayList<>();

        myItem1 = new HomeListItem();
        myItem1.setTitle("The Best Diet for a Flatter Belly");
        myItem1.setDesc("1 KMS");
        myItem1.setCategory("BIRYANI BLUES");
        myItem1.setLocalImage(R.drawable.thumb_11);
        myItem1.setType(8);
        prodList.add(myItem1);

        myItem1 = new HomeListItem();
        myItem1.setTitle("Were Vegetables Better Before?");
        myItem1.setDesc("1 KMS");
        myItem1.setCategory("AROGYA MEALS");
        myItem1.setLocalImage(R.drawable.thumb_12);
        myItem1.setType(8);
        prodList.add(myItem1);

        myItem1 = new HomeListItem();
        myItem1.setTitle("Here Are the Five Best Fruits of the");
        myItem1.setDesc("1 KMS");
        myItem1.setCategory("DANA CHOGA");
        myItem1.setLocalImage(R.drawable.thumb_13);
        myItem1.setType(8);
        prodList.add(myItem1);

        myItem.setItemList(prodList);
        itemList.add(myItem);


        myItem = new CatListItem();
        myItem.setTitle("Popular Stores");
        myItem.setType(1);
        prodList = new ArrayList<>();

        myItem1 = new HomeListItem();
        myItem1.setTitle("Pizza Hut");
        myItem1.setDesc("Sector 56, Gurgaon, Haryana");
        myItem1.setCategory("RESTAURANT");
        myItem1.setLocalImage(R.drawable.thumb_11);
        myItem1.setType(8);
        prodList.add(myItem1);

        myItem1 = new HomeListItem();
        myItem1.setTitle("Subway");
        myItem1.setDesc("Sector 56, Gurgaon, Haryana");
        myItem1.setCategory("RESTAURANT");
        myItem1.setLocalImage(R.drawable.thumb_12);
        myItem1.setType(8);
        prodList.add(myItem1);

        myItem1 = new HomeListItem();
        myItem1.setTitle("Sangam Bazar");
        myItem1.setDesc("Sector 57, Gurgaon, Haryana");
        myItem1.setCategory("GROCERY");
        myItem1.setLocalImage(R.drawable.thumb_13);
        myItem1.setType(8);
        prodList.add(myItem1);

        myItem.setItemList(prodList);
        itemList.add(myItem);

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
        myItemAdapter=new StoreAdapter(this,itemList,"catList");
        recyclerView.setAdapter(myItemAdapter);

        if(itemList.size() == 0){
            showNoData(true);
        }

        initFooter(this,2);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                getItemList();
            }
        });
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

    public void scanBarCode(){
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
        startActivityForResult(intent, 0);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                Log.d(TAG, contents);
                Log.d(TAG, format);
                // Handle successful scan
            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
            }
        }
    }

}
