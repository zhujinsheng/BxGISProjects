package com.bxgis.yczw.ui.main;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bxgis.yczw.MyApp;
import com.bxgis.yczw.R;
import com.bxgis.yczw.base.BaseActivity;
import com.bxgis.yczw.base.BaseView;
import com.bxgis.yczw.bean.BannerOrTypeBean;
import com.bxgis.yczw.bean.ProjectTypeBean;
import com.bxgis.yczw.ui.adapter.GridViewAdapter;
import com.bxgis.yczw.ui.center.AboutActivity;
import com.bxgis.yczw.ui.main.contract.HomeActivityContract;
import com.bxgis.yczw.ui.main.presenter.HomeActivityPresenter;
import com.bxgis.yczw.ui.riverMap.RiverCruiseActivity;
import com.bxgis.yczw.utils.HookViewClickUtil;
import com.bxgis.yczw.utils.ImageLoader;
import com.bxgis.yczw.utils.StringUtil;
import com.bxgis.yczw.widget.CustomGridView;
import com.bxgis.yczw.widget.rvBanner.RecyclerViewBanner;
import com.bxgis.yczw.widget.toast.ToastCompat;
import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Rationale;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import xiaozhu.utilwebx5.X5Utils;
import xiaozhu.utilwebx5.permission.DefaultRationale;
import xiaozhu.utilwebx5.permission.PermissionSetting;

public class MainActivity extends BaseActivity<HomeActivityPresenter> implements HomeActivityContract.View , View.OnClickListener{
    private static Rationale mRationale =null;
    private static PermissionSetting mSetting =null;

    CustomGridView cGridView;
    ImageView userInfo,message;
    LinearLayout llBarInfo,llInspeSeainfo,llEventStatistics,llCompreAssessment; //海湾信息，巡海汇总，事件统计，综合考核
    RecyclerViewBanner mBanner; //轮播图
    private GridViewAdapter mGVAdatpter;
    private List<BannerOrTypeBean> mBannerList=new ArrayList<>();
    private List<ProjectTypeBean> mProjectList=new ArrayList<>();

    private  String [] titles={"开始巡海","快速上报" ,"事件管理","巡海日志","地理信息","热点信息","排污口登记","我的海湾"};
    private  int [] iconId ={R.mipmap.main_inspect_icon,R.mipmap.main_quick_up_icon,R.mipmap.main_project_center_icon3,R.mipmap.main_project_center_icon4,R.mipmap.main_project_center_icon5,R.mipmap.main_project_center_icon6,R.mipmap.main_project_center_icon7,R.mipmap.main_project_center_icon8};
    private String [] bannerUrl={"https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=202431425,2265329314&fm=27&gp=0.jpg","https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=40102123,484090&fm=27&gp=0.jpg","https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=202431425,2265329314&fm=27&gp=0.jpg","https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=40102123,484090&fm=27&gp=0.jpg","https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=40102123,484090&fm=27&gp=0.jpg"};
    @Override
    public void doBeforeSetContentView() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        cGridView = (CustomGridView) findViewById(R.id.gd_main_type);
        userInfo = (ImageView) findViewById(R.id.iv_user_info);
        message = (ImageView) findViewById(R.id.iv_message);
        llBarInfo = (LinearLayout) findViewById(R.id.ll_bay_info);
        llInspeSeainfo = (LinearLayout) findViewById(R.id.ll_inspe_seainfo);
        llEventStatistics = (LinearLayout) findViewById(R.id.ll_event_statistics);
        llCompreAssessment = (LinearLayout) findViewById(R.id.ll_compre_assessment);
        mBanner = (RecyclerViewBanner) findViewById(R.id.rb_banner);

        initData();

        userInfo.setOnClickListener(this);
        message.setOnClickListener(this);
        llBarInfo.setOnClickListener(this);
        llInspeSeainfo.setOnClickListener(this);
        llEventStatistics.setOnClickListener(this);
        llCompreAssessment.setOnClickListener(this);

        mGVAdatpter = new GridViewAdapter(mContext);
        mGVAdatpter.setData(mProjectList);
        cGridView.setAdapter(mGVAdatpter);
        //必须取代掉原来的布局，否则grid view 四周有相当大的边距
        cGridView.setSelector(android.R.color.transparent);

        cGridView.setGravity(Gravity.CENTER);
        mBanner.setRvBannerData(mBannerList);
        mBanner.setOnSwitchRvBannerListener(new RecyclerViewBanner.OnSwitchRvBannerListener() {
            @Override
            public void switchBanner(int position, AppCompatImageView bannerView) {
                String url = mBannerList.get(position).getPiture_url();
                if (StringUtil.isNotEmpty(url,false)) {

                    ImageLoader.showImage(mContext, url, bannerView);
                }
            }
        });

        cGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ToastCompat.makeText(mContext,"点击"+i+" = "+l,Toast.LENGTH_SHORT).show();
                if(i==0){
                    Intent toMap =new Intent(MainActivity.this, RiverCruiseActivity.class);
                    startActivity(toMap);
                }
            }
        });
        /**
         * Hook技术之View点击劫持  ---避免用户快速多次点击
         * MainActivity的View渲染完毕的时候进行注入，即在 getWindow().getDecorView().post()中
         */
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                HookViewClickUtil.getInstance().hookStartActivity(MainActivity.this);
            }
        });
        if(Build.VERSION.SDK_INT>=23) {
            if (X5Utils.hasPermission(MainActivity.this, Manifest.permission.CAMERA,Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {
                // 申请权限。
                X5Utils.requestPermission(MainActivity.this, Permission.Group.STORAGE, Permission.Group.CAMERA,Permission.Group.LOCATION);
            }
        }
    }


    private void initData(){
        ProjectTypeBean typeBean;
        for(int i=0;i<titles.length;i++){
            typeBean = new ProjectTypeBean();
            typeBean.setIconId(iconId[i]);
            typeBean.setTitle(titles[i]);
            typeBean.setUrl("http://www.baidu.com");
            mProjectList.add(typeBean);
        }
        BannerOrTypeBean bannerOrTypeBean;
        for(int a=0;a<bannerUrl.length;a++){
            bannerOrTypeBean = new BannerOrTypeBean();
            bannerOrTypeBean.setId(String.valueOf(a));
            bannerOrTypeBean.setPiture_url(bannerUrl[a]);
            mBannerList.add(bannerOrTypeBean);
        }
    }
    @Override
    protected HomeActivityPresenter initPresenter() {
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(MainActivity.this, AboutActivity.class);
        String data="";
        switch (view.getId()) {
            case R.id.iv_user_info:  //用户信息
                data = "http://www.iqiyi.com/fun/20130701/0ba18f613aa59b46.html";
                break;
            case R.id.iv_message:   //消息列表
                data = "file:///android_asset/webpage/fullscreenVideo.html";
                break;
            case R.id.ll_bay_info:  //海湾信息
//                data= "file:///android_asset/webpage/fileChooser.html";
                data= "file:///android_asset/picTest.html";
                intent.putExtra("VIDEO",true);
                break;
            case R.id.ll_inspe_seainfo:  //巡海汇总
                 intent=null;
                intent = new Intent(MainActivity.this, CommonWebActivity.class);
                break;
            case R.id.ll_event_statistics:  //事件统计
                 intent=null;
                intent = new Intent(MainActivity.this, CommonWebActivity.class);
                break;
            case R.id.ll_compre_assessment:  //综合考核
                 intent=null;
                intent = new Intent(MainActivity.this, CommonWebActivity.class);
                break;
            default:
                break;
        }
        intent.putExtra("PATH",data);
        startActivity(intent);
    }

    @Override
    public void onSucceed() {

    }

    @Override
    public void onFaild(String msg) {

    }

    @Override
    protected void onDestroy() {

        if(mBannerList!=null){
            mBannerList.clear();
        }
        if(mProjectList!=null){
            mProjectList.clear();
        }
        doubleBackExitPressedOnce=false;
        super.onDestroy();
    }

    private boolean doubleBackExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        Timer tExit = null;
        // 非双击退出状态，使用原back逻辑
        // 双击返回键关闭程序
        // 如果两秒重置时间内再次点击返回,则退出程序
        if (doubleBackExitPressedOnce) {
//            finish();
//            System.exit(0);
//            ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
//            am.killBackgroundProcesses("com.bxgis.yczw"); // API Level至少为8才能使用
            finish();
            return;
        }
        doubleBackExitPressedOnce = true;
        Toast.makeText(MyApp.getContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
//        showShortToast("再按一次退出程序");
        tExit = new Timer();
        tExit.schedule(new TimerTask() {
            @Override
            public void run() {
                doubleBackExitPressedOnce = false;//取消退出
            }
        }, 2000);// 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务


    }


}
