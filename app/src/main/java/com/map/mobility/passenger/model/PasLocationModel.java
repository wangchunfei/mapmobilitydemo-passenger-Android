package com.map.mobility.passenger.model;

import android.content.Context;
import android.util.Log;

import com.map.mobility.passenger.IModel;
import com.map.mobility.passenger.IPasView;
import com.map.mobility.passenger.location.ILocation;
import com.map.mobility.passenger.location.TencentLocaiton;
import com.map.mobility.passenger.location.bean.MapLocation;

import java.util.ArrayList;

/**
 * 这是定位的model
 *
 * @author mjzuo
 */
public class PasLocationModel implements IModel {

    ArrayList<IPasView> views = new ArrayList<>();

    /**
     * 定位
     */
    private ILocation locationManager;
    private ILocation.ILocationListener locationListener;
    private MapLocation mapLocation;

    Context mContext;

    public PasLocationModel(Context context) {
        mContext = context;
    }

    @Override
    public void register(IPasView view) {
        int index = views.indexOf(view);
        if(index == -1){
            views.add(view);
        }
    }

    @Override
    public void unregister(IPasView view) {
        if(views != null && views.indexOf(view) != -1)
            views.remove(view);
    }

    public void postChangeLocationEvent() {
        if(locationManager == null){
            initLocation();
        }
        startLocation();
    }

    @Override
    public void onChangeEvent(int eventType) {
        for(IPasView carPreView: views){
            carPreView.onChange(eventType, mapLocation);
        }
    }

    class MyLocationListener implements ILocation.ILocationListener {
        @Override
        public void onLocationChanged(MapLocation location) {
            if(location != null)
                Log.e("tag1234", ">>>> location : lat " + location.getLatitude()
                    + ",lng " + location.getLongitude());
            // 定位更新后，通知view
            mapLocation = location;
            onChangeEvent(IModel.LOCATION);
            // 不需要实时定位
            stopLocation();
        }

        @Override
        public void onStatusUpdate(String s, int i, String s1) {

        }
    }

    private void initLocation() {
        // 定位的管理类
        locationManager = new TencentLocaiton(mContext.getApplicationContext());
        // 注册定位监听
        locationListener = new MyLocationListener();
        locationManager.setLocationListener(locationListener);
    }

    private void startLocation() {
        // 开始定位
        locationManager.startLocation();
    }

    private void stopLocation() {
        // 结束定位
        locationManager.stopLocation();
        locationManager = null;
    }
}

