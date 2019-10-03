package com.shoppurs.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shoppurs.R;
import com.shoppurs.interfaces.MyItemTypeClickListener;
import com.shoppurs.models.FrequencyType;
import com.shoppurs.models.ProductFrequency;

import java.util.List;

public class FrequencyTypeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;
    private List<FrequencyType> mItemList;
    private String type;
    private MyItemTypeClickListener myItemClickListener;
    private int selectedIndex;

    public void setMyItemClickListener(MyItemTypeClickListener myItemClickListener) {
        this.myItemClickListener = myItemClickListener;
    }

    public FrequencyTypeAdapter(Context context, List<FrequencyType> itemList, String type) {
        this.context = context;
        this.mItemList = itemList;
        this.type = type;
    }

    public class MyFrequencyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView textName;

        public MyFrequencyViewHolder(View itemView){
            super(itemView);
            textName=itemView.findViewById(R.id.tv_name);

            if(type.equals("editFrequency"))
            textName.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            FrequencyType item = mItemList.get(getAdapterPosition());
            if(item.isSelected()){
                item.setSelected(false);
            }else{
                item.setSelected(true);
            }
            if(getAdapterPosition() != selectedIndex) {
                mItemList.get(selectedIndex).setSelected(false);
                notifyItemChanged(selectedIndex);
            }
            notifyItemChanged(getAdapterPosition());
            selectedIndex = getAdapterPosition();
            myItemClickListener.onItemClicked(getAdapterPosition(), 2);
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType){
            case 0:
                View v0 = inflater.inflate(R.layout.frequency_item_layout, parent, false);
                viewHolder = new MyFrequencyViewHolder(v0);
                break;
            default:
                View v = inflater.inflate(R.layout.frequency_item_layout, parent, false);
                viewHolder = new MyFrequencyViewHolder(v);
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
        if(holder instanceof MyFrequencyViewHolder){
            MyFrequencyViewHolder myViewHolder = (MyFrequencyViewHolder)holder;
            FrequencyType item = mItemList.get(position);
            myViewHolder.textName.setText(item.getName());

            if(type.equals("editFrequency")){
                if(item.isSelected()){
                    myViewHolder.textName.setBackgroundResource(R.drawable.accent_solid_round_corner_background);
                    myViewHolder.textName.setTextColor(context.getResources().getColor(R.color.white));
                }else{
                    myViewHolder.textName.setBackgroundResource(R.drawable.grey_stroke_white_round_corner_background);
                    myViewHolder.textName.setTextColor(context.getResources().getColor(R.color.secondary_text_color));
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        if(mItemList ==null)
            return 0;
        else
            return mItemList.size();
    }
}
