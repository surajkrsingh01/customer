package com.shoppurscustomer.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shoppurscustomer.R;
import com.shoppurscustomer.activities.CartActivity;
import com.shoppurscustomer.activities.MyOrderDetailsActivity;
import com.shoppurscustomer.models.CartItem;
import com.shoppurscustomer.models.MyOrder;
import com.shoppurscustomer.utilities.DialogAndToast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suraj kumar singh on 20-03-2019.
 */

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.MyViewHolder> {
    private List<MyOrder> myOrderList = new ArrayList<>();
    private Activity context;

    public MyOrderAdapter(Activity context, List<MyOrder> myOrderList) {
        super();
        this.context = context;
        this.myOrderList = myOrderList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.order_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final MyOrder myOrder = myOrderList.get(position);

        //holder.orderRating;
        holder.tv_orderId.setText("Order Id - "+String.valueOf(myOrder.getOrderId()));
        holder.tv_orderStatus.setText("("+myOrder.getOrderStatus()+")");
        if(myOrder.getOrderStatus()!=null && myOrder.getOrderStatus().equals("pending"))
            holder.btn_reorder.setVisibility(View.VISIBLE);
        else holder.btn_reorder.setVisibility(View.GONE);
        holder.tv_orderTotalAmount.setText(String.valueOf(myOrder.getToalAmount()));
        holder.tv_date.setText(myOrder.getOrderDate());
        holder.btn_reorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogAndToast.showToast("Reorder Clicked", context);
            }
        });
        holder.relative_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MyOrderDetailsActivity.class);
                intent.putExtra("orderId", myOrder.getOrderId());
                intent.putExtra("orderDate", myOrder.getOrderDate());
                intent.putExtra("orderAmount", myOrder.getToalAmount());
                intent.putExtra("orderStatus", myOrder.getOrderStatus());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (myOrderList != null) {
            return myOrderList.size();
        }
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_orderId, tv_orderStatus, tv_orderTotalAmount, tv_date;
        private Button btn_reorder;
        private RelativeLayout relative_header;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_orderId = (TextView) itemView.findViewById(R.id.tv_orderId);
            tv_orderStatus = (TextView) itemView.findViewById(R.id.tv_orderStatus);
            tv_orderTotalAmount = (TextView) itemView.findViewById(R.id.tv_orderTotalAmount);
            tv_date = (TextView) itemView.findViewById(R.id.tv_date);
            btn_reorder = (Button) itemView.findViewById(R.id.btn_reorder);
            relative_header = (RelativeLayout) itemView.findViewById(R.id.relative_header);
        }
    }
}