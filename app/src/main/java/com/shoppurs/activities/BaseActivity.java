package com.shoppurs.activities;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.appbar.AppBarLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shoppurs.R;
import com.shoppurs.database.DbHelper;
import com.shoppurs.morphdialog.DialogActivity;
import com.shoppurs.utilities.Constants;
import com.shoppurs.utilities.Utility;

public class BaseActivity extends AppCompatActivity {

    protected  String  TAG = "Base";
    protected ProgressDialog progressDialog;
    protected SharedPreferences sharedPreferences;
    protected SharedPreferences.Editor editor;
    protected DbHelper dbHelper;
    protected boolean isDarkTheme, isLogIn;
    protected int colorTheme;
    private Toolbar toolbar;
    private AppBarLayout appBarLayout;
    protected int limit = 25,offset = 0;
    protected int smallLimit = 4,smallOffset = 0;
    protected int visibleItemCount,pastVisibleItems,totalItemCount;
    protected boolean loading=false,isScroll = true;
    private boolean isSplash ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        sharedPreferences=getSharedPreferences(Constants.MYPREFERENCEKEY,MODE_PRIVATE);
        editor=sharedPreferences.edit();
        isDarkTheme = sharedPreferences.getBoolean(Constants.IS_DARK_THEME,false);
        isLogIn = sharedPreferences.getBoolean(Constants.IS_LOGGED_IN, false);
        colorTheme = sharedPreferences.getInt(Constants.COLOR_THEME,getResources().getColor(R.color.red_500));


        if(isDarkTheme){
            if(!isSplash)
            setTheme(R.style.Dark);
        }else{
            if(!isSplash)
            setTheme(R.style.Light);
        }

        dbHelper=new DbHelper(this);
        progressDialog = new ProgressDialog(BaseActivity.this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading..");
        // Disable the back button
        DialogInterface.OnKeyListener keyListener = new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return keyCode == KeyEvent.KEYCODE_BACK;
            }
        };
        progressDialog.setOnKeyListener(keyListener);

        if(!isSplash)
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    @Override
    public void onResume(){
        super.onResume();

        boolean isDarkTheme = sharedPreferences.getBoolean(Constants.IS_DARK_THEME,false);
        if(this.isDarkTheme != isDarkTheme && !isSplash)
            recreate();
    }


    public void setIsSplash(boolean status){
        this.isSplash = status;
    }

    int backColor=0,textColor = 0;
    public void initFooter(final Context context, int type) {
        if(isDarkTheme){
            backColor = getResources().getColor(R.color.dark_color);
            textColor = getResources().getColor(R.color.white);
        }else{
            backColor = getResources().getColor(R.color.white);
            textColor = getResources().getColor(R.color.primary_text_color);
        }
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.app_bar);
        appBarLayout.setBackgroundColor(backColor);
        toolbar.setBackgroundColor(backColor);
        toolbar.setTitleTextColor(textColor);

        findViewById(R.id.linear_footer).setBackgroundColor(backColor);
        findViewById(R.id.separator_footer_1).setBackgroundColor(backColor);
        findViewById(R.id.separator_footer_2).setBackgroundColor(backColor);
        findViewById(R.id.separator_footer_3).setBackgroundColor(backColor);
        findViewById(R.id.separator_footer_4).setBackgroundColor(backColor);
        findViewById(R.id.separator_footer_5).setBackgroundColor(backColor);

        RelativeLayout relativeLayoutFooter1 = findViewById(R.id.relative_footer_1);
        RelativeLayout relativeLayoutFooter2 = findViewById(R.id.relative_footer_2);
        RelativeLayout relativeLayoutFooter3 = findViewById(R.id.relative_footer_3);
        RelativeLayout relativeLayoutFooter4 = findViewById(R.id.relative_footer_4);
        RelativeLayout relativeLayoutFooter5 = findViewById(R.id.relative_footer_5);

        ImageView imageViewFooter1 = findViewById(R.id.image_footer_1);
        ImageView imageViewFooter2 = findViewById(R.id.image_footer_2);
        TextView imageViewFooter3 = findViewById(R.id.image_footer_3);
        TextView imageViewFooter4 = findViewById(R.id.image_footer_4);
        ImageView imageViewFooter5 = findViewById(R.id.image_footer_5);

        TextView textViewFooter1 = findViewById(R.id.text_footer_1);
        TextView textViewFooter2 = findViewById(R.id.text_footer_2);
        TextView textViewFooter3 = findViewById(R.id.text_footer_3);
        TextView textViewFooter4 = findViewById(R.id.text_footer_4);
        TextView textViewFooter5 = findViewById(R.id.text_footer_5);

        View view1 = findViewById(R.id.separator_footer_1);
        View view2 = findViewById(R.id.separator_footer_2);
        View view3 = findViewById(R.id.separator_footer_3);
        View view4 = findViewById(R.id.separator_footer_4);
        View view5 = findViewById(R.id.separator_footer_5);

        switch (type) {
            case 0:
                imageViewFooter4.setTypeface(Utility.getAwsomeSolidFont(this));
                imageViewFooter3.setTypeface(Utility.getAwsomeSolidFont(this));
                imageViewFooter1.setColorFilter(colorTheme);
                textViewFooter1.setTextColor(colorTheme);
                view1.setBackgroundColor(colorTheme);
                break;
            case 1:
                imageViewFooter4.setTypeface(Utility.getAwsomeSolidFont(this));
                imageViewFooter3.setTypeface(Utility.getAwsomeSolidFont(this));
                imageViewFooter2.setColorFilter(colorTheme);
                textViewFooter2.setTextColor(colorTheme);
                view2.setBackgroundColor(colorTheme);
                break;
            case 2:
                imageViewFooter4.setTypeface(Utility.getAwsomeSolidFont(this));
                imageViewFooter3.setTypeface(Utility.getAwsomeSolidFont(this));
                imageViewFooter3.setTextColor(colorTheme);
                textViewFooter3.setTextColor(colorTheme);
                view3.setBackgroundColor(colorTheme);
                break;
            case 3:
                imageViewFooter4.setTypeface(Utility.getAwsomeSolidFont(this));
                imageViewFooter4.setTextColor(colorTheme);
                imageViewFooter3.setTypeface(Utility.getAwsomeSolidFont(this));
                textViewFooter4.setTextColor(colorTheme);
                view4.setBackgroundColor(colorTheme);
                break;
            case 4:
                imageViewFooter4.setTypeface(Utility.getAwsomeSolidFont(this));
                imageViewFooter3.setTypeface(Utility.getAwsomeSolidFont(this));
                imageViewFooter5.setColorFilter(colorTheme);
                textViewFooter5.setTextColor(colorTheme);
                view5.setBackgroundColor(colorTheme);
                break;
        }

        relativeLayoutFooter1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (context instanceof MainActivity) {
                    //DialogAndToast.showToast("Profile clicked in profile",BaseActivity.this);
                } else {
                    //DialogAndToast.showToast("clicked in Offer ",BaseActivity.this);
                    Intent intent = new Intent(BaseActivity.this, MainActivity.class);
                    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });
        relativeLayoutFooter2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (context instanceof CategoryListActivity) {
                    //DialogAndToast.showToast("Profile clicked in profile",BaseActivity.this);
                } else {
                    Intent intent = new Intent(BaseActivity.this, CategoryListActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });
        relativeLayoutFooter3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if (context instanceof StoresListActivity) {
                    //DialogAndToast.showToast("Profile clicked in profile",BaseActivity.this);
                } else {
                    Intent intent = new Intent(BaseActivity.this, StoresListActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });

        relativeLayoutFooter4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (context instanceof ShoppursProductActivity) {
                    //DialogAndToast.showToast("Profile clicked in profile",BaseActivity.this);
                } else {
                  //  DialogAndToast.showToast("Profile clicked in Search ",BaseActivity.this);
                    Intent intent = new Intent(BaseActivity.this, ShoppursProductActivity.class);
                    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });

        relativeLayoutFooter5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //DialogAndToast.showDialog("Instant Pay", context);
                Intent intent = new Intent(BaseActivity.this,InstantScannarActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("flag","scan");
                intent.putExtra("type","InstantPay");
                startActivity(intent);
            }
        });

    }


    public void showMyDialog(String msg) {
        //  errorNoInternet.setText("Oops... No internet");
        //  errorNoInternet.setVisibility(View.VISIBLE);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set title
        // alertDialogBuilder.setTitle("Oops...No internet");
        // set dialog message
        alertDialogBuilder
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        onDialogPositiveClicked();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public void onDialogPositiveClicked(){

    }


    public void showProgress(boolean show,String message){
        if(show){
            progressDialog.setMessage(message);
            progressDialog.show();
        }else{
            progressDialog.dismiss();
        }
    }

    public void showProgress(boolean show){
        if(show){
            progressDialog.show();
        }else{
            progressDialog.dismiss();
        }
    }

    public void showImageDialog(String url,View v){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Intent intent = new Intent(this, DialogActivity.class);
            intent.putExtra("image",url);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, v, getString(R.string.transition_dialog));
            startActivityForResult(intent, 100, options.toBundle());
        }else {
            int view = R.layout.activity_dialog;
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder
                    .setView(view)
                    .setCancelable(true);

            // create alert dialog
            final AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();

            final ImageView imageView = (ImageView) alertDialog.findViewById(R.id.image);

            Glide.with(getApplicationContext())
                    .load(url)
                    .centerCrop()
                    .skipMemoryCache(false)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(Utility.dpToPx(300,this), Utility.dpToPx(300,this))
                    .into(imageView);
        }
    }

}
