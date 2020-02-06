package com.shoppurs.activities.Settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.shoppurs.R;
import com.shoppurs.activities.NetworkBaseActivity;
import com.shoppurs.adapters.ToDoListAdapter;
import com.shoppurs.adapters.ToDoProductListAdapter;
import com.shoppurs.database.DbHelper;
import com.shoppurs.fragments.BottomSearchProductsFragment;
import com.shoppurs.interfaces.MyItemClickListener;
import com.shoppurs.models.MyProduct;
import com.shoppurs.models.MyToDo;
import com.shoppurs.models.ShopDeliveryModel;
import com.shoppurs.utilities.Constants;
import com.shoppurs.utilities.DialogAndToast;
import com.shoppurs.utilities.Utility;

import java.util.ArrayList;
import java.util.List;

public class ToDoListActivity extends NetworkBaseActivity implements MyItemClickListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private TextView tvNoData;
    private ProgressBar progressBar;
    private ToDoListAdapter toDoListAdapter;
    private String shopCode, shopName,shopAddress, shopdbname,dbuser,dbpassword;
    private List<MyToDo> myToDoList;
    private ImageView searchView;
    private Typeface typeface;
    private int position, type;
    private ShopDeliveryModel shopDeliveryModel;
    private String custCode, custdbname;
    private FloatingActionButton add_todo_list;

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
        searchView.setVisibility(View.GONE);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSearchProductsFragment bottomSearchProductsFragment = new BottomSearchProductsFragment();
                bottomSearchProductsFragment.initParams(sharedPreferences, isDarkTheme, typeface, shopCode, "","", "ToDo List");
                bottomSearchProductsFragment.setMyItemClickListener(ToDoListActivity.this);
                bottomSearchProductsFragment.show(getSupportFragmentManager(), bottomSearchProductsFragment.getTag());
            }
        });
        add_todo_list  = findViewById(R.id.add_todo_list);
        add_todo_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCreateTodoDialog();
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

        myToDoList = new ArrayList<>();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        toDoListAdapter = new ToDoListAdapter(this, myToDoList, shopCode, isDarkTheme, typeface, colorTheme);
        toDoListAdapter.setItemClickListener(this);
        recyclerView.setAdapter(toDoListAdapter);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getToList();
            }
        });
        getToList();
    }

    private void openCreateTodoDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.create_todolilst_dialog_layout);
        dialog.setTitle("ToDo List");
        final TextInputEditText editText = dialog.findViewById(R.id.et_name);
        Button cancel = dialog.findViewById(R.id.btn_cancel);
        Button create = dialog.findViewById(R.id.btn_create);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyToDo myToDo = new MyToDo();
                myToDo.setId(myToDoList.size()+"");
                myToDo.setName(editText.getText().toString());
                myToDo.setShopCode(shopCode);
                myToDoList.add(myToDo);
                dbHelper.addTODOList(myToDo);
                showNoData(false);
                toDoListAdapter.notifyDataSetChanged();
                dialog.dismiss();
            //    DialogAndToast.showDialog("ToDo Name "+editText.getText().toString(), getBaseContext());
            }
        });
        dialog.show();
    }

    private void getToList(){
        myToDoList.clear();
        if(swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
        myToDoList.addAll(dbHelper.getToDoList(shopCode));
        Log.d("List Size ", ""+myToDoList.size());
        if(myToDoList.size()==0){
            showNoData(true);
        }else {
            showNoData(false);
            toDoListAdapter.notifyDataSetChanged();
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

    @Override
    public void onItemClicked(int pos) {

    }

    @Override
    public void onProductSearch(MyProduct myProduct) {

    }

    @Override
    public void onItemClicked(int pos, String type) {
        MyToDo myToDo = myToDoList.get(pos);
        if(type.equals("remove")) {
            dbHelper.removeTODoList(myToDo.getId(), shopCode);
            myToDoList.remove(pos);
            toDoListAdapter.notifyItemRemoved(pos);
        }else if(type.equals("productList")){
            Intent intent = new Intent(this, ToDoListDetailsActivity.class);
            intent.putExtra("callingClass","ShopListActivity");
            intent.putExtra("name",shopName);
            intent.putExtra("address",shopAddress);
            intent.putExtra("dbname",shopdbname);
            intent.putExtra("dbuser",dbuser);
            intent.putExtra("dbpassword", dbpassword);
            intent.putExtra("shop_code", shopCode);
            intent.putExtra("shopDeliveryModel", shopDeliveryModel);
            intent.putExtra("todo_item", myToDo);
            startActivity(intent);
        }
    }
}
