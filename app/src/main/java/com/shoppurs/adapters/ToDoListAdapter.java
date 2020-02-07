package com.shoppurs.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.shoppurs.R;
import com.shoppurs.activities.Settings.ToDoListDetailsActivity;
import com.shoppurs.database.DbHelper;
import com.shoppurs.interfaces.MyItemClickListener;
import com.shoppurs.models.MyProduct;
import com.shoppurs.models.MyToDo;
import com.shoppurs.models.ProductDiscountOffer;
import com.shoppurs.models.ProductPriceOffer;
import com.shoppurs.models.ProductUnit;
import com.shoppurs.utilities.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suraj kumar singh on 20-04-2019.
 */

public class ToDoListAdapter extends RecyclerView.Adapter<ToDoListAdapter.MyViewHolder> {
    private List<MyToDo> myItems = new ArrayList<>();
    private Context context;
    private MyItemClickListener itemClickListener;
    private int colorTheme;

    public ToDoListAdapter(Context context, List<MyToDo> myItems, String shopCode, boolean isDarkTheme, Typeface typeface, int colorTheme) {
        super();
        this.context = context;
        this.myItems = myItems;
        this.colorTheme = colorTheme;
    }

    public void setItemClickListener(MyItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public ToDoListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.todo_list_item_layout, parent, false);
        return new ToDoListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ToDoListAdapter.MyViewHolder myViewHolder, final int position) {
        {
            final MyToDo item =  myItems.get(position);
            myViewHolder.textName.setText(item.getName());
        }
    }

    @Override
    public int getItemCount() {
        if (myItems != null) {
            return myItems.size();
        }
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView textName;
        private View rootView;
        private Button btn_remove;

        public MyViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            textName=itemView.findViewById(R.id.text_name);
            //textName.setTextColor(colorTheme);
            btn_remove = itemView.findViewById(R.id.btn_remove);
            rootView.setOnClickListener(this);
            rootView.setOnClickListener(this);
            btn_remove.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            MyToDo item = myItems.get(getAdapterPosition());
            if(v==rootView){
                itemClickListener.onItemClicked(getAdapterPosition(), "productList");
            }if(v == btn_remove){
                itemClickListener.onItemClicked(getAdapterPosition(), "remove");
            }
        }
    }
}