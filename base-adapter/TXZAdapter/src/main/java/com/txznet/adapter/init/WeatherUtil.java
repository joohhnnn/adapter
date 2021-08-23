package com.txznet.adapter.module;

import android.content.Intent;

import com.txznet.adapter.BaseInitModule;
import com.txznet.adapter.base.util.BroadCastUtil;
import com.txznet.adapter.base.util.LogUtil;
import com.txznet.sdk.TXZNetDataProvider;
import com.txznet.sdk.bean.WeatherData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Nickhu User on 2017/7/26.
 */

public class WeatherUtil  {
    private static final String TAG = WeatherUtil.class.getSimpleName();


    /**
     * 广播反馈天气数据
     */
    public static void sendWeatherData(){
        TXZNetDataProvider.getInstance().getWeatherInfo(
                new TXZNetDataProvider.NetDataCallback<WeatherData>() {

                    @Override
                    public void onResult(WeatherData weatherData) {
                        String data = getWeatherData(weatherData);
                        BroadCastUtil.sendBroadCast(1060, "weatherDatas", data);
                    }

                    @Override
                    public void onError(int arg0) {

                    }
                });
    }

    /**
     * 转换天气数据
     *
     * @param weatherData
     * @return
     */
    private static String getWeatherData(WeatherData weatherData) {
        JSONObject joData = new JSONObject();
        try {
            joData.put("cityCode", weatherData.cityCode);
            joData.put("cityName", weatherData.cityName);
            joData.put("updateTime", weatherData.updateTime.toString());
            JSONArray jaWeatherDays = new JSONArray();
            for (int i = 0; i < weatherData.weatherDays.length; i++) {
                WeatherData.WeatherDay weatherDay = weatherData.weatherDays[i];
                JSONObject joDay = new JSONObject();
                joDay.put("currentTemperature", weatherDay.currentTemperature);
                joDay.put("day", weatherDay.day);
                joDay.put("dayOfWeek", weatherDay.dayOfWeek);
                joDay.put("highestTemperature", weatherDay.highestTemperature);
                joDay.put("lowestTemperature", weatherDay.lowestTemperature);
                joDay.put("month", weatherDay.month);
                joDay.put("pm2_5", weatherDay.pm2_5);
                joDay.put("year", weatherDay.year);
                joDay.put("carWashIndex", weatherDay.carWashIndex);
                joDay.put("carWashIndexDesc", weatherDay.carWashIndexDesc);
                joDay.put("coldIndex", weatherDay.coldIndex);
                joDay.put("coldIndexDesc", weatherDay.coldIndexDesc);
                joDay.put("comfortIndex", weatherDay.comfortIndex);
                joDay.put("comfortIndexDesc", weatherDay.comfortIndexDesc);
                joDay.put("datingIndex", weatherDay.datingIndex);
                joDay.put("datingIndexDesc", weatherDay.datingIndexDesc);
                joDay.put("dressIndex", weatherDay.dressIndex);
                joDay.put("dressIndexDesc", weatherDay.dressIndexDesc);
                joDay.put("dryingIndex", weatherDay.dryingIndex);
                joDay.put("dryingIndexDesc", weatherDay.dryingIndexDesc);
                joDay.put("morningExerciseIndex",
                        weatherDay.morningExerciseIndex);
                joDay.put("morningExerciseIndexDesc",
                        weatherDay.morningExerciseIndexDesc);
                joDay.put("quality", weatherDay.quality);
                joDay.put("sportIndex", weatherDay.sportIndex);
                joDay.put("sportIndexDesc", weatherDay.sportIndexDesc);
                joDay.put("suggest", weatherDay.suggest);
                joDay.put("sunBlockIndex", weatherDay.sunBlockIndex);
                joDay.put("sunBlockIndexDesc", weatherDay.sunBlockIndexDesc);
                joDay.put("travelIndex", weatherDay.travelIndex);
                joDay.put("travelIndexDesc", weatherDay.travelIndexDesc);
                joDay.put("umbrellaIndex", weatherDay.umbrellaIndex);
                joDay.put("umbrellaIndexDesc", weatherDay.umbrellaIndexDesc);
                joDay.put("weather", weatherDay.weather);
                joDay.put("wind", weatherDay.wind);
                jaWeatherDays.put(joDay);
            }
            joData.put("weatherDays", jaWeatherDays);
            LogUtil.d(TAG, "weatherDays ::" + joData.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
        return joData.toString();
    }
}
