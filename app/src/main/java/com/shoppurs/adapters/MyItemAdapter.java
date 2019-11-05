package com.shoppurs.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.SharedPreferences;
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
import com.shoppurs.activities.BannerDetailsActivity;
import com.shoppurs.activities.CartShopListActivity;
import com.shoppurs.activities.MainActivity;
import com.shoppurs.activities.Settings.SettingActivity;
import com.shoppurs.activities.ShopAddressActivity;
import com.shoppurs.models.HomeListItem;
import com.shoppurs.models.MyItem;
import com.shoppurs.models.MyShop;
import com.shoppurs.models.ShopDeliveryModel;
import com.shoppurs.utilities.Constants;
import com.shoppurs.utilities.DialogAndToast;
import com.shoppurs.utilities.Utility;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class MyItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<Object> itemList;
    private Context context;
    private String type;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private ConstraintSet constraintSet = new ConstraintSet();
    public MyItemAdapter(Context context, List<Object> itemList,String type) {
        this.itemList = itemList;
        this.context=context;
        this.type = type;
        sharedPreferences= context.getSharedPreferences(Constants.MYPREFERENCEKEY,MODE_PRIVATE);
        editor=sharedPreferences.edit();
    }

    public class MyHomeHeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView textHeader,textDesc;
        private RecyclerView recyclerView;
        private ImageView profile_image;

        public MyHomeHeaderViewHolder(View itemView) {
            super(itemView);
            textHeader=itemView.findViewById(R.id.text_date_range);
            textDesc=itemView.findViewById(R.id.text_desc);
            recyclerView=itemView.findViewById(R.id.recycler_view);
            profile_image = itemView.findViewById(R.id.profile_image);
            profile_image.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(view == profile_image){
                Intent intent = new Intent(context, SettingActivity.class);
                context.startActivity(intent);
            }
        }
    }

    public class MyListType1ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnTouchListener {

        private TextView textTitle,textName;
        private ImageView imageView;
        private CircleImageView image;
        private View rootView;


        public MyListType1ViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            textTitle=itemView.findViewById(R.id.text_title);
            textName=itemView.findViewById(R.id.text_name);
            imageView=itemView.findViewById(R.id.image_view);
            //itemView.setOnClickListener(this);
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
                   // return true;
                 break;

                case MotionEvent.ACTION_UP:
                   // Log.i("Adapter","onPressUp");
                    HomeListItem item = (HomeListItem) itemList.get(getAdapterPosition());
                    Intent intent = new Intent(context, BannerDetailsActivity.class);
                    intent.putExtra("desc", item.getDesc());
                    context.startActivity(intent);
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

    public class MyListType2ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnTouchListener {

        private TextView textTitle,textDesc,textCat;
        private ImageView imageView;
        private View rootView;

        public MyListType2ViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            textTitle=itemView.findViewById(R.id.text_title);
            textDesc=itemView.findViewById(R.id.text_desc);
            textCat=itemView.findViewById(R.id.text_category);
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

    public class MyStoreListType1ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnTouchListener {

        private TextView textName,textAddress,textMobile;
        private ImageView imageView,imageMenu;
        private View rootView;

        public MyStoreListType1ViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            textName = itemView.findViewById(R.id.text_name);
            textAddress=itemView.findViewById(R.id.text_address);
            textMobile=itemView.findViewById(R.id.text_mobile);
            imageView=itemView.findViewById(R.id.image_view);
            imageMenu=itemView.findViewById(R.id.image_menu);
            rootView.setOnTouchListener(this);
            //imageMenu.setVisibility(View.GONE);
           imageMenu.setOnClickListener(this);
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(view == imageMenu){
                final MyShop shop = (MyShop) itemList.get(getAdapterPosition());
                PopupMenu popupMenu = new PopupMenu(view.getContext(), imageMenu);

                ((Activity)context).getMenuInflater().inflate(R.menu.shop_popup_menu, popupMenu.getMenu());

                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        //Toast.makeText(getBaseContext(), "You selected the action : " + item.getTitle()+" position "+position, Toast.LENGTH_SHORT).show();
                        if(item.getTitle().equals("Call")){
                            ((MainActivity)(context)).makeCall(shop.getMobile());
                            Log.i("Adapter","Call Customer"+shop.getName());
                        }else if(item.getTitle().equals("Message")){
                            ((MainActivity)(context)).openWhatsApp(shop.getMobile());
                            Log.i("Adapter","Message Customer"+shop.getName());
                        }else if (item.getTitle().equals("Location")) {
                            if(shop.getLatitude()!=0) {
                                Intent intent = new Intent(context, ShopAddressActivity.class);
                                intent.putExtra("address", shop.getAddress());
                                intent.putExtra("name", shop.getName());
                                intent.putExtra("mobile", shop.getMobile());
                                intent.putExtra("flag", "shopAddress");
                                intent.putExtra("lat", shop.getLatitude());
                                intent.putExtra("long", shop.getLongitude());
                                context.startActivity(intent);
                            }else {
                                DialogAndToast.showDialog("Location Not Available", context);
                            }
                        }
                        return true;
                    }
                });
            }else if(view ==imageView){
                final MyShop shop = (MyShop) itemList.get(getAdapterPosition());
                ((MainActivity)(context)).showLargeImageDialog(shop, imageView);
            }
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    zoomAnimation(true,rootView);
                    break;

                case MotionEvent.ACTION_UP:
                    MyShop shop = (MyShop) itemList.get(getAdapterPosition());
                     Intent   intent =new Intent(context, ApplyOffersActivity.class);
                    intent.putExtra("flag","mainOffers");
                    intent.putExtra("shopCode",shop.getCode());
                    Log.d("shopCode", shop.getCode());

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
                    editor.putString(Constants.SHOP_INSIDE_CODE,shop.getCode());
                    editor.putString(Constants.SHOP_INSIDE_NAME, shop.getName());
                    editor.putString(Constants.SHOP_DBNAME,shop.getDbname());
                    editor.putString(Constants.SHOP_DB_USER_NAME,shop.getDbusername());
                    editor.putString(Constants.SHOP_DB_PASSWORD,shop.getDbpassword());
                    editor.commit();
                    context.startActivity(intent);
                    zoomAnimation(false,rootView);
                    break;
                case MotionEvent.ACTION_CANCEL:
                    zoomAnimation(false,rootView);
                    break;
            }
            return true;
        }
    }

    public class MyListType3ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnTouchListener {

        private TextView textTitle;
        private ImageView imageView;
        private ConstraintLayout constraintLayout;
        private View rootView;

        public MyListType3ViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            textTitle=itemView.findViewById(R.id.text_title);
            imageView=itemView.findViewById(R.id.image_view);
            constraintLayout=itemView.findViewById(R.id.parentContsraint);
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
                    break;
                // break;

                case MotionEvent.ACTION_UP:
                    // Log.i("Adapter","onPressUp");
                    HomeListItem category = (HomeListItem) itemList.get(getAdapterPosition());
                    Intent intent = new Intent(context, CartShopListActivity.class);
                    //intent.putExtra("custCode",getIntent().getStringExtra("custCode"));
                    intent.putExtra("flag","mainOffers");
                    intent.putExtra("catId", category.getId());
                    context.startActivity(intent);
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


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView textHeader,textDesc;
        private CircleImageView image;


        public MyViewHolder(View itemView) {
            super(itemView);
            textHeader=itemView.findViewById(R.id.text_header);
            textDesc=itemView.findViewById(R.id.text_desc);
            image=itemView.findViewById(R.id.image_pic);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
        }
    }

    public class BannerListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private RecyclerView recyclerView;


        public BannerListViewHolder(View itemView) {
            super(itemView);
            recyclerView=itemView.findViewById(R.id.recycler_view);
        }

        @Override
        public void onClick(View view) {
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType){
            case 0:
                View v0 = inflater.inflate(R.layout.product_list_header_item_layout, parent, false);
                viewHolder = new MyHomeHeaderViewHolder(v0);
                break;
            case 1:
                View v1 = inflater.inflate(R.layout.list_item_type_1_layout, parent, false);
                viewHolder = new MyListType1ViewHolder(v1);
                break;
            case 2:
                View v2 = inflater.inflate(R.layout.list_item_type_2_layout, parent, false);
                viewHolder = new MyListType2ViewHolder(v2);
                break;
            case 3:
                View v3 = inflater.inflate(R.layout.list_item_type_3_layout, parent, false);
                viewHolder = new MyListType3ViewHolder(v3);
                break;
            case 4:
                View v4 = inflater.inflate(R.layout.list_item_type_4_layout, parent, false);
                viewHolder = new MyStoreListType1ViewHolder(v4);
                break;
            case 5:

                View v5 = inflater.inflate(R.layout.banner_horizontal_item_list, parent, false);
                viewHolder = new BannerListViewHolder(v5);
                break;
            default:
                View v = inflater.inflate(R.layout.list_item_layout, parent, false);
                viewHolder = new MyViewHolder(v);
                break;
        }
        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        if(type.equals("homeList")){
            if(itemList.get(position) instanceof  MyShop){
                return  4;
            }else {
                HomeListItem item = (HomeListItem)itemList.get(position);
                if(item.getType() == 0){
                    return 0;
                }else if(item.getType() == 1){
                    return 1;
                }else if(item.getType() == 2){
                    return 2;
                }else if(item.getType() == 3){
                    return 3;
                }else if(item.getType() == 4){
                    return 4;
                }else if(item.getType() == 5){
                    return 5;
                }
                else{
                    return 10;
                }
            }

        }else if(type.equals("countries")){
            return 2;
        } else{
            return 3;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
          if(holder instanceof MyViewHolder){
              MyItem item = (MyItem) itemList.get(position);
              MyViewHolder myViewHolder = (MyViewHolder)holder;
              myViewHolder.textHeader.setText(item.getHeader());
              myViewHolder.textDesc.setText(item.getDesc());
          }else if(holder instanceof MyHomeHeaderViewHolder){
              HomeListItem item = (HomeListItem) itemList.get(position);
              MyHomeHeaderViewHolder myViewHolder = (MyHomeHeaderViewHolder)holder;
              //myViewHolder.textHeader.setVisibility(View.VISIBLE);
              myViewHolder.textDesc.setVisibility(View.VISIBLE);
              myViewHolder.textHeader.setText(item.getTitle());
              myViewHolder.textDesc.setText(item.getDesc());

              StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams)myViewHolder.itemView.getLayoutParams();
              layoutParams.setFullSpan(true);

          }else if(holder instanceof MyListType1ViewHolder){
              HomeListItem item = (HomeListItem) itemList.get(position);
              MyListType1ViewHolder myViewHolder = (MyListType1ViewHolder)holder;

              RequestOptions requestOptions = new RequestOptions();
              requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
              requestOptions.override(Utility.dpToPx(300, context), Utility.dpToPx(350, context));
              requestOptions.centerCrop();
              requestOptions.skipMemoryCache(false);

              Glide.with(context)
                      .load(item.getImage())
                      .apply(requestOptions)
                      .into(myViewHolder.imageView);

              //StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams)myViewHolder.itemView.getLayoutParams();
              //layoutParams.setFullSpan(true);


          }else if(holder instanceof MyListType2ViewHolder){
              HomeListItem item = (HomeListItem) itemList.get(position);
              MyListType2ViewHolder myViewHolder = (MyListType2ViewHolder)holder;

              myViewHolder.textCat.setText(item.getCategory());
              myViewHolder.textTitle.setText(item.getTitle());
              myViewHolder.textDesc.setText(item.getDesc());

              RequestOptions requestOptions = new RequestOptions();
              requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
              // requestOptions.override(Utility.dpToPx(150, context), Utility.dpToPx(150, context));
              requestOptions.centerCrop();
              requestOptions.skipMemoryCache(false);

              Glide.with(context)
                      .load(item.getLocalImage())
                      .apply(requestOptions)
                      .into(myViewHolder.imageView);

              StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams)myViewHolder.itemView.getLayoutParams();
              layoutParams.setFullSpan(true);


          }else if(holder instanceof MyStoreListType1ViewHolder){
              MyShop item = (MyShop) itemList.get(position);
              MyStoreListType1ViewHolder myViewHolder = (MyStoreListType1ViewHolder)holder;
                  MyShop myShop = (MyShop) itemList.get(position);
                  myViewHolder.textName.setText(myShop.getName());
                  myViewHolder.textAddress.setText(myShop.getAddress());
                  myViewHolder.textMobile.setText(myShop.getMobile());
                  RequestOptions requestOptions = new RequestOptions();
                  requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
                  requestOptions.centerCrop();
                  requestOptions.skipMemoryCache(false);
                  Glide.with(context)
                          .load(myShop.getShopimage())
                          .apply(requestOptions)
                          .error(R.drawable.ic_photo_black_192dp)
                          .into(myViewHolder.imageView);

                  StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams)myViewHolder.itemView.getLayoutParams();
                  layoutParams.setFullSpan(true);
          }else if(holder instanceof MyListType3ViewHolder){
              HomeListItem item = (HomeListItem) itemList.get(position);
              MyListType3ViewHolder myViewHolder = (MyListType3ViewHolder)holder;
              myViewHolder.textTitle.setText(item.getTitle());

              RequestOptions requestOptions = new RequestOptions();
              requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
              requestOptions.dontTransform();
              // requestOptions.override(Utility.dpToPx(150, context), Utility.dpToPx(150, context));
             // requestOptions.centerCrop();
              requestOptions.skipMemoryCache(false);

              Glide.with(context)
                      .load(item.getImage())
                      .apply(requestOptions)
                      .into(myViewHolder.imageView);

              String ratio = String.format("%d:%d", (int)item.getWidth(),(int)item.getHeight());

              constraintSet.clone(myViewHolder.constraintLayout);
              constraintSet.setDimensionRatio(myViewHolder.imageView.getId(), ratio);
              constraintSet.applyTo(myViewHolder.constraintLayout);
          }else if(holder instanceof BannerListViewHolder){
              HomeListItem item = (HomeListItem) itemList.get(position);
              BannerListViewHolder myViewHolder = (BannerListViewHolder)holder;
              myViewHolder.recyclerView.setHasFixedSize(true);
              RecyclerView.LayoutManager layoutManagerHomeMenu=new LinearLayoutManager(context,
                      LinearLayoutManager.HORIZONTAL,false);
              myViewHolder.recyclerView.setLayoutManager(layoutManagerHomeMenu);
              myViewHolder.recyclerView.setItemAnimator(new DefaultItemAnimator());
              MyItemAdapter myItemAdapter=new MyItemAdapter(context,item.getItemList(),"homeList");
              myViewHolder.recyclerView.setAdapter(myItemAdapter);

              StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams)myViewHolder.itemView.getLayoutParams();
              layoutParams.setFullSpan(true);
          }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
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

}
