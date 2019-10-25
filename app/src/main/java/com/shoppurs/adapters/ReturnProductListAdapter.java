package com.shoppurs.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.shoppurs.R;
import com.shoppurs.activities.Settings.ReturnProductsActivity;
import com.shoppurs.database.DbHelper;
import com.shoppurs.models.MyProduct;
import com.shoppurs.utilities.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suraj kumar singh on 20-04-2019.
 */

public class ReturnProductListAdapter extends RecyclerView.Adapter<ReturnProductListAdapter.MyViewHolder> {
    private List<MyProduct> myProductsList = new ArrayList<>();
    private Context context;
    private DbHelper dbHelper;
    private String shopCode;
    private boolean isDarkTheme;
    private Typeface typeface;

    public void setDarkTheme(boolean darkTheme) {
        isDarkTheme = darkTheme;
    }

    public ReturnProductListAdapter(Context context, List<MyProduct> myProducts) {
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
    public ReturnProductListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.return_product_list_item_layout, parent, false);
        return new ReturnProductListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ReturnProductListAdapter.MyViewHolder myViewHolder, final int position) {
        {
            final MyProduct item =  myProductsList.get(position);
            myViewHolder.textName.setText(item.getName());
            myViewHolder.textSp.setText(Utility.numberFormat(Double.valueOf(item.getSellingPrice())));
            myViewHolder.textMrp.setText(Utility.numberFormat(Double.valueOf(item.getMrp())));
            myViewHolder.textMrp.setPaintFlags(myViewHolder.textMrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            if (item.getReturnStatus() == 2) {
                    myViewHolder.relative_return_product.setVisibility(View.VISIBLE);
                    myViewHolder.text_return_status.setText("Pending");
                } else {
                    myViewHolder.relative_return_product.setVisibility(View.GONE);
                    if (item.getReturnStatus() == 0)
                        myViewHolder.text_return_status.setText("Cancelled");
                    else myViewHolder.text_return_status.setText("Accepted");
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
        private TextView textName,textMrp, textSp, textOffPer, text_return_status;
        private ImageView imageView;
        private View rootView;
        private RelativeLayout relative_return_product;
        private Button btn_accept, btn_reject;

        public MyViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            textName=itemView.findViewById(R.id.text_name);
            textMrp=itemView.findViewById(R.id.text_mrp);
            textSp=itemView.findViewById(R.id.text_sp);
            textOffPer=itemView.findViewById(R.id.text_off_percentage);
            imageView=itemView.findViewById(R.id.image_view);
            text_return_status = itemView.findViewById(R.id.text_return_status);
            relative_return_product = itemView.findViewById(R.id.relative_return_product);
            btn_accept = itemView.findViewById(R.id.btn_accept);
            btn_reject = itemView.findViewById(R.id.btn_reject);
            btn_accept.setOnClickListener(this);
            btn_reject.setOnClickListener(this);
            rootView.setOnClickListener(this);
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            MyProduct item = (MyProduct) myProductsList.get(getAdapterPosition());
            if(v == imageView){
                ((ReturnProductsActivity)context).showLargeImageDialog(item, imageView);
            }else if(v == rootView){
                ((ReturnProductsActivity)context).showProductDetails(item);
            }else if(v == btn_accept){
                ((ReturnProductsActivity)context).acceptRequest(item);
            }else if(v == btn_reject){
                ((ReturnProductsActivity)context).rejectRequest(item);
            }
        }
    }
}