package com.map.mobility.passenger.map;

import android.content.Context;

import com.map.mobility.passenger.map.sensor.ISensor;
import com.map.mobility.passenger.map.sensor.MyOrientationSensor;

/**
 *  这是使用陀螺仪传感器的helper类
 *
 * @author mingjiezuo
 */
public class CompassManager {

    /** 这是传感器manager*/
    ISensor sensorManager;
    /** 传感器方向的监听类*/
    ISensor.IDirectionListener listener;

    /**上下文*/
    Context mContext;

    public CompassManager(Context context, ISensor.IDirectionListener listener) {
        mContext = context;
        this.listener = listener;
    }

    public void startSensor(){
        sensorManager = new MyOrientationSensor(mContext.getApplicationContext());
        ((MyOrientationSensor) sensorManager).registerDirectionListener(listener);
        ((MyOrientationSensor) sensorManager).startOrientationSensor();
    }

    public void stopSensor() {
        if(sensorManager != null){
            ((MyOrientationSensor) sensorManager).stopOrientationSensor();
        }
    }
}
