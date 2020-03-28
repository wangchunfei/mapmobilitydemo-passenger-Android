package com.map.mobility.passenger.synchro_v2.psg;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.map.mobility.passenger.BaseActivity;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdate;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory;
import com.tencent.tencentmap.mapsdk.maps.MapView;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.model.BitmapDescriptorFactory;
import com.tencent.tencentmap.mapsdk.maps.model.CameraPosition;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.model.Marker;
import com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions;

public abstract class PsgBaseMapActivity extends BaseActivity {

    protected MapView mapView;
    protected TencentMap tencentMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    /**
     * 初始化
     */
    abstract void init();

    /**
     * 添加marker
     * @param latLng 经纬度
     * @param markerId id
     */
    public Marker addMarker(LatLng latLng, int markerId, float rotation) {
        return addMarker(latLng, markerId, rotation, 0.5f, 0.5f);
    }

    public Marker addMarker(LatLng latLng, int markerId, float rotation, float anchorX, float anchorY) {
        if(mapView == null)
            return null;
        return mapView.getMap().addMarker(new MarkerOptions(latLng)
                .icon(BitmapDescriptorFactory.fromResource(markerId))
                .rotation(rotation)
                .anchor(anchorX, anchorY));
    }

    /**
     * 设置地图的中心点坐标
     */
    public void setPoi(LatLng latLng) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition
                (latLng, 16, 0f, 0));
        tencentMap.moveCamera(cameraUpdate);
    }

    @Override
    public void onStart() {
        if (mapView != null) {
            mapView.onStart();
        }
        super.onStart();
    }

    @Override
    public void onRestart() {
        if (mapView != null) {
            mapView.onRestart();
        }
        super.onRestart();
    }

    @Override
    public void onResume() {
        if (mapView != null) {
            mapView.onResume();
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        if (mapView != null) {
            mapView.onPause();
        }
        super.onPause();
    }

    @Override
    public void onStop() {
        if (mapView != null) {
            mapView.onStop();
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (mapView != null) {
            mapView.onDestroy();
        }
        super.onDestroy();
    }
}
