package com.shoppurscustomer.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.shoppurscustomer.R;
import com.shoppurscustomer.activities.InstantPayActivity;
import com.shoppurscustomer.activities.InstantScannarActivity;
import com.shoppurscustomer.activities.ShopContactListActivity;
import com.shoppurscustomer.models.MyShop;
import com.shoppurscustomer.utilities.Constants;
import com.shoppurscustomer.utilities.Utility;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by suraj kumar singh on 18-04-2019.
 */

public class ShopContactsAdapter extends RecyclerView.Adapter<ShopContactsAdapter.MyViewHolder> {
    private Context context;
    private List<MyShop> itemList;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String type;
    private int counter;

    public void setType(String type) {
        this.type = type;
    }


    public ShopContactsAdapter(Context context, List<MyShop> itemList) {
        this.context = context;
        this.itemList = itemList;
        sharedPreferences= context.getSharedPreferences(Constants.MYPREFERENCEKEY,MODE_PRIVATE);
        editor=sharedPreferences.edit();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.shop_contact_list_item_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        {
            MyShop item = (MyShop) itemList.get(position);
            MyViewHolder myViewHolder = (MyViewHolder)holder;
            myViewHolder.textCustName.setText(item.getName());
            myViewHolder.textMobile.setText(item.getMobile());
            /*if(TextUtils.isEmpty(item.getAddress())){
                myViewHolder.textAddress.setVisibility(View.GONE);
                myViewHolder.textStateCity.setVisibility(View.GONE);
            }else{
                myViewHolder.textAddress.setVisibility(View.VISIBLE);
                myViewHolder.textStateCity.setVisibility(View.VISIBLE);
                myViewHolder.textAddress.setText(item.getAddress());
                myViewHolder.textStateCity.setText(item.getState()+", "+item.getCity());
            }*/
            // myViewHolder.textAddress.setText(item.getAddress());
            //  myViewHolder.textStateCity.setText(item.getState()+", "+item.getCity());

            String initials = "";
            if(!TextUtils.isEmpty(item.getName()) && item.getName().length()>0){
            if (item.getName().contains(" ")) {
                String[] name = item.getName().split(" ");
                initials = name[0].substring(0, 1);
            } else {
                initials = item.getName().substring(0, 1);
            }
        }

            myViewHolder.textInitial.setText(initials);

            if(item.getShopimage().contains("http")){
                myViewHolder.textInitial.setVisibility(View.GONE);
                myViewHolder.imageView.setVisibility(View.VISIBLE);
            }else{
                myViewHolder.textInitial.setVisibility(View.VISIBLE);
                myViewHolder.imageView.setVisibility(View.GONE);
               // myViewHolder.textInitial.setBackgroundColor(getTvColor(counter));
                Utility.setColorFilter(myViewHolder.textInitial.getBackground(),getTvColor(counter));
                counter++;
                if(counter == 12){
                    counter = 0;
                }
            }


            RequestOptions requestOptions = new RequestOptions();
            requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
            requestOptions.dontTransform();
            // requestOptions.override(Utility.dpToPx(150, context), Utility.dpToPx(150, context));
            // requestOptions.centerCrop();
            requestOptions.skipMemoryCache(false);

            Glide.with(context)
                    .load(item.getShopimage())
                    .apply(requestOptions)
                    .into(myViewHolder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        if (itemList != null) {
            return itemList.size();
        }
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView textInitial,textCustName,textAddress,textStateCity,textMobile,textEmail;
        private CircleImageView imageView;
        private ImageView imageMenu;
        private View rootView;


        public MyViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            textCustName=itemView.findViewById(R.id.text_customer_name);
            textInitial=itemView.findViewById(R.id.tv_initial);
            textMobile=itemView.findViewById(R.id.text_mobile);
            textAddress=itemView.findViewById(R.id.text_address);
            textStateCity=itemView.findViewById(R.id.text_state_city);
            imageView=itemView.findViewById(R.id.image_view);
            imageMenu=itemView.findViewById(R.id.image_menu);
            imageMenu.setOnClickListener(this);
            rootView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v == rootView){
                    MyShop item = (MyShop)itemList.get(getAdapterPosition());
                    if(!TextUtils.isEmpty(type)) {
                        if(type.equals("contactList"))
                            ((ShopContactListActivity) (context)).scanStoresToPay(item.getMobile());
                        else
                        ((InstantScannarActivity) (context)).scanStoresToPay(item.getMobile());
                    }
            }else if(v == imageMenu){
                //myItemTypeClickListener.onItemClicked(getAdapterPosition(),2);
                final MyShop customer = (MyShop)itemList.get(getAdapterPosition());
               /* if(customer.getIsFav().equals("Y")){
                    myItemClickListener.onItemClicked(getAdapterPosition(),1);
                }else{
                    myItemClickListener.onItemClicked(getAdapterPosition(),2);
                }*/

                /*PopupMenu popupMenu = new PopupMenu(v.getContext(), imageMenu);

                ((Activity)context).getMenuInflater().inflate(R.menu.customer_popup_menu, popupMenu.getMenu());

                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        //Toast.makeText(getBaseContext(), "You selected the action : " + item.getTitle()+" position "+position, Toast.LENGTH_SHORT).show();
                        if(item.getTitle().equals("Call")){
                            myItemTypeClickListener.onItemClicked(getAdapterPosition(),3);
                            Log.i("Adapter","Call Customer"+customer.getName());
                        }else if(item.getTitle().equals("Message")){
                            myItemTypeClickListener.onItemClicked(getAdapterPosition(),4);
                            Log.i("Adapter","Message Customer"+customer.getName());
                        }
                        return true;
                    }
                });*/
            }
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
