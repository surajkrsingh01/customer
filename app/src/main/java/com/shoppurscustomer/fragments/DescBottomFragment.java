package com.shoppurscustomer.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.shoppurscustomer.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DescBottomFragment extends BottomSheetDialogFragment {

    private String desc;

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public DescBottomFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_desc_bottom, container, false);
        ImageView iv_clear = rootView.findViewById(R.id.iv_clear);
        TextView tv_desc = rootView.findViewById(R.id.tv_desc);

        iv_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DescBottomFragment.this.dismiss();
            }
        });

        tv_desc.setText(desc);


        return rootView;
    }

}
