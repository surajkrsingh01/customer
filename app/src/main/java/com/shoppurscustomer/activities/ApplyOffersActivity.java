package com.shoppurscustomer.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.shoppurscustomer.R;
import com.shoppurscustomer.adapters.ApplyOfferAdapter;
import com.shoppurscustomer.adapters.OfferDescAdapter;
import com.shoppurscustomer.interfaces.MyItemTypeClickListener;
import com.shoppurscustomer.models.MyProduct;
import com.shoppurscustomer.models.ProductComboDetails;
import com.shoppurscustomer.models.ProductComboOffer;
import com.shoppurscustomer.models.ProductDiscountOffer;
import com.shoppurscustomer.models.ProductPriceOffer;
import com.shoppurscustomer.utilities.DialogAndToast;
import com.shoppurscustomer.utilities.Utility;

import java.util.ArrayList;
import java.util.List;

public class ApplyOffersActivity extends BaseActivity implements MyItemTypeClickListener {

    private List<Object> itemList;
    private RecyclerView recyclerView;
    private ApplyOfferAdapter myItemAdapter;
    private TextView textViewError;

    private RelativeLayout rlOfferDesc;
    private String flag;
    private int position;
    private int counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_offers);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();
    }

    private void init(){
        flag = getIntent().getStringExtra("flag");
        rlOfferDesc = findViewById(R.id.rl_offer_desc);
        itemList = new ArrayList<>();
        List<ProductComboOffer> comboOffer = dbHelper.getProductComboOffer();
        Log.i(TAG,"combo offer  "+comboOffer.size());
        List<ProductPriceOffer> priceOffer = dbHelper.getProductPriceOffer();
        List<Object> freeOffer = dbHelper.getProductFreeOffer();
        MyProduct myProductItem = null;
        ProductDiscountOffer productDiscountOffer = null;

        for(ProductPriceOffer productPriceOffer : priceOffer){
       //     myProductItem = dbHelper.getProductDetails(productPriceOffer.getProdId());
            myProductItem.setProductOffer(productPriceOffer);
            myProductItem.setOfferId(""+productPriceOffer.getId());
            myProductItem.setOfferType("price");
            itemList.add(myProductItem);
        }

        for(Object ob : freeOffer){
            productDiscountOffer = (ProductDiscountOffer)ob;
       //     myProductItem = dbHelper.getProductDetails(productDiscountOffer.getProdBuyId());
            myProductItem.setProductOffer(productDiscountOffer);
            myProductItem.setOfferId(""+productDiscountOffer.getId());
            myProductItem.setOfferType("free");
            itemList.add(myProductItem);
        }

        String comboName = "";
        String comboIds = "";
        float totAmt = 0f;
        float totMrp = 0f;
        float totSgst =0f;
        float totCgst = 0f;
        MyProduct comboProductItem = null;
        int comboId = 1;
        for(ProductComboOffer productComboOffer : comboOffer){
            myProductItem = new MyProduct();
            for(ProductComboDetails productComboDetails : productComboOffer.getProductComboOfferDetails()){
      //          comboProductItem = dbHelper.getProductDetails(productComboDetails.getPcodProdId());
                totAmt = totAmt + (productComboDetails.getPcodPrice() * productComboDetails.getPcodProdQty());
                totMrp = totMrp + (comboProductItem.getMrp() * productComboDetails.getPcodProdQty());
                totCgst = totCgst + (productComboDetails.getPcodPrice() *
                        comboProductItem.getProdCgst() * productComboDetails.getPcodProdQty()/100);
                totSgst = totSgst + (productComboDetails.getPcodPrice() *
                        comboProductItem.getProdSgst() * productComboDetails.getPcodProdQty()/100);
                if(TextUtils.isEmpty(comboName)){
                    comboName = comboProductItem.getName();
                    comboIds = ""+comboProductItem.getId();
                }else{
                    comboName = comboName +"+"+comboProductItem.getName();
                    comboIds = comboIds+"-"+comboProductItem.getId();
                }

            }
            Log.i(TAG,"cgst "+totCgst);
            Log.i(TAG,"sgst "+totSgst);
            Log.i(TAG,"totAmt "+totAmt);
            Log.i(TAG,"totMrp "+totMrp);
            myProductItem.setId(String.valueOf(comboId));
            myProductItem.setName(comboName);
            myProductItem.setComboProductIds(comboIds);
            myProductItem.setSellingPrice(totAmt);
           // myProductItem.setTotalAmount(totAmt);
            myProductItem.setMrp(totMrp);
            myProductItem.setProdSgst(totSgst);
            myProductItem.setProdCgst(totCgst);
            myProductItem.setIsBarcodeAvailable("N");
            itemList.add(myProductItem);
            comboId++;
        }


        List<MyProduct> cartList =  dbHelper.getCartProducts();
        MyProduct myProductItem1 = null;
        for(MyProduct cartProduct : cartList){
            if(cartProduct.getSellingPrice() != 0f){
                counter++;
                //setOffer(ob);
                for(Object ob1 : itemList){
                    myProductItem1 = (MyProduct)ob1;
                    if(cartProduct.getId() == myProductItem1.getId()){
                        myProductItem1.setQuantity(cartProduct.getQuantity());
                        myProductItem1.setTotalAmount(cartProduct.getTotalAmount());
                        myProductItem1.setFreeProductPosition(cartProduct.getFreeProductPosition());
                        myProductItem1.setOfferItemCounter(cartProduct.getOfferItemCounter());
                    }
                }
            }
        }

        textViewError = findViewById(R.id.text_error);
        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        myItemAdapter=new ApplyOfferAdapter(this,itemList);
        myItemAdapter.setMyItemTypeClickListener(this);
        myItemAdapter.setColorTheme(colorTheme);
        recyclerView.setAdapter(myItemAdapter);

    }

    @Override
    public void onItemClicked(int position,int type) {
        this.position = position;
        MyProduct item = (MyProduct) itemList.get(position);
        String offerName = "";
        if(type == 3){
            List<String> offerDescList = new ArrayList<>();
            Object ob = item.getProductOffer();
            if(ob instanceof ProductDiscountOffer){

                ProductDiscountOffer productDiscountOffer = (ProductDiscountOffer)ob;
                offerName = productDiscountOffer.getOfferName();

                offerDescList.add("Buy "+productDiscountOffer.getProdBuyQty()+" Get "+productDiscountOffer.getProdFreeQty());
                offerDescList.add("Offer valid till "+ Utility.parseDate(productDiscountOffer.getEndDate(),"yyyy-MM-dd",
                        "EEE dd MMMM, yyyy")+" 23:59 PM");

            }else if(ob instanceof ProductComboOffer){
                ProductComboOffer productComboOffer = (ProductComboOffer)ob;
                offerName = productComboOffer.getOfferName();
                float totOfferAmt = 0f;
                for(ProductComboDetails productComboDetails : productComboOffer.getProductComboOfferDetails()){
                    totOfferAmt = totOfferAmt + productComboDetails.getPcodPrice();
                    offerDescList.add("Buy "+productComboDetails.getPcodProdQty()+" at Rs "+
                            Utility.numberFormat(totOfferAmt));
                }

                offerDescList.add("Offer valid till "+Utility.parseDate(productComboOffer.getEndDate(),"yyyy-MM-dd",
                        "EEE dd MMMM, yyyy")+" 23:59 PM");
            }

            rlOfferDesc.setVisibility(View.VISIBLE);
            ImageView iv_clear = findViewById(R.id.iv_clear);
            //Utility.setColorFilter(iv_clear.getDrawable(),colorTheme);
            TextView tvOfferName = findViewById(R.id.text_offer_name);
            findViewById(R.id.relative_footer_action).setBackgroundColor(colorTheme);
            TextView tv = findViewById(R.id.text_action);
            tv.setText("OKAY! GOT IT");

            tvOfferName.setText(offerName);

            iv_clear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rlOfferDesc.setVisibility(View.GONE);
                }
            });
            findViewById(R.id.relative_footer_action).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rlOfferDesc.setVisibility(View.GONE);
                }
            });


            RecyclerView recyclerViewOfferDesc=findViewById(R.id.recycler_view_offer_desc);
            recyclerViewOfferDesc.setHasFixedSize(true);
            final RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this);
            recyclerViewOfferDesc.setLayoutManager(layoutManager);
            recyclerViewOfferDesc.setItemAnimator(new DefaultItemAnimator());
            OfferDescAdapter offerDescAdapter =new OfferDescAdapter(this,offerDescList);
            recyclerViewOfferDesc.setAdapter(offerDescAdapter);
            recyclerViewOfferDesc.setNestedScrollingEnabled(false);
        }else if(type == 1){
            if(item.getQuantity() > 0){
                if(item.getQuantity() == 1){
                    counter--;
                    dbHelper.removeProductFromCart(item.getBarCode());
                    dbHelper.removeProductFromCart(item.getId());
                    if(item.getProductOffer() instanceof ProductDiscountOffer){
                        ProductDiscountOffer productDiscountOffer = (ProductDiscountOffer)item.getProductOffer();

                        if(productDiscountOffer.getProdBuyId() != productDiscountOffer.getProdFreeId())
                        dbHelper.removeFreeProductFromCart(productDiscountOffer.getProdFreeId());
                    }
                    item.setQuantity(0);
                    myItemAdapter.notifyItemChanged(position);
                }else{
                    int qty = item.getQuantity() - 1;
                    float netSellingPrice = getOfferAmount(item,type);
                     item.setQuantity(qty);
                    qty = item.getQuantity();
                    Log.i(TAG,"netSellingPrice "+netSellingPrice);
                    float amount = item.getTotalAmount() - netSellingPrice;
                    Log.i(TAG,"tot amount "+amount);
                    item.setTotalAmount(amount);
                    dbHelper.updateCartData(item);
                    myItemAdapter.notifyItemChanged(position);
                }
            }

        }else if(type == 2){
            if(item.getIsBarcodeAvailable().equals("Y")){
                if(item.getQuantity() == item.getBarcodeList().size()){
                    DialogAndToast.showDialog("There are no more stocks",this);
                }else{
                    int qty = item.getQuantity() + 1;
                    if(qty == 1){
                        counter++;
                        item.setFreeProductPosition(counter);
                        dbHelper.addProductToCart(item);
                    }
                    item.setQuantity(qty);
                    Log.i(TAG,"qty "+qty);
                    float netSellingPrice = getOfferAmount(item,type);
                    qty = item.getQuantity();
                    Log.i(TAG,"netSellingPrice "+netSellingPrice);
                    float amount = item.getTotalAmount() + netSellingPrice;
                    Log.i(TAG,"tot amount "+amount);
                    item.setTotalAmount(amount);
                    dbHelper.updateCartData(item);
                    myItemAdapter.notifyItemChanged(position);
                }

            }else{
                if((item.getComboProductIds() == null || item.getComboProductIds().equals("null"))
                        && item.getQuantity() == item.getQoh()){
                    DialogAndToast.showDialog("There are no more stocks",this);
                }else{
                    int qty = item.getQuantity() + 1;
                    item.setQuantity(qty);
                    if(qty == 1){
                        counter++;
                        item.setFreeProductPosition(counter);
                        dbHelper.addProductToCart(item);
                    }
                    float netSellingPrice = getOfferAmount(item,type);
                    Log.i(TAG,"netSellingPrice "+netSellingPrice);
                    float amount = item.getTotalAmount() + netSellingPrice;
                    Log.i(TAG,"tot amount "+amount);
                    item.setTotalAmount(amount);
                    qty = item.getQuantity();
                    Log.i(TAG,"qty "+qty);

                    dbHelper.updateCartData(item);
                    myItemAdapter.notifyItemChanged(position);
                }
            }
        }
    }


    private float getOfferAmount(MyProduct item,int type){
        float amount = 0f;
        int qty = item.getQuantity();
        if(item.getProductOffer() != null && item.getProductOffer() instanceof ProductComboOffer){
            ProductComboOffer productComboOffer = (ProductComboOffer)item.getProductOffer();
            if(qty > 1){
                int maxSize = productComboOffer.getProductComboOfferDetails().size();
                int mod = qty % maxSize;
                Log.i(TAG,"mod "+mod);
                if(mod == 0){
                    mod = maxSize;
                }
                amount = getOfferPrice(mod,item.getSellingPrice(),productComboOffer.getProductComboOfferDetails());
            }else{
                amount = item.getSellingPrice();
            }

               if(type == 1)
                item.setQuantity(item.getQuantity() - 1);

        }else if(item.getProductOffer() != null && item.getProductOffer() instanceof ProductDiscountOffer){

            ProductDiscountOffer productDiscountOffer = (ProductDiscountOffer)item.getProductOffer();
            amount = item.getSellingPrice();
            if(type == 1){
                if(productDiscountOffer.getProdBuyId() == productDiscountOffer.getProdFreeId()){
                    Log.i(TAG,"item qty "+item.getQuantity()+" offer buy qty"+productDiscountOffer.getProdBuyQty());
                    Log.i(TAG,"minus mode "+(item.getQuantity() - item.getOfferItemCounter()-1)% productDiscountOffer.getProdBuyQty());
                    if((item.getQuantity() - item.getOfferItemCounter() -1)% productDiscountOffer.getProdBuyQty() ==
                            (productDiscountOffer.getProdBuyQty()-1)){
                        item.setQuantity(item.getQuantity() - 2);
                        item.setOfferItemCounter(item.getOfferItemCounter() - 1);
                        dbHelper.updateOfferCounterCartData(item.getOfferItemCounter(),Integer.parseInt(item.getId()));

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
                            dbHelper.updateOfferCounterCartData(item.getOfferItemCounter(),Integer.parseInt(item.getId()));
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
                     //       item1 = dbHelper.getProductDetails(productDiscountOffer.getProdFreeId());
                            item1.setSellingPrice(0f);
                            item1.setQuantity(1);
                            item1.setFreeProductPosition(item.getFreeProductPosition());
                            dbHelper.addProductToCart(item1);
                            dbHelper.updateFreePositionCartData(item.getFreeProductPosition(),Integer.parseInt(item.getId()));
                            dbHelper.updateOfferCounterCartData(item.getOfferItemCounter(), Integer.parseInt(item.getId()));
                            Log.i(TAG,"Different product added to cart");
                        }else{
                          //  item1 = itemList.get(item.getFreeProductPosition());
                         //   item1.setQty(item.getOfferCounter());
                            dbHelper.updateFreeCartData(productDiscountOffer.getProdFreeId(),item.getOfferItemCounter(),0f);
                            dbHelper.updateOfferCounterCartData(item.getOfferItemCounter(),Integer.parseInt(item.getId()));
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

    private float getOfferPrice(int qty,float sp,List<ProductComboDetails> productComboDetailsList){
        float amount = 0f;
        for(ProductComboDetails productComboDetails:productComboDetailsList){
            if(productComboDetails.getPcodProdQty() == qty){
                amount = productComboDetails.getPcodPrice();
                Log.i(TAG,"offer price "+amount);
                break;
            }else{
                amount = sp;
            }
        }
        Log.i(TAG,"final selling price "+amount);
        return amount;
    }

    private void setOffer(MyProduct item){
        List<ProductPriceOffer> productPriceOfferList = dbHelper.getProductPriceOffer(""+item.getId());
        List<ProductDiscountOffer> productDiscountOfferList = dbHelper.getProductFreeOffer(""+item.getId());
        Log.i(TAG,"comboOfferList size "+productPriceOfferList.size());
        Log.i(TAG,"productDiscountOfferList size "+productDiscountOfferList.size());

        if(productPriceOfferList.size() > 0){
            item.setProductOffer(productPriceOfferList.get(0));
        }else if(productDiscountOfferList.size() > 0){
            item.setProductOffer(productDiscountOfferList.get(0));
        }
    }

}
