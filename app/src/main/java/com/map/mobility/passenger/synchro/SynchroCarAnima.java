package com.map.mobility.passenger.synchro;

import android.graphics.Color;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.map.mobility.passenger.R;
import com.map.mobility.passenger.helper.SHelper;
import com.map.mobility.passenger.utils.CommentUtils;
import com.tencent.map.carpreview.anima.MarkerTranslateAnimator;
import com.tencent.map.locussynchro.TencentLocusSynchro;
import com.tencent.map.locussynchro.model.Order;
import com.tencent.map.locussynchro.model.RouteUploadError;
import com.tencent.map.locussynchro.model.SyncData;
import com.tencent.map.locussynchro.model.SynchroLocation;
import com.tencent.tencentmap.mapsdk.maps.MapView;
import com.tencent.tencentmap.mapsdk.maps.model.BitmapDescriptorFactory;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.model.Marker;
import com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions;
import com.tencent.tencentmap.mapsdk.maps.model.Polyline;
import com.tencent.tencentmap.mapsdk.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * 小车平滑和路线擦除效果
 *
 * @author mjzuo
 */
public class SynchroCarAnima extends SynchroBase {

    private static final int ERASE_MSG = 0;

    ArrayList<LatLng> points = new ArrayList<>();// 路线点串
    Polyline polyline;
    Marker carMarker;
    LatLng lastPoint;// 保存当前拉取到的最后点

    Marker startMarker, endMarker;// 起终点marker

    HandlerThread eraseThread;
    EraseHandler eraseHandler;

    int animaTime = 5000;// 动画时间
    SynchroLocation curEraseLatlng;

    boolean eraseAble;

    View mView;

    public void start(View view) {
        synchroInit();
        startSychro();
    }

    public void stop(View view) {
        stopSynchro();
    }

    public void erase(View view) {
        if(view.getTag() == null || !(boolean)(view.getTag())) {
            ((Button)view).setText("置灰效果");
            view.setTag(true);
            eraseAble = true;
        }else {
            ((Button)view).setText("擦除效果");
            view.setTag(false);
            eraseAble = false;
        }
    }

    @Override
    View getSychroView() {
        return mView;
    }

    @Override
    protected MapView getMap() {
        if(mapView == null)
            mView = LayoutInflater.from(this).inflate(R.layout.sychro_car_anima, null);
        return mView.findViewById(R.id.spot_map);
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

                if(syncData.getOrder() != null)// 更新订单状态
                    orderStatus = syncData.getOrder().getOrderStatus();

                lastRouteId = syncData.getRoute().getRouteId();// 更新routedId

                if(points.size() != 0)
                    points.clear();
                points.addAll(SHelper.transformLatLngs(syncData.getRoute().getRoutePoints()));
                if(polyline == null)
                    showPolyline(points);// 展示路线
                else
                    showPolyline();

                if(startMarker == null && endMarker == null && points.size() >= 2) {
                    startMarker = addMarker(points.get(0), R.mipmap.line_start_point, 0);// 起点marker
                    endMarker = addMarker(points.get(points.size() - 1), R.mipmap.line_end_point, 0);// 终点marker
                }

                ArrayList<SynchroLocation> locations = syncData.getLocations();// 司机点串
                for(SynchroLocation lo : locations) {
                    Log.e("tag1234", ">>>" + lo.getAttachedIndex());
                }
                if(locations != null && locations.size() != 0) {
                    curEraseLatlng = SHelper.getFirsttLocation(locations);
                    List<LatLng> latLngs = new LinkedList<>(Arrays.asList(SHelper.getLatLngsBySynchroLocation(locations)));
                    if(lastPoint != null)
                        ((LinkedList<LatLng>) latLngs).addFirst(lastPoint);
                    LatLng[] ls = latLngs.toArray(new LatLng[latLngs.size()]);
                    translateAnima(ls);// 平滑移动
                    lastPoint = ls.length > 0 ? ls[ls.length - 1] : null;
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
     * 展示路线
     * @param routePoints
     */
    private void showPolyline(List<LatLng> routePoints) {
        // 调整视图，使中心点为起点终点的中点
        SHelper.fitsWithRoute(mapView.getMap()
                , routePoints
                , CommentUtils.dip2px(this, 32)
                , CommentUtils.dip2px(this, 64)
                , CommentUtils.dip2px(this, 32)
                , CommentUtils.dip2px(this, 64));

        polyline = mapView.getMap().addPolyline
                (new PolylineOptions()
                        .latLngs(points)
                        .color(0xff6cbe89)
                        .arrow(true)
                        .eraseColor(0x00000000));
    }

    private void showPolyline() {
        if(polyline != null) {
            polyline.setPoints(points);
            polyline.setEraseable(eraseAble);
        }
    }

    /**
     * 平滑移动
     * @param points
     */
    private void translateAnima(LatLng[] points) {
        if(points == null || points.length <= 0)
            return;
        // 当司机没有新数据上传，防止拉取回上个点串的最后一个点
        if(points.length == 2 && SHelper.equalOfLatlng(points[0], points[1]))
            return;
        if(carMarker != null)
            carMarker.remove();
        carMarker = mapView.getMap().addMarker(
                new MarkerOptions(points[0])
                        .anchor(0.5f, 0.5f)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.car_bar))
                        //设置此属性 marker 会跟随地图旋转
                        .flat(true)
                        //marker 逆时针方向旋转
                        .clockwise(false));
        Log.e("tag1234", ">>>>>startAnimation()");
        MarkerTranslateAnimator mTranslateAnimator = new MarkerTranslateAnimator(
                //执行此平移动画的 marker
                carMarker,
                //动画持续时间
                animaTime,
                //平移动画点串
                points,
                //marker 是否会根据传入的点串计算并执行旋转动画, marker 方向将与移动方向保持一致
                true);
        mTranslateAnimator.startAnimation();
        mTranslateAnimator.setFloatValuesListener(new MarkerTranslateAnimator.IAnimaFloatValuesListener() {
            @Override
            public void floatValues(LatLng latLng) {
                eraseRoute(latLng);
            }
        });
    }

    private void eraseRoute(LatLng latLng) {
        if(eraseThread == null) {
            eraseThread = new HandlerThread("car_erase_line");
            eraseThread.start();
        }
        if(eraseHandler == null ) {
            eraseHandler = new EraseHandler(eraseThread.getLooper());
        }

        Message message = Message.obtain();
        message.obj = latLng;
        message.what = ERASE_MSG;
        eraseHandler.sendMessage(message);
    }

    class EraseHandler extends Handler {

        public EraseHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try{
                switch (msg.what){
                    case ERASE_MSG:
                        LatLng latLng = (LatLng) (msg.obj);
                        if(latLng != null && polyline != null)
                            polyline.eraseTo(curEraseLatlng != null ? curEraseLatlng.getAttachedIndex() : 0, latLng);
                        eraseHandler.removeMessages(ERASE_MSG);
                        break;
                }
            }catch (Exception e){
                Log.e(LOG_TAG, "erase handler handle message error:" + e.getMessage());
            }
        }
    }

}
