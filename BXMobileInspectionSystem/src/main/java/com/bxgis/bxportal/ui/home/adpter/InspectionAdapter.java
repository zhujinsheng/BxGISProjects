package com.bxgis.bxportal.ui.home.adpter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bxgis.bxportal.R;
import com.bxgis.bxportal.bean.BaseInspection;
import com.bxgis.bxportal.myInterface.MyOnItemClickListener;
import com.bxgis.bxportal.utils.NoDoubleClickListener;
import com.bxgis.bxportal.utils.date.TimestampUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaozhu on 2017/11/20.
 */

public class InspectionAdapter extends RecyclerView.Adapter<InspectionAdapter.InspectionAdapterViewHolder> {
    private List<BaseInspection> list = new ArrayList<BaseInspection>();
    private Context mContext;
    private MyOnItemClickListener mOnItemClickListener;//声明接口

    public void setMyOnItemClickListener(MyOnItemClickListener myOnItemClickListener) {
        this.mOnItemClickListener = myOnItemClickListener;
    }

    public InspectionAdapter(Context context, List<BaseInspection> data) {
        this.list = data;
        this.mContext = context;

    }

    public void setData(List<BaseInspection> data) {
        this.list = data;
        notifyDataSetChanged();
    }

    public void clearData() {
        this.list.clear();
    }


    @Override
    public InspectionAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_inspection, parent, false);
        return new InspectionAdapterViewHolder(v, viewType);
    }

    @Override
    public void onBindViewHolder(final InspectionAdapterViewHolder holder, int position) {
        int inspecton_type =list.get(position).getInspection_type(); //完成 未完成
        int iType = list.get(position).getType();
        switch (iType) {
            case 0:
                holder.type.setText("巡检计划");
                break;
            case 1:
                holder.type.setText("巡检任务");
                break;
            case 2:

                holder.type.setText("现场巡检");
                if (inspecton_type == 0) {
                    holder.iconShow.setVisibility(View.VISIBLE);
                    holder.iconShow.setImageResource(R.mipmap.to_feedback);
                }
                break;
            case 3:
                holder.type.setText("整改复查");
                break;
        }
        holder.title.setText(list.get(position).getInspection_name());
        holder.projectName.setText(list.get(position).getInspection_project());
        String tempT = list.get(position).getEnd_time();
        String time = "";
        if (!TextUtils.isEmpty(tempT)) {
            if (System.currentTimeMillis() > Long.parseLong(tempT)&&inspecton_type == 0) {
//                 holder.iconShow.setVisibility(View.VISIBLE);
                holder.iconShow.setImageResource(R.mipmap.out_of_date);
            }
            time = TimestampUtils.timestamp2String(Long.parseLong(tempT), "yyyy-MM-dd");
        }
        holder.date.setText(time);
        holder.projectName.setText(list.get(position).getInspection_project());
        holder.itemView.setOnClickListener(new NoDoubleClickListener() {
                                               @Override
                                               protected void onNoDoubleClick(View v) {
                                                   int id = holder.getLayoutPosition(); //获取控件从1开始  因为包含了头部
                                                   if (null != mOnItemClickListener) {
                                                       mOnItemClickListener.onItemClick(v, id);
                                                   }
                                               }
                                           }
        );
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class InspectionAdapterViewHolder extends RecyclerView.ViewHolder {

        public View rootView;
        public TextView title;
        public TextView date;
        public TextView type;
        public TextView projectName;
        public ImageView iconShow;
        public int position;


        public InspectionAdapterViewHolder(View itemView, int viewType) {
            super(itemView);
            init(itemView, viewType);

        }

        private void init(View itemView, int viewType) {

            title = (TextView) itemView.findViewById(R.id.tv_title);
            date = (TextView) itemView.findViewById(R.id.tv_create_time);
            type = (TextView) itemView.findViewById(R.id.tv_type);
            iconShow = (ImageView) itemView.findViewById(R.id.iv_tip);
            projectName = (TextView) itemView.findViewById(R.id.tv_project_name);
            rootView = itemView.findViewById(R.id.ll_item);


        }
    }
}
