package com.shoppurscustomer.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.shoppurscustomer.R;
import com.shoppurscustomer.models.MyOrder;
import com.shoppurscustomer.models.MyOrderDetail;
import com.shoppurscustomer.models.MyProduct;
import com.shoppurscustomer.utilities.DialogAndToast;

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
    public void onBindViewHolder(final MyOrderDetailsAdapter.MyViewHolder holder, final int position) {
        final MyOrderDetail myOrderDetail = myOrderDetailslist.get(position);
        MyProduct myProduct = myOrderDetail.getMyProduct();

        //holder.orderRating;
        holder.tv_shopname.setText(String.valueOf(myOrderDetail.getShopName())+" Shop");
        holder.tv_prodname.setText("Product - "+myProduct.getName());
        if(myOrderDetail.getStatus()!=null /*&& myOrder.getOrderStatus().equals("pending")*/) {
            holder.tv_status.setVisibility(View.VISIBLE);
            holder.tv_status.setText("Status - "+ myOrderDetail.getStatus());
        }
        else holder.tv_status.setVisibility(View.GONE);
        holder.tv_qty.setText("Quantity - "+String.valueOf(myProduct.getQuantity()));
    }

    @Override
    public int getItemCount() {
        if (myOrderDetailslist != null) {
            return myOrderDetailslist.size();
        }
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_status, tv_qty, tv_prodname, tv_shopname;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_status = (TextView) itemView.findViewById(R.id.tv_status);
            tv_qty = (TextView) itemView.findViewById(R.id.tv_qty);
            tv_prodname = (TextView) itemView.findViewById(R.id.tv_prodname);
            tv_shopname = (TextView) itemView.findViewById(R.id.tv_shopname);

        }
    }
}