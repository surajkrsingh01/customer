package com.shoppurs.activities.Settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.shoppurs.R;
import com.shoppurs.activities.BaseImageActivity;
import com.shoppurs.activities.ProductDetailActivity;
import com.shoppurs.adapters.ChatAdapter;
import com.shoppurs.fragments.BottomSearchFragment;
import com.shoppurs.interfaces.MyItemClickListener;
import com.shoppurs.models.ChatMessage;
import com.shoppurs.models.MyProduct;
import com.shoppurs.utilities.ConnectionDetector;
import com.shoppurs.utilities.Constants;
import com.shoppurs.utilities.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChatActivity extends BaseImageActivity implements MyItemClickListener {

    public static boolean isVisible = false;

    private RecyclerView recyclerView;
    private ChatAdapter myItemAdapter;
    private List<ChatMessage> itemList;
    private EditText etMessage;
    private TextView text_second_label;
    private String messageType,messageTo,messageToMobile,messageToPic,messageToName;
    private boolean isSearchingProduct;
    private IntentFilter intentFilter;
    private String shopCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

       // initFooter(this,2);
        init();
    }

    private void init(){
        limit = 50;
        text_second_label = findViewById(R.id.text_second_label);
        messageTo = getIntent().getStringExtra("messageTo");
        messageToMobile = getIntent().getStringExtra("messageToMobile");
        messageToPic = getIntent().getStringExtra("messageToPic");
        messageToName= getIntent().getStringExtra("messageToName");
        shopCode = getIntent().getStringExtra("shopCode");
        text_second_label.setText(messageToName);
        etMessage = findViewById(R.id.et_message);
        itemList = new ArrayList<>();

        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        final RecyclerView.LayoutManager layoutManagerHomeMenu=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManagerHomeMenu);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        myItemAdapter=new ChatAdapter(this,itemList,sharedPreferences.getString(Constants.USER_ID,""));
        myItemAdapter.setMyItemClickListener(this);
        recyclerView.setAdapter(myItemAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isScroll) {
                    visibleItemCount = layoutManagerHomeMenu.getChildCount();
                    totalItemCount = layoutManagerHomeMenu.getItemCount();
                    Log.i(TAG,"visible "+visibleItemCount+" total "+totalItemCount);
                    pastVisibleItems = ((LinearLayoutManager)layoutManagerHomeMenu).findLastVisibleItemPosition();
                    Log.i(TAG,"past visible "+(pastVisibleItems));

                    if (!loading) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            loading = true;
                            offset = limit + offset;
                            getMessage();
                        }
                    }

                }
            }
        });

        //recyclerView.scrollToPosition(itemList.size()-1);

        ImageView iv_send = findViewById(R.id.iv_send);
        Utility.setColorFilter(iv_send.getDrawable(),colorTheme);
        iv_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        ImageView iv_attach = findViewById(R.id.iv_attach);
        Utility.setColorFilter(iv_attach.getDrawable(),colorTheme);
        iv_attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectChatImage();
            }
        });

        if(ConnectionDetector.isNetworkAvailable(this)){
            getMessage();
        }else{

        }

        intentFilter=new IntentFilter();
        intentFilter.addAction("com.shoppursshop.broadcast.messageReceived");

    }

    private void getMessage(){
        Map<String,String> params=new HashMap<>();
        params.put("limit",""+limit);
        params.put("offset",""+offset);
        params.put("messageTo",""+messageTo);
        params.put("messageToMobile",""+messageToMobile);
        params.put("mobile",sharedPreferences.getString(Constants.MOBILE_NO,""));
        String url=getResources().getString(R.string.url_web)+Constants.GET_MESSAGE;
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"getMessage");
    }

    private void sendMessage(){
        String message = etMessage.getText().toString();
        messageType = "text";
        if(!TextUtils.isEmpty(message)){
            Map<String,String> params=new HashMap<>();
            params.put("message",message);
            params.put("position",""+(itemList.size()));
            params.put("messageFromName",sharedPreferences.getString(Constants.FULL_NAME,""));
            params.put("createdBy",sharedPreferences.getString(Constants.FULL_NAME,""));
            params.put("messageFromMobile",sharedPreferences.getString(Constants.MOBILE_NO,""));
            params.put("messageFromPic",sharedPreferences.getString(Constants.PROFILE_PIC,""));
            params.put("messageFromCode",sharedPreferences.getString(Constants.USER_ID,""));
            params.put("messageToCode",messageTo);
            params.put("messageToName",messageToName);
            params.put("messageToMobile",messageToMobile);
            params.put("messageToPic",messageToPic);
            params.put("messageType","text");

            String url=getResources().getString(R.string.url_web)+Constants.SEND_MESSAGE;
            showProgress(true);
            jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"sendMessage");

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
            chatMessage.setMessageType("text");
            chatMessage.setMessageTo("");
            chatMessage.setMessageFrom(sharedPreferences.getString(Constants.USER_ID,""));
            itemList.add(chatMessage);
            myItemAdapter.notifyItemInserted(itemList.size()-1);
            recyclerView.smoothScrollToPosition(itemList.size()-1);
            etMessage.setText("");
        }

    }

    @Override
    public void onJsonObjectResponse(JSONObject jsonObject, String apiName) {
        try{
            if(apiName.equals("sendMessage")){
                if(jsonObject.getBoolean("status")){
                    JSONObject dataObject = jsonObject.getJSONObject("result");
                    ChatMessage chatMessage = itemList.get(dataObject.getInt("position"));
                    if(messageType.equals("image")){
                        chatMessage.setMessageStatus("uploaded");
                        myItemAdapter.notifyItemChanged(dataObject.getInt("position"));
                    }else{
                        chatMessage.setMessageStatus("notSent");
                    }
                }
            }else if(apiName.equals("getMessage")){
                if(jsonObject.getBoolean("status")){
                    if(!jsonObject.getString("result").equals("1")){
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        JSONObject dataObject = null;
                        ChatMessage chatMessage = null;
                        int len = jsonArray.length();
                        for(int i = 0; i<len; i++){
                            dataObject = jsonArray.getJSONObject(i);
                            chatMessage = new ChatMessage();
                            chatMessage.setMessageId(1);
                            chatMessage.setMessageText(dataObject.getString("message"));
                            String[] time = dataObject.getString("createdDate").split(" ")[1].split(":");
                            chatMessage.setMessageTime(time[0]+":"+time[1]);
                            chatMessage.setMessageReadStatus("read");
                            chatMessage.setNotificationReadSatus("read");
                            chatMessage.setMessageType(dataObject.getString("messageType"));
                            chatMessage.setMessageTo(dataObject.getString("messageToCode"));
                            chatMessage.setMessageFrom(dataObject.getString("messageFromCode"));
                            chatMessage.setMessageFromPic(dataObject.getString("messageFromPic"));
                            if(chatMessage.getMessageType().equals("image")){
                                chatMessage.setFileUrl(dataObject.getString("messageFileUrl"));
                                chatMessage.setProdCode(dataObject.getString("messageFileCode"));
                                chatMessage.setMessageStatus("downloaded");
                                //chatMessage.setF(dataObject.getString("messageFromPic"));
                            }else if(chatMessage.getMessageType().equals("product")){
                                chatMessage.setFileUrl(dataObject.getString("messageFileUrl"));
                                chatMessage.setProdCode(dataObject.getString("messageFileCode"));
                            }
                            itemList.add(chatMessage);
                        }

                        myItemAdapter.notifyDataSetChanged();

                        if(itemList.size() > 0){
                            if(len < limit){
                                isScroll = false;
                            }
                            if(len > 0){
                                if(offset == 0){
                                    myItemAdapter.notifyDataSetChanged();
                                }else{
                                    recyclerView.post(new Runnable() {
                                        public void run() {
                                            myItemAdapter.notifyItemRangeInserted(offset,limit);
                                            loading = false;
                                        }
                                    });
                                    Log.d(TAG, "NEXT ITEMS LOADED");
                                }
                            }else{
                                Log.d(TAG, "NO ITEMS FOUND");
                            }
                            recyclerView.scrollToPosition(itemList.size()-1);
                        }else{

                        }
                    }
                }
            }
        }catch (JSONException error){
            error.printStackTrace();
        }
    }

    @Override
    protected void imageAdded(){
        messageType = "image";
        Map<String,String> params=new HashMap<>();
        params.put("message","Image");
        File file = new File(imagePath);
        params.put("messageFileUrl",convertToBase64(file));
        String timestamp = Utility.getTimeStamp();
        timestamp =timestamp.replaceAll(" ","").replaceAll(":","").replaceAll("-","");
        params.put("messageFileName",timestamp+".jpg");
        params.put("position",""+(itemList.size()));
        params.put("messageFromName",sharedPreferences.getString(Constants.FULL_NAME,""));
        params.put("createdBy",sharedPreferences.getString(Constants.FULL_NAME,""));
        params.put("messageFromMobile",sharedPreferences.getString(Constants.MOBILE_NO,""));
        params.put("messageFromPic",sharedPreferences.getString(Constants.PROFILE_PIC,""));
        params.put("messageFromCode",sharedPreferences.getString(Constants.USER_ID,""));
        params.put("messageToCode",messageTo);
        params.put("messageToName",messageToName);
        params.put("messageToMobile",messageToMobile);
        params.put("messageToPic",messageToPic);
        params.put("messageType","image");

        String url=getResources().getString(R.string.url_web)+Constants.SEND_MESSAGE;
        showProgress(true);
        jsonObjectApiRequest(Request.Method.POST,url,new JSONObject(params),"sendMessage");

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        String timeStamp=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
        String[] time = timeStamp.split(" ")[1].split(":");
        ChatMessage chatMessage=new ChatMessage();
        chatMessage.setMessageId(1);
        chatMessage.setMessageText("Image");
        chatMessage.setMessageTime(time[0]+":"+time[1]);
        chatMessage.setMessageStatus("notSent");
        chatMessage.setMessageReadStatus("read");
        chatMessage.setNotificationReadSatus("read");
        chatMessage.setMessageType("image");
        chatMessage.setMessageStatus("uploading");
        chatMessage.setFileUrl(imagePath);
        chatMessage.setMessageTo("");
        chatMessage.setMessageFrom(sharedPreferences.getString(Constants.USER_ID,""));
        itemList.add(chatMessage);
        myItemAdapter.notifyItemInserted(itemList.size()-1);
        recyclerView.smoothScrollToPosition(itemList.size()-1);
    }

    @Override
    public void selectProduct(){
        isSearchingProduct = true;
        BottomSearchFragment bottomSearchFragment = new BottomSearchFragment();
        Bundle bundle = new Bundle();
        bundle.putString("shopCode", shopCode);
        Log.d("shopCode", shopCode);
        bottomSearchFragment.setArguments(bundle);
        bottomSearchFragment.setCallingActivityName("ChatActivity", sharedPreferences, isDarkTheme);
        bottomSearchFragment.setMyItemClickListener(this);
        bottomSearchFragment.show(getSupportFragmentManager(), bottomSearchFragment.getTag());
    }

    @Override
    public void onProductSearch(MyProduct item) {
        if(isSearchingProduct) {
            isSearchingProduct = false;
            messageType = "product";
            String imageUrl = item.getProdImage1();
            Map<String, String> params = new HashMap<>();
            params.put("message", item.getName());
            params.put("messageFileCode", item.getCode());
            params.put("messageFileUrl", item.getProdImage1());
            String timestamp = Utility.getTimeStamp();
            //timestamp =timestamp.replaceAll(" ","").replaceAll(":","").replaceAll("-","");
            params.put("messageFileName", imageUrl.substring(item.getProdImage1().lastIndexOf("/") + 1));
            params.put("position", "" + (itemList.size()));
            params.put("messageFromName", sharedPreferences.getString(Constants.FULL_NAME, ""));
            params.put("createdBy", sharedPreferences.getString(Constants.FULL_NAME, ""));
            params.put("messageFromMobile", sharedPreferences.getString(Constants.MOBILE_NO, ""));
            params.put("messageFromPic", sharedPreferences.getString(Constants.PROFILE_PIC, ""));
            params.put("messageFromCode", sharedPreferences.getString(Constants.USER_ID, ""));
            params.put("messageToCode", messageTo);
            params.put("messageToName", messageToName);
            params.put("messageToMobile", messageToMobile);
            params.put("messageToPic", messageToPic);
            params.put("messageType", "product");

            String url = getResources().getString(R.string.url_web) + Constants.SEND_MESSAGE;
            showProgress(true);
            jsonObjectApiRequest(Request.Method.POST, url, new JSONObject(params), "sendMessage");

            Calendar calendar = Calendar.getInstance(Locale.getDefault());
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
            String[] time = timeStamp.split(" ")[1].split(":");
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setMessageId(1);
            chatMessage.setMessageText(item.getName());
            chatMessage.setProdCode(item.getCode());
            chatMessage.setMessageTime(time[0] + ":" + time[1]);
            chatMessage.setMessageStatus("notSent");
            chatMessage.setMessageReadStatus("read");
            chatMessage.setNotificationReadSatus("read");
            chatMessage.setMessageType("product");
            chatMessage.setFileUrl(imagePath);
            chatMessage.setMessageTo("");
            chatMessage.setMessageFrom(sharedPreferences.getString(Constants.USER_ID, ""));
            itemList.add(chatMessage);
            myItemAdapter.notifyItemInserted(itemList.size() - 1);
            recyclerView.smoothScrollToPosition(itemList.size() - 1);
        }
    }

    @Override
    public void onItemClicked(int pos) {
            ChatMessage chatMessage = itemList.get(pos);
            Log.i(TAG,"code "+chatMessage.getProdCode());
            MyProduct item = new MyProduct();
            item.setId(chatMessage.getProdCode());
            item.setName(chatMessage.getMessageText());
            Intent intent = new Intent(ChatActivity.this, ProductDetailActivity.class);
            intent.putExtra("flag", "ChatProduct");
            intent.putExtra("MyProduct", item);
            startActivity(intent);
    }

    @Override
    public void onResume(){
        super.onResume();
        isVisible = true;
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onStop(){
        super.onStop();
        isVisible = false;
        unregisterReceiver(broadcastReceiver);
    }

    private BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent serviceIntent) {
            if (serviceIntent.getAction().equals("com.shoppursshop.broadcast.messageReceived")) {
                try {
                    JSONObject jsonObject = new JSONObject(serviceIntent.getStringExtra("data"));
                    Calendar calendar = Calendar.getInstance(Locale.getDefault());
                    String timeStamp=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
                    String[] time = timeStamp.split(" ")[1].split(":");
                    ChatMessage chatMessage=new ChatMessage();
                    chatMessage.setMessageId(1);
                    chatMessage.setMessageText(jsonObject.getString("message"));
                    chatMessage.setMessageTime(time[0]+":"+time[1]);
                    chatMessage.setMessageStatus("sent");
                    chatMessage.setMessageReadStatus("read");
                    chatMessage.setNotificationReadSatus("read");
                    chatMessage.setMessageType(jsonObject.getString("messageType"));
                    chatMessage.setMessageFromPic(jsonObject.getString("pic"));
                    if(chatMessage.getMessageType().equals("image")){
                        chatMessage.setFileUrl(jsonObject.getString("messageFileUrl"));
                    }else if(chatMessage.getMessageType().equals("product")){
                        chatMessage.setFileUrl(jsonObject.getString("messageFileUrl"));
                        chatMessage.setProdCode(jsonObject.getString("messageFileCode"));
                    }
                    chatMessage.setMessageTo(sharedPreferences.getString(Constants.USER_ID,""));
                    chatMessage.setMessageFrom(jsonObject.getString("from"));
                    itemList.add(chatMessage);
                    myItemAdapter.notifyItemInserted(itemList.size()-1);
                    recyclerView.smoothScrollToPosition(itemList.size()-1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };
}
