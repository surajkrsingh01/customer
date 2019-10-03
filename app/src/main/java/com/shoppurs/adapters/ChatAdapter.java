package com.shoppurs.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.shoppurs.R;
import com.shoppurs.interfaces.MyItemClickListener;
import com.shoppurs.models.ChatMessage;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<ChatMessage> itemList;
    private Context context;
    private String userId;
    private final int GOING = 0,COMING = 1;

    private MyItemClickListener myItemClickListener;

    public void setMyItemClickListener(MyItemClickListener myItemClickListener) {
        this.myItemClickListener = myItemClickListener;
    }

    public ChatAdapter(Context context, List<ChatMessage> itemList, String userId) {
        this.itemList = itemList;
        this.context=context;
        this.userId = userId;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView txtTime,textMessage,textName,text_prod_name,text_prod_code;
        public ImageView iv_product_image,imageViewImage,status;
        public CircleImageView profilePic;
        private ProgressBar progressBar;
        private RelativeLayout rlImage,rl_product_layout;
        public View itemView;

        public MyViewHolder(View view) {
            super(view);
            txtTime = (TextView) view.findViewById(R.id.message_time);
            textMessage = (TextView) view.findViewById(R.id.text_message);
            profilePic =(CircleImageView) view.findViewById(R.id.image_user);
            progressBar =view.findViewById(R.id.progress_bar);
            rlImage = view.findViewById(R.id.rl_image_layout);
            imageViewImage = view.findViewById(R.id.iv_image);
            iv_product_image = view.findViewById(R.id.iv_product_image);
            text_prod_name = view.findViewById(R.id.text_prod_name);
            text_prod_code = view.findViewById(R.id.text_prod_code);
            rl_product_layout = view.findViewById(R.id.rl_product_layout);

            rl_product_layout.setOnClickListener(this);
           // profilePic.setImageResource(R.drawable.user);
        }

        @Override
        public void onClick(View view) {
            myItemClickListener.onItemClicked(getAdapterPosition());
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView=null;
        switch (viewType){
            case GOING:
                itemView = inflater.inflate(R.layout.chat_going_item_layout, parent, false);
                viewHolder = new MyViewHolder(itemView);
                break;
            case COMING:
                itemView = inflater.inflate(R.layout.chat_coming_item_layout, parent, false);
                viewHolder = new MyViewHolder(itemView);
                break;
        }

        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        String messageFrom = itemList.get(position).getMessageFrom();
        if(messageFrom.equals(userId))
            return GOING;
        else
            return COMING;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MyViewHolder){
            MyViewHolder myViewHolder = (MyViewHolder)holder;
            ChatMessage chatMessage=itemList.get(position);
            myViewHolder.textMessage.setText(chatMessage.getMessageText());
            myViewHolder.txtTime.setText(chatMessage.getMessageTime());
            if(!chatMessage.getMessageFrom().equals(userId)){
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
                requestOptions.dontTransform();
                // requestOptions.override(Utility.dpToPx(150, context), Utility.dpToPx(150, context));
                // requestOptions.centerCrop();
                requestOptions.skipMemoryCache(false);

                Glide.with(context)
                        .load(chatMessage.getMessageFromPic())
                        .apply(requestOptions)
                        .into(myViewHolder.profilePic);
            }

            if(chatMessage.getMessageType().equals("image")){
                myViewHolder.textMessage.setVisibility(View.GONE);
                myViewHolder.rlImage.setVisibility(View.VISIBLE);
                if(!chatMessage.getMessageFrom().equals(userId)){
                    Glide.with(context)
                            .load(chatMessage.getFileUrl())
                            .centerCrop()
                            .skipMemoryCache(false)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            //.override(Utility.dpToPx(150,context),Utility.dpToPx(200,context))
                            .into(myViewHolder.imageViewImage);
                    myViewHolder.progressBar.setVisibility(View.GONE);
                }else{
                    if(chatMessage.getMessageStatus().equals("uploaded")){
                        Glide.with(context)
                                .load(chatMessage.getFileUrl())
                                .centerCrop()
                                .skipMemoryCache(false)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                //.override(Utility.dpToPx(150,context),Utility.dpToPx(200,context))
                                .into(myViewHolder.imageViewImage);
                        myViewHolder.progressBar.setVisibility(View.GONE);
                    }else {
                        myViewHolder.progressBar.setVisibility(View.VISIBLE);
                    }
                }

            }else if(chatMessage.getMessageType().equals("product")){
                myViewHolder.textMessage.setVisibility(View.GONE);
                myViewHolder.rlImage.setVisibility(View.GONE);
                myViewHolder.rl_product_layout.setVisibility(View.VISIBLE);
                myViewHolder.text_prod_name.setText("Prod Name: "+chatMessage.getMessageText());
                myViewHolder.text_prod_code.setText("Prod Code: "+chatMessage.getProdCode());
                Glide.with(context)
                        .load(chatMessage.getFileUrl())
                        .centerCrop()
                        .skipMemoryCache(false)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                       // .override(Utility.dpToPx(150,context),Utility.dpToPx(200,context))
                        .into(myViewHolder.iv_product_image);
            }else{
                myViewHolder.textMessage.setVisibility(View.VISIBLE);
                myViewHolder.rlImage.setVisibility(View.GONE);
            }

        }
    }



    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
