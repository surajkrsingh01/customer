package com.shoppurscustomer.fragments;


import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.shoppurscustomer.R;
import com.shoppurscustomer.adapters.OfferDescAdapter;
import com.shoppurscustomer.interfaces.MyItemTypeClickListener;
import com.shoppurscustomer.models.MyProduct;
import com.shoppurscustomer.models.ProductDiscountOffer;
import com.shoppurscustomer.models.ProductPriceDetails;
import com.shoppurscustomer.models.ProductPriceOffer;
import com.shoppurscustomer.utilities.Utility;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class OfferDescriptionFragment extends BottomSheetDialogFragment {

    private String TAG = "BottomSearchFragment";
    private int colorTheme;
    private MyProduct myProduct;
    private RecyclerView recyclerViewOfferDesc;
    private TextView tvOfferName;

    public void setColorTheme(int colorTheme) {
        this.colorTheme = colorTheme;
    }
    public OfferDescriptionFragment() {
        // Required empty public constructor
    }

    public void setProduct(MyProduct myProduct){
        this.myProduct  = myProduct;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_offer_desciption, container, false);
        RelativeLayout relative_search = rootView.findViewById(R.id.relative_header);
        //((GradientDrawable)relative_search.getBackground()).setColor(colorTheme);
        ImageView iv_clear = rootView.findViewById(R.id.iv_clear);
        tvOfferName = rootView.findViewById(R.id.text_offer_name);
        recyclerViewOfferDesc= rootView.findViewById(R.id.recycler_view_offer_desc);
        rootView.findViewById(R.id.relative_footer_action).setBackgroundColor(colorTheme);
        TextView tv = rootView.findViewById(R.id.text_action);
        tv.setText("OKAY! GOT IT");
        if(colorTheme == getResources().getColor(R.color.white)){
            tv.setTextColor(getResources().getColor(R.color.primary_text_color));
        }else{
            tv.setTextColor(getResources().getColor(R.color.white));
        }

        getOfferDetails();

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OfferDescriptionFragment.this.dismiss();
            }
        });
        iv_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OfferDescriptionFragment.this.dismiss();
            }
        });
        return rootView;
    }

    private void getOfferDetails(){
        String offerName = null;
        List<String> offerDescList = new ArrayList<>();
        if(myProduct.getProductPriceOffer()!=null) {
            ProductPriceOffer productPriceOffer = (ProductPriceOffer) myProduct.getProductPriceOffer();
            offerName = productPriceOffer.getOfferName();
            float totOfferAmt = 0f;
            for(ProductPriceDetails productPriceDetails : productPriceOffer.getProductPriceDetails()){
                totOfferAmt = totOfferAmt + productPriceDetails.getPcodPrice();
                offerDescList.add("Buy "+productPriceDetails.getPcodProdQty()+" at Rs "+
                        Utility.numberFormat(totOfferAmt));
            }
            offerDescList.add("Offer valid till "+Utility.parseDate(productPriceOffer.getEndDate(),"yyyy-MM-dd",
                    "EEE dd MMMM, yyyy")+" 23:59 PM");
        }else if(myProduct.getProductDiscountOffer() != null ) {
            ProductDiscountOffer productDiscountOffer = (ProductDiscountOffer) myProduct.getProductDiscountOffer();
            offerName = productDiscountOffer.getOfferName();
        }
        tvOfferName.setText(offerName);


        recyclerViewOfferDesc.setHasFixedSize(true);
        final RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getContext());
        recyclerViewOfferDesc.setLayoutManager(layoutManager);
        recyclerViewOfferDesc.setItemAnimator(new DefaultItemAnimator());
        OfferDescAdapter offerDescAdapter =new OfferDescAdapter(getContext(),offerDescList);
        recyclerViewOfferDesc.setAdapter(offerDescAdapter);
        recyclerViewOfferDesc.setNestedScrollingEnabled(false);
    }

}
