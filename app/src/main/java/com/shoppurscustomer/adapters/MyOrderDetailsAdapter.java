package com.shoppurscustomer.adapters;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.shoppurscustomer.R;
import com.shoppurscustomer.models.MyOrderDetail;
import com.shoppurscustomer.models.MyProduct;
import com.shoppurscustomer.utilities.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suraj kumar singh on 05-04-2019.
 */

public class MyOrderDetailsAdapter extends RecyclerView.Adapter<MyOrderDetailsAdapter.MyViewHolder> {
    private List<MyOrderDetail> myOrderDetailslist = new ArrayList<>();
    private Activity context;

    public MyOrderDetailsAdapter(Activity context, List<MyOrderDetail> myOrderDetailslist) {
        super();
        this.context = context;
        this.myOrderDetailslist = myOrderDetailslist;
    }

    @Override
    public MyOrderDetailsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.order_item_details, parent, false);
        return new MyOrderDetailsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyOrderDetailsAdapter.MyViewHolder myViewHolder, final int position) {
        final MyOrderDetail myOrderDetail = myOrderDetailslist.get(position);
        MyProduct item = myOrderDetail.getMyProduct();

        myViewHolder.textName.setText(item.getName());
        //myViewHolder.textAmount.setText("Rs. "+String.format("%.02f",item.getMrp()));
        myViewHolder.textSp.setText(Utility.numberFormat(item.getSellingPrice()));
        myViewHolder.textMrp.setText(Utility.numberFormat(item.getMrp()));
        myViewHolder.textMrp.setPaintFlags(myViewHolder.textMrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        float diff = item.getMrp() - item.getSellingPrice();
        if(diff > 0f){
            float percentage = diff * 100 /item.getMrp();
            myViewHolder.textOffPer.setText(String.format("%.02f",percentage)+"% off");
            myViewHolder.textMrp.setVisibility(View.VISIBLE);
            myViewHolder.textOffPer.setVisibility(View.VISIBLE);
        }else{
            myViewHolder.textMrp.setVisibility(View.GONE);
            myViewHolder.textOffPer.setVisibility(View.GONE);
        }
        myViewHolder.textQty.setText("Qty: "+item.getQuantity());

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
        if (myOrderDetailslist != null) {
            return myOrderDetailslist.size();
        }
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textName,textSp,textMrp,textOffPer,textQty;
        private ImageView imageView;
        private View rootView;

        public MyViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            textName=itemView.findViewById(R.id.text_name);
            textSp=itemView.findViewById(R.id.text_sp);
            textMrp=itemView.findViewById(R.id.text_mrp);
            textOffPer=itemView.findViewById(R.id.text_off_percentage);
            textQty=itemView.findViewById(R.id.text_qty);
            imageView=itemView.findViewById(R.id.image_view);
        }
    }
}