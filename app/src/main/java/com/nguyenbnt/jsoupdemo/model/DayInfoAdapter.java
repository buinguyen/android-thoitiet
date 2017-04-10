package com.nguyenbnt.jsoupdemo.model;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;

import com.nguyenbnt.jsoupdemo.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by 4000225 on 3/28/2017.
 */

public class DayInfoAdapter extends RecyclerView.Adapter<DayInfoAdapter.MyViewHolder> {

    private Context mContext;
    private List<DayInfo> dayInfos;

    public DayInfoAdapter(Context pContext) {
        mContext = pContext;
    }

    public DayInfoAdapter(Context pContext, List<DayInfo> dayInfos) {
        this(pContext);
        this.dayInfos = dayInfos;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.day_info_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        DayInfo dayInfo = dayInfos.get(position);
        if (dayInfo == null) {
            return;
        }
        Log.d("Adapter", position + "  weather = " + dayInfo.weather);

        holder.mTvDate.setText(dayInfo.dayTitle);
        holder.mTvWeather.setText(dayInfo.weather);
        holder.mTvTemp.setText(mContext.getString(R.string.pretext_temp) + dayInfo.temp + mContext.getResources().getString(R.string.degree_celsius));
        holder.mTvMinTemp.setText(mContext.getString(R.string.pretext_min_temp) + dayInfo.minTemp + mContext.getResources().getString(R.string.degree_celsius));
        holder.mTvMaxTemp.setText(mContext.getString(R.string.pretext_max_temp) + dayInfo.maxTemp + mContext.getResources().getString(R.string.degree_celsius));
        holder.mTvDoam.setText(mContext.getString(R.string.pretext_do_am) + dayInfo.doam);
        holder.mTvWind.setText(mContext.getString(R.string.pretext_wind) + dayInfo.wind);
        Picasso.with(mContext).load(dayInfo.imgWeather).into(holder.mImvWeather);

        setVisibility(holder, dayInfo);
    }

    @Override
    public int getItemCount() {
        return dayInfos.size();
    }

    private void setVisibility(MyViewHolder holder, DayInfo dayInfo) {
        holder.mTvWeather.setVisibility(dayInfo.weather.isEmpty() ? View.GONE : View.VISIBLE);
        holder.mTvTemp.setVisibility(dayInfo.temp.isEmpty() ? View.GONE : View.VISIBLE);
        holder.mTvMinTemp.setVisibility(dayInfo.minTemp.isEmpty() ? View.GONE : View.VISIBLE);
        holder.mTvMaxTemp.setVisibility(dayInfo.maxTemp.isEmpty() ? View.GONE : View.VISIBLE);
        holder.mTvDoam.setVisibility(dayInfo.doam.isEmpty() ? View.GONE : View.VISIBLE);
        holder.mTvWind.setVisibility(dayInfo.wind.isEmpty() ? View.GONE : View.VISIBLE);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_date)
        public TextView mTvDate;
        @Bind(R.id.tv_weather)
        public TextView mTvWeather;
        @Bind(R.id.tv_temp)
        public TextView mTvTemp;
        @Bind(R.id.tv_min_temp)
        public TextView mTvMinTemp;
        @Bind(R.id.tv_max_temp)
        public TextView mTvMaxTemp;
        @Bind(R.id.tv_today_do_am_tt)
        public TextView mTvDoam;
        @Bind(R.id.tv_today_wind)
        public TextView mTvWind;
        @Bind(R.id.imv_weather)
        public ImageView mImvWeather;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}