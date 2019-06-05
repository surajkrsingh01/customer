package com.shoppurscustomer.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shoppurscustomer.R;
import com.shoppurscustomer.models.Bar;
import com.shoppurscustomer.utilities.Utility;

import java.util.List;

/**
 * Created by ARIEON-7 on 27-07-2017.
 */

public class MonthlyGraphAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Bar> rowItems;
    private float totalTarget,barHeight;
    private int graphType,height;

    public int getGraphType() {
        return graphType;
    }

    public void setGraphType(int graphType) {
        this.graphType = graphType;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public float getTotalTarget() {
        return totalTarget;
    }

    public void setTotalTarget(float totalTarget) {
        this.totalTarget = totalTarget;
    }

    public float getBarHeight() {
        return barHeight;
    }

    public void setBarHeight(float barHeight) {
        this.barHeight = barHeight;
    }

    public class MyViewOneHolder extends RecyclerView.ViewHolder{//,HomeActivity.OnSendRequest {
        public TextView textMonth;
        private View viewSale;
        public View itemView;

        public MyViewOneHolder(View view) {
            super(view);
            textMonth = (TextView) view.findViewById(R.id.text_month);
            viewSale = view.findViewById(R.id.view_sale);
            // barHeight = view.getHeight();
        }

    }

    public class MyViewTwoHolder extends RecyclerView.ViewHolder{//,HomeActivity.OnSendRequest {
        public TextView textMonth;
        private View viewSale,viewTarget;
        public View itemView;

        public MyViewTwoHolder(View view) {
            super(view);
            textMonth = (TextView) view.findViewById(R.id.text_month);
            viewSale = view.findViewById(R.id.view_sale);
            viewTarget = view.findViewById(R.id.view_target);
            // barHeight = view.getHeight();
        }

    }

    public class MyViewThreeHolder extends RecyclerView.ViewHolder{//,HomeActivity.OnSendRequest {
        public TextView textMonth;
        private View viewSale,viewTarget,viewBudget;
        public View itemView;

        public MyViewThreeHolder(View view) {
            super(view);
            textMonth = (TextView) view.findViewById(R.id.text_month);
            viewSale = view.findViewById(R.id.view_sale);
            viewTarget = view.findViewById(R.id.view_target);
            viewBudget = view.findViewById(R.id.view_budget);
            // barHeight = view.getHeight();
        }

    }

    public MonthlyGraphAdapter(Context mContext, List<Bar> rowItems,int graphType) {
        this.mContext = mContext;
        this.rowItems = rowItems;
        this.graphType = graphType;
        barHeight = Utility.dpToPx(200,mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case 1:
                View v1 = inflater.inflate(R.layout.chart_one_item_layout, parent, false);
                viewHolder = new MyViewOneHolder(v1);
                break;
            case 2:
                View v2 = inflater.inflate(R.layout.chart_two_item_layout, parent, false);
                viewHolder = new MyViewTwoHolder(v2);
                break;
            case 3:
                View v3 = inflater.inflate(R.layout.chart_three_item_layout, parent, false);
                viewHolder = new MyViewThreeHolder(v3);
                break;
            default:
                View v = inflater.inflate(R.layout.chart_one_item_layout, parent, false);
                viewHolder = new MyViewOneHolder(v);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MyViewOneHolder){
            Bar bar = rowItems.get(position);
            ((MyViewOneHolder)holder).textMonth.setText(bar.getName());

            int amount = bar.getSaleValue();
            int currentBarHeight = (int) (((float) amount / (float) totalTarget) * barHeight);

            Log.i("Sale Target","Sale "+amount+" totalTarget "+totalTarget+" barHeigth "+barHeight);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)((MyViewOneHolder)holder).viewSale.getLayoutParams();
            params.height = currentBarHeight;
            ((MyViewOneHolder)holder).viewSale.setLayoutParams(params);

        }else if(holder instanceof MyViewTwoHolder){
            Bar bar = rowItems.get(position);
            ((MyViewTwoHolder)holder).textMonth.setText(bar.getName());

            int amount = bar.getSaleAchievedValue();
            int currentBarHeight = (int) (((float) amount / (float) totalTarget) * barHeight);

            Log.i("Sale Target","Sale "+amount+" totalTarget "+totalTarget+" barHeigth "+barHeight);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)((MyViewTwoHolder)holder).viewSale.getLayoutParams();
            params.height = currentBarHeight;
            ((MyViewTwoHolder)holder).viewSale.setLayoutParams(params);

            amount = bar.getSaleTargetValue();
            currentBarHeight = (int) (((float) amount / (float) totalTarget) * barHeight);

            RelativeLayout.LayoutParams paramsTarget = (RelativeLayout.LayoutParams)((MyViewTwoHolder)holder).viewTarget.getLayoutParams();
            paramsTarget.height = currentBarHeight;
            ((MyViewTwoHolder)holder).viewTarget.setLayoutParams(paramsTarget);
        }else {
            Bar bar = rowItems.get(position);
            ((MyViewThreeHolder)holder).textMonth.setText(bar.getName());

            int amount = bar.getSaleAchievedValue();
            int currentBarHeight = (int) (((float) amount / (float) totalTarget) * barHeight);

            Log.i("Sale Target","Sale "+amount+" totalTarget "+totalTarget+" barHeigth "+barHeight);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)((MyViewThreeHolder)holder).viewSale.getLayoutParams();
            params.height = currentBarHeight;
            ((MyViewThreeHolder)holder).viewSale.setLayoutParams(params);

            amount = bar.getSaleTargetValue();
            currentBarHeight = (int) (((float) amount / (float) totalTarget) * barHeight);

            RelativeLayout.LayoutParams paramsTarget = (RelativeLayout.LayoutParams)((MyViewThreeHolder)holder).viewTarget.getLayoutParams();
            paramsTarget.height = currentBarHeight;
            ((MyViewThreeHolder)holder).viewTarget.setLayoutParams(paramsTarget);

            amount = bar.getBudget();
            currentBarHeight = (int) (((float) amount / (float) totalTarget) * barHeight);

            RelativeLayout.LayoutParams paramsBudget = (RelativeLayout.LayoutParams)((MyViewThreeHolder)holder).viewBudget.getLayoutParams();
            paramsBudget.height = currentBarHeight;
            ((MyViewThreeHolder)holder).viewBudget.setLayoutParams(paramsBudget);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return graphType;
    }

    @Override
    public int getItemCount() {
        return rowItems.size();
    }

    private int dpToPx(float dp) {
        Resources r = mContext.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}
