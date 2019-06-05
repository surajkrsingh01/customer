package com.shoppurscustomer.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.shoppurscustomer.R;
import com.shoppurscustomer.activities.ShopProductListActivity;
import com.shoppurscustomer.models.Category;
import com.shoppurscustomer.models.MyProduct;
import com.shoppurscustomer.models.MyShop;
import com.shoppurscustomer.models.SubCategory;
import com.shoppurscustomer.utilities.Constants;
import com.shoppurscustomer.utilities.DialogAndToast;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by suraj kumar singh on 18-04-2019.
 */

public class TopCategoriesAdapter extends RecyclerView.Adapter<TopCategoriesAdapter.MyViewHolder> {
    private Context context;
    private List<Category> categoryList;
    private int selectedIndex;

    public TopCategoriesAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @Override
    public TopCategoriesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_categories, parent, false);
        return new TopCategoriesAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final TopCategoriesAdapter.MyViewHolder myViewHolder, final int position) {
        {
            Category item = categoryList.get(position);
            myViewHolder.tvCategoryName.setText(item.getName());
            Log.d("TopCategoiesAdapter", item.isSelected()+"");
            if(item.isSelected()){
                myViewHolder.relative_category.setBackground(ContextCompat.getDrawable(context,R.drawable.orange_solid_small_round_corner_background));
                myViewHolder.tvCategoryName.setTextColor(context.getResources().getColor(R.color.white));
               // myViewHolder.iv_options.setVisibility(View.VISIBLE);

            }else{
                myViewHolder.relative_category.setBackgroundColor(context.getResources().getColor(R.color.white));
                myViewHolder.tvCategoryName.setTextColor(context.getResources().getColor(R.color.primary_text_color));
                //myViewHolder.iv_options.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (categoryList != null) {
            return categoryList.size();
        }
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
         private TextView tvCategoryName;
         private RelativeLayout relative_category;
         private ImageView iv_options;


        public MyViewHolder(View itemView) {
            super(itemView);
            relative_category = itemView.findViewById(R.id.relative_category);
            tvCategoryName = itemView.findViewById(R.id.tv_Category);
            iv_options = itemView.findViewById(R.id.iv_options);
            relative_category.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v == relative_category){
                if(selectedIndex == getAdapterPosition()){

                }else {
                    categoryList.get(selectedIndex).setSelected(false);
                    Category item = categoryList.get(getAdapterPosition());
                    item.setSelected(true);
                    selectedIndex = getAdapterPosition();
                    DialogAndToast.showToast(item.getName(), context);
                    ((ShopProductListActivity) (context)).getProducts(String.valueOf(item.getId()), "onCategorySelected");
                    notifyDataSetChanged();
                }
            }
        }
    }
}
