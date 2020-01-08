package com.map.mobility.passenger.synchro;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.map.mobility.passenger.BaseActivity;
import com.tencent.map.locussynchro.TencentLocusSynchro;
import com.tencent.map.locussynchro.model.Order;
import com.tencent.map.locussynchro.model.PassengerSynchroOptions;
import com.tencent.tencentmap.mapsdk.maps.MapView;

public abstract class SynchroBase extends BaseActivity {

    static final String LOG_TAG = "tag1234";

    /**
     * orderId : 订单id
     * passengerId : 乘客id
     */
    String orderId = "ltb_00059";
    String passengerId = "OU_ltb_00059_1";

    /**
     * 订单状态
     * STATUS_NO_ORDER 订单状态-无订单
     * STATUS_ORDER_SENT 订单状态-已派单
     * STATUS_CHARGING_STARTED 订单状态-开始计费
     */
    int orderStatus = Order.STATUS_CHARGING_STARTED;

    /**
     * 最新的路线id
     */
    String lastRouteId;

    TencentLocusSynchro tencentLocusSynchro;

    MapView mapView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getSychroView());
        mapView = getMap();

    }

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

    /**
     * 司乘初始化
     */
    void synchroInit() {
        PassengerSynchroOptions passengerOptions = new PassengerSynchroOptions();
        passengerOptions.setAccountId(passengerId);
        tencentLocusSynchro = new TencentLocusSynchro(this, passengerOptions);
        tencentLocusSynchro.setSyncEnabled(true);// 同步
    }

    /**
     * 开始司乘
     */
    void startSychro() {
        try{
            Log.e("tag1234", "开始");
            tencentLocusSynchro.start(getSyncListener());
        }catch (Exception e){
            Log.e(LOG_TAG, "synchro of passenger fail while starting! message:" + e.getMessage());
        }
    }

    /**
     * 结束司乘
     */
    void stopSynchro() {
        if(tencentLocusSynchro != null) {
            tencentLocusSynchro.stop();
        }
    }

    abstract View getSychroView();

    abstract MapView getMap();

    abstract TencentLocusSynchro.DataSyncListener getSyncListener();

    private boolean checkTencentMap() {
        return mapView != null;
    }

}
