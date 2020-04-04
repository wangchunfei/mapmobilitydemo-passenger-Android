package com.map.mobility.passenger.search;

import com.tencent.map.navi.agent.address.interfaces.OnAddressListener;
import com.tencent.map.navi.agent.routes.interfaces.OnRouteWalkListener;
import com.tencent.map.navi.agent.routes.interfaces.OnRoutesListener;
import com.tencent.map.navi.agent.sug.interfaces.OnSugListener;

public interface IModel {

    /**
     * 检索初始化
     */
    void init();

    /**
     * sug请求
     * @param onSugListener
     */
    void sugRequest(OnSugListener onSugListener);

    /**
     * 逆地理编码
     * @param onAddressListener
     */
    void reGeocoding(OnAddressListener onAddressListener);

    /**
     * 驾车路线
     * @param onDrivingListener
     */
    void routeOfDriving(OnRoutesListener onDrivingListener);

    /**
     * 步行路线
     * @param onWalkListeer
     */
    void routeOfWalking(OnRouteWalkListener onWalkListeer);

}
