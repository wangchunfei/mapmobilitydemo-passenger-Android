package com.map.mobility.passenger.utils;

import android.content.Context;
import android.widget.Toast;

import com.tencent.map.lssupport.util.StringUtils;

public class ToastUtils {

    private static ToastUtils mToast;
    static Context mContext;

    private ToastUtils(){}

    public static ToastUtils INSTANCE() {
        if(mToast == null) {
            synchronized (ToastUtils.class){
                if(mToast == null){
                    mToast = new ToastUtils();
                }
            }
        }
        return mToast;
    }

    public static void init(Context applicationContext) {
        mContext = applicationContext;
    }

    public void destory() {
        mContext = null;
        mToast = null;
    }

    public void Toast(String msg) {
        if(!StringUtils.isEmpty(msg) && mContext != null){
            Toast.makeText(mContext.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }
}
