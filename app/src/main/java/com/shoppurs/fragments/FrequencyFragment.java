package com.shoppurs.fragments;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
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
import com.shoppurs.activities.ShopProductListActivity;
import com.shoppurs.adapters.FrequencyTypeAdapter;
import com.shoppurs.interfaces.MyItemTypeClickListener;
import com.shoppurs.models.FrequencyType;
import com.shoppurs.models.ProductFrequency;
import com.shoppurs.models.MyProduct;
import com.shoppurs.utilities.DialogAndToast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.view.Gravity.CENTER;


/**
 * A simple {@link Fragment} subclass.
 */
public class FrequencyFragment extends BottomSheetDialogFragment implements MyItemTypeClickListener {

    private String TAG = "BottomSearchFragment";
    private int colorTheme;
    private MyProduct myProduct;
    private int position;
    private RecyclerView recyclerViewFrequency;
    private TextView tvProductName, tv_next_order_date, tv_product_frequency_details;
    private FrequencyTypeAdapter frequencyTypeAdapter;
    private List<FrequencyType> frequencyTypeList;
    private ImageView iv_clear;
    private Spinner spinnerMonthDay;
    private String selectedMonthDay;
    private boolean isDarkTheme;
    private RelativeLayout relative_footer_action;
    private ProductFrequency frequency;
    private TextInputEditText et_qty, et_no_of_days;
    private String flag;

    public void setColorTheme(int colorTheme) {
        this.colorTheme = colorTheme;
    }

    public void setDarkTheme(boolean isDarkTheme){
        this.isDarkTheme = isDarkTheme;
    }

    public FrequencyFragment() {
        // Required empty public constructor
    }

    public void setProduct(MyProduct myProduct, int position, String flag){
        this.myProduct  = myProduct;
        this.position = position;
        this.flag = flag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_add_frequency, container, false);
        initViews(rootView);
        setFrequencyParameter();
        return rootView;
    }

    private void initViews(View rootView){
        RelativeLayout relative_search = rootView.findViewById(R.id.relative_header);
        //((GradientDrawable)relative_search.getBackground()).setColor(colorTheme);
        rootView.findViewById(R.id.relative_footer_submit).setBackgroundColor(colorTheme);
        iv_clear = rootView.findViewById(R.id.iv_clear);
        TextView tv = rootView.findViewById(R.id.text_action);
        tv.setText("Submit");
        if(colorTheme == getResources().getColor(R.color.white)){
            tv.setTextColor(getResources().getColor(R.color.primary_text_color));
        }else{
            tv.setTextColor(getResources().getColor(R.color.white));
        }
        tvProductName = rootView.findViewById(R.id.text_product_name);
        tvProductName.setText(myProduct.getName());
        recyclerViewFrequency= rootView.findViewById(R.id.recycler_view_frequency);
        spinnerMonthDay = rootView.findViewById(R.id.spinner_month_day);
        setMonthDaySpinner();
        /*tv_next_order_date = rootView.findViewById(R.id.tv_next_order_date);
        tv_next_order_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTv_next_order_date();
            }
        });*/
        et_qty = rootView.findViewById(R.id.et_qty);
        et_no_of_days = rootView.findViewById(R.id.et_no_of_days);
        tv_product_frequency_details = rootView.findViewById(R.id.tv_product_frequency_details);
        frequency = new ProductFrequency();
        if(flag.equals("edit")) {
            frequency = myProduct.getFrequency();
            et_qty.setText(frequency.getQyantity());
            et_no_of_days.setText(frequency.getNoOfDays());
            //tv_product_frequency_details.setText(frequency.getQyantity());
        }

        iv_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FrequencyFragment.this.dismiss();
            }
        });
        relative_footer_action = rootView.findViewById(R.id.relative_footer_submit);
        relative_footer_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmitFrequency();
            }
        });
    }

    private void onSubmitFrequency(){
        String qty = et_qty.getText().toString();
        String no_of_days = et_no_of_days.getText().toString();
        frequency.setQyantity(qty);
        frequency.setNoOfDays(no_of_days);
        //tv_product_frequency_details

        if(frequency==null){
            DialogAndToast.showDialog("Please Select ProductFrequency Type ", getContext());
            return;
        }else {
            if(TextUtils.isEmpty(frequency.getName())){
                DialogAndToast.showDialog("Please Select ProductFrequency Type ", getContext());
                return;
            }if(TextUtils.isEmpty(frequency.getNoOfDays())){
                DialogAndToast.showDialog("Please Select Number of Days ", getContext());
                return;
            }if(TextUtils.isEmpty(frequency.getQyantity())){
                DialogAndToast.showDialog("Please Select Product Qyantity ", getContext());
                return;
            }

            if(getActivity() instanceof ShopProductListActivity) {
                ((ShopProductListActivity) (getActivity())).setFrequencySelecte(true, frequency, position);
                ((ShopProductListActivity) (getActivity())).updateCart(2, position);
                FrequencyFragment.this.dismiss();

            }

        }
    }

    private void setMonthDaySpinner(){
        final List<String> monthDayList = new ArrayList<>();
        monthDayList.add("Select Month Day");
        for(int i=1;i<31;i++){
            monthDayList.add(""+i);
        }

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<String>(getContext(), R.layout.simple_dropdown_unit_item, monthDayList){
            @Override
            public boolean isEnabled(int position){
                return true;
            }
            @Override
            public View getView(int position, View convertView,
                                ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(isDarkTheme){
                    tv.setTextColor(getResources().getColor(R.color.white));
                }else{
                    tv.setTextColor(getResources().getColor(R.color.primary_text_color));
                }
                return view;
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                if(isDarkTheme){
                    view.setBackgroundColor(getResources().getColor(R.color.dark_color));
                }else{
                    view.setBackgroundColor(getResources().getColor(R.color.white));
                }
                TextView tv = (TextView) view;
                if(isDarkTheme){
                    tv.setTextColor(getResources().getColor(R.color.white));
                }else{
                    tv.setTextColor(getResources().getColor(R.color.primary_text_color));
                }
                tv.setPadding(20,20,20,20);
                tv.setGravity(CENTER);
                return view;
            }
        };

        spinnerMonthDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedMonthDay = monthDayList.get(i);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        spinnerMonthDay.setAdapter(unitAdapter);
    }


    private void setFrequencyParameter(){
        frequencyTypeList = new ArrayList<>();
        FrequencyType type;

        type = new FrequencyType();
        type.setName("Everyday");
        if(flag.equals("edit") && type.getName().equals(frequency.getName()))
        type.setSelected(true);
        else
        type.setSelected(false);
        frequencyTypeList.add(type);

        type = new FrequencyType();
        type.setName("Every 2 Days");
        if(flag.equals("edit") && type.getName().equals(frequency.getName()))
            type.setSelected(true);
        else
            type.setSelected(false);
        frequencyTypeList.add(type);

        type = new FrequencyType();
        type.setName("Every 3 Days");
        if(flag.equals("edit") && type.getName().equals(frequency.getName()))
            type.setSelected(true);
        else
            type.setSelected(false);
        frequencyTypeList.add(type);

        type = new FrequencyType();
        type.setName("Weekly");
        if(flag.equals("edit") && type.getName().equals(frequency.getName()))
            type.setSelected(true);
        else
            type.setSelected(false);
        frequencyTypeList.add(type);

        type = new FrequencyType();
        type.setName("Monthly");
        if(flag.equals("edit") && type.getName().equals(frequency.getName()))
            type.setSelected(true);
        else
            type.setSelected(false);
        frequencyTypeList.add(type);

        recyclerViewFrequency.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager=new GridLayoutManager(getActivity(),3,GridLayoutManager.VERTICAL,false);
        recyclerViewFrequency.setLayoutManager(layoutManager);
        recyclerViewFrequency.setItemAnimator(new DefaultItemAnimator());
        frequencyTypeAdapter =new FrequencyTypeAdapter(getContext(), frequencyTypeList,"editFrequency");
        frequencyTypeAdapter.setMyItemClickListener(this);
        recyclerViewFrequency.setAdapter(frequencyTypeAdapter);
        recyclerViewFrequency.setNestedScrollingEnabled(false);
    }

    private void setTv_next_order_date(){
        final Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "dd/MM/yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                tv_next_order_date.setText(sdf.format(myCalendar.getTime()));
                //DialogAndToast
            }
        };

        new DatePickerDialog(getContext(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    @Override
    public void onItemClicked(int position, int type) {
        frequency.setName(frequencyTypeList.get(position).getName());
        Log.d("clicked Position ",frequency.getName());
    }
}
