package com.map.mobility.passenger.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class CommontUtils {

    /**
     *  根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getApplicationContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * sp转换成px
     */
    public static int sp2px(Context context,float spValue){
        float fontScale=context.getApplicationContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue*fontScale+0.5f);
    }

    public static void toIntent(Activity a, Class c) {
        Intent intent = new Intent(a, c);
        a.startActivity(intent);
    }
}
