package com.map.mobility.passenger.synchro_v2.helper;

import com.map.mobility.passenger.location.bean.MapLocation;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.lssupport.bean.TLSBDriverPosition;
import com.tencent.map.lssupport.bean.TLSBPosition;
import com.tencent.map.lssupport.bean.TLSBRouteTrafficItem;
import com.tencent.map.lssupport.bean.TLSLatlng;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;

import java.util.ArrayList;

public class ConvertHelper {

    public static TLSBDriverPosition tenPoToTLSDPo(TencentLocation tenLo) {
        if(tenLo == null)
            return null;
        TLSBDriverPosition driverPosition = new TLSBDriverPosition();
        driverPosition.setTime(tenLo.getTime());
        driverPosition.setCityCode(tenLo.getCityCode());
        driverPosition.setLatitude(tenLo.getLatitude());
        driverPosition.setLongitude(tenLo.getLongitude());
        driverPosition.setAccuracy(tenLo.getAccuracy());
        driverPosition.setBearing((float) tenLo.getBearing());
        driverPosition.setVelocity(tenLo.getSpeed());
        driverPosition.setTime(tenLo.getTime());
        driverPosition.setAltitude(tenLo.getAltitude());
        driverPosition.setProvider(tenLo.getProvider());
        driverPosition.setCityCode(tenLo.getCityCode());
        return driverPosition;
    }

    public static TLSBPosition tenPoTOTLSPo(MapLocation location) {
        TLSBPosition position = new TLSBPosition();
        position.setLatitude(location.getLatitude());
        position.setLongitude(location.getLongitude());
        position.setProvider(location.getProvider());
        position.setVelocity(location.getSpeed());
        position.setBearing(location.getBearing());
        position.setCityCode(location.getCityCode());
        return position;
    }

    public static ArrayList<TLSLatlng> convertLatLngToTLS(ArrayList<LatLng> latLngs) {
        if(latLngs == null)
            return null;
        ArrayList<TLSLatlng> tls = new ArrayList<>();
        for(LatLng latlng : latLngs) {
            TLSLatlng tl = new TLSLatlng();
            tl.setLatitude(latlng.getLatitude());
            tl.setLongitude(latlng.getLongitude());
            tl.setAltitude(latlng.getAltitude());
            tls.add(tl);
        }
        return tls;
    }

    public static ArrayList<TLSBRouteTrafficItem> convertIntegerToTraffic(ArrayList<Integer> trafficItems) {
        if(trafficItems == null)
            return null;
        int itemSize = trafficItems.size();
        if(itemSize == 0 || itemSize % 3 != 0)
            return null;
        ArrayList<TLSBRouteTrafficItem> tlsTraItems = new ArrayList<>();
        for(int index = 0; index < itemSize; index += 3) {
            TLSBRouteTrafficItem tlsTraItem = new TLSBRouteTrafficItem();
            tlsTraItem.setFrom(trafficItems.get(index));
            tlsTraItem.setTo(trafficItems.get(index + 1));
            tlsTraItem.setColor(trafficItems.get(index + 2));
            tlsTraItems.add(tlsTraItem);
        }
        return tlsTraItems;
    }

    /**
     *  latlng的转换
     * @param list
     */
    public static ArrayList<LatLng> transformLatLngs(ArrayList<TLSLatlng> list) {
        if(list == null){
            return null;
        }
        ArrayList<LatLng> latLngs = new ArrayList<>();
        for(TLSLatlng lstlng : list) {
            latLngs.add(new LatLng(lstlng.getLatitude(), lstlng.getLongitude()));
        }
        return latLngs;
    }
}
