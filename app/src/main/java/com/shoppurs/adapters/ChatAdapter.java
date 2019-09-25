package com.shoppurs.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

    public ChatAdapter(Context context, List<ChatMessage> itemList,String userId) {
        this.itemList = itemList;
        this.context=context;
        this.userId = userId;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView txtTime,textMessage,textName;
        public ImageView imageFile,imageViewImage,status;
        public CircleImageView profilePic;
        private ProgressBar progressBar;
        public View itemView;

        public MyViewHolder(View view) {
            super(view);
            txtTime = (TextView) view.findViewById(R.id.message_time);
            textMessage = (TextView) view.findViewById(R.id.text_message);
            profilePic =(CircleImageView) view.findViewById(R.id.image_user);
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
        }
    }



    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
