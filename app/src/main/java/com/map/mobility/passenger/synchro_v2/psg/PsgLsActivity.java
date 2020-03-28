package com.map.mobility.passenger.synchro_v2.psg;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.map.mobility.passenger.R;
import com.map.mobility.passenger.location.ILocation;
import com.map.mobility.passenger.location.TencentLocaiton;
import com.map.mobility.passenger.location.bean.MapLocation;
import com.map.mobility.passenger.synchro_v2.helper.ConvertHelper;
import com.map.mobility.passenger.utils.ToastUtils;
import com.tencent.map.lspassenger.TSLPassengerManager;
import com.tencent.map.lspassenger.lsp.listener.PsgDataListener;
import com.tencent.map.lssupport.bean.TLSBDriverPosition;
import com.tencent.map.lssupport.bean.TLSBOrder;
import com.tencent.map.lssupport.bean.TLSBOrderStatus;
import com.tencent.map.lssupport.bean.TLSBOrderType;
import com.tencent.map.lssupport.bean.TLSBRoute;
import com.tencent.map.lssupport.bean.TLSConfigPreference;
import com.tencent.map.lssupport.bean.TLSDDrvierStatus;
import com.tencent.map.lssupport.bean.TLSDFetchedData;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.model.Marker;

import java.util.ArrayList;

public abstract class PsgLsActivity extends PsgBaseMapActivity {

    static final String LOG_TAG = "navi1234";

    TSLPassengerManager tlspManager;// 司乘管理类
    TencentLocaiton locationManager;// 定位管理类

    Marker psgMarker;

    /**
     * 默认选择订单A
     */
    String orderId = "test_driver_order_000011";// 顺风车司机订单id
    String psgId = "test_passenger_000001";// 顺风车乘客id
    String pOrderId = "test_passenger_order_000011";// 乘客子订单id
    int curOrderType = TLSBOrderType.TLSDOrderTypeHitchRide;
    int curOrderState = TLSBOrderStatus.TLSDOrderStatusNone;
    int curDriverState = TLSDDrvierStatus.TLSDDrvierStatusStopped;

    String deviceId;// 设备id

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ToastUtils.init(getApplicationContext());// 初始化toast

        tlspManager = TSLPassengerManager.getInstance();// 初始化司乘
        tlspManager.init(getApplicationContext(), TLSConfigPreference.create()
                .setAccountId(psgId).setDeviceId(deviceId));
        tlspManager.addTLSPassengerListener(new MyPullDriverInfo());// 司乘回调

        locationManager = new TencentLocaiton(getApplicationContext()); // 初始化定位
        locationManager.setLocationListener(new ILocation.ILocationListener() {
            @Override
            public void onLocationChanged(MapLocation location) {
                if(tlspManager != null)// 上传定位点
                    tlspManager.uploadPosition(ConvertHelper.tenPoTOTLSPo(location));
                if(location != null) {// 展示自己位置
                    if(psgMarker == null)
                        psgMarker = addMarker(new LatLng(location.getLatitude(), location.getLongitude())
                                , R.mipmap.psg_position_icon, 0);
                    else
                        psgMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
                }
            }

            @Override
            public void onStatusUpdate(String s, int i, String s1) {

            }
        });
    }

    /**
     * 模拟拼单A
     * @param view
     */
    public void sendHitchHikeOrderA(View view) {
        if(tlspManager == null)
            return;
        curOrderType = TLSBOrderType.TLSDOrderTypeHitchRide;
        curOrderState = TLSBOrderStatus.TLSDOrderStatusTrip;// 顺风单都是送驾状态
        curDriverState = TLSDDrvierStatus.TLSDDrvierStatusServing;
        tlspManager.getTLSPOrder().setpOrderId(pOrderId)
                .setOrderId(orderId)
                .setOrderStatus(curOrderState)
                .setDrvierStatus(curDriverState)
                .setOrderType(curOrderType);
        if(!tlspManager.isRuning())
            tlspManager.start();
    }

    /**
     * 模拟听单B
     * @param view
     */
    public void sendHitchHikeOrderB(View view) {
        if(tlspManager == null)
            return;
        curOrderType = TLSBOrderType.TLSDOrderTypeHitchRide;
        curOrderState = TLSBOrderStatus.TLSDOrderStatusTrip;// 顺风单都是送驾状态
        curDriverState = TLSDDrvierStatus.TLSDDrvierStatusServing;
        tlspManager.getTLSPOrder().setpOrderId(pOrderId)
                .setOrderId(orderId)
                .setOrderStatus(curOrderState)
                .setDrvierStatus(curDriverState)
                .setOrderType(curOrderType);
        if(!tlspManager.isRuning())
            tlspManager.start();
    }

    /**
     * 模拟进入快车单
     * @param view
     */
    public void sendHighOrder(View view) {
        if(tlspManager == null)
            return;
        curOrderType = TLSBOrderType.TLSDOrderTypeNormal;
        curOrderState = TLSBOrderStatus.TLSDOrderStatusPickUp;// 接驾
        curDriverState = TLSDDrvierStatus.TLSDDrvierStatusServing;
        tlspManager.getTLSPOrder().setpOrderId(pOrderId)// pOrderId == ""
                .setOrderId(orderId)
                .setOrderStatus(curOrderState)
                .setDrvierStatus(curDriverState)
                .setOrderType(curOrderType);
        if(!tlspManager.isRuning())
            tlspManager.start();
    }

    /**
     * 结束司乘
     * @param view
     */
    public void finishOrder(View view) {
        ToastUtils.INSTANCE().Toast("结束司乘!!");
        if(tlspManager != null)
            tlspManager.stop();
        clearUi();
    }

    /**
     * 开启司乘
     * @param view
     */
    public void startLs(View view) {
        ToastUtils.INSTANCE().Toast("开启司乘");
        if(tlspManager != null)
            tlspManager.start();
    }

    /**
     * 开启定位
     * @param view
     */
    public void startLocation(View view) {
        ToastUtils.INSTANCE().Toast("开启定位");
        if(locationManager != null)
            locationManager.startLocation();
    }

    /**
     * 停止定位
     * @param view
     */
    public void stopLocation(View view) {
        ToastUtils.INSTANCE().Toast("停止定位!!");
        if(locationManager != null)
            locationManager.stopLocation();
    }

    /**
     * 上报定位点
     * @param view
     */
    public void pushLocation(View view) {
        ToastUtils.INSTANCE().Toast("上报定位点");
        if(tlspManager != null)
            tlspManager.uploadPassengerPositionsEnabled(true);
    }

    /**
     * 取消上传定位点
     * @param view
     */
    public void stopPushLocation(View view) {
        ToastUtils.INSTANCE().Toast("停止上报定位点!!");
        if(tlspManager != null)
            tlspManager.uploadPassengerPositionsEnabled(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ToastUtils.INSTANCE().destory();
        if(tlspManager != null)
            tlspManager.stop();
    }

    /**
     * 拉取到的司机信息，子类做各种的处理
     * @param route
     * @param order
     * @param pos
     */
    abstract void updateDriverInfo(TLSBRoute route, TLSBOrder order, ArrayList<TLSBDriverPosition> pos);

    /**
     * 清空当前界面ui
     */
    abstract void clearUi();

    /**
     * 司乘sdk对外暴露的回调接口
     */
    class MyPullDriverInfo implements PsgDataListener.ITLSPassengerListener {

        @Override
        public void onPullLsInfoSuc(TLSDFetchedData fetchedData) {
            Log.e(LOG_TAG, "pull driver info suc !!");

            updateDriverInfo(fetchedData.getRoute(), fetchedData.getOrder(), fetchedData.getPositions());

        }

        @Override
        public void onPullLsInfoFail(int errCode, String errMsg) {
            Log.e(LOG_TAG, "pull driver info fail -> errCode : " +errCode + ", err" + errMsg);
        }

        @Override
        public void onPushPositionSuc() {
            Log.e(LOG_TAG, "push location suc !!");
        }

        @Override
        public void onPushPositionFail(int errCode, String errMsg) {
            Log.e(LOG_TAG, "push location fail -> errCode : " +errCode + ", err" + errMsg);
        }
    }

}
