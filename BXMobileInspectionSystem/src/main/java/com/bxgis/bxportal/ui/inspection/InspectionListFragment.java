//package com.bxgis.mmgk.ui.inspection;
//
//import android.os.Bundle;
//import android.support.v4.widget.SwipeRefreshLayout;
//import android.support.v7.widget.DefaultItemAnimator;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.bxgis.mmgk.R;
//import com.bxgis.mmgk.base.BaseFragment;
//import com.bxgis.mmgk.bean.BaseInspection;
//import com.bxgis.mmgk.ui.home.adpter.InspectionAdapter;
//import com.bxgis.mmgk.ui.inspection.contract.InspectionContract;
//import com.bxgis.mmgk.ui.inspection.model.InspectionModel;
//import com.bxgis.mmgk.ui.inspection.presenter.InspectionPresenter;
//import com.bxgis.mmgk.utils.WindowUtils;
//import com.bxgis.mmgk.widget.customRecyclerView.ProgressStyle;
//import com.bxgis.mmgk.widget.customRecyclerView.SRecyclerView;
//import com.bxgis.mmgk.widget.customRecyclerView.listener.OnLoadMoreListener;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import butterknife.Bind;
//
///**
// * Created by xiaozhu on 2017/11/17.
// */
//
//public class InspectionListFragment extends BaseFragment<InspectionPresenter> implements InspectionContract.View, SwipeRefreshLayout.OnRefreshListener {
//    @Bind(R.id.s_refresh)
//    SwipeRefreshLayout mSwipeRefreshLayout1;
//    @Bind(R.id.rv_1)
//    SRecyclerView mSRecyclerView1;
//    List<BaseInspection> datas = new ArrayList<>();
//    InspectionAdapter mInspectionAdapter1;
//    private int site; //0为进行中， 1为已完成页面
//    String userId;
//
//    public InspectionListFragment() {
//    }
//
//    @Override
//    public int getLayoutId() {
//        return R.layout.inspectionning_fragment_rv;
//    }
//
//    @Override
//    protected void initView(View v) {
//        Bundle bundle = getArguments();
//        userId = bundle.getString("USERID");
//        site = getArguments().getInt(SubInspectionFragment.SUB_FRAGMENT_POSITON);
//        initRecycleView(v);
//        if (site == 0) {
//            mPresenter.getInspections(userId, "0", 1);
//        } else {
//            mPresenter.getInspections(userId, "1", 1);
//        }
////        mPresenter.getBanner("0");//类型  0  banner图，1 入口种类
//
//    }
//
//    private void initRecycleView(View v1) {
//        View vEmpty = LayoutInflater.from(mContext).inflate(R.layout.empty_home_inspection, (ViewGroup) v1.getParent(), false);
//        mSwipeRefreshLayout1.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_orange_light, android.R.color.holo_red_light, android.R.color.holo_green_light);
//        mSwipeRefreshLayout1.setRefreshing(false);
//        mSwipeRefreshLayout1.setProgressViewOffset(true, 0, WindowUtils.dip2px(24));
//        mSwipeRefreshLayout1.setOnRefreshListener(this);
//        mSRecyclerView1.setItemAnimator(new DefaultItemAnimator());
//        mSRecyclerView1.setHasFixedSize(true);
//        mSRecyclerView1.setEmptyView(vEmpty);
//        mInspectionAdapter1 = new InspectionAdapter(mContext, datas);
//        mSRecyclerView1.setAdapter(mInspectionAdapter1);
//        mSRecyclerView1.setLoadingMoreProgressStyle(ProgressStyle.BallSpinFadeLoader);
//        /*
//         * 加载更多
//         */
//        mSRecyclerView1.setOnLoadMoreListener(new OnLoadMoreListener() {
//            @Override
//            public void onLoadMore() {
//
//            }
//        });
//        //设置底部加载颜色
//        mSRecyclerView1.setFooterViewColor(R.color.tv_gray_2, R.color.tv_gray_3, R.color.white);
//    }
//
//    @Override
//    protected InspectionPresenter initPresenter() {
//        return new InspectionPresenter(this, new InspectionModel());
//    }
//
//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//    }
//
//
//    @Override
//    public void showInspectionData(List<BaseInspection> bOT) {
//             datas.clear();
//             datas=bOT;
//             mInspectionAdapter1.setData(datas);
//    }
//
//    @Override
//    public void onSucceed() {
//
//    }
//
//    @Override
//    public void onFaild(String msg) {
//
//    }
//
//    /**
//     * 下拉刷新
//     */
//    @Override
//    public void onRefresh() {
//
//    }
//}
