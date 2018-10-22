package com.example.difficulty;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.difficulty.db.City;
import com.example.difficulty.db.County;
import com.example.difficulty.db.Province;
import com.example.difficulty.util.HttpUtil;
import com.example.difficulty.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChooseAreaFragment extends Fragment {

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CIYTY = 1;
    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;
    private TextView textView;
    private Button backButton;
    private ListView listView;

    private ArrayAdapter<String> adapter;
    private List<String> datalist = new ArrayList<>();
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countryList;

    private Province selectedprovince;
    private City selectedcity;
    private County selectedcountry;

    private  int currentLevel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.choose_area ,container ,false);
        textView = (TextView) view.findViewById(R.id.title_text);
        backButton = (Button) view.findViewById(R.id.back_button);
        backButton.setVisibility(View.GONE);
        listView = (ListView) view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(getContext() , android.R.layout.simple_list_item_1 , datalist);
        listView.setAdapter(adapter);
        return view;
    }
  @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
              if (currentLevel == LEVEL_PROVINCE){
                    selectedprovince = provinceList.get(position);
                    queryCity();
                }else if (currentLevel == LEVEL_CIYTY){
                    selectedcity = cityList.get(position);
                    queryCounty();
                }
            }
        });
       backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (currentLevel == LEVEL_COUNTY){
                    queryCity();
                }else if (currentLevel == LEVEL_CIYTY){
                    queryProvince();
                }
            }
        });
      queryProvince();
    }
   private void queryProvince (){
        textView.setText("中国");
        backButton.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size()>0){
            datalist.clear();
            for (Province province : provinceList){
                datalist.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        }else{
            String address = "http://guolin.tech/api/china";
            queryfromServer(address,"province");
        }
    }
    private void queryCity(){

        textView.setText(selectedprovince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceid = ?",
                String.valueOf(selectedprovince.getId())).find(City.class);
        if (cityList.size()>0){
            datalist.clear();
            for (City city : cityList){
                datalist.add(city.getCityame());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CIYTY;
        }else{
            int provinceCode = selectedprovince.getProvinceCode();
            String address = "http://guolin.tech/api/china/"+provinceCode;
            queryfromServer(address ,"city");
        }
    }
    private void queryCounty(){
        textView.setText(selectedcity.getCityame());
        backButton.setVisibility(View.VISIBLE);
        countryList = DataSupport.where(" cityid = ?",
                String.valueOf(selectedcity.getId())).find(County.class);
        if (countryList.size()>0){
            datalist.clear();
            for (County county : countryList){
                datalist.add(county.getCountyName());
            }
//            datalist.add("asdfg");
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        }else {
            int provinceCode = selectedprovince.getProvinceCode();
            int cityCode = selectedcity.getCityCode();
            String address = "http://guolin.tech/api/china/"+provinceCode+"//"+cityCode;
            queryfromServer(address , "county");
        }
    }
    private void queryfromServer(String address,final String type) {

        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(), "加载失败.....", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String responseText = response.body().string();
                boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.handleProvinceResponse(responseText);
                } else if ("city".equals(type)) {
                    result = Utility.handleCityResponse(responseText, selectedprovince.getId());
                } else if ("county".equals(type)) {
                    result = Utility.handleCountyResponse(responseText, selectedcity.getId());
                }
                if (result) {
                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryProvince();
                            }
                           else if ("city".equals(type)) {
                                queryCity();
                            } else if ("county".equals(type)) {
                                queryCounty();
                            }
                        }
                    });
                }
            }
        });
    }
    private void showProgressDialog(){

        if (progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载......");
            progressDialog.setCancelable(true);
        }
        progressDialog.show();
    }
    private void  closeProgressDialog(){
        if (progressDialog != null){
           progressDialog.dismiss();
        }

    }
    }