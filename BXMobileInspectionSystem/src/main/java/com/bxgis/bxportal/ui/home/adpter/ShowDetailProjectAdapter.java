package com.bxgis.bxportal.ui.home.adpter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bxgis.bxportal.R;
import com.bxgis.bxportal.bean.BaseInspectionSubProject;
import com.bxgis.bxportal.myInterface.MyOnItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaozhu on 2017/11/20.
 */

public class ShowDetailProjectAdapter extends RecyclerView.Adapter<ShowDetailProjectAdapter.SubProjectAdapterViewHolder> {
    private List<BaseInspectionSubProject> list = new ArrayList<BaseInspectionSubProject>();
    private Context mContext;
    private MyOnItemClickListener mOnItemClickListener;//声明接口

    public void setMyOnItemClickListener(MyOnItemClickListener myOnItemClickListener) {
        this.mOnItemClickListener = myOnItemClickListener;
    }

    public ShowDetailProjectAdapter(Context context, List<BaseInspectionSubProject> data) {
        this.list = data;
        this.mContext = context;

    }

    public void setData(List<BaseInspectionSubProject> data) {
        this.list = data;
        notifyDataSetChanged();
    }

    public void clearData() {
        this.list.clear();
    }


    @Override
    public SubProjectAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subproject_adapter, parent, false);
        return new SubProjectAdapterViewHolder(v, viewType);
    }

    @Override
    public void onBindViewHolder(final SubProjectAdapterViewHolder holder, int position) {
        holder.projectName.setText(position + 1 + "、" + list.get(position).getProject_name());
        holder.subProjectName.setText(list.get(position).getSubproject_name());
        String conment1 = list.get(position).getInspection_content();
        holder.conment.setText(conment1 != null ? conment1 : "");

    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class SubProjectAdapterViewHolder extends RecyclerView.ViewHolder {

        public TextView projectName;
        public TextView subProjectName;
        public TextView conment;
        public int position;


        public SubProjectAdapterViewHolder(View itemView, int viewType) {
            super(itemView);
            init(itemView, viewType);

        }

        private void init(View itemView, int viewType) {
            subProjectName = (TextView) itemView.findViewById(R.id.tv_subproject_name);
            conment = (TextView) itemView.findViewById(R.id.tv_subproject_content);
            projectName = (TextView) itemView.findViewById(R.id.tv_project_name);


        }
    }
}
