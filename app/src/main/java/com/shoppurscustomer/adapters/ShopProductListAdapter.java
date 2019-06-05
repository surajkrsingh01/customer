package com.shoppurscustomer.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.shoppurscustomer.R;
import com.shoppurscustomer.activities.ProductDetailActivity;
import com.shoppurscustomer.activities.ProductListActivity;
import com.shoppurscustomer.activities.ShopProductListActivity;
import com.shoppurscustomer.database.DbHelper;
import com.shoppurscustomer.models.MyProduct;
import com.shoppurscustomer.utilities.DialogAndToast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suraj kumar singh on 20-04-2019.
 */

public class ShopProductListAdapter extends RecyclerView.Adapter<ShopProductListAdapter.MyViewHolder> {
    private List<MyProduct> myProductsList = new ArrayList<>();
    private Context context;
    private DbHelper dbHelper;
    private String shopCode;

    public ShopProductListAdapter(Context context, List<MyProduct> myProducts) {
        super();
        this.context = context;
        this.myProductsList = myProducts;
        dbHelper = new DbHelper(context);
    }

    public void setShopCode(String code){
        this.shopCode = code;
    }

    @Override
    public ShopProductListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.product_list_item_layout, parent, false);
        return new ShopProductListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ShopProductListAdapter.MyViewHolder myViewHolder, final int position) {
        {
            final MyProduct item = (MyProduct) myProductsList.get(position);
            myViewHolder.textbarcode.setText(item.getCode());
            myViewHolder.textName.setText(item.getName()+", "+item.getName());
            myViewHolder.textMrp.setText("MRP: Rs"+item.getMrp());
            if(dbHelper.isProductInCart(item.getCode(), shopCode)){
                myViewHolder.btnAddCart.setVisibility(View.GONE);
                myViewHolder.linear_plus_minus.setVisibility(View.VISIBLE);
                myViewHolder.tv_cartCount.setText(String.valueOf(dbHelper.getProductQuantity(item.getCode(), shopCode)));
            }else {
                myViewHolder.tv_cartCount.setText(String.valueOf(1));
                myViewHolder.linear_plus_minus.setVisibility(View.GONE);
                myViewHolder.btnAddCart.setVisibility(View.VISIBLE);
            }

            myViewHolder.btnAddCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myViewHolder.linear_plus_minus.setVisibility(View.VISIBLE);
                    myViewHolder.btnAddCart.setVisibility(View.GONE);
                    ((ShopProductListActivity)context).add_toCart(item);
                    DialogAndToast.showToast("Add to Cart ", context);
                }
            });
            myViewHolder.btn_minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int count = Integer.parseInt(myViewHolder.tv_cartCount.getText().toString());
                    if(count==1){
                        myViewHolder.linear_plus_minus.setVisibility(View.GONE);
                        myViewHolder.btnAddCart.setVisibility(View.VISIBLE);
                        ((ShopProductListActivity)context).removeCart(item);
                    }else {
                        myViewHolder.tv_cartCount.setText(String.valueOf(count-1));
                        ((ShopProductListActivity)context).updateCart(item,count-1 );
                    }
                }
            });
            myViewHolder.btn_plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int count = Integer.parseInt(myViewHolder.tv_cartCount.getText().toString());
                    myViewHolder.tv_cartCount.setText(String.valueOf(count+1));
                    ((ShopProductListActivity)context).updateCart(item,count+1 );
                }
            });

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
            // requestOptions.override(Utility.dpToPx(150, context), Utility.dpToPx(150, context));
            requestOptions.centerCrop();
            requestOptions.skipMemoryCache(true);

            Glide.with(context)
                    .load(item.getLocalImage())
                    .apply(requestOptions)
                    .into(myViewHolder.imageView);


        }
    }

    @Override
    public int getItemCount() {
        if (myProductsList != null) {
            return myProductsList.size();
        }
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView textName,textMrp, textbarcode, tv_cartCount;
        private ImageView imageView;
        private View rootView;
        private Button btnAddCart, btn_plus, btn_minus;
        private LinearLayout linear_plus_minus;

        public MyViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            textbarcode = itemView.findViewById(R.id.text_bar_code);
            textName=itemView.findViewById(R.id.text_name);
            textMrp=itemView.findViewById(R.id.text_mrp);
            imageView=itemView.findViewById(R.id.image_view);
            btnAddCart = itemView.findViewById(R.id.btn_addCart);
            linear_plus_minus = itemView.findViewById(R.id.linear_plus_minus);
            btn_plus = itemView.findViewById(R.id.btn_plus);
            btn_minus = itemView.findViewById(R.id.btn_minus);
            tv_cartCount = itemView.findViewById(R.id.tv_cartCount);
            rootView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v == rootView){
                MyProduct item = (MyProduct) myProductsList.get(getAdapterPosition());
                //Intent intent = new Intent(context,CartActivity.class);
                Intent intent = new Intent(context,ProductDetailActivity.class);
                intent.putExtra("code",item.getCode());
                intent.putExtra("barcode", item.getBarCode());
                intent.putExtra("price", item.getSellingPrice());
                intent.putExtra("subCatName",item.getSubCatName());
                intent.putExtra("productName",item.getName());
                intent.putExtra("productDesc",item.getDesc());
                intent.putExtra("productCode",item.getCode());
                intent.putExtra("productLocalImage",item.getLocalImage());
                context.startActivity(intent);
            }
        }
    }
}