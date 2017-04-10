package com.nguyenbnt.jsoupdemo.model;

/**
 * Created by 4000225 on 3/28/2017.
 */

public class DayInfo {
    public String dayTitle;
    public String weather;
    public String temp;
    public String doam;
    public String wind;
    public String minTemp;
    public String maxTemp;
    public String imgWeather;

    public DayInfo(){
        this.dayTitle = "";
        this.weather = "";
        this.minTemp = "";
        this.maxTemp = "";
        this.imgWeather = "";
        this.temp = "";
        this.doam = "";
        this.wind = "";
    }

    public DayInfo(String dayTitle, String weather, String minTemp, String maxTemp, String imgWeather) {
        this.dayTitle = dayTitle;
        this.weather = weather;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.imgWeather = imgWeather;
        this.temp = "";
        this.doam = "";
        this.wind = "";
    }
}
