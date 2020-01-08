package com.map.mobility.passenger.synchro;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.map.mobility.passenger.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class SynchroRecy extends RecyclerView.Adapter<SynchroRecy.ViewHolder> {

    public static final String LOG_TAG = "navi";

    private ArrayList<String> mobilityDate = new ArrayList<>();

    /**
     * 外部的点击监听
     */
    private SynchroRecy.IClickListener clickListener;

    public SynchroRecy(SynchroRecy.IClickListener listener) {
        this.clickListener = listener;

        if(mobilityDate.size() != 0)
            mobilityDate.clear();
        mobilityDate.add("小车平滑与路线擦除");
        mobilityDate.add("路况线");
    }

    @NonNull
    @Override
    public SynchroRecy.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.main_list_recy_item, parent, false);
        return new SynchroRecy.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SynchroRecy.ViewHolder holder, int position) {
        String content = mobilityDate.get(position);
        holder.tvContent.setText(content);

        holder.listener.position = position;
        holder.listener.setViewHolder(holder);
        holder.allLayout.setOnClickListener(holder.listener);
    }

    @Override
    public int getItemCount() {
        return mobilityDate.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        SynchroRecy.MyClickListener listener = new SynchroRecy.MyClickListener();

        LinearLayout allLayout;
        TextView tvContent;

        public ViewHolder(View view) {
            super(view);
            tvContent = view.findViewById(R.id.tv_recycler_item_content);
            allLayout = view.findViewById(R.id.all_layout);
        }
    }

    class MyClickListener implements View.OnClickListener {

        public WeakReference<SynchroRecy.ViewHolder> wrf;
        public int position;

        public void setViewHolder(SynchroRecy.ViewHolder viewHolder) {
            wrf = new WeakReference<>(viewHolder);
        }

        @Override
        public void onClick(View v) {
            if(wrf == null || wrf.get() == null){
                Log.e(LOG_TAG, "view holder or wrf null");
                return;
            }
            if(clickListener != null){
                clickListener.onClick(position);
            }
        }
    }

    interface IClickListener {
        void onClick(int itemPosition);
    }
}


