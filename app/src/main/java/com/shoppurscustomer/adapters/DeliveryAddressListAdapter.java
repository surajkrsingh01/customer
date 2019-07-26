package com.shoppurscustomer.adapters;

import android.content.Context;
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
            myViewHolder.text_house.setText(deliveryAddress.getHouseNo());
            myViewHolder.text_address.setText(deliveryAddress.getAddress());
            myViewHolder.text_landmark.setText(deliveryAddress.getLandmark());
            myViewHolder.text_city_state_pin.setText(deliveryAddress.getCity() +", "+deliveryAddress.getState()+", " +deliveryAddress.getPinCode());
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
            text_name = itemView.findViewById(R.id.text_name);
            text_house = itemView.findViewById(R.id.text_house);
            text_address = itemView.findViewById(R.id.text_address);
            text_landmark = itemView.findViewById(R.id.text_landmark);
            text_city_state_pin=itemView.findViewById(R.id.text_city_state_pin);
            text_mobile = itemView.findViewById(R.id.text_mobile);
            btn_edit = itemView.findViewById(R.id.btn_edit);
            btn_delete = itemView.findViewById(R.id.btn_delete);
            checkBox_default_address = itemView.findViewById(R.id.checkbox_default_address);
            checkBox_default_address.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    DialogAndToast.showDialog("Want to use as default address", context);
                }
            });
        }

        @Override
        public void onClick(View v) {

        }
    }
}
