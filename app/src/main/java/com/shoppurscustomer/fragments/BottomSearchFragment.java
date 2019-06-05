package com.shoppurscustomer.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.shoppurscustomer.R;
import com.shoppurscustomer.activities.BaseActivity;
import com.shoppurscustomer.activities.NetworkBaseActivity;
import com.shoppurscustomer.activities.ShopListActivity;
import com.shoppurscustomer.adapters.SearchProductAdapter;
import com.shoppurscustomer.adapters.SearchShopAdapter;
import com.shoppurscustomer.models.MyProduct;
import com.shoppurscustomer.models.MyShop;
import com.shoppurscustomer.utilities.AppController;
import com.shoppurscustomer.utilities.DialogAndToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by suraj kumar singh on 18-04-2019.
 */

public class BottomSearchFragment extends BottomSheetDialogFragment {
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

    public BottomSearchFragment() {

    }

    public void setCallingActivityName(String name){
     this.callingActivityName = name;
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
                if(callingActivityName.equals("ShopList"))
                    searchShops(s.toString());
                else if(callingActivityName.equals("ProductList")) {
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
        productAdapter = new SearchProductAdapter(getContext(),myProductList);
        productAdapter.setShopCode(shopCode);
        recyclerView_Search.setAdapter(productAdapter);

        Map<String,String> params=new HashMap<>();
        params.put("query",query);
        params.put("shopCode", shopCode);
        params.put("limit", "10");
        params.put("offset", ""+myProductList.size());
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
                if (response.getString("status").equals("true") || response.getString("status").equals(true)) {
                    JSONArray productJArray = response.getJSONArray("result");
                    for (int i = 0; i < productJArray.length(); i++) {
                        MyProduct myProduct = new MyProduct();
                        myProduct.setId(productJArray.getJSONObject(i).getString("prodId"));
                        myProduct.setName(productJArray.getJSONObject(i).getString("prodName"));
                        myProduct.setQuantity(productJArray.getJSONObject(i).getInt("prodQoh"));
                        myProduct.setMrp(productJArray.getJSONObject(i).getString("prodMrp"));
                        myProduct.setSellingPrice(productJArray.getJSONObject(i).getString("prodSp"));
                        myProduct.setCode(productJArray.getJSONObject(i).getString("prodCode"));
                        myProduct.setBarCode(productJArray.getJSONObject(i).getString("prodBarCode"));
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
                        myProduct.setProdCgst(productJArray.getJSONObject(i).getString("prodCgst"));
                        myProduct.setProdIgst(productJArray.getJSONObject(i).getString("prodIgst"));
                        myProduct.setProdSgst(productJArray.getJSONObject(i).getString("prodSgst"));
                        myProduct.setProdWarranty(productJArray.getJSONObject(i).getString("prodWarranty"));
                        myProduct.setSubCatName(subCatName);
                        myProductList.add(myProduct);
                    }
                    if (myProductList.size() > 0) {
                        productAdapter.notifyDataSetChanged();
                    } else {
                       myProductList.clear();
                       productAdapter.notifyDataSetChanged();
                    }
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

}

