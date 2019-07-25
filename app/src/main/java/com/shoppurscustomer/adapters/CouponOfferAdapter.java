package com.shoppurscustomer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.shoppurscustomer.R;
import com.shoppurscustomer.interfaces.MyItemClickListener;
import com.shoppurscustomer.models.Coupon;

import java.util.List;

public class CouponOfferAdapter extends RecyclerView.Adapter<CouponOfferAdapter.MyViewHolder> {
    private List<Object> myItemList;
    private Context context;
    private int colorTheme;

    public void setColorTheme(int colorTheme) {
        this.colorTheme = colorTheme;
    }

    private MyItemClickListener myItemClickListener;

    public MyItemClickListener getMyItemClickListener() {
        return myItemClickListener;
    }

    public void setMyItemClickListener(MyItemClickListener myItemClickListener) {
        this.myItemClickListener = myItemClickListener;
    }

    public CouponOfferAdapter(Context context, List<Object> myItemList) {
        super();
        this.context = context;
        this.myItemList = myItemList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.apply_coupon_item_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder myViewHolder, final int position) {
        {
            Coupon coupon = (Coupon) myItemList.get(position);
            myViewHolder.text_offerName.setText(coupon.getName());
            myViewHolder.textDesc.setText("Use this coupon to get "+(int)coupon.getPercentage()+"% off on your order.");
            myViewHolder.textMaxAmount.setText("Maximum discount: Rs. "+(int)coupon.getAmount());
        }
    }

    @Override
    public int getItemCount() {
        if (myItemList != null) {
            return myItemList.size();
        }
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView text_offerName,textDesc,textMaxAmount,textApply;

        public MyViewHolder(View itemView) {
            super(itemView);
            textDesc = itemView.findViewById(R.id.text_desc);
            textMaxAmount = itemView.findViewById(R.id.text_max_discount);
            textApply = itemView.findViewById(R.id.btn_apply);
            text_offerName=itemView.findViewById(R.id.text_name);
            textApply.setOnClickListener(this);
            textApply.setTextColor(colorTheme);
        }

        @Override
        public void onClick(View v) {
            if(v == textApply){
                myItemClickListener.onItemClicked(getAdapterPosition());
            }
        }
    }
}
