package com.map.mobility.passenger.spot;

import android.view.LayoutInflater;
import android.view.View;

import com.map.mobility.passenger.R;
import com.tencent.recommendspot.ui.PointMarkerView;
import com.tencent.tencentmap.mapsdk.maps.MapView;

public class SpotBaseUi extends SpotBase {

    View spotView;

    PointMarkerView dotPoint;

    @Override
    View getNearbyCarView() {
        spotView = LayoutInflater.from(this).inflate(R.layout.pickup_spot_ui, null);
        dotPoint = spotView.findViewById(R.id.point_marker);
        return spotView;
    }

    @Override
    MapView getMap() {
        return spotView.findViewById(R.id.spot_map);
    }


}
