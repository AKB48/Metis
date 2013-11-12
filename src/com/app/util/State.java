package com.app.util;

import java.util.ArrayList;


public class State extends Object {

    private String name;
    private ArrayList<City> cityList;
    

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public ArrayList<City> getAreaList() {
        return cityList;
    }

    public void setAreaList(ArrayList<City> cityList) {
        this.cityList = cityList;
    }

    @Override
    public String toString() {
        return name;
    }
}
