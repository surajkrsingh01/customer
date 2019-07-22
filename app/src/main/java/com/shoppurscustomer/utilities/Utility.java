package com.shoppurscustomer.utilities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;

import android.util.Log;
import android.util.TypedValue;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Shweta on 6/17/2016.
 */
public class Utility {
    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 123;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 122;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 125;
    public static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 126;
    public static final int MY_PERMISSIONS_REQUEST_READ_CONTACT = 127;
    public static final int MY_PERMISSIONS_CAMERA = 129;
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean verifyStoragePermissions(final Context context)
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>= Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean verifyReadPermissions(final Context context)
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>= Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkLocationPermission(final Context context)
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>= Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("Location permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions((Activity) context,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean verifyCallPhonePermissions(final Context context)
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>= Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE},
                        MY_PERMISSIONS_REQUEST_CALL_PHONE);
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkReadContactPermission(final Context context)
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>= Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_CONTACT);
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean verifyCameraPermissions(final Context context)
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>= Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) !=
                    PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean verifyStorageOnlyPermissions(final Context context)
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>= Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public static int dpToPx(int dp,Context context) {
        Resources r = context.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public static String getTimeStamp(){
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        String timeStamp=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
        return timeStamp;
    }

    public static Typeface getSimpleLineIconsFont(Context context){
        Typeface custom_font = Typeface.createFromAsset(context.getAssets(), "fonts/Simple-Line-Icons.ttf");
        return custom_font;
    }

    public static void setColorFilter(Drawable drawable, int color){
        drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP));
    }

    public static String numberFormat(double number){
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
        String formattedNo = numberFormat.format(number);
        Log.i("UTILITY ","formatted "+formattedNo);
        if(formattedNo.contains(".")){
            String[] arrayNo = formattedNo.split("\\.");
            String temp = String.format("%.02f",number);
            formattedNo = arrayNo[0] +"."+ temp.split("\\.")[1];
        }else{
            formattedNo =  formattedNo+".00";
        }

        return formattedNo;

    }

    public static String parseDate(String date,String format,String returnFormat){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        try {
            cal.setTime(sdf.parse(date));// all done
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String timeStamp=new SimpleDateFormat(returnFormat).format(cal.getTime());
        return timeStamp;
    }

}
