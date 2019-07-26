package com.shoppurscustomer.adapters;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.constraintlayout.widget.ConstraintSet;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.shoppurscustomer.R;
import com.shoppurscustomer.activities.Settings.SettingActivity;
import com.shoppurscustomer.models.HomeListItem;
import com.shoppurscustomer.models.MyItem;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<Object> itemList;
    private Context context;
    private String type;

    private ConstraintSet constraintSet = new ConstraintSet();
    public MyItemAdapter(Context context, List<Object> itemList,String type) {
        this.itemList = itemList;
        this.context=context;
        this.type = type;
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

    public class MyListType5ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnTouchListener {

        private TextView textTitle;
        private ImageView imageView;
        // private ConstraintLayout constraintLayout;
        private View rootView;
        private long downTime,upTime;

        public MyListType5ViewHolder(View itemView) {
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
                    if(getAdapterPosition() == 0){

                    }else{

                    }
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
                viewHolder = new MyListType2ViewHolder(v4);
                break;
            case 5:
                View v5 = inflater.inflate(R.layout.list_item_type_3_1_layout, parent, false);
                viewHolder = new MyListType5ViewHolder(v5);
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
              myViewHolder.textHeader.setVisibility(View.GONE);
              //myViewHolder.textHeader.setText(item.getTitle());
              myViewHolder.textDesc.setText(item.getDesc());

              StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams)myViewHolder.itemView.getLayoutParams();
              layoutParams.setFullSpan(true);

          }else if(holder instanceof MyListType1ViewHolder){
              HomeListItem item = (HomeListItem) itemList.get(position);
              MyListType1ViewHolder myViewHolder = (MyListType1ViewHolder)holder;

              if(TextUtils.isEmpty(item.getTitle())){
                  myViewHolder.textTitle.setVisibility(View.GONE);
              }else{
                  myViewHolder.textTitle.setVisibility(View.VISIBLE);
                  myViewHolder.textTitle.setText(item.getTitle());
              }
              myViewHolder.textName.setText(item.getName());

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
                      .load(item.getLocalImage())
                      .apply(requestOptions)
                      .into(myViewHolder.imageView);

              String ratio = String.format("%d:%d", (int)item.getWidth(),(int)item.getHeight());

              constraintSet.clone(myViewHolder.constraintLayout);
              constraintSet.setDimensionRatio(myViewHolder.imageView.getId(), ratio);
              constraintSet.applyTo(myViewHolder.constraintLayout);
          }else if(holder instanceof MyListType5ViewHolder){
              HomeListItem item = (HomeListItem) itemList.get(position);
              MyListType5ViewHolder myViewHolder = (MyListType5ViewHolder)holder;
              myViewHolder.textTitle.setText(item.getName());

              RequestOptions requestOptions = new RequestOptions();
              requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
              //requestOptions.dontTransform();
              // requestOptions.override(Utility.dpToPx(150, context), Utility.dpToPx(150, context));
              // requestOptions.centerCrop();
              requestOptions.skipMemoryCache(false);

              Glide.with(context)
                      .load(item.getLocalImage())
                      .apply(requestOptions)
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
