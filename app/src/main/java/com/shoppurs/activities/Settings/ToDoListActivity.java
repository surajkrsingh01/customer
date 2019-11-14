package com.shoppurs.activities.Settings;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.shoppurs.R;
import com.shoppurs.activities.HandleCartActivity;
import com.shoppurs.activities.NetworkBaseActivity;
import com.shoppurs.activities.ProductDetailActivity;
import com.shoppurs.activities.ShopProductListActivity;
import com.shoppurs.adapters.ReturnProductListAdapter;
import com.shoppurs.adapters.ToDoProductListAdapter;
import com.shoppurs.database.DbHelper;
import com.shoppurs.fragments.BottomSearchFragment;
import com.shoppurs.fragments.BottomSearchProductsFragment;
import com.shoppurs.fragments.OfferDescriptionFragment;
import com.shoppurs.interfaces.MyItemClickListener;
import com.shoppurs.interfaces.MyItemTypeClickListener;
import com.shoppurs.models.MyProduct;
import com.shoppurs.models.ShopDeliveryModel;
import com.shoppurs.utilities.Constants;
import com.shoppurs.utilities.DialogAndToast;
import com.shoppurs.utilities.Utility;

import java.util.ArrayList;
import java.util.List;

public class ToDoListActivity extends HandleCartActivity implements MyItemClickListener{

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);
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
                bottomSearchProductsFragment.setMyItemClickListener(ToDoListActivity.this);
                bottomSearchProductsFragment.show(getSupportFragmentManager(), bottomSearchProductsFragment.getTag());
            }
        });
        TextView text_left_label = findViewById(R.id.text_left_label);
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
        myProductList.addAll(dbHelper.getToDoListProducts(shopCode));
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
        }else{
            recyclerView.setVisibility(View.VISIBLE);
            tvNoData.setVisibility(View.GONE);
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
        updateCart(type, position, shopCode, myProductList.get(position), shopDeliveryModel, this);
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
        dbHelper.addProductInTODOList(myProduct);
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

    @Override
    public void updateUi(MyProduct product, int position) {
        Log.d("return position ", position+"");
        Log.d("return product ", product.getId()+"");
        myProductList.set(position, product);
        toDoProductListAdapter.notifyItemChanged(position);
        toDoProductListAdapter.notifyDataSetChanged();
       // updateCartCount();
    }
}
