package com.shoppurscustomer.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.shoppurscustomer.R;
import com.shoppurscustomer.activities.CartActivity;
import com.shoppurscustomer.models.CartItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suraj kumar singh on 20-03-2019.
 */

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {
    private List<CartItem> cartItems = new ArrayList<>();
    private Activity context;

    public CartAdapter(Activity context, List<CartItem> cartItems) {
        super();
        this.context = context;
        this.cartItems = cartItems;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final CartItem cartItem = cartItems.get(position);

        holder.tv_name.setText(cartItem.getItemName());
        holder.tv_shopname.setText(cartItem.getShopCode());
        holder.tv_price.setText("Price: Rs" +cartItem.getPrice());
        if(cartItem.getColor()==null)
            holder.tv_color.setVisibility(View.GONE);
        else
        holder.tv_color.setText("Color: " +String.valueOf(cartItem.getColor()));
        if(cartItem.getSize()==0)
            holder.tv_size.setVisibility(View.GONE);
        else
        holder.tv_size.setText("Size: " +String.valueOf(cartItem.getSize()));
        holder.tv_subTotal.setText("Amount: " +String.valueOf(cartItem.getTotalAmout()));
        holder.tv_quantity.setText(String.valueOf(cartItem.getQuantity()));


        holder.plus_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = Integer.parseInt(holder.tv_quantity.getText().toString());
                cartItem.setQuantity(qty + 1);
                //holder.tv_quantity.setText(String.valueOf(cartItem.getQuantity()));
                cartItem.setTotalAmout(cartItem.getQuantity() * cartItem.getPrice());
                //holder.tv_subTotal.setText(String.valueOf(cartItem.getTotalAmout()));
                ((CartActivity) context).updateCart(cartItem);
            }
        });

        holder.minus_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = Integer.parseInt(holder.tv_quantity.getText().toString());
                if(cartItem.getQuantity()>1) {
                    cartItem.setQuantity(qty - 1);
                    //holder.tv_quantity.setText(String.valueOf(cartItem.getQuantity()));
                    cartItem.setTotalAmout(cartItem.getQuantity() * cartItem.getPrice());
                    //holder.tv_subTotal.setText(String.valueOf(cartItem.getTotalAmout()));
                    ((CartActivity) context).updateCart(cartItem);
                }
            }
        });

        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartItems.remove(cartItem);
                notifyItemRemoved(position);
                ((CartActivity) context).removeCart(cartItem);
            }
        });
    }


   /* //Animating single element
    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.push_right_in);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
        position++;
    }*/

    @Override
    public int getItemCount() {
        //Log.d("Size List:",String.valueOf(callListResponses.size()));
        if (cartItems != null) {
            return cartItems.size();
        }
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_price, tv_name,tv_shopname, tv_size, tv_quantity, tv_color, tv_subTotal;

        private Button plus_btn, minus_btn, btn_delete;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_shopname = itemView.findViewById(R.id.tv_shopname);
            tv_price = (TextView) itemView.findViewById(R.id.tv_rate);
            tv_subTotal = (TextView) itemView.findViewById(R.id.tv_subTotal);
            tv_size = (TextView) itemView.findViewById(R.id.tv_size);
            tv_quantity = (TextView) itemView.findViewById(R.id.tv_qty);
            tv_color = (TextView) itemView.findViewById(R.id.tv_color);
            plus_btn = (Button) itemView.findViewById(R.id.plus_btn);
            minus_btn = (Button) itemView.findViewById(R.id.minus_btn);
            btn_delete = (Button) itemView.findViewById(R.id.btn_delete);
        }
    }
}