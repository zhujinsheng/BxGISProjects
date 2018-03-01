package com.bxgis.bxportal.ui.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bxgis.bxportal.R;
import com.bxgis.bxportal.base.BaseFragment;
import com.bxgis.bxportal.bean.BannerOrTypeBean;
import com.bxgis.bxportal.bean.BaseInspection;
import com.bxgis.bxportal.bean.BaseInspectionTotal;
import com.bxgis.bxportal.bean.HiddenDangerReViewBean;
import com.bxgis.bxportal.bean.HomepageEntity;
import com.bxgis.bxportal.bean.InspectionProjectBean;
import com.bxgis.bxportal.event.UpdateEvent;
import com.bxgis.bxportal.myInterface.MyOnItemClickListener;
import com.bxgis.bxportal.myInterface.OPenSideslip;
import com.bxgis.bxportal.ui.home.adpter.CagegoryViewPagerAdapter;
import com.bxgis.bxportal.ui.home.adpter.EntranceAdapter;
import com.bxgis.bxportal.ui.home.adpter.HomeFragmentAdapter;
import com.bxgis.bxportal.ui.home.contract.HomeContract;
import com.bxgis.bxportal.ui.home.model.HomeModel;
import com.bxgis.bxportal.ui.home.presenter.HomePresenter;
import com.bxgis.bxportal.ui.reinspection.ReinspectionFeedBackActivity;
import com.bxgis.bxportal.utils.StatusBarUtils;
import com.bxgis.bxportal.widget.RecycleViewDivider;
import com.bxgis.bxportal.widget.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;


/**
 * Created by xiaozhu on 2017/11/17.
 */

public class HomeFragment extends BaseFragment<HomePresenter> implements HomeContract.View, View.OnClickListener, PullLoadMoreRecyclerView.PullLoadMoreListener {
    @Bind(R.id.top_menu_icon)
    ImageView mImageView;
    @Bind(R.id.home_title)
    TextView title;
    @Bind(R.id.notice)
    ImageView notice;
    /**
     * 每一页展示多少条数据
     */
    public static final int ROWS = 10;
    protected int mCurrentPage = 1;
    protected int totalPage = 2;
    protected final int REQUEST_COUNT = 11;
    @Bind(R.id.pullrecycler_view)
    PullLoadMoreRecyclerView mPullLoadMoreRecyclerView;

    private RecyclerView mRecyclerView;
    //    @Bind(R.id.swipe_refresh_layout)
//    protected SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.root_view)
    protected FrameLayout mRootView;


    protected boolean isRequestInProcess = false;
    protected boolean mIsStart = false;

    private int itemPositon=0; //记录当前选择的列表位置
    private View headerView;
    public static final int HOME_ENTRANCE_PAGE_SIZE = 8;//首页菜单单页显示数量
    private HomeFragmentAdapter mHomeFragmentAdapter; //整改复查列表适配器
    private CagegoryViewPagerAdapter prijectAdapter;
    private List<BaseInspection> inspeData = new ArrayList<>();
    private List<InspectionProjectBean> projects = new ArrayList<>();
    int num = 1;
    private String userId; //用户ID

    private OPenSideslip mOPenSideslip;
    private boolean dropDown = false;//是否是下载加载
    private boolean upMore = false;//是否是上拉加载更多
    private boolean dataEmpty = true; //返回的数据为空
    List<HiddenDangerReViewBean> tempInspection = new ArrayList<>();
    HomepageEntity homepageEntity = new HomepageEntity();
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mOPenSideslip = (OPenSideslip) context;
    }

//    public static HomeFragment newInstance(String param1) {
//        HomeFragment fragment = new HomeFragment();
//        Bundle args = new Bundle();
//        args.putString("agrs1", param1);
//        fragment.setArguments(args);
//
//        return fragment;
//
//    }

    public HomeFragment() {
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView(View v) {
        StatusBarUtils.with(getActivity()).init();
        mImageView = (ImageView) v.findViewById(R.id.top_menu_icon);
        mImageView.setOnClickListener(this);
        Bundle bundle = getArguments();
        String agrs1 = bundle.getString("agrs1");
//        initInspectionSort();
        initHomeRv(v);
        mPresenter.getBanner("0");
//        mPresenter.getProjectList();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateReViewDate(UpdateEvent messageEvent) {
        if(null !=mHomeFragmentAdapter&& messageEvent.message.equals("3")){
//            System.out.println("删除的位置= "+itemPositon);
//            mHomeFragmentAdapter.removeItem(itemPositon);
            mPresenter.getBanner("0");
        }
    }
    private void initHomeRv(View v) {
        //获取mRecyclerView对象
        mRecyclerView = mPullLoadMoreRecyclerView.getRecyclerView();
        //代码设置scrollbar无效？未解决！
        mRecyclerView.setVerticalScrollBarEnabled(true);
        //设置是否可以下拉刷新
        mPullLoadMoreRecyclerView.setPullRefreshEnable(true);
        //设置是否可以上拉刷新
        mPullLoadMoreRecyclerView.setPushRefreshEnable(true);
        //显示下拉刷新
        mPullLoadMoreRecyclerView.setRefreshing(false);
        //设置上拉刷新文字
        mPullLoadMoreRecyclerView.setFooterViewText("加载更多");
        //设置上拉刷新文字颜色
        //mPullLoadMoreRecyclerView.setFooterViewTextColor(R.color.white);
        //设置加载更多背景色
        //mPullLoadMoreRecyclerView.setFooterViewBackgroundColor(R.color.colorBackground);
        mPullLoadMoreRecyclerView.setLinearLayout();
        mPullLoadMoreRecyclerView.addItemDecoration(new RecycleViewDivider(getActivity(), LinearLayout.HORIZONTAL));
        mPullLoadMoreRecyclerView.setOnPullLoadMoreListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mHomeFragmentAdapter = new HomeFragmentAdapter();

        mPullLoadMoreRecyclerView.setAdapter(mHomeFragmentAdapter);
        mHomeFragmentAdapter.setMyOnItemClickListener(new MyOnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {//position这是控件的布局位置，因为其前面还有4个布局，故获取复检的数据树要减去4
                int dateIndex=position-4;
                itemPositon=position-4;
                Intent i = new Intent(getActivity(), ReinspectionFeedBackActivity.class);
                i.putExtra("ReInspection", homepageEntity.getC().getList().get(dateIndex));
                //  InspectionSite  site; //0为进行中， 1为已完成页面, 2为整改复查入口
//                i.putExtra("ReViewInspecrion", homepageEntity.getC().getList().get(dateIndex));
                i.putExtra("InspectionSite", 3);
                startActivity(i);
            }
        });


    }
    @Override
    public void onRefresh() {
        dropDown = true;
        upMore = false;
        mHomeFragmentAdapter.clearData();
        mCurrentPage = 1;
        mPresenter.getBanner("0");
        mPullLoadMoreRecyclerView.setRefreshing(false);
    }

    @Override
    public void onLoadMore() {
        dropDown = false;
        upMore = true;
        getdata();
//        mPresenter.getInspectionList(userId, "0",3, mCurrentPage);

    }

    private void getdata() {
        if (dataEmpty) {
            mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
            return;
        }
        mCurrentPage = mCurrentPage + 1;
        mPresenter.getBanner("0");

    }


    @Override
    public void onPause() {
        Glide.with(getActivity()).pauseRequests();
        super.onPause();
    }

    @Override
    public void onResume() {
        Glide.with(getActivity()).resumeRequests();
        super.onResume();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.top_menu_icon:
                if (mOPenSideslip != null) {
                    mOPenSideslip.onOpenSideslipe();
                }
                break;
            default:
                break;
        }
    }



    @Override
    protected HomePresenter initPresenter() {
        return new HomePresenter(this, new HomeModel());
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void showBannerData(List<BannerOrTypeBean> bOT) {
//        if (bOT.size() > 0) {
            homepageEntity.setA(bOT);
//        }
        mPresenter.getProjectList();
    }

    @Override
    public void showProjectData(List<InspectionProjectBean> bOT) {
        homepageEntity.setB(bOT);
        userId = getArguments().getString("USERID");
        mPresenter.getHiddenDangerList(mCurrentPage,10);
    }



    @Override
    public void showReviewData(List<HiddenDangerReViewBean> bI) {
//        inspeData.clear();
        dataEmpty = false;
        if (dropDown) {
            if (null != tempInspection && tempInspection.size() > 0) {
                tempInspection.clear();
            }
            tempInspection.addAll(bI);
        } else if (upMore) {
            tempInspection.addAll(bI);
        } else { //第一次加载数据
            mCurrentPage = 1;
            tempInspection.clear();
            tempInspection = bI;
        }
//        inspectionAdapter.setData(datas);
        BaseInspectionTotal total = new BaseInspectionTotal();
        total.setTotalpage(10);
        total.setPage(10);
        total.setList(tempInspection);
        homepageEntity.setC(total);
        mHomeFragmentAdapter.setData(homepageEntity);
        mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();

    }

    @Override
    public void showReviewEmpty(String msg) {
        dataEmpty = true;
        mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
        mHomeFragmentAdapter.setData(homepageEntity);
        mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
    }

    @Override
    public void onSucceed() {

    }

    @Override
    public void onFaild(String msg) {
//        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mCurrentPage = 1;
    }
}
