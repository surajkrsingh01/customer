package com.shoppurscustomer.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.shoppurscustomer.models.MyShop;
import com.shoppurscustomer.utilities.Constants;
import com.shoppurscustomer.utilities.DialogAndToast;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by suraj kumar singh on 18-04-2019.
 */

public class SearchShopAdapter extends RecyclerView.Adapter<SearchShopAdapter.MyViewHolder> {
    private Context context;
    private List<MyShop> mShopList;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String subCatid, subCatName;

    public String getSubCatid() {
        return subCatid;
    }

    public void setSubCatid(String subCatid) {
        this.subCatid = subCatid;
    }

    public String getSubCatName() {
        return subCatName;
    }

    public void setSubCatName(String subCatName) {
        this.subCatName = subCatName;
    }

    public SearchShopAdapter(Context context, List<MyShop> myShops) {
        this.context = context;
        this.mShopList = myShops;
        sharedPreferences= context.getSharedPreferences(Constants.MYPREFERENCEKEY,MODE_PRIVATE);
        editor=sharedPreferences.edit();
    }

    @Override
    public SearchShopAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.shop_list_item_layout, parent, false);
        return new SearchShopAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SearchShopAdapter.MyViewHolder holder, final int position) {
        {
            MyShop item = (MyShop) mShopList.get(position);
            SearchShopAdapter.MyViewHolder myViewHolder = (SearchShopAdapter.MyViewHolder)holder;
            myViewHolder.textCustName.setText(item.getName());
            myViewHolder.text_shop_mobile.setText(item.getMobile());
            myViewHolder.textAddress.setText(item.getAddress());
            //myViewHolder.textStateCity.setText(item.getState()+", "+item.getCity());

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
            requestOptions.dontTransform();
            // requestOptions.override(Utility.dpToPx(150, context), Utility.dpToPx(150, context));
            // requestOptions.centerCrop();
            requestOptions.skipMemoryCache(true);

            Glide.with(context)
                    .load(item.getImage())
                    .apply(requestOptions)
                    .into(myViewHolder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        if (mShopList != null) {
            return mShopList.size();
        }
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView textCustName,text_shop_mobile, textAddress, textStateCity;
        private ImageView imageView;
        private View rootView;


        public MyViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            textCustName=itemView.findViewById(R.id.text_customer_name);
            text_shop_mobile = itemView.findViewById(R.id.text_shop_mobile);
            textAddress=itemView.findViewById(R.id.text_address);
            textStateCity=itemView.findViewById(R.id.text_state_city);
            imageView=itemView.findViewById(R.id.image_view);
            rootView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v == rootView){
                MyShop shop = mShopList.get(getAdapterPosition());
                Intent intent = new Intent(context,ShopProductListActivity.class);
                intent.putExtra("shopCode",shop.getId());
                intent.putExtra("shopName",shop.getName());
                intent.putExtra("address",shop.getAddress());
                intent.putExtra("mobile", shop.getMobile());
                intent.putExtra("subCatId",subCatid);
                intent.putExtra("subCatName",subCatName);
                editor.putString(Constants.SHOP_INSIDE,shop.getId());
                editor.putString(Constants.SHOP_INSIDE_NAME, shop.getName());
                editor.commit();
                context.startActivity(intent);
            }
        }
    }
}
