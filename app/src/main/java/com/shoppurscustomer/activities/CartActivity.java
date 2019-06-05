package com.shoppurscustomer.activities;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.shoppurscustomer.R;
import com.shoppurscustomer.adapters.CartAdapter;
import com.shoppurscustomer.models.CartItem;
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

/**
 * Created by suraj kumar singh on 20-03-2019.
 */

public class CartActivity extends NetworkBaseActivity{

    private TextView tv_total,tv_totalItems;
    private RecyclerView recycler_itemlist;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItemList;
    private TextView tv_placeorder, tvnoData;
    private LinearLayout linear_details;
    private String dbName, dbUserName, dbPassword,custCode,custName,custMobile;
    private CartItem cartItem;
    private RelativeLayout rlfooterviewcart;
    private float total_amount;
    private int total_quantity;
    private String shopcode_prodcode_qty, transactionId;
    private JSONArray shopArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        cartItem = new CartItem();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        cartItemList = new ArrayList<>();
        tv_total = findViewById(R.id.itemPrice);
        tv_totalItems = findViewById(R.id.itemCount);
        tv_placeorder = findViewById(R.id.viewCart);
        linear_details = findViewById(R.id.linear_details);
        rlfooterviewcart = findViewById(R.id.rlfooterviewcart);
        tvnoData = findViewById(R.id.tvnoData);

        recycler_itemlist = (RecyclerView) findViewById(R.id.recycler_cart);
        recycler_itemlist.setHasFixedSize(true);
        recycler_itemlist.setRecycledViewPool(new RecyclerView.RecycledViewPool());
        recycler_itemlist.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recycler_itemlist.getRecycledViewPool().setMaxRecycledViews(0, 0);

        dbName = sharedPreferences.getString(Constants.DB_NAME,"");
        dbUserName = sharedPreferences.getString(Constants.DB_USER_NAME,"");
        dbPassword = sharedPreferences.getString(Constants.DB_PASSWORD,"");
        custCode =  sharedPreferences.getString(Constants.DB_NAME,""); //
        custName =sharedPreferences.getString(Constants.FULL_NAME,"");
        custMobile = sharedPreferences.getString(Constants.MOBILE_NO, "");

        getCartData();
        //cartItemList = dbHelper.getAllCartItems();
        Log.d("cart Size ", " "+cartItemList.size());
        cartAdapter = new CartAdapter(CartActivity.this, cartItemList);
        recycler_itemlist.setAdapter(cartAdapter);

        tv_placeorder.setText("Place Order");
        tv_placeorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //placeOrder();
                generateJson();
                DialogAndToast.showToast("Placing Order Wait", CartActivity.this);
            }
        });
    }

    private void generateJson(){
        try {
            cartItemList = new ArrayList<>();
            cartItemList = dbHelper.getAllCartItems();

            List<String> tempShopList = new ArrayList<>();
            shopArray = new JSONArray();
            JSONObject shopObject = new JSONObject();
            JSONArray productArray = new JSONArray();
            JSONObject productObject = new JSONObject();

            for (CartItem cartItem : cartItemList) {
                Log.d(TAG, cartItem.getBarcode()+"");
                if (!tempShopList.contains(cartItem.getShopCode())) {
                    //Log.d("PRD list "+tempShopList.toString(), cartItem.getShopCode());
                    tempShopList.add(cartItem.getShopCode());
                    shopObject = new JSONObject();
                    productArray = new JSONArray();
                    productObject = new JSONObject();

                    shopObject.put("shopCode", cartItem.getShopCode());
                    shopObject.put("orderDate", Utility.getTimeStamp());
                    shopObject.put("orderDeliveryNote","Note");
                    shopObject.put("orderDeliveryMode","Self");
                    shopObject.put("paymentMode","Online");
                    shopObject.put("deliveryAddress","Delhi");
                    shopObject.put("pinCode","110091");
                    shopObject.put("createdBy",custName);
                    shopObject.put("updateBy",custName);
                    shopObject.put("orderStatus","pending");
                    shopObject.put("orderPaymentStatus", "pending");
                    shopObject.put("custName", custName);
                    shopObject.put("custCode",custCode);
                    shopObject.put("mobileNo",custMobile);
                    shopObject.put("orderImage","ShoppursImages/products/1/1/prod_1_1.jpg");
                    shopObject.put("totalQuantity",String.valueOf(dbHelper.getTotalQuantity(cartItem.getShopCode())));
                    shopObject.put("toalAmount",String.valueOf(dbHelper.getTotalAmount(cartItem.getShopCode())));
                    shopObject.put("dbName",dbName);
                    shopObject.put("dbUserName",dbUserName);
                    shopObject.put("dbPassword",dbPassword);
                    productObject.put("prodCode", cartItem.getProdCode());
                    productObject.put("prodBarCode", cartItem.getBarcode());
                    productObject.put("qty", cartItem.getQuantity());
                    productArray.put(productObject);
                    shopObject.put("myProductList", productArray);
                    shopArray.put(shopObject);
                } else {
                    productObject = new JSONObject();

                    productObject.put("prodCode", cartItem.getProdCode());
                    productObject.put("prodBarCode", cartItem.getBarcode());
                    productObject.put("qty", cartItem.getQuantity());
                    productArray.put(productObject);
                    shopObject.put("myProductList", productArray);
                }
            }
            generateOrder(shopArray);
            Log.d(TAG, shopArray.toString());
        }catch (Exception a){
            a.printStackTrace();
        }
    }

    public void updateUi(){
            cartItemList = new ArrayList<>();
            cartItemList = dbHelper.getAllCartItems();

            total_amount = 0;
            total_quantity = 0;
            shopcode_prodcode_qty = "";
            for (CartItem cartItem : cartItemList) {
                total_amount = total_amount + cartItem.getTotalAmout();
                total_quantity = total_quantity + cartItem.getQuantity();
            }
            tv_total.setText("Amount " + String.valueOf(total_amount));
            tv_totalItems.setText("Items(" + String.valueOf(cartItemList.size()) + ")");
            cartAdapter.notifyDataSetChanged();
    }

    public void getCartData(){
        Map<String,String> params=new HashMap<>();
        params.put("dbName",dbName);
        params.put("dbUserName",dbUserName);
        params.put("dbPassword",dbPassword);
        String url=getResources().getString(R.string.root_url)+"cart/getCartData";
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"getCartData");
    }

    public void updateCart(CartItem cartItem){
        this.cartItem = cartItem;
        Map<String,String> params=new HashMap<>();
        params.put("shopCode",cartItem.getShopCode());
        params.put("prodCode", cartItem.getProdCode());
        params.put("prodQty", String.valueOf(cartItem.getQuantity()));
        params.put("dbName", dbName);
        params.put("dbUserName", dbUserName);
        params.put("dbPassword", dbPassword);
        Log.d(TAG, params.toString());

        String url=getResources().getString(R.string.root_url)+"cart/update";
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"updatCart");
    }

    public void removeCart(CartItem cartItem){
        this.cartItem = cartItem;
        Map<String,String> params=new HashMap<>();
        params.put("shopCode",cartItem.getShopCode());
        params.put("prodCode", cartItem.getProdCode());
        params.put("dbName", dbName);
        params.put("dbUserName", dbUserName);
        params.put("dbPassword", dbPassword);
        Log.d(TAG, params.toString());

        String url=getResources().getString(R.string.root_url)+"cart/remove_product";
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"removeCart");
    }

    private void generateOrder(JSONArray shopArray){
         Log.d(TAG, shopArray.toString());
        String url=getResources().getString(R.string.root_url)+"order/generate_order";
        showProgress(true);
        jsonArrayV2ApiRequest(Request.Method.POST,url, shopArray,"generate_order");
    }



    private void placeOrder(JSONArray shopArray, String orderId) throws JSONException {
        transactionId = "trans12345";
        shopArray.getJSONObject(0).put("orderId", orderId );
       for(int i=0;i<shopArray.length();i++){
            shopArray.getJSONObject(i).put("orderPaymentStatus", "Done");
            shopArray.getJSONObject(i).put("transactionId", transactionId);
        }
        Log.d(TAG, shopArray.toString());
        String url=getResources().getString(R.string.root_url)+"order/place_order";
        showProgress(true);
        jsonArrayV2ApiRequest(Request.Method.POST,url, shopArray,"place_order");
    }

    @Override
    public void onJsonObjectResponse(JSONObject response, String apiName) {
        showProgress(false);
        try {
            // JSONObject jsonObject=response.getJSONObject("response");
            Log.d("response", response.toString());
            if(apiName.equals("getCartData")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)){

                    JSONArray jsonArray = response.getJSONArray("result");
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject cartObject = jsonArray.getJSONObject(i).getJSONObject("myProduct");
                        CartItem cartItem = new CartItem();
                        cartItem.setShopCode(jsonArray.getJSONObject(i).getString("shopCode"));
                        cartItem.setQuantity(Integer.parseInt(jsonArray.getJSONObject(i).getString("prodQty")));
                        cartItem.setBarcode(cartObject.getString("prodBarCode"));
                        cartItem.setProdCode(cartObject.getString("prodCode"));
                        cartItem.setItemName(cartObject.getString("prodName"));
                        cartItem.setShopName("Vipin Dhama Shop");
                        cartItem.setPrice(Float.parseFloat(cartObject.getString("prodSp")));
                        //cartItem.setSize(cartObject.getInt("prodId"));
                        cartItem.setCustCode(custCode);
                        //cartItem.setCustName(cartObject.getString("prodId"));
                        float subtotal = cartItem.getPrice() * cartItem.getQuantity();
                        cartItem.setTotalAmout(subtotal);
                        cartItem.setProdCgst(Float.parseFloat(cartObject.getString("prodCgst")));
                        cartItem.setProdIgst(Float.parseFloat(cartObject.getString("prodIgst")));
                        cartItem.setProdSgst(Float.parseFloat(cartObject.getString("prodSgst")));
                        //"": 5, "": 1, "": 2, "prodWarranty": 2,
                        cartItemList.add(cartItem);
                    }

                    if(cartItemList.size()>0){
                        dbHelper.deleteAllCart();
                        dbHelper.add_to_Cart(cartItemList);
                        cartAdapter.notifyDataSetChanged();
                        updateUi();
                    }else {
                        showNoData(true);
                    }

                }else {
                    DialogAndToast.showDialog(response.getString("message"),CartActivity.this);
                }
            }else if(apiName.equals("updatCart")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)){
                    dbHelper.updateCart(cartItem);
                    updateUi();
                    Log.d(TAG, "updated cart" );
                }else {
                    DialogAndToast.showToast(response.getString("message"),CartActivity.this);
                }
            }else if(apiName.equals("removeCart")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)){
                    dbHelper.deleteCart(cartItem);
                    updateUi();
                    Log.d(TAG, "Deleted cart" );
                }else {
                    DialogAndToast.showToast(response.getString("message"),CartActivity.this);
                }
            }else if (apiName.equals("generate_order")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)){
                    Log.d(TAG, "Ordeer Generated" );
                    String orderId = response.getJSONObject("result").getString("orderId");
                    Log.d(TAG, "orderId "+orderId );
                    placeOrder(shopArray, orderId);  // open payment option
                }else {
                    DialogAndToast.showToast(response.getString("message"),CartActivity.this);
                }
            }else if(apiName.equals("place_order")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)){
                    dbHelper.deleteAllCart();
                    cartItemList.clear();
                    recycler_itemlist.setAdapter(null);
                    cartAdapter.notifyDataSetChanged();
                    updateCartCount();
                    Log.d(TAG, "Ordeer Placed" );
                }else {
                    DialogAndToast.showToast(response.getString("message"),CartActivity.this);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            DialogAndToast.showToast(getResources().getString(R.string.json_parser_error)+e.toString(),CartActivity.this);
        }
    }

    private void showNoData(boolean show){
        if(show){
            recycler_itemlist.setVisibility(View.GONE);
            tvnoData.setVisibility(View.VISIBLE);
        }else{
            recycler_itemlist.setVisibility(View.VISIBLE);
            tvnoData.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateCartCount();
    }

    public void updateCartCount(){
        if(dbHelper.getCartCount()>0){
            rlfooterviewcart.setVisibility(View.VISIBLE);
            List<CartItem> cartItemList = new ArrayList<>();
            cartItemList = dbHelper.getAllCartItems();
            float total_amount =0;
            for (CartItem cartItem: cartItemList) {
                total_amount = total_amount + cartItem.getTotalAmout();
            }
            tv_total.setText("Amount "+String.valueOf(total_amount));
            tv_totalItems.setText("Item "+String.valueOf(dbHelper.getCartCount()));
        }else rlfooterviewcart.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
