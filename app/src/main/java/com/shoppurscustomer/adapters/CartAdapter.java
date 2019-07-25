package com.shoppurscustomer.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.shoppurscustomer.R;
import com.shoppurscustomer.activities.CartActivity;
import com.shoppurscustomer.interfaces.MyItemTypeClickListener;
import com.shoppurscustomer.models.MyProduct;
import com.shoppurscustomer.models.ProductComboOffer;
import com.shoppurscustomer.models.ProductDiscountOffer;
import com.shoppurscustomer.models.ProductPriceOffer;
import com.shoppurscustomer.models.ProductUnit;
import com.shoppurscustomer.utilities.Utility;


import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<MyProduct> itemList;
    private Context context;
    private boolean isDarkTheme;

    public void setDarkTheme(boolean darkTheme) {
        isDarkTheme = darkTheme;
    }

    private MyItemTypeClickListener myItemTypeClickListener;

    public void setMyItemTypeClickListener(MyItemTypeClickListener myItemTypeClickListener) {
        this.myItemTypeClickListener = myItemTypeClickListener;
    }

    public CartAdapter(Context context, List<MyProduct> itemList) {
        this.itemList = itemList;
        this.context=context;

    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView textBarCode,textName,textMrp,textSp,textOffPer,textCounter,textOffer;
        private ImageView imageView,imageViewMinus,imageViewAdd;
        private RelativeLayout rlOffer;
        private RelativeLayout relativeLayoutUnit;
        private Spinner spinnerUnit;
        private View viewSeparator;

        public MyViewHolder(View itemView) {
            super(itemView);
            //textBarCode=itemView.findViewById(R.id.text_bar_code);
            textName=itemView.findViewById(R.id.text_name);
            textMrp=itemView.findViewById(R.id.text_mrp);
            textSp=itemView.findViewById(R.id.text_sp);
            textOffPer=itemView.findViewById(R.id.text_off_percentage);
            textCounter=itemView.findViewById(R.id.tv_counter);
            imageViewMinus=itemView.findViewById(R.id.image_minus);
            imageViewAdd=itemView.findViewById(R.id.image_add);
            imageView=itemView.findViewById(R.id.image_view);
            viewSeparator=itemView.findViewById(R.id.view_separator);
            textOffer=itemView.findViewById(R.id.text_offer);
            rlOffer=itemView.findViewById(R.id.relative_offer);
            relativeLayoutUnit=itemView.findViewById(R.id.relative_unit);
            spinnerUnit=itemView.findViewById(R.id.spinner_unit);

             textMrp.setPaintFlags(textMrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            imageViewAdd.setOnClickListener(this);
            imageViewMinus.setOnClickListener(this);
            rlOffer.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(view == imageViewAdd){
                myItemTypeClickListener.onItemClicked(getAdapterPosition(),2);
            }else if(view == rlOffer){
                ((CartActivity)context).showOfferDescription(itemList.get(getAdapterPosition()));
                //myItemTypeClickListener.onItemClicked(getAdapterPosition(),3);
            }else{
                myItemTypeClickListener.onItemClicked(getAdapterPosition(),1);
            }
        }
    }

    public class MyFreeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView textName,textFreeItem,textOffer;
        private RelativeLayout relativeLayoutUnit;
        private Spinner spinnerUnit;
        private ImageView imageView;

        public MyFreeViewHolder(View itemView) {
            super(itemView);
            //textBarCode=itemView.findViewById(R.id.text_bar_code);
            textName=itemView.findViewById(R.id.text_name);
            imageView=itemView.findViewById(R.id.image_view);
            textFreeItem=itemView.findViewById(R.id.text_free_item);
            textOffer=itemView.findViewById(R.id.text_offer);
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
                View v0 = inflater.inflate(R.layout.cart_item_layout, parent, false);
                viewHolder = new MyViewHolder(v0);
                break;
            case 1:
                View v1 = inflater.inflate(R.layout.cart_free_item_layout, parent, false);
                viewHolder = new MyFreeViewHolder(v1);
                break;
            default:
                View v = inflater.inflate(R.layout.cart_item_layout, parent, false);
                viewHolder = new MyViewHolder(v);
                break;
        }

        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        MyProduct item = itemList.get(position);
        if(((MyProduct) item).getSellingPrice() == 0f){
            return 1;
        }else{
            return 0;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final MyProduct item = (MyProduct) itemList.get(position);
        if(holder instanceof MyViewHolder){
            MyViewHolder myViewHolder = (MyViewHolder)holder;
            //  myViewHolder.textBarCode.setText(item.getProdBarCode());
            myViewHolder.textName.setText(item.getName());
            //myViewHolder.textAmount.setText("Rs. "+String.format("%.02f",item.getMrp()));
            myViewHolder.textSp.setText(Utility.numberFormat(item.getSellingPrice()));
            myViewHolder.textMrp.setText(Utility.numberFormat(item.getMrp()));
            myViewHolder.textCounter.setText(""+item.getQuantity());

            float diff = item.getMrp() - item.getSellingPrice();
            if(diff > 0f){
                float percentage = diff * 100 /item.getMrp();
                myViewHolder.textOffPer.setText(String.format("%.02f",percentage)+"% off");
                myViewHolder.textMrp.setVisibility(View.VISIBLE);
                myViewHolder.textOffPer.setVisibility(View.VISIBLE);
            }else{
                myViewHolder.textMrp.setVisibility(View.GONE);
                myViewHolder.textOffPer.setVisibility(View.GONE);
            }

            if(position == itemList.size() -1){
                myViewHolder.viewSeparator.setVisibility(View.GONE);
            }else{
                myViewHolder.viewSeparator.setVisibility(View.VISIBLE);
            }

            if(item.getProductPriceOffer() != null ) {
                ProductPriceOffer productPriceOffer = item.getProductPriceOffer();
                myViewHolder.textOffer.setText(productPriceOffer.getOfferName());
            }else if(item.getProductDiscountOffer() != null ){
                ProductDiscountOffer productDiscountOffer = item.getProductDiscountOffer();
                myViewHolder.textOffer.setText(productDiscountOffer.getOfferName());
            }
            if(item.getProductPriceOffer() == null  && item.getProductDiscountOffer() == null ){
                myViewHolder.rlOffer.setVisibility(View.GONE);
            }else myViewHolder.rlOffer.setVisibility(View.VISIBLE);

            if(item.getProductUnitList() != null && item.getProductUnitList().size() > 0){
                myViewHolder.relativeLayoutUnit.setVisibility(View.VISIBLE);
                List<String> unitList = new ArrayList<>();
                for(ProductUnit unit : item.getProductUnitList()){
                    unitList.add(unit.getUnitValue()+" "+unit.getUnitName());
                }
                ArrayAdapter<String> unitAdapter = new ArrayAdapter<String>(context, R.layout.simple_dropdown_list_item, unitList){
                    @Override
                    public boolean isEnabled(int position){
                        return true;
                    }
                    @Override
                    public View getView(int position, View convertView,
                                        ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView tv = (TextView) view;
                        if(isDarkTheme){
                            tv.setTextColor(context.getResources().getColor(R.color.white));
                        }else{
                            tv.setTextColor(context.getResources().getColor(R.color.primary_text_color));
                        }
                        return view;
                    }
                    @Override
                    public View getDropDownView(int position, View convertView,
                                                ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
                        if(isDarkTheme){
                            view.setBackgroundColor(context.getResources().getColor(R.color.dark_color));
                        }else{
                            view.setBackgroundColor(context.getResources().getColor(R.color.white));
                        }
                        TextView tv = (TextView) view;
                        if(isDarkTheme){
                            tv.setTextColor(context.getResources().getColor(R.color.white));
                        }else{
                            tv.setTextColor(context.getResources().getColor(R.color.primary_text_color));
                        }
                        tv.setPadding(20,20,20,20);
                        return view;
                    }
                };

                myViewHolder.spinnerUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        ProductUnit unit = item.getProductUnitList().get(i);
                        item.setUnit(unit.getUnitValue()+" "+unit.getUnitName());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                myViewHolder.spinnerUnit.setAdapter(unitAdapter);

                int unitPostion = unitAdapter.getPosition(item.getUnit());
                myViewHolder.spinnerUnit.setSelection(unitPostion);

            }else{
                myViewHolder.relativeLayoutUnit.setVisibility(View.GONE);
            }

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
            // requestOptions.override(Utility.dpToPx(150, context), Utility.dpToPx(150, context));
            requestOptions.centerCrop();
            requestOptions.skipMemoryCache(false);

            Glide.with(context)
                    .load(item.getProdImage1())
                    .apply(requestOptions)
                    .error(R.drawable.ic_photo_black_192dp)
                    .into(myViewHolder.imageView);

        }else if(holder instanceof MyFreeViewHolder){
            MyFreeViewHolder myViewHolder = (MyFreeViewHolder)holder;
            //  myViewHolder.textBarCode.setText(item.getProdBarCode());
            myViewHolder.textName.setText(item.getName());
            if(item.getQuantity() > 1){
                myViewHolder.textFreeItem.setText(item.getQuantity()+" free items!");
            }else{
                myViewHolder.textFreeItem.setText("1 free item!");
            }

            myViewHolder.textOffer.setText("offer applied");
        }

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
