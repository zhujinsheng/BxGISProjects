package com.bxgis.yczw.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bxgis.yczw.R;
import com.bxgis.yczw.bean.ProjectTypeBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Author : xiaozhu
 * Time:2018/3/15
 * Description:
 */
public class GridViewAdapter extends BaseAdapter {
    private List<ProjectTypeBean> projectList =new ArrayList<>();
     private Context mContext;
    public GridViewAdapter(Context context){
        this.mContext =context;
    }

    public void setData(List<ProjectTypeBean> data) {
        this.projectList = data;
        notifyDataSetChanged();

    }
    @Override
    public int getCount() {
        return projectList !=null ?projectList.size():0;
    }

    @Override
    public Object getItem(int i) {
        ProjectTypeBean bean =projectList.get(i);
        return bean;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.main_grid_item, parent, false);
            viewHolder.title = convertView.findViewById(R.id.tv_item_title);
            viewHolder.icon = convertView.findViewById(R.id.iv_grid_icon);
            //通过setTag将convertView与viewHolder关联
            convertView.setTag(viewHolder);
        }else{
            //如果缓存池中有对应的view缓存，则直接通过getTag取出viewHolder
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // 取出bean对象
        ProjectTypeBean bean = projectList.get(i);
        // 设置控件的数据
        viewHolder.icon.setImageResource(bean.getIconId());
        viewHolder.title.setText(bean.getTitle());
        return convertView;
    }

    // ViewHolder用于缓存控件，两个个属性分别对应item布局文件的两个控件
    class ViewHolder{
        public ImageView icon;
        public TextView title;

    }
}
