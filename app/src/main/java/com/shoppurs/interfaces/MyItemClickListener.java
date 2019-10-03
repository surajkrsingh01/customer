package com.shoppurs.interfaces;

import com.shoppurs.models.MyProduct;

public interface MyItemClickListener {
    void onItemClicked(int pos);
    void onProductSearch(MyProduct myProduct);
}
