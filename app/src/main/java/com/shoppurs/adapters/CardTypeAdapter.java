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
import com.shoppurs.activities.payment.ccavenue.utility.CardTypeDTO;
import com.shoppurs.interfaces.MyItemClickListener;
import com.shoppurs.utilities.Utility;

import java.util.List;

public class CardTypeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<CardTypeDTO> itemList;
    private Context context;
    private String type;
    boolean isDarkTheme;
    private int prePosition = -1;

    private MyItemClickListener myItemClickListener;

    public void setMyItemClickListener(MyItemClickListener myItemClickListener) {
        this.myItemClickListener = myItemClickListener;
    }

    public CardTypeAdapter(Context context, List<CardTypeDTO> itemList) {
        this.itemList = itemList;
        this.context=context;
    }

    public class MyHomeHeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView textHeader;
        private ImageView imageViewSelected;
        private RelativeLayout relativeLayoutContainer;

        public MyHomeHeaderViewHolder(View itemView) {
            super(itemView);
            textHeader=itemView.findViewById(R.id.text_name);
            imageViewSelected=itemView.findViewById(R.id.image_selected);
            relativeLayoutContainer = (RelativeLayout)itemView;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            CardTypeDTO item = (CardTypeDTO)itemList.get(getAdapterPosition());
            if(prePosition >= 0){
                CardTypeDTO preItem = (CardTypeDTO)itemList.get(prePosition);
                preItem.setSelected(false);
                notifyItemChanged(prePosition);
            }
            item.setSelected(true);
            prePosition = getAdapterPosition();
            notifyItemChanged(getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType){
            case 0:
                View v0 = inflater.inflate(R.layout.simple_list_item, parent, false);
                viewHolder = new MyHomeHeaderViewHolder(v0);
                break;
            default:
                View v = inflater.inflate(R.layout.simple_list_item, parent, false);
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
            CardTypeDTO item = (CardTypeDTO) itemList.get(position);
            MyHomeHeaderViewHolder myViewHolder = (MyHomeHeaderViewHolder)holder;
            myViewHolder.textHeader.setText(item.getCardName());
            if(item.isSelected()){
                myViewHolder.imageViewSelected.setVisibility(View.VISIBLE);
                if(isDarkTheme){
                    Utility.setColorFilter(myViewHolder.imageViewSelected.getDrawable(),
                            context.getResources().getColor(R.color.white));
                }else{
                    Utility.setColorFilter(myViewHolder.imageViewSelected.getDrawable(),
                            context.getResources().getColor(R.color.primary_text_color));
                }
            }else{
                myViewHolder.imageViewSelected.setVisibility(View.GONE);

            }
        }
    }



    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
