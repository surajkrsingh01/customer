package com.shoppurs.adapters;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.shoppurs.R;
import com.shoppurs.activities.CartActivity;
import com.shoppurs.activities.ProductDetailActivity;
import com.shoppurs.activities.ShopProductListActivity;
import com.shoppurs.activities.ShoppursProductActivity;
import com.shoppurs.database.DbHelper;
import com.shoppurs.interfaces.MyItemTypeClickListener;
import com.shoppurs.models.MyProduct;
import com.shoppurs.models.ProductDiscountOffer;
import com.shoppurs.models.ProductFrequency;
import com.shoppurs.models.ProductPriceOffer;
import com.shoppurs.models.ProductUnit;
import com.shoppurs.utilities.DialogAndToast;
import com.shoppurs.utilities.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suraj kumar singh on 18-04-2019.
 */

public class SearchProductAdapter extends RecyclerView.Adapter<SearchProductAdapter.MyViewHolder> {
    private List<MyProduct> myProductsList = new ArrayList<>();
    private Context context;
    private DbHelper dbHelper;
    private String shopCode;
    private boolean isDarkTheme;
    private String flag;
    private MyItemTypeClickListener myItemClickListener;
    private Typeface typeface;

    public void setMyItemClickListener(MyItemTypeClickListener myItemClickListener) {
        this.myItemClickListener = myItemClickListener;
    }

    public SearchProductAdapter(Context context, String shopCode, List<MyProduct> myProducts, String flag, Typeface typeface, boolean isDarkTheme ) {
        super();
        this.context = context;
        this.shopCode = shopCode;
        this.myProductsList = myProducts;
        this.typeface = typeface;
        this.flag = flag;
        dbHelper = new DbHelper(context);
    }

    @Override
    public SearchProductAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.product_list_item_layout, parent, false);
        return new SearchProductAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SearchProductAdapter.MyViewHolder myViewHolder, final int position) {
        final MyProduct item = (MyProduct) myProductsList.get(position);
        ProductFrequency frequency = item.getFrequency();
        if(frequency!=null  && frequency.getStatus().equals("1")){
            myViewHolder.text_frequency.setBackground(context.getResources().getDrawable(R.drawable.orange_solid_blue_stroke_circle_background));
            myViewHolder.text_frequency.setTextColor(context.getResources().getColor(R.color.white));
        }else {
            myViewHolder.text_frequency.setBackground(context.getResources().getDrawable(R.drawable.white_solid_blue_stroke_circle_background));
            myViewHolder.text_frequency.setTextColor(context.getResources().getColor(R.color.orange500));
        }

        if (dbHelper.checkProdExistInCart(item.getId(), shopCode)) {
            myViewHolder.btnAddCart.setVisibility(View.GONE);
            myViewHolder.linear_plus_minus.setVisibility(View.VISIBLE);
            myViewHolder.tv_cartCount.setText(String.valueOf(dbHelper.getProductQuantity(item.getId(), shopCode, "normal")));
            item.setFreeProductPosition(dbHelper.getFreeProductPosition(item.getId(), shopCode));
            item.setOfferItemCounter(dbHelper.getOfferCounter(item.getId(), shopCode));
            item.setQuantity(Integer.parseInt(myViewHolder.tv_cartCount.getText().toString()));
            item.setTotalAmount(dbHelper.getTotalAmount(item.getId(), shopCode, "normal"));
            item.setSellingPrice(dbHelper.getProductSellingPrice(item.getId(), shopCode));

        } else {
            item.setTotalAmount(0);
            item.setQuantity(0);
            myViewHolder.tv_cartCount.setText(String.valueOf(0));
            myViewHolder.linear_plus_minus.setVisibility(View.GONE);
            myViewHolder.btnAddCart.setVisibility(View.VISIBLE);
        }

        if(flag.equals("ToDo List")){
            myViewHolder.btnAddCart.setVisibility(View.GONE);
            myViewHolder.linear_plus_minus.setVisibility(View.GONE);
            myViewHolder.text_frequency.setVisibility(View.GONE);
            if(dbHelper.checkProdExistInToDoList(item.getId(), shopCode))
                myViewHolder.tv_todo_status.setVisibility(View.VISIBLE);
        }

        myViewHolder.btnAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // myViewHolder.linear_plus_minus.setVisibility(View.VISIBLE);
                //int count = Integer.parseInt(myViewHolder.tv_cartCount.getText().toString());
                if(flag.equals("ShopProductListActivity") || flag.equals("ShoppursProductActivity") || flag.equals("CartActivity"))
                    myItemClickListener.onItemClicked(position, 2);
                //((ShopProductListActivity) context).updateCart( 2, position);
            }
        });
        myViewHolder.image_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = Integer.parseInt(myViewHolder.tv_cartCount.getText().toString());
                if(flag.equals("ShopProductListActivity")  || flag.equals("ShoppursProductActivity") ||  flag.equals("CartActivity"))
                    myItemClickListener.onItemClicked(position, 1);
                //((ShopProductListActivity) context).updateCart(1, position);
            }
        });
        myViewHolder.image_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = Integer.parseInt(myViewHolder.tv_cartCount.getText().toString());
                if(flag.equals("ShopProductListActivity") || flag.equals("ShoppursProductActivity") || flag.equals("CartActivity"))
                    myItemClickListener.onItemClicked(position, 2);
                //((ShopProductListActivity) context).updateCart(2, position);
            }
        });

        myViewHolder.textbarcode.setText(item.getBarCode());
        myViewHolder.textName.setText(item.getName());
        //myViewHolder.textAmount.setText("Rs. "+String.format("%.02f",item.getMrp()));
        myViewHolder.textSp.setText(Utility.numberFormat(Double.valueOf(item.getSellingPrice())));
        myViewHolder.textMrp.setText(Utility.numberFormat(Double.valueOf(item.getMrp())));
        myViewHolder.textMrp.setPaintFlags(myViewHolder.textMrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        float diff = Float.valueOf(item.getMrp()) - Float.valueOf(item.getSellingPrice());
        if (diff > 0f) {
            float percentage = diff * 100 / Float.valueOf(item.getMrp());
            myViewHolder.textOffPer.setText(String.format("%.02f", percentage) + "% off");
            myViewHolder.textMrp.setVisibility(View.VISIBLE);
            myViewHolder.textOffPer.setVisibility(View.VISIBLE);
        } else {
            myViewHolder.textMrp.setVisibility(View.GONE);
            myViewHolder.textOffPer.setVisibility(View.GONE);
        }

          /*  if(item.getIsBarCodeAvailable().equals("Y")){
                if(item.getBarcodeList() != null && item.getBarcodeList().size() > 0){
                    myViewHolder.textStatus.setVisibility(View.GONE);
                }else{
                    myViewHolder.textStatus.setText("Out of stock");
                    myViewHolder.textStatus.setVisibility(View.GONE);

                }
            }else{
                if(item.getProdQoh() == 0){
                    myViewHolder.textStatus.setText("Out of stock");
                    myViewHolder.textStatus.setVisibility(View.GONE);
                }else{
                    myViewHolder.textStatus.setVisibility(View.GONE);
                }
            }*/


        if(item.getProductPriceOffer() != null ) {
            ProductPriceOffer productPriceOffer = item.getProductPriceOffer();
            myViewHolder.text_offer.setText(productPriceOffer.getOfferName());
        }else if(item.getProductDiscountOffer() != null ){
            ProductDiscountOffer productDiscountOffer = item.getProductDiscountOffer();
            myViewHolder.text_offer.setText(productDiscountOffer.getOfferName());
        }
        if(item.getProductPriceOffer() == null  && item.getProductDiscountOffer() == null ){
            myViewHolder.text_offer.setVisibility(View.GONE);
        }


        if(item.getProductUnitList() != null && item.getProductUnitList().size() > 0){
            myViewHolder.relative_unit.setVisibility(View.VISIBLE);
            List<String> unitList = new ArrayList<>();
            for(ProductUnit unit : item.getProductUnitList()){
                unitList.add(unit.getUnitValue()+" "+unit.getUnitName());
            }
            ArrayAdapter<String> unitAdapter = new ArrayAdapter<String>(context, R.layout.simple_dropdown_unit_item, unitList){
                @Override
                public boolean isEnabled(int position){
                    return true;
                }
                @Override
                public View getView(int position, View convertView,
                                    ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView tv = (TextView) view;
                    if(isDarkTheme){
                        tv.setTextColor(context.getResources().getColor(R.color.white));
                    }else{
                        tv.setTextColor(context.getResources().getColor(R.color.primary_text_color));
                    }
                    return view;
                }
                @Override
                public View getDropDownView(int position, View convertView,
                                            ViewGroup parent) {
                    View view = super.getDropDownView(position, convertView, parent);
                    if(isDarkTheme){
                        view.setBackgroundColor(context.getResources().getColor(R.color.dark_color));
                    }else{
                        view.setBackgroundColor(context.getResources().getColor(R.color.white));
                    }
                    TextView tv = (TextView) view;
                    if(isDarkTheme){
                        tv.setTextColor(context.getResources().getColor(R.color.white));
                    }else{
                        tv.setTextColor(context.getResources().getColor(R.color.primary_text_color));
                    }
                    tv.setPadding(20,20,20,20);
                    return view;
                }
            };

            myViewHolder.spinnerUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    ProductUnit unit = item.getProductUnitList().get(i);
                    item.setUnit(unit.getUnitValue()+" "+unit.getUnitName());
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            myViewHolder.spinnerUnit.setAdapter(unitAdapter);

        }else{
            myViewHolder.relative_unit.setVisibility(View.GONE);
        }

        if(item.getName().length()>1) {
            myViewHolder.tv_shortName.setText(item.getName().substring(0, 1));
            //image_view_shop .setText(shopName);
            String initials = "";
            if (item.getName().contains(" ")) {
                String[] nameArray = item.getName().split(" ");
                Log.d("nameArray ", item.getName());
                String firstChar = nameArray[0].substring(0, 1);
                String secondChar = "";
                if(nameArray.length>2 && nameArray[1].length()>0) {
                    secondChar = nameArray[1].substring(0, 1);
                    if(secondChar.contains("-") || secondChar.contains("("))
                        secondChar ="";
                }
                initials = firstChar + secondChar;
            } else if(item.getName().length()>2){
                initials = item.getName().substring(0, 2);
            }

            myViewHolder.tv_shortName.setText(initials);
        }
        Log.d("shopImage ", item.getProdImage1());
        Log.d("shopName ", item.getName());
        if(item.getProdImage1() !=null && item.getProdImage1().contains("http")){
            myViewHolder.tv_shortName.setVisibility(View.GONE);
            myViewHolder.imageView.setVisibility(View.VISIBLE);
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
            requestOptions.dontTransform();
            // requestOptions.override(Utility.dpToPx(150, context), Utility.dpToPx(150, context));
            // requestOptions.centerCrop();
            requestOptions.skipMemoryCache(false);

            Glide.with(context)
                    .load(item.getProdImage1())
                    .apply(requestOptions)
                    .error(R.drawable.ic_photo_black_192dp)
                    .into(myViewHolder.imageView);
        }else{
            myViewHolder.tv_shortName.setVisibility(View.VISIBLE);
            myViewHolder.imageView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if (myProductsList != null) {
            return myProductsList.size();
        }
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView textName, tv_shortName,textMrp, textSp, textOffPer, textStatus, textbarcode, tv_cartCount,tv_todo_status ;
        private ImageView imageView, image_minus, image_plus;
        private View rootView;
        private Button btnAddCart;
        private LinearLayout linear_plus_minus;
        private Spinner spinnerUnit;
        private RelativeLayout relative_unit;
        private TextView text_offer, text_frequency;

        public MyViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            textbarcode = itemView.findViewById(R.id.text_bar_code);
            textName = itemView.findViewById(R.id.text_name);
            tv_shortName = itemView.findViewById(R.id.tv_shortName);
            textMrp = itemView.findViewById(R.id.text_mrp);
            textSp = itemView.findViewById(R.id.text_sp);
            textOffPer = itemView.findViewById(R.id.text_off_percentage);
            textStatus = itemView.findViewById(R.id.text_status);
            imageView = itemView.findViewById(R.id.image_view);
            btnAddCart = itemView.findViewById(R.id.btn_addCart);
            linear_plus_minus = itemView.findViewById(R.id.linear_plus_minus);
            image_minus = itemView.findViewById(R.id.image_minus);
            image_plus = itemView.findViewById(R.id.image_plus);
            tv_cartCount = itemView.findViewById(R.id.tv_cartCount);
            spinnerUnit = itemView.findViewById(R.id.spinner_unit);
            relative_unit = itemView.findViewById(R.id.relative_unit);
            text_offer = itemView.findViewById(R.id.text_offer);
            text_frequency = itemView.findViewById(R.id.text_frequency);
            tv_todo_status = itemView.findViewById(R.id.tv_todo_status);
            if(flag.equals("ShopProductListActivity") ||flag.equals("ShoppursProductActivity")) {
                text_frequency.setVisibility(View.VISIBLE);
                text_frequency.setTypeface(typeface);
                text_frequency.setOnClickListener(this);
            }
            rootView.setOnClickListener(this);
            text_offer.setOnClickListener(this);
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v==imageView){
                if(flag.equals("ShopProductListActivity"))
                    ((ShopProductListActivity)context).showLargeImageDialog(myProductsList.get(getAdapterPosition()), imageView);
                else if(flag.equals("ShoppursProductActivity"))
                    ((ShoppursProductActivity)context).showLargeImageDialog(myProductsList.get(getAdapterPosition()), imageView);
            }
            if(v==text_offer){
                if(flag.equals("ShopProductListActivity") )
                ((ShopProductListActivity)context).showOfferDescription(myProductsList.get(getAdapterPosition()));
                else if(flag.equals("ShoppursProductActivity"))
                    ((ShoppursProductActivity)context).showOfferDescription(myProductsList.get(getAdapterPosition()));
                else if(flag.equals("CartActivity"))
                    ((CartActivity)context).showOfferDescription(myProductsList.get(getAdapterPosition()));
            }else if(v == text_frequency){
                ProductFrequency frequency = myProductsList.get(getAdapterPosition()).getFrequency();
                if(frequency!=null && frequency.getStatus().equals("1"))
                    DialogAndToast.showDialog("Product Frequency is already Active", context);
                else{
                    if(flag.equals("ShopProductListActivity") )
                    ((ShopProductListActivity)context).showFrequencyBottomShet(myProductsList.get(getAdapterPosition()), getAdapterPosition());
                    else if(flag.equals("ShoppursProductActivity"))
                        ((ShoppursProductActivity)context).showFrequencyBottomShet(myProductsList.get(getAdapterPosition()), getAdapterPosition());
                }
            }else if (v == rootView) {
                MyProduct item = (MyProduct) myProductsList.get(getAdapterPosition());
                if(flag.equals("ChatActivity")){
                    myItemClickListener.onItemClicked(getAdapterPosition(), 0);
                }else if(flag.equals("ToDo List")){
                    if(!dbHelper.checkProdExistInToDoList(item.getId(), shopCode)) {
                        myItemClickListener.onItemClicked(getAdapterPosition(), 0);
                        tv_todo_status.setVisibility(View.VISIBLE);
                    }
                } else {
                    Intent intent = new Intent(context, ProductDetailActivity.class);
                    intent.putExtra("MyProduct", item);
                    context.startActivity(intent);
                }
            }
        }
    }
}