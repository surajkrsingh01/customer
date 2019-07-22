package com.shoppurscustomer.activities;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import com.google.android.material.appbar.AppBarLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.shoppurscustomer.R;
import com.shoppurscustomer.adapters.PopulatCategoriesAdapter;
import com.shoppurscustomer.models.Category;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends NetworkBaseActivity{

    private Toolbar toolbar;
    private AppBarLayout appBarLayout;
    private RecyclerView recycler_popular_category, recycler_popular_tags;
    private List<Category> categoryList, tagList;
    private PopulatCategoriesAdapter populatCategoriesAdapter;
    private TextView cancel;
    private EditText et_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        initFooter(this,3);

        init();
    }

    private void init(){
        et_search = findViewById(R.id.et_search);
        ((GradientDrawable)et_search.getBackground()).setColor(colorTheme);
        cancel = findViewById(R.id.cancel);
        recycler_popular_category = findViewById(R.id.recycler_popular_category);
        recycler_popular_tags =  findViewById(R.id.recycler_popular_tags);

        getTopCategories();
        getTopTags();

        recycler_popular_category.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        recycler_popular_category.setLayoutManager(layoutManager);
        recycler_popular_category.setItemAnimator(new DefaultItemAnimator());
        populatCategoriesAdapter = new PopulatCategoriesAdapter(this, categoryList);
        populatCategoriesAdapter.setTheme(isDarkTheme, colorTheme);
        recycler_popular_category.setAdapter(populatCategoriesAdapter);
        recycler_popular_category.setNestedScrollingEnabled(false);

        recycler_popular_tags.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        recycler_popular_tags.setLayoutManager(layoutManager1);
        recycler_popular_tags.setItemAnimator(new DefaultItemAnimator());
        populatCategoriesAdapter = new PopulatCategoriesAdapter(this, tagList);
        populatCategoriesAdapter.setTheme(isDarkTheme, colorTheme);
        recycler_popular_tags.setAdapter(populatCategoriesAdapter);
        recycler_popular_tags.setNestedScrollingEnabled(false);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_search.setText("");
                cancel.setVisibility(View.GONE);
            }
        });

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!TextUtils.isEmpty(s)){
                    cancel.setVisibility(View.VISIBLE);
                }else {
                    cancel.setVisibility(View.GONE);
                }
            }
        });
    }

    private void getTopCategories(){
        categoryList = new ArrayList<>();
        Category category;
        category = new Category();
        category.setName("Grocery");
        categoryList.add(category);

        category = new Category();
        category.setName("Stationary");
        categoryList.add(category);

        category = new Category();
        category.setName("Fashion");
        categoryList.add(category);

        category = new Category();
        category.setName("Electronics");
        categoryList.add(category);

        category = new Category();
        category.setName("Jewellery");
        categoryList.add(category);

        category = new Category();
        category.setName("Restaurants");
        categoryList.add(category);
    }

    private void getTopTags(){
        tagList = new ArrayList<>();
        Category category;
        category = new Category();
        category.setName("Restaurants Nearby");
        tagList.add(category);

        category = new Category();
        category.setName("Grocery Stores Nearby");
        tagList.add(category);

        category = new Category();
        category.setName("Fashoion Stores");
        tagList.add(category);

        category = new Category();
        category.setName("Toy Stores");
        tagList.add(category);

        category = new Category();
        category.setName("Stationary Stores");
        tagList.add(category);

        category = new Category();
        category.setName("Home Appliances Stores");
        tagList.add(category);
    }

    public void setEt_search(String search_value){
        et_search.setText(search_value);
    }
}
