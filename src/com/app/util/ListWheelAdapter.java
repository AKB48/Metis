package com.app.util;


import java.util.ArrayList;




public class ListWheelAdapter <T>implements WheelAdapter{
    ArrayList<T> itemList;
    int length = -1;
    public ListWheelAdapter(ArrayList<T> itemList) {
        this.itemList = itemList;
        length = itemList.size();
    }

    @Override
    public String getItem(int index) {
        return itemList.get(index).toString();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getItemsCount() {
        return itemList.size();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getMaximumLength() {
        return length;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
