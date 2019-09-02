package com.shoppurs.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.shoppurs.R;
import com.shoppurs.activities.CartActivity;
import com.shoppurs.models.Coupon;

import java.util.List;

/**
 * Created by suraj kumar singh on 18-04-2019.
 */

public class AppliedCouponsAdapter extends RecyclerView.Adapter<AppliedCouponsAdapter.MyViewHolder> {
    private Context context;
    private List<Coupon> couponList;

    public AppliedCouponsAdapter(Context context, List<Coupon> coupons) {
        this.context = context;
        this.couponList = coupons;
    }

    @Override
    public AppliedCouponsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_applied_coupons, parent, false);
        return new AppliedCouponsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AppliedCouponsAdapter.MyViewHolder myViewHolder, final int position) {
        {
                Coupon item = couponList.get(position);
                myViewHolder.tv_offer_name.setText(item.getName());
                myViewHolder.tv_offer_desc.setText("Coupon Applied on Shop "+item.getShopCode()+" " +item.getPercentage()+" %");

        }
    }

    @Override
    public int getItemCount() {
        if (couponList != null) {
            return couponList.size();
        }
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
         private TextView tv_offer_name, tv_offer_desc;
         private ImageView image_remove_offer;


        public MyViewHolder(View itemView) {
            super(itemView);
            tv_offer_name = itemView.findViewById(R.id.tv_offer_name);
            tv_offer_desc = itemView.findViewById(R.id.tv_offer_desc);
            image_remove_offer = itemView.findViewById(R.id.image_remove_offer);
           // Utility.setColorFilter(image_remove_offer.getDrawable(),colorTheme);
            image_remove_offer.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Coupon coupon = couponList.get(getAdapterPosition());
            if(v == image_remove_offer){
                //DialogAndToast.showDialog("want to remove "+coupon.getName(), context);
                ((CartActivity)(context)).removeCoupon(coupon, getAdapterPosition());
            }
        }
    }
}
