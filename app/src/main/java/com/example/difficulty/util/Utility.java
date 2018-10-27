package com.example.difficulty.util;

import android.text.TextUtils;

import com.example.difficulty.db.City;
import com.example.difficulty.db.County;
import com.example.difficulty.db.Province;
import com.example.difficulty.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

public class Utility {

    public static boolean handleProvinceResponse(String response){
        if (!TextUtils.isEmpty(response)){
            try{
                JSONArray allProvince = new JSONArray(response);
                for (int i = 0;i<allProvince.length();i++){
                    JSONObject provinceObject = allProvince.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }
                return true;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return  false;
    }
    public static boolean handleCityResponse(String response,int ProvinceId){
        if (!TextUtils.isEmpty(response)){
            try{
                JSONArray allCities = new JSONArray(response);
                for (int i =0 ;i<allCities.length();i++){
                    JSONObject ciytObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityame(ciytObject.getString("name"));
                    city.setCityCode(ciytObject.getInt("id"));
                    city.setProvinceId(ProvinceId);
                    city.save();
                }
                return true;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }

    public  static boolean handleCountyResponse(String response,int cityId){
        if (!TextUtils.isEmpty(response)){
            try{
                JSONArray allCounties = new JSONArray(response);
            for (int i = 0 ; i <allCounties.length();i++){
                    JSONObject  countyObject = allCounties.getJSONObject(i);
//                    City city = new City();
                    County country = new County() ; // Country == County
                    country.setCountyName(countyObject.getString("name"));
                    country.setWeatherId(countyObject.getString("weather_id"));
//                    country.setCityId(countyObject.getInt("id"));
                    country.setCityId(cityId);
                    country.save();
                }
                return true;
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }

    public static Weather handleWeatherResponse (String response){
        try{
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent , Weather.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
