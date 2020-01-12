package com.map.mobility.passenger.spot;

import android.animation.ObjectAnimator;
import android.view.LayoutInflater;
import android.view.View;

import com.map.mobility.passenger.R;
import com.tencent.recommendspot.TMMRecommendedBoardManager;
import com.tencent.recommendspot.ui.PointMarkerView;
import com.tencent.tencentmap.mapsdk.maps.MapView;

/**
 * 使用提供的PointMarkerView
 *
 * @author mjzuo
 */
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

    @Override
    void handleSpot() {
        if(spotView == null || dotPoint == null)
            return;
        spotManager.setPointAnimaListener(new TMMRecommendedBoardManager.TMMPointAnimaListener() {
            @Override
            public void startLoadingAnima() {
                dotPoint.startLoadingAnima();
            }

            @Override
            public void stopLoadingAnima() {
                dotPoint.stopLoadingAnima();
            }

            @Override
            public void startRippleAnima() {
                dotPoint.startRippleAnima();
            }

            @Override
            public void stopRippleAnima() {
                dotPoint.stopRippleAnima();
            }

            @Override
            public ObjectAnimator transactionAnimWithMarker() {
                if(dotPoint != null)
                    return dotPoint.transactionAnimWithMarker();
                return null;
            }
        });
    }
}
