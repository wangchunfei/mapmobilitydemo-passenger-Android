package com.map.mobility.passenger.spot;

import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.map.mobility.passenger.BaseActivity;
import com.map.mobility.passenger.R;
import com.map.mobility.passenger.utils.ToastUtils;

public class SpotList extends BaseActivity {

    static final int TYPE_UI = 0;// default ui

    static final int TYPE_MARKER = 1;// marker

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_list_recy);

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

        recyclerView.setAdapter(new SpotRecy(new SpotRecy.IClickListener() {
            @Override
            public void onClick(int itemPosition) {

                click(itemPosition);
            }
        }));

    }

    private void click(int position) {
        switch (position){
            case TYPE_UI:
                toIntent(SpotBaseWithUI.class);
                break;
            case TYPE_MARKER:
                ToastUtils.INSTANCE().Toast("待添加");
                break;
        }
    }

    private void toIntent(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }
}
