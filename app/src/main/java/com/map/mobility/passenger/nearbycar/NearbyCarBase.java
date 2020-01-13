package com.map.mobility.passenger.nearbycar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.map.mobility.passenger.BaseActivity;
import com.map.mobility.passenger.IModel;
import com.map.mobility.passenger.IPasView;
import com.map.mobility.passenger.model.PasLocationModel;
import com.map.mobility.passenger.R;
import com.map.mobility.passenger.location.bean.MapLocation;
import com.tencent.map.carpreview.PreviewMapManager;
import com.tencent.map.carpreview.ui.TencentCarsMap;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 周边车辆
 *
 * @author mjzuo
 * @since 20/01/06
 */
public abstract class NearbyCarBase extends BaseActivity implements IPasView {

    static final String LOG_TAG = "tag1234";

    PasLocationModel loModel;// 定位

    PreviewMapManager carManager;
    TencentCarsMap carsMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getNearbyCarView());

        initCar();
        getLocation();
    }

    @Override
    public void onChange(int eventType, Object obj) {
        switch (eventType) {
            case IModel.LOCATION :
                /**
                 * 获取周边车辆
                 */
                if(obj instanceof MapLocation && carManager != null) {
                    if(loModel != null)
                        loModel.stopLocation();
                    try {
                        carManager.setCurrentLatLng(new LatLng(((MapLocation) obj).getLatitude()
                                , ((MapLocation) obj).getLongitude()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    abstract View getNearbyCarView();

    abstract TencentCarsMap getCarsMap();

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
     * 开启/关闭 周边车辆
     * @param isNearbyCar
     */
    void nearbyCar(boolean isNearbyCar) {
        if(carManager != null)
            carManager.isNearCarShow(isNearbyCar);
    }

    /**
     * 显示/隐藏 sdk自带大头针图标
     * @param isShow
     */
    void dotPointIcon(boolean isShow) {
        if(carsMap == null)
            return;
        if(isShow)
            carsMap.showPoiMaker();
        else
            carsMap.hidePoiMaker();
    }

    /**
     * default config
     */
    private void initCar() {

        carsMap = getCarsMap();
        if(carsMap == null) {
            Log.e(LOG_TAG, "NearbyCarBase nearby car carsMap null");
        }

        PreviewMapManager.init(this);
        carManager = new PreviewMapManager();

        /**
         * 车辆召回数量
         */
        carManager.setCarsCount(10);

        /**
         * 车辆召回半径
         */
        carManager.setRadius(100);

        /**
         * 城市编码
         */
        carManager.setCity(110000);

        /**
         * 召回特定类型车辆
         */
        ArrayList types = new ArrayList();
        types.add("1");
//        types.add("2");
//        types.add("3");
//        types.add("4");
//        types.add("5");
//        types.add("6");
        carManager.setCarsType(types);

        /**
         * 模拟数据
         */
        carManager.setMock(true);

        /**
         * 关联carsMap
         */
        carManager.attachCarsMap(carsMap);

        /**
         * 开启签名校验方式
         */
        carManager.setWebServiceKey("IQaEcTUvT6RaFOcOdcwzYIpQMMgSGd5W", true);

        /**
         * 开启debug log 输出
         */
        carManager.isOpenLog(true);

        /**
         * 车辆图标key : car type，value : resource id
         */
        HashMap<String, Integer> typeResMap = new HashMap<>();
        typeResMap.put("1", R.mipmap.car1);
        typeResMap.put("2", R.mipmap.car2);
        typeResMap.put("3", R.mipmap.car3);
        typeResMap.put("4", R.mipmap.car4);
        typeResMap.put("5", R.mipmap.car5);
        try {
            carManager.setCarsTypeResMap(typeResMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkTencentMap())
            carsMap.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (checkTencentMap())
            carsMap.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (checkTencentMap())
            carsMap.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (checkTencentMap())
            carsMap.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(loModel != null)
            loModel.unregister(this);
        loModel = null;
    }

    private boolean checkTencentMap() {
        return carsMap != null;
    }

}
