package com.shoppurs.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.shoppurs.R;
import com.shoppurs.activities.ApplyOffersActivity;
import com.shoppurs.activities.ProductDetailActivity;
import com.shoppurs.database.DbHelper;
import com.shoppurs.interfaces.MyItemTypeClickListener;
import com.shoppurs.models.MyProduct;
import com.shoppurs.models.ProductComboOffer;
import com.shoppurs.models.ProductDiscountOffer;
import com.shoppurs.models.ProductPriceOffer;
import com.shoppurs.models.ProductUnit;
import com.shoppurs.utilities.Utility;

import java.util.ArrayList;
import java.util.List;

public class ApplyOfferAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Object> myItemList = new ArrayList<>();
    private Context context;
    private int colorTheme;
    private boolean isDarkTheme;
    private DbHelper dbHelper;
    private String shopCode;

    public void setDarkTheme(boolean darkTheme) {
        isDarkTheme = darkTheme;
    }

    private MyItemTypeClickListener myItemTypeClickListener;

    public void setShopCode(String shopCode){
        this.shopCode = shopCode;
    }


    public void setMyItemTypeClickListener(MyItemTypeClickListener myItemTypeClickListener) {
        this.myItemTypeClickListener = myItemTypeClickListener;
    }

    public ApplyOfferAdapter(Context context, List<Object> myItemList) {
        super();
        this.context = context;
        this.myItemList = myItemList;
        dbHelper = new DbHelper(context);
    }

    public void setColorTheme(int colorTheme){
        this.colorTheme = colorTheme;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.product_list_item_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
       final MyProduct item = (MyProduct) myItemList.get(position);
        final MyViewHolder myViewHolder = (MyViewHolder)holder;

        if(dbHelper.checkProdExistInCart(item.getId(), shopCode)){
            myViewHolder.btnAddCart.setVisibility(View.GONE);
            myViewHolder.linear_plus_minus.setVisibility(View.VISIBLE);
            myViewHolder.tv_cartCount.setText(String.valueOf(dbHelper.getProductQuantity(item.getId(), shopCode, "normal")));
            item.setFreeProductPosition(dbHelper.getFreeProductPosition(item.getId(), shopCode));
            item.setOfferItemCounter(dbHelper.getOfferCounter(item.getId(), shopCode));
            item.setQuantity(Integer.parseInt(myViewHolder.tv_cartCount.getText().toString()));
            item.setTotalAmount(dbHelper.getTotalAmount(item.getId(), shopCode, "normal"));
            item.setSellingPrice(dbHelper.getProductSellingPrice(item.getId(), shopCode));
            Log.d("Quantity ........", ""+dbHelper.getProductQuantity(item.getId(), shopCode, "normal"));
            Log.d("Total Amout ........", ""+dbHelper.getTotalAmount(item.getId(), shopCode, "normal"));
        }else {
            item.setTotalAmount(0);
            item.setQuantity(0);
            myViewHolder.tv_cartCount.setText(String.valueOf(0));
            myViewHolder.linear_plus_minus.setVisibility(View.GONE);
            myViewHolder.btnAddCart.setVisibility(View.VISIBLE);
        }

        myViewHolder.btnAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //myViewHolder.linear_plus_minus.setVisibility(View.VISIBLE);
                //myViewHolder.btnAddCart.setVisibility(View.GONE);
                int count = Integer.parseInt(myViewHolder.tv_cartCount.getText().toString());
                ((ApplyOffersActivity)context).updateCart(2, position);
                //((ShopProductListActivity)context).add_toCart(item);
                //DialogAndToast.showToast("Add to Cart ", context);
            }
        });
        myViewHolder.image_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = Integer.parseInt(myViewHolder.tv_cartCount.getText().toString());
                ((ApplyOffersActivity)context).updateCart(1, position);
            }
        });
        myViewHolder.image_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = Integer.parseInt(myViewHolder.tv_cartCount.getText().toString());
                ((ApplyOffersActivity)context).updateCart(2, position);
            }
        });
       // myViewHolder.code.setText(item.getBarCode());
        myViewHolder.textName.setText(item.getName());
        //myViewHolder.textAmount.setText("Rs. "+String.format("%.02f",item.getMrp()));
        myViewHolder.textSp.setText(Utility.numberFormat(Double.valueOf(item.getSellingPrice())));
        myViewHolder.textMrp.setText(Utility.numberFormat(Double.valueOf(item.getMrp())));
        myViewHolder.textMrp.setPaintFlags(myViewHolder.textMrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        float diff = Float.valueOf(item.getMrp()) - Float.valueOf(item.getSellingPrice());
        if(diff > 0f){
            float percentage = diff * 100 /Float.valueOf(item.getMrp());
            myViewHolder.textOffPer.setText(String.format("%.02f",percentage)+"% off");
            myViewHolder.textMrp.setVisibility(View.VISIBLE);
            myViewHolder.textOffPer.setVisibility(View.VISIBLE);
        }else{
            myViewHolder.textMrp.setVisibility(View.GONE);
            myViewHolder.textOffPer.setVisibility(View.GONE);
        }

        if(item.getProductPriceOffer() != null ) {
            ProductPriceOffer productPriceOffer = item.getProductPriceOffer();
            myViewHolder.text_offer.setText(productPriceOffer.getOfferName());
        }else if(item.getProductDiscountOffer() != null ){
            ProductDiscountOffer productDiscountOffer = item.getProductDiscountOffer();
            myViewHolder.text_offer.setText(productDiscountOffer.getOfferName());
        }else if(item.getProductComboOffer() != null ){
            ProductComboOffer productComboOffer = item.getProductComboOffer();
            myViewHolder.text_offer.setText(productComboOffer.getOfferName());
        }
        if(item.getProductPriceOffer() == null  && item.getProductDiscountOffer() == null  && item.getProductComboOffer() == null){
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


        RequestOptions requestOptions = new RequestOptions();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        // requestOptions.override(Utility.dpToPx(150, context), Utility.dpToPx(150, context));
        requestOptions.centerCrop();
        requestOptions.skipMemoryCache(false);

        Glide.with(context)
                .load(item.getProdImage1())
                .apply(requestOptions)
                .error(R.drawable.ic_photo_black_192dp)
                .into(myViewHolder.imageView);
    }


    @Override
    public int getItemCount() {
        if (myItemList != null) {
            return myItemList.size();
        }
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView textName,textMrp, textSp, textOffPer, textStatus, textbarcode, tv_cartCount;
        private ImageView imageView,image_minus, image_plus;
        private View rootView;
        private Button btnAddCart;
        private LinearLayout linear_plus_minus;
        private Spinner spinnerUnit;
        private RelativeLayout relative_unit;
        private TextView text_offer;

        public MyViewHolder(View itemView) {
            super(itemView);

            rootView = itemView;
            textbarcode = itemView.findViewById(R.id.text_bar_code);
            textName=itemView.findViewById(R.id.text_name);
            textMrp=itemView.findViewById(R.id.text_mrp);
            textSp=itemView.findViewById(R.id.text_sp);
            textOffPer=itemView.findViewById(R.id.text_off_percentage);
            textStatus=itemView.findViewById(R.id.text_status);
            imageView=itemView.findViewById(R.id.image_view);
            btnAddCart = itemView.findViewById(R.id.btn_addCart);
            linear_plus_minus = itemView.findViewById(R.id.linear_plus_minus);
            image_plus = itemView.findViewById(R.id.image_plus);
            image_minus = itemView.findViewById(R.id.image_minus);
            tv_cartCount = itemView.findViewById(R.id.tv_cartCount);
            spinnerUnit = itemView.findViewById(R.id.spinner_unit);
            relative_unit = itemView.findViewById(R.id.relative_unit);
            text_offer = itemView.findViewById(R.id.text_offer);
            rootView.setOnClickListener(this);
            text_offer.setOnClickListener(this);
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(view == imageView){
                final MyProduct product = (MyProduct) myItemList.get(getAdapterPosition());
                ((ApplyOffersActivity)context).showLargeImageDialog(product, imageView);
            } else if(view==text_offer){
                MyProduct item = (MyProduct) myItemList.get(getAdapterPosition());
                ((ApplyOffersActivity)context).showOfferDescription(item);
            }else if(view == rootView){
                MyProduct item = (MyProduct) myItemList.get(getAdapterPosition());
                Intent intent = new Intent(context, ProductDetailActivity.class);
                intent.putExtra("MyProduct",item);
                context.startActivity(intent);
            }
        }
    }
}
