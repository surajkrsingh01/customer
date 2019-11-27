package com.shoppurs.adapters;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.shoppurs.R;
import com.shoppurs.activities.ProductDetailActivity;
import com.shoppurs.activities.ProductListActivity;
import com.shoppurs.activities.ShopListActivity;
import com.shoppurs.activities.SubCatListActivity;
import com.shoppurs.database.DbHelper;
import com.shoppurs.interfaces.MyItemTouchListener;
import com.shoppurs.models.CatListItem;
import com.shoppurs.models.Category;
import com.shoppurs.models.HomeListItem;
import com.shoppurs.models.MyHeader;
import com.shoppurs.models.MyItem;
import com.shoppurs.models.MyProduct;
import com.shoppurs.models.SubCategory;
import com.shoppurs.utilities.DialogAndToast;
import com.shoppurs.utilities.Utility;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<Object> itemList;
    private Context context;
    private String type;
    private String shopCode;
    private DbHelper dbHelper;
    private int colorTheme;

    private MyItemTouchListener myItemTouchListener;

    public void setMyItemTouchListener(MyItemTouchListener myItemTouchListener) {
        this.myItemTouchListener = myItemTouchListener;
    }

    private ConstraintSet constraintSet = new ConstraintSet();

    public void setShopCode(String shopCode){
        this.shopCode = shopCode;
    }

    public CategoryAdapter(Context context, List<Object> itemList, String type) {
        this.itemList = itemList;
        this.context=context;
        this.type = type;
        dbHelper = new DbHelper(context);
    }

    public void setColorTheme(int colorTheme){
        this.colorTheme = colorTheme;
    }

    public class MyHomeHeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView textHeader,textDesc;
        private RecyclerView recyclerView;

        public MyHomeHeaderViewHolder(View itemView) {
            super(itemView);
            textHeader=itemView.findViewById(R.id.text_date_range);
            //textHeader.setVisibility(View.GONE);
            //textDesc=itemView.findViewById(R.id.text_desc);
            recyclerView=itemView.findViewById(R.id.recycler_view);
            recyclerView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onClick(View view) {
        }
    }

    public class MyHomeHeader1ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView textHeader;
        private Button btnSeeAll;
        private RecyclerView recyclerView;

        public MyHomeHeader1ViewHolder(View itemView) {
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
                CatListItem myItem = (CatListItem) itemList.get(getAdapterPosition());
                List<Object>  obj =  myItem.getItemList();
                Intent intent = new Intent(context,SubCatListActivity.class);
                intent.putExtra("catName",myItem.getTitle());
                Log.d("catName called", myItem.getTitle());
                intent.putExtra("catId",((SubCategory)obj.get(0)).getId());
                context.startActivity(intent);
            }
        }
    }


    public class MyListType31ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnTouchListener {

        private TextView textTitle;
        private ImageView imageView;
       // private ConstraintLayout constraintLayout;
        private View rootView;
        private long downTime,upTime;

        public MyListType31ViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            textTitle=itemView.findViewById(R.id.text_title);
            imageView=itemView.findViewById(R.id.image_view);
            //constraintLayout=itemView.findViewById(R.id.parentContsraint);
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
                    downTime = new Date().getTime();
                    //myItemTouchListener.onPressDown(getAdapterPosition());
                    break;
                // break;

                case MotionEvent.ACTION_UP:
                    // Log.i("Adapter","onPressUp");
                    zoomAnimation(false,rootView);
                    upTime = new Date().getTime();
                    long diff = upTime - downTime;

                        Category myItem = (Category) itemList.get(getAdapterPosition());
                        Intent intent = new Intent(context,SubCatListActivity.class);
                        intent.putExtra("catName",myItem.getName());
                        intent.putExtra("catId", myItem.getId());
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

    public class MyListScanViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnTouchListener {

        private TextView textTitle;
        private ImageView imageView;
        // private ConstraintLayout constraintLayout;
        private View rootView;
        private TextView text_add_title;

        public MyListScanViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            textTitle=itemView.findViewById(R.id.text_title);
            imageView=itemView.findViewById(R.id.image_view);
            text_add_title =itemView.findViewById(R.id.text_add_title);
            //constraintLayout=itemView.findViewById(R.id.parentContsraint);
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
                    //myItemTouchListener.onPressDown(getAdapterPosition());
                    break;
                // break;

                case MotionEvent.ACTION_UP:
                    // Log.i("Adapter","onPressUp");
                    zoomAnimation(false,rootView);
                    SubCategory myItem = (SubCategory) itemList.get(getAdapterPosition());
                    Intent intent = new Intent(context,ShopListActivity.class);
                    intent.putExtra("subCatName",myItem.getName());
                    intent.putExtra("subCatId",myItem.getSubcatid());
                    intent.putExtra("CatId",myItem.getId());
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

    public class MySubHomeHeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView textHeader,textDesc, text_mobile;


        public MySubHomeHeaderViewHolder(View itemView) {
            super(itemView);
            textHeader=itemView.findViewById(R.id.text_date_range);
            textDesc=itemView.findViewById(R.id.text_desc);
            textHeader.setVisibility(View.GONE);
            textDesc.setVisibility(View.GONE);
            if(type.equals("productList")){
                text_mobile = itemView.findViewById(R.id.text_mobile);
                text_mobile.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onClick(View view) {
        }
    }

    public class MyProductListType1ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnTouchListener {

        private TextView textName,textMrp, textbarcode, tv_cartCount;
        private ImageView imageView, image_plus, image_minus;
        private View rootView;
        private Button btnAddCart;
        private LinearLayout linear_plus_minus;

        public MyProductListType1ViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            textbarcode = itemView.findViewById(R.id.text_bar_code);
            textName=itemView.findViewById(R.id.text_name);
            textMrp=itemView.findViewById(R.id.text_mrp);
            imageView=itemView.findViewById(R.id.image_view);
            btnAddCart = itemView.findViewById(R.id.btn_addCart);
            linear_plus_minus = itemView.findViewById(R.id.linear_plus_minus);
            image_plus = itemView.findViewById(R.id.image_plus);
            image_minus = itemView.findViewById(R.id.image_minus);
            tv_cartCount = itemView.findViewById(R.id.tv_cartCount);
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
                    MyProduct item = (MyProduct) itemList.get(getAdapterPosition());
                    //Intent intent = new Intent(context,CartActivity.class);
                    Intent intent = new Intent(context,ProductDetailActivity.class);
                    intent.putExtra("MyProduct", item);
                    Log.d("subcatname", item.getSubCatName());
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

    public class MyHomeHeader2ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private EditText etSearch;
        private TextView tvSubcat;
        private Button buttonSeAll;

        public MyHomeHeader2ViewHolder(View itemView) {
            super(itemView);
            buttonSeAll=itemView.findViewById(R.id.btn_see_all);
            tvSubcat = itemView.findViewById(R.id.tvSubcat);

            //buttonAdd.setText("Add Product");
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
            case 0: // categories list item
                View v0 = inflater.inflate(R.layout.product_list_header_item_layout, parent, false);
                viewHolder = new MyHomeHeaderViewHolder(v0);
                break;
            case 1: // categories header
                View v1 = inflater.inflate(R.layout.list_item_type_11_layout, parent, false);
                viewHolder = new MyListType31ViewHolder(v1);
                break;
            case 2: // categories header 1
                View v2 = inflater.inflate(R.layout.header_list_item_type_1_layout, parent, false);
                viewHolder = new MyHomeHeader1ViewHolder(v2);
                break;
            case 3: // categories list item
                View v3 = inflater.inflate(R.layout.list_item_type_3_layout, parent, false);
                viewHolder = new MyListType3ViewHolder(v3);
                break;
            case 4:
                View v4 = inflater.inflate(R.layout.list_item_scan_layout, parent, false);
                viewHolder = new MyListScanViewHolder(v4);
                break;

                case 5: // subcategories header
                View v5 = inflater.inflate(R.layout.home_list_header_item_layout, parent, false);
                viewHolder = new MySubHomeHeaderViewHolder(v5);
                break;
            case 6:   //subcategories list item
                View v6 = inflater.inflate(R.layout.list_item_type_3_layout, parent, false);
                viewHolder = new MyListType3ViewHolder(v6);
                break;

              //product list
            case 7:
                View v7 = inflater.inflate(R.layout.home_list_header_item_layout, parent, false);
                viewHolder = new MySubHomeHeaderViewHolder(v7);
                break;
            case 8:
                View v8 = inflater.inflate(R.layout.product_list_item_layout, parent, false);
                viewHolder = new MyProductListType1ViewHolder(v8);
                break;
            case 9:
                View v9 = inflater.inflate(R.layout.header_item_search_type_2_layout, parent, false);
                viewHolder = new MyHomeHeader2ViewHolder(v9);
                break;
            //product list
            default:
                View v = inflater.inflate(R.layout.list_item_layout, parent, false);
                viewHolder = new MyViewHolder(v);
                break;
        }

        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        if(type.equals("catList")){
            Object item = itemList.get(position);
            if(item instanceof CatListItem){
                CatListItem myItem = (CatListItem) item;
                if(myItem.getType() == 0){
                    return 0;
                }else{
                    return 2;
                }

            }else if(item instanceof Category){
                /*if(position == 0){
                    return 4;
                }else{*/
                    return 1;
                //}

            }else{
                return 3;
            }
        }else if(type.equals("subCatList")){
            Object item = itemList.get(position);
            if(position == 0){
                return 5;
            }else{
                return 6;
            }
        }else if(type.equals("productList")){
            Object item = itemList.get(position);
            if(position == 0){
                return 7;
            }else if(position == 1){
                return 9;
            }else{
                return 8;
            }
        } else{
            return 10;
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

            CatListItem item = (CatListItem) itemList.get(position);
            MyHomeHeaderViewHolder myViewHolder = (MyHomeHeaderViewHolder)holder;
            //myViewHolder.textHeader.setText(item.getTitle());
            //myViewHolder.textDesc.setText(item.getDesc());

           // Log.d("title ",item.getTitle());
            //Log.d("title ",item.getDesc());

            myViewHolder.recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false);
            myViewHolder.recyclerView.setLayoutManager(layoutManager);
            myViewHolder.recyclerView.setItemAnimator(new DefaultItemAnimator());
            CategoryAdapter myItemAdapter = new CategoryAdapter(context,item.getItemList(),"catList");
            myViewHolder.recyclerView.setAdapter(myItemAdapter);

            //StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams)myViewHolder.itemView.getLayoutParams();
            //layoutParams.setFullSpan(true);


        }else if(holder instanceof MyHomeHeader1ViewHolder){

            CatListItem item = (CatListItem) itemList.get(position);
            MyHomeHeader1ViewHolder myViewHolder = (MyHomeHeader1ViewHolder)holder;
            myViewHolder.textHeader.setText(item.getTitle());

            myViewHolder.recyclerView.setHasFixedSize(true);
            //RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false);
            StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            myViewHolder.recyclerView.setLayoutManager(staggeredGridLayoutManager);
            myViewHolder.recyclerView.setItemAnimator(new DefaultItemAnimator());
            CategoryAdapter myItemAdapter = new CategoryAdapter(context,item.getItemList(),"catList");
            myViewHolder.recyclerView.setAdapter(myItemAdapter);

          //  StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams)myViewHolder.itemView.getLayoutParams();
         //   layoutParams.setFullSpan(true);


        }else if(holder instanceof MyListType3ViewHolder){

            SubCategory item = (SubCategory) itemList.get(position);
            MyListType3ViewHolder myViewHolder = (MyListType3ViewHolder)holder;
            myViewHolder.textTitle.setText(item.getName());

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
            //requestOptions.dontTransform();
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

        }else if(holder instanceof MyListType31ViewHolder){
            Category item = (Category) itemList.get(position);
            MyListType31ViewHolder myViewHolder = (MyListType31ViewHolder)holder;
            myViewHolder.textTitle.setText(item.getName());

            Log.d("textTitle",item.getName() );

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
            //requestOptions.dontTransform();
            // requestOptions.override(Utility.dpToPx(150, context), Utility.dpToPx(150, context));
            // requestOptions.centerCrop();
            requestOptions.skipMemoryCache(false);

            Glide.with(context)
                    .load(item.getImage())
                    .apply(requestOptions)
                    .into(myViewHolder.imageView);
        }else if(holder instanceof MySubHomeHeaderViewHolder){

            HomeListItem item = (HomeListItem) itemList.get(position);
            MySubHomeHeaderViewHolder myViewHolder = (MySubHomeHeaderViewHolder)holder;
            myViewHolder.textHeader.setText(item.getTitle());
            myViewHolder.textDesc.setText(item.getDesc());
            if(type.equals("productList")){
                myViewHolder.text_mobile.setText(item.getMobile());
            }

            if(!type.equals("productList")){
                StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams)myViewHolder.itemView.getLayoutParams();
                layoutParams.setFullSpan(true);
            }


        }else if(holder instanceof MyProductListType1ViewHolder){
           final MyProduct item = (MyProduct) itemList.get(position);
            final MyProductListType1ViewHolder myViewHolder = (MyProductListType1ViewHolder)holder;

            myViewHolder.textbarcode.setText(item.getCode());
            myViewHolder.textName.setText(item.getName());
            myViewHolder.textMrp.setText("MRP: Rs"+item.getMrp());
            if(dbHelper.checkProdExistInCart(item.getId(), shopCode)){
                myViewHolder.btnAddCart.setVisibility(View.GONE);
                myViewHolder.linear_plus_minus.setVisibility(View.VISIBLE);
                myViewHolder.tv_cartCount.setText(String.valueOf(dbHelper.getProductQuantity(item.getCode(), shopCode, "normal")));
            }else {
                myViewHolder.tv_cartCount.setText(String.valueOf(1));
                myViewHolder.linear_plus_minus.setVisibility(View.GONE);
                myViewHolder.btnAddCart.setVisibility(View.VISIBLE);
            }

            myViewHolder.btnAddCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myViewHolder.linear_plus_minus.setVisibility(View.VISIBLE);
                    myViewHolder.btnAddCart.setVisibility(View.GONE);
                    ((ProductListActivity)context).add_toCart(item);
                    DialogAndToast.showToast("Add to Cart ", context);
                }
            });
            myViewHolder.image_minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   int count = Integer.parseInt(myViewHolder.tv_cartCount.getText().toString());
                   if(count==1){
                       myViewHolder.linear_plus_minus.setVisibility(View.GONE);
                       myViewHolder.btnAddCart.setVisibility(View.VISIBLE);
                       ((ProductListActivity)context).removeCart(item);
                   }else {
                       myViewHolder.tv_cartCount.setText(String.valueOf(count-1));
                       ((ProductListActivity)context).updateCart(item,count-1 );
                   }
                }
            });
            myViewHolder.image_plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int count = Integer.parseInt(myViewHolder.tv_cartCount.getText().toString());
                    myViewHolder.tv_cartCount.setText(String.valueOf(count+1));
                    ((ProductListActivity)context).updateCart(item,count+1 );
                }
            });

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
            // requestOptions.override(Utility.dpToPx(150, context), Utility.dpToPx(150, context));
            requestOptions.centerCrop();
            requestOptions.skipMemoryCache(false);

            Glide.with(context)
                    .load(item.getProdImage1())
                    .apply(requestOptions)
                    .into(myViewHolder.imageView);


        }else if(holder instanceof MyHomeHeader2ViewHolder){
            MyHeader item = (MyHeader) itemList.get(position);
            MyHomeHeader2ViewHolder myViewHolder = (MyHomeHeader2ViewHolder)holder;
            myViewHolder.tvSubcat.setText(item.getTitle());
            //myViewHolder.textHeader.setText(item.getTitle());
        }
    }



    @Override
    public int getItemCount() {
        if(itemList!=null)
        return itemList.size();
        else return 0;
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
