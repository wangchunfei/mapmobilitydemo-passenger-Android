package com.map.mobility.passenger.search;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.map.mobility.passenger.BaseActivity;
import com.map.mobility.passenger.R;
import com.tencent.map.navi.agent.address.beans.AddressBean;
import com.tencent.map.navi.agent.address.interfaces.OnAddressListener;
import com.tencent.map.navi.agent.routes.beans.DrivingNaviResponse;
import com.tencent.map.navi.agent.routes.beans.WalkingNaviResponse;
import com.tencent.map.navi.agent.routes.interfaces.OnRouteWalkListener;
import com.tencent.map.navi.agent.routes.interfaces.OnRoutesListener;
import com.tencent.map.navi.agent.sug.beans.SugBean;
import com.tencent.map.navi.agent.sug.interfaces.OnSugListener;

/**
 * 检索sdk
 *
 * 运行demo前，请设置key
 */
public class SearchActivity extends BaseActivity implements IView {

    private static final String LOG_TAG = "search1234";

    IModel model;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);
        new SearchModel(this);
    }

    @Override
    public void setModel(IModel model) {
        this.model = model;
    }

    public void sugSearch(View view) {
        if(model != null)
            model.sugRequest(new OnSugListener() {
                @Override
                public void onSuccess(SugBean sugBean) {
                    Log.e(LOG_TAG, "sug suc !!");
                }

                @Override
                public void onError(int i, String s) {
                    Log.e(LOG_TAG, "sug err : " + s + " !!");
                }
            });

    }

    public void reGroSearch(View view) {
        if(model != null)
            model.reGeocoding(new OnAddressListener() {
                @Override
                public void onSuccess(AddressBean addressBean) {
                    Log.e(LOG_TAG, "re geo suc !!");
                }

                @Override
                public void onError(int i, String s) {
                    Log.e(LOG_TAG, "re geo err : " + s + " !!");
                }
            });
    }

    public void routeDriSearch(View view) {
        if(model != null)
            model.routeOfDriving(new OnRoutesListener() {
                @Override
                public void onSuccess(DrivingNaviResponse drivingNaviResponse) {
                    Log.e(LOG_TAG, "route drive suc !!");
                }

                @Override
                public void onError(int i, String s) {
                    Log.e(LOG_TAG, "route drive err : " + s + " !!");
                }
            });
    }

    public void routeWalkSearch(View view) {
        if(model != null)
            model.routeOfWalking(new OnRouteWalkListener() {
                @Override
                public void onSuccess(WalkingNaviResponse walkingNaviResponse) {
                    Log.e(LOG_TAG, "route walk suc !!");
                }

                @Override
                public void onError(int i, String s) {
                    Log.e(LOG_TAG, "route walk err : " + s + " !!");
                }
            });
    }

    @Override
    public Context getAppContext() {
        return getApplicationContext();
    }
}
