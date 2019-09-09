package com.shoppurs.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.shoppurs.R;
import com.shoppurs.activities.payment.ccavenue.activities.CardDetailsActivity;
import com.shoppurs.activities.payment.ccavenue.activities.OtherPaymentListActivity;
import com.shoppurs.activities.payment.ccavenue.utility.CardTypeDTO;
import com.shoppurs.interfaces.MyItemTypeClickListener;
import com.shoppurs.models.PaymentOption;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PaymentAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<PaymentOption> itemList;
    private Context context;
    private String type;
    private boolean isDarkTheme;
    private View preCreditCard,preRecyclerView,preButtonPay,preRlNetBanking;

    Map<String, ArrayList<CardTypeDTO>> cardsList;

    private MyItemTypeClickListener myItemTypeClickListener;

    public void setMyItemTypeClickListener(MyItemTypeClickListener myItemTypeClickListener) {
        this.myItemTypeClickListener = myItemTypeClickListener;
    }

    public void setCardsList(Map<String, ArrayList<CardTypeDTO>> cardsList) {
        this.cardsList = cardsList;
    }

    public PaymentAdapter(Context context, List<PaymentOption> itemList,boolean isDarkTheme) {
        this.itemList = itemList;
        this.context=context;
        this.isDarkTheme = isDarkTheme;

    }

    public class MyHomeHeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView textHeader,tv_select_bank_label;
        private ImageView imageViewArrow;
        private LinearLayout llCreditCard;
        private RelativeLayout rl_net_banking;
        private Spinner spinnerCardType;
        private RecyclerView recyclerView;
        private Button btnPay;


        public MyHomeHeaderViewHolder(View itemView) {
            super(itemView);
            textHeader=itemView.findViewById(R.id.text_name);
            tv_select_bank_label=itemView.findViewById(R.id.tv_select_bank_label);
            llCreditCard=itemView.findViewById(R.id.ll_credit_card);
            rl_net_banking=itemView.findViewById(R.id.rl_net_banking);
            recyclerView=itemView.findViewById(R.id.recycler_view);
            spinnerCardType=itemView.findViewById(R.id.spinner_bank);
            btnPay=itemView.findViewById(R.id.btn_pay);
            imageViewArrow=itemView.findViewById(R.id.image_arrow);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            PaymentOption paymentOption = itemList.get(getAdapterPosition());

            if(paymentOption.getId().equals("OPTCRDC") || paymentOption.getId().equals("OPTDBCRD")){

                Intent intent = new Intent(context, CardDetailsActivity.class);
                intent.putExtra("flag",paymentOption.getId());
                context.startActivity(intent);

              /*  if(preRecyclerView != null){
                    preRecyclerView.setVisibility(View.GONE);
                }

                if(preButtonPay != null)
                    preButtonPay.setVisibility(View.GONE);

                if(preRlNetBanking != null){
                    preRlNetBanking.setVisibility(View.GONE);
                }

                if(preCreditCard != null)
                    preCreditCard.setVisibility(View.GONE);

                llCreditCard.setVisibility(View.VISIBLE);
                btnPay.setVisibility(View.VISIBLE);
                preButtonPay = btnPay;
                preCreditCard = llCreditCard;*/
            }else if(paymentOption.getId().equals("OPTNBK") || paymentOption.getId().equals("OPTWLT")
            || paymentOption.getId().equals("OPTUPI")){

                Intent intent = new Intent(context, OtherPaymentListActivity.class);
                intent.putExtra("flag",paymentOption.getId());
                intent.putExtra("cardList",cardsList.get(paymentOption.getId()));
                context.startActivity(intent);

               /* if(preCreditCard != null)
                preCreditCard.setVisibility(View.GONE);

                if(preButtonPay != null)
                preButtonPay.setVisibility(View.GONE);

                if(preRlNetBanking != null){
                    preRlNetBanking.setVisibility(View.GONE);
                }
                if(preRecyclerView != null){
                    preRecyclerView.setVisibility(View.GONE);
                }

                recyclerView.setVisibility(View.VISIBLE);
                rl_net_banking.setVisibility(View.VISIBLE);
                btnPay.setVisibility(View.VISIBLE);
                preRecyclerView = recyclerView;
                preButtonPay = btnPay;
                preRlNetBanking = rl_net_banking;*/
            }else{
               /* if(preCreditCard != null)
                preCreditCard.setVisibility(View.GONE);
                if(preButtonPay != null)
                preButtonPay.setVisibility(View.GONE);
                if(preRlNetBanking != null){
                    preRlNetBanking.setVisibility(View.GONE);
                }
                if(preRecyclerView != null){
                    preRecyclerView.setVisibility(View.GONE);
                    preButtonPay.setVisibility(View.GONE);
                }*/
            }
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType){
            case 0:
                View v0 = inflater.inflate(R.layout.payment_option_item_layout, parent, false);
                viewHolder = new MyHomeHeaderViewHolder(v0);
                break;
            default:
                View v = inflater.inflate(R.layout.payment_option_item_layout, parent, false);
                viewHolder = new MyHomeHeaderViewHolder(v);
                break;
        }

        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MyHomeHeaderViewHolder){
            MyHomeHeaderViewHolder myViewHolder = (MyHomeHeaderViewHolder)holder;
            PaymentOption item = itemList.get(position);
            myViewHolder.textHeader.setText(item.getTitle());

            myViewHolder.recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(context);
            // staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            myViewHolder.recyclerView.setLayoutManager(layoutManager);
            myViewHolder.recyclerView.setItemAnimator(new DefaultItemAnimator());
            ArrayList<CardTypeDTO> cardTypeDTOAllOthersArrayList = cardsList.get(item.getId());
            ArrayList<CardTypeDTO> cardTypeDTOArrayList = new ArrayList<>();
            if(cardTypeDTOAllOthersArrayList == null){
                cardTypeDTOAllOthersArrayList = new ArrayList<>();
            }

            List<String> cardNameList = new ArrayList();

            if(item.getId().equals("OPTNBK")){
                cardNameList.add(0,"Select Bank");
                myViewHolder.tv_select_bank_label.setText("All Other Banks");
            }else if(item.getId().equals("OPTWLT")){
                cardNameList.add(0,"Select Wallet");
                myViewHolder.tv_select_bank_label.setText("All Other Wallets");
            }else if(item.getId().equals("OPTUPI")){
                cardNameList.add(0,"Select UPI");
                myViewHolder.tv_select_bank_label.setText("All Other UPI");
            }

            for(CardTypeDTO cardTypeDTO : cardTypeDTOAllOthersArrayList){
                cardNameList.add(cardTypeDTO.getCardName());

                if(item.getId().equals("OPTNBK")){
                    if(cardTypeDTO.getCardName().toLowerCase().contains("icici") ||
                            cardTypeDTO.getCardName().toLowerCase().contains("hdfc")  ||
                            cardTypeDTO.getCardName().toLowerCase().contains("state bank of india") ||
                            cardTypeDTO.getCardName().toLowerCase().contains("citi") ||
                            cardTypeDTO.getCardName().toLowerCase().contains("kotak")){
                        cardTypeDTOArrayList.add(cardTypeDTO);
                    }
                }else if(item.getId().equals("OPTWLT")){
                    if(cardTypeDTO.getCardName().toLowerCase().contains("freecharge") ||
                            cardTypeDTO.getCardName().toLowerCase().contains("mobikwik")  ||
                            cardTypeDTO.getCardName().toLowerCase().contains("jiomoney")){
                        cardTypeDTOArrayList.add(cardTypeDTO);
                    }
                }else if(item.getId().equals("OPTUPI")){

                    if(cardTypeDTO.getCardName().toLowerCase().contains("bhim") ||
                            cardTypeDTO.getCardName().toLowerCase().contains("phonepay")  ||
                            cardTypeDTO.getCardName().toLowerCase().contains("tez") ||
                            cardTypeDTO.getCardName().toLowerCase().contains("paytm") ||
                            cardTypeDTO.getCardName().toLowerCase().contains("whatsapp")){
                        cardTypeDTOArrayList.add(cardTypeDTO);
                    }
                }

            }

            CardTypeAdapter itemAdapter=new CardTypeAdapter(context,cardTypeDTOArrayList);
           // itemAdapter.setMyItemClickListener(context);
            myViewHolder.recyclerView.setAdapter(itemAdapter);
            myViewHolder.recyclerView.setNestedScrollingEnabled(false);

            ArrayAdapter<String>  cardAdapter = new ArrayAdapter<String>(context, R.layout.simple_dropdown_list_item, cardNameList){
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
                        tv.setTextColor(context.getResources().getColor(R.color.grey500));
                    }else{
                        if(isDarkTheme){
                            tv.setTextColor(context.getResources().getColor(R.color.white));
                        }else{
                            tv.setTextColor(context.getResources().getColor(R.color.primary_text_color));
                        }
                    }
                    return view;
                }
                @Override
                public View getDropDownView(int position, View convertView,
                                            ViewGroup parent) {
                    View view = super.getDropDownView(position, convertView, parent);
                    if(isDarkTheme){
                        view.setBackgroundColor(context.getResources().getColor(R.color.dark_color));
                    }else{
                        view.setBackgroundColor(context.getResources().getColor(R.color.white));
                    }
                    TextView tv = (TextView) view;
                    if(position == 0){
                        // Set the hint text color gray
                        tv.setTextColor(context.getResources().getColor(R.color.grey500));
                    }else{
                        if(isDarkTheme){
                            tv.setTextColor(context.getResources().getColor(R.color.white));
                        }else{
                            tv.setTextColor(context.getResources().getColor(R.color.primary_text_color));
                        }
                    }
                    tv.setPadding(20,20,20,20);
                    return view;
                }
            };

            myViewHolder.spinnerCardType.setAdapter(cardAdapter);

        }
    }



    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
