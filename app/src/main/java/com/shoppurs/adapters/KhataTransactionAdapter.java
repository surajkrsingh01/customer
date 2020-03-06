package com.shoppurs.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shoppurs.R;
import com.shoppurs.interfaces.MyItemClickListener;
import com.shoppurs.models.KhataTransaction;
import com.shoppurs.utilities.Utility;

import java.util.List;

public class KhataTransactionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<KhataTransaction> itemList;
    private Context context;
    private String type;

    private MyItemClickListener myItemClickListener;

    public void setMyItemClickListener(MyItemClickListener myItemClickListener) {
        this.myItemClickListener = myItemClickListener;
    }

    public KhataTransactionAdapter(Context context, List<KhataTransaction> itemList) {
        this.itemList = itemList;
        this.context=context;

    }

    public class MyHomeHeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView tvAmt,tvDate;
        private ImageView tvView;

        public MyHomeHeaderViewHolder(View itemView) {
            super(itemView);
            tvAmt=itemView.findViewById(R.id.tvAmt);
            tvDate=itemView.findViewById(R.id.tvDate);
            tvView=itemView.findViewById(R.id.tvView);
            RelativeLayout relative_message_layout = itemView.findViewById(R.id.relative_message_layout);
            // Utility.setColorFilter(relative_message_layout.getBackground(),context.getResources().getColor(R.color.red_500));
            tvView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            myItemClickListener.onItemClicked(getAdapterPosition());
        }
    }

    public class MyHomeDebitViewHolder extends RecyclerView.ViewHolder{

        private TextView tvAmt,tvDate,tvView,tvPaymentMode,tvTransaction, tvPaymentStatus;

        public MyHomeDebitViewHolder(View itemView) {
            super(itemView);
            tvAmt=itemView.findViewById(R.id.tvAmt);
            tvDate=itemView.findViewById(R.id.tvDate);
            tvPaymentMode=itemView.findViewById(R.id.tvPaymentMode);
            tvTransaction=itemView.findViewById(R.id.tvTransaction);
            tvPaymentStatus = itemView.findViewById(R.id.tvPaymentStatus);
            RelativeLayout relative_message_layout = itemView.findViewById(R.id.relative_message_layout);
            // Utility.setColorFilter(relative_message_layout.getBackground(),context.getResources().getColor(R.color.green500));
        }

    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType){
            case 0:
                View v0 = inflater.inflate(R.layout.khata_credit_item_layout, parent, false);
                viewHolder = new MyHomeHeaderViewHolder(v0);
                break;
            case 1:
                View v1 = inflater.inflate(R.layout.khata_debit_item_layout, parent, false);
                viewHolder = new MyHomeDebitViewHolder(v1);
                break;
            default:
                View v = inflater.inflate(R.layout.khata_credit_item_layout, parent, false);
                viewHolder = new MyHomeHeaderViewHolder(v);
                break;
        }

        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        KhataTransaction item = itemList.get(position);
        if(item.getPaymentTransactionType().contains("Credit"))
            return 0;
        else
            return 1;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MyHomeHeaderViewHolder){
            MyHomeHeaderViewHolder myViewHolder = (MyHomeHeaderViewHolder)holder;
            KhataTransaction item = itemList.get(position);
            myViewHolder.tvAmt.setText(String.format("%.02f",item.getPaymentAmount()));
            myViewHolder.tvDate.setText(Utility.parseDate(item.getCreatedDate(),"yyyy-MM-dd HH:mm:ss","dd MMM yyyy"));
        }else if(holder instanceof MyHomeDebitViewHolder){
            MyHomeDebitViewHolder myViewHolder = (MyHomeDebitViewHolder)holder;
            KhataTransaction item = itemList.get(position);
            myViewHolder.tvPaymentMode.setText(item.getPaymentPaymentMethod());
            myViewHolder.tvTransaction.setText(item.getPaymentTransactionId());
            myViewHolder.tvAmt.setText(String.format("%.02f",item.getPaymentAmount()));
            myViewHolder.tvDate.setText(Utility.parseDate(item.getCreatedDate(),"yyyy-MM-dd HH:mm:ss","dd MMM yyyy"));
            if(item.getPaymentStatus().equals("Approved")){
                myViewHolder.tvPaymentStatus.setTextColor(context.getResources().getColor(R.color.green500));
            }
            myViewHolder.tvPaymentStatus.setText(item.getPaymentStatus());
        }
    }



    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
