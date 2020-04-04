package com.map.mobility.passenger.search;

import com.tencent.map.navi.agent.LocationBean;
import com.tencent.map.navi.agent.TencentDataManager;
import com.tencent.map.navi.agent.address.AddressOptions;
import com.tencent.map.navi.agent.address.interfaces.OnAddressListener;
import com.tencent.map.navi.agent.routes.RoutesDrivingOptions;
import com.tencent.map.navi.agent.routes.RoutesWalkingOptions;
import com.tencent.map.navi.agent.routes.interfaces.NaviPOI;
import com.tencent.map.navi.agent.routes.interfaces.OnRouteWalkListener;
import com.tencent.map.navi.agent.routes.interfaces.OnRoutesListener;
import com.tencent.map.navi.agent.sug.SugOptions;
import com.tencent.map.navi.agent.sug.interfaces.OnSugListener;

public class SearchModel implements IModel {

    IView mView;

    boolean isHasKey;

    public SearchModel(IView view) {
        this.mView = view;
        mView.setModel(this);
        init();
    }

    @Override
    public void init() {
        TencentDataManager.init(mView.getAppContext()
                , "key"
                , "secretKey");
        // 在这里设置标志位的目的，是提醒用户设置key
        isHasKey = false;
    }

    /**
     * sug请求
     *
     * @param onSugListener
     */
    @Override
    public void sugRequest(OnSugListener onSugListener) {
        if(!isHasKey) {
            onSugListener.onError(-1, "no key");
            return;
        }

        //创建对象
        TencentDataManager dataManager = new TencentDataManager(mView.getAppContext());
        dataManager.setSugListener(onSugListener);
        //请求参数的封装（关键词、根据名称限制区域范围等）
        SugOptions sugOptions = new SugOptions();
        LocationBean locationBean = new LocationBean();
        locationBean.setLat(40.034852);// demo就写死了
        locationBean.setLng(116.319820);
        sugOptions.setPolicy("1")
                .setRegion("北京")
                .setKeyword("之春里")
                .setLocation(locationBean);
        dataManager.getSug(sugOptions);
    }

    /**
     * 逆地理编码
     *
     * @param onAddressListener
     */
    @Override
    public void reGeocoding(OnAddressListener onAddressListener) {
        if(!isHasKey) {
            onAddressListener.onError(-1, "no key");
            return;
        }

        TencentDataManager dataManager = new TencentDataManager(mView.getAppContext());
        dataManager.setOnAddressListener(onAddressListener);
        AddressOptions addressOptions = new AddressOptions();
        LocationBean locationBean = new LocationBean();
        locationBean.setLat(40.034852);
        locationBean.setLng(116.319820);
        addressOptions.setLocationBean(locationBean);
        dataManager.getAddress(addressOptions);
    }

    /**
     * 驾车路线
     *
     * @param onDrivingListener
     */
    @Override
    public void routeOfDriving(OnRoutesListener onDrivingListener) {
        if(!isHasKey) {
            onDrivingListener.onError(-1, "no key");
            return;
        }

        TencentDataManager dataManager = new TencentDataManager(mView.getAppContext());
        dataManager.setOnRoutesListener(onDrivingListener);
        RoutesDrivingOptions drivingOptions = new RoutesDrivingOptions();
        NaviPOI from = new NaviPOI();
        from.setLat(40.034852);
        from.setLng(116.319820);
        drivingOptions.setFrom(from);
        NaviPOI to = new NaviPOI();
        to.setLat(40.034852);
        to.setLng(117.319820);
        drivingOptions.setTo(to);
        dataManager.getDriving(drivingOptions);
    }

    /**
     * 步行路线
     *
     * @param onWalkListeer
     */
    @Override
    public void routeOfWalking(OnRouteWalkListener onWalkListeer) {
        if(!isHasKey) {
            onWalkListeer.onError(-1, "no key");
            return;
        }

        TencentDataManager dataManager = new TencentDataManager(mView.getAppContext());
        dataManager.setOnRouteWalkListener(onWalkListeer);
        RoutesWalkingOptions walkingOptions = new RoutesWalkingOptions();
        LocationBean from = new LocationBean();
        from.setLat(40.034852);
        from.setLng(116.319820);
        walkingOptions.setFromLocation(from);
        LocationBean to = new LocationBean();
        to.setLat(40.034852);
        to.setLng(117.319820);
        walkingOptions.setToLocation(to);
        dataManager.getWalking(walkingOptions);
    }

}
