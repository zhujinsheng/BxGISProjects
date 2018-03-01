package com.bxgis.bxportal.ui.home.adpter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bxgis.bxportal.MISystemApplication;
import com.bxgis.bxportal.R;
import com.bxgis.bxportal.api.Api;
import com.bxgis.bxportal.bean.BannerOrTypeBean;
import com.bxgis.bxportal.bean.HiddenDangerReViewBean;
import com.bxgis.bxportal.bean.HomepageEntity;
import com.bxgis.bxportal.bean.InspectionProjectBean;
import com.bxgis.bxportal.myInterface.MyOnItemClickListener;
import com.bxgis.bxportal.utils.ImageLoader;
import com.bxgis.bxportal.utils.NoDoubleClickListener;
import com.bxgis.bxportal.utils.WindowUtils;
import com.bxgis.bxportal.utils.date.TimestampUtils;
import com.bxgis.bxportal.widget.CustomViewPager;
import com.bxgis.bxportal.widget.IndicatorView;
import com.bxgis.bxportal.widget.rvBanner.RecyclerViewBanner;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xiaozhu on 2017/11/22.
 */

public class HomeFragmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //type
    public static final int TYPE_REMOVE_SLIDER = 101;
    public static final int TYPE_SLIDER = 0; //banner图列表的标记
    public static final int TYPE_TYPE_PROJECT = 1; //巡检项目分类
    public static final int TYPE_HEAD = 2;  //整改列表头部提示
    public static final int TYPE_TYPE_REVIEW = 3; //整改复查列表
    public static final int TYPE_DIVIDER = 6;
    private CagegoryViewPagerAdapter prijectAdapter;
    private int bannerSize = 1; //banner位置
    private int typeSize = 0; // 项目类型位置
    private int reViewSize = 0; // 整改复查位置

    public static final int ROWS = 8;
    private int itemCount = 0;
    SharedPreferences sp = MISystemApplication.getContext().getSharedPreferences("loginUser", Context.MODE_PRIVATE);
    String token3 = sp.getString("TOKEN1", "");
    private MyOnItemClickListener mOnItemClickListener;//声明接口

    public void setMyOnItemClickListener(MyOnItemClickListener myOnItemClickListener) {
        this.mOnItemClickListener = myOnItemClickListener;
    }

    private Context context;
    private HomepageEntity entity = new HomepageEntity();


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        switch (viewType) {
            case TYPE_REMOVE_SLIDER: //当banner隐藏时候就调用
                return new HolderRemoveBanner(LayoutInflater.from(parent.getContext()).inflate(R.layout.homepage_item_remove_banner, parent, false));
            case TYPE_SLIDER:
                return new HolderBanner(LayoutInflater.from(parent.getContext()).inflate(R.layout.homepage_item_banner, parent, false));
            case TYPE_TYPE_PROJECT:
                return new HolderTypeProject(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_home_entrance, parent, false));
            case TYPE_TYPE_REVIEW:
                return new HolderTypeReview(LayoutInflater.from(parent.getContext()).inflate(R.layout.homepage_item_review, parent, false));
            case TYPE_HEAD:
                return new HolderTypeHead(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_home_head_tip, parent, false));
            case TYPE_DIVIDER:
                return new HolderTypeDivider(LayoutInflater.from(parent.getContext()).inflate(R.layout.homepage_item_divider, parent, false));

            default:
                Log.d("error", "viewholder is null");
                return null;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (null != entity) {
            if (position == 0) { //banner列表
                if (null != entity.A && entity.A.size() > 0) {
                    return TYPE_SLIDER;
                } else {
                    return TYPE_REMOVE_SLIDER;
                }
            } else if (position == 1) {
                return TYPE_TYPE_PROJECT; //类别
            } else if (2 == position) {
                return TYPE_DIVIDER;
            } else if (3 == position) {
                return TYPE_HEAD;
            } else {
                return TYPE_TYPE_REVIEW; //复查列表
            }
        }
        return 0;
    }

    public void setData(HomepageEntity entity) {
        this.entity = entity;
        // 第一个1是banner，第二个1是
        reViewSize = (null == entity.getC() || null == entity.getC().getList() || 0 == entity.getC().getList().size()) ? 0 : entity.getC().getList().size();
        itemCount = 4 + reViewSize;
        notifyDataSetChanged();
    }

    public void clearData() {
        this.entity = null;
    }

    public void removeItem(int pos) { //前提是要减去顶部的4个位置
        entity.getC().getList().remove(pos);
        this.notifyItemChanged(0, getItemCount());
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return itemCount;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //根据position来确定位置
        if (holder instanceof HolderRemoveBanner) {
            // nothing to do
        } else if (holder instanceof HolderBanner) {
            bindTypeBanner((HolderBanner) holder, position);
        } else if (holder instanceof HolderTypeProject) {
            bindTypeProject((HolderTypeProject) holder, position);
        } else if (holder instanceof HolderTypeReview) {
            bindTypeReview((HolderTypeReview) holder, position - (1 + 1 + 1 + 1));
        } else if (holder instanceof HolderTypeDivider) {
            bindTypeDivider((HolderTypeDivider) holder, position);
        } else if (holder instanceof HolderTypeHead) {
            bindTypeHead((HolderTypeHead) holder, position);
        }
    }

    private void bindTypeDivider(final HolderTypeDivider holder, int position) {
//        if (position == 1) {
//            LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
//            holder.dividerLayout.setLayoutParams(ll);
//        }
    }

    private void bindTypeHead(final HolderTypeHead holder, int position) {
//        if (position == 1) {
//            LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
//            holder.headLayout.setLayoutParams(ll);
//        }
    }

    private void bindTypeBanner(final HolderBanner holder, final int pos) {
        if (null == entity)
            return;
        final List<BannerOrTypeBean> bannerList = entity.getA();
        if (bannerList != null && bannerList.size() > 0) {
            holder.mBanner.setRvBannerData(bannerList);
            holder.mBanner.setOnSwitchRvBannerListener(new RecyclerViewBanner.OnSwitchRvBannerListener() {
                @Override
                public void switchBanner(int position, AppCompatImageView bannerView) {
//                    ImageLoader.showImage(context, "http://192.168.3.39:18096" + bannerList.get(position).getPiture_url(), bannerView);
                    String host = bannerList.get(position).getPiture_url();
                    if (!TextUtils.isEmpty(host)) {
                        if (host.contains("gisserver")) {
                            host = (Api.BASE + host.split(":")[2]);
                        }
                        ImageLoader.showImage(context, host + "?_sso_token=" + token3, bannerView);
                    }
                }
            });
        }
    }

    private void bindTypeReview(final HolderTypeReview holder, final int position) {
        if (null == entity)
            return;
        final List<HiddenDangerReViewBean> inspectionList = entity.getC().getList();
        if (inspectionList != null && inspectionList.size() > 0 && position < inspectionList.size()) {
            String hedLevel = inspectionList.get(position).getHid_level();
            String state = inspectionList.get(position).getRectify_init_unit();////整改状态（0，待整改;1，已整改，2待复查;3，复查完成;）
            //// 整改状态
            if (state.equals("2")) {
                state = "待复检";
                holder.type.setTextColor(Color.RED);
            } else if (state.equals("3")) {
                state = "复查完成";

            }
            holder.type.setText(state);
            //登记
            if (hedLevel.equals("0")) {
                hedLevel = "一般";
            } else if (hedLevel.equals("1")) {
                hedLevel = "重大";
                holder.projectName.setTextColor(Color.RED);
            } else {
                hedLevel = "未知";
            }
            holder.title.setText(inspectionList.get(position).getHid_name());
            holder.projectName.setText(hedLevel);

            String time = inspectionList.get(position).getRectify_finish_time();
            if (!TextUtils.isEmpty(time)) {
                holder.date.setText("整改完成时间：" + TimestampUtils.timestamp2String(Long.parseLong(time), "yyyy-MM-dd"));
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

    }

    private void bindTypeProject(final HolderTypeProject holder, final int position) {
        if (null == entity)
            return;
        final List<InspectionProjectBean> projects = entity.getB();
        if (projects != null && projects.size() > 0) {
            LinearLayout.LayoutParams layoutParams12 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) ((float) WindowUtils.getScreenWidth(context) / 2.0f));
            //首页菜单分页
            RecyclerView.LayoutParams entrancelayoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, (int) ((float) WindowUtils.getScreenWidth(context) / 2.0f + 70));
            holder.homeEntranceLayout.setLayoutParams(entrancelayoutParams);
            holder.entranceViewPager.setLayoutParams(layoutParams12);
            LayoutInflater inflater = LayoutInflater.from(context);
            //将RecyclerView放至ViewPager中：
            int pageSize = 8;
            //一共的页数等于 总数/每页数量，并取整。
            int pageCount = (int) Math.ceil(projects.size() * 1.0 / pageSize);
            List<View> viewList = new ArrayList<View>();
            for (int index = 0; index < pageCount; index++) {
                //每个页面都是inflate出一个新recyclerV实例
                RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.item_home_entrance_rv, holder.entranceViewPager, false);
                recyclerView.setLayoutParams(layoutParams12);
                recyclerView.setLayoutManager(new GridLayoutManager(context, 4));
                EntranceAdapter entranceAdapter = new EntranceAdapter(context, projects, index, 8);
                recyclerView.setAdapter(entranceAdapter);
                viewList.add(recyclerView);
            }
            prijectAdapter = new CagegoryViewPagerAdapter(viewList);
            holder.entranceViewPager.setAdapter(prijectAdapter);
            holder.entranceIndicatorView.setIndicatorCount(holder.entranceViewPager.getAdapter().getCount());
            holder.entranceIndicatorView.setCurrentIndicator(holder.entranceViewPager.getCurrentItem());
            holder.entranceViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    holder.entranceIndicatorView.setCurrentIndicator(position);
                }
            });
//            holder. entranceViewPager.setParent(mRecycleView);

        } else {
            LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
            holder.homeEntranceLayout.setLayoutParams(ll);
        }
    }


    public class HolderRemoveBanner extends RecyclerView.ViewHolder {

        public HolderRemoveBanner(View itemView) {
            super(itemView);
        }
    }

    public class HolderBanner extends RecyclerView.ViewHolder {
        @Bind(R.id.banner)
        public RecyclerViewBanner mBanner;

        public HolderBanner(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }

    public class HolderTypeProject extends RecyclerView.ViewHolder {
        @Bind(R.id.home_entrance)
        public LinearLayout homeEntranceLayout;
        @Bind(R.id.main_home_entrance_vp)
        public CustomViewPager entranceViewPager;
        @Bind(R.id.main_home_entrance_indicator)
        public IndicatorView entranceIndicatorView;

        public HolderTypeProject(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }

    public class HolderTypeHead extends RecyclerView.ViewHolder {
        @Bind(R.id.ll_head)
        LinearLayout headLayout;

        public HolderTypeHead(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class HolderTypeDivider extends RecyclerView.ViewHolder {
        @Bind(R.id.divider_layout)
        public View dividerLayout;

        public HolderTypeDivider(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class HolderTypeReview extends RecyclerView.ViewHolder {
        @Bind(R.id.ll_item)
        public View rootView;
        @Bind(R.id.tv_title)
        public TextView title;
        @Bind(R.id.tv_create_time)
        public TextView date;
        @Bind(R.id.tv_state)
        public TextView type;
        @Bind(R.id.tv_project_name)
        public TextView projectName;

        public HolderTypeReview(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
