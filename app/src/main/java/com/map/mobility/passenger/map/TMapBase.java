package com.map.mobility.passenger.map;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.map.mobility.passenger.IPasView;
import com.map.mobility.passenger.MapBase;
import com.tencent.tencentmap.mapsdk.maps.MapView;

public abstract class TMapBase extends MapBase implements IPasView {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLaout());

        handle();
    }

    abstract View getLaout();

    protected abstract MapView getMap();

    abstract void handle();

    @Override
    protected void onResume() {
        super.onResume();
        if (checkTencentMap())
            mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (checkTencentMap())
            mapView.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (checkTencentMap())
            mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (checkTencentMap())
            mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mapView != null)
            mapView.onDestroy();
    }

    private boolean checkTencentMap() {
        return mapView != null;
    }

}
