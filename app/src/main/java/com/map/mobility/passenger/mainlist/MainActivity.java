package com.map.mobility.passenger.mainlist;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.map.mobility.passenger.BaseActivity;
import com.map.mobility.passenger.R;
import com.map.mobility.passenger.nearbycar.NearbyCarUi;
import com.map.mobility.passenger.spot.SpotList;
import com.map.mobility.passenger.synchro.SynchroList;
import com.map.mobility.passenger.utils.ToastUtils;

/**
 *  出行demo乘客端
 *
 *  @author mjzuo
 */
public class MainActivity extends BaseActivity {

    /**
     * 地图
     */
    static final int MAP_HELPER = 0;

    /**
     * 周边车辆
     */
    static final int CAR_PREVIEW = 1;

    /**
     * 上车点
     */
    static final int CAR_POI = 2;

    /**
     * 司乘同显-乘客端
     */
    static final int S_DISPLAY = 3;

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_list_recy);

        if (Build.VERSION.SDK_INT >= 23) {
            String[] permissions = {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };

            if (checkSelfPermission(permissions[0]) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(permissions, 0);
            } else {

            }
        }

        initRecy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        ToastUtils.INSTANCE().init(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ToastUtils.INSTANCE().destory();
    }

    private void initRecy() {

        recyclerView = findViewById(R.id.mobility_recycler_view);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        recyclerView.setAdapter(new MainRecycler(new MainRecycler.IClickListener() {
            @Override
            public void onClick(int itemPosition) {

                click(itemPosition);
            }
        }));

    }

    private void click(int position) {
        switch (position){
            case MAP_HELPER:
                ToastUtils.INSTANCE().Toast("待添加");
                break;
            case CAR_PREVIEW:
                toIntent(NearbyCarUi.class);
                break;
            case CAR_POI:
                toIntent(SpotList.class);
                break;
            case S_DISPLAY:
                toIntent(SynchroList.class);
                break;
        }
    }

    private void toIntent(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }

}
