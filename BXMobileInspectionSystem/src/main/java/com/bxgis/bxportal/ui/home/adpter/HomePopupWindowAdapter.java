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
import com.bxgis.bxportal.bean.InspectionProjectBean;
import com.bxgis.bxportal.myInterface.MyOnItemClickListener;
import com.bxgis.bxportal.utils.ImageLoader;
import com.bxgis.bxportal.utils.NoDoubleClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaozhu on 2017/11/20.
 */

public class HomePopupWindowAdapter extends RecyclerView.Adapter<HomePopupWindowAdapter.PopupWidonViewHolder> {
    private List<InspectionProjectBean> list = new ArrayList<>();
    private Context mContext;
    private MyOnItemClickListener mOnItemClickListener;//声明接口

    public void setOnMyItemClickListener(MyOnItemClickListener myOnItemClickListener) {
        this.mOnItemClickListener = myOnItemClickListener;
    }

    public HomePopupWindowAdapter(Context context, List<InspectionProjectBean> data) {
        this.list = data;
        this.mContext = context;

    }

    public void setData(List<InspectionProjectBean> data) {
        this.list = data;
        notifyDataSetChanged();
    }

    public void clearData() {
        this.list.clear();
    }


    @Override
    public PopupWidonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_popupwindow, parent, false);
        return new PopupWidonViewHolder(v, viewType);
    }

    @Override
    public void onBindViewHolder(final PopupWidonViewHolder holder, int position) {
        String path = list.get(position).getIcon_url();
        String projectName = list.get(position).getProject_name();
        if (!TextUtils.isEmpty(path)) {
            ImageLoader.showCircleImageByError(mContext, path, holder.icon, R.mipmap.default1);
        }
        if (!TextUtils.isEmpty(projectName)) {
            holder.title.setText(projectName);
        }
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

    public class PopupWidonViewHolder extends RecyclerView.ViewHolder {

        public View rootView;
        public TextView title;
        public ImageView icon;

        public int position;


        public PopupWidonViewHolder(View itemView, int viewType) {
            super(itemView);
            init(itemView, viewType);

        }

        private void init(View itemView, int viewType) {
            title = (TextView) itemView.findViewById(R.id.popupwindows_title);
            icon = (ImageView) itemView.findViewById(R.id.popupwindows_image);
            rootView = itemView.findViewById(R.id.item_popupwindown);


        }
    }
}
