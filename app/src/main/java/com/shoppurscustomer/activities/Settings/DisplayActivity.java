package com.shoppurscustomer.activities.Settings;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.appbar.AppBarLayout;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.shoppurscustomer.R;
import com.shoppurscustomer.activities.BaseActivity;
import com.shoppurscustomer.utilities.Constants;


public class DisplayActivity extends BaseActivity implements View.OnClickListener {

    private ImageView imageViewRed,imageViewGreen,imageViewBlue,imageViewPink,imageViewYellow,imageViewAmber,
            imageViewTeal,imageViewGrey,imageViewPurple,imageViewTemp;
    private RelativeLayout relativeLayoutRed,relativeLayoutGreen,relativeLayoutBlue,relativeLayoutPink,
            relativeLayoutYellow,relativeLayoutAmber,
            relativeLayoutTeal,relativeLayoutGrey,relativeLayoutPurple;

    private TextView textViewDarkLabel,textViewEnableDarkLabel,textViewColorLabel, text_second_label;

    private Switch themeSwitch;

    private RelativeLayout relativeLayoutContainer,relativeLayoutSetting1;
    private LinearLayout linearLayoutSetting2;
    private Toolbar toolbar;
    private AppBarLayout appBarLayout;
    private TextView tv_top_parent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
        initFooter(this,2);
    }

    private void init(){
        themeSwitch = findViewById(R.id.switch_theme);
        textViewDarkLabel = findViewById(R.id.text_dark_label);
        text_second_label = findViewById(R.id.text_second_label);
        textViewEnableDarkLabel = findViewById(R.id.text_enable_dark_label);
        textViewColorLabel = findViewById(R.id.text_color_label);
        relativeLayoutContainer = findViewById(R.id.container);
        linearLayoutSetting2 = findViewById(R.id.linear_setting_2);
        relativeLayoutSetting1 = findViewById(R.id.relative_setting_1);

        relativeLayoutRed = findViewById(R.id.relative_red);
        relativeLayoutBlue= findViewById(R.id.relative_blue);
        relativeLayoutGreen = findViewById(R.id.relative_green);
        relativeLayoutPink = findViewById(R.id.relative_pink);
        relativeLayoutYellow = findViewById(R.id.relative_yellow);
        relativeLayoutAmber = findViewById(R.id.relative_amber);
        relativeLayoutTeal = findViewById(R.id.relative_teal);
        relativeLayoutGrey = findViewById(R.id.relative_grey);
        relativeLayoutPurple = findViewById(R.id.relative_purple);


        imageViewRed = findViewById(R.id.image_color_red);
        imageViewBlue= findViewById(R.id.image_color_blue);
        imageViewGreen = findViewById(R.id.image_color_green);
        imageViewPink = findViewById(R.id.image_color_pink);
        imageViewYellow = findViewById(R.id.image_color_yellow);
        imageViewAmber = findViewById(R.id.image_color_amber);
        imageViewTeal = findViewById(R.id.image_color_teal);
        imageViewGrey = findViewById(R.id.image_color_grey);
        imageViewPurple = findViewById(R.id.image_color_purple);

        changeColor(imageViewRed.getBackground(),getResources().getColor(R.color.red_500));
        changeColor(imageViewBlue.getBackground(),getResources().getColor(R.color.blue500));
        changeColor(imageViewGreen.getBackground(),getResources().getColor(R.color.green500));
        changeColor(imageViewPink.getBackground(),getResources().getColor(R.color.pink500));
        changeColor(imageViewYellow.getBackground(),getResources().getColor(R.color.yellow500));
        changeColor(imageViewGrey.getBackground(),getResources().getColor(R.color.grey600));
        changeColor(imageViewAmber.getBackground(),getResources().getColor(R.color.amber600));

        if(isDarkTheme){
            themeSwitch.setChecked(true);
        }else{
            themeSwitch.setChecked(false);
        }


        themeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    editor.putBoolean(Constants.IS_DARK_THEME,true);
                    changeTheme(true);
                }else{
                    editor.putBoolean(Constants.IS_DARK_THEME,false);
                    changeTheme(false);
                }
                editor.commit();
            }
        });

        setFooterColorTheme();

        relativeLayoutRed.setOnClickListener(this);
        relativeLayoutBlue.setOnClickListener(this);
        relativeLayoutGreen.setOnClickListener(this);
        relativeLayoutPink.setOnClickListener(this);
        relativeLayoutYellow.setOnClickListener(this);
        relativeLayoutAmber.setOnClickListener(this);
        relativeLayoutPurple.setOnClickListener(this);
        relativeLayoutGrey.setOnClickListener(this);
        relativeLayoutTeal.setOnClickListener(this);

        tv_top_parent = findViewById(R.id.text_left_label);
        tv_top_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DisplayActivity.this, SettingActivity.class));
                finish();
            }
        });

    }


    private void changeTheme(boolean isDark){
        int backColor=0,textColor = 0;
        int borderDrawable = 0,statusBarColor = 0;
        if(isDark){
            backColor = getResources().getColor(R.color.dark_color);
            textColor = getResources().getColor(R.color.white);
            statusBarColor = getResources().getColor(R.color.dark_color);
            borderDrawable = R.drawable.grey_border_dark_solid_background;
        }else{
            backColor = getResources().getColor(R.color.white);
            statusBarColor = getResources().getColor(R.color.grey300);
            textColor = getResources().getColor(R.color.primary_text_color);
            borderDrawable = R.drawable.grey_border_background;
        }

        relativeLayoutContainer.setBackgroundColor(backColor);
        textViewDarkLabel.setTextColor(textColor);
        text_second_label.setTextColor(textColor);
        textViewEnableDarkLabel.setTextColor(textColor);
        textViewColorLabel.setTextColor(textColor);
        appBarLayout.setBackgroundColor(backColor);
        toolbar.setBackgroundColor(backColor);
        toolbar.setTitleTextColor(textColor);
        findViewById(R.id.linear_footer).setBackgroundColor(backColor);
        findViewById(R.id.separator_footer_1).setBackgroundColor(backColor);
        findViewById(R.id.separator_footer_2).setBackgroundColor(backColor);
        findViewById(R.id.separator_footer_3).setBackgroundColor(backColor);
        findViewById(R.id.separator_footer_4).setBackgroundColor(backColor);
        relativeLayoutSetting1.setBackgroundResource(borderDrawable);
        linearLayoutSetting2.setBackgroundResource(borderDrawable);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(statusBarColor);
        }

    }

    private void changeColor(Drawable drawable, int color){
        drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
    }


    public void setFooterColorTheme(){
        if(colorTheme == getResources().getColor(R.color.red_500)){
            imageViewTemp = imageViewRed;
            imageViewRed.setImageResource(R.drawable.ic_check_black_24dp);
        }else if(colorTheme == getResources().getColor(R.color.blue500)){
            imageViewTemp = imageViewBlue;
            imageViewBlue.setImageResource(R.drawable.ic_check_black_24dp);
        }else if(colorTheme == getResources().getColor(R.color.green500)){
            imageViewTemp = imageViewGreen;
            imageViewGreen.setImageResource(R.drawable.ic_check_black_24dp);
        }else if(colorTheme == getResources().getColor(R.color.pink500)){
            imageViewTemp = imageViewPink;
            imageViewPink.setImageResource(R.drawable.ic_check_black_24dp);
        }else if(colorTheme == getResources().getColor(R.color.yellow500)){
            imageViewTemp = imageViewYellow;
            imageViewYellow.setImageResource(R.drawable.ic_check_black_24dp);
        }else if(colorTheme == getResources().getColor(R.color.amber600)){
            imageViewTemp = imageViewAmber;
            imageViewAmber.setImageResource(R.drawable.ic_check_black_24dp);
        }else if(colorTheme == getResources().getColor(R.color.grey600)){
            imageViewTemp = imageViewGrey;
            imageViewGrey.setImageResource(R.drawable.ic_check_black_24dp);
        }else if(colorTheme == getResources().getColor(R.color.teal_500)){
            imageViewTemp = imageViewTeal;
            imageViewTeal.setImageResource(R.drawable.ic_check_black_24dp);
        }else if(colorTheme == getResources().getColor(R.color.purple500)){
            imageViewTemp = imageViewPurple;
            imageViewPurple.setImageResource(R.drawable.ic_check_black_24dp);
        }
    }

    @Override
    public void onClick(View view) {
        if(view == relativeLayoutRed){
            editor.putInt(Constants.COLOR_THEME,getResources().getColor(R.color.red_500));
            imageViewRed.setImageResource(R.drawable.ic_check_black_24dp);
            imageViewTemp.setImageResource(0);
            imageViewTemp  = imageViewRed;
        }else if(view == relativeLayoutBlue){
            editor.putInt(Constants.COLOR_THEME,getResources().getColor(R.color.blue500));
            imageViewBlue.setImageResource(R.drawable.ic_check_black_24dp);
            imageViewTemp.setImageResource(0);
            imageViewTemp  = imageViewBlue;
        }else if(view == relativeLayoutGreen){
            editor.putInt(Constants.COLOR_THEME,getResources().getColor(R.color.green500));
            imageViewGreen.setImageResource(R.drawable.ic_check_black_24dp);
            imageViewTemp.setImageResource(0);
            imageViewTemp  = imageViewGreen;
        }else if(view == relativeLayoutPink){
            editor.putInt(Constants.COLOR_THEME,getResources().getColor(R.color.pink500));
            imageViewPink.setImageResource(R.drawable.ic_check_black_24dp);
            imageViewTemp.setImageResource(0);
            imageViewTemp  = imageViewPink;
        }else if(view == relativeLayoutYellow){
            editor.putInt(Constants.COLOR_THEME,getResources().getColor(R.color.yellow500));
            imageViewYellow.setImageResource(R.drawable.ic_check_black_24dp);
            imageViewTemp.setImageResource(0);
            imageViewTemp  = imageViewYellow;
        }else if(view == relativeLayoutAmber){
            editor.putInt(Constants.COLOR_THEME,getResources().getColor(R.color.amber600));
            imageViewAmber.setImageResource(R.drawable.ic_check_black_24dp);
            imageViewTemp.setImageResource(0);
            imageViewTemp  = imageViewAmber;
        }else if(view == relativeLayoutTeal){
            editor.putInt(Constants.COLOR_THEME,getResources().getColor(R.color.teal_500));
            imageViewTeal.setImageResource(R.drawable.ic_check_black_24dp);
            imageViewTemp.setImageResource(0);
            imageViewTemp  = imageViewTeal;
        }else if(view == relativeLayoutGrey){
            editor.putInt(Constants.COLOR_THEME,getResources().getColor(R.color.grey600));
            imageViewGrey.setImageResource(R.drawable.ic_check_black_24dp);
            imageViewTemp.setImageResource(0);
            imageViewTemp  = imageViewGrey;
        }else if(view == relativeLayoutPurple){
            editor.putInt(Constants.COLOR_THEME,getResources().getColor(R.color.purple500));
            imageViewPurple.setImageResource(R.drawable.ic_check_black_24dp);
            imageViewTemp.setImageResource(0);
            imageViewTemp  = imageViewPurple;
        }

        editor.commit();
        colorTheme = sharedPreferences.getInt(Constants.COLOR_THEME,getResources().getColor(R.color.red_500));
        ImageView imageViewFooter5 = findViewById(R.id.image_footer_5);
        TextView textViewFooter5 = findViewById(R.id.text_footer_5);
        View view5 = findViewById(R.id.separator_footer_5);
        imageViewFooter5.setColorFilter(colorTheme);
        textViewFooter5.setTextColor(colorTheme);
        view5.setBackgroundColor(colorTheme);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            super.onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
