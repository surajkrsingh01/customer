package com.shoppurscustomer.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.shoppurscustomer.R;
import com.shoppurscustomer.activities.ProductListActivity;
import com.shoppurscustomer.activities.ShopProductListActivity;
import com.shoppurscustomer.models.MyHeader;
import com.shoppurscustomer.models.MyShop;
import com.shoppurscustomer.utilities.Constants;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by suraj kumar singh on 09-03-2019.
 */

public class ShopAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;
    private List<Object> mShopList;
    private String type;
    private String catId, subcatid, subcatname;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public ShopAdapter(Context context, List<Object> mShopList, String type,String catId, String subcatid, String subcatname){
        this.context = context;
        this.mShopList = mShopList;
        this.type = type;
        this.catId = catId;
        this.subcatid = subcatid;
        this.subcatname = subcatname;
        sharedPreferences= context.getSharedPreferences(Constants.MYPREFERENCEKEY,MODE_PRIVATE);
        editor=sharedPreferences.edit();
    }

    public class MyShopHeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
       private TextView textHeader, textDesc;
        public MyShopHeaderViewHolder(View itemView){
            super(itemView);
            textHeader=itemView.findViewById(R.id.text_title);
            textDesc=itemView.findViewById(R.id.text_desc);
        }

        @Override
        public void onClick(View v) {

        }
    }

    public class MyShopTitleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView textHeader;
        public MyShopTitleViewHolder(View itemView){
            super(itemView);
            textHeader=itemView.findViewById(R.id.text_title);
        }

        @Override
        public void onClick(View v) {

        }
    }

    public class MyShopListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnTouchListener{
        private TextView textCustName,text_shop_mobile, textAddress, textStateCity;
        private ImageView imageView;
        private View rootView;

        public MyShopListViewHolder(View itemView){
            super(itemView);
            rootView = itemView;
            textCustName=itemView.findViewById(R.id.text_customer_name);
            text_shop_mobile = itemView.findViewById(R.id.text_shop_mobile);
            textAddress=itemView.findViewById(R.id.text_address);
            textStateCity=itemView.findViewById(R.id.text_state_city);
            imageView=itemView.findViewById(R.id.image_view);
            rootView.setOnTouchListener(this);
        }

        @Override
        public void onClick(View view) {
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    //Log.i("Adapter","onPressDown");
                    zoomAnimation(true,rootView);
                    //myItemTouchListener.onPressDown(getAdapterPosition());
                    break;
                // break;

                case MotionEvent.ACTION_UP:
                    // Log.i("Adapter","onPressUp");
                    MyShop shop = (MyShop) mShopList.get(getAdapterPosition());
                    Intent intent = new Intent(context, ShopProductListActivity.class);
                    intent.putExtra("callingClass","ShopListActivity");
                    intent.putExtra("name",shop.getName());
                    intent.putExtra("address",shop.getAddress());
                    intent.putExtra("mobile", shop.getMobile());
                    intent.putExtra("stateCity",shop.getState()+", "+shop.getCity());
                    intent.putExtra("catId", catId);
                    intent.putExtra("subcatid",subcatid);
                    intent.putExtra("subcatname",subcatname);
                    intent.putExtra("dbname",shop.getDbname());
                    intent.putExtra("dbuser",shop.getDbusername());
                    intent.putExtra("dbpassword",shop.getDbpassword());
                    intent.putExtra("shop_code",shop.getId());
                    editor.putString(Constants.SHOP_INSIDE,shop.getId());
                    editor.putString(Constants.SHOP_INSIDE_NAME, shop.getName());
                    editor.commit();
                    context.startActivity(intent);
                    Toast.makeText(context, "shop dbname "+shop.getDbname() +" subcatname "+subcatname, Toast.LENGTH_SHORT).show();
                    zoomAnimation(false,rootView);
                    break;
                case MotionEvent.ACTION_CANCEL:
                    Log.i("Adapter","onPressCancel");
                    zoomAnimation(false,rootView);
                    break;
            }
            return true;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType){
            case 0:
                View v0 = inflater.inflate(R.layout.header_item_type_1_layout, parent, false);
                viewHolder = new MyShopHeaderViewHolder(v0);
                break;
            case 1:
                View v1 = inflater.inflate(R.layout.header_item_type_2_layout, parent, false);
                viewHolder = new MyShopTitleViewHolder(v1);
                break;
            case 2:
                View v2 = inflater.inflate(R.layout.shop_list_item_layout, parent, false);
                viewHolder = new MyShopListViewHolder(v2);
                break;
            default:
                View v = inflater.inflate(R.layout.list_item_layout, parent, false);
                viewHolder = new MyShopHeaderViewHolder(v);
                break;
        }
        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        if(type.equals("shopList")){
            Object item = mShopList.get(position);
            if(item instanceof MyHeader){
                MyHeader myItem = (MyHeader) item;
                if(myItem.getType() == 0){
                    return 0;
                }else{
                    return 1;
                }

            }else if(item instanceof MyShop){
                return 2;
            }else{
                return 3;
            }
        }else if(type.equals("countries")){
            return 2;
        } else{
            return 3;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if(holder instanceof MyShopHeaderViewHolder){

            MyHeader item = (MyHeader) mShopList.get(position);
            MyShopHeaderViewHolder myViewHolder = (MyShopHeaderViewHolder)holder;
            myViewHolder.textHeader.setText(item.getTitle());
            myViewHolder.textDesc.setText(item.getDesc());


        }else if(holder instanceof MyShopTitleViewHolder){

            MyHeader item = (MyHeader) mShopList.get(position);
            MyShopTitleViewHolder myViewHolder = (MyShopTitleViewHolder)holder;
            myViewHolder.textHeader.setText(item.getTitle());


        }else if(holder instanceof MyShopListViewHolder){

            MyShop item = (MyShop) mShopList.get(position);
            MyShopListViewHolder myViewHolder = (MyShopListViewHolder)holder;
            myViewHolder.textCustName.setText(item.getName());
            myViewHolder.text_shop_mobile.setText(item.getMobile());
            myViewHolder.textAddress.setText(item.getAddress());
            //myViewHolder.textStateCity.setText(item.getState()+", "+item.getCity());

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
            requestOptions.dontTransform();
            // requestOptions.override(Utility.dpToPx(150, context), Utility.dpToPx(150, context));
            // requestOptions.centerCrop();
            requestOptions.skipMemoryCache(true);

            Glide.with(context)
                    .load(item.getShopimage())
                    .apply(requestOptions)
                    .into(myViewHolder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        if(mShopList==null)
            return 0;
        else
            return mShopList.size();
    }

    private void zoomAnimation(boolean zoomOut,View view){
        if(zoomOut){
            Animation aniSlide = AnimationUtils.loadAnimation(context, R.anim.zoom_out);
            aniSlide.setFillAfter(true);
            view.startAnimation(aniSlide);
        }else{
            Animation aniSlide = AnimationUtils.loadAnimation(context,R.anim.zoom_in);
            aniSlide.setFillAfter(true);
            view.startAnimation(aniSlide);
        }
    }
}
