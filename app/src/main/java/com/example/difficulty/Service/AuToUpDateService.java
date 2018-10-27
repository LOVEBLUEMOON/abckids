package com.example.difficulty.Service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.example.difficulty.gson.Weather;
import com.example.difficulty.util.HttpUtil;
import com.example.difficulty.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AuToUpDateService extends Service {

    public AuToUpDateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
       return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        upDateWeather();
        upDateBingPic();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 8*3600*1000;
        long time = SystemClock.elapsedRealtime()+anHour;
        Intent intent1 = new Intent(this ,AuToUpDateService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this,0,intent1,0);
        manager.cancel(pendingIntent);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,time,pendingIntent);
        return super.onStartCommand(intent, flags, startId);
    }
    public void upDateWeather(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = preferences.getString("weather",null);
        if (weatherString != null){
            final Weather weather = Utility.handleWeatherResponse(weatherString);
            String weatherId = weather.basic.weatherId;
            String weatherURl = "http://guolin.tech/api/weather?cityid="+
                    weatherId+"&key==ebe5a4623b8247aaab622d2074ddf71b";
            HttpUtil.sendOkHttpRequest(weatherURl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText = response.body().string();
                    Weather weather1 = Utility.handleWeatherResponse(responseText);
                    if (weather1!=null&&"ok".equals(weather.status)){
                        SharedPreferences.Editor editor = PreferenceManager.
                                getDefaultSharedPreferences(AuToUpDateService.this).edit();
                        editor.putString("weather",responseText);
                        editor.apply();
                    }
                }
            });
        }
    }
    public void upDateBingPic(){
        String  PictureURL = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(PictureURL, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.
                        getDefaultSharedPreferences(AuToUpDateService.this).edit();
                editor.putString("weather",responseText);
                editor.apply();
            }
        });
    }
}
