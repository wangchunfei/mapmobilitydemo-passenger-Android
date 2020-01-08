package com.map.mobility.passenger.helper;

import android.os.Handler;

import com.tencent.map.locussynchro.model.SynchroLocation;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SHelper {

    private SHelper(){}

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
     *  latlng的转换
     * @param list
     */
    public static ArrayList<com.tencent.tencentmap.mapsdk.maps.model.LatLng>
            transformLatLngs(ArrayList<com.tencent.map.locussynchro.model.LatLng> list) {
        if(list == null){
            return null;
        }
        ArrayList<com.tencent.tencentmap.mapsdk.maps.model.LatLng> latLngs = new ArrayList<>();
        for(com.tencent.map.locussynchro.model.LatLng lstlng : list) {
            latLngs.add(new com.tencent.tencentmap.mapsdk.maps.model.LatLng
                    (lstlng.getLatitude(), lstlng.getLongitude()));
        }
        return latLngs;
    }

    /**
     *  获取小车平滑需要的点串信息
     * @param locations
     */
    public static com.tencent.tencentmap.mapsdk.maps.model.LatLng[]
                getLatLngsBySynchroLocation(ArrayList<SynchroLocation> locations) {
        if(locations == null){
            return null;
        }
        for(int i=locations.size() - 1; i >= 0; i --) {
            SynchroLocation l = locations.get(i);
            // 剔除掉吸附失败的点，会造成角度偏差
            if(l.getAttachedIndex() == -1){
                locations.remove(i);
            }
        }
        int size = locations.size();
        com.tencent.tencentmap.mapsdk.maps.model.LatLng[] latLngs =
                new com.tencent.tencentmap.mapsdk.maps.model.LatLng[size];
        for(int i = 0; i < size; i ++) {
            latLngs[i] = new com.tencent.tencentmap.mapsdk.maps.model.LatLng
                    (locations.get(i).getAttachedLatitude()
                            , locations.get(i).getAttachedLongitude());
        }
        return latLngs;
    }

    /**
     * 获取点串第一个吸附成功的点
     */
    public static SynchroLocation getFirsttLocation(ArrayList<SynchroLocation> locations) {
        if(locations == null || locations.size() == 0)
            return null;
        for(SynchroLocation location : locations) {
            if(location.getAttachedIndex() != -1)
                return location;
        }
        return null;
    }

    /**
     * 获取点串最后吸附成功的点
     */
    public static SynchroLocation getLastLocation(ArrayList<SynchroLocation> locations) {
        if(locations == null || locations.size() == 0)
            return null;
        Collections.reverse(locations);
        for(SynchroLocation location : locations) {
            if(location.getAttachedIndex() != -1)
                return location;
        }
        return null;
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
}
