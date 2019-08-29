package com.shoppurscustomer.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.shoppurscustomer.R;
import com.shoppurscustomer.activities.ShopProductListActivity;
import com.shoppurscustomer.models.Category;
import com.shoppurscustomer.utilities.DialogAndToast;

import java.util.List;

/**
 * Created by suraj kumar singh on 18-04-2019.
 */

public class AppliedCouponsAdapter extends RecyclerView.Adapter<AppliedCouponsAdapter.MyViewHolder> {
    private Context context;
    private List<Category> categoryList;
    private int selectedIndex;

    public AppliedCouponsAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @Override
    public AppliedCouponsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_categories, parent, false);
        return new AppliedCouponsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AppliedCouponsAdapter.MyViewHolder myViewHolder, final int position) {
        {
            Category item = categoryList.get(position);
            myViewHolder.tvCategoryName.setText(item.getName());
            Log.d("TopCategoiesAdapter", item.isSelected()+"");
            if(item.isSelected()){
                myViewHolder.relative_category.setBackground(ContextCompat.getDrawable(context,R.drawable.orange_solid_small_round_corner_background));
                myViewHolder.tvCategoryName.setTextColor(context.getResources().getColor(R.color.white));
               // myViewHolder.iv_options.setVisibility(View.VISIBLE);

            }else{
                myViewHolder.relative_category.setBackground(ContextCompat.getDrawable(context,R.drawable.white_solid_small_round_corner_background));
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
