//package com.map.mobility.passenger.synchro;
//
//import android.content.Intent;
//import android.os.Bundle;
//
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.map.mobility.passenger.BaseActivity;
//import com.map.mobility.passenger.R;
//import com.map.mobility.passenger.utils.ToastUtils;
//
//public class SynchroList extends BaseActivity {
//
//    static final int TYPE_CAR_ANIMA_ERASE = 0;// 小车平滑和路线擦除
//
//    static final int TYPE_ROUTE_TRAFFIC = 1;// 路况线
//
//    static final int TYPE_DOTTED_LINE = 2;// 虚线
//
//    private RecyclerView recyclerView;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        setContentView(R.layout.main_list_recy);
//
//        initRecy();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        ToastUtils.INSTANCE().init(this);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//        ToastUtils.INSTANCE().destory();
//    }
//
//    private void initRecy() {
//
//        recyclerView = findViewById(R.id.mobility_recycler_view);
//
//        LinearLayoutManager manager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(manager);
//
//        recyclerView.setAdapter(new SynchroRecy(new SynchroRecy.IClickListener() {
//            @Override
//            public void onClick(int itemPosition) {
//
//                click(itemPosition);
//            }
//        }));
//
//    }
//
//    private void click(int position) {
//        switch (position){
//            case TYPE_CAR_ANIMA_ERASE:
//                toIntent(SynchroCarAnima.class);
//                break;
//            case TYPE_ROUTE_TRAFFIC:
//                toIntent(SychroTraffic.class);
//                break;
//            case TYPE_DOTTED_LINE:
//                toIntent(SychroDottedLine.class);
//                break;
//        }
//    }
//
//    private void toIntent(Class c) {
//        Intent intent = new Intent(this, c);
//        startActivity(intent);
//    }
//}
