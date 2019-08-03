package com.shoppurscustomer.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.shoppurscustomer.R;
import com.shoppurscustomer.activities.Settings.AddDeliveryAddressActivity;
import com.shoppurscustomer.activities.Settings.DeliveryAddressListActivity;
import com.shoppurscustomer.interfaces.MyItemClickListener;
import com.shoppurscustomer.models.Coupon;
import com.shoppurscustomer.models.DeliveryAddress;
import com.shoppurscustomer.utilities.DialogAndToast;

import java.util.List;

public class DeliveryAddressListAdapter extends RecyclerView.Adapter<DeliveryAddressListAdapter.MyViewHolder> {
    private List<DeliveryAddress> myItemList;
    private Context context;
    private int colorTheme;

    public void setColorTheme(int colorTheme) {
        this.colorTheme = colorTheme;
    }

    private MyItemClickListener myItemClickListener;

    public MyItemClickListener getMyItemClickListener() {
        return myItemClickListener;
    }

    public void setMyItemClickListener(MyItemClickListener myItemClickListener) {
        this.myItemClickListener = myItemClickListener;
    }

    public DeliveryAddressListAdapter(Context context, List<DeliveryAddress> myItemList) {
        super();
        this.context = context;
        this.myItemList = myItemList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.list_item_delivery_address, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder myViewHolder, final int position) {
        {
            DeliveryAddress deliveryAddress = (DeliveryAddress) myItemList.get(position);
            myViewHolder.text_name.setText(deliveryAddress.getName());
            myViewHolder.text_mobile.setText("Mobile:"+deliveryAddress.getMobile());
           // myViewHolder.text_house.setText(deliveryAddress.getHouseNo());
            myViewHolder.text_address.setText(deliveryAddress.getAddress());
            if(TextUtils.isEmpty(deliveryAddress.getLandmark())) {
                myViewHolder.text_landmark.setVisibility(View.GONE);
            }
            else {
                myViewHolder.text_landmark.setVisibility(View.VISIBLE);
                myViewHolder.text_landmark.setText("Near " + deliveryAddress.getLandmark());
            }
            myViewHolder.text_city_state_pin.setText(deliveryAddress.getCity() +", "+deliveryAddress.getState()+", " +deliveryAddress.getPinCode());
            if(deliveryAddress.getIsDefaultAddress()!=null && deliveryAddress.getIsDefaultAddress().equals("Yes")){
                myViewHolder.checkBox_default_address.setChecked(true);
            }else {
                myViewHolder.checkBox_default_address.setChecked(false);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (myItemList != null) {
            return myItemList.size();
        }
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView text_name,text_house, text_address, text_landmark,text_city_state_pin,text_mobile;
        private CheckBox checkBox_default_address;
        private ImageView btn_edit, btn_delete;


        public MyViewHolder(View itemView) {
            super(itemView);
            text_name = itemView.findViewById(R.id.text_cust_name);
            text_house = itemView.findViewById(R.id.text_house);
            text_address = itemView.findViewById(R.id.text_address);
            text_landmark = itemView.findViewById(R.id.text_landmark);
            text_city_state_pin=itemView.findViewById(R.id.text_city_state_pin);
            text_mobile = itemView.findViewById(R.id.text_mobile);
            btn_edit = itemView.findViewById(R.id.btn_edit);
            btn_delete = itemView.findViewById(R.id.btn_delete);
            checkBox_default_address = itemView.findViewById(R.id.checkbox_default_address);

            checkBox_default_address.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean checked = ((CheckBox) v).isChecked();
                    if (checked){
                        ((DeliveryAddressListActivity)context).showAlert("Delivery Address", "Do you want to use as Default Delivery Address?", "set_default", myItemList.get(getAdapterPosition()));
                    }
                    else{

                    }
                }
            });

            btn_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, AddDeliveryAddressActivity.class);
                    intent.putExtra("flag", "edit");
                    intent.putExtra("object", myItemList.get(getAdapterPosition()));
                    context.startActivity(intent);
                }
            });

            btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((DeliveryAddressListActivity)context).showAlert("Delivery Address", "Do you want to delete this address?", "delete", myItemList.get(getAdapterPosition()));
                    //((DeliveryAddressListActivity)context).deleteAddress(myItemList.get(getAdapterPosition()));
                }
            });
        }

        @Override
        public void onClick(View v) {

        }
    }
}
