package com.shoppurs.activities.Settings;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.shoppurs.R;
import com.shoppurs.activities.BaseActivity;
import com.shoppurs.activities.CartActivity;
import com.shoppurs.activities.ChangePasswordActivity;
import com.shoppurs.activities.LoginActivity;
import com.shoppurs.activities.MyOrderActivity;
import com.shoppurs.activities.ScrollShopListActivity;
import com.shoppurs.activities.ShopListActivity;
import com.shoppurs.activities.UserListForChatActivity;
import com.shoppurs.utilities.Constants;
import com.shoppurs.utilities.CountDrawable;

public class SettingActivity extends BaseActivity implements View.OnClickListener {

    private RelativeLayout relative_personalInfo, relative_my_cart, relative_display, relative_my_order,
            relative_khatabook, relative_delivery_address, relative_invite,
            relative_frequency_product_list,relative_return_product, relative_chat,relative_change_password, relative_logout, relative_todo_list;
    private Toolbar toolbar;
    private Menu myMenu;
    private int cartCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        init();
        initFooter(this,2);
        cartCount = dbHelper.getCartCount();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        myMenu = menu;
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        setCount(this, cartCount+"");
        return true;
    }

    public void setCount(Context context, String count) {
        MenuItem menuItem = myMenu.findItem(R.id.action_settings);
        LayerDrawable icon = (LayerDrawable) menuItem.getIcon();
        CountDrawable badge;
        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_group_count);
        if (reuse != null && reuse instanceof CountDrawable) {
            badge = (CountDrawable) reuse;
        } else {
            badge = new CountDrawable(context);
        }

        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_group_count, badge);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_settings :
                //Toast.makeText(getApplicationContext(),"Cart list Selected", Toast.LENGTH_LONG).show();
                startActivity(new Intent(SettingActivity.this, CartActivity.class));
                setCount(this, "0");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void init(){
        relative_personalInfo = findViewById(R.id.relative_personalInfo);
        relative_my_cart = findViewById(R.id.relative_my_cart);
        relative_display = findViewById(R.id.relative_display);
        relative_my_order = findViewById(R.id.relative_my_order);
        relative_khatabook = findViewById(R.id.relative_khatabook);
        relative_delivery_address = findViewById(R.id.relative_delivery_address);
        relative_frequency_product_list = findViewById(R.id.relative_frequency_product_list);
        relative_return_product  = findViewById(R.id.relative_return_product);
        relative_invite = findViewById(R.id.relative_invite);
        relative_change_password = findViewById(R.id.relative_change_password);
        relative_chat = findViewById(R.id.relative_chat);
        relative_logout = findViewById(R.id.relative_logout);
        relative_todo_list = findViewById(R.id.relative_todo_list);

        relative_personalInfo.setOnClickListener(this);
        relative_my_cart.setOnClickListener(this);
        relative_display.setOnClickListener(this);
        relative_my_order.setOnClickListener(this);
        relative_khatabook.setOnClickListener(this);
        relative_delivery_address.setOnClickListener(this);
        relative_frequency_product_list.setOnClickListener(this);
        relative_return_product.setOnClickListener(this);
        relative_invite.setOnClickListener(this);
        relative_change_password.setOnClickListener(this);
        relative_chat.setOnClickListener(this);
        relative_logout.setOnClickListener(this);
        relative_todo_list.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == relative_personalInfo){
            startActivity(new Intent( SettingActivity.this, PersonalProfileActivity.class));
        }else if(view == relative_display){
            startActivity(new Intent( SettingActivity.this, DisplayActivity.class));
        }else if(view == relative_my_order){
            startActivity(new Intent( SettingActivity.this, MyOrderActivity.class));
        }else if(view == relative_khatabook){
            Intent intent = new Intent( SettingActivity.this, ScrollShopListActivity.class);
            intent.putExtra("flag", "KhataBook");
            startActivity(intent);
        }else if(view == relative_delivery_address){
            startActivity(new Intent( SettingActivity.this, DeliveryAddressListActivity.class));
        }else if(view == relative_invite){
            referToFriend();
        }else if(view == relative_change_password){
            Intent intent = new Intent( SettingActivity.this, ChangePasswordActivity.class);
            intent.putExtra("flag", "changePassword");
            startActivity(intent);
        }else if(view == relative_chat){
            Intent intent = new Intent( SettingActivity.this, UserListForChatActivity.class);
            intent.putExtra("flag", "chat");
            startActivity(intent);
        }else if(view == relative_my_cart){
            startActivity(new Intent( SettingActivity.this, CartActivity.class));
        }else if(view == relative_logout){
            String fcmTocken = sharedPreferences.getString(Constants.FCM_TOKEN,"");
            dbHelper.deleteAllTable();
            editor.clear();
            editor.putString(Constants.FCM_TOKEN, fcmTocken);
            editor.commit();
            Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }else if(view == relative_frequency_product_list){
            Intent intent = new Intent( SettingActivity.this, ShopListActivity.class);
            intent.putExtra("flag", "Frequency");
            startActivity(intent);
        }else if(view == relative_return_product){
            Intent intent = new Intent( SettingActivity.this, ShopListActivity.class);
            intent.putExtra("flag", "Return Product");
            startActivity(intent);
        }else if(view == relative_todo_list){
            Intent intent = new Intent( SettingActivity.this, ShopListActivity.class);
            intent.putExtra("flag", "ToDo List");
            startActivity(intent);
        }
    }

    private void referToFriend(){
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
            String sAux = "\n Download "+getResources().getString(R.string.app_name)+" app from below link \n\n";
            sAux = sAux + "https://play.google.com/store/apps/details?id=com.shoppurs \n\n";
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            startActivity(Intent.createChooser(i, "Choose one"));
        } catch(Exception e) {
            //e.toString();
        }
    }
}
