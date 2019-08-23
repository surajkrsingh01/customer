package com.shoppurscustomer.activities;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.Transition;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.shoppurscustomer.R;
import com.shoppurscustomer.utilities.Constants;


public class ImageActivity extends AppCompatActivity {

    private boolean isDarkTheme;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences=getSharedPreferences(Constants.MYPREFERENCEKEY,MODE_PRIVATE);

        isDarkTheme = sharedPreferences.getBoolean(Constants.IS_DARK_THEME,true);
        if(isDarkTheme)
            setTheme(R.style.Dark);
        else
            setTheme(R.style.Light);

        setContentView(R.layout.activity_image);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTransition();

        ImageView imageView = (ImageView)findViewById(R.id.image_flang);
       // imageView.setImage(ImageSource.resource(R.drawable.flang_sizing_chart));
        Glide.with(this)
                .load(getIntent().getStringExtra("imageUrl"))
                .skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setTransition(){
        Transition fade = new Fade();
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        fade.excludeTarget(R.id.app_bar, true);
        getWindow().setExitTransition(fade);
        getWindow().setEnterTransition(fade);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
