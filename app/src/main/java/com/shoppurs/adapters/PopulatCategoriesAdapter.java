package com.shoppurs.adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;

import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shoppurs.R;
import com.shoppurs.activities.SearchActivity;
import com.shoppurs.models.Category;

import java.util.List;

/**
 * Created by suraj kumar singh on 18-04-2019.
 */

public class PopulatCategoriesAdapter extends RecyclerView.Adapter<PopulatCategoriesAdapter.MyViewHolder> {
    private Context context;
    private List<Category> categoryList;
    private boolean isDarkTheme;
    private int themeColor;

    public void setTheme(boolean isDark, int themeColor){
        isDarkTheme = isDark;
        this.themeColor = themeColor;
    }

    public PopulatCategoriesAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @Override
    public PopulatCategoriesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_popular_categories, parent, false);
        return new PopulatCategoriesAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PopulatCategoriesAdapter.MyViewHolder myViewHolder, final int position) {
        {
            Category item = categoryList.get(position);
            myViewHolder.tvCategoryName.setText(item.getName());
            Log.d("TopCategoiesAdapter", item.isSelected()+"");

            if(isDarkTheme){
                ((GradientDrawable)myViewHolder.relative_category.getBackground()).setColor(themeColor);
                myViewHolder.tvCategoryName.setTextColor(context.getResources().getColor(R.color.white));
            }else {
                ((GradientDrawable)myViewHolder.relative_category.getBackground()).setColor(themeColor);
                myViewHolder.tvCategoryName.setTextColor(context.getResources().getColor(R.color.white));
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
                Category item = categoryList.get(getAdapterPosition());
                ((SearchActivity) (context)).setEt_search(String.valueOf(item.getName()), item.getId());
            }
        }
    }
}
