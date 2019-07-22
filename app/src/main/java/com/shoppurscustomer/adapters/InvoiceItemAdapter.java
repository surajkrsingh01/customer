package com.shoppurscustomer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.shoppurscustomer.R;
import com.shoppurscustomer.models.InvoiceItem;
import com.shoppurscustomer.utilities.Utility;

import java.util.List;

public class InvoiceItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<InvoiceItem> itemList;
    private Context context;

    public InvoiceItemAdapter(Context context, List<InvoiceItem> itemList) {
        this.itemList = itemList;
        this.context=context;

    }

    public class MyHomeHeaderViewHolder extends RecyclerView.ViewHolder{

        private TextView tvName,tvQty,tvHsn,tvMrp,tvRate,tvAmt;

        public MyHomeHeaderViewHolder(View itemView) {
            super(itemView);
            tvName=itemView.findViewById(R.id.text_name);
            tvQty=itemView.findViewById(R.id.text_qty);
            tvMrp=itemView.findViewById(R.id.text_mrp);
            tvRate=itemView.findViewById(R.id.text_rate);
            tvAmt=itemView.findViewById(R.id.text_amount);
            //tvGst=itemView.findViewById(R.id.text_gst);
            tvHsn=itemView.findViewById(R.id.text_hsn);
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType){
            case 0:
                View v0 = inflater.inflate(R.layout.invoice_product_item_layout, parent, false);
                viewHolder = new MyHomeHeaderViewHolder(v0);
                break;
            default:
                View v = inflater.inflate(R.layout.invoice_product_item_layout, parent, false);
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
            InvoiceItem item= itemList.get(position);
            myViewHolder.tvName.setText(item.getItemName());
            myViewHolder.tvHsn.setText(item.getHsn());
            if(item.getUnit() == null || item.getUnit().toLowerCase().equals("null") || item.getUnit().equals("")){
                myViewHolder.tvQty.setText(""+item.getQty());
            }else{
              String[] unitArray = item.getUnit().split(" ");
              float totalUnit = Float.parseFloat(unitArray[0]) * item.getQty();
              myViewHolder.tvQty.setText(String.format("%.00f",totalUnit)+" "+unitArray[1]);
            }
            myViewHolder.tvMrp.setText(Utility.numberFormat(item.getMrp()));
            myViewHolder.tvRate.setText(Utility.numberFormat(item.getRate()));
          //  myViewHolder.tvGst.setText(Utility.numberFormat(item.getGst())+"%");
            float amt = item.getQty() * item.getRate();
            myViewHolder.tvAmt.setText(Utility.numberFormat(amt));

            if(item.getFreeItems() > 0){
                if(item.getOfferType().equals("free")){
                    if(item.getFreeItems() == 1){
                        myViewHolder.tvName.setText(item.getItemName()+" ("+item.getFreeItems()+" free item)");
                    }else{
                        myViewHolder.tvName.setText(item.getItemName()+" ("+item.getFreeItems()+" free items)");
                    }

                }
            }

        }
    }



    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
