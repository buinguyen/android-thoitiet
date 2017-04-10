package com.nguyenbnt.jsoupdemo;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.nguyenbnt.jsoupdemo.model.DayInfo;

/**
 * Created by 4000225 on 3/28/2017.
 */

public class Parser {
    private final String TAG = this.getClass().getSimpleName();

    private final String TODAY_WEATHER_TABLE_ID = "_ctl1__ctl0__ctl0_pnl_img";
    private final String TODAY_WEATHER_INFO_TABLE_ID = "_ctl1__ctl0__ctl0_pnl_infor";
    private final String WEATHER_TABLE_ID = "_ctl1__ctl0__ctl0_dl_Thoitietthanhpho";

    final String class_today_content = "forecast_detail";
    final String class_day_title = "tieude_dubao";
    final String class_day_yeuto = "tieude_dubao_yeuto";
    final String class_day_yeuto_content = "tieude_dubao_yeuto_content";
    final String class_table_image = "tableMap";

    private Context mContext;
    private Document mDocument;

    public Parser(Context context, Document document, @Nullable String tableName) {
        mContext = context;
        mDocument = document;
    }

    public DayInfo parseTodayWeather() {
        // get today weather table by id
        Elements tables = mDocument.select("table#" + TODAY_WEATHER_TABLE_ID);
        if (tables == null || tables.size() <= 0) {
            Log.e(TAG, "today tables NULL");
            return null;
        }
        Element table = tables.get(0); // select the first table.
        if (table == null) {
            Log.e(TAG, "today table NULL");
            return null;
        }

        // get all <img>
        String imgLink = "";
        Elements imgs = table.select("img");
        for (Element image : imgs) {
            if (image != null && image.hasAttr("abs:src")) {
                imgLink = image.attr("abs:src");
                Log.d(TAG, "today img src = " + imgLink);
                break;
            }
        }

        // get all <span>
        String weather = "";
        Elements spans = table.select("span");
        for (Element span : spans) {
            if (span != null && span.hasText()) {
                weather = span.text();
                Log.d(TAG, "today weather = " + weather);
                break;
            }
        }

        // get today info table by id
        tables = mDocument.select("table#" + TODAY_WEATHER_INFO_TABLE_ID);
        if (tables == null || tables.size() <= 0) {
            Log.e(TAG, "today tables NULL");
            return null;
        }
        table = tables.get(0); // select the first table.
        if (table == null) {
            Log.e(TAG, "today table NULL");
            return null;
        }

        // get all <td>
        Elements tds = table.select("td");
        if (tds == null || tds.size() <= 0) {
            Log.e(TAG, "titleRow NULL");
            return null;
        }

        ArrayList<String> dayContent = new ArrayList<>();
        for (Element element : tds) {
            // get days
            if (element.hasClass(class_today_content)) {
                dayContent.add(element.text());
            }
        }
        if (dayContent.size() <= 0) {
            return null;
        }

        DayInfo dayInfo = new DayInfo();
        dayInfo.dayTitle = mContext.getString(R.string.title_today);
        String[] data = dayContent.get(0).split(" ");
        if (data.length > 0) {
            dayInfo.temp = data[0];
        }
        dayInfo.weather = dayContent.get(1);
        dayInfo.doam = dayContent.get(2);
        dayInfo.wind = dayContent.get(3);
        dayInfo.imgWeather = imgLink;

        return dayInfo;
    }

    public List<DayInfo> parseTable() {
        // get table by id
        Elements tables = mDocument.select("table#" + WEATHER_TABLE_ID);
        if (tables == null || tables.size() <= 0) {
            Log.e(TAG, "tables NULL");
            return null;
        }
        Element table = tables.get(0); // select the first table.
        if (table == null) {
            Log.e(TAG, "table NULL");
            return null;
        }

        // get all <td>
        Elements tds = table.select("td");
        if (tds == null || tds.size() <= 0) {
            Log.e(TAG, "titleRow NULL");
            return null;
        }

        ArrayList<String> dayTitles = new ArrayList<>();
        ArrayList<String> dayYeuto = new ArrayList<>();
        ArrayList<String> dayContent = new ArrayList<>();
        for (Element element : tds) {
            // get days
            if (element.hasClass(class_day_title)) {
                dayTitles.add(element.text());
            }
            // get yeu to
            if (element.hasClass(class_day_yeuto)) {
                dayYeuto.add(element.text());
            }
            // get content
            if (element.hasClass(class_day_yeuto_content)) {
                dayContent.add(element.text());
            }
        }
        if (dayTitles.size() > 0) {
            // remove field 'Thoi gian' at position 0
            dayTitles.remove(0);
        }
        if (dayYeuto.size() > 1) {
            // remove field 'Nhiet do' at position 1
            dayYeuto.remove(1);
        }
        Log.d(TAG, "title day size = " + dayTitles.size());
        Log.d(TAG, "title day = " + dayTitles.toString());
        Log.d(TAG, "title day - yeu to size = " + dayYeuto.size());
        Log.d(TAG, "title day - yeu to = " + dayYeuto.toString());
        Log.d(TAG, "title day - content size = " + dayContent.size());
        Log.d(TAG, "title day - content = " + dayContent.toString());

        if (dayTitles.size() * dayYeuto.size() != dayContent.size()) {
            Log.d(TAG, "data format is invalid");
            return null;
        }

        // get all <img>
        ArrayList<String> imgList = new ArrayList<>();
        Elements imgs = table.select("img");
        for (Element image : imgs) {
            if (image != null && image.hasAttr("abs:src")) {
                imgList.add(image.attr("abs:src"));
                Log.d(TAG, "img src = " + image.attr("abs:src"));
            }
        }

        ArrayList<DayInfo> dayInfos = new ArrayList<>();
        for (int i = 0; i < dayTitles.size(); i++) {
            if (!dayTitles.get(i).isEmpty()) {
                DayInfo dayInfo = new DayInfo();
                dayInfo.dayTitle = dayTitles.get(i);
                dayInfo.weather = dayContent.get(i);
                dayInfo.minTemp = dayContent.get(dayTitles.size() + i);
                dayInfo.maxTemp = dayContent.get(2 * dayTitles.size() + i);
                if (imgList.size() > i) {
                    dayInfo.imgWeather = imgList.get(i);
                }
                dayInfos.add(dayInfo);
            }
        }

        Log.d(TAG, "RESULT = " + dayInfos.size());
        return dayInfos;
    }
}
