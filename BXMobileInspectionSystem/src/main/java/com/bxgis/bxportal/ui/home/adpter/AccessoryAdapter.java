package com.bxgis.bxportal.ui.home.adpter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bxgis.bxportal.R;
import com.bxgis.bxportal.myInterface.MyOnItemClickListener;
import com.bxgis.bxportal.utils.NoDoubleClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaozhu on 2017/11/20.
 */

public class AccessoryAdapter extends RecyclerView.Adapter<AccessoryAdapter.AccessoryAdapterViewHolder> {
    private List<String> list = new ArrayList<>();
    private Context mContext;
    private MyOnItemClickListener mOnItemClickListener;//声明接口

    public void setMyOnItemClickListener(MyOnItemClickListener myOnItemClickListener) {
        this.mOnItemClickListener = myOnItemClickListener;
    }

    public AccessoryAdapter(Context context, List<String> data) {
        this.list = data;
        this.mContext = context;

    }

    public void setData(List<String> data) {
        this.list = data;
        notifyDataSetChanged();
    }

    public void clearData() {
        this.list.clear();
    }


    @Override
    public AccessoryAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inspection_accessory, parent, false);
        return new AccessoryAdapterViewHolder(v, viewType);
    }

    @Override
    public void onBindViewHolder(final AccessoryAdapterViewHolder holder, int position) {
        String path = list.get(position);
        String pathName = path.substring(path.lastIndexOf('/') + 1);
        String suffix = pathName.split("[.]")[1];
        holder.fileName.setText(pathName);
        if (suffix.equalsIgnoreCase("png") || suffix.contains("jpg") || suffix.equalsIgnoreCase("jpeg")) {
//            Glide.with(mContext).load(path).thumbnail(0.5f).error(R.mipmap.image).into(holder.icon);
            holder.icon.setImageResource(R.mipmap.image);
        } else if (suffix.equalsIgnoreCase("xls") || suffix.equalsIgnoreCase("xlsx")) {
            holder.icon.setImageResource(R.mipmap.axls);
        } else if (suffix.equalsIgnoreCase("doc")) {
            holder.icon.setImageResource(R.mipmap.word);
        } else {
            holder.icon.setImageResource(R.mipmap.unknown);
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

    public class AccessoryAdapterViewHolder extends RecyclerView.ViewHolder {

        public View rootView;
        public TextView fileName;
        public ImageView icon;

        public int position;


        public AccessoryAdapterViewHolder(View itemView, int viewType) {
            super(itemView);
            init(itemView, viewType);

        }

        private void init(View itemView, int viewType) {

            fileName = (TextView) itemView.findViewById(R.id.tv_accessory_title);
            icon = (ImageView) itemView.findViewById(R.id.iv_accessory_logo);
            rootView = itemView.findViewById(R.id.ll_item);


        }
    }
}
