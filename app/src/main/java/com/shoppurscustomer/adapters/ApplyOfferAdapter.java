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
import com.shoppurscustomer.interfaces.MyItemTypeClickListener;
import com.shoppurscustomer.models.MyProduct;
import com.shoppurscustomer.models.ProductComboOffer;
import com.shoppurscustomer.models.ProductDiscountOffer;
import com.shoppurscustomer.models.ProductUnit;
import com.shoppurscustomer.utilities.DialogAndToast;
import com.shoppurscustomer.utilities.Utility;

import java.util.ArrayList;
import java.util.List;

public class ApplyOfferAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Object> myItemList = new ArrayList<>();
    private Context context;
    private int colorTheme;
    private boolean isDarkTheme;

    public void setDarkTheme(boolean darkTheme) {
        isDarkTheme = darkTheme;
    }

    private MyItemTypeClickListener myItemTypeClickListener;


    public void setMyItemTypeClickListener(MyItemTypeClickListener myItemTypeClickListener) {
        this.myItemTypeClickListener = myItemTypeClickListener;
    }

    public ApplyOfferAdapter(Context context, List<Object> myItemList) {
        super();
        this.context = context;
        this.myItemList = myItemList;
    }

    public void setColorTheme(int colorTheme){
        this.colorTheme = colorTheme;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.product_on_offer_item_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
       final MyProduct item = (MyProduct) myItemList.get(position);
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

        if(position == myItemList.size() -1){
            myViewHolder.viewSeparator.setVisibility(View.GONE);
        }else{
            myViewHolder.viewSeparator.setVisibility(View.VISIBLE);
        }

        Object ob = item.getProductOffer();

        if(ob instanceof ProductDiscountOffer){
            ProductDiscountOffer productDiscountOffer = (ProductDiscountOffer)ob;
            if(item.getOfferItemCounter() > 0){
                myViewHolder.textOffer.setText(item.getOfferItemCounter()+" free item - offer on "+item.getName());
            }else{
                myViewHolder.textOffer.setText(productDiscountOffer.getOfferName());
            }
        }else if(ob instanceof ProductComboOffer){
            ProductComboOffer productComboOffer = (ProductComboOffer)ob;
            myViewHolder.textOffer.setText(productComboOffer.getOfferName());
        }

        if(item.getProductUnitList() != null && item.getProductUnitList().size() > 0){
            myViewHolder.relativeLayoutUnit.setVisibility(View.VISIBLE);
            ;
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
    }


    @Override
    public int getItemCount() {
        if (myItemList != null) {
            return myItemList.size();
        }
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView textName,textMrp,textSp,textOffPer,textCounter,textOffer;
        private ImageView imageView,imageViewMinus,imageViewAdd;
        private RelativeLayout rlOffer;
        private RelativeLayout relativeLayoutUnit;
        private Spinner spinnerUnit;
        private View viewSeparator;

        public MyViewHolder(View itemView) {
            super(itemView);

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
                DialogAndToast.showDialog("Cannot Add this product ", context);
            //    myItemTypeClickListener.onItemClicked(getAdapterPosition(),2);
            }else if(view == rlOffer){
            //    myItemTypeClickListener.onItemClicked(getAdapterPosition(),3);
            }else{
               // DialogAndToast.showDialog("Cannot Add this product ", context);
             //   myItemTypeClickListener.onItemClicked(getAdapterPosition(),1);
            }
        }
    }
}
