package com.shoppurs.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.shoppurs.R;
import com.shoppurs.activities.Settings.SettingActivity;
import com.shoppurs.activities.ShopAddressActivity;
import com.shoppurs.activities.ShopListActivity;
import com.shoppurs.activities.ShopProductListActivity;
import com.shoppurs.activities.ShoppursProductActivity;
import com.shoppurs.activities.StoresListActivity;
import com.shoppurs.activities.SubCatListActivity;
import com.shoppurs.models.CatListItem;
import com.shoppurs.models.Category;
import com.shoppurs.models.Market;
import com.shoppurs.models.MyItem;
import com.shoppurs.models.MyShop;
import com.shoppurs.models.ShopDeliveryModel;
import com.shoppurs.utilities.Constants;
import com.shoppurs.utilities.DialogAndToast;
import com.shoppurs.utilities.Utility;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class StoreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<Object> itemList;
    private Context context;
    private String type;
    private int colorTheme;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    public LinearLayoutManager layoutManager;

    public LinearLayoutManager getLayoutManager() {
        return layoutManager;
    }

    public void setLayoutManager(LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    public void setColorTheme(int colorTheme){
        this.colorTheme = colorTheme;
    }

    public StoreAdapter(Context context, List<Object> itemList, String type) {
        this.itemList = itemList;
        this.context=context;
        this.type = type;
        sharedPreferences= context.getSharedPreferences(Constants.MYPREFERENCEKEY,MODE_PRIVATE);
        editor=sharedPreferences.edit();
    }

    public class MyCategoryHeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView marketHeader, textHeader,textDesc;
        private RecyclerView recyclerView;
        private ImageView profile_image;

        public MyCategoryHeaderViewHolder(View itemView) {
            super(itemView);
            textHeader=itemView.findViewById(R.id.text_date_range);
            marketHeader = itemView.findViewById(R.id.text_header);
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


    public class MyStoreHeader1ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView textHeader;
        private Button btnSeeAll;
        private RecyclerView recyclerView;


        public MyStoreHeader1ViewHolder(View itemView) {
            super(itemView);
            textHeader=itemView.findViewById(R.id.text_title);
            btnSeeAll=itemView.findViewById(R.id.btn_see_all);
            Utility.setColorFilter(btnSeeAll.getBackground(), colorTheme);
            recyclerView=itemView.findViewById(R.id.recycler_view);
            btnSeeAll.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(view == btnSeeAll){
                /*CatListItem myItem = (CatListItem) itemList.get(getAdapterPosition());
                Intent intent = new Intent(context,SubCatListActivity.class);
                intent.putExtra("catName",myItem.getTitle());
                context.startActivity(intent);*/
            }
        }
    }


    public class MyCategoryAllViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnTouchListener {

        private TextView textTitle;
        private ImageView imageView;
        private View rootView;

        public MyCategoryAllViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            textTitle=itemView.findViewById(R.id.text_title);
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
                    zoomAnimation(true,rootView);
                    break;

                case MotionEvent.ACTION_UP:
                    zoomAnimation(false,rootView);
                    Category item = (Category) itemList.get(getAdapterPosition());
                    if(context instanceof StoresListActivity) {
                        Intent intent = new Intent(context, SubCatListActivity.class);
                        intent.putExtra("catName", item.getName());
                        Log.d("catName called", item.getName());
                        intent.putExtra("catId", item.getId());
                        context.startActivity(intent);
                    }else if(context instanceof ShoppursProductActivity){
                        ((ShoppursProductActivity)(context)).selectCategory(item);
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    zoomAnimation(false,rootView);
                    break;
            }
            return true;
        }
    }

    public class MyCategoryScanViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnTouchListener {

        private TextView textTitle;
        private ImageView imageView;
        private View rootView;

        public MyCategoryScanViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            textTitle=itemView.findViewById(R.id.text_title);
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
                    zoomAnimation(true,rootView);
                    break;

                case MotionEvent.ACTION_UP:
                    zoomAnimation(false,rootView);
                    if(context instanceof StoresListActivity)
                    ((StoresListActivity)(context)).scanBarCode();
                    else ((ShoppursProductActivity)(context)).scanBarCode();
                    break;
                case MotionEvent.ACTION_CANCEL:
                    zoomAnimation(false,rootView);
                    break;
            }
            return true;
        }
    }

    public class MyMarketViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnTouchListener {

        private TextView textTitle;
        private ImageView imageView;
        private View rootView;

        public MyMarketViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            textTitle=itemView.findViewById(R.id.text_title);
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
                    zoomAnimation(true,rootView);
                    break;

                case MotionEvent.ACTION_UP:
                    zoomAnimation(false,rootView);
                    Market item = (Market) itemList.get(getAdapterPosition());
                    //DialogAndToast.showToast(item.getName(), context);

                    Intent intent = new Intent(context, ShopListActivity.class);
                    intent.putExtra("flag", "MarketStore");
                    intent.putExtra("marketId",item.getId());
                    context.startActivity(intent);

                    break;
                case MotionEvent.ACTION_CANCEL:
                    zoomAnimation(false,rootView);
                    break;
            }
            return true;
        }
    }


    public class MyStoreListType1ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnTouchListener {

        private TextView textName,tv_shortName, textAddress,textMobile;
        private ImageView imageView,imageMenu;
        private View rootView;

        public MyStoreListType1ViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            textName = itemView.findViewById(R.id.text_name);
            tv_shortName = itemView.findViewById(R.id.tv_shortName);
            textAddress=itemView.findViewById(R.id.text_address);
            textMobile=itemView.findViewById(R.id.text_mobile);
            imageView=itemView.findViewById(R.id.image_view);
            imageMenu=itemView.findViewById(R.id.image_menu);
            rootView.setOnTouchListener(this);
            imageMenu.setOnClickListener(this);
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(view == imageView){
                final MyShop shop = (MyShop) itemList.get(getAdapterPosition());
                ((StoresListActivity)context).showLargeImageDialog(shop, imageView);
            }else if(view == imageMenu){
                final MyShop shop = (MyShop) itemList.get(getAdapterPosition());
                PopupMenu popupMenu = new PopupMenu(view.getContext(), imageMenu);

                ((Activity)context).getMenuInflater().inflate(R.menu.shop_popup_menu, popupMenu.getMenu());

                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        //Toast.makeText(getBaseContext(), "You selected the action : " + item.getTitle()+" position "+position, Toast.LENGTH_SHORT).show();
                        if(item.getTitle().equals("Call")){
                            ((StoresListActivity)(context)).makeCall(shop.getMobile());
                            Log.i("Adapter","Call Customer"+shop.getName());
                        }else if(item.getTitle().equals("Message")){
                            ((StoresListActivity)(context)).openWhatsApp(shop.getMobile());
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
                               /* Intent intent = new Intent(context, ShopAddressActivity.class);
                                intent.putExtra("flag", "shopAddress");
                                intent.putExtra("lat", 30.651100);
                                intent.putExtra("long", 76.821000);
                                context.startActivity(intent);*/
                                DialogAndToast.showDialog("Location Not Available", context);
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
                    zoomAnimation(true,rootView);
                    break;

                case MotionEvent.ACTION_UP:
                    MyShop shop = (MyShop) itemList.get(getAdapterPosition());
                   // DialogAndToast.showDialog("item Name "+item.getName(), context );
                    Intent intent = new Intent(context, ShopProductListActivity.class);
                    intent.putExtra("callingClass","SearchShopListActivity");
                    intent.putExtra("name",shop.getName());
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

                    editor.putString(Constants.SHOP_INSIDE_CODE,shop.getId());
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

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType){

            case 0:  //categories header
                View v0 = inflater.inflate(R.layout.store_list_header_item_layout, parent, false);
                viewHolder = new MyCategoryHeaderViewHolder(v0);
                break;
            case 2:  //product header
                View v2 = inflater.inflate(R.layout.header_list_item_type_1_layout, parent, false);
                viewHolder = new MyStoreHeader1ViewHolder(v2);
                break;


            case 4:
                View v4 = inflater.inflate(R.layout.list_store_scan_layout, parent, false);
                viewHolder = new MyCategoryScanViewHolder(v4);
                break;
            case 1:
               // View v1 = inflater.inflate(R.layout.list_item_type_3_1_layout, parent, false);
                View v1 = inflater.inflate(R.layout.list_item_type_11_layout, parent, false);
                viewHolder = new MyCategoryAllViewHolder(v1);
                break;

            case 8:
                View v8 = inflater.inflate(R.layout.list_item_type_4_layout, parent, false);
                viewHolder = new MyStoreListType1ViewHolder(v8);
                break;

            case 3:
                // View v1 = inflater.inflate(R.layout.list_item_type_3_1_layout, parent, false);
                View v3 = inflater.inflate(R.layout.list_item_market_layout, parent, false);
                viewHolder = new MyMarketViewHolder(v3);
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
            Object item = itemList.get(position);
            if(item instanceof CatListItem){ //used for headers
                CatListItem myItem = (CatListItem) item;
                if(myItem.getType() == 0){
                    return 0;
                }else{
                    return 2;
                }

            }else if(item instanceof Category){ //used for top categories
                Log.d("type ", "Category");
                if(position == 0){
                    return 4;
                }else{
                    return 1;
                }
            }else if(item instanceof Market){
                return 3;
            }else if(item instanceof MyShop){ //used for products
                Log.d("type ", "myShop");
                return 8;
            }else {
                return 8;   //if(item instanceof SubCategory)
            }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MyViewHolder){ //not used
            MyItem item = (MyItem) itemList.get(position);
            MyViewHolder myViewHolder = (MyViewHolder)holder;
            myViewHolder.textHeader.setText(item.getHeader());
            myViewHolder.textDesc.setText(item.getDesc());
        }else if(holder instanceof MyCategoryHeaderViewHolder){

            CatListItem item = (CatListItem) itemList.get(position);
            MyCategoryHeaderViewHolder myViewHolder = (MyCategoryHeaderViewHolder)holder;
            if(item.getItemList().size()>0){
                if(item.getItemList().get(0)instanceof Market) {
                    myViewHolder.marketHeader.setVisibility(View.VISIBLE);
                    myViewHolder.marketHeader.setText(item.getDesc());
                }else {
                    myViewHolder.marketHeader.setVisibility(View.GONE);
                }
            }


            myViewHolder.recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false);
            myViewHolder.recyclerView.setLayoutManager(layoutManager);
            myViewHolder.recyclerView.setItemAnimator(new DefaultItemAnimator());
            StoreAdapter myItemAdapter = new StoreAdapter(context,item.getItemList(),"catList");
            myViewHolder.recyclerView.setAdapter(myItemAdapter);

        }else if(holder instanceof MyStoreHeader1ViewHolder){

            CatListItem item = (CatListItem) itemList.get(position);
            MyStoreHeader1ViewHolder myViewHolder = (MyStoreHeader1ViewHolder)holder;
            myViewHolder.textHeader.setText(item.getTitle());
            myViewHolder.recyclerView.setHasFixedSize(true);
           // myViewHolder.recyclerView.setNestedScrollingEnabled(false);
            //if(item.getItemList().get(0) instanceof  MyShop) { //call catList section type 8 layout
                Log.d("itemType 8",""+item.getType());
               LinearLayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
                myViewHolder.recyclerView.setLayoutManager(layoutManager);
                //setLayoutManager(layoutManager);
            //}
            myViewHolder.recyclerView.setItemAnimator(new DefaultItemAnimator());
            StoreAdapter myItemAdapter = new StoreAdapter(context,item.getItemList(),"catList");
            myViewHolder.recyclerView.setAdapter(myItemAdapter);

        }else if(holder instanceof MyCategoryScanViewHolder){
            Category item = (Category) itemList.get(position);
            MyCategoryScanViewHolder myViewHolder = (MyCategoryScanViewHolder)holder;
            myViewHolder.textTitle.setText(item.getName());
        }
        else if(holder instanceof MyCategoryAllViewHolder){
            Category item = (Category) itemList.get(position);
            MyCategoryAllViewHolder myViewHolder = (MyCategoryAllViewHolder)holder;
            myViewHolder.textTitle.setText(item.getName());
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
            requestOptions.skipMemoryCache(false);
            Glide.with(context)
                    .load(item.getImage())
                    .apply(requestOptions)
                    .into(myViewHolder.imageView);
        }else if(holder instanceof MyMarketViewHolder){
            Market item = (Market) itemList.get(position);
            MyMarketViewHolder myViewHolder = (MyMarketViewHolder)holder;
            myViewHolder.textTitle.setText(item.getName());
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
            requestOptions.skipMemoryCache(false);
            Glide.with(context)
                    .load(item.getImage())
                    .apply(requestOptions)
                    .into(myViewHolder.imageView);
        }else if(holder instanceof MyStoreListType1ViewHolder){
            MyShop item = (MyShop) itemList.get(position);
            MyStoreListType1ViewHolder myViewHolder = (MyStoreListType1ViewHolder)holder;
            if(itemList.get(position) instanceof CatListItem) {
                CatListItem mcatitem = (CatListItem) itemList.get(position);
                if (mcatitem.getTitle() != null && mcatitem.getTitle().equals("My Favourite Stores")){
                    Log.d("Title", mcatitem.getTitle());
                   // myViewHolder.image_icon.setVisibility(View.GONE);
                }//else myViewHolder.image_icon.setVisibility(View.VISIBLE);
            }
            myViewHolder.textName.setText(item.getName());
            myViewHolder.textAddress.setText(item.getAddress());
            myViewHolder.textMobile.setText(item.getMobile());

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
           // Log.d("shopImage ", item.getShopimage());
            //Log.d("shopName ", item.getName());
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
