package com.example.difficulty;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.difficulty.gson.Forecast;
import com.example.difficulty.gson.Weather;
import com.example.difficulty.util.HttpUtil;
import com.example.difficulty.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdataTime;
    private TextView degreeText;
    private TextView weatherInfoText ;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private ImageView bingPicImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdataTime = (TextView) findViewById(R.id.updata_time);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_text);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        aqiText = (TextView) findViewById(R.id.aqi_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        comfortText = (TextView) findViewById(R.id.comfor_text);
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        bingPicImg = (ImageView) findViewById(R.id.bing_pic_img);
        String bingpic = preferences.getString("bing_pic",null);
        if (bingpic != null){
            Glide.with(this).load(bingpic).into(bingPicImg);
        }else{
            loadBingPic();
        }
        String  weatherId = getIntent().getStringExtra("weather_id");
        weatherLayout.setVisibility(View.INVISIBLE);
        requestWeather(weatherId);
    }
    private void loadBingPic(){
        String requestPicture = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestPicture, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingpic = response.body().string();
//                SharedPreferences.Editor
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingpic).into(bingPicImg);
                    }
                });
            }
        });
    }
    public void  requestWeather(String weatherId){
        String weatherURL = "http://guolin.tech/api/weather?cityid="+
                weatherId+"&key=ebe5a4623b8247aaab622d2074ddf71b";
        HttpUtil.sendOkHttpRequest(weatherURL, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)){
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences
                                    (WeatherActivity.this).edit();
                            editor.putString("weather" , responseText);
                            editor.apply();
//                            Toast.makeText(WeatherActivity.this, weather.now.temperature, Toast.LENGTH_SHORT).show();
                            showWeatherInfo(weather);
                        }else{
                            Toast.makeText(WeatherActivity.this, "shibao", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
        private void showWeatherInfo(Weather weather){
                String cityName = weather.basic.cityName;
//                String updataTime = weather.basic.updata.updataTime.split(" ")[1];
                String degree = weather.now.temperature+"C";
                String weatherInfo = weather.now.more.info;
                titleCity.setText(cityName);
//                titleUpdataTime.setText(updataTime);
                degreeText.setText(degree);
                weatherInfoText.setText(weatherInfo);
                forecastLayout.removeAllViews();
                for (Forecast forecast : weather.forecastlist){
                    View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,
                            forecastLayout,false);
                    TextView dataText = (TextView) view.findViewById(R.id.data_text);
                    TextView infoText = (TextView) view.findViewById(R.id.info_text);
                    TextView maxText = (TextView) view.findViewById(R.id.max_text);
                    TextView minText = (TextView) view.findViewById(R.id.min_text);
                    dataText.setText(forecast.data);
                    infoText.setText(forecast.more.info);
                    maxText.setText(forecast.temperature.max);
                    minText.setText(forecast.temperature.min);
                    forecastLayout.addView(view);
                }
                if (weather.aqi != null){
                    aqiText.setText(weather.aqi.city.aqi);
                    pm25Text.setText(weather.aqi.city.pm25);
                }
                String comfor = weather.suggestion.comfort.info;
                String carWash = weather.suggestion.carWash.info;
//                String sport =  weather.suggestion.sports.info;
                comfortText.setText(comfor);
                carWashText.setText(carWash);
//               sportText.setText(sport);
               weatherLayout.setVisibility(View.VISIBLE);
            }
    }
