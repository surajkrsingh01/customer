package com.shoppurs.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
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

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.shoppurs.R;
import com.shoppurs.activities.Settings.FrequencyProductsActivity;
import com.shoppurs.activities.ShopProductListActivity;
import com.shoppurs.database.DbHelper;
import com.shoppurs.models.MyProduct;
import com.shoppurs.models.ProductDiscountOffer;
import com.shoppurs.models.ProductFrequency;
import com.shoppurs.models.ProductPriceOffer;
import com.shoppurs.models.ProductUnit;
import com.shoppurs.utilities.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suraj kumar singh on 20-04-2019.
 */

public class FrequencyProductListAdapter extends RecyclerView.Adapter<FrequencyProductListAdapter.MyViewHolder> {
    private List<MyProduct> myProductsList = new ArrayList<>();
    private Context context;
    private DbHelper dbHelper;
    private String shopCode;
    private boolean isDarkTheme;
    private Typeface typeface;

    public void setDarkTheme(boolean darkTheme) {
        isDarkTheme = darkTheme;
    }

    public FrequencyProductListAdapter(Context context, List<MyProduct> myProducts) {
        super();
        this.context = context;
        this.myProductsList = myProducts;
        dbHelper = new DbHelper(context);
    }

    public void setTypeFace(Typeface typeFace){
        this.typeface = typeFace;
    }

    public void setShopCode(String code){
        this.shopCode = code;
    }

    @Override
    public FrequencyProductListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.frequency_product_list_item_layout, parent, false);
        return new FrequencyProductListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final FrequencyProductListAdapter.MyViewHolder myViewHolder, final int position) {
        {
            final MyProduct item =  myProductsList.get(position);
            ProductFrequency frequency = item.getFrequency();
           // myViewHolder.textbarcode.setText(item.getBarCode());
            myViewHolder.textName.setText(item.getName());
            //myViewHolder.textAmount.setText("Rs. "+String.format("%.02f",item.getMrp()));
            myViewHolder.textSp.setText(Utility.numberFormat(Double.valueOf(item.getSellingPrice())));
            myViewHolder.textMrp.setText(Utility.numberFormat(Double.valueOf(item.getMrp())));
            myViewHolder.textMrp.setPaintFlags(myViewHolder.textMrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            myViewHolder.text_start_date.setText("Start Order Date: "+frequency.getStartDate());
            myViewHolder.text_end_date.setText("End Order Date: "+frequency.getEndDate());
            myViewHolder.text_next_date.setText("Next Order Date: "+frequency.getNextOrderDate());
            if(frequency.getStatus().equals("1"))
            myViewHolder.text_frequency_status.setText("Active");
            else {
                myViewHolder.text_frequency_status.setText("InActive");
                myViewHolder.text_frequency.setVisibility(View.VISIBLE);
            }

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
    }

    @Override
    public int getItemCount() {
        if (myProductsList != null) {
            return myProductsList.size();
        }
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView textName,textMrp, textSp, textOffPer, textStatus, textbarcode, text_frequency, text_start_date, text_end_date,text_next_date, text_frequency_status ;
        private ImageView imageView, image_minus, image_plus;
        private View rootView;
        private Spinner spinnerUnit;
        private RelativeLayout relative_unit;

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
            image_plus = itemView.findViewById(R.id.image_plus);
            image_minus = itemView.findViewById(R.id.image_minus);
            spinnerUnit = itemView.findViewById(R.id.spinner_unit);
            relative_unit = itemView.findViewById(R.id.relative_unit);
            text_frequency = itemView.findViewById(R.id.text_frequency);
            text_start_date = itemView.findViewById(R.id.text_start_date);
            text_end_date = itemView.findViewById(R.id.text_end_date);
            text_next_date = itemView.findViewById(R.id.text_next_date);
            text_frequency_status = itemView.findViewById(R.id.text_frequency_status);
            text_frequency.setTypeface(typeface);
            text_frequency.setOnClickListener(this);
            rootView.setOnClickListener(this);
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v == imageView){
                ((FrequencyProductsActivity)context).showLargeImageDialog(myProductsList.get(getAdapterPosition()), imageView);
            }else if(v == rootView){
                MyProduct item = (MyProduct) myProductsList.get(getAdapterPosition());
                ((FrequencyProductsActivity)context).showProductDetails(item);
            }else if(v == text_frequency){
                ((FrequencyProductsActivity)context).showFrequencyBottomShet(myProductsList.get(getAdapterPosition()), getAdapterPosition());
            }
        }
    }
}