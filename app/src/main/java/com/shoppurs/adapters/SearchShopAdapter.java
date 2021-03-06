package com.shoppurs.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

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
import com.shoppurs.activities.ApplyOffersActivity;
import com.shoppurs.activities.CartShopListActivity;
import com.shoppurs.activities.CouponOffersActivity;
import com.shoppurs.activities.ShopListActivity;
import com.shoppurs.activities.ShopProductListActivity;
import com.shoppurs.interfaces.MyItemTypeClickListener;
import com.shoppurs.models.MyShop;
import com.shoppurs.models.ShopDeliveryModel;
import com.shoppurs.utilities.Constants;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by suraj kumar singh on 18-04-2019.
 */

public class SearchShopAdapter extends RecyclerView.Adapter<SearchShopAdapter.MyViewHolder> {
    private Context context;
    private List<MyShop> mShopList;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String subCatid, subCatName, flag;
    private int counter, colorTheme;
    MyItemTypeClickListener myItemClickListener;

    public void setFlag(String flag){
        this.flag = flag;
    }

    public void setColorTheme(int colorTheme){
        this.colorTheme = colorTheme;
    }

    public String getSubCatid() {
        return subCatid;
    }

    public void setSubCatid(String subCatid) {
        this.subCatid = subCatid;
    }

    public String getSubCatName() {
        return subCatName;
    }

    public void setSubCatName(String subCatName) {
        this.subCatName = subCatName;
    }

    public void setMyItemClickListener(MyItemTypeClickListener myItemClickListener) {
        this.myItemClickListener = myItemClickListener;
    }


    public SearchShopAdapter(Context context, List<MyShop> myShops) {
        this.context = context;
        this.mShopList = myShops;
        sharedPreferences= context.getSharedPreferences(Constants.MYPREFERENCEKEY,MODE_PRIVATE);
        editor=sharedPreferences.edit();
    }

    @Override
    public SearchShopAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.shop_list_item_layout, parent, false);
        return new SearchShopAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SearchShopAdapter.MyViewHolder holder, final int position) {
        {
            Log.d("SearchhShop", ""+mShopList.size());

            MyShop item = (MyShop) mShopList.get(position);
            SearchShopAdapter.MyViewHolder myViewHolder = (SearchShopAdapter.MyViewHolder)holder;
            myViewHolder.textShopName.setText(item.getName());
            myViewHolder.text_shop_mobile.setText(item.getMobile());
            if(TextUtils.isEmpty(item.getAddress()) || item.getAddress().equals("null")){
                myViewHolder.textAddress.setVisibility(View.GONE);
            }else{
                myViewHolder.textAddress.setVisibility(View.VISIBLE);
                myViewHolder.textAddress.setText(item.getAddress());
            }

           /* if(TextUtils.isEmpty(item.getState()) || item.getState().equals("null")){
                myViewHolder.textStateCity.setVisibility(View.GONE);
            }else{
                myViewHolder.textStateCity.setVisibility(View.VISIBLE);
                myViewHolder.textStateCity.setText(item.getState()+", "+item.getCity());
            }*/

            if(item.getName().length()>1) {
                myViewHolder.tv_shortName.setText(item.getName().substring(0, 1));
                //image_view_shop .setText(shopName);
                String initials = "";
                if (item.getName().contains(" ")) {
                    String[] nameArray = item.getName().split(" ");
                    initials = nameArray[0].substring(0, 1) + nameArray[1].substring(0, 1);
                } else {
                    initials = item.getName().substring(0, 2);
                }

                myViewHolder.tv_shortName.setText(initials);
            }
            Log.d("shopImage ", item.getShopimage());
            Log.d("shopName ", item.getName());
            if(item.getShopimage() !=null && item.getShopimage().contains("http")){
                myViewHolder.tv_shortName.setVisibility(View.GONE);
                myViewHolder.imageView.setVisibility(View.VISIBLE);
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
            }else{
                myViewHolder.tv_shortName.setVisibility(View.VISIBLE);
                myViewHolder.imageView.setVisibility(View.GONE);
            }


        }
    }

    @Override
    public int getItemCount() {
        if (mShopList != null) {
            return mShopList.size();
        }
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnTouchListener{
        private TextView tv_shortName, textShopName,text_shop_mobile, textAddress, textStateCity;
        private ImageView imageView, imageMenu;
        private View rootView;


        public MyViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            rootView = itemView;
            tv_shortName=itemView.findViewById(R.id.tv_shortName);
            textShopName=itemView.findViewById(R.id.text_name);
            text_shop_mobile = itemView.findViewById(R.id.text_mobile);
            textAddress=itemView.findViewById(R.id.text_address);
            textStateCity=itemView.findViewById(R.id.text_state_city);
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
                if(flag!=null && flag.equals("CartShopOffers")  || flag!=null && flag.equals("CartShopCoupons") || flag!=null && flag.equals("mainOffers")){
                    ((CartShopListActivity)context).showLargeImageDialog(shop, imageView);
                }else{
                    ((ShopListActivity)context).showLargeImageDialog(shop, imageView);
                }

            }else if(view == imageMenu){
                final MyShop shop = (MyShop) mShopList.get(getAdapterPosition());
                PopupMenu popupMenu = new PopupMenu(view.getContext(), imageMenu);

                ((Activity)context).getMenuInflater().inflate(R.menu.shop_popup_menu, popupMenu.getMenu());

                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        //Toast.makeText(getBaseContext(), "You selected the action : " + item.getTitle()+" position "+position, Toast.LENGTH_SHORT).show();
                        if(item.getTitle().equals("Call")){
                           /* if(shop.getIsFav().equals("Y")){
                                myItemClickListener.onItemClicked(getAdapterPosition(),1);
                            }else{
                                myItemClickListener.onItemClicked(getAdapterPosition(),3);
                            }*/
                            Log.i("Adapter","Call Customer"+shop.getName());
                        }else if(item.getTitle().equals("Message")){
                            /*if(customer.getIsFav().equals("Y")){
                                myItemClickListener.onItemClicked(getAdapterPosition(),2);
                            }else{
                                myItemClickListener.onItemClicked(getAdapterPosition(),4);
                            }*/
                            Log.i("Adapter","Message Customer"+shop.getName());
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
                    if(flag!=null && flag.equals("CartShopOffers") || flag!=null && flag.equals("CartShopCoupons") || flag!=null && flag.equals("mainOffers")){
                        MyShop shop = (MyShop) mShopList.get(getAdapterPosition());
                        Intent intent = new Intent();
                        if(flag.equals("CartShopOffers") || flag.equals("mainOffers"))
                            intent =new Intent(context, ApplyOffersActivity.class);
                            else
                            intent =new Intent(context, CouponOffersActivity.class);
                        intent.putExtra("flag",flag);
                        intent.putExtra("shopCode",shop.getCode());
                        Log.d("shopCode", shop.getCode());
                        intent.putExtra("photo",shop.getShopimage());
                        intent.putExtra("address",shop.getAddress());
                        intent.putExtra("mobile", shop.getMobile());
                        intent.putExtra("stateCity",shop.getState()+", "+shop.getCity());
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
                        editor.putString(Constants.SHOP_INSIDE_CODE,shop.getCode());
                        editor.putString(Constants.SHOP_INSIDE_NAME, shop.getName());
                        editor.putString(Constants.SHOP_DBNAME,shop.getDbname());
                        editor.putString(Constants.SHOP_DB_USER_NAME,shop.getDbusername());
                        editor.putString(Constants.SHOP_DB_PASSWORD,shop.getDbpassword());
                        editor.commit();
                        context.startActivity(intent);
                    }else if(flag!=null && flag.equals("shopInfoActivity")){
                        myItemClickListener.onItemClicked(getAdapterPosition(), 2);
                    }else {
                        MyShop shop = (MyShop) mShopList.get(getAdapterPosition());
                        Intent intent = new Intent(context, ShopProductListActivity.class);
                        intent.putExtra("callingClass","SearchShopListActivity");
                        intent.putExtra("name",shop.getName());
                        intent.putExtra("photo",shop.getShopimage());
                        intent.putExtra("address",shop.getAddress());
                        intent.putExtra("mobile", shop.getMobile());
                        intent.putExtra("stateCity",shop.getState()+", "+shop.getCity());
                        //intent.putExtra("catId", catId);
                        //intent.putExtra("subcatid",subcatid);
                        // intent.putExtra("subcatname",subcatname);
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
                        editor.putString(Constants.SHOP_INSIDE_CODE,shop.getCode());
                        editor.putString(Constants.SHOP_INSIDE_NAME, shop.getName());
                        editor.putString(Constants.SHOP_DBNAME,shop.getDbname());
                        editor.putString(Constants.SHOP_DB_USER_NAME,shop.getDbusername());
                        editor.putString(Constants.SHOP_DB_PASSWORD,shop.getDbpassword());
                        editor.commit();
                        context.startActivity(intent);
                    }

              //      Toast.makeText(context, "shop dbname "+shop.getDbname() +" subcatname "+subcatname, Toast.LENGTH_SHORT).show();
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
