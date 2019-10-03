package com.shoppurs.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.shoppurs.R;
import com.shoppurs.models.MyProduct;
import com.shoppurs.models.ProductDiscountOffer;
import com.shoppurs.models.ProductFrequency;
import com.shoppurs.models.ProductPriceDetails;
import com.shoppurs.models.ProductPriceOffer;
import com.shoppurs.models.ShopDeliveryModel;
import com.shoppurs.utilities.DialogAndToast;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HandleCartActivity extends NetworkBaseActivity{

    private int position, type, counter, productDetailsType;
    private MyProduct myProduct, freeProdut;
    private Context context;
    private String shopCode;
    private ShopDeliveryModel shopDeliveryModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void updateUi(MyProduct myProduct, int position){

    }

    public void updateCart(int type, int position, String shopCode, MyProduct product, ShopDeliveryModel deliveryModel){
        Log.d("clicked Position ", position+"");
        this.position = position;
        this.type = type;
        this.shopCode = shopCode;
        myProduct = product;
        shopDeliveryModel = deliveryModel;
        myProduct.setShopCode(shopCode);
        if(type==2){
            productDetailsType = 1;
            getProductDetails(myProduct.getId());
        }else onProductClicked(type, position);
    }

    private void onProductClicked(int type, int position){
        if(type == 1){
            if(myProduct.getQuantity() > 0){
                if(myProduct.getQuantity() == 1){
                    counter--;
                    //dbHelper.removeProductFromCart(myProduct.getProdBarCode());
                    dbHelper.removeProductFromCart(myProduct.getId(), myProduct.getShopCode());
                    dbHelper.removePriceProductFromCart(myProduct.getId(), myProduct.getShopCode());
                    if(myProduct.getProductPriceOffer()!=null){
                        ProductPriceOffer productPriceOffer = myProduct.getProductPriceOffer();
                        for(ProductPriceDetails productPriceDetails : productPriceOffer.getProductPriceDetails()){
                            dbHelper.removePriceProductDetailsFromCart(String.valueOf(productPriceDetails.getId()), myProduct.getShopCode());
                        }
                    }
                    if(myProduct.getProductDiscountOffer()!=null){
                        ProductDiscountOffer productDiscountOffer = (ProductDiscountOffer)myProduct.getProductOffer();
                        if(productDiscountOffer.getProdBuyId() != productDiscountOffer.getProdFreeId())
                            dbHelper.removeFreeProductFromCart(productDiscountOffer.getProdFreeId(), myProduct.getShopCode());
                    }
                    myProduct.setQuantity(0);
                    myProduct.setTotalAmount(0);

                    //shopProductAdapter.notifyItemChanged(position);
                    updateUi(myProduct, position);

                    if(dbHelper.getCartCount(myProduct.getShopCode())<1)
                        dbHelper.removeCouponFromCart(myProduct.getShopCode());
                    //callingActivity.updateCartCount();
                 //   updateCartCount();
                    Log.d("onRemove Qyantity ", myProduct.getQuantity()+"");
                }else{
                    int qty = myProduct.getQuantity() - 1;
                    float netSellingPrice = getOfferAmount(myProduct,type);
                    myProduct.setQuantity(myProduct.getQuantity());
                    qty = myProduct.getQuantity();
                    Log.i(TAG,"netSellingPrice "+netSellingPrice);
                    float amount = 0;
                    amount = myProduct.getTotalAmount() - netSellingPrice;
                    Log.i(TAG,"tot amount "+amount);
                    myProduct.setTotalAmount(amount);
                    if(myProduct.getProductPriceOffer()!=null){
                        myProduct.setSellingPrice(amount/qty);
                    }
                    dbHelper.updateCartData(myProduct);

                    updateUi(myProduct, position);
                    //shopProductAdapter.notifyItemChanged(position);
                }
            }

        }else if(type == 2){
            Log.d("type ", ""+type);
            Log.d("myProduct ", ""+myProduct.getQuantity());
            Log.d("position ", ""+position);

            if(myProduct.getQuantity() >= myProduct.getQoh()){
                //shopProductAdapter.notifyDataSetChanged();
                updateUi(myProduct, position);
                DialogAndToast.showDialog("There are no more stocks",this);
            }else{
                int qty = myProduct.getQuantity() + 1;
                myProduct.setQuantity(qty);
                if(qty == 1){
                    counter++;
                    myProduct.setFreeProductPosition(counter);
                    dbHelper.addProductToCart(myProduct);
                    dbHelper.addShopDeliveryDetails(shopDeliveryModel);
                }
                float netSellingPrice = getOfferAmount(myProduct,type);
                float amount = 0;
                Log.i(TAG,"netSellingPrice "+netSellingPrice);
                amount = myProduct.getTotalAmount() + netSellingPrice;
                Log.i(TAG,"tot amount "+amount);
                myProduct.setTotalAmount(amount);
                if(myProduct.getProductPriceOffer()!=null){
                    myProduct.setSellingPrice(amount/qty);
                }
                qty = myProduct.getQuantity();
                Log.i(TAG,"qty "+qty);
                myProduct.setQuantity(myProduct.getQuantity());
                dbHelper.updateCartData(myProduct);

                if(myProduct.getFrequency()!=null){
                    int frequencyQty = Integer.parseInt(myProduct.getFrequency().getQyantity());
                    if(frequencyQty>=myProduct.getQoh()){
                        DialogAndToast.showDialog("We have only "+myProduct.getQoh() +"product available",this);
                    }else updateFrequencyProducttoCart(type, position);
                }else
                updateUi(myProduct, position);
            }
        }
    }

    public void updateFrequencyProducttoCart(int type, int position){
        Log.d("clicked Position ", position+"");
        ProductFrequency productFrequency = myProduct.getFrequency();
        myProduct.setQuantity(Integer.parseInt(productFrequency.getQyantity()));
        myProduct.setFreeProductPosition(dbHelper.getFreeProductPosition(myProduct.getId(), shopCode));
        myProduct.setOfferItemCounter(dbHelper.getOfferCounter(myProduct.getId(), shopCode));
      //  myProduct.setTotalAmount(dbHelper.getTotalAmount(myProduct.getId(), shopCode));
        myProduct.setSellingPrice(dbHelper.getProductSellingPrice(myProduct.getId(), shopCode));
        int qty = myProduct.getQuantity();
        if (qty == 1) {
            counter++;
            myProduct.setFreeProductPosition(counter);
            dbHelper.addProductToCart(myProduct);
            dbHelper.addShopDeliveryDetails(shopDeliveryModel);
            //myProduct
        }
        qty = myProduct.getQuantity();
        float netSellingPrice = getOfferAmount(myProduct, type);
        float amount = 0;
        Log.d(TAG, "netSellingPrice " + netSellingPrice);
        amount = netSellingPrice + (netSellingPrice * (myProduct.getQuantity()-1));
        Log.d(TAG, "tot amount " + amount);
        myProduct.setTotalAmount(amount);
        if (myProduct.getProductPriceOffer() != null) {
            myProduct.setSellingPrice(amount / qty);
        }
        qty = myProduct.getQuantity();
        Log.i(TAG, "qty " + qty);
        myProduct.setQuantity(myProduct.getQuantity());
        dbHelper.updateCartData(myProduct);

        startActivity(new Intent(this, CartActivity.class));
    }

    private void getProductDetails(String prodId){
        if(productDetailsType==1)
            showProgress(true);
        Map<String,String> params=new HashMap<>();
        params.put("id", prodId); // as per user selected category from top horizontal categories list
        params.put("code", shopCode);
        params.put("dbName",shopCode);
        Log.d(TAG, params.toString());
        String url=getResources().getString(R.string.url)+"/products/ret_products_details";
        jsonObjectApiRequest(Request.Method.POST, url,new JSONObject(params),"HandleCartproductDetails");
    }

    private void checkFreeProductOffer(){
        if(type ==2 && myProduct.getProductDiscountOffer()!=null){
            ProductDiscountOffer productDiscountOffer = myProduct.getProductDiscountOffer();
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
            //if(qty > 1){
            int maxSize = productPriceOffer.getProductPriceDetails().size();
            int mod = qty % maxSize;
            Log.i(TAG,"mod "+mod);
            if(mod == 0){
                mod = maxSize;
            }
            amount = getOfferPrice(mod,item.getSellingPrice(),productPriceOffer.getProductPriceDetails());
            // }else{
            //     amount = item.getSellingPrice();
            //}

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
                        dbHelper.updateOfferCounterCartData(item.getOfferItemCounter(),Integer.parseInt(item.getId()), item.getShopCode());

                    }else{
                        item.setQuantity(item.getQuantity() - 1);
                    }
                }else{
                    item.setQuantity(item.getQuantity() - 1);
                    Log.i(TAG,"minus mode "+item.getQuantity() % productDiscountOffer.getProdBuyQty());
                    if(item.getQuantity() % productDiscountOffer.getProdBuyQty() == (productDiscountOffer.getProdBuyQty()-1)){
                        item.setOfferItemCounter(item.getOfferItemCounter() - 1);
                        if(item.getOfferItemCounter() == 0){
                            dbHelper.removeFreeProductFromCart(productDiscountOffer.getProdFreeId(), item.getShopCode());
                        }else{
                            dbHelper.updateFreeCartData(productDiscountOffer.getProdFreeId(),item.getOfferItemCounter(),0f, item.getShopCode());
                            dbHelper.updateOfferCounterCartData(item.getOfferItemCounter(),Integer.parseInt(item.getId()), item.getShopCode());
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
                            //            item1 = dbHelper.getProductDetails(productDiscountOffer.getProdFreeId());
                            item1 = freeProdut;
                            item1.setShopCode(shopCode);
                            item1.setSellingPrice(0f);
                            item1.setQuantity(1);
                            item1.setFreeProductPosition(item.getFreeProductPosition());
                            dbHelper.addProductToCart(item1);
                            Log.d("FreeProductPosition ", ""+item.getFreeProductPosition());
                            dbHelper.updateFreePositionCartData(item.getFreeProductPosition(),Integer.parseInt(item.getId()), item.getShopCode());
                            dbHelper.updateOfferCounterCartData(item.getOfferItemCounter(),Integer.parseInt(item.getId()), item.getShopCode());
                            Log.i(TAG,"Different product added to cart");
                        }else{

                            dbHelper.updateFreeCartData(productDiscountOffer.getProdFreeId(),item.getOfferItemCounter(),0f, item.getShopCode());
                            dbHelper.updateOfferCounterCartData(item.getOfferItemCounter(),Integer.parseInt(item.getId()), item.getShopCode());
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
        int i=-1;
        for(ProductPriceDetails productPriceDetails:productPriceDetailsList){
            if(productPriceDetails.getPcodProdQty() == qty){
                amount = productPriceDetails.getPcodPrice();
                if(qty!=1){
                    amount = amount - productPriceDetailsList.get(i).getPcodPrice();
                }
                Log.i(TAG,"offer price "+amount);
                break;
            }else{
                amount = sp;
            }
            i++;
        }
        Log.i(TAG,"final selling price "+amount);
        return amount;
    }



    @Override
    public void onJsonObjectCartResponse(JSONObject response, String apiName) {
        showProgressBar(false);
        try {
            // JSONObject jsonObject=response.getJSONObject("response");
            Log.d("response", response.toString());
            Log.d("type ", productDetailsType+"");
               if(apiName.equals("HandleCartproductDetails")){
                if(response.getString("status").equals("true")||response.getString("status").equals(true)){
                    JSONObject jsonObject = response.getJSONObject("result");
                    if(productDetailsType == 1){
                        Log.d("type ", productDetailsType+"");
                        Log.d("myProduct ", myProduct.getName());
                        myProduct.setQoh(jsonObject.getInt("prodQoh"));
                        Log.d("Qoh ", myProduct.getQoh()+"");
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
                    DialogAndToast.showToast("Something went wrong, Please try again", HandleCartActivity.this);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            DialogAndToast.showToast(getResources().getString(R.string.json_parser_error)+e.toString(),HandleCartActivity.this);
        }
    }

    private void showProgressBar(boolean show){
        if(context instanceof ShopProductListActivity){
            ((ShopProductListActivity)context).showProgress(show);
        }
    }

    private void updateCartCount(){
        if(context instanceof ShopProductListActivity){
            ((ShopProductListActivity)context).updateCartCount();
        }
    }

}
