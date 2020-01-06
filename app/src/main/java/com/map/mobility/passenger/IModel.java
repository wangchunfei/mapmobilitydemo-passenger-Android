package com.map.mobility.passenger;

public interface IModel {

    /**
     * LOCATION : 定位
     * MAP : 地图
     * NEAEBY_CAR : 运力
     * SPOT : 上车点
     */
    int LOCATION = 0;
    int MAP = 1;
    int NEAEBY_CAR = 2;
    int SPOT = 3;

    /**
     *  注册监听
     */
    void register(IPasView view);

    /**
     *  取消监听
     */
    void unregister();

    /**
     *  改变后的通知
     */
    void onChangeEvent(int eventType);

}
