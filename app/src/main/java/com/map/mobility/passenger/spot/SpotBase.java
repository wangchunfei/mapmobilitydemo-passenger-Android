package com.map.mobility.passenger.spot;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.map.mobility.passenger.BaseActivity;
import com.map.mobility.passenger.IModel;
import com.map.mobility.passenger.IPasView;
import com.map.mobility.passenger.model.PasLocationModel;
import com.map.mobility.passenger.location.bean.MapLocation;
import com.tencent.recommendspot.TMMRecommendedBoardManager;
import com.tencent.recommendspot.recospot.bean.TMMLatlng;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdate;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory;
import com.tencent.tencentmap.mapsdk.maps.MapView;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.model.CameraPosition;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;

/**
 * 上车点
 *
 * @author mjzuo
 * @since 20/01/06
 */
public abstract class SpotBase extends BaseActivity implements IPasView {

    static final String LOG_TAG = "tag1234";

    PasLocationModel loModel;// 定位

    TMMRecommendedBoardManager spotManager;
    TencentMap tencentMap;

    MapView mapView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getNearbyCarView());

        mapView = getMap();
        tencentMap = mapView.getMap();

        initSpot();
        getLocation();

        handleSpot();
    }

    @Override
    public void onChange(int eventType, Object obj) {
        switch (eventType) {
            case IModel.LOCATION :
                /**
                 * 移动地图到中心
                 * 注意：因为调用此方法后，sdk内部 onCameraChangeFinished 会自动请求上车点
                 * 不需要重复调用 getRecommendSpot 方法
                 */
                if(obj instanceof MapLocation && tencentMap != null) {
                    try {

                        setPoi(new LatLng(((MapLocation) obj).getLatitude()
                                , ((MapLocation) obj).getLongitude()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    /**
     * 获取定位
     */
    void getLocation() {
        if(loModel == null) {
            loModel = new PasLocationModel(this);
            loModel.register(this);
        }
        loModel.postChangeLocationEvent();// 获取定位点
    }

    /**
     * 获取推荐上车点数据
     * @param latLng
     */
    void getSpot(TMMLatlng latLng) {
        if(spotManager == null)
            return;

        spotManager.getRecommendSpot(spotManager.getBoardOption()
                .latlng(latLng));
    }

    abstract View getNearbyCarView();

    abstract MapView getMap();

    abstract void handleSpot();

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
        if(loModel != null)
            loModel.unregister();
        loModel = null;
        if(spotManager != null)
            spotManager.destory();
    }

    private boolean checkTencentMap() {
        return mapView != null;
    }

    /**
     * default config
     */
    private void initSpot() {

        spotManager = new TMMRecommendedBoardManager(tencentMap);
        TMMRecommendedBoardManager.mContext = this;

        spotManager.getManagerConfig()
                .isRecommendSpotDefaultUI(true)// 使用默认上车点view
                .isAbsorbed(true);// 允许吸附
        spotManager.getUiStyle()
                .setMaxWordsPerLine(6);

        /**
         * 关联cameraChange监听
         */
        tencentMap.setOnCameraChangeListener(new TencentMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                spotManager.onCamerChangeListener(cameraPosition);
            }

            @Override
            public void onCameraChangeFinished(CameraPosition cameraPosition) {
                spotManager.onCameraChangeFinish(cameraPosition);
            }
        });
    }

    /**
     * 设置地图的中心点坐标
     */
    private void setPoi(LatLng latLng) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition
                (latLng, 16, 0f, 0));
        tencentMap.moveCamera(cameraUpdate);
    }
}
