package com.nguyenbnt.jsoupdemo;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.nguyenbnt.jsoupdemo.model.DayInfo;
import com.nguyenbnt.jsoupdemo.model.DayInfoAdapter;
import com.nguyenbnt.jsoupdemo.model.DividerItemDecoration;
import com.nguyenbnt.jsoupdemo.model.Province;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;

public class MainActivity extends AppCompatActivity implements DownloadTask.IDownloadInfo {
    private final String TAG = this.getClass().getSimpleName();

    private Context mContext;

    @Bind(R.id.spn_area)
    MaterialSpinner mSpnArea;
    @Bind(R.id.spn_province)
    MaterialSpinner mSpnProvince;
    @Bind(R.id.rv_weather)
    RecyclerView mRcvWeather;
    @Bind(R.id.tv_province_name)
    TextView mTvProvinceName;
    @Bind(R.id.tv_nodata)
    TextView mTvNodata;

    private List<Province> mProvinceList = new ArrayList<>();
    private List<String> mAreaList = new ArrayList<>();
    private List<DayInfo> mDayInfoList = new ArrayList<>();
    private DayInfoAdapter mDayInfoAdapter;

    private boolean isRunning = false;
    private ProgressDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mContext = this;

        initProvinceList();

        initSpinners();

//        mTvProvinceName = (TextView) findViewById(R.id.tv_province_name);
//        mTvNodata = (TextView) findViewById(R.id.tv_nodata);

        // Init recycle view
        mDayInfoAdapter = new DayInfoAdapter(mContext, mDayInfoList);
//        mRcvWeather = (RecyclerView) findViewById(R.id.rv_weather);
        mRcvWeather.setHasFixedSize(true);
        mRcvWeather.addItemDecoration(new DividerItemDecoration(this));
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRcvWeather.setLayoutManager(llm);
        mRcvWeather.setAdapter(mDayInfoAdapter);

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(500);
        itemAnimator.setRemoveDuration(500);
        mRcvWeather.setItemAnimator(itemAnimator);
    }

    protected void onItemSelectedSpnArea(MaterialSpinner spinner, int position) {
        reset();

        List<String> provinceList = new ArrayList<String>();
        provinceList.add("- Tỉnh/thành phố -");
        for (Province province : mProvinceList) {
            if (province.area.equals(mAreaList.get(position))) {
                provinceList.add(province.name);
            }
        }
        mSpnProvince.setItems(provinceList);
        mSpnProvince.setSelectedIndex(0);
    }

    protected void onItemSelectedSpnProvince(MaterialSpinner spinner, int position) {
        reset();

        if (position <= 0) {
            Log.e(TAG, "No province is selected");
            return;
        }
        if (isNetworkConnected() == false) {
            Toast.makeText(getApplicationContext(), "No connect Internet", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "No connect Internet");
            return;
        }
        if (isRunning) {
            Toast.makeText(getApplicationContext(), "Running...", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoadingDialog();

        for (Province province : mProvinceList) {
            if (province.name.equals(mSpnProvince.getItems().get(position))) {
                isRunning = true;
                new DownloadTask(mContext).execute(province);
                break;
            }
        }
    }

    private void initSpinners() {
        mSpnArea.setItems(mAreaList);
        mSpnArea.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                onItemSelectedSpnArea(view, position);
            }
        });

        List<String> provinceList = new ArrayList<String>();
        provinceList.add("- Tỉnh/thành phố -");
        mSpnProvince.setItems(provinceList);
        mSpnProvince.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                onItemSelectedSpnProvince(view, position);
            }
        });
    }

    /**
     * To read data from asset files
     */
    public void initProvinceList() {
        try {
            JSONArray usersList = new JSONArray(loadJSONFromAsset("province_list"));
            for (int i = 0; i < usersList.length(); i++) {
                JSONObject user = usersList.getJSONObject(i);
                if (user != null) {
                    Province provinceInfo = new Province();
                    provinceInfo.id = user.getString("id");
                    provinceInfo.area = user.getString("area");
                    provinceInfo.name = user.getString("name");
                    provinceInfo.link = user.getString("link");
                    mProvinceList.add(provinceInfo);
                }
            }

            mAreaList.add("- Khu vực -");
            JSONArray areas = new JSONArray(loadJSONFromAsset("area_list"));
            for (int i = 0; i < areas.length(); i++) {
                JSONObject area = areas.getJSONObject(i);
                if (area != null) {
                    String name = area.getString("name");
                    mAreaList.add(name);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * To load JSON string from text file in asset folder
     *
     * @param pFilePath asset file path
     * @return String - json string
     */
    public String loadJSONFromAsset(String pFilePath) {
        String json = null;
        try {
            InputStream is = getAssets().open(pFilePath);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private void reset() {
        mDayInfoList.clear();
        mDayInfoAdapter.notifyDataSetChanged();
        mTvProvinceName.setText("");
        mTvNodata.setVisibility(View.INVISIBLE);
    }

    public void updateData(Province province, List<DayInfo> dayInfos) {
        mDayInfoList.clear();
        isRunning = false;

        mTvProvinceName.setText(getString(R.string.pretext_title_today) + province.name);

        if (dayInfos == null || dayInfos.size() <= 0) {
            mTvNodata.setVisibility(View.VISIBLE);
            Log.d(TAG, "--- Internet connection was lost or no data was found. ---");
            return;
        }

        mDayInfoList.addAll(dayInfos);
        mDayInfoAdapter.notifyDataSetChanged();
        mRcvWeather.setAdapter(mDayInfoAdapter);
    }

    @Override
    public void onResult(Province province, List<DayInfo> dayInfos) {
        Log.d(TAG, "---- Finished ----");

        updateData(province, dayInfos);

        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    /**
     * Show loading dialog with custom message
     *
     * @param pMsg obmited if using default loading message
     */
    protected void showLoadingDialog(String... pMsg) {
        if (pMsg != null && pMsg.length > 0) {
            mLoadingDialog = ProgressDialog.show(this, "", pMsg[0], true);
        } else {
            mLoadingDialog = ProgressDialog.show(this, "", "Đang tải dữ liệu...", true);
        }
        mLoadingDialog.setCancelable(false);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
