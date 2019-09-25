package com.shoppurs.activities.Settings;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.shoppurs.R;
import com.shoppurs.activities.NetworkBaseActivity;
import com.shoppurs.adapters.ChatAdapter;
import com.shoppurs.interfaces.MyItemClickListener;
import com.shoppurs.models.ChatMessage;
import com.shoppurs.utilities.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends NetworkBaseActivity implements MyItemClickListener {

    private RecyclerView recyclerView;
    private ChatAdapter myItemAdapter;
    private List<ChatMessage> itemList;
    private EditText etMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

       // initFooter(this,2);
        init();
    }

    private void init(){
        etMessage = findViewById(R.id.et_message);
        /*if(myUser.isOnline()){
            tv_status.setText("online");
            view_online_status.setBackgroundResource(R.drawable.green_circle_background);
        }else{
            tv_status.setText("offline");
            view_online_status.setBackgroundResource(R.drawable.grey_circle_background);
        }*/

        itemList = new ArrayList<>();

        ChatMessage chatMessage=new ChatMessage();
        chatMessage.setMessageId(1);
        chatMessage.setMessageText(getResources().getString(R.string.type_ur_message));
        chatMessage.setMessageTime("11:35 AM");
        chatMessage.setMessageStatus("notSent");
        chatMessage.setMessageReadStatus("read");
        chatMessage.setNotificationReadSatus("read");
        chatMessage.setMessageType("chat");
        chatMessage.setMessageTo("1");
        chatMessage.setMessageFrom(sharedPreferences.getString(Constants.USER_ID,""));
        itemList.add(chatMessage);

        chatMessage=new ChatMessage();
        chatMessage.setMessageId(1);
        chatMessage.setMessageText(getResources().getString(R.string.type_ur_message));
        chatMessage.setMessageTime("11:35 AM");
        chatMessage.setMessageStatus("notSent");
        chatMessage.setMessageReadStatus("read");
        chatMessage.setNotificationReadSatus("read");
        chatMessage.setMessageType("chat");
        chatMessage.setMessageTo("1");
        chatMessage.setMessageFrom(sharedPreferences.getString(Constants.USER_ID,""));
        itemList.add(chatMessage);


        chatMessage=new ChatMessage();
        chatMessage.setMessageId(1);
        chatMessage.setMessageText(getResources().getString(R.string.type_ur_message));
        chatMessage.setMessageTime("11:35 AM");
        chatMessage.setMessageStatus("notSent");
        chatMessage.setMessageReadStatus("read");
        chatMessage.setNotificationReadSatus("read");
        chatMessage.setMessageType("chat");
        chatMessage.setMessageTo(sharedPreferences.getString(Constants.USER_ID,""));
        chatMessage.setMessageFrom("1");
        itemList.add(chatMessage);

        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManagerHomeMenu=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManagerHomeMenu);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        myItemAdapter=new ChatAdapter(this,itemList,"1");
        myItemAdapter.setMyItemClickListener(this);
        recyclerView.setAdapter(myItemAdapter);

        recyclerView.scrollToPosition(itemList.size()-1);

        ImageView iv_send = findViewById(R.id.iv_send);
        iv_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });


    }

    private void sendMessage(){
        String message = etMessage.getText().toString();
        if(!TextUtils.isEmpty(message)){
            Calendar calendar = Calendar.getInstance(Locale.getDefault());
            String timeStamp=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
            String[] time = timeStamp.split(" ")[1].split(":");
            ChatMessage chatMessage=new ChatMessage();
            chatMessage.setMessageId(1);
            chatMessage.setMessageText(message);
            chatMessage.setMessageTime(time[0]+":"+time[1]);
            chatMessage.setMessageStatus("notSent");
            chatMessage.setMessageReadStatus("read");
            chatMessage.setNotificationReadSatus("read");
            chatMessage.setMessageType("chat");
            chatMessage.setMessageTo(sharedPreferences.getString(Constants.USER_ID,""));
            chatMessage.setMessageFrom("1");
            itemList.add(chatMessage);
            myItemAdapter.notifyItemInserted(itemList.size()-1);
            recyclerView.smoothScrollToPosition(itemList.size()-1);
            etMessage.setText("");
        }

    }

    @Override
    public void onItemClicked(int position) {

    }
}
