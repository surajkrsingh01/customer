package com.shoppurscustomer.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.shoppurscustomer.R;
import com.shoppurscustomer.interfaces.MyItemClickListener;
import com.shoppurscustomer.models.MyReview;

import java.util.List;

public class MyReviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<MyReview> itemList;
    private Context context;
    private String type;

    private MyItemClickListener myItemClickListener;

    public void setMyItemClickListener(MyItemClickListener myItemClickListener) {
        this.myItemClickListener = myItemClickListener;
    }

    public MyReviewAdapter(Context context, List<MyReview> itemList, String type) {
        this.itemList = itemList;
        this.context=context;
        this.type = type;

    }

    public class MyHomeHeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView textHeader,textSubHeader,textDesc;
        private RatingBar ratingBar;

        public MyHomeHeaderViewHolder(View itemView) {
            super(itemView);
            textHeader=itemView.findViewById(R.id.text_header);
            textSubHeader=itemView.findViewById(R.id.text_sub_header);
            textDesc = itemView.findViewById(R.id.text_desc);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {


        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType){
            case 0:
                View v0 = inflater.inflate(R.layout.review_item_layout, parent, false);
                viewHolder = new MyHomeHeaderViewHolder(v0);
                break;
            default:
                View v = inflater.inflate(R.layout.review_item_layout, parent, false);
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
            MyReview item = itemList.get(position);
            MyHomeHeaderViewHolder myViewHolder = (MyHomeHeaderViewHolder)holder;
            myViewHolder.textHeader.setText(item.getUserName());
            myViewHolder.textSubHeader.setText(item.getDateTime());
            myViewHolder.textDesc.setText(item.getReview());
            myViewHolder.ratingBar.setRating(item.getRating());
        }
    }



    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
