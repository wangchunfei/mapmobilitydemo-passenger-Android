package com.map.mobility.passenger.map;

import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.map.mobility.passenger.IModel;
import com.map.mobility.passenger.IPasView;
import com.map.mobility.passenger.R;
import com.map.mobility.passenger.location.bean.MapLocation;
import com.map.mobility.passenger.model.PasLocationModel;
import com.tencent.tencentmap.mapsdk.maps.LocationSource;
import com.tencent.tencentmap.mapsdk.maps.MapView;
import com.tencent.tencentmap.mapsdk.maps.model.BitmapDescriptorFactory;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.model.MyLocationStyle;

/**
 *  罗盘by地图UiSetting自带
 *
 * @author mjzuo
 */
public class MapCompassBySetting extends TMapBase implements IPasView {

    PasLocationModel loModel;
    LatLng lastLatlng;

    LocationSource.OnLocationChangedListener ls;
    Location lo;

    View view;

    @Override
    public void onChange(int eventType, Object obj) {
        switch (eventType) {
            case IModel.LOCATION :
                if(obj instanceof MapLocation) {
                    MapLocation location = (MapLocation)obj;
                    lastLatlng = new LatLng(location.getLatitude(), location.getLongitude());
                    setPoi(lastLatlng);

                    Log.e("tag1234", ">>>>> location bearing : " + (location != null ? location.getBearing() : 0));

                    // location source
                    if(ls != null) {
                        lo = new Location(location.getProvider());
                        lo.setLatitude(location.getLatitude());
                        lo.setLongitude(location.getLongitude());
                        lo.setBearing(location.getBearing());
                        ls.onLocationChanged(lo);
                    }
                }
                break;
        }
    }

    public void showCompass(View view) {
        showMarkerByLocationSource();
    }

    public void hideCompass(View view) {
        removeMarkerByLocationSource();
    }

    @Override
    protected MapView getMap() {
        if(view == null)
            view = LayoutInflater.from(this).inflate(R.layout.map_compass_by_setting, null);
        return view.findViewById(R.id.map_compass);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(loModel != null) {
            loModel.stopLocation();
            loModel.unregister(this);
        }
        loModel = null;
    }

    @Override
    View getLaout() {
        return view;
    }

    @Override
    void handle() {
        if(loModel == null) {
            loModel = new PasLocationModel(this);
            loModel.register(this);
        }
        loModel.postChangeLocationEvent();
    }

    private void showMarkerByLocationSource() {
        // location source init
        tencentMap.setLocationSource(new LocationSource() {
            @Override
            public void activate(OnLocationChangedListener onlocationchangedlistener) {
                ls = onlocationchangedlistener;
            }

            @Override
            public void deactivate() {
                ls = null;
                lo = null;
            }
        });
        tencentMap.setMyLocationEnabled(true);
        tencentMap.getUiSettings().setMyLocationButtonEnabled(true);
        // add style
        MyLocationStyle myLocationStyle = new MyLocationStyle()
                . anchor(0.5f, 0.5f)
                . icon(BitmapDescriptorFactory.fromResource(R.mipmap.blue_compass))
                . myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
        tencentMap.setMyLocationStyle(myLocationStyle);
    }

    private void removeMarkerByLocationSource() {
        if(tencentMap.isMyLocationEnabled()){
            tencentMap.setMyLocationEnabled(false);
            tencentMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }
}

