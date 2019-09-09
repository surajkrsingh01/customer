package com.shoppurs.activities.payment.ccavenue.activities;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shoppurs.R;
import com.shoppurs.activities.NetworkBaseActivity;
import com.shoppurs.activities.payment.ccavenue.utility.CardTypeDTO;
import com.shoppurs.adapters.CardTypeAdapter;

import java.util.ArrayList;
import java.util.List;

public class OtherPaymentListActivity extends NetworkBaseActivity {

    private String flag;

    private RecyclerView recyclerView;
    private List<CardTypeDTO> itemList;
    private CardTypeAdapter itemAdapter;

    private Spinner spinnerCardType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_payment_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button btnPay = findViewById(R.id.btn_pay);
        Button btnCencel = findViewById(R.id.btn_cancel);

        btnPay.setBackgroundColor(colorTheme);
        btnCencel.setBackgroundColor(colorTheme);

        flag = getIntent().getStringExtra("flag");
        TextView textLabel = findViewById(R.id.text_second_label);
        TextView tv_select_bank_label = findViewById(R.id.tv_select_bank_label);
        ArrayList<CardTypeDTO> cardTypeDTOAllOthersArrayList = (ArrayList<CardTypeDTO>)getIntent().getSerializableExtra("cardList");
        ArrayList<CardTypeDTO> cardTypeDTOArrayList = new ArrayList<>();
        if(cardTypeDTOAllOthersArrayList == null){
            cardTypeDTOAllOthersArrayList = new ArrayList<>();
        }

        List<String> cardNameList = new ArrayList();

        if(flag.equals("OPTNBK")) {
            textLabel.setText("Select Bank");
            cardNameList.add(0,"Select Bank");
            tv_select_bank_label.setText("All Other Banks");
        }else if(flag.equals("OPTWLT")) {
            textLabel.setText("Select Wallet");
            cardNameList.add(0,"Select Wallet");
            tv_select_bank_label.setText("All Other Wallets");
            findViewById(R.id.text_info_4).setVisibility(View.GONE);
            findViewById(R.id.rl_save_layout).setVisibility(View.GONE);
        }else if(flag.equals("OPTUPI")) {
            textLabel.setText("Select UPI");
            cardNameList.add(0,"Select UPI");
            tv_select_bank_label.setText("All Other UPI");
            findViewById(R.id.text_info_4).setVisibility(View.GONE);
            findViewById(R.id.text_info_1).setVisibility(View.GONE);
            findViewById(R.id.rl_save_layout).setVisibility(View.GONE);
        }

        for(CardTypeDTO cardTypeDTO : cardTypeDTOAllOthersArrayList){
            cardNameList.add(cardTypeDTO.getCardName());

            if(flag.equals("OPTNBK")){
                if(cardTypeDTO.getCardName().toLowerCase().contains("icici") ||
                        cardTypeDTO.getCardName().toLowerCase().contains("hdfc")  ||
                        cardTypeDTO.getCardName().toLowerCase().contains("state bank of india") ||
                        cardTypeDTO.getCardName().toLowerCase().contains("citi") ||
                        cardTypeDTO.getCardName().toLowerCase().contains("kotak")){
                    cardTypeDTOArrayList.add(cardTypeDTO);
                }
            }else if(flag.equals("OPTWLT")){
                if(cardTypeDTO.getCardName().toLowerCase().contains("freecharge") ||
                        cardTypeDTO.getCardName().toLowerCase().contains("mobikwik")  ||
                        cardTypeDTO.getCardName().toLowerCase().contains("jiomoney")){
                    cardTypeDTOArrayList.add(cardTypeDTO);
                }
            }else if(flag.equals("OPTUPI")){

                if(cardTypeDTO.getCardName().toLowerCase().contains("bhim") ||
                        cardTypeDTO.getCardName().toLowerCase().contains("phonepay")  ||
                        cardTypeDTO.getCardName().toLowerCase().contains("tez") ||
                        cardTypeDTO.getCardName().toLowerCase().contains("paytm") ||
                        cardTypeDTO.getCardName().toLowerCase().contains("whatsapp")){
                    cardTypeDTOArrayList.add(cardTypeDTO);
                }
            }

        }

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this);
        // staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        itemAdapter=new CardTypeAdapter(this,cardTypeDTOAllOthersArrayList);
        recyclerView.setAdapter(itemAdapter);
        recyclerView.setNestedScrollingEnabled(false);

        spinnerCardType=findViewById(R.id.spinner_bank);

        ArrayAdapter<String> cardAdapter = new ArrayAdapter<String>(this, R.layout.simple_dropdown_list_item, cardNameList){
            @Override
            public boolean isEnabled(int position){
                if(position == 0){
                    return false;
                }else{
                    return true;
                }
            }
            @Override
            public View getView(int position, View convertView,
                                ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(getResources().getColor(R.color.grey500));
                }else{
                    if(isDarkTheme){
                        tv.setTextColor(getResources().getColor(R.color.white));
                    }else{
                        tv.setTextColor(getResources().getColor(R.color.primary_text_color));
                    }
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
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(getResources().getColor(R.color.grey500));
                }else{
                    if(isDarkTheme){
                        tv.setTextColor(getResources().getColor(R.color.white));
                    }else{
                        tv.setTextColor(getResources().getColor(R.color.primary_text_color));
                    }
                }
                tv.setPadding(20,20,20,20);
                return view;
            }
        };

       // spinnerCardType.setAdapter(cardAdapter);

    }

}
