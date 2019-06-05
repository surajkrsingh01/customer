package com.shoppurscustomer.activities.Settings;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
            imageViewWhite,imageViewGrey,imageViewBlack,imageViewTemp;
    private RelativeLayout relativeLayoutRed,relativeLayoutGreen,relativeLayoutBlue,relativeLayoutPink,
            relativeLayoutYellow,relativeLayoutAmber, relativeLayoutContainer, relativeLayoutSetting1,
            relativeLayoutWhite,relativeLayoutGrey,relativeLayoutBlack;
    private TextView textViewDarkLabel,textViewEnableDarkLabel,textViewColorLabel, text_display;
    private LinearLayout linearLayoutSetting2;
    private Switch themeSwitch;
    private Toolbar toolbar;
    private AppBarLayout appBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        toolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        initFooter(this,4);
        initViews();
    }

    private void initViews(){
        themeSwitch = findViewById(R.id.switch_theme);
        textViewDarkLabel = findViewById(R.id.text_dark_label);
        text_display = findViewById(R.id.text_display);
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
        relativeLayoutWhite = findViewById(R.id.relative_white);
        relativeLayoutGrey = findViewById(R.id.relative_grey);
        relativeLayoutBlack = findViewById(R.id.relative_black);

        imageViewRed = findViewById(R.id.image_color_red);
        imageViewBlue= findViewById(R.id.image_color_blue);
        imageViewGreen = findViewById(R.id.image_color_green);
        imageViewPink = findViewById(R.id.image_color_pink);
        imageViewYellow = findViewById(R.id.image_color_yellow);
        imageViewAmber = findViewById(R.id.image_color_amber);
        imageViewWhite = findViewById(R.id.image_color_white);
        imageViewGrey = findViewById(R.id.image_color_grey);
        imageViewBlack = findViewById(R.id.image_color_black);
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

        setColorTheme();

        relativeLayoutRed.setOnClickListener(this);
        relativeLayoutBlue.setOnClickListener(this);
        relativeLayoutGreen.setOnClickListener(this);
        relativeLayoutPink.setOnClickListener(this);
        relativeLayoutYellow.setOnClickListener(this);
        relativeLayoutAmber.setOnClickListener(this);
        relativeLayoutWhite.setOnClickListener(this);
        relativeLayoutGrey.setOnClickListener(this);
        relativeLayoutBlack.setOnClickListener(this);
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
        text_display.setTextColor(textColor);
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

    private void setColorTheme(){
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
        }else if(colorTheme == getResources().getColor(R.color.amber500)){
            imageViewTemp = imageViewAmber;
            imageViewAmber.setImageResource(R.drawable.ic_check_black_24dp);
        }else if(colorTheme == getResources().getColor(R.color.grey500)){
            imageViewTemp = imageViewGrey;
            imageViewGrey.setImageResource(R.drawable.ic_check_black_24dp);
        }else if(colorTheme == getResources().getColor(R.color.white)){
            imageViewTemp = imageViewWhite;
            imageViewWhite.setImageResource(R.drawable.ic_check_black_24dp);
        }else if(colorTheme == getResources().getColor(R.color.black)){
            imageViewTemp = imageViewBlack;
            imageViewBlack.setImageResource(R.drawable.ic_check_black_24dp);
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
        }else if(view == relativeLayoutWhite){
            editor.putInt(Constants.COLOR_THEME,getResources().getColor(R.color.white));
            imageViewWhite.setImageResource(R.drawable.ic_check_black_24dp);
            imageViewTemp.setImageResource(0);
            imageViewTemp  = imageViewWhite;
        }else if(view == relativeLayoutGrey){
            editor.putInt(Constants.COLOR_THEME,getResources().getColor(R.color.grey600));
            imageViewGrey.setImageResource(R.drawable.ic_check_black_24dp);
            imageViewTemp.setImageResource(0);
            imageViewTemp  = imageViewGrey;
        }else if(view == relativeLayoutBlack){
            editor.putInt(Constants.COLOR_THEME,getResources().getColor(R.color.black));
            imageViewBlack.setImageResource(R.drawable.ic_check_black_24dp);
            imageViewTemp.setImageResource(0);
            imageViewTemp  = imageViewBlack;
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
}
