package com.shoppurs.activities.Settings;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.shoppurs.R;
import com.shoppurs.activities.HandleCartActivity;
import com.shoppurs.activities.ManageToDoListProducts;
import com.shoppurs.activities.ProductDetailActivity;
import com.shoppurs.adapters.ToDoProductListAdapter;
import com.shoppurs.database.DbHelper;
import com.shoppurs.fragments.BottomSearchProductsFragment;
import com.shoppurs.fragments.OfferDescriptionFragment;
import com.shoppurs.interfaces.MyItemClickListener;
import com.shoppurs.models.MyProduct;
import com.shoppurs.models.MyToDo;
import com.shoppurs.models.ShopDeliveryModel;
import com.shoppurs.utilities.Constants;
import com.shoppurs.utilities.DialogAndToast;
import com.shoppurs.utilities.Utility;

import java.util.ArrayList;
import java.util.List;

public class ToDoListDetailsActivity extends ManageToDoListProducts implements MyItemClickListener{

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private TextView tvNoData;
    private ProgressBar progressBar;
    private ToDoProductListAdapter toDoProductListAdapter;
    private String shopCode, shopName,shopAddress, shopdbname,dbuser,dbpassword;
    private List<MyProduct> myProductList;
    private ImageView searchView;
    private Typeface typeface;
    private int position, type;
    private ShopDeliveryModel shopDeliveryModel;
    private String custCode, custdbname;
    private RelativeLayout relative_footer_action;
    private MyToDo myToDo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_product_list);
        typeface = Utility.getSimpleLineIconsFont(this);
        dbHelper = new DbHelper(this);
        shopCode = getIntent().getStringExtra("shop_code");
        shopName = getIntent().getStringExtra("name");
        shopAddress = getIntent().getStringExtra("address");
        shopdbname = getIntent().getStringExtra("dbname");
        dbuser = getIntent().getStringExtra("dbuser");
        dbpassword = getIntent().getStringExtra("dbpassword");
        shopDeliveryModel = new ShopDeliveryModel();
        shopDeliveryModel = (ShopDeliveryModel) getIntent().getSerializableExtra("shopDeliveryModel");
        myToDo = (MyToDo) getIntent().getSerializableExtra("todo_item");
        custCode = sharedPreferences.getString(Constants.USER_ID,"");
        custdbname = sharedPreferences.getString(Constants.DB_NAME, "");
        initViews();
    }

    private void initViews(){
        searchView = findViewById(R.id.image_search);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSearchProductsFragment bottomSearchProductsFragment = new BottomSearchProductsFragment();
                bottomSearchProductsFragment.initParams(sharedPreferences, isDarkTheme, typeface, shopCode, "","", "ToDo List");
                bottomSearchProductsFragment.setMyItemClickListener(ToDoListDetailsActivity.this);
                bottomSearchProductsFragment.show(getSupportFragmentManager(), bottomSearchProductsFragment.getTag());
            }
        });
        TextView text_left_label = findViewById(R.id.text_left_label);
        TextView text_right_label = findViewById(R.id.text_right_label);
        text_right_label.setText(myToDo.getName());
        TextView text_shop_name = findViewById(R.id.text_shop_name);
        text_shop_name.setText(shopName);
        text_left_label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        swipeRefreshLayout =findViewById(R.id.swipe_refresh);
        recyclerView = findViewById(R.id.recycler_viewProduct);
        tvNoData = findViewById(R.id.tvNoData);
        progressBar=findViewById(R.id.progress_bar);
        relative_footer_action = findViewById(R.id.relative_footer_action);
        relative_footer_action.setBackgroundColor(colorTheme);
        TextView tvAction = findViewById(R.id.text_action);
        tvAction.setTextColor(Color.WHITE);
        tvAction.setText("Add ToDo List to Cart");
        relative_footer_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogAndToast.showToast("please wait..", ToDoListDetailsActivity.this);
                //init(2, shopCode, myProductList, shopDeliveryModel, ToDoListDetailsActivity.this);
            }
        });

        myProductList = new ArrayList<>();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        toDoProductListAdapter = new ToDoProductListAdapter(this, myProductList, shopCode, isDarkTheme, typeface);
        toDoProductListAdapter.setItemClickListener(this);
        recyclerView.setAdapter(toDoProductListAdapter);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getToList();
            }
        });
        getToList();
    }

    private void getToList(){
        myProductList.clear();
        if(swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
        myProductList.addAll(dbHelper.getToDoListProducts(shopCode, myToDo.getId()));
        Log.d("List Size ", ""+myProductList.size());
        if(myProductList.size()==0){
            showNoData(true);
        }else {
            showNoData(false);
            toDoProductListAdapter.notifyDataSetChanged();
        }
    }

    private void showNoData(boolean show){
        if(show){
            recyclerView.setVisibility(View.GONE);
            tvNoData.setVisibility(View.VISIBLE);
            relative_footer_action.setVisibility(View.GONE);
        }else{
            recyclerView.setVisibility(View.VISIBLE);
            tvNoData.setVisibility(View.GONE);
            relative_footer_action.setVisibility(View.VISIBLE);
        }
    }

    private void showProgressBar(boolean show, String type){
        if(show){
            if(type.equals("RefreshList"))
                recyclerView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            tvNoData.setVisibility(View.GONE);
        }else{
            recyclerView.setVisibility(View.VISIBLE);
            tvNoData.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
    }


    public void showLargeImageDialog(MyProduct product, View view){
        showImageDialog(product.getProdImage1(),view);
    }

    public void showProductDetails(MyProduct product){
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra("MyProduct",product);
        startActivity(intent);
    }

    public void showOfferDescription(MyProduct item){
        OfferDescriptionFragment offerDescriptionFragment = new OfferDescriptionFragment();
        offerDescriptionFragment.setProduct(item);
        offerDescriptionFragment.setColorTheme(colorTheme);
        offerDescriptionFragment.show(getSupportFragmentManager(), "Offer Description Bottom Sheet");
    }

    public void updateCart(int type, int position){
        Log.d("clicked Position ", position+"");
        this.position = position;
        this.type = type;
        //updateCart(type, position, shopCode, myProductList.get(position), shopDeliveryModel, this);
    }

    @Override
    public void onItemClicked(int pos) {

    }

    @Override
    public void onProductSearch(MyProduct myProduct) {
       // DialogAndToast.showToast(myProduct.getName() + " Selected ", this);
        myProductList.add(myProduct);
        showNoData(false);
        toDoProductListAdapter.notifyItemInserted(myProductList.size()-1);
        dbHelper.addProductInTODOList(myProduct, myToDo.getId());
    }

    @Override
    public void onItemClicked(int pos, String type) {
        MyProduct product = myProductList.get(pos);
        //DialogAndToast.showDialog(product.getName() +" "+type, this);

        if(type.equals("showOffer"))
            showOfferDescription(product);
        else if(type.equals("showDetails"))
            showProductDetails(product);
        else if(type.equals("remove")) {
            dbHelper.removeProductFromTODoList(product.getId(), shopCode);
            myProductList.remove(pos);
            toDoProductListAdapter.notifyItemRemoved(pos);
        }
    }

    public void showProgress(boolean show){
        if(show){
            progressDialog.show();
        }else{
            progressDialog.dismiss();
        }
    }
}
