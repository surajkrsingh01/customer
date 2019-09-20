package com.shoppurs.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.shoppurs.R;
import com.shoppurs.activities.SearchActivity;
import com.shoppurs.activities.ShopAddressActivity;
import com.shoppurs.activities.ShopListActivity;
import com.shoppurs.activities.ShopProductListActivity;
import com.shoppurs.models.MyHeader;
import com.shoppurs.models.MyShop;
import com.shoppurs.models.ShopDeliveryModel;
import com.shoppurs.utilities.Constants;
import com.shoppurs.utilities.DialogAndToast;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by suraj kumar singh on 09-03-2019.
 */

public class ShopAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;
    private List<Object> mShopList;
    private String type;
    private String catId, subcatid, subcatname, flag;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private int counter;


    public ShopAdapter(Context context, List<Object> mShopList, String type,String catId, String subcatid, String subcatname){
        this.context = context;
        this.mShopList = mShopList;
        this.type = type;
        this.catId = catId;
        this.subcatid = subcatid;
        this.subcatname = subcatname;
        sharedPreferences= context.getSharedPreferences(Constants.MYPREFERENCEKEY,MODE_PRIVATE);
        editor=sharedPreferences.edit();
        Log.d("subcatid", subcatid);
        Log.d("subcatname", subcatname);
    }

    public void setFlag(String flag){
        this.flag = flag;
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
        private TextView textShopName,text_shop_mobile, textAddress;
        private ImageView imageView, imageMenu;
        private View rootView;

        public MyShopListViewHolder(View itemView){
            super(itemView);
            rootView = itemView;
            textShopName=itemView.findViewById(R.id.text_name);
            text_shop_mobile = itemView.findViewById(R.id.text_mobile);
            textAddress=itemView.findViewById(R.id.text_address);
            imageView=itemView.findViewById(R.id.image_view);
            imageMenu=itemView.findViewById(R.id.image_menu);
            imageMenu.setOnClickListener(this);
            rootView.setOnTouchListener(this);
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(view == imageView){
                final MyShop shop = (MyShop) mShopList.get(getAdapterPosition());
                if(flag!=null && flag.equals("SearchActivity"))
                    ((SearchActivity)context).showLargeImageDialog(shop, imageView);
                else
               ((ShopListActivity)context).showLargeImageDialog(shop, imageView);
            }else if(view == imageMenu){
                final MyShop shop = (MyShop) mShopList.get(getAdapterPosition());
                PopupMenu popupMenu = new PopupMenu(view.getContext(), imageMenu);

                ((Activity)context).getMenuInflater().inflate(R.menu.shop_popup_menu, popupMenu.getMenu());

                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        //Toast.makeText(getBaseContext(), "You selected the action : " + item.getTitle()+" position "+position, Toast.LENGTH_SHORT).show();
                        if(flag!=null && flag.equals("SearchActivity")){
                            if (item.getTitle().equals("Call")) {
                                ((SearchActivity) (context)).makeCall(shop.getMobile());
                                Log.i("Adapter", "Call Customer" + shop.getName());
                            } else if (item.getTitle().equals("Message")) {
                                ((SearchActivity) (context)).openWhatsApp(shop.getMobile());
                                Log.i("Adapter", "Message Customer" + shop.getName());
                            }else if (item.getTitle().equals("Location")) {
                                if(shop.getLatitude()!=0) {
                                    Intent intent = new Intent(context, ShopAddressActivity.class);
                                    intent.putExtra("flag", "shopAddress");
                                    intent.putExtra("lat", shop.getLatitude());
                                    intent.putExtra("long", shop.getLongitude());
                                    context.startActivity(intent);
                                }else {
                                    DialogAndToast.showDialog("Location Not Available", context);
                                }
                            }
                        }else {
                            if (item.getTitle().equals("Call")) {
                                ((ShopListActivity) (context)).makeCall(shop.getMobile());
                                Log.i("Adapter", "Call Customer" + shop.getName());
                            } else if (item.getTitle().equals("Message")) {
                                ((ShopListActivity) (context)).openWhatsApp(shop.getMobile());
                                Log.i("Adapter", "Message Customer" + shop.getName());
                            }else if (item.getTitle().equals("Location")) {
                                if(shop.getLatitude()!=0) {
                                    Intent intent = new Intent(context, ShopAddressActivity.class);
                                    intent.putExtra("flag", "shopAddress");
                                    intent.putExtra("lat", shop.getLatitude());
                                    intent.putExtra("long", shop.getLongitude());
                                    context.startActivity(intent);
                                }else {
                                    DialogAndToast.showDialog("Location Not Available", context);
                                }
                            }
                        }
                        return true;
                    }
                });

            }
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
                    intent.putExtra("photo",shop.getShopimage());
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

                    ShopDeliveryModel shopDeliveryModel = new ShopDeliveryModel();
                    shopDeliveryModel.setShopCode(shop.getId());
                    shopDeliveryModel.setRetLat(shop.getLatitude());
                    shopDeliveryModel.setRetLong(shop.getLongitude());
                    shopDeliveryModel.setIsDeliveryAvailable(shop.getIsDeliveryAvailable());
                    shopDeliveryModel.setMinDeliveryAmount(shop.getMinDeliveryAmount());
                    shopDeliveryModel.setMinDeliverytime(shop.getMinDeliverytime());
                    shopDeliveryModel.setMinDeliverydistance(shop.getMinDeliverydistance());
                    shopDeliveryModel.setChargesAfterMinDistance(shop.getChargesAfterMinDistance());
                    intent.putExtra("shopDeliveryModel", shopDeliveryModel);

                    editor.putString(Constants.SHOP_DBNAME,shop.getDbname());
                    editor.putString(Constants.SHOP_DB_USER_NAME,shop.getDbusername());
                    editor.putString(Constants.SHOP_DB_PASSWORD,shop.getDbpassword());
                    editor.putString(Constants.SHOP_INSIDE_CODE,shop.getId());
                    editor.putString(Constants.SHOP_INSIDE_NAME, shop.getName());
                    editor.commit();
                    context.startActivity(intent);
                    //Toast.makeText(context, "shop dbname "+shop.getDbname() +" subcatname "+subcatname, Toast.LENGTH_SHORT).show();
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
                View v2 = inflater.inflate(R.layout.list_item_type_4_layout, parent, false);
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
                myViewHolder.textShopName.setText(item.getName());
                myViewHolder.text_shop_mobile.setText(item.getMobile());
                if(TextUtils.isEmpty(item.getAddress()) || item.getAddress().equals("null")){
                    myViewHolder.textAddress.setVisibility(View.GONE);
                }else{
                    myViewHolder.textAddress.setVisibility(View.VISIBLE);
                    myViewHolder.textAddress.setText(item.getAddress());
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
                        .error(R.drawable.ic_photo_black_192dp)
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

    private int getTvColor(int position){
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
