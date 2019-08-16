package com.shoppurscustomer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.shoppurscustomer.R;
import com.shoppurscustomer.activities.Settings.DeliveryAddressListActivity;
import com.shoppurscustomer.activities.Settings.SettingActivity;
import com.shoppurscustomer.activities.payment.ccavenue.activities.CCAvenueWebViewActivity;
import com.shoppurscustomer.activities.payment.ccavenue.utility.AvenuesParams;
import com.shoppurscustomer.adapters.CartAdapter;
import com.shoppurscustomer.adapters.OfferDescAdapter;
import com.shoppurscustomer.database.DbHelper;
import com.shoppurscustomer.fragments.BottomSearchFragment;
import com.shoppurscustomer.fragments.OfferDescriptionFragment;
import com.shoppurscustomer.interfaces.MyItemClickListener;
import com.shoppurscustomer.interfaces.MyItemTypeClickListener;
import com.shoppurscustomer.models.Barcode;
import com.shoppurscustomer.models.MyProduct;
import com.shoppurscustomer.models.ProductDiscountOffer;
import com.shoppurscustomer.models.ProductPriceDetails;
import com.shoppurscustomer.models.ProductPriceOffer;
import com.shoppurscustomer.utilities.Constants;
import com.shoppurscustomer.utilities.DialogAndToast;
import com.shoppurscustomer.utilities.Utility;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartActivity extends NetworkBaseActivity implements MyItemTypeClickListener, MyItemClickListener {

    private RecyclerView recyclerView;
    private CartAdapter myItemAdapter;
    private List<MyProduct> itemList;
    private MyProduct freeProdut;

    private RelativeLayout relativeLayoutCartFooter;
    private LinearLayout rlAddressBilling;
    private TextView tvItemCount,tvItemPrice,tvCheckout,tvItemTotal,tvTotalTaxes,tvSgst,tvTotalDiscount,
            tvDeliveryCharges,tvNetPayable,tvOfferName;
    private RelativeLayout relativeLayoutPayOptionLayout,rlDiscount,rlDelivery,rl_offer_applied_layout,rlOfferLayout;
    private TextView tvCash,tvCard,tvMPos,tvAndroidPos;

    private ImageView imageViewScan,imageViewSearch,imageViewRemoveOffer;

    private float totalPrice,totalTax,totDiscount,offerPer,offerMaxAmount,deliveryDistance,deliveryCharges;
    private int offerId,deliveryTypeId;

    private String offerName,paymentMode,orderNumber;
    private String deviceType;

    private JSONArray shopArray;
    List <MyProduct> cartItemList;

    private TextView tv_mode, tv_self_status, tv_address_label, tv_address,tvApplyCoupon;
    private View seperator_delivery_address;
    private RadioGroup rg_delivery;
    private RadioButton rb_home_delivery, rb_self_delivery;
    private LinearLayout linearLayoutScanCenter;
    private RelativeLayout rlOfferDesc;
    private Button btnStoreOffers;
    private int position, type, productDetailsType;

    private FloatingActionButton fabScan;
    private String shopCode, shopName;
    private TextView text_left_label, text_right_label;

    private BottomSearchFragment bottomSearchFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
    }

    private void init(){
        text_left_label = findViewById(R.id.text_left_label);
        text_right_label = findViewById(R.id.text_right_label);
        text_left_label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CartActivity.this, SettingActivity.class));
                finish();
            }
        });

        shopCode = sharedPreferences.getString(Constants.SHOP_INSIDE_CODE,"");
        shopName = sharedPreferences.getString(Constants.SHOP_INSIDE_NAME,"");

        itemList = new ArrayList<>();
        imageViewScan = findViewById(R.id.image_scan);
        imageViewSearch = findViewById(R.id.image_search);
        tvItemCount=findViewById(R.id.itemCount);
        tvItemPrice=findViewById(R.id.itemPrice);
        tvCheckout=findViewById(R.id.viewCart);
        tvCard = findViewById(R.id.tv_pay_card);
        tvCash = findViewById(R.id.tv_pay_cash);
        tvMPos = findViewById(R.id.tv_mpos);
        tvAndroidPos = findViewById(R.id.tv_android_pos);
        linearLayoutScanCenter = findViewById(R.id.linear_action);

        tvApplyCoupon = findViewById(R.id.tv_coupon_label);
        tvItemTotal = findViewById(R.id.tv_item_total);
        tvTotalTaxes = findViewById(R.id.tv_total_taxes);
        tvSgst = findViewById(R.id.tv_sgst);
        tvTotalDiscount = findViewById(R.id.tv_total_discount);
        tvDeliveryCharges = findViewById(R.id.tv_delivery_charges);
        tvNetPayable = findViewById(R.id.tv_net_pay);
        tvOfferName = findViewById(R.id.tv_offer_name);
        rlDelivery = findViewById(R.id.relative_delivery_layout);
        rlDiscount = findViewById(R.id.relative_discount);
        rl_offer_applied_layout = findViewById(R.id.rl_offer_applied_layout);
        rlOfferDesc = findViewById(R.id.rl_offer_desc);
        rlOfferDesc.setVisibility(View.GONE);
        rlOfferLayout = findViewById(R.id.rl_offer_layout);
        imageViewRemoveOffer = findViewById(R.id.image_remove_offer);
        btnStoreOffers = findViewById(R.id.btn_store_offers);
        btnStoreOffers.setVisibility(View.GONE);
        fabScan = findViewById(R.id.fab);

        Utility.setColorFilter(imageViewRemoveOffer.getDrawable(),colorTheme);
        Utility.setColorFilter(btnStoreOffers.getBackground(),colorTheme);

        relativeLayoutPayOptionLayout = findViewById(R.id.relative_pay_layout);
        rlAddressBilling = findViewById(R.id.linear_billing);
        relativeLayoutCartFooter=findViewById(R.id.rlfooterviewcart);
        relativeLayoutCartFooter.setBackgroundColor(colorTheme);
        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        final RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        myItemAdapter=new CartAdapter(this,itemList);
        myItemAdapter.setDarkTheme(isDarkTheme);
        myItemAdapter.setMyItemTypeClickListener(this);
        recyclerView.setAdapter(myItemAdapter);
        recyclerView.setNestedScrollingEnabled(false);

        tvCheckout.setText("Make Payment");


        tvCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                relativeLayoutPayOptionLayout.setVisibility(View.VISIBLE);
            }
        });

        tvCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               paymentMode = "Online";
               generateJson(paymentMode);
            }
        });

        tvCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paymentMode = "Cash";
                generateJson(paymentMode);
                /*Intent intent = new Intent(CartActivity.this, MPayActivity.class);
                intent.putExtra("totalAmount",String.format("%.02f",dbHelper.getTotalPriceCart()));
                intent.putExtra("ordId",getIntent().getStringExtra("ordId"));
                startActivity(intent);*/
            }
        });

        tvMPos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paymentMode = "Credit/Debit Card";
                deviceType = "ME30S";
                generateJson(paymentMode);
            }
        });

        tvAndroidPos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paymentMode = "Credit/Debit Card";
                deviceType = "N910";
                generateJson(paymentMode);
            }
        });


        imageViewSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSearchFragment bottomSearchFragment = new BottomSearchFragment();
                Bundle bundle = new Bundle();
                bundle.putString("shopCode", shopCode);
                bundle.putString("shopName", shopName);
                bundle.putString("shopAddress", "");
                bundle.putString("shopMobile", "");
                bottomSearchFragment.setArguments(bundle);
                bottomSearchFragment.setCallingActivityName("CartActivity", sharedPreferences, isDarkTheme);
                bottomSearchFragment.setSubCatName("");
                bottomSearchFragment.setSubcatId("");
                bottomSearchFragment.show(getSupportFragmentManager(), bottomSearchFragment.getTag());
            }
        });

        boolean isDeliveryAvailable = sharedPreferences.getBoolean(Constants.IS_DELIVERY_AVAILABLE, false);
        tv_mode = findViewById(R.id.tv_mode);
        tv_self_status = findViewById(R.id.tv_self_status);
        rg_delivery = findViewById(R.id.rg_delivery);
        tv_address_label = findViewById(R.id.tv_address_label);
        tv_address = findViewById(R.id.tv_address);
       // seperator_delivery_address = findViewById(R.id.seperator_delivery_address);
        rb_home_delivery = findViewById(R.id.rb_home_delivery);
        rb_self_delivery = findViewById(R.id.rb_self_delivery);

        rb_self_delivery.setChecked(true);

        if(!isDeliveryAvailable){ // home delivery not available
            tv_self_status.setText("Self Delivery");
            rg_delivery.setVisibility(View.GONE);
        } else {
            rg_delivery.setVisibility(View.VISIBLE);
            tv_self_status.setVisibility(View.GONE);
        }

        rg_delivery.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.rb_self_delivery){
                    deliveryTypeId = checkedId;
                    tv_self_status.setText("Self Delivery");
                    //tv_self_status.setVisibility(View.VISIBLE);
                    tv_address_label.setVisibility(View.GONE);
                   // seperator_delivery_address.setVisibility(View.GONE);
                    tv_address.setVisibility(View.GONE);
                    tvDeliveryCharges.setText("0.00");
                    deliveryDistance = 0;
                    rlDelivery.setVisibility(View.GONE);
                }else{
                    if(totalPrice > sharedPreferences.getInt(Constants.MIN_DELIVERY_AMOUNT,0)){
                        startActivityForResult(new Intent(CartActivity.this, DeliveryAddressListActivity.class), 101);
                        tv_self_status.setVisibility(View.GONE);
                    }else{
                        rb_self_delivery.setChecked(true);
                        DialogAndToast.showDialog("Minimum amount should be more than Rs."
                                +sharedPreferences.getInt(Constants.MIN_DELIVERY_AMOUNT,0)+" for home delivery.",CartActivity.this);
                    }

                }
            }
        });

        tv_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(CartActivity.this, DeliveryAddressListActivity.class), 101);
                tv_self_status.setVisibility(View.GONE);
            }
        });

        tv_address_label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(CartActivity.this, DeliveryAddressListActivity.class), 101);
                tv_self_status.setVisibility(View.GONE);
            }
        });

        tvApplyCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(CartActivity.this, CartShopListActivity.class);
                intent.putExtra("custCode",getIntent().getStringExtra("custCode"));
                intent.putExtra("flag","coupons");
                startActivity(intent);
                //startActivityForResult(intent,5);
            }
        });

        imageViewRemoveOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rl_offer_applied_layout.setVisibility(View.GONE);
                tvApplyCoupon.setVisibility(View.VISIBLE);
                offerId = 0;
                offerMaxAmount = 0f;
                offerPer = 0f;
                offerName = "";
                setFooterValues();
            }
        });

        btnStoreOffers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CartActivity.this, CartShopListActivity.class);
                intent.putExtra("custCode",getIntent().getStringExtra("custCode"));
                intent.putExtra("flag","offers");
                startActivity(intent);
                //startActivityForResult(intent,6);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(requestCode ==101 && intent!=null){
            String address = intent.getStringExtra("address");
            String country = intent.getStringExtra("country");
            String state = intent.getStringExtra("state");
            String city = intent.getStringExtra("city");
            String zip = intent.getStringExtra("zip");
            deliveryDistance = intent.getFloatExtra("distance",0f);
            Log.d("addr ", address +country + state + city +zip+deliveryDistance);
            tv_address_label.setVisibility(View.VISIBLE);
            tv_address.setVisibility(View.VISIBLE);
            tv_address.setText(address.concat(zip));
            setFooterValues();
           // seperator_delivery_address.setVisibility(View.VISIBLE);
        }else if(requestCode == 5){
            if(intent != null){
                offerName = intent.getStringExtra("name");
                offerId = intent.getIntExtra("id",0);
                offerPer = intent.getFloatExtra("per",0f);
                offerMaxAmount = intent.getFloatExtra("amount",0f);
                rl_offer_applied_layout.setVisibility(View.VISIBLE);
                tvOfferName.setText(offerName);
                tvApplyCoupon.setVisibility(View.GONE);
                setFooterValues();
            }
        }
    }

    private void openScannar(){
        Intent intent = new Intent(this,ScannarActivity.class);
        intent.putExtra("flag","scan");
        intent.putExtra("type","addToCart");
        startActivity(intent);

      /* dbHelper.deleteTable(DbHelper.CART_TABLE);

        MyProductItem myProductItem = dbHelper.getProductDetailsByBarCode("N7NL00824803");
        myProductItem.setProdBarCode("N7NL00824803");
        myProductItem.setQty(1);
        myProductItem.setTotalAmount(myProductItem.getProdSp());
        dbHelper.addProductToCart(myProductItem);

        itemList.clear();
        List<MyProductItem> cartList =  dbHelper.getCartProducts();
        for(MyProductItem ob : cartList){
            itemList.add(ob);
        }

        myItemAdapter.notifyDataSetChanged();

        if(itemList.size() > 0){
            setFooterValues();
            relativeLayoutCartFooter.setVisibility(View.VISIBLE);
            myItemAdapter.notifyDataSetChanged();
        }else{
            relativeLayoutCartFooter.setVisibility(View.GONE);
        }*/

    }

    public void setFooterValues(){
        recyclerView.setVisibility(View.VISIBLE);
        linearLayoutScanCenter.setVisibility(View.GONE);
        if(itemList.size() == 1){
            tvItemCount.setText(itemList.size()+" item");
        }else{
            tvItemCount.setText(itemList.size()+" items");
        }


        tvItemTotal.setText(Utility.numberFormat(dbHelper.getTotalMrpPriceCart()));
        totalTax = dbHelper.getTotalTaxesart();
        tvTotalTaxes.setText(Utility.numberFormat(totalTax));
        totalPrice = dbHelper.getTotalPriceCart() + totalTax;
        totDiscount = dbHelper.getTotalMrpPriceCart() - dbHelper.getTotalPriceCart();
        float dis = 0f;
        if(offerPer > 0f){
            dis = totalPrice * offerPer / 100;
            totDiscount = totDiscount + dis ;
        }

        Log.i(TAG," Taxes "+dbHelper.getTotalTaxesart());
        Log.i(TAG," MRP "+dbHelper.getTotalMrpPriceCart()
                +" Price "+dbHelper.getTotalPriceCart());
        Log.i(TAG," diff "+totDiscount+" offerPer "+offerPer+" totalPrice "+totalPrice);

        if(totDiscount == 0f){
            rlDiscount.setVisibility(View.GONE);
        }else{
            rlDiscount.setVisibility(View.VISIBLE);
            tvTotalDiscount.setText(Utility.numberFormat(totDiscount));
        }

        totalPrice = totalPrice - dis;
        if(deliveryTypeId == R.id.rb_self_delivery){
            rlDelivery.setVisibility(View.GONE);
        }else{
            rlDelivery.setVisibility(View.VISIBLE);
            Log.i(TAG,"Min Delivery Distance "+sharedPreferences.getInt(Constants.MIN_DELIVERY_DISTANCE,0));
            Log.i(TAG,"Charges after Delivery Distance "+sharedPreferences.getInt(Constants.CHARGE_AFTER_MIN_DISTANCE,0));
            Log.i(TAG,"Delivery Distance "+deliveryDistance);
            if(deliveryDistance > sharedPreferences.getInt(Constants.MIN_DELIVERY_DISTANCE,0)){
               float diffKms = deliveryDistance -  sharedPreferences.getInt(Constants.MIN_DELIVERY_DISTANCE,0);
               int intKms = (int)diffKms;
               float decimalKms = diffKms - intKms;

                int chargesPerKm = sharedPreferences.getInt(Constants.CHARGE_AFTER_MIN_DISTANCE,0);
                deliveryCharges = intKms * chargesPerKm + decimalKms * chargesPerKm;
                tvDeliveryCharges.setText(Utility.numberFormat(deliveryCharges));
            }else{
                tvDeliveryCharges.setText("0.00");
            }
        }

        totalPrice = totalPrice + deliveryCharges;
        tvItemPrice.setText("Rs "+Utility.numberFormat(totalPrice));
        tvNetPayable.setText("Rs "+Utility.numberFormat(totalPrice));

        tvCheckout.setVisibility(View.VISIBLE);
        rlAddressBilling.setVisibility(View.VISIBLE);

    }

    private void generateOrder(JSONArray shopArray){
        Log.d(TAG, shopArray.toString());
        String url=getResources().getString(R.string.root_url)+Constants.GENERATE_ORDER;
        showProgress(true);
        jsonArrayV2ApiRequest(Request.Method.POST,url, shopArray,"generate_order");
    }

    private void placeOrder(JSONArray shopArray, String orderId) throws JSONException {
        shopArray.getJSONObject(0).put("orderId", orderId );
        Log.d(TAG, shopArray.toString());
        String url=getResources().getString(R.string.root_url)+Constants.PLACE_ORDER;
        showProgress(true);
        jsonArrayV2ApiRequest(Request.Method.POST,url, shopArray,"place_order");
    }

    private void generateJson(String paymentMode){
        try {
            cartItemList = dbHelper.getCartProducts();

            List<String> tempShopList = new ArrayList<>();
            shopArray = new JSONArray();
            JSONObject shopObject = new JSONObject();
            JSONArray productArray = new JSONArray();
            JSONObject productObject = new JSONObject();

            JSONArray tempbarcodeArray =null;
            JSONObject tempbarcodeObject = null;


            for (MyProduct cartItem : cartItemList) {
                String shopCode = cartItem.getShopCode();
                Log.d(TAG, cartItem.getBarCode()+"");
                if (!tempShopList.contains(shopCode)) {
                    //Log.d("PRD list "+tempShopList.toString(), cartItem.getShopCode());
                    tempShopList.add(shopCode);
                    shopObject = new JSONObject();
                    productArray = new JSONArray();
                    productObject = new JSONObject();

                   /* if(cartItem.getIsBarcodeAvailable().equals("Y")){
                        cartItem.setBarcodeList(dbHelper.getBarCodesForCart(cartItem.getId()));
                        tempbarcodeArray = new JSONArray();
                        for (int i=0;i<cartItem.getQty();i++){
                            tempbarcodeObject = new JSONObject();
                            tempbarcodeObject.put("barcode", cartItem.getBarcodeList().get(i).getBarcode());
                            tempbarcodeArray.put(tempbarcodeObject);
                        }
                    }*/

                    shopObject.put("shopCode", shopCode);
                    shopObject.put("orderDate", Utility.getTimeStamp());
                    shopObject.put("orderDeliveryNote","Note");
                    shopObject.put("orderDeliveryMode","Self");
                    shopObject.put("paymentMode",paymentMode);
                    shopObject.put("deliveryAddress",sharedPreferences.getString(Constants.CUST_ADDRESS,""));
                    shopObject.put("deliveryCountry",sharedPreferences.getString(Constants.CUST_COUNTRY,""));
                    shopObject.put("deliveryState",sharedPreferences.getString(Constants.CUST_STATE,""));
                    shopObject.put("deliveryCity",sharedPreferences.getString(Constants.CUST_CITY,""));
                    shopObject.put("pinCode",sharedPreferences.getString(Constants.CUST_PINCODE,""));
                    shopObject.put("createdBy",sharedPreferences.getString(Constants.FULL_NAME,""));
                    shopObject.put("updateBy",sharedPreferences.getString(Constants.FULL_NAME,""));
                    if(paymentMode.equals("Cash")){
                        shopObject.put("orderStatus","Delivered");
                        shopObject.put("orderPaymentStatus", "Done");
                    }else{
                        shopObject.put("orderStatus","pending");
                        shopObject.put("orderPaymentStatus", "pending");
                    }
                    shopObject.put("custName", sharedPreferences.getString(Constants.FULL_NAME, ""));
                    shopObject.put("custCode",sharedPreferences.getString(Constants.USER_ID, ""));
                    shopObject.put("mobileNo",sharedPreferences.getString(Constants.MOBILE_NO, ""));
                    shopObject.put("orderImage","customer.png");
                    shopObject.put("custUserCreateStatus","C");
                    shopObject.put("totalQuantity",String.valueOf(dbHelper.getTotalQuantity(shopCode)));
                    shopObject.put("toalAmount",String.valueOf(getTotalAmount(shopCode)));
                    shopObject.put("ordCouponId",String.valueOf(offerId));
                    shopObject.put("totCgst",String.valueOf(dbHelper.getTaxesCart("cgst", shopCode)));
                    shopObject.put("totSgst",String.valueOf(dbHelper.getTaxesCart("sgst", shopCode)));
                    shopObject.put("totIgst",String.valueOf(dbHelper.getTaxesCart("igst", shopCode)));
                    shopObject.put("totTax",String.valueOf(dbHelper.getTotalTaxesart(shopCode)));
                    shopObject.put("deliveryCharges",String.valueOf(deliveryCharges));
                    shopObject.put("totDiscount",String.valueOf(dbHelper.getTotalDisValue(shopCode)));
                    shopObject.put("dbName",sharedPreferences.getString(Constants.DB_NAME,""));
                    shopObject.put("dbUserName",sharedPreferences.getString(Constants.DB_USER_NAME,""));
                    shopObject.put("dbPassword",sharedPreferences.getString(Constants.DB_PASSWORD,""));

                    productObject.put("prodCode", cartItem.getCode());
                    productObject.put("prodHsnCode", cartItem.getProdHsnCode());
                    productObject.put("prodId", cartItem.getId());

                    if(cartItem.getComboProductIds() != null)
                    productObject.put("comboProdIds", cartItem.getComboProductIds());

                    /*if(cartItem.getIsBarcodeAvailable().equals("Y")){
                        productObject.put("prodBarCode", cartItem.getBarcodeList().get(0).getBarcode());
                        productObject.put("barcodeList",  tempbarcodeArray);
                    }*/

                    productObject.put("qty", cartItem.getQuantity());
                    productObject.put("prodName",cartItem.getName());
                    productObject.put("prodUnit",cartItem.getUnit());
                    productObject.put("prodSize",cartItem.getSize());
                    productObject.put("prodColor",cartItem.getColor());
                    productObject.put("prodDesc",cartItem.getDesc());
                    productObject.put("prodMrp",cartItem.getMrp());
                    productObject.put("prodSp", cartItem.getSellingPrice());
                    productObject.put("prodCgst", cartItem.getProdCgst());
                    productObject.put("prodSgst", cartItem.getProdSgst());
                    productObject.put("prodIgst", cartItem.getProdIgst());
                    productObject.put("prodImage1",cartItem.getProdImage1());
                    productObject.put("prodImage2",cartItem.getProdImage2());
                    productObject.put("prodImage3",cartItem.getProdImage3());
                    productObject.put("isBarcodeAvailable",cartItem.getIsBarcodeAvailable());
                    if(cartItem.getOfferItemCounter() > 0){
                        productObject.put("offerId", cartItem.getOfferId());
                        productObject.put("offerType", cartItem.getOfferType());
                        productObject.put("freeItems", String.valueOf(cartItem.getOfferItemCounter()));
                    }
                    productArray.put(productObject);
                    shopObject.put("myProductList", productArray);
                    shopArray.put(shopObject);
                } else {
                    productObject = new JSONObject();
                    if(cartItem.getComboProductIds() != null)
                    productObject.put("comboProdIds", cartItem.getComboProductIds());
                    productObject.put("prodCode", cartItem.getCode());
                    productObject.put("prodHsnCode", cartItem.getProdHsnCode());
                    productObject.put("prodId", cartItem.getId());
                    /*if(cartItem.getIsBarcodeAvailable().equals("Y")){
                        productObject.put("prodBarCode", cartItem.getBarcodeList().get(0).getBarcode());
                        productObject.put("barcodeList",  tempbarcodeArray);
                    }*/
                    productObject.put("qty", cartItem.getQuantity());
                    productObject.put("prodName",cartItem.getName());
                    productObject.put("prodUnit",cartItem.getUnit());
                    productObject.put("prodSize",cartItem.getSize());
                    productObject.put("prodColor",cartItem.getColor());
                    productObject.put("prodDesc",cartItem.getDesc());
                    productObject.put("prodMrp",cartItem.getMrp());
                    productObject.put("prodSp", cartItem.getSellingPrice());
                    productObject.put("prodCgst", cartItem.getProdCgst());
                    productObject.put("prodSgst", cartItem.getProdSgst());
                    productObject.put("prodIgst", cartItem.getProdIgst());
                    productObject.put("prodImage1",cartItem.getProdImage1());
                    productObject.put("prodImage2",cartItem.getProdImage2());
                    productObject.put("prodImage3",cartItem.getProdImage3());
                    productObject.put("isBarcodeAvailable",cartItem.getIsBarcodeAvailable());
                    productObject.put("prodCgst", cartItem.getProdCgst());
                    productObject.put("prodSgst", cartItem.getProdSgst());
                    productObject.put("prodIgst", cartItem.getProdIgst());
                    productObject.put("prodSp", cartItem.getSellingPrice());

                    if(cartItem.getOfferItemCounter() > 0){
                        productObject.put("offerId", cartItem.getOfferId());
                        productObject.put("offerType", cartItem.getOfferType());
                        productObject.put("freeItems", String.valueOf(cartItem.getOfferItemCounter()));
                    }

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

    public float getTotalAmount(String shopCode) {
        totalTax = dbHelper.getTotalTaxesart(shopCode);
        totalPrice = dbHelper.getTotalPriceCart(shopCode) + totalTax;
        totDiscount = dbHelper.getTotalMrpPriceCart(shopCode) - dbHelper.getTotalPriceCart(shopCode);
        float dis = 0f;
        if (offerPer > 0f) {
            dis = totalPrice * offerPer / 100;
            totDiscount = totDiscount + dis;
        }

        Log.i(TAG, " Taxes " + dbHelper.getTotalTaxesart(shopCode));

        totalPrice = totalPrice - dis;
        if (deliveryTypeId == R.id.rb_self_delivery) {
            rlDelivery.setVisibility(View.GONE);
        } else {
            rlDelivery.setVisibility(View.VISIBLE);
            Log.i(TAG, "Min Delivery Distance " + sharedPreferences.getInt(Constants.MIN_DELIVERY_DISTANCE, 0));
            Log.i(TAG, "Charges after Delivery Distance " + sharedPreferences.getInt(Constants.CHARGE_AFTER_MIN_DISTANCE, 0));
            Log.i(TAG, "Delivery Distance " + deliveryDistance);
            if (deliveryDistance > sharedPreferences.getInt(Constants.MIN_DELIVERY_DISTANCE, 0)) {
                float diffKms = deliveryDistance - sharedPreferences.getInt(Constants.MIN_DELIVERY_DISTANCE, 0);
                int intKms = (int) diffKms;
                float decimalKms = diffKms - intKms;

                int chargesPerKm = sharedPreferences.getInt(Constants.CHARGE_AFTER_MIN_DISTANCE, 0);
                deliveryCharges = intKms * chargesPerKm + decimalKms * chargesPerKm;
            }
        }

        totalPrice = totalPrice + deliveryCharges;
        return  totalPrice;
    }


    @Override
    public void onJsonObjectResponse(JSONObject response, String apiName) {
        showProgress(false);
        try {
            // JSONObject jsonObject=response.getJSONObject("response");
            Log.d("response", response.toString());
             if (apiName.equals("generate_order")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)){
                    Log.d(TAG, "Ordeer Generated" );
                    orderNumber = response.getJSONObject("result").getString("orderNumber");
                    List<MyProduct> myProductList = new ArrayList<>();
                    List<String> shopCodeList = new ArrayList<>();
                    float totalCartAmout=0;
                    for(MyProduct myProduct: cartItemList){
                        if(!shopCodeList.contains(myProduct.getShopCode())){
                            shopCodeList.add(myProduct.getShopCode());
                            float totalAmount = dbHelper.getTotalAmount(myProduct.getShopCode());
                            totalCartAmout = totalAmount + totalCartAmout;
                            int totalItem = dbHelper.getTotalQuantity(myProduct.getShopCode());
                            myProduct.setTotalAmount(totalAmount);
                            myProduct.setQuantity(totalItem);
                            myProductList.add(myProduct);
                        }
                    }
                    for(MyProduct myProduct: myProductList){
                        Log.d("shopCode ", myProduct.getShopCode());
                        Log.d("TotalAmount ", ""+myProduct.getTotalAmount());
                        Log.d("TotalQuantity ", ""+myProduct.getQuantity());

                    }

                    itemList.clear();
                    myItemAdapter.notifyDataSetChanged();
                    if(paymentMode.equals("Cash")){
                        dbHelper.deleteTable(DbHelper.CART_TABLE);
                        dbHelper.deleteTable(DbHelper.PRODUCT_UNIT_TABLE);
                        dbHelper.deleteTable(DbHelper.PRODUCT_SIZE_TABLE);
                        dbHelper.deleteTable(DbHelper.PRODUCT_COLOR_TABLE);
                        dbHelper.deleteTable(DbHelper.PROD_FREE_OFFER_TABLE);
                        dbHelper.deleteTable(DbHelper.PROD_PRICE_TABLE);
                        dbHelper.deleteTable(DbHelper.PROD_PRICE_DETAIL_TABLE);
                        dbHelper.deleteTable(DbHelper.PROD_COMBO_TABLE);
                        dbHelper.deleteTable(DbHelper.PROD_COMBO_DETAIL_TABLE);
                        dbHelper.deleteTable(DbHelper.COUPON_TABLE);
                        /*for (MyProduct cartItem : cartItemList) {
                           dbHelper.setQoh(cartItem.getProdId(),-cartItem.getQty());
                            if(cartItem.getIsBarCodeAvailable().equals("Y")){
                                for(Barcode barcode : cartItem.getBarcodeList()){
                                    dbHelper.removeBarCode(barcode.getBarcode());
                                }
                            }
                        }*/


                        Intent intent = new Intent(CartActivity.this, TransactionDetailsActivity.class);
                        intent.putExtra("orderNumber", orderNumber);
                        String ta = tvNetPayable.getText().toString().split(" ")[1];
                        ta = ta.replaceAll(",", "");
                        intent.putExtra("totalAmount", Float.parseFloat(ta));
                        intent.putExtra("shopOrderList", (Serializable) myProductList);
                        startActivity(intent);
                        finish();
                    }else{
                        Log.d("shopArray ", shopArray.toString());
                        // integrate ccAvenue..
                        Intent intent = new Intent(CartActivity.this, CCAvenueWebViewActivity.class);
                        String ta = tvNetPayable.getText().toString().split(" ")[1];
                        ta = ta.replaceAll(",", "");
                        intent.putExtra(AvenuesParams.AMOUNT, String.format("%.02f", Float.parseFloat(ta)));
                        intent.putExtra(AvenuesParams.ORDER_ID, orderNumber);
                        intent.putExtra(AvenuesParams.CURRENCY, "INR");
                        intent.putExtra("flag", "online_shoping");
                        intent.putExtra("shopArray",shopArray.toString());
                        startActivity(intent);
                        finish();
                    }

                    //placeOrder(shopArray, orderId);
                }else {
                    DialogAndToast.showToast(response.getString("message"),CartActivity.this);
                }
            }else if(apiName.equals("place_order")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)){
                    dbHelper.deleteTable(DbHelper.CART_TABLE);
                    itemList.clear();
                    myItemAdapter.notifyDataSetChanged();
                    relativeLayoutCartFooter.setVisibility(View.GONE);
                    Log.d(TAG, "Ordeer Placed" );
                }else {
                    DialogAndToast.showToast(response.getString("message"),CartActivity.this);
                }
            }else if(apiName.equals("productDetails")){
                 if(response.getString("status").equals("true")||response.getString("status").equals(true)){
                     JSONObject jsonObject = response.getJSONObject("result");
                     if(productDetailsType == 1){
                        // MyProduct product = itemList.get(position);
                        // product.setQoh(jsonObject.getInt("prodQoh"));
                         itemList.get(position).setQoh(jsonObject.getInt("prodQoh"));
                         checkFreeProductOffer();
                     }else {
                         freeProdut = new MyProduct();
                         freeProdut.setId(jsonObject.getString("prodId"));
                         freeProdut.setCatId(jsonObject.getString("prodCatId"));
                         freeProdut.setSubCatId(jsonObject.getString("prodSubCatId"));
                         freeProdut.setName(jsonObject.getString("prodName"));
                         freeProdut.setQoh(jsonObject.getInt("prodQoh"));
                         freeProdut.setQuantity(1);
                         freeProdut.setFreeProductPosition(position+1);
                         freeProdut.setMrp(Float.parseFloat(jsonObject.getString("prodMrp")));
                         freeProdut.setSellingPrice(0);
                         freeProdut.setCode(jsonObject.getString("prodCode"));
                         freeProdut.setIsBarcodeAvailable(jsonObject.getString("isBarcodeAvailable"));
                         //myProduct.setBarCode(productJArray.getJSONObject(i).getString("prodBarCode"));
                         freeProdut.setDesc(jsonObject.getString("prodDesc"));
                         freeProdut.setLocalImage(R.drawable.thumb_16);
                         freeProdut.setProdImage1(jsonObject.getString("prodImage1"));
                         freeProdut.setProdImage2(jsonObject.getString("prodImage2"));
                         freeProdut.setProdImage3(jsonObject.getString("prodImage3"));
                         freeProdut.setProdHsnCode(jsonObject.getString("prodHsnCode"));
                         freeProdut.setProdMfgDate(jsonObject.getString("prodMfgDate"));
                         freeProdut.setProdExpiryDate(jsonObject.getString("prodExpiryDate"));
                         freeProdut.setProdMfgBy(jsonObject.getString("prodMfgBy"));
                         freeProdut.setProdExpiryDate(jsonObject.getString("prodExpiryDate"));
                         freeProdut.setOfferId(jsonObject.getString("offerId"));
                         freeProdut.setProdCgst(Float.parseFloat(jsonObject.getString("prodCgst")));
                         freeProdut.setProdIgst(Float.parseFloat(jsonObject.getString("prodIgst")));
                         freeProdut.setProdSgst(Float.parseFloat(jsonObject.getString("prodSgst")));
                         freeProdut.setProdWarranty(jsonObject.getString("prodWarranty"));
                         //myProduct.setSubCatName(subcatname);
                         onProductClicked(position, type);
                     }

                 }
             }
        } catch (JSONException e) {
            e.printStackTrace();
            DialogAndToast.showToast(getResources().getString(R.string.json_parser_error)+e.toString(),CartActivity.this);
        }
    }

    @Override
    public void onDialogPositiveClicked(){
      Intent intent = new Intent(CartActivity.this,InvoiceActivity.class);
      intent.putExtra("orderNumber",orderNumber);
      startActivity(intent);
      finish();
    }


    @Override
    public void onItemClicked(int position, int type) {
        this.position = position;
        this.type = type;
        if(type==2){
            productDetailsType = 1;
            getProductDetails(itemList.get(position).getId(), itemList.get(position).getShopCode());
        }else onProductClicked(position, type);
    }

    private void checkFreeProductOffer(){
        if(type ==2 && itemList.get(position).getProductDiscountOffer()!=null){
            ProductDiscountOffer productDiscountOffer = itemList.get(position).getProductDiscountOffer();
            if(productDiscountOffer.getProdBuyId()!= productDiscountOffer.getProdFreeId()){
                productDetailsType = 2;
                getProductDetails(String.valueOf(productDiscountOffer.getProdFreeId()),itemList.get(position).getShopCode());
            }else onProductClicked(position, type);
        }else {
            onProductClicked(position, type);
        }
    }

    private void getProductDetails(String prodId, String shopcode){
        Map<String,String> params=new HashMap<>();
        params.put("id", prodId); // as per user selected category from top horizontal categories list
        params.put("code", shopcode);
        params.put("dbName",shopcode);
        Log.d(TAG, params.toString());
        String url=getResources().getString(R.string.url)+"/products/ret_products_details";
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST, url,new JSONObject(params),"productDetails");
    }


    private  void onProductClicked(int position, int type){
        this.position = position;
        Log.i(TAG,"onItemClicked "+position+" type "+type);
        MyProduct item = (MyProduct) itemList.get(position);
        if(type == 1){
            if(itemList.size() == 1 && item.getQuantity() == 1){
                itemList.clear();
                //dbHelper.removeProductFromCart(item.getProdBarCode());
                dbHelper.removeProductFromCart(item.getId());
                relativeLayoutCartFooter.setVisibility(View.GONE);
                rlAddressBilling.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                linearLayoutScanCenter.setVisibility(View.VISIBLE);
                myItemAdapter.notifyDataSetChanged();
                offerPer = 0f;
                offerName = "";
                offerMaxAmount = 0f;
                offerId = 0;
                deliveryDistance = 0;

            }else if(itemList.size() > 1 && item.getQuantity() == 1){
                dbHelper.removeProductFromCart(item.getId());
                int qty = item.getQuantity() - 1;
                itemList.remove(position);
                /*for(int i=position; i<itemList.size();i++){
                    MyProduct product = itemList.get(i);
                    ProductDiscountOffer pdoffer = product.getProductDiscountOffer();
                    if(pdoffer!=null && product.getFreeProductPosition()>0) {
                        Log.d("Free Product name", product.getName());
                        Log.d("Free Product position", product.getFreeProductPosition()+"");
                        product.setFreeProductPosition(product.getFreeProductPosition() - 1);
                        dbHelper.updateFreeCartData(pdoffer.getProdFreeId(),product.getOfferItemCounter(),0f);
                        dbHelper.updateFreePositionCartData(product.getFreeProductPosition(), Integer.parseInt(product.getId()));
                    }
                }*/

               // myItemAdapter.notifyItemChanged(position);
                myItemAdapter.notifyDataSetChanged();
                setFooterValues();
            }else {
                int qty = item.getQuantity() - 1;
                float netSellingPrice = getOfferAmount(item,type);
                item.setQuantity(item.getQuantity());
                qty = item.getQuantity();
                Log.i(TAG,"netSellingPrice "+netSellingPrice);
                float amount = item.getTotalAmount() - netSellingPrice;
                Log.i(TAG,"tot amount "+amount);
                item.setTotalAmount(amount);
                dbHelper.updateCartData(item);
                setFooterValues();
                if(qty == 0){
                    itemList.remove(position);
                }
                myItemAdapter.notifyItemChanged(position);
                myItemAdapter.notifyDataSetChanged();
            }
        }else if(type == 2){
            /*if(item.getIsBarcodeAvailable().equals("Y")){
               *//* if(item.getQuantity() == item.getBarcodeList().size()){
                    DialogAndToast.showDialog("There are no more stocks",this);
                }else{*//*
                    int qty = item.getQuantity() + 1;
                    item.setQuantity(qty);
                    Log.i(TAG,"qty "+qty);
                    float netSellingPrice = getOfferAmount(item,type);
                    qty = item.getQuantity();
                    Log.i(TAG,"netSellingPrice "+netSellingPrice);
                    float amount = item.getTotalAmount() + netSellingPrice;
                    Log.i(TAG,"tot amount "+amount);
                    item.setTotalAmount(amount);
                    dbHelper.updateCartData(item);
                    if(itemList.size() == 1){
                        relativeLayoutCartFooter.setVisibility(View.VISIBLE);
                    }
                    setFooterValues();
                    myItemAdapter.notifyItemChanged(position);
                //}

            }else{*/
                if(item.getQuantity() >= item.getQoh()){
                    DialogAndToast.showDialog("There are no more stocks",this);
                }else{
                    int qty = item.getQuantity() + 1;
                    item.setQuantity(qty);
                    float netSellingPrice = getOfferAmount(item,type);
                    Log.i(TAG,"netSellingPrice "+netSellingPrice);
                    float amount = item.getTotalAmount() + netSellingPrice;
                    Log.i(TAG,"tot amount "+amount);
                    item.setTotalAmount(amount);
                    qty = item.getQuantity();
                    item.setQuantity(item.getQuantity());
                    Log.i(TAG,"qty "+qty);
                    dbHelper.updateCartData(item);
                    if(itemList.size() == 1){
                        relativeLayoutCartFooter.setVisibility(View.VISIBLE);
                    }
                    setFooterValues();
                    myItemAdapter.notifyItemChanged(position);
                }
          //  }

        }else if(type == 3){

        }
    }

    private float getOfferAmount(MyProduct item,int type){
        Log.d("item "+item.getName(), " type "+type);
        float amount = 0f;
        int qty = item.getQuantity();
        if(item.getProductPriceOffer()!=null){
            ProductPriceOffer productPriceOffer = (ProductPriceOffer)item.getProductPriceOffer();
            if(qty > 1){
                int maxSize = productPriceOffer.getProductPriceDetails().size();
                int mod = qty % maxSize;
                Log.i(TAG,"mod "+mod);
                if(mod == 0){
                    mod = maxSize;
                }
                amount = getOfferPrice(mod,item.getSellingPrice(),productPriceOffer.getProductPriceDetails());
            }else{
                amount = item.getSellingPrice();
            }

            if(type == 1)
            item.setQuantity(item.getQuantity() - 1);

        }else if(item.getProductDiscountOffer()!=null){

            ProductDiscountOffer productDiscountOffer = (ProductDiscountOffer)item.getProductDiscountOffer();
            amount = item.getSellingPrice();
            if(type == 1){
                if(productDiscountOffer.getProdBuyId() == productDiscountOffer.getProdFreeId()){
                    Log.i(TAG,"item qty "+item.getQuantity()+" offer buy qty"+productDiscountOffer.getProdBuyQty());
                    Log.i(TAG,"minus mode "+(item.getQuantity() - item.getOfferItemCounter()-1)% productDiscountOffer.getProdBuyQty());
                    if((item.getQuantity() - item.getOfferItemCounter() -1)% productDiscountOffer.getProdBuyQty() ==
                            (productDiscountOffer.getProdBuyQty()-1)){
                        item.setQuantity(item.getQuantity() - 2);
                        item.setOfferItemCounter(item.getOfferItemCounter() - 1);
                        dbHelper.updateOfferCounterCartData(item.getOfferItemCounter(),Integer.parseInt(item.getId()), item.getShopCode());
                        Log.d(TAG, "minus mode -2 ");
                    }else{
                        item.setQuantity(item.getQuantity() - 1);
                        Log.d(TAG, "minus mode -1 ");
                    }
                }else{
                    item.setQuantity(item.getQuantity() - 1);
                    Log.i(TAG,"minus mode "+item.getQuantity() % productDiscountOffer.getProdBuyQty());
                    if(item.getQuantity() % productDiscountOffer.getProdBuyQty() == (productDiscountOffer.getProdBuyQty()-1)){
                        item.setOfferItemCounter(item.getOfferItemCounter() - 1);
                        if(item.getOfferItemCounter() == 0){
                            dbHelper.removeFreeProductFromCart(productDiscountOffer.getProdFreeId());
                            itemList.remove(item.getFreeProductPosition());
                          //  myItemAdapter.notifyItemRemoved(item.getFreeProductPosition());
                            myItemAdapter.notifyDataSetChanged();
                        }else{
                            MyProduct item1 = itemList.get(item.getFreeProductPosition());
                            item1.setQuantity(item.getOfferItemCounter());
                            dbHelper.updateFreeCartData(productDiscountOffer.getProdFreeId(),item.getOfferItemCounter(),0f);
                            myItemAdapter.notifyItemChanged(item.getFreeProductPosition());
                            dbHelper.updateOfferCounterCartData(item.getOfferItemCounter(), Integer.parseInt(item.getId()), item.getShopCode());
                        }
                    }

                }
            }else if(type == 2){
                if(productDiscountOffer.getProdBuyId() == productDiscountOffer.getProdFreeId()){
                    Log.i(TAG,"Same product");
                    Log.i(TAG,"item qty "+item.getQuantity()+" offer buy qty"+productDiscountOffer.getProdBuyQty());
                    Log.i(TAG,"plus mode "+(item.getQuantity() - item.getOfferItemCounter())% productDiscountOffer.getProdBuyQty());
                    Log.d(TAG, " offerCounter "+item.getOfferItemCounter() +" shopCode "+item.getShopCode() +" Qyantity "+item.getQuantity() +" prodId "+item.getId() +" offer Amount "+amount);
                    if((item.getQuantity() - item.getOfferItemCounter())% productDiscountOffer.getProdBuyQty() == 0){
                        item.setQuantity(item.getQuantity() + 1);
                        item.setOfferItemCounter(item.getOfferItemCounter() + 1);
                        dbHelper.updateOfferCounterCartData(item.getOfferItemCounter(),Integer.parseInt(item.getId()), item.getShopCode());
                    }else{

                    }
                }else{
                    Log.i(TAG,"Different product");
                   if(item.getQuantity() % productDiscountOffer.getProdBuyQty() == 0){
                       item.setOfferItemCounter(item.getOfferItemCounter() + 1);
                       MyProduct item1 = null;
                       if(item.getOfferItemCounter() == 1){
                          // item1 = dbHelper.getProductDetails(productDiscountOffer.getProdFreeId());
                           // integrate Api
                           item1 = freeProdut;
                           item1.setShopCode(shopCode);
                           item1.setSellingPrice(0f);
                           item1.setQuantity(1);
                           item1.setFreeProductPosition(position+1);
                           dbHelper.addProductToCart(item1);
                           itemList.add(position+1,item1);
                           item.setFreeProductPosition(position+1);
                           dbHelper.updateFreePositionCartData(item.getFreeProductPosition(),Integer.parseInt(item.getId()));
                           dbHelper.updateOfferCounterCartData(item.getOfferItemCounter(),Integer.parseInt(item.getId()), item.getShopCode());
                           //myItemAdapter.notifyItemInserted(itemList.size()-1);
                           myItemAdapter.notifyDataSetChanged();
                           Log.i(TAG,"Different product added to cart");
                       }else{
                           item1 = itemList.get(item.getFreeProductPosition());
                           item1.setQuantity(item.getOfferItemCounter());
                           item1.setTotalAmount(0f);
                           dbHelper.updateFreeCartData(Integer.parseInt(item1.getId()),item1.getQuantity(),0f);
                           dbHelper.updateOfferCounterCartData(item.getOfferItemCounter(),Integer.parseInt(item.getId()), item.getShopCode());
                           myItemAdapter.notifyItemChanged(item.getFreeProductPosition());
                           Log.i(TAG,"Different product updated in cart");
                       }
                     //  myItemAdapter.notifyDataSetChanged();
                   }

                }

            }else{
                amount = item.getSellingPrice();
            }

        }else{
            amount = item.getSellingPrice();
            if(type == 1)
                item.setQuantity(item.getQuantity() - 1);
        }

        return amount;
    }

    private float getOfferPrice(int qty,float sp,List<ProductPriceDetails> productPriceDetailsList){
        float amount = 0f;
        for(ProductPriceDetails productPriceDetails:productPriceDetailsList){
            if(productPriceDetails.getPcodProdQty() == qty){
                amount = productPriceDetails.getPcodPrice();
                Log.i(TAG,"offer price "+amount);
                break;
            }else{
                amount = sp;
            }
        }
        Log.i(TAG,"final selling price "+amount);
        return amount;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cart_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            BottomSearchFragment bottomSearchFragment = new BottomSearchFragment();
            Bundle bundle = new Bundle();
            bundle.putString("shopCode", shopCode);
            bundle.putString("shopName", shopName);
            bundle.putString("shopAddress", "");
            bundle.putString("shopMobile", "");
            bottomSearchFragment.setArguments(bundle);
            bottomSearchFragment.setCallingActivityName("CartActivity", sharedPreferences, isDarkTheme);
            bottomSearchFragment.setSubCatName("");
            bottomSearchFragment.setSubcatId("");
            bottomSearchFragment.show(getSupportFragmentManager(), bottomSearchFragment.getTag());
            return true;
        }else if (id == android.R.id.home) {
            super.onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClicked(int prodId) {

      //  Log.i(TAG,"item clicked "+prodId+" "+dbHelper.getBarCodesForCart(prodId).size());
     //   MyProduct item = dbHelper.getProductDetails(prodId);
        MyProduct item = null;
       // integrate Api
       setOffer(item);
       // item.setProductPriceOfferList(dbHelper.getProductPriceOffer(""+item.getProdId()));

        /*if(item.getIsBarcodeAvailable().equals("Y")){
          //  item.setBarcodeList(dbHelper.getBarCodesForCart(prodId));
            if(item.getBarcodeList() != null && item.getBarcodeList().size() > 0){
                item.setQuantity(1);
                item.setTotalAmount(item.getSellingPrice());
                dbHelper.addProductToCart(item);
                itemList.add(item);
                myItemAdapter.notifyDataSetChanged();
                if(itemList.size() > 0){
                    setFooterValues();
                    relativeLayoutCartFooter.setVisibility(View.VISIBLE);
                }
            }else{
                DialogAndToast.showDialog("Product is out of stock.",this);
            }

        }else{*/
            if(item.getQuantity() <= item.getQoh() ){
                item.setQuantity(1);
                item.setTotalAmount(item.getSellingPrice());
                dbHelper.addProductToCart(item);
                itemList.add(item);
                //myItemAdapter.notifyItemChanged(itemList.size() -1 );
                myItemAdapter.notifyDataSetChanged();
                if(itemList.size() > 0){
                    setFooterValues();
                    relativeLayoutCartFooter.setVisibility(View.VISIBLE);
                }
            }else{
                DialogAndToast.showDialog("Product is out of stock.",this);
            }
        //}
    }

    private void setOffer(MyProduct item){
        List<ProductPriceOffer> productPriceOfferList = dbHelper.getProductPriceOffer(""+item.getId());
        List<ProductDiscountOffer> productDiscountOfferList = dbHelper.getProductFreeOffer(""+item.getId());
        Log.i(TAG,"comboOfferList size "+productPriceOfferList.size());
        Log.i(TAG,"productDiscountOfferList size "+productDiscountOfferList.size());

        if(productPriceOfferList.size() > 0){
            item.setProductOffer(productPriceOfferList.get(0));
            item.setOfferId(""+productPriceOfferList.get(0).getId());
            item.setOfferType("price");
        }else if(productDiscountOfferList.size() > 0){
            item.setProductOffer(productDiscountOfferList.get(0));
            item.setOfferId(""+productDiscountOfferList.get(0).getId());
            item.setOfferType("free");
        }
    }

    public void showOfferDescription(MyProduct item){
        OfferDescriptionFragment offerDescriptionFragment = new OfferDescriptionFragment();
        offerDescriptionFragment.setProduct(item);
        offerDescriptionFragment.setColorTheme(colorTheme);
        offerDescriptionFragment.show(getSupportFragmentManager(), "Offer Description Bottom Sheet");
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.i(TAG,"onResume called.");
        if(itemList == null){
            itemList = dbHelper.getCartProducts();
        }else{
            itemList.clear();
            List<MyProduct> cartList =  dbHelper.getCartProducts();
            itemList.addAll(cartList);
        }
        if(itemList.size() > 0){
            setFooterValues();
            relativeLayoutCartFooter.setVisibility(View.VISIBLE);
            btnStoreOffers.setVisibility(View.VISIBLE);
            myItemAdapter.notifyDataSetChanged();
        }else{
            relativeLayoutCartFooter.setVisibility(View.GONE);
            linearLayoutScanCenter.setVisibility(View.VISIBLE);
            btnStoreOffers.setVisibility(View.GONE);
        }
    }
}
