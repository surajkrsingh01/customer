package com.shoppurscustomer.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.shoppurscustomer.R;
import com.shoppurscustomer.activities.ApplyOffersActivity;
import com.shoppurscustomer.activities.CartActivity;
import com.shoppurscustomer.activities.ShopProductListActivity;
import com.shoppurscustomer.activities.TransactionDetailsActivity;
import com.shoppurscustomer.adapters.SearchProductAdapter;
import com.shoppurscustomer.adapters.SearchShopAdapter;
import com.shoppurscustomer.database.DbHelper;
import com.shoppurscustomer.interfaces.MyItemTypeClickListener;
import com.shoppurscustomer.models.Barcode;
import com.shoppurscustomer.models.MyProduct;
import com.shoppurscustomer.models.MyShop;
import com.shoppurscustomer.models.ProductColor;
import com.shoppurscustomer.models.ProductDiscountOffer;
import com.shoppurscustomer.models.ProductPriceDetails;
import com.shoppurscustomer.models.ProductPriceOffer;
import com.shoppurscustomer.models.ProductSize;
import com.shoppurscustomer.models.ProductUnit;
import com.shoppurscustomer.utilities.AppController;
import com.shoppurscustomer.utilities.Constants;
import com.shoppurscustomer.utilities.DialogAndToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by suraj kumar singh on 18-04-2019.
 */

public class BottomSearchFragment extends BottomSheetDialogFragment implements MyItemTypeClickListener {
    private String TAG = "BottomSearchFragment";
    private ImageView iv_clear;
    private EditText et_search;
    private RecyclerView recyclerView_Search;
    private SearchShopAdapter shopAdapter;
    private SearchProductAdapter productAdapter;
    private List<MyProduct> myProductList;
    private List<MyShop> myShopList;
    private String callingActivityName, shopCode,catId, subCatName, subcatId;
    private RelativeLayout relative_header;
    private ProgressDialog progressDialog;
    protected SharedPreferences sharedPreferences;
    private boolean isDarkTheme;
    private DbHelper dbHelper;

    public BottomSearchFragment() {

    }

    public void setCallingActivityName(String name, SharedPreferences sharedPreferences, boolean isDarkTheme){
     this.callingActivityName = name;
     this.sharedPreferences = sharedPreferences;
     this.isDarkTheme = isDarkTheme;
    }

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public void setSubCatName(String name){
        this.subCatName = name;
    }

    public void setSubcatId(String id){
        this.subcatId = id;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         View view = inflater.inflate(R.layout.fragment_bottom_sheet_dialog, container, false);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCanceledOnTouchOutside(false);
        dbHelper = new DbHelper(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
         iv_clear = view.findViewById(R.id.iv_clear);
         et_search = view.findViewById(R.id.et_search);
         recyclerView_Search = view.findViewById(R.id.recyclerView_Search);
         relative_header = view.findViewById(R.id.relative_header);
       /* BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(relative_header);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);*/
//         Log.d(TAG, callingActivityName);

        iv_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSearchFragment.this.dismiss();
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
                if(callingActivityName.equals("ShopListActivity"))
                    searchShops(s.toString());
                else if(callingActivityName.equals("ShopProductListActivity") || callingActivityName.equals("CartActivity")) {
                    Bundle bundle = getArguments();
                    shopCode = bundle.getString("shopCode");
                    searchProducts(s.toString());
                }
            }
        });

        return view;
    }

    private void searchShops(String query){
        myShopList = new ArrayList<>();
        recyclerView_Search.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getActivity());
        recyclerView_Search.setLayoutManager(layoutManager);
        recyclerView_Search.setItemAnimator(new DefaultItemAnimator());
        shopAdapter=new SearchShopAdapter(getContext(),myShopList);
        shopAdapter.setSubCatid(subcatId);
        shopAdapter.setSubCatName(subCatName);
        recyclerView_Search.setAdapter(shopAdapter);

        Map<String,String> params=new HashMap<>();
        params.put("query",query);
        params.put("limit", "10");
        params.put("offset", ""+myShopList.size());
        params.put("dbName",sharedPreferences.getString(Constants.DB_NAME,""));
        params.put("dbUserName",sharedPreferences.getString(Constants.DB_USER_NAME,""));
        params.put("dbPassword",sharedPreferences.getString(Constants.DB_PASSWORD,""));
        String url=getResources().getString(R.string.root_url)+"/search/shops";
        //showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"searchShops");
    }

    private void searchProducts(String query){
        myProductList = new ArrayList<>();
        recyclerView_Search.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getActivity());
        recyclerView_Search.setLayoutManager(layoutManager);
        recyclerView_Search.setItemAnimator(new DefaultItemAnimator());
        productAdapter = new SearchProductAdapter(getContext(),myProductList, callingActivityName);
        productAdapter.setMyItemClickListener(this);
        productAdapter.setDarkTheme(isDarkTheme);
        productAdapter.setShopCode(shopCode);
        recyclerView_Search.setAdapter(productAdapter);

        //"code":"SHP4","dbName":"SHP4","id":"3","dbUserName":"SHPC1","dbPassword":"SHPC1"

        Map<String,String> params=new HashMap<>();
        params.put("query",query);
        params.put("shopCode", shopCode);
        params.put("limit", "10");
        params.put("offset", ""+myProductList.size());
        params.put("dbName",sharedPreferences.getString(Constants.DB_NAME,""));
        params.put("dbUserName",sharedPreferences.getString(Constants.DB_USER_NAME,""));
        params.put("dbPassword",sharedPreferences.getString(Constants.DB_PASSWORD,""));
        String url=getResources().getString(R.string.root_url)+"search/shop_products";
        //showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"searchProducts");
    }

    public void onJsonObjectResponse(JSONObject response, String apiName) {
        showProgress(false);
        try {
            // JSONObject jsonObject=response.getJSONObject("response");
            Log.d("response", response.toString());
            if(apiName.equals("searchShops")) {
                if (response.getString("status").equals("true") || response.getString("status").equals(true)) {
                    JSONArray shopJArray = response.getJSONArray("result");
                    for (int i = 0; i < shopJArray.length(); i++) {
                        MyShop myShop = new MyShop();
                        String shop_code = shopJArray.getJSONObject(i).getString("retcode");
                        myShop.setId(shop_code);
                        Log.d("shop_id", myShop.getId());
                        myShop.setName(shopJArray.getJSONObject(i).getString("retshopname"));
                        myShop.setMobile(shopJArray.getJSONObject(i).getString("retmobile"));
                        myShop.setAddress(shopJArray.getJSONObject(i).getString("retaddress"));
                        myShop.setState(shopJArray.getJSONObject(i).getString("retcountry"));
                        myShop.setCity(shopJArray.getJSONObject(i).getString("retcity"));
                        myShop.setShopimage(shopJArray.getJSONObject(i).getString("retphoto"));

                        myShop.setDbname(shopJArray.getJSONObject(i).getString("dbname"));
                        myShop.setDbusername(shopJArray.getJSONObject(i).getString("dbuser"));
                        myShop.setDbpassword(shopJArray.getJSONObject(i).getString("dbpassword"));
                        myShop.setImage(R.drawable.thumb_21);
                        myShopList.add(myShop);
                    }
                    if (myShopList.size() > 0)
                        shopAdapter.notifyDataSetChanged();
                    else {
                        myShopList.clear();
                        shopAdapter.notifyDataSetChanged();
                    }

                } else {
                    DialogAndToast.showDialog(response.getString("message"), getContext());
                }
            }
            else if(apiName.equals("searchProducts")) {
                if(response.getString("status").equals("true")||response.getString("status").equals(true)) {
                    JSONObject dataObject;
                    if(!response.getString("result").equals("null")){
                        JSONArray productJArray = response.getJSONArray("result");

                        for (int i = 0; i < productJArray.length(); i++) {
                            MyProduct myProduct = new MyProduct();
                            myProduct.setShopCode(shopCode);
                            myProduct.setId(productJArray.getJSONObject(i).getString("prodId"));
                            myProduct.setCatId(productJArray.getJSONObject(i).getString("prodCatId"));
                            myProduct.setSubCatId(productJArray.getJSONObject(i).getString("prodSubCatId"));
                            myProduct.setName(productJArray.getJSONObject(i).getString("prodName"));
                            myProduct.setQoh(productJArray.getJSONObject(i).getInt("prodQoh"));
                            myProduct.setMrp(Float.parseFloat(productJArray.getJSONObject(i).getString("prodMrp")));
                            myProduct.setSellingPrice(Float.parseFloat(productJArray.getJSONObject(i).getString("prodSp")));
                            myProduct.setCode(productJArray.getJSONObject(i).getString("prodCode"));
                            myProduct.setIsBarcodeAvailable(productJArray.getJSONObject(i).getString("isBarcodeAvailable"));
                            //myProduct.setBarCode(productJArray.getJSONObject(i).getString("prodBarCode"));
                            myProduct.setDesc(productJArray.getJSONObject(i).getString("prodDesc"));
                            myProduct.setLocalImage(R.drawable.thumb_16);
                            myProduct.setProdImage1(productJArray.getJSONObject(i).getString("prodImage1"));
                            myProduct.setProdImage2(productJArray.getJSONObject(i).getString("prodImage2"));
                            myProduct.setProdImage3(productJArray.getJSONObject(i).getString("prodImage3"));
                            myProduct.setProdHsnCode(productJArray.getJSONObject(i).getString("prodHsnCode"));
                            myProduct.setProdMfgDate(productJArray.getJSONObject(i).getString("prodMfgDate"));
                            myProduct.setProdExpiryDate(productJArray.getJSONObject(i).getString("prodExpiryDate"));
                            myProduct.setProdMfgBy(productJArray.getJSONObject(i).getString("prodMfgBy"));
                            myProduct.setProdExpiryDate(productJArray.getJSONObject(i).getString("prodExpiryDate"));
                            myProduct.setOfferId(productJArray.getJSONObject(i).getString("offerId"));
                            myProduct.setProdCgst(Float.parseFloat(productJArray.getJSONObject(i).getString("prodCgst")));
                            myProduct.setProdIgst(Float.parseFloat(productJArray.getJSONObject(i).getString("prodIgst")));
                            myProduct.setProdSgst(Float.parseFloat(productJArray.getJSONObject(i).getString("prodSgst")));
                            myProduct.setProdWarranty(productJArray.getJSONObject(i).getString("prodWarranty"));
                            //myProduct.setSubCatName(subcatname);


                            int innerLen = 0;
                            int len = 0;

                            JSONArray tempArray = null;
                            JSONObject jsonObject = null, tempObject = null;
                            ProductUnit productUnit = null;
                            ProductSize productSize = null;
                            ProductColor productColor = null;

                            if (!productJArray.getJSONObject(i).getString("productUnitList").equals("null")) {
                                tempArray = productJArray.getJSONObject(i).getJSONArray("productUnitList");
                                int tempLen = tempArray.length();
                                List<ProductUnit> productUnitList = new ArrayList<>();

                                for (int unitCounter = 0; unitCounter < tempLen; unitCounter++) {
                                    tempObject = tempArray.getJSONObject(unitCounter);
                                    productUnit = new ProductUnit();
                                    productUnit.setId(tempObject.getInt("id"));
                                    productUnit.setUnitName(tempObject.getString("unitName"));
                                    productUnit.setUnitValue(tempObject.getString("unitValue"));
                                    productUnit.setStatus(tempObject.getString("status"));
                                    productUnitList.add(productUnit);
                                }
                                myProduct.setProductUnitList(productUnitList);
                            }

                            if (!productJArray.getJSONObject(i).getString("productSizeList").equals("null")) {
                                tempArray = productJArray.getJSONObject(i).getJSONArray("productSizeList");
                                int tempLen = tempArray.length();
                                List<ProductSize> productSizeList = new ArrayList<>();

                                for (int unitCounter = 0; unitCounter < tempLen; unitCounter++) {
                                    List<ProductColor> productColorList = new ArrayList<>();
                                    tempObject = tempArray.getJSONObject(unitCounter);
                                    productSize = new ProductSize();
                                    productSize.setId(tempObject.getInt("id"));
                                    productSize.setSize(tempObject.getString("size"));
                                    productSize.setStatus(tempObject.getString("status"));
                                    productSize.setProductColorList(productColorList);

                                    if (!productJArray.getJSONObject(i).getString("productSizeList").equals("null")) {
                                        JSONArray colorArray = tempObject.getJSONArray("productColorList");
                                        for (int colorCounter = 0; colorCounter < colorArray.length(); colorCounter++) {
                                            tempObject = colorArray.getJSONObject(colorCounter);
                                            productColor = new ProductColor();
                                            productColor.setId(tempObject.getInt("id"));
                                            productColor.setSizeId(tempObject.getInt("sizeId"));
                                            productColor.setColorName(tempObject.getString("colorName"));
                                            productColor.setColorValue(tempObject.getString("colorValue"));
                                            productColor.setStatus(tempObject.getString("status"));
                                            productColorList.add(productColor);
                                        }

                                    }
                                    productSizeList.add(productSize);
                                }
                                myProduct.setProductSizeList(productSizeList);
                            }


                            if (!productJArray.getJSONObject(i).getString("barcodeList").equals("null")) {
                                JSONArray productBarCodeArray = productJArray.getJSONObject(i).getJSONArray("barcodeList");
                                len = productBarCodeArray.length();
                                JSONObject barcodeJsonObject = null;
                                List<Barcode> barcodeList = new ArrayList<>();
                                for (int j = 0; j < len; j++) {
                                    barcodeJsonObject = productBarCodeArray.getJSONObject(j);
                                    barcodeList.add(new Barcode(barcodeJsonObject.getString("barcode")));
                                    // dbHelper.addProductBarcode(jsonObject.getInt("prodId"),jsonObject.getString("prodBarCode"));
                                }
                                myProduct.setBarcodeList(barcodeList);
                            }

                            if (!productJArray.getJSONObject(i).getString("productDiscountOfferList").equals("null")) {
                                JSONArray freeArray = productJArray.getJSONObject(i).getJSONArray("productDiscountOfferList");
                                len = freeArray.length();
                                ProductDiscountOffer productDiscountOffer = null;
                                for (int k = 0; k < len; k++) {
                                    dataObject = freeArray.getJSONObject(k);
                                    Log.d("index ", "" + len);
                                    productDiscountOffer = new ProductDiscountOffer();
                                    productDiscountOffer.setId(dataObject.getInt("id"));
                                    productDiscountOffer.setOfferName(dataObject.getString("offerName"));
                                    productDiscountOffer.setProdBuyId(dataObject.getInt("prodBuyId"));
                                    productDiscountOffer.setProdFreeId(dataObject.getInt("prodFreeId"));
                                    productDiscountOffer.setProdBuyQty(dataObject.getInt("prodBuyQty"));
                                    productDiscountOffer.setProdFreeQty(dataObject.getInt("prodFreeQty"));
                                    productDiscountOffer.setStatus(dataObject.getString("status"));
                                    productDiscountOffer.setStartDate(dataObject.getString("startDate"));
                                    productDiscountOffer.setEndDate(dataObject.getString("endDate"));

                                    //myProduct.setproductoffer
                                    myProduct.setOfferId(String.valueOf(productDiscountOffer.getId()));
                                    myProduct.setOfferType("free");
                                    myProduct.setProductOffer(productDiscountOffer);

                                    //  dbHelper.addProductFreeOffer(productDiscountOffer, Utility.getTimeStamp(),Utility.getTimeStamp());
                                }
                                myProduct.setProductDiscountOffer(productDiscountOffer);
                            }


                            if (!productJArray.getJSONObject(i).getString("productPriceOffer").equals("null")) {
                                JSONArray priceArray = productJArray.getJSONObject(i).getJSONArray("productPriceOffer");
                                len = priceArray.length();
                                JSONArray productPriceArray = null;
                                ProductPriceOffer productPriceOffer = null;
                                ProductPriceDetails productPriceDetails;
                                List<ProductPriceDetails> productPriceOfferDetails = null;
                                for (int l = 0; l < len; l++) {
                                    dataObject = priceArray.getJSONObject(l);
                                    productPriceOffer = new ProductPriceOffer();
                                    productPriceOffer.setId(dataObject.getInt("id"));
                                    productPriceOffer.setProdId(dataObject.getInt("prodId"));
                                    productPriceOffer.setOfferName(dataObject.getString("offerName"));
                                    productPriceOffer.setStatus(dataObject.getString("status"));
                                    productPriceOffer.setStartDate(dataObject.getString("startDate"));
                                    productPriceOffer.setEndDate(dataObject.getString("endDate"));
                                    productPriceArray = dataObject.getJSONArray("productComboOfferDetails");

                                    myProduct.setOfferId(String.valueOf(productPriceOffer.getId()));
                                    myProduct.setOfferType("price");
                                    myProduct.setProductOffer(productPriceOffer);


                                    productPriceOfferDetails = new ArrayList<>();
                                    innerLen = productPriceArray.length();
                                    for (int k = 0; k < innerLen; k++) {
                                        dataObject = productPriceArray.getJSONObject(k);
                                        productPriceDetails = new ProductPriceDetails();
                                        productPriceDetails.setId(dataObject.getInt("id"));
                                        productPriceDetails.setPcodPcoId(dataObject.getInt("pcodPcoId"));
                                        productPriceDetails.setPcodProdQty(dataObject.getInt("pcodProdQty"));
                                        productPriceDetails.setPcodPrice((float) dataObject.getDouble("pcodPrice"));
                                        productPriceDetails.setStatus(dataObject.getString("status"));
                                        productPriceOfferDetails.add(productPriceDetails);
                                    }
                                    productPriceOffer.setProductPriceDetails(productPriceOfferDetails);
                                }
                                myProduct.setProductPriceOffer(productPriceOffer);
                            }


                            myProductList.add(myProduct);
                        }
                    }
                    if(myProductList.size()>0){
                        productAdapter.notifyDataSetChanged();
                    }else {
                       // showNoData(true);
                    }

                }else {
                    DialogAndToast.showDialog(response.getString("message"), getContext());
                }
            }else if(apiName.equals("productDetails")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)){
                    JSONObject jsonObject = response.getJSONObject("result");
                    if(productDetailsType == 1){
                       myProductList.get(position).setQoh(jsonObject.getInt("prodQoh"));
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
                        onProductClicked(type, position);
                    }

                }else {
                    DialogAndToast.showToast("Something went wrong, Please try again", getContext());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            DialogAndToast.showToast(getResources().getString(R.string.json_parser_error)+e.toString(),getContext());
        }
    }

    public void jsonObjectApiRequest(int method,String url, JSONObject jsonObject, final String apiName){
        Log.i(TAG,url);
        Log.i(TAG,jsonObject.toString());
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(method,url,jsonObject,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                AppController.getInstance().getRequestQueue().getCache().clear();
                Log.i(TAG,response.toString());
                showProgress(false);
                onJsonObjectResponse(response,apiName);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                AppController.getInstance().getRequestQueue().getCache().clear();
                Log.i(TAG,"Json Error "+error.toString());
                showProgress(false);
            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    void showProgress(boolean show){
        if(show){
            progressDialog.show();
        }else{
            progressDialog.dismiss();
        }
    }

    private int position, type, productDetailsType, counter;
    private  MyProduct myProduct, freeProdut;
    @Override
    public void onItemClicked(int position, int type) {
        Log.d("clicked Position "+position, "type "+type);
        this.position = position;
        this.type = type;
        if(type==2){
            productDetailsType = 1;
            getProductDetails(myProductList.get(position).getId());
        }else onProductClicked(type, position);
    }

    private void onProductClicked(int type, int position){
        this.position = position;
        this.myProduct = myProductList.get(position);
       // myProduct.setQuantity(myProduct.getQuantity());
        myProduct.setShopCode(shopCode);
       // myProduct.setSellingPrice(myProduct.getSellingPrice());
       // myProduct.setTotalAmount(myProduct.getSellingPrice() * myProduct.getQuantity());

        if(type == 1){
            if(myProduct.getQuantity() > 0){
                if(myProduct.getQuantity() == 1){
                    counter--;
                    //dbHelper.removeProductFromCart(myProduct.getProdBarCode());
                    dbHelper.removeProductFromCart(myProduct.getId());
                    if(myProduct.getProductDiscountOffer()!=null){
                        ProductDiscountOffer productDiscountOffer = (ProductDiscountOffer)myProduct.getProductOffer();

                        if(productDiscountOffer.getProdBuyId() != productDiscountOffer.getProdFreeId())
                            dbHelper.removeFreeProductFromCart(productDiscountOffer.getProdFreeId());
                    }
                    myProduct.setQuantity(0);
                    productAdapter.notifyItemChanged(position);
                    if(callingActivityName.equals("ShopProductListActivity"))
                        ((ShopProductListActivity)getContext()).updateCartCount();
                    else if(callingActivityName.equals("CartActivity"))
                        ((CartActivity)getContext()).setFooterValues();
                }else{
                    int qty = myProduct.getQuantity() - 1;
                    float netSellingPrice = getOfferAmount(myProduct,type);
                    myProduct.setQuantity(qty);
                    qty = myProduct.getQuantity();
                    Log.i(TAG,"netSellingPrice "+netSellingPrice);
                    float amount = myProduct.getTotalAmount() - netSellingPrice;
                    Log.i(TAG,"tot amount "+amount);
                    myProduct.setTotalAmount(amount);
                    dbHelper.updateCartData(myProduct);
                    productAdapter.notifyItemChanged(position);
                    if(callingActivityName.equals("ShopProductListActivity"))
                        ((ShopProductListActivity)getContext()).updateCartCount();
                    else if(callingActivityName.equals("CartActivity"))
                        ((CartActivity)getContext()).setFooterValues();
                }
            }

        }else if(type == 2){
            /*if(myProduct.getIsBarcodeAvailable().equals("Y")){
                if(myProduct.getQuantity() == myProduct.getBarcodeList().size()){
                    DialogAndToast.showDialog("There are no more stocks",getContext());
                }else{
                    int qty = myProduct.getQuantity() + 1;
                    if(qty == 1){
                        counter++;
                        myProduct.setFreeProductPosition(counter);
                        dbHelper.addProductToCart(myProduct);
                    }
                    myProduct.setQuantity(qty);
                    Log.i(TAG,"qty "+qty);
                    float netSellingPrice = getOfferAmount(myProduct,type);
                    qty = myProduct.getQuantity();
                    Log.i(TAG,"netSellingPrice "+netSellingPrice);
                    float amount = myProduct.getTotalAmount() + netSellingPrice;
                    Log.i(TAG,"tot amount "+amount);
                    myProduct.setTotalAmount(amount);
                    dbHelper.updateCartData(myProduct);
                    productAdapter.notifyItemChanged(position);
                    if(callingActivityName.equals("ShopProductListActivity"))
                        ((ShopProductListActivity)getContext()).updateCartCount();
                    else if(callingActivityName.equals("CartActivity"))
                        ((CartActivity)getContext()).setFooterValues();
                }

            }else{*/
                if(myProduct.getQuantity() == myProduct.getQoh()){
                    productAdapter.notifyDataSetChanged();
                    DialogAndToast.showDialog("There are no more stocks",getContext());
                }else{
                    int qty = myProduct.getQuantity() + 1;
                    myProduct.setQuantity(qty);
                    if(qty == 1){
                        counter++;
                        myProduct.setFreeProductPosition(counter);
                        dbHelper.addProductToCart(myProduct);
                    }
                    float netSellingPrice = getOfferAmount(myProduct,type);
                    Log.i(TAG,"netSellingPrice "+netSellingPrice);
                    float amount = myProduct.getTotalAmount() + netSellingPrice;
                    Log.i(TAG,"tot amount "+amount);
                    myProduct.setTotalAmount(amount);
                    qty = myProduct.getQuantity();
                    Log.i(TAG,"qty "+qty);

                    dbHelper.updateCartData(myProduct);
                    productAdapter.notifyItemChanged(position);
                    if(callingActivityName.equals("ShopProductListActivity"))
                        ((ShopProductListActivity)getContext()).updateCartCount();
                    else if(callingActivityName.equals("CartActivity"))
                        ((CartActivity)getContext()).setFooterValues();
                }
           // }
        }
    }

    private void getProductDetails(String prodId){
        if(productDetailsType==1)
            showProgress(true);
        Map<String,String> params=new HashMap<>();
        params.put("id", prodId); // as per user selected category from top horizontal categories list
        params.put("code", shopCode);
        params.put("dbName",shopCode);
        params.put("dbUserName",sharedPreferences.getString(Constants.DB_USER_NAME,""));
        params.put("dbPassword",sharedPreferences.getString(Constants.DB_PASSWORD,""));
        Log.d(TAG, params.toString());
        String url=getResources().getString(R.string.url)+"/products/ret_products_details";
        jsonObjectApiRequest(Request.Method.POST, url,new JSONObject(params),"productDetails");
    }

    private void checkFreeProductOffer(){
        if(type ==2 && myProductList.get(position).getProductDiscountOffer()!=null){
            ProductDiscountOffer productDiscountOffer = myProductList.get(position).getProductDiscountOffer();
            if(productDiscountOffer.getProdBuyId()!= productDiscountOffer.getProdFreeId()){
                productDetailsType = 2;
                getProductDetails(String.valueOf(productDiscountOffer.getProdFreeId()));
            }else onProductClicked(type, position);
        }else {
            onProductClicked(type,position);
        }
    }

    private float getOfferAmount(MyProduct item,int type){
        float amount = 0f;
        int qty = item.getQuantity();
        if(item.getProductPriceOffer() != null){
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

        }else if(item.getProductDiscountOffer() != null){

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
                        dbHelper.updateOfferCounterCartData(item.getOfferItemCounter(), Integer.parseInt(item.getId()));

                    }else{
                        item.setQuantity(item.getQuantity() - 1);
                    }
                }else{
                    item.setQuantity(item.getQuantity() - 1);
                    Log.i(TAG,"minus mode "+item.getQuantity() % productDiscountOffer.getProdBuyQty());
                    if(item.getQuantity() % productDiscountOffer.getProdBuyQty() == (productDiscountOffer.getProdBuyQty()-1)){
                        item.setOfferItemCounter(item.getOfferItemCounter() - 1);
                        if(item.getOfferItemCounter() == 0){
                            dbHelper.removeFreeProductFromCart(productDiscountOffer.getProdFreeId());
                        }else{
                            dbHelper.updateFreeCartData(productDiscountOffer.getProdFreeId(),item.getOfferItemCounter(),0f);
                            dbHelper.updateOfferCounterCartData(item.getOfferItemCounter(), Integer.parseInt(item.getId()));
                        }
                    }

                }
            }else if(type == 2){
                if(productDiscountOffer.getProdBuyId() == productDiscountOffer.getProdFreeId()){
                    Log.i(TAG,"Same product");
                    Log.i(TAG,"item qty "+item.getQuantity()+" offer buy qty"+productDiscountOffer.getProdBuyQty());
                    Log.i(TAG,"plus mode "+(item.getQuantity() - item.getOfferItemCounter())% productDiscountOffer.getProdBuyQty());
                    if((item.getQuantity() - item.getOfferItemCounter())% productDiscountOffer.getProdBuyQty() == 0){
                        item.setQuantity(item.getQuantity() + 1);
                        item.setOfferItemCounter(item.getOfferItemCounter() + 1);
                        dbHelper.updateOfferCounterCartData(item.getOfferItemCounter(),Integer.parseInt(item.getId()));
                    }else{

                    }
                }else{
                    Log.i(TAG,"Different product");
                    if(item.getQuantity() % productDiscountOffer.getProdBuyQty() == 0){
                        item.setOfferItemCounter(item.getOfferItemCounter() + 1);
                        MyProduct item1 = null;
                        if(item.getOfferItemCounter() == 1){
                            //            item1 = dbHelper.getProductDetails(productDiscountOffer.getProdFreeId());
                            item1 = freeProdut;
                            item1.setShopCode(shopCode);
                            item1.setSellingPrice(0f);
                            item1.setQuantity(1);
                            item1.setFreeProductPosition(item.getFreeProductPosition());
                            dbHelper.addProductToCart(item1);
                            Log.d("FreeProductPosition ", ""+item.getFreeProductPosition());
                            dbHelper.updateFreePositionCartData(item.getFreeProductPosition(),Integer.parseInt(item.getId()));
                            dbHelper.updateOfferCounterCartData(item.getOfferItemCounter(), Integer.parseInt(item.getId()));
                            Log.i(TAG,"Different product added to cart");
                        }else{

                            dbHelper.updateFreeCartData(productDiscountOffer.getProdFreeId(),item.getOfferItemCounter(),0f);
                            dbHelper.updateOfferCounterCartData(item.getOfferItemCounter(), Integer.parseInt(item.getId()));
                            Log.i(TAG,"Different product updated in cart");
                        }
                        //  myItemAdapter.notifyDataSetChanged();
                    }

                }

            }else{
                amount = item.getSellingPrice();
            }

        }else{
            if(type == 1)
                item.setQuantity(item.getQuantity() - 1);
            amount = item.getSellingPrice();
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
}

