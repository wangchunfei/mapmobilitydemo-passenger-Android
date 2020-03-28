package com.map.mobility.passenger.synchro_v2.helper;

import android.os.Handler;

import com.tencent.map.lssupport.bean.TLSBDriverPosition;
import com.tencent.map.lssupport.bean.TLSBWayPoint;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;

public class SHelper {

    /**
     * 缩放至整条路线都在可视区域内
     *
     * @param map 底图map
     * @param routePoints 当前待操作的路线的点串
     * @param leftMargin 左边距
     * @param topMargin 上边距
     * @param rightMargin 右边距
     * @param bottomMargin 下边距
     */
    public static void fitsWithRoute(final TencentMap map
            , final List<LatLng> routePoints
            , final int leftMargin
            , final int topMargin
            , final int rightMargin
            , final int bottomMargin) {
        if((routePoints == null)|| map == null){
            return;
        }
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(routePoints);
                LatLngBounds bounds = builder.build();
                map.animateCamera(CameraUpdateFactory.newLatLngBoundsRect(bounds,
                        leftMargin, rightMargin, topMargin, bottomMargin));
            }
        });
    }

    /**
     * 获取点串第一个吸附成功的点
     */
    public static TLSBDriverPosition getFirsttLocation(ArrayList<TLSBDriverPosition> locations) {
        if(locations == null || locations.size() == 0)
            return null;
        for(TLSBDriverPosition location : locations) {
            if(location.getPointIndex() != -1)
                return location;
        }
        return null;
    }

    /**
     *  获取小车平滑需要的点串信息
     * @param locations
     */
    public static LatLng[] getLatLngsBySynchroLocation(ArrayList<TLSBDriverPosition> locations) {
        if(locations == null){
            return null;
        }
        for(int i = locations.size() - 1; i >= 0; i --) {
            TLSBDriverPosition l = locations.get(i);
            // 剔除掉吸附失败的点，会造成角度偏差
            if(l.getPointIndex() == -1){
                locations.remove(i);
            }
        }
        int size = locations.size();
        LatLng[] latLngs = new LatLng[size];
        for(int i = 0; i < size; i ++) {
            latLngs[i] = new LatLng(locations.get(i).getAttachLat()
                            , locations.get(i).getAttachLng());
        }
        return latLngs;
    }

    /**
     * 对比经纬度相等
     */
    public static boolean equalOfLatlng(LatLng latLng0, LatLng latLng1) {
        if(latLng0 == null || latLng1 == null)
            return false;
        if(latLng0.getLongitude() == latLng1.getLongitude()
                && latLng0.getLatitude() == latLng1.getLatitude()) {
            return true;
        }
        return false;
    }

    /**
     * 途经点排序
     */
    public static ArrayList<TLSBWayPoint> sortWayPoints(ArrayList<TLSBWayPoint> wayPoints) {
        ArrayList<TLSBWayPoint> ways = new ArrayList<>();
        while (wayPoints.size() != 0) {
            TLSBWayPoint wayPoint = wayPoints.get(0);
            for(int index = 1; index < wayPoints.size(); index ++) {
                if(wayPoints.get(index).getPassengerOrderId().equals(wayPoint.getPassengerOrderId())) {
                    ways.add(wayPoints.get(0));
                    ways.get(index);
                    wayPoints.remove(index);
                    wayPoints.remove(0);
                    break;
                }
            }
        }
        return ways;
    }
}
