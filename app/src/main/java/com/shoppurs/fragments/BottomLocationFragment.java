package com.shoppurs.fragments;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.shoppurs.R;
import com.shoppurs.activities.Settings.FrequencyProductsActivity;
import com.shoppurs.activities.ShopProductListActivity;
import com.shoppurs.adapters.FrequencyTypeAdapter;
import com.shoppurs.interfaces.LocationActionListener;
import com.shoppurs.interfaces.MyItemTypeClickListener;
import com.shoppurs.models.FrequencyType;
import com.shoppurs.models.MyProduct;
import com.shoppurs.models.ProductFrequency;
import com.shoppurs.utilities.DialogAndToast;
import com.shoppurs.utilities.Utility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.view.Gravity.CENTER;


/**
 * A simple {@link Fragment} subclass.
 */
public class BottomLocationFragment extends BottomSheetDialogFragment {

    private String TAG = "BottomSearchFragment";
    private int colorTheme;
    private ImageView iv_clear;
    private String flag, address;
    private boolean isDarkTheme;
    private LocationActionListener listener;
    private LinearLayout linear_edit_location;
    private Button btn_search_location, btn_current_location, btn_update_location;
    private TextInputEditText et_address;

    public BottomLocationFragment() {
        // Required empty public constructor
    }

    public void initFragment(int colorTheme, boolean isDarkTheme, String flag, String address){
        this.colorTheme = colorTheme;
        this.isDarkTheme = isDarkTheme;
        this.flag = flag;
        this.address = address;
    }

    public void setListener(LocationActionListener listener){
        this.listener = listener;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_location_action, container, false);
        initViews(rootView);
        return rootView;
    }

    private void initViews(View rootView){
       /* RelativeLayout relative_search = rootView.findViewById(R.id.relative_header);
        //((GradientDrawable)relative_search.getBackground()).setColor(colorTheme);
        rootView.findViewById(R.id.relative_footer_submit).setBackgroundColor(colorTheme);
        iv_clear = rootView.findViewById(R.id.iv_clear);
        TextView tv = rootView.findViewById(R.id.text_action);
        tv.setText("Submit");
        if(colorTheme == getResources().getColor(R.color.white)){
            tv.setTextColor(getResources().getColor(R.color.primary_text_color));
        }else{
            tv.setTextColor(getResources().getColor(R.color.white));
        }*/
        iv_clear = rootView.findViewById(R.id.iv_clear);
        iv_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        linear_edit_location = rootView.findViewById(R.id.linear_edit_location);
        btn_search_location = rootView.findViewById(R.id.btn_search_location);
        btn_current_location = rootView.findViewById(R.id.btn_current_location);
        btn_update_location = rootView.findViewById(R.id.btn_update_location);
        et_address = rootView.findViewById(R.id.et_address);
        et_address.setText(address);

        btn_search_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onLocationAction("Search Location", null);
                dismiss();
            }
        });
        btn_current_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onLocationAction("Get Current Location", null);
                dismiss();
            }
        });
        btn_update_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onLocationAction("Update Location", et_address.getText().toString());
                dismiss();
            }
        });
    }

}
