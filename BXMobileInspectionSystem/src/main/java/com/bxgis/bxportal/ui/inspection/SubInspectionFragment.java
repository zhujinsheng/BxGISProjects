package com.bxgis.bxportal.ui.inspection;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bxgis.bxportal.R;
import com.bxgis.bxportal.base.LazyBaseFragment;
import com.bxgis.bxportal.bean.BaseInspection;
import com.bxgis.bxportal.event.UpdateEvent;
import com.bxgis.bxportal.myInterface.MyOnItemClickListener;
import com.bxgis.bxportal.ui.home.adpter.InspectionAdapter;
import com.bxgis.bxportal.ui.inspection.contract.InspectionContract;
import com.bxgis.bxportal.ui.inspection.model.InspectionModel;
import com.bxgis.bxportal.ui.inspection.presenter.InspectionPresenter;
import com.bxgis.bxportal.utils.NoDoubleClickListener;
import com.bxgis.bxportal.widget.RecycleViewDivider;
import com.bxgis.bxportal.widget.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import me.leefeng.promptlibrary.PromptDialog;


public class SubInspectionFragment extends LazyBaseFragment<InspectionPresenter> implements InspectionContract.View, PullLoadMoreRecyclerView.PullLoadMoreListener {
    public static final String INTENT_STRING_TABNAME = "intent_String_tabName";
    public static final String SUB_FRAGMENT_POSITON = "SUB_FRAGMENT_POSITON";
    private String tabName;
    private int position;
    @Bind(R.id.rv_1)
    PullLoadMoreRecyclerView mPullLoadMoreRecyclerView;

    @Bind(R.id.rl_empty_data)
    RelativeLayout emptyDataView; //空数据视图

    private RecyclerView mRecyclerView;
    InspectionAdapter inspectionAdapter;
    List<BaseInspection> datas = new ArrayList<>();
    private int site; //0为进行中， 1为已完成页面
    String userId;
    int currentPage = 1;  //默认是第一页
    private boolean dropDown = false;//是否是下载加载
    private boolean upLoadMore = false;//是否是上拉加载更多
    private boolean dataEmpty = true; //返回的数据为空

    private PromptDialog promptDialog;  //自定义对话框

    @Override
    protected View getPreviewLayout(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.layout_preview, container, false);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateReViewDate(UpdateEvent messageEvent) {
        if(null !=inspectionAdapter){
            dropDown = true;
            upLoadMore = false;
            inspectionAdapter.clearData(); //
            if( messageEvent.message.equals("0")){
                Log.e("xiaozhu已完成000 = ", String.valueOf(currentPage));
                mPresenter.getInspections(userId, "0", 1, 1);
            }else if( messageEvent.message.equals("1")){
                Log.e("xiaozhu已完成111= ", String.valueOf(currentPage));
                mPresenter.getInspections(userId, "1", 1, 1);
            }
        }
    }
    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        tabName = getArguments().getString(INTENT_STRING_TABNAME);
        position = getArguments().getInt(SUB_FRAGMENT_POSITON);
        setContentView(R.layout.inspectionning_fragment_rv);
        Bundle bundle = getArguments();
        userId = bundle.getString("USERID");
        //创建对象
        promptDialog = new PromptDialog(getActivity());
        //设置自定义属性
        promptDialog.getDefaultBuilder().touchAble(true).round(3).loadingDuration(3000);
        site = getArguments().getInt(SubInspectionFragment.SUB_FRAGMENT_POSITON);
        initRecycleView(getContentView());
        if (site == 0) {
            mPresenter.getInspections(userId, "0", 1, currentPage);
        } else {
            mPresenter.getInspections(userId, "1", 1, currentPage);
        }
        emptyDataView.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                if (site == 0) {
                    mPresenter.getInspections(userId, "0", 1, currentPage);
                } else {
                    mPresenter.getInspections(userId, "1", 1, currentPage);
                }
            }
        });

    }

    private void initRecycleView(View v1) {
        //获取mRecyclerView对象
        mRecyclerView = mPullLoadMoreRecyclerView.getRecyclerView();
        //代码设置scrollbar无效？未解决！
        mRecyclerView.setVerticalScrollBarEnabled(true);
        //设置是否可以下拉刷新
        mPullLoadMoreRecyclerView.setPullRefreshEnable(true);
        //设置是否可以上拉刷新
        mPullLoadMoreRecyclerView.setPushRefreshEnable(true);
        //显示下拉刷新
        mPullLoadMoreRecyclerView.setRefreshing(true);
        //设置上拉刷新文字
        mPullLoadMoreRecyclerView.setFooterViewText("加载更多");

        //设置上拉刷新文字颜色
        //mPullLoadMoreRecyclerView.setFooterViewTextColor(R.color.white);
        //设置加载更多背景色
        //mPullLoadMoreRecyclerView.setFooterViewBackgroundColor(R.color.colorBackground);
        mPullLoadMoreRecyclerView.setLinearLayout();
        mPullLoadMoreRecyclerView.addItemDecoration(new RecycleViewDivider(getActivity(), LinearLayout.HORIZONTAL));
        mPullLoadMoreRecyclerView.setOnPullLoadMoreListener(this);
        inspectionAdapter = new InspectionAdapter(getActivity(), datas);
        mPullLoadMoreRecyclerView.setAdapter(inspectionAdapter);
        inspectionAdapter.setMyOnItemClickListener(new MyOnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                if (site == 0) {
                    Intent intent = new Intent(getActivity(), InspectionFeedBackActivity.class);
                    //site; //0为进行中， 1为已完成页面, 2为整改复查入口
                    intent.putExtra("BaseInspection", datas.get(position));
                    intent.putExtra("InspectionSite", site);
                    startActivity(intent);
                } else if(site==1){
                    Intent intent = new Intent(getActivity(), InspectionDetailActivity.class);
                    //site; //0为进行中， 1为已完成页面
                    intent.putExtra("BaseInspection", datas.get(position));
                    intent.putExtra("InspectionSite", site);
                    startActivity(intent);
                }
            }
        });

//        View vEmpty = LayoutInflater.from(getActivity()).inflate(R.layout.empty_home_inspection, (ViewGroup) v1.getParent(), false);
    }

    @Override
    protected InspectionPresenter initPresenter() {
        return new InspectionPresenter(this, new InspectionModel());
    }

    @Override
    protected void onDestroyViewLazy() {
        super.onDestroyViewLazy();
    }


    @Override
    public void showInspectionData(List<BaseInspection> bOT) {
        emptyDataView.setVisibility(View.GONE);
        dataEmpty = false;
        if (dropDown) {
            if (null != datas && datas.size() > 0) {
                datas.clear();
            }
            datas.addAll(bOT);
        } else if (upLoadMore) {
            datas.addAll(bOT);
        } else { //第一次加载
            currentPage = 1;
            datas.clear();
            datas = bOT;
        }
        inspectionAdapter.setData(datas);
        mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
    }

    @Override
    public void showEmptyData(String msg) {
        dataEmpty = true;
        mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
        if(currentPage==1){
            emptyDataView.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onSucceed() {

    }

    public void clearData() {
        inspectionAdapter.clearData();
        inspectionAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFaild(String msg) {
        promptDialog.showInfo(msg);
        mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
    }

    @Override
    public void onRefresh() {
        Log.e("wxl", "onRefresh");
        dropDown = true;
        upLoadMore = false;
        inspectionAdapter.clearData(); //
        if (site == 0) {
            mPresenter.getInspections(userId, "0", 1, 1);
        } else {
            Log.e("xiaozhu已完成", String.valueOf(currentPage));
            mPresenter.getInspections(userId, "1", 1, 1);
        }
    }

    @Override
    public void onLoadMore() {
        dropDown = false;
        upLoadMore = true;
        Log.e("wxl", "onLoadMore");
        getdata();
    }

    private void getdata() {
        if (dataEmpty) {
            mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
            return;
        }
        currentPage = currentPage + 1;
        if (site == 0) {
            Log.e("xiaozhu未完成", String.valueOf(currentPage));
            mPresenter.getInspections(userId, "0", 1, currentPage);
        } else {
            Log.e("xiaozhu已完成", String.valueOf(currentPage));
            mPresenter.getInspections(userId, "1", 1, currentPage);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        currentPage = 1;
        dropDown = false;
        upLoadMore = false;
    }
}
