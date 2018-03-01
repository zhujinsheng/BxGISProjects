package com.bxgis.bxportal.ui.home;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bxgis.bxportal.MISystemApplication;
import com.bxgis.bxportal.R;
import com.bxgis.bxportal.base.BaseActivity;
import com.bxgis.bxportal.bean.AppInfonBean;
import com.bxgis.bxportal.bean.InspectionCountBean;
import com.bxgis.bxportal.bean.InspectionProjectBean;
import com.bxgis.bxportal.bean.UserBean;
import com.bxgis.bxportal.event.UpdateICountEvent;
import com.bxgis.bxportal.myInterface.MyOnItemClickListener;
import com.bxgis.bxportal.myInterface.OPenSideslip;
import com.bxgis.bxportal.ui.home.adpter.HomePopupWindowAdapter;
import com.bxgis.bxportal.ui.home.contract.HomeActivityContract;
import com.bxgis.bxportal.ui.home.model.HomeActivityModel;
import com.bxgis.bxportal.ui.home.presenter.HomeActivityPresenter;
import com.bxgis.bxportal.ui.inspection.InspectionMainFragment;
import com.bxgis.bxportal.ui.inspection.NowInspecrionRelaseActivity;
import com.bxgis.bxportal.ui.login.LoginActivity;
import com.bxgis.bxportal.utils.DataCacheUtils;
import com.bxgis.bxportal.utils.FileCacheUtils;
import com.bxgis.bxportal.utils.HookViewClickUtil;
import com.bxgis.bxportal.utils.NoDoubleClickListener;
import com.bxgis.bxportal.utils.PermissionUtils;
import com.bxgis.bxportal.utils.SPUtils;
import com.bxgis.bxportal.utils.SystemBarTintManager;
import com.bxgis.bxportal.utils.WindowUtils;
import com.bxgis.bxportal.utils.appupdate.DownLoadApk;
import com.bxgis.bxportal.widget.ResideLayout;
import com.bxgis.bxportal.widget.SpaceItemDecoration;
import com.shizhefei.view.indicator.FixedIndicatorView;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;
import com.shizhefei.view.viewpager.SViewPager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import me.leefeng.promptlibrary.PromptButton;
import me.leefeng.promptlibrary.PromptButtonListener;
import me.leefeng.promptlibrary.PromptDialog;

import static com.bxgis.bxportal.MISystemApplication.INTENT_INT_INDEX;
import static com.bxgis.bxportal.MISystemApplication.INTENT_STRING_TABNAME;

public class MainActivity extends BaseActivity<HomeActivityPresenter> implements View.OnClickListener, OPenSideslip, HomeActivityContract.View {
    @Bind(R.id.ll_about_us)
    LinearLayout aboutUs;
    @Bind(R.id.ll_cache_clean)
    LinearLayout claeanCache;
    @Bind(R.id.ll_edit_data)
    LinearLayout dateEdit;
    @Bind(R.id.ll_feedback)
    LinearLayout feedBack;
    @Bind(R.id.ll_inspection_finish)
    LinearLayout inspectionFinish;
    @Bind(R.id.ll_inspection_ing)
    LinearLayout inspectioning;
    @Bind(R.id.ll_safe)
    LinearLayout accountSecurity;
    @Bind(R.id.ll_update)
    LinearLayout appUpdate;
    @Bind(R.id.bottom_menu)
    LinearLayout loginOut;


    @Bind(R.id.iv_head)
    ImageView head;
    @Bind(R.id.tv_company_name)
    TextView companyName;
    @Bind(R.id.tv_login_name)
    TextView loginName;
    @Bind(R.id.sp_layout)
    ResideLayout mSideslipLayout;

    @Bind(R.id.mark_ing)
    TextView countUnfinish; // 未完成数量显示位置
    @Bind(R.id.mark_finish)
    TextView countComplete;//完成的巡检数量显示位置

    //tab
    @Bind(R.id.tabmain_viewPager)
    SViewPager mSViewPager;
    @Bind(R.id.tabmain_indicator)
    FixedIndicatorView indicator;
    @Bind(R.id.update_tip)
    TextView updateTip;
    @Bind(R.id.ll_center_tab)
    LinearLayout centetTab;
//    @Bind(R.id.main_tv_final)
//    TextView center

    private IndicatorViewPager indicatorViewPager;
    HomeFragment mHomeFragment;

    private String userId;
    private final String PERMISSION_READ_EXTERNAL = Manifest.permission.READ_EXTERNAL_STORAGE; //开启读写权限
    private final int REQUEST_CODE_EXTERNAL = 15; //读取权限请求码
    private List<InspectionProjectBean> mProjectBeens = new ArrayList<InspectionProjectBean>();

    private PromptDialog promptDialog;  //自定义对话框
    private PopupWindow popupWindow;  //自定义对话框

    UserBean mUser;
    private AppInfonBean mAppInfonBean = null;
    private boolean isUpdate = false; //是否可以更新


    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (promptDialog != null)
                        promptDialog.showInfo("已成功清理了" + msg.obj.toString() + "的空间");
                    break;
                default:
                    break;
            }
        }

        ;
    };

    private boolean doubleBackExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        Timer tExit = null;
        // 非双击退出状态，使用原back逻辑
        // 双击返回键关闭程序
        // 如果两秒重置时间内再次点击返回,则退出程序
        if (doubleBackExitPressedOnce) {
            exit();
            return;
        }
        doubleBackExitPressedOnce = true;
        showShortToast("再按一次退出程序");
        tExit = new Timer();
        tExit.schedule(new TimerTask() {
            @Override
            public void run() {
                doubleBackExitPressedOnce = false;//取消退出
            }
        }, 2000);// 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务


    }

    //    @Override
//    public void onBackPressed() {
//        if (mSideslipLayout.isOpen()) {
//            mSideslipLayout.closePane();
//        } else {
//            super.onBackPressed();
//        }
//    }
    AlertDialog.Builder mAlertDialog;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {            //系统版本大于19
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.btn_blue_login);
        Class clazz = this.getWindow().getClass();
        try {
            int darkModeFlag = 0;
            Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            if (true) {
                extraFlagField.invoke(this.getWindow(), darkModeFlag, 0);//状态栏透明且黑色字体
            } else {
                extraFlagField.invoke(this.getWindow(), 0, darkModeFlag);//清除黑色字体
            }
        } catch (Exception e) {

        }

        mUser = (UserBean) getIntent().getSerializableExtra("USER_INFO");
        if (null != mUser) {
            userId = mUser.getId();
            mPresenter.getCountInspection(userId); //获取巡检的订单数目
        }
        mPresenter.getProjectList(); //获取所有的巡检项目类型
        //创建对象
        promptDialog = new PromptDialog(this);
        //设置自定义属性
        promptDialog.getDefaultBuilder().touchAble(true).round(3).loadingDuration(3000);
        aboutUs.setOnClickListener(this);
        claeanCache.setOnClickListener(this);
        dateEdit.setOnClickListener(this);
        feedBack.setOnClickListener(this);
        inspectionFinish.setOnClickListener(this);
        inspectioning.setOnClickListener(this);
        accountSecurity.setOnClickListener(this);
        appUpdate.setOnClickListener(this);
        loginOut.setOnClickListener(this);
        centetTab.setOnClickListener(this);
        initBottomNatigationBar();
        //根据decorView就可以递归查找所有的子view以及需要hook的子view
        HookViewClickUtil.hookStartActivity(this);
//        mSideslipLayout.requestDisallowInterceptTouchEvent(false);
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermission();
        }
        initViewData();
        updateTip.setText("V" + WindowUtils.getVerName(mContext));
        mPresenter.getAppInfo();
    }

    // 申请读写权限
    private void requestPermission() {
        PermissionUtils.checkAndRequestPermission(mContext, PERMISSION_READ_EXTERNAL, REQUEST_CODE_EXTERNAL,
                new PermissionUtils.PermissionRequestSuccessCallBack() {
                    @Override
                    public void onHasPermission() {
                        // 权限已被授予

                    }
                });
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;        // a|=b的意思就是把a和b按位或然后赋值给a   按位或的意思就是先把a和b都换成2进制，然后用或操作，相当于a=a|b
        } else {
            winParams.flags &= ~bits;        //&是位运算里面，与运算  a&=b相当于 a = a&b  ~非运算符
        }
        win.setAttributes(winParams);
    }

    private void initViewData() {
        if (null == mUser) {
            return;
        }
        loginName.setText(mUser.getReal_name());
//        companyName.setText(mUser.get);

    }

    private void initBottomNatigationBar() {
        //tab点击时候文字的颜色转换
        indicator.setOnTransitionListener(new OnTransitionTextListener().setColor(Color.parseColor("#0094DB"), Color.GRAY));
        View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_tab_main, null, false);
        indicator.setCenterView(v, getResources().getDimensionPixelOffset(R.dimen.x200), getResources().getDimensionPixelOffset(R.dimen.y100));
        indicatorViewPager = new IndicatorViewPager(indicator, mSViewPager);
        indicatorViewPager.setAdapter(new MyIndicatorAdapter(getSupportFragmentManager()));
        // 禁止viewpager的滑动事件
        mSViewPager.setCanScroll(false);
        // 设置viewpager保留界面不重新加载的页面数量
        mSViewPager.setOffscreenPageLimit(2);
    }

    /**
     * 根据decorView就可以递归查找所有的子view以及需要hook的子view
     *
     * @return
     */

    @Override
    protected HomeActivityPresenter initPresenter() {
        return new HomeActivityPresenter(this, new HomeActivityModel());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_EXTERNAL:
                if (PermissionUtils.isPermissionRequestSuccess(grantResults)) {
                    // 权限申请成功
//            toCamera();
                } else {
                    Toast.makeText(mContext, "开启文件读写权限失败，有可能会导致相应的功能使用异常", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * 监听巡检订单的数量变化  没有任务就隐藏
     *
     * @param messageEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateReViewDate(UpdateICountEvent messageEvent) {
        if (messageEvent.getCountComplete() > 0) {
            countComplete.setVisibility(View.VISIBLE);
            countComplete.setText(String.valueOf(messageEvent.getCountComplete()));
        } else {
            countComplete.setVisibility(View.VISIBLE);
        }
        if (messageEvent.getCountUnfinished() > 0) {
            countUnfinish.setVisibility(View.VISIBLE);
            countUnfinish.setText(String.valueOf(messageEvent.getCountUnfinished()));
        } else {
            countUnfinish.setVisibility(View.GONE);
        }


    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.ll_about_us:
                mSideslipLayout.closePane();
                Log.e("测试快速点击", "1");
                break;
            case R.id.ll_cache_clean:
//                mSideslipLayout.closePane();
                promptDialog.showLoading("正在清除缓存数据...", true);
                new TaskThread().start();

                break;
            case R.id.ll_edit_data:
                mSideslipLayout.closePane();
                break;
            case R.id.ll_feedback:
                mSideslipLayout.closePane();
                break;
            case R.id.ll_inspection_finish:
                mSideslipLayout.closePane();
                break;
            case R.id.ll_inspection_ing:
                mSideslipLayout.closePane();
                break;
            case R.id.ll_safe:
                mSideslipLayout.closePane();
                break;
            case R.id.ll_update:

                if (isUpdate && null != mAlertDialog) {
                    mAlertDialog.create().show();
                } else {
                    Toast.makeText(mContext, "已是最新版本！", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.ll_center_tab: //底部导航中间位置的点击处理
                initPopupWindow(v);
                break;
            case R.id.bottom_menu: //退出
//                mSideslipLayout.closePane();
                promptDialog.showAlertSheet("是否回到登陆页?", true, new PromptButton("否", new PromptButtonListener() {
                    @Override
                    public void onClick(PromptButton button) {
                        promptDialog.dismiss();
                    }
                }), new PromptButton("是", new PromptButtonListener() {
                    @Override
                    public void onClick(PromptButton button) {
                        SPUtils.remove(mContext, "_sso_token");
                        Intent mIntent = new Intent(mContext, LoginActivity.class);
                        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(mIntent);
                        finish();
//                        exit();
                    }
                }));

                break;

        }
    }

    /**
     * 初始化底部导航栏的中间按钮的弹出效果
     */
    private void initPopupWindow(View v) {
        View popupWidownView = LayoutInflater.from(mContext).inflate(R.layout.popupwindow_content, null);
        RecyclerView cotentRV = (RecyclerView) popupWidownView.findViewById(R.id.rv_projectlist);
        cotentRV.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelOffset(R.dimen.y40)));
        cotentRV.setLayoutManager(new GridLayoutManager(mContext, 3));
        HomePopupWindowAdapter popupWindowAdapter = new HomePopupWindowAdapter(mContext, mProjectBeens);
        cotentRV.setAdapter(popupWindowAdapter);
        popupWindowAdapter.setOnMyItemClickListener(new MyOnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent = new Intent(MainActivity.this, NowInspecrionRelaseActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("InspectionProject", mProjectBeens.get(position));
                startActivity(intent);
                if(null!=popupWindow){
                    popupWindow.dismiss();
                }
            }
        });
        ImageView back = (ImageView) popupWidownView.findViewById(R.id.iv_back_home);
//        popupWindow = new PopupWindow(popupWidownView, ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelOffset(R.dimen.y650), true);
        popupWindow = new PopupWindow(popupWidownView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setAnimationStyle(R.style.AnimationBottomFade);
        popupWindow.setContentView(popupWidownView);
//        popupWindow.showAtLocation(findViewById(R.id.sp_layout), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        popupWindow.setOutsideTouchable(true);
        popupWidownView.measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED);
        int mShowPopuVWidth = -popupWidownView.getMeasuredWidth();
        int mShoePopuVHeight = -popupWidownView.getMeasuredHeight();
        popupWindow.showAsDropDown(v,mShowPopuVWidth,mShoePopuVHeight);

        // 设置弹出窗体可点击
        popupWindow.setFocusable(true);
        //设置背景半透明
        backgroundAlpha(0.5f);
        //关闭事件
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
            }
        });
        back.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }

    /**
     * 回调接口进行打开侧滑菜单
     */
    @Override
    public void onOpenSideslipe() {

        if (!mSideslipLayout.isOpen()) {
            mSideslipLayout.openPane();
        }
    }


    @Override
    public void showCount(InspectionCountBean inspectionCountBean) {
        EventBus.getDefault().post(new UpdateICountEvent(inspectionCountBean.getCount_complete(), inspectionCountBean.getCount_unfinished()));
    }


    @Override
    public void showAppInfo(final AppInfonBean appInfonBean) {
        mAppInfonBean = appInfonBean;
        String vsersionName = WindowUtils.getVerName(mContext);
        int currentVersionC = WindowUtils.getVersionCode(mContext);
        String vsersionCode = appInfonBean.getVersion_code();
        final String url = appInfonBean.getApk_url();
        if (null == vsersionCode || null == url) {
            return;
        }
        final String appName = appInfonBean.getApp_name();
        final String update_content = appInfonBean.getUpdate_content();

        if (Integer.parseInt(vsersionCode) > currentVersionC) {
            isUpdate = true;
            updateTip.setText("有版本更新了");
            updateTip.setTextColor(Color.RED);
            mAlertDialog = new AlertDialog.Builder(mContext);
            mAlertDialog.setTitle(appName + "又更新咯！");
            mAlertDialog.setMessage(update_content);
            mAlertDialog.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (!canDownloadState()) {
                        showDownloadSetting();
                        return;
                    }
                    DownLoadApk.download(MainActivity.this, url, "系统版本更新", appName);
                }
            });
            mAlertDialog.setNegativeButton("暂不更新", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            mAlertDialog.setCancelable(false);
            mAlertDialog.create().show();
        } else {
            isUpdate = false;
        }

    }

    @Override
    public void showProjectList(List<InspectionProjectBean> projectBeans) {
        if (null != mProjectBeens && mProjectBeens.size() > 0) {
            mProjectBeens.clear();
        }
        mProjectBeens.addAll(projectBeans);
    }

    @Override
    public void onSuccedGetData() {

    }

    private boolean intentAvailable(Intent intent) {
        PackageManager packageManager = getPackageManager();
        List list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    private void showDownloadSetting() {
        String packageName = "com.android.providers.downloads";
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + packageName));
        if (intentAvailable(intent)) {
            startActivity(intent);
        }
    }

    private boolean canDownloadState() {
        try {
            int state = this.getPackageManager().getApplicationEnabledSetting("com.android.providers.downloads");

            if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED) {
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void onSucceed() {

    }

    @Override
    public void onFaild(String msg) {

    }

    private class MyIndicatorAdapter extends IndicatorViewPager.IndicatorFragmentPagerAdapter {
        private String[] tabNames = {"首页", "巡检"};
        private int[] tabIcons = {R.drawable.tab_home_selector, R.drawable.tab_todo_selector,};
        private LayoutInflater inflater;

        public MyIndicatorAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
            inflater = LayoutInflater.from(getApplicationContext());
        }

        @Override
        public int getCount() {
            return tabNames.length;
        }

        @Override
        public View getViewForTab(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
//                convertView = inflater.inflate(R.layout.tab_main, container, false);
                convertView = inflater.inflate(R.layout.tab_main, container, false);
            }
            TextView textView = (TextView) convertView;
            textView.setText(tabNames[position]);
            Drawable myImage = ContextCompat.getDrawable(mContext, tabIcons[position]);
            myImage.setBounds(1, 1, getResources().getDimensionPixelOffset(R.dimen.x48), getResources().getDimensionPixelOffset(R.dimen.x48));
//            textView.setCompoundDrawablesWithIntrinsicBounds(0, tabIcons[position], 0, 0);

            textView.setCompoundDrawables(null, myImage, null, null);
            textView.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.y2));
            return textView;
        }

        @Override
        public Fragment getFragmentForPage(int position) {
            if (position == 0) {
                HomeFragment homeFragment = new HomeFragment();
                Bundle bundle1 = new Bundle();
                bundle1.putString("agrs1", tabNames[position]);
                bundle1.putString("USERID", userId);
                homeFragment.setArguments(bundle1);
                return homeFragment;
            } else {
                InspectionMainFragment mainFragment = new InspectionMainFragment();

                Bundle bundle = new Bundle();
                bundle.putString(INTENT_STRING_TABNAME, tabNames[position]);
                bundle.putInt(INTENT_INT_INDEX, position);
                bundle.putString("USERID", userId);
                mainFragment.setArguments(bundle);
                return mainFragment;
            }
        }
    }

    /**
     * 启动子线程来获取缓存大小
     */
    class TaskThread extends Thread {
        public void run() {
            try {
                String cleanSize = DataCacheUtils.getTotalCacheSize(mContext);//做一些耗时的任务
                DataCacheUtils.clearAllCache(mContext);
                Thread.sleep(1000);
                Message message = new Message();
                message.obj = cleanSize;
                message.what = 0;
                handler.sendMessage(message);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取缓存数据的大小
     */
    private String initCacheSize() {
    /*
    * 获取SD卡根目录：Environment.getExternalStorageDirectory().getAbsolutePath();
        外部Cache路径：/mnt/sdcard/android/data/com.xxx.xxx/cache 一般存储缓存数据（注：通过getExternalCacheDir()获取）
        外部File路径：/mnt/sdcard/android/data/com.xxx.xxx/files 存储长时间存在的数据
        （注：通过getExternalFilesDir(String type)获取， type为特定类型，可以是以下任何一种
                    Environment.DIRECTORY_MUSIC,
                    Environment.DIRECTORY_PODCASTS,
                     Environment.DIRECTORY_RINGTONES,
                     Environment.DIRECTORY_ALARMS,
                     Environment.DIRECTORY_NOTIFICATIONS,
                     Environment.DIRECTORY_PICTURES,
                      Environment.DIRECTORY_MOVIES. ）
    **/
        String outFileSize = "";
        String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File outCachePath = mContext.getExternalCacheDir();
        File outFilePath = mContext.getExternalFilesDir(Environment.DIRECTORY_ALARMS);

        try {
            String outCacheSize = FileCacheUtils.getCacheSize(outCachePath);
            String outCustomSize = FileCacheUtils.getCacheSize(new File(MISystemApplication.mainPath));
            outFileSize = FileCacheUtils.getCacheSize(outFilePath);
//            tvCacheSize.setText(outCacheSize);
            return outFileSize;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outFileSize;
    }

    /**
     * 退出程序
     */
    private void exit() {
        // 退出程序方法有多种 这里使用SingleTask式
        //1、设置MainActivity的加载模式为singleTask
        //2、重写MainActivity中的onNewIntent方法
        //3、需要退出时在Intent中添加退出的tag

//        Intent intent = new Intent(this, MainActivity.class);
//        intent.putExtra("exit", true);
//        startActivity(intent);
        if (mSideslipLayout.isOpen()) {
            mSideslipLayout.closePane();
        }
        finish();
        System.exit(0);
    }

    /**
     * app更新
     */

    @Override
    protected void onResume() {
        super.onResume();
//        if (isUpdate && null != mAlertDialog) {
//            mAlertDialog.create().show();
//        }
//        else {
//            Toast.makeText(mContext, "已是最新版本！", Toast.LENGTH_SHORT).show();
//        }
    }
}
