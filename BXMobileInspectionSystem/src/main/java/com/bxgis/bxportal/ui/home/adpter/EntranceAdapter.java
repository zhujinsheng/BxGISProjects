package com.bxgis.bxportal.ui.home.adpter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bxgis.bxportal.MISystemApplication;
import com.bxgis.bxportal.R;
import com.bxgis.bxportal.api.Api;
import com.bxgis.bxportal.bean.InspectionProjectBean;
import com.bxgis.bxportal.ui.inspection.NowInspecrionRelaseActivity;
import com.bxgis.bxportal.utils.ImageLoader;

import java.util.List;

/**
 * 首页类别的分页菜单项列表适配器
 */
public class EntranceAdapter extends RecyclerView.Adapter<EntranceAdapter.EntranceViewHolder> {

    private List<InspectionProjectBean> mDatas;

    /**
     * 页数下标,从0开始(通俗讲第几页)
     */
    private int mIndex;

    /**
     * 每页显示最大条目个数
     */
    private int mPageSize;

    private Context mContext;

    private final LayoutInflater mLayoutInflater;

    private List<InspectionProjectBean> projects;
    SharedPreferences sp = MISystemApplication.getContext().getSharedPreferences("loginUser", Context.MODE_PRIVATE);
    String token1 = sp.getString("TOKEN1", "");

    public EntranceAdapter(Context context, List<InspectionProjectBean> datas, int index, int pageSize) {
        this.mContext = context;
        this.projects = datas;
        mPageSize = pageSize;
        mDatas = datas;
        mIndex = index;
        mLayoutInflater = LayoutInflater.from(context);

    }


    public void insert(InspectionProjectBean project, int position) {
        projects.add(position, project);
        notifyItemInserted(position);
    }

    public void setData(List<InspectionProjectBean> list) {
        this.projects = list;
        notifyDataSetChanged();
    }

    @Override
    public EntranceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_home_entrance, parent, false);
        EntranceViewHolder vh = new EntranceViewHolder(v, true);
        return vh;
    }

    @Override
    public void onBindViewHolder(EntranceViewHolder holder, int position) {
        /**
         * 在给View绑定显示的数据时，计算正确的position = position + mIndex * mPageSize，
         */
        final int pos = position + mIndex * mPageSize;
        holder.entranceNameTextView.setText(projects.get(pos).getProject_name());
        String url = projects.get(pos).getIcon_url();
        if (url.contains("gisserver")) {
            url = (Api.BASE + url.split(":")[2]);
        }
        //一定要加上token不然会请求不到就产生未登录页面
        ImageLoader.showCircleImageByError(mContext, url + "?_sso_token=" + token1, holder.entranceIconImageView, R.mipmap.default1);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InspectionProjectBean entrance = projects.get(pos);
                if (null != entrance) {
                    Intent intent = new Intent(mContext, NowInspecrionRelaseActivity.class);
                    intent.putExtra("InspectionProject", entrance);
//                intent.putExtra("InspectionProject",entrance.getId());
                    mContext.startActivity(intent);
                }
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position + mIndex * mPageSize;
    }

    @Override
    public int getItemCount() {
        return mDatas.size() > (mIndex + 1) * mPageSize ? mPageSize : (mDatas.size() - mIndex * mPageSize);
    }

    class EntranceViewHolder extends RecyclerView.ViewHolder {
        private TextView entranceNameTextView;
        private ImageView entranceIconImageView;

        public EntranceViewHolder(View itemView, boolean isItem) {
            super(itemView);
            if (isItem) {
                entranceIconImageView = (ImageView) itemView.findViewById(R.id.entrance_image);
                entranceNameTextView = (TextView) itemView.findViewById(R.id.entrance_name);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) ((float) (mContext.getResources().getDisplayMetrics().widthPixels) / 4.0f));
                itemView.setLayoutParams(layoutParams);
            }
        }
    }
}
