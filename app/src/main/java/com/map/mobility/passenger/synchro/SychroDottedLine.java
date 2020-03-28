//package com.map.mobility.passenger.synchro;
//
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//
//import com.map.mobility.passenger.R;
//import com.map.mobility.passenger.helper.SHelper;
//import com.map.mobility.passenger.utils.CommontUtils;
//import com.tencent.map.locussynchro.TencentLocusSynchro;
//import com.tencent.map.locussynchro.model.Order;
//import com.tencent.map.locussynchro.model.RouteUploadError;
//import com.tencent.map.locussynchro.model.SyncData;
//import com.tencent.tencentmap.mapsdk.maps.MapView;
//import com.tencent.tencentmap.mapsdk.maps.model.BitmapDescriptorFactory;
//import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
//import com.tencent.tencentmap.mapsdk.maps.model.Marker;
//import com.tencent.tencentmap.mapsdk.maps.model.Polyline;
//import com.tencent.tencentmap.mapsdk.maps.model.PolylineOptions;
//
//import java.util.ArrayList;
//
//public class SychroDottedLine extends SynchroBase {
//
//    ArrayList<LatLng> points = new ArrayList<>();// 路线点串
//
//    Polyline polyline;
//    Marker startMarker, endMarker;
//
//    View view;
//
//    public void start(View view) {
//        synchroInit();
//        startSychro();
//    }
//
//    public void dotLine(View view) {
//        drawLine(0);
//    }
//
//    public void rectLine(View view) {
//        drawLine(1);
//    }
//
//    public void stop(View view) {
//        stopSynchro();
//        if(polyline != null)
//            polyline.remove();
//    }
//
//    @Override
//    View getSychroView() {
//        return view;
//    }
//
//    @Override
//    protected MapView getMap() {
//        if(view == null)
//            view = LayoutInflater.from(this).inflate(R.layout.sychro_dotted_line, null);
//        return view.findViewById(R.id.dotted_line_view);
//    }
//
//    @Override
//    TencentLocusSynchro.DataSyncListener getSyncListener() {
//
//        return new TencentLocusSynchro.DataSyncListener() {
//
//            /**
//             * 订单信息同步
//             * 为保证司乘同显功能正常，请开发者务必返回最新的OrderInfo
//             */
//            @Override
//            public Order onOrderInfoSynchro() {
//                Order order = new Order();
//                order.setOrderId(orderId);
//                order.setOrderStatus(orderStatus);
//                return order;
//            }
//
//            /**
//             * 拉取成功
//             * @param syncData
//             */
//            @Override
//            public void onSyncDataUpdated(SyncData syncData) {
//                if(syncData == null || syncData.getRoute() == null
//                        || syncData.getRoute().getRoutePoints() == null) {
//                    Log.e(LOG_TAG, "syncData or route or points of onSyncDataUpdated null");
//                    return;
//                }
//
//                if(points.size() != 0)
//                    points.clear();
//                points.addAll(SHelper.transformLatLngs(syncData.getRoute().getRoutePoints()));
//
//                showRoute(syncData);
//
//                if(startMarker == null && endMarker == null && points.size() >= 2) {// 添加起点终点marker
//                    startMarker = addMarker(points.get(0), R.mipmap.line_start_point, 0);
//                    endMarker = addMarker(points.get(points.size() - 1), R.mipmap.line_end_point, 0);
//                }
//            }
//
//            /**
//             * 路线上传失败
//             * 开发者需要重新上传路线重试
//             */
//            @Override
//            public void onRouteUploadFailed(RouteUploadError routeUploadError) {
//                Log.e(LOG_TAG, ">>>onRouteUploadFailed " + routeUploadError);
//            }
//
//            /**
//             * 路线上传成功
//             */
//            @Override
//            public void onRouteUploadComplete() {
//                Log.d(LOG_TAG, ">>>onRouteUploadComplete()");
//            }
//
//            /**
//             * 定位点上传失败
//             */
//            @Override
//            public void onLocationUploadFailed(RouteUploadError routeUploadError) {
//                Log.e(LOG_TAG, ">>>onLocationUploadFailed()" + routeUploadError);
//            }
//
//            /**
//             * 定位点上传成功
//             */
//            @Override
//            public void onLocationUploadComplete() {
//                Log.d(LOG_TAG, "onLocationUploadComplete()");
//            }
//        };
//    }
//
//    private void showRoute(SyncData syncData) {
//        if(syncData == null || syncData.getRoute() == null)
//            return;
//
//        if(polyline != null)
//            polyline.remove();
//
//        // 调整视图中心点
//        SHelper.fitsWithRoute(mapView.getMap()
//                , points
//                , CommontUtils.dip2px(this, 32)
//                , CommontUtils.dip2px(this, 64)
//                , CommontUtils.dip2px(this, 32)
//                , CommontUtils.dip2px(this, 64));
//
//        int width = (int) (10 * getResources().getDisplayMetrics().density + 0.5);
//
//        polyline = mapView.getMap().addPolyline(new PolylineOptions()
//                .addAll(points)
//                .width(width)
//                .color(0xff6cbe89)
//                .arrow(true));
//    }
//
//    /**
//     * 绘制虚线
//     * @param type
//     */
//    private void drawLine(int type) {
//        if(polyline == null)
//            return;
//        polyline.remove();
//
//        int width = (int) (10 * getResources().getDisplayMetrics().density + 0.5);
//
//        PolylineOptions options = new PolylineOptions()
//                .addAll(points)
//                .width(width)
//                .color(0xff6cbe89)
//                .arrow(true);
//        if(type == 0) {
//            options.lineType(PolylineOptions.LineType.LINE_TYPE_DOTTEDLINE)// 圆点虚线方法
//                    .colorTexture(BitmapDescriptorFactory.fromResource(R.mipmap.dot_bule_icon));
//        }else if(type == 1) {
//            ArrayList<Integer> l = new ArrayList<>();// 矩形虚线
//            // 虚线的实线部分长度
//            l.add(30);
//            // 虚线的空白部分长度
//            l.add(5);
//            options.pattern(l);
//        }
//        polyline = mapView.getMap().addPolyline(options);
//    }
//}
