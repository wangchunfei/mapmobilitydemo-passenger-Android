package com.map.mobility.passenger.synchro;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.map.mobility.passenger.R;
import com.map.mobility.passenger.helper.SHelper;
import com.map.mobility.passenger.utils.CommentUtils;
import com.tencent.map.locussynchro.TencentLocusSynchro;
import com.tencent.map.locussynchro.model.Order;
import com.tencent.map.locussynchro.model.RouteUploadError;
import com.tencent.map.locussynchro.model.SyncData;
import com.tencent.map.locussynchro.model.TrafficItem;
import com.tencent.tencentmap.mapsdk.maps.MapView;
import com.tencent.tencentmap.mapsdk.maps.model.BitmapDescriptorFactory;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.model.Marker;
import com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions;
import com.tencent.tencentmap.mapsdk.maps.model.Polyline;
import com.tencent.tencentmap.mapsdk.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class SychroTraffic extends SynchroBase {

    ArrayList<LatLng> points = new ArrayList<>();// 路线点串

    Polyline polyline;
    Marker startMarker, endMarker;

    View mView;
    MapView mapView;

    public void start(View view) {
        synchroInit();
        startSychro();
    }

    public void stop(View view) {
        stopSynchro();
        if(polyline != null)
            polyline.remove();
    }

    @Override
    View getSychroView() {
        mView = LayoutInflater.from(this).inflate(R.layout.sychro_traffic, null);
        mapView = mView.findViewById(R.id.spot_map);
        return mView;
    }

    @Override
    MapView getMap() {
        return mapView;
    }

    @Override
    TencentLocusSynchro.DataSyncListener getSyncListener() {

        return new TencentLocusSynchro.DataSyncListener() {

            /**
             * 订单信息同步
             * 为保证司乘同显功能正常，请开发者务必返回最新的OrderInfo
             */
            @Override
            public Order onOrderInfoSynchro() {
                Order order = new Order();
                order.setOrderId(orderId);
                order.setOrderStatus(orderStatus);
                return order;
            }

            /**
             * 拉取成功
             * @param syncData
             */
            @Override
            public void onSyncDataUpdated(SyncData syncData) {
                if(syncData == null || syncData.getRoute() == null
                        || syncData.getRoute().getRoutePoints() == null) {
                    Log.e(LOG_TAG, "syncData or route or points of onSyncDataUpdated null");
                    return;
                }

                showRoute(syncData);// 显示路况颜色

                if(points.size() != 0)
                    points.clear();
                points.addAll(SHelper.transformLatLngs(syncData.getRoute().getRoutePoints()));

                if(startMarker == null && endMarker == null && points.size() >= 2) {// 添加起点终点marker
                    startMarker = addMarker(points.get(0), R.mipmap.line_start_point, 0);
                    endMarker = addMarker(points.get(points.size() - 1), R.mipmap.line_end_point, 0);
                }
            }

            /**
             * 路线上传失败
             * 开发者需要重新上传路线重试
             */
            @Override
            public void onRouteUploadFailed(RouteUploadError routeUploadError) {
                Log.e(LOG_TAG, ">>>onRouteUploadFailed " + routeUploadError);
            }

            /**
             * 路线上传成功
             */
            @Override
            public void onRouteUploadComplete() {
                Log.d(LOG_TAG, ">>>onRouteUploadComplete()");
            }

            /**
             * 定位点上传失败
             */
            @Override
            public void onLocationUploadFailed(RouteUploadError routeUploadError) {
                Log.e(LOG_TAG, ">>>onLocationUploadFailed()" + routeUploadError);
            }

            /**
             * 定位点上传成功
             */
            @Override
            public void onLocationUploadComplete() {
                Log.d(LOG_TAG, "onLocationUploadComplete()");
            }
        };
    }

    /**
     * 添加marker
     * @param latLng 经纬度
     * @param markerId id
     */
    private Marker addMarker(LatLng latLng, int markerId, float rotation) {
        return addMarker(latLng, markerId, rotation, 0.5f, 0.5f);
    }

    private Marker addMarker(LatLng latLng, int markerId, float rotation, float anchorX, float anchorY) {
        return mapView.getMap().addMarker(new MarkerOptions(latLng)
                .icon(BitmapDescriptorFactory.fromResource(markerId))
                .rotation(rotation)
                .anchor(anchorX, anchorY));
    }

    /**
     * 路况颜色
     */
    private void showRoute(SyncData syncData) {
        if(syncData == null || syncData.getRoute() == null)
            return;

        int width = (int) (10 * getResources().getDisplayMetrics().density + 0.5);
        ArrayList<TrafficItem> traffics = syncData.getRoute().getTrafficItems();
        List<LatLng> mRoutePoints = SHelper.transformLatLngs(syncData.getRoute().getRoutePoints());
        // 点的个数
        int pointSize = mRoutePoints.size();
        // 路段总数 三个index是一个路况单元，分别为：路况级别，起点，终点
        int trafficSize = traffics.size();
        // 路段index所对应的颜色值数组
        int[] trafficColors = new int[pointSize];
        // 路段index数组
        int[] trafficColorsIndex = new int[pointSize];
        int pointStart = 0;
        int pointEnd = 0;
        int trafficColor = 0;
        int index = 0;
        for (int j = 0; j < trafficSize; j++) {
            pointStart = traffics.get(j).getFromIndex();
            pointEnd = traffics.get(j).getToIndex();
            trafficColor = getTrafficColor(traffics.get(j).getTraffic());
            for (int k = pointStart; k < pointEnd || k == pointSize - 1; k++) {
                trafficColors[index] = trafficColor;
                trafficColorsIndex[index] = index;
                index++;
            }
        }

        if(polyline == null) {
            // 首次调整视图中心点
            SHelper.fitsWithRoute(mapView.getMap()
                    , mRoutePoints
                    , CommentUtils.dip2px(this, 32)
                    , CommentUtils.dip2px(this, 64)
                    , CommentUtils.dip2px(this, 32)
                    , CommentUtils.dip2px(this, 64));
        }else
            polyline.remove();

        polyline = mapView.getMap().addPolyline(new PolylineOptions()
                .addAll(mRoutePoints)
                .width(width)
                .arrow(true)
                .colors(trafficColors, trafficColorsIndex));
    }

    private int getTrafficColor(int type) {
        int color = 0xFFFFFFFF;
        switch (type) {
            case 0:
                // 路况标签-畅通
                // 绿色
                color = 0xff3EBA79;
                break;
            case 1:
                // 路况标签-缓慢
                // 黄色
                color = 0xffF4BB45;
                break;
            case 2:
                // 路况标签-拥堵
                // 红色
                color = 0xffE85854;
                break;
            case 3:
                // 路况标签-无路况
                color = 0xff4F96EE;
                break;
            case 4:
                // 路况标签-特别拥堵（猪肝红）
                color = 0xffAF333D;
                break;
        }
        return color;
    }
}
