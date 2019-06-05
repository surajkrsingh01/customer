package com.shoppurscustomer.models;

import java.util.List;

public class CatListItem {
    private String title,desc;
    private int type;
    private boolean isSelectingAll;
    private List<Object> itemList;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<Object> getItemList() {
        return itemList;
    }

    public void setItemList(List<Object> itemList) {
        this.itemList = itemList;
    }

    public boolean isSelectingAll() {
        return isSelectingAll;
    }

    public void setSelectingAll(boolean selectingAll) {
        isSelectingAll = selectingAll;
    }
}
