package com.shoppurs.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.annotation.AttrRes;
import androidx.annotation.IdRes;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.shoppurs.R;

public class BannerDetailsActivity extends NetworkBaseActivity{
    private TextView tv_Back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tv_Back = findViewById(R.id.text_customer_address);
        tv_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String newHtmlString =  getIntent().getStringExtra("desc");
        setHtml((WebView) findViewById(R.id.web_view),
                newHtmlString,
                backColor,
                R.attr.primaryTextColor,
                android.R.attr.colorPrimary,
                getResources().getDimension(R.dimen.normal_text_size),
                getResources().getDimension(R.dimen.activity_horizontal_margin));
    }

    @SuppressLint("ResourceType")
    private void setHtml(WebView webView,
                         String html,
                         @AttrRes int backgroundColor,
                         @AttrRes int textColor,
                         @AttrRes int linkColor,
                         float textSize,
                         float margin) {
        if (TextUtils.isEmpty(html)) {
            return;
        }

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setBackgroundColor(backgroundColor);
        //webView.getSettings().setBuiltInZoomControls(true); // optional
        webView.loadDataWithBaseURL(null,
                wrapHtml(webView.getContext(),  String.format(" %s ",html), textColor, linkColor, textSize, margin),
                "text/html", "UTF-8", null);
    }

    private String wrapHtml(Context context, String html,
                            @AttrRes int textColor,
                            @AttrRes int linkColor,
                            float textSize,
                            float margin) {
        return context.getString(R.string.html,
                html,
                toHtmlColor(context, textColor),
                toHtmlColor(context, linkColor),
                toHtmlPx(context, textSize),
                toHtmlPx(context, margin));
    }

    private static final String FORMAT_HTML_COLOR = "%06X";
    @SuppressLint("ResourceType")
    private String toHtmlColor(Context context, int colorAttr) {
        return String.format(FORMAT_HTML_COLOR, 0xFFFFFF &
                ContextCompat.getColor(context, getIdRes(context, colorAttr)));
    }

    private float toHtmlPx(Context context, float dimen) {
        return dimen / context.getResources().getDisplayMetrics().density;
    }

    @IdRes
    private int getIdRes(Context context,  int attrRes) {
        TypedArray ta = context.getTheme().obtainStyledAttributes(new int[]{attrRes});
        int resId = ta.getResourceId(0, 0);
        ta.recycle();
        return resId;
    }
}
