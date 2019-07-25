package com.shoppurscustomer.adapters;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.shoppurscustomer.R;
import com.shoppurscustomer.activities.MyOrderDetailsActivity;
import com.shoppurscustomer.models.MyOrder;
import com.shoppurscustomer.utilities.DialogAndToast;
import com.shoppurscustomer.utilities.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suraj kumar singh on 20-03-2019.
 */

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.MyViewHolder> {
    private List<MyOrder> myOrderList = new ArrayList<>();
    private Activity context;
    private int counter;

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
    public void onBindViewHolder(final MyViewHolder myViewHolder, final int position) {

            MyOrder item = (MyOrder) myOrderList.get(position);
            myViewHolder.textCustName.setText(item.getShopName());
            myViewHolder.textAmount.setText(Utility.numberFormat(item.getToalAmount()));
            myViewHolder.textDeliveryType.setText(item.getOrderDeliveryMode());
            myViewHolder.textViewStatus.setText(item.getOrderStatus());

            if(item.getOrderStatus() != null){
                myViewHolder.textViewStatus.setVisibility(View.VISIBLE);
                if(item.getOrderStatus().equals("Accepted")|| item.getOrderStatus().equals("Delivered")){
                    myViewHolder.textViewStatus.setTextColor(context.getResources().getColor(R.color.green500));
                }else{
                    myViewHolder.textViewStatus.setTextColor(context.getResources().getColor(R.color.red_500));
                }
            }else{
                myViewHolder.textViewStatus.setVisibility(View.GONE);
            }

            String initials = "";
            if(item.getShopName().contains(" ")){
                String[] name = item.getShopName().split(" ");
                initials = name[0].substring(0,1)+name[1].substring(0,1);
            }else{
                initials = item.getShopName().substring(0,2);
            }

            myViewHolder.textInitial.setText(initials);

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
            // requestOptions.override(Utility.dpToPx(150, context), Utility.dpToPx(150, context));
            requestOptions.centerCrop();
            requestOptions.skipMemoryCache(false);

            Glide.with(context)
                    .load(item.getOrderImage())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            myViewHolder.textInitial.setVisibility(View.VISIBLE);
                            myViewHolder.imageView.setVisibility(View.GONE);
                            myViewHolder.textInitial.setBackgroundColor(getTvColor(counter));
                            counter++;
                            if(counter == 12){
                                counter = 0;
                            }
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            myViewHolder.textInitial.setVisibility(View.GONE);
                            myViewHolder.imageView.setVisibility(View.VISIBLE);
                            return false;
                        }
                    })
                    .apply(requestOptions)
                    .into(myViewHolder.imageView);

    }

    @Override
    public int getItemCount() {
        if (myOrderList != null) {
            return myOrderList.size();
        }
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener {
        private TextView textInitial,textCustName,textAmount,textDeliveryType,textViewStatus;
        private ImageView imageView;
        private View rootView;

        public MyViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            textCustName=itemView.findViewById(R.id.text_customer_name);
            textInitial=itemView.findViewById(R.id.tv_initial);
            textAmount=itemView.findViewById(R.id.text_amount);
            textDeliveryType=itemView.findViewById(R.id.text_delivery_type);
            textViewStatus=itemView.findViewById(R.id.text_status);
            imageView=itemView.findViewById(R.id.image_view);
            rootView.setOnTouchListener(this);
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    zoomAnimation(true,rootView);
                    //myItemTouchListener.onPressDown(getAdapterPosition());
                    break;
                // break;

                case MotionEvent.ACTION_UP:
                    zoomAnimation(false,rootView);
                    MyOrder item = (MyOrder) myOrderList.get(getAdapterPosition());
                        Intent intent = new Intent(context, MyOrderDetailsActivity.class);

                        intent.putExtra("orderId",String.valueOf(item.getOrderId()));
                        intent.putExtra("orderNumber",item.getOrderNumber());
                        intent.putExtra("shopName",item.getShopName());
                        intent.putExtra("orderDate",item.getOrderDate());
                        intent.putExtra("orderAmount",item.getToalAmount());
                        intent.putExtra("deliveryMode",item.getOrderDeliveryMode());
                        intent.putExtra("orderStatus",item.getOrderStatus());
                       // intent.putExtra("ordPaymentStatus",item.getOrderPayStatus());
                        intent.putExtra("orderPosition",getAdapterPosition());

                        /*if(Utility.getTimeStamp("yyyy-MM-dd").equals(item.getOrderDate().split(" ")[0]))
                            intent.putExtra("type","today");
                        else
                            intent.putExtra("type","pre");*/

                        context.startActivity(intent);

                    break;
                case MotionEvent.ACTION_CANCEL:
                    Log.i("Adapter","onPressCancel");
                    zoomAnimation(false,rootView);
                    break;
            }
            return true;
        }
    }
    private void zoomAnimation(boolean zoomOut,View view){
        if(zoomOut){
            Animation aniSlide = AnimationUtils.loadAnimation(context,R.anim.zoom_out);
            aniSlide.setFillAfter(true);
            view.startAnimation(aniSlide);
        }else{
            Animation aniSlide = AnimationUtils.loadAnimation(context,R.anim.zoom_in);
            aniSlide.setFillAfter(true);
            view.startAnimation(aniSlide);
        }
    }

    private int getTvColor(int position){

        if(position >= 12){
            position = 0;
        }

        int[] tvColor={context.getResources().getColor(R.color.light_blue500),
                context.getResources().getColor(R.color.yellow500),context.getResources().getColor(R.color.green500),
                context.getResources().getColor(R.color.orange500),context.getResources().getColor(R.color.red_500),
                context.getResources().getColor(R.color.teal_500),context.getResources().getColor(R.color.cyan500),
                context.getResources().getColor(R.color.deep_orange500),context.getResources().getColor(R.color.blue500),
                context.getResources().getColor(R.color.purple500),context.getResources().getColor(R.color.amber500),
                context.getResources().getColor(R.color.light_green500)};

        return tvColor[position];
    }
}