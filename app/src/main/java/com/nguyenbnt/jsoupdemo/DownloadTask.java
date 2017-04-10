package com.nguyenbnt.jsoupdemo;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.nguyenbnt.jsoupdemo.model.DayInfo;
import com.nguyenbnt.jsoupdemo.model.Province;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 4000225 on 3/28/2017.
 */

public class DownloadTask extends AsyncTask<Province, Void, List<DayInfo>> {
    private final String TAG = this.getClass().getSimpleName();

    private IDownloadInfo mCallback;
    private Context mContext;
    private Province mProvince;

    public DownloadTask(Context context) {
        mCallback = (IDownloadInfo) context;
        mContext = context;
    }

    @Override
    protected List<DayInfo> doInBackground(Province... info) {
        Log.d(TAG, "start DownloadTask");
        if (info == null || info.length <= 0) {
            return new ArrayList<>();
        }
        mProvince = info[0];

        Connection connection = null;
        try {
            connection = Jsoup.connect(mProvince.link).timeout(10 * 1000);
        } catch (Exception e) {
            Log.e(TAG, "No data");
            Toast.makeText(mContext, "No data", Toast.LENGTH_SHORT).show();
        }
        if (connection == null) {
            return new ArrayList<>();
        }
        Document document = null;
        Parser parser = null;
        try {
            document = (Document) connection.get();
            parser = new Parser(mContext, document, null);

            DayInfo todayInfo = parser.parseTodayWeather();
            List<DayInfo> list = parser.parseTable();
            if (list != null && todayInfo != null) {
                list.add(0, todayInfo);
            }
            return list;

        } catch (IOException e) {
            e.printStackTrace();

            try {
                document = (Document) connection.get();
                parser = new Parser(mContext, document, null);

                DayInfo todayInfo = parser.parseTodayWeather();
                List<DayInfo> list = parser.parseTable();
                if (list != null && todayInfo != null) {
                    list.add(0, todayInfo);
                }
                return list;
            } catch (IOException e1) {
                e1.printStackTrace();
                Log.e(TAG, "No data");
            }
        }
        Log.d(TAG, "finish DownloadTask");
        return new ArrayList<>();
    }

    @Override
    protected void onPostExecute(List<DayInfo> dayInfos) {
        mCallback.onResult(mProvince, dayInfos);
    }

    public interface IDownloadInfo {
        void onResult(Province province, List<DayInfo> dayInfos);
    }
}
