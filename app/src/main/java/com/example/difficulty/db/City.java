package com.example.difficulty.db;

import org.litepal.crud.DataSupport;

public class City extends DataSupport {

    private int id;
    private int cityCode;
    private int provinceId;
    private String cityame;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public String getCityame() {
        return cityame;
    }

    public void setCityame(String cityame) {
        this.cityame = cityame;
    }
}
