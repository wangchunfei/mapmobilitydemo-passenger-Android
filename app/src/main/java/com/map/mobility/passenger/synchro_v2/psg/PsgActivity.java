package com.map.mobility.passenger.synchro_v2.psg;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;

import com.map.mobility.passenger.R;
import com.map.mobility.passenger.synchro_v2.helper.ConvertHelper;
import com.map.mobility.passenger.synchro_v2.helper.SHelper;
import com.map.mobility.passenger.utils.CommontUtils;
import com.tencent.map.lspassenger.anima.MarkerTranslateAnimator;
import com.tencent.map.lssupport.bean.TLSBDriverPosition;
import com.tencent.map.lssupport.bean.TLSBOrder;
import com.tencent.map.lssupport.bean.TLSBOrderStatus;
import com.tencent.map.lssupport.bean.TLSBOrderType;
import com.tencent.map.lssupport.bean.TLSBRoute;
import com.tencent.map.lssupport.bean.TLSBWayPoint;
import com.tencent.map.lssupport.bean.TLSBWayPointType;
import com.tencent.map.lssupport.bean.TLSDDrvierStatus;
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

public class PsgActivity extends PsgLsActivity implements RadioGroup.OnCheckedChangeListener {

    private static final int ERASE_MSG = 0;

    ArrayList<LatLng> points = new ArrayList<>();// 路线点串
    ArrayList<TLSBWayPoint> wayPoints = new ArrayList<>();// 途经点
    ArrayList<Marker> wayMarkers = new ArrayList<>();
    int[] icons = new int[] {R.mipmap.waypoint1_1// 途经点图标
            , R.mipmap.waypoint1_2
            , R.mipmap.waypoint2_1
            , R.mipmap.waypoint2_2};
    Polyline polyline;
    Marker carMarker;
    LatLng lastPoint;// 保存当前拉取到的最后点

    Marker startMarker, endMarker;// 起终点marker

    HandlerThread eraseThread;
    EraseHandler eraseHandler;

    int animaTime = 5000;// 动画时间
    TLSBDriverPosition curEraseLatlng;

    boolean eraseAble;

    RadioGroup radioGroup;// cur account

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.hh_a:// 顺风单A
                orderId = "test_driver_order_000011";// 顺风车司机订单id
                psgId = "test_passenger_000001";// 顺风车乘客id
                pOrderId = "test_passenger_order_000011";// 乘客子订单id
                curOrderType = TLSBOrderType.TLSDOrderTypeHitchRide;
                curOrderState = TLSBOrderStatus.TLSDOrderStatusNone;
                curDriverState = TLSDDrvierStatus.TLSDDrvierStatusStopped;
                break;
            case R.id.hh_b:// 顺风单B
                orderId = "test_driver_order_000011";// 顺风车司机订单id
                psgId = "test_passenger_000002";// 顺风车乘客id
                pOrderId = "test_passenger_order_000012";// 乘客子订单id
                curOrderType = TLSBOrderType.TLSDOrderTypeHitchRide;
                curOrderState = TLSBOrderStatus.TLSDOrderStatusNone;
                curDriverState = TLSDDrvierStatus.TLSDDrvierStatusStopped;
                break;
            case R.id.fast_c:// 快车单
                orderId = "xc_1112";// 快车订单id
                psgId = "OU_xc_10001_1";// 快车乘客id
                pOrderId = "";// 乘客子订单id
                curOrderType = TLSBOrderType.TLSDOrderTypeNormal;
                curOrderState = TLSBOrderStatus.TLSDOrderStatusNone;
                curDriverState = TLSDDrvierStatus.TLSDDrvierStatusStopped;
                break;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.sychro_v2_psg_layout);
        super.onCreate(savedInstanceState);
    }

    @Override
    void init() {
        mapView = findViewById(R.id.passenger_map);
        tencentMap = mapView.getMap();
        radioGroup = findViewById(R.id.cur_account);
        radioGroup.setOnCheckedChangeListener(this);
    }

    @Override
    void clearUi() {
        if(polyline != null)
            polyline.remove();
        if(startMarker != null)
            startMarker.remove();
        if(endMarker != null)
            endMarker.remove();
        if(wayMarkers != null) {
            for(Marker m : wayMarkers) {
                m.remove();
            }
            wayMarkers.clear();
        }
        if(carMarker != null)
            carMarker.remove();
        polyline = null;
        startMarker = null;
        endMarker = null;
        carMarker = null;
    }

    @Override
    void updateDriverInfo(TLSBRoute route, TLSBOrder order, ArrayList<TLSBDriverPosition> pos) {

        if(route == null || order == null || pos == null)
            return;

        if(points.size() != 0)
            points.clear();
        points.addAll(ConvertHelper.transformLatLngs(route.getPoints()));

        // 展示路线
        polyline = polyline == null ? showPolyline(points) : showPolyline();
        polyline.setEraseable(eraseAble);

        // 起终点
        if(startMarker == null && endMarker == null && points.size() >= 2) {
            startMarker = addMarker(points.get(0), R.mipmap.line_start_point, 0);// 起点marker
            endMarker = addMarker(points.get(points.size() - 1), R.mipmap.line_end_point, 0);// 终点marker
        }

        // 添加途经点marker
        if(route.getWayPoints() != null && route.getWayPoints().size() != wayPoints.size()) {
            removeWaysMarker();
            wayPoints.addAll(route.getWayPoints());

            ArrayList<TLSBWayPoint> ws = new ArrayList<>();
            ws.addAll(route.getWayPoints());
            int curIndex = 0;
            while (ws.size() != 0) {
                TLSBWayPoint wayPoint = ws.get(0);
                if(ws.size() == 1) {// 只有一个途经点
                    addWaysMarker(wayPoint, curIndex);
                    ws.remove(0);
                    break;
                }
                for(int index = 1; index < ws.size(); index ++) {
                    if(ws.get(index).getPassengerOrderId().equals(wayPoint.getPassengerOrderId())) {
                        addWaysMarker(ws.get(index), curIndex);
                        ws.remove(index);
                        break;
                    }
                }
                addWaysMarker(wayPoint, curIndex);
                ws.remove(0);
                curIndex += 2;
            }
        }

        if(pos.size() != 0) {
            curEraseLatlng = SHelper.getFirsttLocation(pos);
            List<LatLng> latLngs = new LinkedList<>(Arrays.asList(SHelper.getLatLngsBySynchroLocation(pos)));
            if(lastPoint != null)
                ((LinkedList<LatLng>) latLngs).addFirst(lastPoint);
            LatLng[] ls = latLngs.toArray(new LatLng[latLngs.size()]);
            translateAnima(ls);// 平滑移动
            lastPoint = ls.length > 0 ? ls[ls.length - 1] : null;
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
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_icon_driver))
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

    /**
     * 展示路线
     * @param routePoints
     */
    private Polyline showPolyline(List<LatLng> routePoints) {
        // 调整视图，使中心点为起点终点的中点
        if(points.size() >= 2) {
            SHelper.fitsWithRoute(mapView.getMap(), routePoints
                    , CommontUtils.dip2px(this, 32)
                    , CommontUtils.dip2px(this, 64)
                    , CommontUtils.dip2px(this, 32)
                    , CommontUtils.dip2px(this, 64));
        }
        polyline = mapView.getMap().addPolyline(new PolylineOptions()
                .latLngs(points)
                .color(0xff6cbe89)
                .arrow(true).eraseColor(0x00000000));
        return polyline;
    }

    private Polyline showPolyline() {
        if(polyline != null) {
            polyline.setPoints(points);
        }
        return polyline;
    }

    /**
     * 添加途经点
     * @param way
     * @param curIndex
     */
    private void addWaysMarker(TLSBWayPoint way, int curIndex) {
        if(way == null && curIndex >= icons.length)
            return;
        if(way.getWayPointType() == TLSBWayPointType.TLSDWayPointTypeGetIn) {// 上车点
            wayMarkers.add(addMarker(new LatLng(way.getPosition().getLatitude()
                            , way.getPosition().getLongitude()), icons[curIndex]
                    , 0, 0.5f, 1f));
        }else if(way.getWayPointType() == TLSBWayPointType.TLSDWayPointTypeGetOff) {// 下车点
            wayMarkers.add(addMarker(new LatLng(way.getPosition().getLatitude()
                    , way.getPosition().getLongitude()), icons[curIndex + 1]
                    , 0, 0.5f, 1f));
        }
    }

    private void removeWaysMarker() {
        if(wayMarkers.size() != 0) {
            for(Marker m : wayMarkers) {
                m.remove();
            }
            wayMarkers.clear();
        }
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
                            polyline.eraseTo(curEraseLatlng != null ? curEraseLatlng.getPointIndex() : 0, latLng);
                        eraseHandler.removeMessages(ERASE_MSG);
                        break;
                }
            }catch (Exception e){
                Log.e(LOG_TAG, "erase handler handle message error:" + e.getMessage());
            }
        }
    }
}
