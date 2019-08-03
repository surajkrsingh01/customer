package com.shoppurscustomer.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

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
import com.shoppurscustomer.R;
import com.shoppurscustomer.activities.Settings.SettingActivity;
import com.shoppurscustomer.activities.ShopAddressActivity;
import com.shoppurscustomer.activities.ShopListActivity;
import com.shoppurscustomer.activities.ShopProductListActivity;
import com.shoppurscustomer.activities.StoresListActivity;
import com.shoppurscustomer.activities.SubCatListActivity;
import com.shoppurscustomer.interfaces.MyItemTouchListener;
import com.shoppurscustomer.models.CatListItem;
import com.shoppurscustomer.models.Category;
import com.shoppurscustomer.models.HomeListItem;
import com.shoppurscustomer.models.MyHeader;
import com.shoppurscustomer.models.MyItem;
import com.shoppurscustomer.models.MyProduct;
import com.shoppurscustomer.models.MyShop;
import com.shoppurscustomer.models.SubCategory;
import com.shoppurscustomer.utilities.Constants;
import com.shoppurscustomer.utilities.DialogAndToast;
import com.shoppurscustomer.utilities.Utility;

import java.util.Date;
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

        private TextView textHeader,textDesc;
        private RecyclerView recyclerView;
        private ImageView profile_image;

        public MyCategoryHeaderViewHolder(View itemView) {
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
                    //DialogAndToast.showDialog("item Name "+item.getName(), context );
                    Intent intent = new Intent(context,SubCatListActivity.class);
                    intent.putExtra("catName",item.getName());
                    Log.d("catName called", item.getName());
                    intent.putExtra("catId",item.getId());
                    context.startActivity(intent);
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
                    ((StoresListActivity)(context)).scanBarCode();
                    break;
                case MotionEvent.ACTION_CANCEL:
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
            imageMenu.setOnClickListener(this);
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
                            ((StoresListActivity)(context)).makeCall(shop.getMobile());
                            Log.i("Adapter","Call Customer"+shop.getName());
                        }else if(item.getTitle().equals("Message")){
                            ((StoresListActivity)(context)).openWhatsApp(shop.getMobile());
                            Log.i("Adapter","Message Customer"+shop.getName());
                        }else if (item.getTitle().equals("Location")) {
                            if(shop.getLatitude()!=0) {
                                Intent intent = new Intent(context, ShopAddressActivity.class);
                                intent.putExtra("flag", "shopAddress");
                                intent.putExtra("lat", shop.getLatitude());
                                intent.putExtra("long", shop.getLongitude());
                                context.startActivity(intent);
                            }else {
                                Intent intent = new Intent(context, ShopAddressActivity.class);
                                intent.putExtra("flag", "shopAddress");
                                intent.putExtra("lat", 30.651100);
                                intent.putExtra("long", 76.821000);
                                context.startActivity(intent);
                                //DialogAndToast.showDialog("Location Not Available", context);
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
                    //intent.putExtra("catId", catId);
                    //intent.putExtra("subcatid",subcatid);
                    // intent.putExtra("subcatname",subcatname);
                    intent.putExtra("dbname",shop.getDbname());
                    intent.putExtra("dbuser",shop.getDbusername());
                    intent.putExtra("dbpassword",shop.getDbpassword());
                    intent.putExtra("shop_code",shop.getId());
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
            myViewHolder.textHeader.setText(item.getTitle());
            myViewHolder.textDesc.setText(item.getDesc());

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
            if(item.getItemList().get(0) instanceof  MyShop) { //call catList section type 8 layout
                Log.d("itemType 8",""+item.getType());
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
                myViewHolder.recyclerView.setLayoutManager(layoutManager);
            }
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
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
            requestOptions.centerCrop();
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
