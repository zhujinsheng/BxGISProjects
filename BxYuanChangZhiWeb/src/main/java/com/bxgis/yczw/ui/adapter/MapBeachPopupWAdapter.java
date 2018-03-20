package com.bxgis.yczw.ui.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.bxgis.yczw.R;
import com.bxgis.yczw.bean.BeachAndBenchlandBean;
import com.bxgis.yczw.bean.CommonBeachBean;
import com.bxgis.yczw.myInterface.MyOnItemClickListener;
import com.bxgis.yczw.utils.NoDoubleClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaozhu on 2017/11/20.
 */

public class MapBeachPopupWAdapter extends RecyclerView.Adapter<MapBeachPopupWAdapter.PopupWidonViewHolder> {
    private List<BeachAndBenchlandBean> list = new ArrayList<>();
    private Context mContext;
    private MyOnItemClickListener mOnItemClickListener;//声明接口

    public void setOnMyItemClickListener(MyOnItemClickListener myOnItemClickListener) {
        this.mOnItemClickListener = myOnItemClickListener;
    }

    public MapBeachPopupWAdapter(Context context, List<BeachAndBenchlandBean> data) {
        this.list = data;
        this.mContext = context;

    }

    public void setData(List<BeachAndBenchlandBean> data) {
        this.list = data;
        notifyDataSetChanged();
    }

    public void clearData() {
        this.list.clear();
    }


    @Override
    public PopupWidonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_map_beach, parent, false);
        return new PopupWidonViewHolder(v, viewType);
    }

    @Override
    public void onBindViewHolder(final PopupWidonViewHolder holder, final int position) {
//        String path = list.get(position).getIcon_url();
//        String projectName = list.get(position).getProject_name()
        holder.distance.setText(list.get(position).getDistance()+" Km");
        holder.name.setText(list.get(position).getName());
     holder.rootView.setOnClickListener(new NoDoubleClickListener() {
                                               @Override
                                               protected void onNoDoubleClick(View v) {
//                                                   int id = holder.getLayoutPosition(); //获取控件从1开始  因为包含了头部
                                                   if (null != mOnItemClickListener) {
                                                       mOnItemClickListener.onItemClick(v, position,list.get(position));
                                                   }
                                               }
                                           }
        );

    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class PopupWidonViewHolder extends RecyclerView.ViewHolder {

        public View rootView;
        public TextView name;
        public TextView distance;

//        public TextView beach;
//        public TextView benchland;
//        public TextView typeTitle;

//        public RecyclerView mRecyclerView;

        public int position;


        public PopupWidonViewHolder(View itemView, int viewType) {
            super(itemView);
            init(itemView, viewType);

        }

        private void init(View itemView, int viewType) {
            rootView = itemView.findViewById(R.id.ll_item);
            name = (TextView) itemView.findViewById(R.id.tv_beach_name);
            distance = (TextView) itemView.findViewById(R.id.tv_beach_distance);
//            beach = (TextView) itemView.findViewById(R.id.tv_map_beach);
//            benchland = (TextView) itemView.findViewById(R.id.tv_map_benchland);
//            typeTitle = (TextView) itemView.findViewById(R.id.tv_beach_title);
//            mRecyclerView = itemView.findViewById(R.id.rv_map_beachType);

        }
    }
}
